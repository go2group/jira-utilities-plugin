package com.go2group.jira.plugin.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface CSITEntity extends Entity{

	Long getCustomfield();
	
	void setCustomfield(Long customfield);
	
	String getIssuetype();
	
	void setIssuetype(String issuetype);

	String getOptLvl1();
	
	void setOptLvl1(String optLvl1);
	
	String getOptLvl2();
	
	void setOptLvl2(String optLvl2);
}
