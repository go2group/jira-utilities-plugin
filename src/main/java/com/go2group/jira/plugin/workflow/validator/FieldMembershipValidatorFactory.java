package com.go2group.jira.plugin.workflow.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.core.util.collection.EasyList;
import com.google.common.base.Joiner;
import org.apache.commons.collections.map.ListOrderedMap;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.customfields.impl.UserCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.atlassian.jira.security.groups.GroupManager;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

public class FieldMembershipValidatorFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory{

	private final CustomFieldManager customfieldManager;
	
	private final GroupManager groupManager;
	
	public FieldMembershipValidatorFactory(CustomFieldManager customfieldManager, GroupManager groupManager) {
		this.customfieldManager = customfieldManager;
		this.groupManager = groupManager;
	}
	
	@Override
	protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put("usercustomfield",getUserCustomFields());
		velocityParams.put("userjirafield", getUserJiraFields());
		velocityParams.put("selecteduserfield", getSelectedUserField(descriptor));
		velocityParams.put("groups", getAllGroups());
		velocityParams.put("selectedgroup", getSelectedGroup(descriptor));
	}

	@Override
	protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
		velocityParams.put("usercustomfield",getUserCustomFields());
		velocityParams.put("userjirafield", getUserJiraFields());
		velocityParams.put("groups", getAllGroups());
	}

	@Override
	protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		velocityParams.put("selecteduserfield", getUserFieldDisplayName(getSelectedUserField(descriptor)));
		velocityParams.put("selectedgroup", getSelectedGroup(descriptor));
	}

	@Override
	public Map<String, Object> getDescriptorParams(Map<String, Object> params) {
		
    	Map<String,Object> retMap = new HashMap<String, Object>();

    	retMap.put("userfield", extractSingleParam(params, "userfield"));
        retMap.put("group", Joiner.on(",").join(extractMultipleParams(params, "group")));
    	
		return retMap;
	}

    private String[] extractMultipleParams(final Map<String, Object> conditionParams, final String paramName) {
        if (conditionParams.containsKey(paramName)) {
            final Object argument = conditionParams.get(paramName);
            if (argument instanceof String[]) {
                return (String[]) argument;
            } else {
                throw new IllegalArgumentException("Argument '" + paramName + "' is not a String array.");
            }
        } else {
            throw new IllegalArgumentException("Cannot find expected argument '" + paramName + "' in parameters.");
        }
    }
	
	private Collection<Group> getAllGroups(){
		return groupManager.getAllGroups();
	}
	
	private String getUserFieldDisplayName(String field){
		if (field != null && field.startsWith("customfield_")){
			//It is a customfield
			CustomField cf = customfieldManager.getCustomFieldObject(field);
			
			if (cf != null){
				return cf.getName();
			}else{
				return "";
			}
		}else{
			if (field != null && field.equals(IssueFieldConstants.ASSIGNEE)){
				return "Assignee";
			}else if (field != null && field.equals(IssueFieldConstants.REPORTER)){
				return "Reporter";
			}else{
				return "";
			}
		}
	}
	
	private List<CustomField> getUserCustomFields(){
		List<CustomField> customfields = customfieldManager.getCustomFieldObjects();
		
		List<CustomField> returnList = new ArrayList<CustomField>();
		
		for (CustomField cf : customfields){
			if (cf.getCustomFieldType() instanceof UserCFType){
				returnList.add(cf);
			}
		}
		
		return returnList;
	}
	
	private Map<String,String> getUserJiraFields(){
		Map fields = new ListOrderedMap();
		
		fields.put(IssueFieldConstants.ASSIGNEE,"Assignee");
		fields.put(IssueFieldConstants.REPORTER,"Reporter");
		
		return fields;
	}
	
	private String getSelectedGroup(AbstractDescriptor descriptor) throws IllegalArgumentException{
		if (!(descriptor instanceof ValidatorDescriptor)){
			IllegalArgumentException exp = new IllegalArgumentException("Descriptor must be a ValidatorDescriptor.");
			throw exp;
		}
		
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
        String groups = (String)args.get("group");
        return groups;
	}
	
	private String getSelectedUserField(AbstractDescriptor descriptor) throws IllegalArgumentException{
		if (!(descriptor instanceof ValidatorDescriptor)){
			IllegalArgumentException exp = new IllegalArgumentException("Descriptor must be a ValidatorDescriptor.");
			throw exp;
		}
		
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		
		Map args = validatorDescriptor.getArgs();
		
		return (String)args.get("userfield");
	}
	
}
