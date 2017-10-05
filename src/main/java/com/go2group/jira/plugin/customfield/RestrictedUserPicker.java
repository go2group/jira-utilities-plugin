package com.go2group.jira.plugin.customfield;

/* Copyright (c) 2009 Go2Group
 * All rights reserved.
 */

// JDK imports
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.UserConverter;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.UserCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.UserBeanFactory;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.template.soy.SoyTemplateRendererProvider;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserFilterManager;
import com.atlassian.jira.user.UserHistoryManager;
import com.atlassian.jira.util.EmailFormatter;
import com.atlassian.jira.util.I18nHelper;
import com.go2group.jira.plugin.util.RestrictedFieldUtility;

import webwork.action.ActionContext;

/**
 * Field with security restrictions
 */
public class RestrictedUserPicker extends UserCFType {


    /**
     * makes a helper class available to the velocity template
     */
    private final RestrictedFieldUtility helper;

    /**
     * auth context
     */
    private final JiraAuthenticationContext authenticationContext;

    /**
     * Constructor. All of the arguments are passed by JIRA.
     */

	public RestrictedUserPicker(
			CustomFieldValuePersister customFieldValuePersister,
			GenericConfigManager genericConfigManager,
			ApplicationProperties applicationProperties,
			JiraAuthenticationContext authenticationContext,
			FieldConfigSchemeManager fieldConfigSchemeManager,
			ProjectManager projectManager,
			SoyTemplateRendererProvider soyTemplateRendererProvider,
			GroupManager groupManager, ProjectRoleManager projectRoleManager,
			UserSearchService searchService, JiraBaseUrls jiraBaseUrls,
			UserHistoryManager userHistoryManager,
			UserFilterManager userFilterManager,
			I18nHelper i18nHelper, EmailFormatter emailFormatter, UserBeanFactory userBeanFactory) {
		/* Changes for JUP-79 - start */
		/*
		 * To make the constructor compatible with the JIRA 6.3.13 API
		 */
		super(customFieldValuePersister, ComponentAccessor.getComponentOfType(UserConverter.class), genericConfigManager,
				applicationProperties, authenticationContext,
				fieldConfigSchemeManager, projectManager,
				soyTemplateRendererProvider, groupManager, projectRoleManager,
				searchService, jiraBaseUrls, userHistoryManager,
				userFilterManager, i18nHelper,
				userBeanFactory);
		/* Changes for JUP-79 - end */

		this.authenticationContext = authenticationContext;
		helper = new RestrictedFieldUtility();
	}

    /**
     * Puts helper class into velocity template
     *
     * @param issue
     * @param field
     * @param fieldlayoutItem
     * @return velocity map
     */
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
    } // end method getVelocityParameters

    @Override
    public ApplicationUser getSingularObjectFromString(String string) throws FieldValidationException {
        Project project = getCurrentProject();
        if (project != null && !this.helper.canView(project, this.authenticationContext.getLoggedInUser())) {
            return null;
        }
        return super.getSingularObjectFromString(string);
    }

    @Override
    public String getChangelogString(CustomField field, ApplicationUser value) {
        return null;
    }

    @Override
    public String getChangelogValue(CustomField field, ApplicationUser value) {
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

} // end class RestrictedUserPicker
