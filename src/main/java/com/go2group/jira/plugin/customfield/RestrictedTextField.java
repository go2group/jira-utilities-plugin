package com.go2group.jira.plugin.customfield;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.TextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.go2group.jira.plugin.util.RestrictedFieldUtility;

import webwork.action.ActionContext;

public class RestrictedTextField extends TextCFType {
    private static final Logger log = LoggerFactory.getLogger(RestrictedSelectList.class);
    private final RestrictedFieldUtility helper;
    private final JiraAuthenticationContext authenticationContext;

    public RestrictedTextField(CustomFieldValuePersister customFieldValuePersister,
                               GenericConfigManager genericConfigManager, JiraAuthenticationContext authenticationContext) {
        super(customFieldValuePersister, genericConfigManager);
        this.authenticationContext = authenticationContext;
        this.helper = new RestrictedFieldUtility();
    }

    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
        log.debug("getVelocityParameters Invoked");

        Map<String, Object> x = super.getVelocityParameters(issue, field, fieldLayoutItem);

        if(null == x) {
            log.debug("map was null from superclass");
            x = new HashMap<String, Object>();
        } // no map
        if(null != issue) {
            x.put("canview", new Boolean(helper.canView(issue.getProjectObject(), authenticationContext.getLoggedInUser())));
        } // has issue
        else {
            log.debug("issue was null");
            x.put("canview", new Boolean(true));
        } // no issue

        return x;
    }

    @Override
    protected Object getObjectFromDbValue(Object databaseValue) throws FieldValidationException {
        Project project = getCurrentProject();
        if (project != null && !this.helper.canView(project, this.authenticationContext.getLoggedInUser())) {
            return null;
        }
        return super.getObjectFromDbValue(databaseValue);
    }

    /*	@Override
        public Object getValueFromIssue(CustomField customfield, Issue issue) {

            if (helper.canView(issue.getProjectObject(), authenticationContext.getLoggedInUser())){
                return super.getValueFromIssue(customfield, issue);
            }else{
                log.debug("Access Restricted : returning NULL");
                return null;
            }
        }
    */
    @Override
    public String getChangelogString(CustomField field, Object value) {
        return null;
    }

    @Override
    public String getChangelogValue(CustomField field, Object value) {
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