package com.go2group.jira.plugin.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.DelegatorInterface;
import org.ofbiz.core.entity.jdbc.SQLProcessor;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.status.Status;

/**
 * Used to calculate the amount of time spent is a specified status
 */
public class TimeInStatusDAO
{
    public static final String STATUSES_SEPARATOR = "_*|*_";
    public static final String STATUS_VALUES_SEPARATOR = "_*:*_";

    String ENTITY_DS = "defaultDS"; // this is package private so that it can be overridden in tests
    private static final Logger log = Logger.getLogger(TimeInStatusDAO.class);
    protected final DelegatorInterface delegatorInterface;
    private String selectStatement;

    private static final String CHANGEGROUP_TABLE_ALIAS = "cg";
    private static final String CHANGEITEM_TABLE_ALIAS = "ci";

    protected static ConstantsManager constantsManager;

    protected TimeInStatusDAO(DelegatorInterface delegatorInterface)
    {
        this.delegatorInterface = delegatorInterface;
        String changeGroup = getTableName("ChangeGroup");
        String changeItem = getTableName("ChangeItem");
        String cg_created = getColName("ChangeGroup", "created");
        String cg_issueid = getColName("ChangeGroup", "issue");
        String cg_id = getColName("ChangeGroup", "id");
        String ci_field = getColName("ChangeItem", "field");
        String ci_oldvalue = getColName("ChangeItem", "oldvalue");
        String ci_newvalue = getColName("ChangeItem", "newvalue");
        String ci_fieldtype = getColName("ChangeItem", "fieldtype");
        String ci_gid = getColName("ChangeItem", "group");

        selectStatement = "select " + CHANGEGROUP_TABLE_ALIAS + "." + cg_created + ", " +
                CHANGEITEM_TABLE_ALIAS + "." + ci_oldvalue + ", " +
                CHANGEITEM_TABLE_ALIAS + "." + ci_newvalue + " " +
                " from " + changeGroup + " " + CHANGEGROUP_TABLE_ALIAS + " , " + 
                        changeItem + " " + CHANGEITEM_TABLE_ALIAS +
                " where " + CHANGEGROUP_TABLE_ALIAS + "." + cg_issueid + " = ? " +
                " and " + CHANGEGROUP_TABLE_ALIAS + "." + cg_id + " = " + CHANGEITEM_TABLE_ALIAS + "." + ci_gid +
                " and " + CHANGEITEM_TABLE_ALIAS + "." + ci_fieldtype + "='jira' " +
                " and " + CHANGEITEM_TABLE_ALIAS + "." + ci_field + "='status' " +
                " order by " + CHANGEGROUP_TABLE_ALIAS + "." + cg_created + " asc";
    }

    /**
     * This is a select statement, that takes one parameter (an issue id), and returns a single Timestamp column
     *
     * @return A select statement that returns a single column, the date field that you wish
     */
    protected String getSelectStatement()
    {
        return selectStatement;
    }

    protected String getTableName(String entityName)
    {
        return delegatorInterface.getModelEntity(entityName).getTableName(ENTITY_DS);
    }

    protected String getColName(String entityName, String fieldName)
    {
        return delegatorInterface.getModelEntity(entityName).getField(fieldName).getColName();
    }

    public String calculateForStatuses(Issue issue)
    {
        String statusSummary = null;
        SQLProcessor sqlProcessor = null;
        ResultSet resultSet;

        try
        {
            sqlProcessor = getSQLProcessor();
            sqlProcessor.prepareStatement(getSelectStatement());
            sqlProcessor.setValue(issue.getId());

            sqlProcessor.executeQuery();
            resultSet = sqlProcessor.getResultSet();

            Map<Timestamp, String> results = processResultSet(resultSet, issue);
            if (!results.isEmpty())
                statusSummary = doCalculation(results);
        }
        catch (Throwable e)
        {
            log.error(e, e);
        }
        finally
        {
            if (sqlProcessor != null)
            {
                try
                {
                    // Closing the SQLProcessor closes the ResultSet as well.
                    sqlProcessor.close();
                }
                catch (Exception e)
                {
                    log.error(e, e);
                }
            }
        }
        return statusSummary;
    }

