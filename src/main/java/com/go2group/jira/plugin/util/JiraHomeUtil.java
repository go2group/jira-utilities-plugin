package com.go2group.jira.plugin.util;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

public class JiraHomeUtil {
	private static JiraHome jirahome = ComponentAccessor.getComponentOfType(JiraHome.class);

	public static String getJiraImportDirectory(){
		return jirahome.getImportDirectory().getAbsolutePath();
	}
	
	public static String getJiraHomeDirectory(){
		return jirahome.getHomePath();
	}

}
