package com.go2group.jira.plugin.condition;

import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.jira.user.ApplicationUser;

public class ProjectAdminCondition extends AbstractWebCondition {

	private final PermissionManager permissionManager;

	public ProjectAdminCondition(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	@Override
	public boolean shouldDisplay(ApplicationUser user, JiraHelper helper) {
		ProjectPermissionKey projectPermissionKey = new ProjectPermissionKey("ADMINISTER_PROJECTS");
		return permissionManager.hasPermission(projectPermissionKey, helper.getProject(), user);
	}
}