    protected SQLProcessor getSQLProcessor() 
    {
        return new SQLProcessor(ENTITY_DS);
    }

    public static Double getMillisInStatus(String status, String customFieldValue)
    {
        return getValueFromStatusCustomField(status, customFieldValue, false);
    }

    private static Double getValueFromStatusCustomField(String statusId, String customFieldValue, boolean numberOfTimesIn)
    {
        String [] statuses = StringUtils.splitByWholeSeparator(customFieldValue, STATUSES_SEPARATOR);
        Map<String, Status> statusNameMap = getStatusNameMap();

        for (int i = 0; i < statuses.length; i++)
        {
            String statusString = statuses[i];
            String [] vals = StringUtils.splitByWholeSeparator(statusString, STATUS_VALUES_SEPARATOR);
            if (vals != null && vals.length == 3)
            {
                String currentStatus = vals[0];
                String currentStatusId;
                //if status !Number assume that it is a status name and convert to a statusId
                if (isInteger(currentStatus))
                    currentStatusId = currentStatus;
                else
                {
                    if (statusNameMap.containsKey(currentStatus))
                        currentStatusId = ((Status)statusNameMap.get(currentStatus)).getId();
                    else
                        continue; /* Status cannot be determined, skip */
                }

                String numberOfTimesInStatus = vals[1];
                String secondsInStatus = vals[2];

                if (currentStatusId.equals(statusId))
                {
                    if (numberOfTimesIn)
                        return new Double(numberOfTimesInStatus);
                    else
                        return new Double(secondsInStatus);
                }
            }
        }

        return null;
    }

    private String doCalculation(Map<Timestamp, String> results)
    {
        Timestamp previousStart = null;
        String previousStatus = null;

        MultiMap numberOfTimesInStatuses = new MultiValueMap();
        Map<String, Long> secondsInStatuses = new LinkedHashMap<String, Long>();

        // Use the previousStart as an indicator that we need to record the amount
        // of time in the last state
        for (Iterator<Timestamp> iterator = results.keySet().iterator(); iterator.hasNext();)
        {
            Timestamp startTime = (Timestamp) iterator.next();
            String dbStatus = (String) results.get(startTime);

            // record that we have hit the status
            numberOfTimesInStatuses.put(dbStatus, "");

            // If we have set the previous start in a earlier loop then we must
            // record the amount of time spent in that state
            if (previousStart != null && previousStatus != null)
            {
                Long secondsInStatusLong = (Long) secondsInStatuses.get(previousStatus);
                long secondsInStatus = (secondsInStatusLong == null) ? 0 : secondsInStatusLong.longValue();

                //secondsInStatus += startTime.getTime() - previousStart.getTime();
                secondsInStatus += timeDiffIgnoreWeekends(startTime.getTime(), previousStart.getTime());
                secondsInStatuses.put(previousStatus, new Long(secondsInStatus));
            }

            // If we are in a matching status then we set the previousStart this this
            // start time, otherwise we make sure we have null'ed out the value.
            previousStart = startTime;
            previousStatus = dbStatus;
        }
        return buildCustomFieldValueFromData(numberOfTimesInStatuses, secondsInStatuses);
    }

