package com.go2group.jira.plugin.customfield.searcher;

import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.customfields.searchers.renderer.DateCustomFieldSearchRenderer;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchContext;
import com.atlassian.jira.issue.search.constants.SimpleFieldSearchConstants;
import com.atlassian.jira.issue.search.searchers.util.DateSearcherConfig;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.template.VelocityTemplatingEngine;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.velocity.VelocityRequestContextFactory;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.jira.web.action.util.CalendarLanguageUtil;
import com.go2group.jira.plugin.util.RestrictedFieldUtility;

public class RestrictedDateFieldRenderer extends DateCustomFieldSearchRenderer{
	
	private final RestrictedFieldUtility helper;
	private final ProjectManager projectManager;

	public RestrictedDateFieldRenderer(boolean isDateTimePicker, CustomField customField,
			SimpleFieldSearchConstants constants, DateSearcherConfig config,
			VelocityRequestContextFactory velocityRequestContextFactory, ApplicationProperties applicationProperties,
			VelocityTemplatingEngine templatingEngine, CalendarLanguageUtil calendarUtils,
			FieldVisibilityManager fieldVisibilityManager) {

	    super(isDateTimePicker, customField, constants, config, velocityRequestContextFactory, applicationProperties,
	        templatingEngine, calendarUtils, fieldVisibilityManager);
		this.projectManager = ComponentAccessor.getProjectManager();
		helper = new RestrictedFieldUtility();
	}
	
	@Override
	public boolean isShown(ApplicationUser searcher, SearchContext searchContext) {
		boolean shown = super.isShown(searcher, searchContext);
		List<Long> projects = searchContext.getProjectIds();
		if (projects != null && projects.size() > 0) {
			for (Long project : projects) {
				if (!this.helper.canView(this.projectManager.getProjectObj(project), searcher)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return shown;
	}

	
}
