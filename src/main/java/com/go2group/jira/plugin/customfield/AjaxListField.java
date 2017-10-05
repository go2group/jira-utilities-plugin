package com.go2group.jira.plugin.customfield;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.FeatureManager;
import com.atlassian.jira.issue.customfields.impl.MultiSelectCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.security.JiraAuthenticationContext;

public class AjaxListField extends MultiSelectCFType{

	public AjaxListField(OptionsManager optionsManager, CustomFieldValuePersister valuePersister,
			GenericConfigManager genericConfigManager, JiraBaseUrls jiraBaseUrls, SearchService searchService,
			FeatureManager featureManager, JiraAuthenticationContext jiraAuthenticationContext) {
		super(optionsManager, valuePersister, genericConfigManager, jiraBaseUrls, searchService, featureManager, jiraAuthenticationContext);
        //super(optionsManager, valuePersister, genericConfigManager, jiraBaseUrls);
	}

}
