package com.go2group.jira.plugin.cfcreator;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.core.entity.GenericEntityException;

import webwork.action.ServletActionContext;

import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.context.GlobalIssueContext;
import com.atlassian.jira.issue.customfields.CustomFieldSearcher;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.util.EasyList;
import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.go2group.jira.plugin.cfcreator.pojo.CustomFieldRepresentation;
import com.google.common.collect.Lists;

public class CreateCustomFieldAction extends JiraWebActionSupport {

	private final CustomFieldManager customFieldManager;
	private final IssueTypeManager issueTypeManager;
	
	public List<CustomFieldRepresentation> reps = Lists.newArrayList();
	public String cfJSON;
	public List<String> checkboxGroup;
	
	public CreateCustomFieldAction(CustomFieldManager customFieldManager, IssueTypeManager issueTypeManager) {
		this.customFieldManager = customFieldManager;
		this.issueTypeManager = issueTypeManager;
	}

	public String doExecute() {
		return INPUT;
	}
	
	public String doProcess() {
		JSONArray cfJSONArray = null;
		try {
			cfJSONArray = new JSONArray(cfJSON);
		} catch (JSONException e) {
		}
		
		for (int i = 0; i < cfJSONArray.length(); ++i) {
			JSONObject obj = null;
			JSONObject schema = null;
			try {
				obj = cfJSONArray.getJSONObject(i);
				schema = obj.getJSONObject("schema");
			} catch (JSONException e) {
			}
			
			if (schema != null) {
				try {
					Integer customId = schema.getInt("customId");
					if (customId != null) {
						String customFieldType = schema.getString("custom");
						// Code only support default custom field types
						if (customFieldType.startsWith("com.atlassian.jira.plugin.system.customfieldtypes")) {
							CustomFieldRepresentation rep = new CustomFieldRepresentation(customId, obj.getString("name"), customFieldType, "");
							reps.add(rep);
						}
					}
				} catch (JSONException e) {
				}
			}
		}
		
		return "processed";
	}
	
	public String doCreate() throws GenericEntityException {
		HttpServletRequest request = ServletActionContext.getRequest();
		String[] values = request.getParameterValues("checkboxGroup");
		
		for (String value : values) {
			String[] valueSplit = value.split(",");
			String name = valueSplit[0];
			String type = valueSplit[1];
			
			CustomField exists = customFieldManager.getCustomFieldObjectByName(name);
			if (exists == null) {
				CustomFieldType<?, ?> cfType = customFieldManager.getCustomFieldType(type);
				List<CustomFieldSearcher> searchers = customFieldManager.getCustomFieldSearchers(cfType);
				
				List nullList = new ArrayList(1);
				nullList.add(null);
				
				CustomField newCustomField = customFieldManager.createCustomField(name, "", 
						cfType, 
						searchers.get(0), 
						EasyList.build(GlobalIssueContext.getInstance()), nullList);
			}
		}
		
		return SUCCESS;
	}

	public List<CustomFieldRepresentation> getReps() {
		return reps;
	}

	public void setReps(List<CustomFieldRepresentation> reps) {
		this.reps = reps;
	}

	public String getCfJSON() {
		return cfJSON;
	}

	public void setCfJSON(String cfJSON) {
		this.cfJSON = cfJSON;
	}
}
