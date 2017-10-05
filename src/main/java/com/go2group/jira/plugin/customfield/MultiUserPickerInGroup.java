package com.go2group.jira.plugin.customfield;

/* Copyright (c) 2002-2008 Go2Group
 * All rights reserved.
 */

//Atlassian imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.MultiUserConverter;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.MultiUserCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.UserBeanFactory;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.EmailFormatter;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.go2group.jira.plugin.ao.MultiUserPickerService;
import com.go2group.jira.plugin.customfield.config.MultiUserpickerInGroupConfig;
import com.go2group.jira.plugin.util.MultiUserPickerUtil;

/**
 * Implement a custom field that has an user picker for the users in the specified group
 * @author doug.bass@go2group.com
 */
public class MultiUserPickerInGroup extends MultiUserCFType
{

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(MultiUserPickerInGroup.class);

    private JiraAuthenticationContext authenticationContext;
    private UserSearchService searchService;
    private ApplicationProperties applicationProperties;
    private MultiUserPickerService multiuserpickerService;

    public MultiUserPickerInGroup(CustomFieldValuePersister customFieldValuePersister,
                                  GenericConfigManager genericConfigManager, MultiUserConverter multiUserConverter,
                                  ApplicationProperties applicationProperties, JiraAuthenticationContext authenticationContext,
                                  UserSearchService searchService, FieldVisibilityManager fieldVisibilityManager,
                                  JiraBaseUrls jiraBaseUrls, MultiUserPickerService multiuserpickerService,
                                  EmailFormatter emailFormatter, UserBeanFactory userBeanFactory) {
    	/* Changes for JUP-79 - start */
    	/*
    	 * To make the constructor compatible with the JIRA 6.3.13 API
    	 */
//        super(customFieldValuePersister, genericConfigManager, multiUserConverter, applicationProperties,
//                authenticationContext, searchService, fieldVisibilityManager, jiraBaseUrls, emailFormatter);
    	
    	super(customFieldValuePersister, genericConfigManager, multiUserConverter, applicationProperties, authenticationContext, searchService, fieldVisibilityManager, jiraBaseUrls, userBeanFactory);
        /* Changes for JUP-79 - end */
    	
        this.authenticationContext = authenticationContext;
        this.searchService = searchService;
        this.applicationProperties = applicationProperties;
        this.multiuserpickerService = multiuserpickerService;
    }

    /**
     * include our autocomplete javascript in the velocity form
     * @param issue the issue or null
     * @param field the custom field
     * @parm fieldlayoutItem layout for the field
     * @return the userids to display on the edit screen
     */
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem)
    {
        Map<String, Object> velocityParams = super.getVelocityParameters(issue, field, fieldLayoutItem);
        JiraServiceContext ctx = new JiraServiceContextImpl(authenticationContext.getUser());

        boolean canPerformAjaxSearch = searchService.canPerformAjaxSearch(ctx);
        if (canPerformAjaxSearch)
        {
            velocityParams.put("canPerformAjaxSearch", "true");
            velocityParams.put("ajaxLimit", applicationProperties.getDefaultBackedString(APKeys.JIRA_AJAX_AUTOCOMPLETE_LIMIT));
        }
        WebResourceManager webResourceManager = ComponentAccessor.getWebResourceManager();
        webResourceManager.requireResource("com.go2group.jira.plugin.jira-utilities:myautocomplete");

        return velocityParams;
    }

    /**
     * Ensure the users are in the group specified in the properties file and they are valid users
     */
    public void validateFromParams(CustomFieldParams relevantParams, ErrorCollection errorCollectionToAddTo, FieldConfig config)
    {
        StringBuffer errors = null;
        Collection userStrings;
        String user;
        String singleParam;
        for (Iterator it = relevantParams.getValuesForNullKey().iterator(); it.hasNext();)
        {
            singleParam = (String) it.next();

            userStrings = multiUserConverter.extractUserStringsFromString(singleParam);

            if (userStrings == null)
            {
                return;
            }
            for (Iterator i = userStrings.iterator(); i.hasNext();)
            {
                user = (String) i.next();
                try
                {
                    multiUserConverter.getUser(user);

                    log.debug("Key : "+ config.getCustomField().getId());

                    // the valid group for this custom field
                    if (user != null && !user.isEmpty() && !MultiUserPickerUtil.isUserInGroup(config.getCustomField().getId(), user, multiuserpickerService))
                        errorCollectionToAddTo.addError(config.getCustomField().getId(), "Not a valid " + config.getCustomField().getName() + ": " + user);
                }
                catch (FieldValidationException e)
                {
                    if (errors == null)
                    {
                        errors = new StringBuffer(user);
                    }
                    else
                    {
                        errors.append(", ").append(user);
                    }
                }
                if (errors != null)
                {
                    errorCollectionToAddTo.addError(config.getCustomField().getId(), getI18nBean().getText("admin.errors.could.not.find.usernames", errors));
                }
            }
        }
    }

    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes() {
        List<FieldConfigItemType> configurationItemTypes = new ArrayList<FieldConfigItemType>();
        configurationItemTypes.add(new MultiUserpickerInGroupConfig(multiuserpickerService));
        return configurationItemTypes;
    }

}