    private String buildCustomFieldValueFromData(MultiMap numberOfTimesInStatuses, Map<String, Long> secondsInStatuses)
    {
        StringBuffer result = new StringBuffer();
        boolean first = true;

        for (Iterator<?> iterator = numberOfTimesInStatuses.keySet().iterator(); iterator.hasNext();)
        {
            String statusId = (String) iterator.next();
            int numberOfTimesInStatus = ((Collection<?>) numberOfTimesInStatuses.get(statusId)).size();
            Long secondsInStatusLong = (Long) secondsInStatuses.get(statusId);
            long secondsInStatus = (secondsInStatusLong != null) ? secondsInStatusLong.longValue() : 0;

            if (!first)
                result.append(STATUSES_SEPARATOR);
            else
                first = false;

            result.append(statusId);
            result.append(STATUS_VALUES_SEPARATOR);
            result.append(numberOfTimesInStatus);
            result.append(STATUS_VALUES_SEPARATOR);
            result.append(secondsInStatus);

        }
        return result.toString();
    }

    private Map<Timestamp, String> processResultSet(ResultSet resultSet, Issue issue) throws SQLException
    {
        boolean first = true;
        Map<Timestamp, String> timeInStatusesMap = new ListOrderedMap();
        while (resultSet.next())
        {
            Timestamp startTime = resultSet.getTimestamp(1);
            String oldString = resultSet.getString(2);
            String newString = resultSet.getString(3);
            // Treat the first status as special since we use the issue create date as the start time
            // and the old value of the change log as the initial state.
            if (first)
            {
                timeInStatusesMap.put(issue.getCreated(), oldString);
                first = false;
            }

            // Always store the new value and timestamp as a transition entry
            timeInStatusesMap.put(startTime, newString);
        }
        return timeInStatusesMap;
    }

    private static Map<String, Status> getStatusNameMap()
    {
        ConstantsManager constantsManager = getConstantsManager();
        Map<String, Status> statusMap = new HashMap<String, Status>();

        for (Status status : constantsManager.getStatusObjects())
            statusMap.put(status.getName(), status);
        
        return statusMap;
    }

    protected static ConstantsManager getConstantsManager() 
    {
        if (null == constantsManager)
            constantsManager = ComponentAccessor.getConstantsManager();

        return constantsManager;
    }

    private static boolean isInteger(String str)
    {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
    }
    
    private long timeDiffIgnoreWeekends(long endTime, long startTime)
    {
        Calendar calendar = Calendar.getInstance();

        // If startTime is on Sat or Sun, advance it to Mon 
        
        long startPartialDay = 0;
        calendar.setTimeInMillis(startTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
        {
            calendar.add(Calendar.DAY_OF_MONTH, 2);
        }
        else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
        {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        else
        {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            startPartialDay = calendar.getTimeInMillis() - startTime;
        }
        startTime = calendar.getTimeInMillis();

        // If endTime is on Sat or Sun, back it up it to Fri 
        
        long endPartialDay = 0;
        calendar.setTimeInMillis(endTime);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
        {
            // We are already at the end of Friday
        }
        else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
        {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        else
        {
            endPartialDay = endTime - calendar.getTimeInMillis();
        }
        endTime = calendar.getTimeInMillis();

        return ((wdnum(endTime) - wdnum(startTime)) * 1000 * 60 * 60 * 24) + startPartialDay + endPartialDay;
    }

    /**
     * Return the number of week days between Monday, 29 December, 1969 and the given date.
     *
     * @param d a date
     * @return the number of week days since Monday, 29 December, 1969
     */
    private long wdnum(long time) 
    {
        long l = div(time, 1000 * 60 * 60 * 24)[0] + 3;
        long d[] = div(l, 7);
        return l - 2 * d[0] - Math.max(d[1] - 5, 0);
    }

    /**
     * Integer division with rounding towards negative infinity, which Java does not provide by itself (the normal division and modulus operations round towards zero). 
     * Use of this function makes the wdnum method usable with dates before 1970 because it will handle negative numbers correctly.
     *
     * @param n the numerator
     * @param d the denominator
     * @return the quotient and remainder
     * @throws ArithmeticException if <code>d == 0</code>
     */
    private long[] div(long n, long d) 
    {
        long q = n / d;
        long r = n % d;
        if (r < 0) 
        {
            q--;
            r += d;
        }
        return new long[] {q, r};
    }
}
