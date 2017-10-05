package com.go2group.jira.plugin.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;

@Preload
public interface JQLRule extends Entity{

	@NotNull
	public String getName();
	public void setName(String name);

	@NotNull
	public String getJQL();
	public void setJQL(String name);
	
	@NotNull
	public Boolean getJQLValid();
	public void setJQLValid(Boolean isValid);
	
	@NotNull
	public Boolean getRuleActive();
	public void setRuleActive(Boolean isActive);
}
