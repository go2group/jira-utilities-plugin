package com.go2group.jira.plugin.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;

@Preload
public interface JQLRuleAction extends Entity{

	@NotNull
	public Integer getRuleId();
	public void setRuleId(Integer ruleId);
	
	@NotNull
	public String getField();
	public void setField(String field);
	
	public String getValue();
	public void setValue(String value);
}
