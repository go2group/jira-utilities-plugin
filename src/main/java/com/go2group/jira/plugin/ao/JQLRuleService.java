package com.go2group.jira.plugin.ao;

import java.util.List;

import com.atlassian.activeobjects.tx.Transactional;

@Transactional
public interface JQLRuleService {

	public List<JQLRule> getAllRules();
	
	public JQLRule getRuleById(Integer id);
	
	public List<JQLRuleBean> getActiveRules();
	
	public List<JQLRuleAction> getActionsByRuleId(Integer ruleId);
	
	public JQLRule createRule(String ruleName, String jqlRule);
	
	public JQLRule updateRule(Integer id, String ruleName, String jqlRule);
	
	public boolean deleteRule(Integer id);
	
	public boolean deleteRuleAction(Integer id);
	
	public void toggleRuleActivation(Integer id);
	
	public JQLRuleAction createRuleAction(Integer ruleId, String fieldName, String fieldValue);
	
}
