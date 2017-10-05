package com.go2group.jira.plugin.workflow.validator;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.resolution.Resolution;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;

public class ResolutionValidator implements Validator {
    private static final Logger log = LoggerFactory.getLogger(ResolutionValidator.class);
    public static final String FIELD = "field";
    public static final String RESOLUTION = "resolution";
    
    public void validate(Map transientVars, Map args, PropertySet ps) throws InvalidInputException {
        String fieldId = (String) args.get(FIELD);
        String resolutionId = (String) args.get(RESOLUTION);

        Issue issue = (Issue) transientVars.get("issue");

        if (null != issue.getResolutionObject() && issue.getResolutionObject().getId().equals(resolutionId)) {
            Resolution resolution = ComponentAccessor.getConstantsManager()
                    .getResolutionObject(resolutionId);
            FieldManager fieldManager = ComponentAccessor.getFieldManager();
            if (fieldManager.isCustomField(fieldId)) {
                CustomField customField = ComponentAccessor.getCustomFieldManager()
                        .getCustomFieldObject(fieldId);
                Object cfValue = issue.getCustomFieldValue(customField);
                if (cfValue == null) {
                    throwError(resolution, customField);
                }
            } else {
                Field field = fieldManager.getField(fieldId);
                if (fieldId.equals(IssueFieldConstants.ASSIGNEE) && issue.getAssigneeId() == null) {
                    throwError(resolution, field);
                } else if (fieldId.equals(IssueFieldConstants.DESCRIPTION) && issue.getDescription() == null) {
                    throwError(resolution, field);
                } else if (fieldId.equals(IssueFieldConstants.ENVIRONMENT) && issue.getEnvironment() == null) {
                    throwError(resolution, field);
                } else if (fieldId.equals(IssueFieldConstants.PRIORITY) && issue.getPriorityObject() == null) {
                    throwError(resolution, field);
                } else if (fieldId.equals(IssueFieldConstants.RESOLUTION) && issue.getResolutionObject() == null) {
                    throwError(resolution, field);
                } else if (fieldId.equals(IssueFieldConstants.TIME_ORIGINAL_ESTIMATE)
                        && issue.getOriginalEstimate() == null) {
                    throwError(resolution, field);
                } else if (fieldId.equals(IssueFieldConstants.TIME_ESTIMATE) && issue.getEstimate() == null) {
                    throwError(resolution, field);
                } else if (fieldId.equals(IssueFieldConstants.TIME_SPENT) && issue.getTimeSpent() == null) {
                    throwError(resolution, field);
                } else if (fieldId.equals(IssueFieldConstants.FIX_FOR_VERSIONS)
                        && (issue.getFixVersions() == null || issue.getFixVersions().isEmpty())) {
                    throwError(resolution, field);
                } else if (fieldId.equals(IssueFieldConstants.AFFECTED_VERSIONS)
                        && (issue.getAffectedVersions() == null || issue.getAffectedVersions().isEmpty())) {
                    throwError(resolution, field);
                }
            }
        }
    }

    private void throwError(Resolution resolution, Field field) throws InvalidInputException {
        throw new InvalidInputException(field.getName() + " was required when Resolution is " + resolution.getName());
    }
}
