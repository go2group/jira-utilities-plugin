package com.go2group.jira.plugin.ao;

import java.util.List;

public class JQLRuleBean {

	private JQLRule rule;
	
	private List<JQLRuleAction> actions;

	public JQLRule getRule() {
		return rule;
	}

	public void setRule(JQLRule rule) {
		this.rule = rule;
	}

	public List<JQLRuleAction> getActions() {
		return actions;
	}

	public void setActions(List<JQLRuleAction> actions) {
		this.actions = actions;
	}
	
}
