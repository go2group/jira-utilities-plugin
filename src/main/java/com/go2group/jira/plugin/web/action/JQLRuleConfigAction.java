package com.go2group.jira.plugin.web.action;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.go2group.jira.plugin.ao.JQLRule;
import com.go2group.jira.plugin.ao.JQLRuleBean;
import com.go2group.jira.plugin.ao.JQLRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JQLRuleConfigAction extends JiraWebActionSupport{

	private Logger log = LoggerFactory.getLogger(JQLRuleConfigAction.class);
	
	private JQLRuleService jqlRuleService;
	
	private List<JQLRule> rules;
	
	private String ruleName;
	
	private String jqlRule;
	
	private Integer ruleId;
	
	private JQLRule editRule;
	
	private List<String> messages;
	
	private List<String> errorMessages;
	
	public JQLRuleConfigAction(JQLRuleService jqlRuleService) {
		this.jqlRuleService = jqlRuleService;
		this.messages = new ArrayList<String>();
		this.errorMessages = new ArrayList<String>();
	}
	
	@Override
	protected String doExecute() throws Exception {
		
		rules = jqlRuleService.getAllRules();
		
		return SUCCESS;
	}
	
	public String doAddRule() throws Exception{
		
		jqlRuleService.createRule(ruleName, jqlRule);
		
		messages.add("Rule successfully added");
		
		return doExecute();
	}
	
	public String doEditRule() throws Exception{
		editRule = jqlRuleService.getRuleById(ruleId);
		
		if (editRule == null){
			errorMessages.add("Invalid Rule ID received for edit");
			return doExecute();
		}
		
		return "edit"; 
	}
	
	public String doUpdateRule() throws Exception{
		JQLRule rule = jqlRuleService.updateRule(ruleId, ruleName, jqlRule);
		
		if (rule == null){
			errorMessages.add("Failed updating rule : "+ruleName);
		}
		
		return doExecute();
	}

	public String doDeleteRule() throws Exception{
		boolean success = jqlRuleService.deleteRule(ruleId);
		
		if (!success){
			errorMessages.add("Failed to delete rule");
		}
		
		return doExecute();
	}
	
	public String doActivateRule() throws Exception{
		jqlRuleService.toggleRuleActivation(ruleId);
		
		return doExecute();
	}
	
	public List<JQLRule> getRules() {
		return rules;
	}

	public void setRules(List<JQLRule> rules) {
		this.rules = rules;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getJqlRule() {
		return jqlRule;
	}

	public void setJqlRule(String jqlRule) {
		this.jqlRule = jqlRule;
	}

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	public JQLRule getEditRule() {
		return editRule;
	}

	public void setEditRule(JQLRule editRule) {
		this.editRule = editRule;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
}
