package com.go2group.jira.plugin.listener;

import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.*;
import org.ofbiz.core.entity.DelegatorInterface;
import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItem;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.util.IssueChangeHolder;

/**
 *  When an issue is updated, use the change-log to calculate and store the values for any time-in-status custom fields
 */
public class TimeInStatusFieldListener extends AbstractIssueEventListener
{
    private CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
    private TimeInStatusDAO timeInStatusDAO;
    private String timeInStatusValues;

    public TimeInStatusFieldListener()
    {
        timeInStatusDAO = new TimeInStatusDAO(ComponentManager.getComponent(DelegatorInterface.class));
    }

    public String getDescription()
    {
        return "When an issue is created/updated, calculate and store the values for any time-in-status custom fields.";
    }

    public void workflowEvent(IssueEvent event)
    {
        MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(event.getIssue().getKey());
        if (issue == null) // Is null when the issue is deleted
            return;
        
        // Did the status field change?  Because we only need to calculate or re-calculate the time-in-status values when the status changes.
        
        GenericValue gvChangeLog = event.getChangeLog();
        if (gvChangeLog == null)
            return;
        
        boolean statusChanged = false;
        List<GenericValue> changeItems;
        try
        {
            changeItems = gvChangeLog.getRelated("ChildChangeItem");
        } 
        catch (GenericEntityException e)
        {
            e.printStackTrace();
            return;
        }

        for (GenericValue changeItem : changeItems)
        {
            if ("status".equals(changeItem.get("field")))
            {
                statusChanged = true;
                break;
            }
        }
        
        if (statusChanged == false)
            return;

        timeInStatusValues = null;
        
        // Get all the time-in-status custom fields and update their values
        
        List<CustomField> customFields = customFieldManager.getCustomFieldObjects(issue);
        for (CustomField customField : customFields)
        {
            if ("com.go2group.jira.plugin.jira-utilities:g2g-time-in-status-custom-field".equals(customField.getCustomFieldType().getKey()))
            {
                IssueChangeHolder changeHolder = new DefaultIssueChangeHolder();
                String curValue = nullToBlank((String) issue.getCustomFieldValue(customField));
                String newValue = calculateTimeInStatus(issue, customField);
                if (!curValue.equals(nullToBlank(newValue)))
                    customField.updateValue(null, issue, new ModifiedValue(curValue, nullToBlank(newValue)), changeHolder);
            }
        }
        
    }
    
    private String nullToBlank(String s)
    {
        if (s == null)
            return "";
        return s;
    }
    
    private String calculateTimeInStatus(Issue issue, CustomField customField)
    {
        // Get the status associated with this time-in-status custom field
        
        FieldConfig fieldConfig = customField.getRelevantConfig(issue);
        List<FieldConfigItem> fieldConfigItems = fieldConfig.getConfigItems(); // This is the list based on TimeInStatus.getConfigurationItemTypes()
        Map<String, Object> options = (Map<String, Object>) fieldConfigItems.get(0).getType().getConfigurationObject(issue, fieldConfig);
        if (options == null)
            return null;
        String statusId = (String) options.get("status");
        
        // If the issue is currently in this status, don't display a value
        
        if (statusId.equals(issue.getStatusObject().getId()))
            return null;
        
        if (timeInStatusValues == null)
            timeInStatusValues = timeInStatusDAO.calculateForStatuses(issue);
        
        Double millisInStatus = TimeInStatusDAO.getMillisInStatus(statusId, timeInStatusValues);
        if (millisInStatus == null || millisInStatus == 0.0)
            return null;
        
        // Format the seconds into "n Days n Hours n Minutes n Seconds"
        
        StringBuffer sb = new StringBuffer();
        
        double secs = millisInStatus / 1000;

        int nDays = (int) (secs / (60 * 60 * 24));
        secs -= nDays * 60 * 60 * 24;

        int nHours = (int) (secs / (60 * 60));
        secs -= nHours * 60 * 60;
        
        int nMins = (int) (secs / 60);
        secs -= nMins * 60;
        
        int nSecs = (int) secs;
        
        if (nDays > 0)
        {
            sb.append(nDays);
            if (nDays == 1)
                sb.append(" Day ");
            else
                sb.append(" Days ");
        }
        
        if (nHours > 0)
        {
            sb.append(nHours);
            if (nHours == 1)
                sb.append(" Hour ");
            else
                sb.append(" Hours ");
        }

        if (nMins > 0)
        {
            sb.append(nMins);
            if (nMins == 1)
                sb.append(" Minute ");
            else
                sb.append(" Minutes ");
        }
        
        sb.append(nSecs);
        sb.append(" Seconds");
        
        return sb.toString();
    }
}