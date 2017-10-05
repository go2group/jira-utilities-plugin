package com.go2group.jira.plugin.customfield.searcher;

import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.CustomFieldValueProvider;
import com.atlassian.jira.issue.customfields.searchers.renderer.CustomFieldRenderer;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.ClauseNames;
import com.atlassian.jira.issue.search.SearchContext;
import com.atlassian.jira.plugin.customfield.CustomFieldSearcherModuleDescriptor;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.go2group.jira.plugin.util.RestrictedFieldUtility;

public class RestrictedCustomFieldRenderer extends CustomFieldRenderer {

	private final RestrictedFieldUtility helper;
	private final ProjectManager projectManager;

	public RestrictedCustomFieldRenderer(ClauseNames clauseNames,
			CustomFieldSearcherModuleDescriptor customFieldSearcherModuleDescriptor, CustomField field,
			CustomFieldValueProvider customFieldValueProvider, FieldVisibilityManager fieldVisibilityManager) {
		super(clauseNames, customFieldSearcherModuleDescriptor, field, customFieldValueProvider, fieldVisibilityManager);
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
