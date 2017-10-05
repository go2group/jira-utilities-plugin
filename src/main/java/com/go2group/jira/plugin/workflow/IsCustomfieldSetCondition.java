package com.go2group.jira.plugin.workflow;

import java.util.Map;


import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IsCustomfieldSetCondition extends AbstractJiraCondition{

	private static Logger log = LoggerFactory.getLogger(IsCustomfieldSetCondition.class);

	private final CustomFieldManager customfieldManager;
	
	public IsCustomfieldSetCondition(CustomFieldManager customfieldManager) {
		this.customfieldManager = customfieldManager;
	}
	
	@Override
	public boolean passesCondition(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
		
		Issue issue = getIssue(transientVars);

		log.debug("Evaluation workflow condition for issue "+issue);
		
		String testOption = (String)args.get("checkparam");

		CustomField cf = customfieldManager.getCustomFieldObject((String)args.get("customfield"));
		
		if ("Set".equals(testOption)){
			//Test for the customfieldvalue set
			return issue.getCustomFieldValue(cf) != null;
		}else{
			//Test for the customfieldvalue not set
			return issue.getCustomFieldValue(cf) == null;
		}
	}

}
