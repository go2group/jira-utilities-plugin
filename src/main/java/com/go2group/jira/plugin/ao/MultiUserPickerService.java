package com.go2group.jira.plugin.ao;

public interface MultiUserPickerService {

	void updateConfig(String cfKey, String groupsConfig);
	
	MultiUsrPkrEntity getConfig(String cfKey);
}
