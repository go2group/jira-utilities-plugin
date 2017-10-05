package com.go2group.jira.plugin.customfield;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.DatePickerConverter;
import com.atlassian.jira.issue.customfields.impl.DateCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.history.DateTimeFieldChangeLogHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.DateFieldFormat;
import com.go2group.jira.plugin.util.RestrictedFieldUtility;

import webwork.action.ActionContext;

public class RestrictedDateField extends DateCFType {
    private static final Logger log = LoggerFactory.getLogger(RestrictedSelectList.class);
    private final RestrictedFieldUtility helper;
    private final JiraAuthenticationContext authenticationContext;

    public RestrictedDateField(CustomFieldValuePersister customFieldValuePersister, DatePickerConverter dateConverter,
                               GenericConfigManager genericConfigManager, JiraAuthenticationContext authenticationContext,
                               DateTimeFieldChangeLogHelper dateTimeFieldChangeLogHelper, DateFieldFormat dateFieldFormat,
                               DateTimeFormatterFactory dateTimeFormatterFactory) {
        super(customFieldValuePersister, dateConverter, genericConfigManager, dateTimeFieldChangeLogHelper,
                dateFieldFormat, dateTimeFormatterFactory);
        this.authenticationContext = authenticationContext;
        this.helper = new RestrictedFieldUtility();
    }

    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
        Map<String, Object> x = super.getVelocityParameters(issue, field, fieldLayoutItem);
        if (null == x) {
            log.debug("map was null from superclass");
            x = new HashMap<String, Object>();
        }
        if (null != issue) {
            x.put("canview",
                    new Boolean(this.helper.canView(issue.getProjectObject(),
                            this.authenticationContext.getLoggedInUser())));
        } else {
            log.debug("issue was null");
            x.put("canview", new Boolean(true));
        }

        return x;
    }

    @Override
    protected Date getObjectFromDbValue(Object databaseValue) throws FieldValidationException {
        Project project = getCurrentProject();
        if (project != null && !this.helper.canView(project, this.authenticationContext.getLoggedInUser())) {
            return null;
        }
        return super.getObjectFromDbValue(databaseValue);
    }

    @Override
    public String getChangelogString(CustomField field, Date value) {
        return null;
    }

    @Override
    public String getChangelogValue(CustomField field, Date value) {
        return null;
    }

    private Project getCurrentProject() {
        Object ids = ActionContext.getParameters().get("key");
        Issue currentIssue = null;
        if (ids != null) {
            String currentKey = ((String[]) ids)[0];
            currentIssue = ComponentAccessor.getIssueManager().getIssueObject(currentKey);
            return currentIssue == null ? null : currentIssue.getProjectObject();
        }
        return null;
    }
}