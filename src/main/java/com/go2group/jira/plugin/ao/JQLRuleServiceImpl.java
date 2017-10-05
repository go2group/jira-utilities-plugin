package com.go2group.jira.plugin.ao;

import java.util.ArrayList;
import java.util.List;

import net.java.ao.DBParam;


import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JQLRuleServiceImpl implements JQLRuleService{

	private final Logger log = LoggerFactory.getLogger(JQLRuleServiceImpl.class);
	
	private final ActiveObjects activeObjects;
	
	private final JqlQueryParser jqlParser;
	
	public JQLRuleServiceImpl(ActiveObjects activeObjects, JqlQueryParser jqlParser) {
		this.activeObjects = activeObjects;
		this.jqlParser = jqlParser;
	}
	
	private boolean isValidJQL(String jql){
		try{
			jqlParser.parseQuery(jql);
			return true;
		}catch(JqlParseException ex){
			return false;
		}
	}
	
	@Override
	public JQLRule createRule(String ruleName, String jqlRule) {
		
		JQLRule rule = activeObjects.create(JQLRule.class, new DBParam("NAME",ruleName), new DBParam("JQL",jqlRule),
												new DBParam("JQLVALID",isValidJQL(jqlRule)), new DBParam("RULE_ACTIVE",false));
		return rule;
	}
	
	@Override
	public JQLRule updateRule(Integer id, String ruleName, String jqlRule){
		JQLRule rule = getRuleById(id);
		
		if (rule != null){
			rule.setName(ruleName);
			rule.setJQL(jqlRule);
			rule.setJQLValid(isValidJQL(jqlRule));
			
			rule.save();
			
			return rule;
		}else{
			return null;
		}
	}
	
	@Override
	public boolean deleteRule(Integer id){
		JQLRule rule = getRuleById(id);
		
		if (rule != null){
			activeObjects.delete(rule);
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public boolean deleteRuleAction(Integer id){
		JQLRuleAction ruleAction = getRuleActionById(id);
		
		if (ruleAction != null){
			activeObjects.delete(ruleAction);
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void toggleRuleActivation(Integer id){
		JQLRule rule = getRuleById(id);
		
		if (rule != null){
			if(rule.getRuleActive()){
				rule.setRuleActive(false);
			}else{
				rule.setRuleActive(true);
			}
			
			rule.save();
		}
	}

	@Override
	public JQLRuleAction createRuleAction(Integer ruleId, String fieldName, String fieldValue) {
		JQLRuleAction ruleAction = activeObjects.create(JQLRuleAction.class, new DBParam("RULE_ID",ruleId),new DBParam("FIELD",fieldName),new DBParam("VALUE",fieldValue));
	
		return ruleAction;
	}

	@Override
	public List<JQLRuleAction> getActionsByRuleId(Integer ruleId) {
		
		JQLRuleAction[] ruleActions = activeObjects.find(JQLRuleAction.class,"RULE_ID = ? ",ruleId);
		
		List<JQLRuleAction> ruleActionList = new ArrayList<JQLRuleAction>();
		
		if (ruleActions != null && ruleActions.length > 0){
			for (JQLRuleAction ruleAction : ruleActions){
				ruleActionList.add(ruleAction);
			}
		}
		
		return ruleActionList;
	}

	@Override
	public List<JQLRuleBean> getActiveRules() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JQLRule> getAllRules() {
		JQLRule[] rules = activeObjects.find(JQLRule.class);
		
		List<JQLRule> rulesList = new ArrayList<JQLRule>();
		
		if (rules != null && rules.length > 0){
			for (JQLRule rule : rules){
				rulesList.add(rule);
			}
		}
		
		return rulesList;
	}

	@Override
	public JQLRule getRuleById(Integer id) {
		JQLRule[] rules = activeObjects.find(JQLRule.class,"ID = ?", id);
		
		//With primary key search only one record possible
		
		if (rules != null && rules.length > 0){
			return rules[0];
		}else{
			return null;
		}
	}

	public JQLRuleAction getRuleActionById(Integer id){
		JQLRuleAction[] ruleActions = activeObjects.find(JQLRuleAction.class,"ID = ?",id);
		//With primary key search only one record possible
		
		if (ruleActions != null && ruleActions.length > 0){
			return ruleActions[0];
		}else{
			return null;
		}
	}
	
}
