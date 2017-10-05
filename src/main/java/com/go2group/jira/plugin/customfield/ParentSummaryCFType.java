package com.go2group.jira.plugin.customfield;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

public class ParentSummaryCFType extends CalculatedCFType{

    private final CustomFieldManager customfieldManager;
    private static Logger log = LoggerFactory.getLogger(ParentSummaryCFType.class);

    public ParentSummaryCFType(CustomFieldManager customfieldManager) {
        this.customfieldManager = customfieldManager;
    }

    @Override
    public Object getSingularObjectFromString(String string) throws FieldValidationException {
        log.debug("GetSingular Object from String");
        return string;
    }

    @Override
    public String getStringFromSingularObject(Object arg0) {
        log.debug("GetSingular String from Object");
        return null;
    }

    @Override
    public Object getValueFromIssue(CustomField cf, Issue issue) {
        log.debug("Get Value from Issue");
        if (issue.isSubTask()){
            return issue.getParentObject().getSummary();
        }else{
            return issue.getSummary();
        }

    }

    @Override
    public Map getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
        log.debug("Get Velocity params");
        Map velocityParameters = super.getVelocityParameters(issue, field, fieldLayoutItem);
        if (issue.isSubTask()){
            velocityParameters.put("parentIssueKey", issue.getParentObject().getKey());
            velocityParameters.put("parentIssueSummary", issue.getParentObject().getSummary());
        }

        return velocityParameters;
    }
}
