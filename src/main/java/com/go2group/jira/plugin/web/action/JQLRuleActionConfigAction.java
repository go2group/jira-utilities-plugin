package com.go2group.jira.plugin.web.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.FieldException;
import com.atlassian.jira.issue.fields.NavigableField;
import com.atlassian.jira.issue.fields.SearchableField;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.go2group.jira.plugin.ao.JQLRuleAction;
import com.go2group.jira.plugin.ao.JQLRuleService;
import com.go2group.jira.plugin.util.FieldComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JQLRuleActionConfigAction extends JiraWebActionSupport{

	private Logger log = LoggerFactory.getLogger(JQLRuleActionConfigAction.class);

	private JQLRuleService jqlRuleService;
	
	private Integer ruleId;
	
	private String field;
	
	private String value;
	
	private Integer actionId;
	
	private List<JQLRuleAction> ruleActions;
	
	public JQLRuleActionConfigAction(JQLRuleService jqlRuleService) {
		this.jqlRuleService = jqlRuleService;
	}
	
	@Override
	protected String doExecute() throws Exception {
		
		ruleActions = jqlRuleService.getActionsByRuleId(ruleId);
		
		return SUCCESS;
	}

	public String doAddAction() throws Exception{
		jqlRuleService.createRuleAction(ruleId, field, value);
		
		return doExecute();
	}
	
	public String doDeleteAction() throws Exception{
		jqlRuleService.deleteRuleAction(actionId);
		
		return doExecute();
	}
	
	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	public List<JQLRuleAction> getRuleActions() {
		return ruleActions;
	}

	public void setRuleActions(List<JQLRuleAction> ruleActions) {
		this.ruleActions = ruleActions;
	}
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Integer getActionId() {
		return actionId;
	}

	public void setActionId(Integer actionId) {
		this.actionId = actionId;
	}

	public List<SearchableField> getFields(){
		
		List<SearchableField> applicableFields = new ArrayList<SearchableField>();
		
		Set<SearchableField> fields = ComponentAccessor.getFieldManager().getAllSearchableFields();
		
		for (SearchableField field : fields){
			if (field.getId().startsWith("customfield_")){
				applicableFields.add(field);
			}else{
				if ("priority,resolution,assignee,reporter,versions,fixVersions,components,duedate,labels".contains(field.getId())){
					applicableFields.add(field);
				}
			}
		}
		
		Collections.sort(applicableFields, new FieldComparator());
		
		return applicableFields;
	}
	
}
