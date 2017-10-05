package com.go2group.jira.plugin.customfield;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.go2group.jira.plugin.util.RestrictedFieldUtility;

import webwork.action.ActionContext;

public class RestrictedNumberField extends NumberCFType{

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(RestrictedNumberField.class);

    /**
     * makes a helper class available to the velocity template
     */
    private final RestrictedFieldUtility helper;

    /**
     * auth context
     */
    private final JiraAuthenticationContext authenticationContext;

    public RestrictedNumberField(CustomFieldValuePersister customFieldValuePersister, DoubleConverter doubleConverter,
                                 GenericConfigManager genericConfigManager, JiraAuthenticationContext authenticationContext) {
        super(customFieldValuePersister, doubleConverter, genericConfigManager);
        helper = new RestrictedFieldUtility();
        this.authenticationContext = authenticationContext;
    }

    /**
     * Puts helper class into velocity template
     * @param issue
     * @param field
     * @param fieldlayoutItem
     * @return velocity map
     */
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem)
    {
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

    } // end method getVelocityParameters


    @Override
    public Double getSingularObjectFromString(String string) throws FieldValidationException {
        Project project = getCurrentProject();
        if (project != null && !this.helper.canView(project, this.authenticationContext.getLoggedInUser())) {
            return null;
        }
        return super.getSingularObjectFromString(string);
    }

    @Override
    public String getChangelogString(CustomField field, Double value) {
        return null;
    }

    @Override
    public String getChangelogValue(CustomField field, Double value) {
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
