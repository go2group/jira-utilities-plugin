package com.go2group.jira.plugin.util.impl;

import com.atlassian.jira.issue.IssueFieldConstants;
import com.go2group.jira.plugin.util.JiraSystemFieldName;

/**
 * Created by bhushan154 on 10/06/14.
 */
public class JiraSystemFieldNameImpl implements JiraSystemFieldName{
    public String getSystemFieldName(String systemFieldName){
         if(systemFieldName.contains(",")){
            StringBuilder stringBuilder = new StringBuilder();
             for(String string:systemFieldName.split(",")){
                 stringBuilder.append(getName(string)+",");
             }
             return stringBuilder.substring(0, stringBuilder.toString().lastIndexOf(","));
         }
        else{
             return getName(systemFieldName);
         }
    }

    private String getName(String systemFieldName){
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.AFFECTED_VERSIONS))
            return "Affected versions";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.ASSIGNEE))
            return "Assignee";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.COMPONENTS))
            return "Components";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.CREATED))
            return "Date created";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.DESCRIPTION))
            return "Description";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.DUE_DATE))
            return "Due date";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.ENVIRONMENT))
            return "Environment";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.TIME_ESTIMATE))
            return "Remaining estimate";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.TIME_SPENT))
            return "Time spent";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.TIME_ORIGINAL_ESTIMATE))
            return "Original estimate";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.FIX_FOR_VERSIONS))
            return "Fix for versions";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.ISSUE_TYPE))
            return "Issue type";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.ISSUE_KEY))
            return "Issue key";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.PRIORITY))
            return "Priority";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.PROJECT))
            return "Project";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.REPORTER))
            return "Reporter";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.RESOLUTION))
            return "Resolution";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.RESOLUTION_DATE))
            return "Resolution date";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.SECURITY))
            return "Security";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.STATUS))
            return "Status";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.SUMMARY))
            return "Summary";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.UPDATED))
            return "Last updated";
        if(systemFieldName.equalsIgnoreCase(IssueFieldConstants.VOTES))
            return "Votes";
        return systemFieldName;
    }
}