package com.go2group.jira.plugin.workflow;

import com.atlassian.core.util.map.EasyMap;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.customfields.impl.UserCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import org.apache.commons.collections.map.ListOrderedMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserProjectRoleFieldValidatorFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory {


    public static final String ROLE_NAME = "role";
    private static final String ROLES = "roles";
    public  static final String FIELD_NAME = "field";
    private static final String FIELDS = "fields";
    private static final String NOT_DEFINED = "Not Defined";
    private static final String USER_CUSTOM_FIELD = "usercustomfield";
    private static final String USER_JIRA_FIELD = "userjirafield";
    public static final String SELECTED_USER_FIELD = "selecteduserfield";
    public static final String CURRENT_USER = "currentuser";



    private final CustomFieldManager customFieldManager;
    private final GroupManager groupManager;

    public UserProjectRoleFieldValidatorFactory(CustomFieldManager customFieldManager, GroupManager groupManager) {
        this.customFieldManager = customFieldManager;
        this.groupManager = groupManager;
    }

    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        velocityParams.put(USER_CUSTOM_FIELD,getUserCustomFields());
        velocityParams.put(USER_JIRA_FIELD, getUserJiraFields());
        velocityParams.put(SELECTED_USER_FIELD, getSelectedUserField(descriptor));
        velocityParams.put(ROLE_NAME, getProjectRoleName(descriptor));
        velocityParams.put(ROLES, getProjectRoles());
        velocityParams.put(FIELD_NAME, getFieldName(descriptor));
        velocityParams.put(FIELDS, getAllFields());
    }

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
        velocityParams.put(ROLES, getProjectRoles());
        velocityParams.put(FIELDS, getAllFields());
        velocityParams.put(USER_CUSTOM_FIELD,getUserCustomFields());
        velocityParams.put(USER_JIRA_FIELD, getUserJiraFields());
    }

    private Collection getProjectRoles(){
        return  ComponentAccessor.getComponentOfType(ProjectRoleManager.class).getProjectRoles();
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
        velocityParams.put(ROLE_NAME, getProjectRoleName(descriptor));
        velocityParams.put(FIELD_NAME, getFieldName(descriptor));
        velocityParams.put(SELECTED_USER_FIELD, getUserFieldDisplayName(getSelectedUserField(descriptor)));
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getDescriptorParams(Map<String, Object> conditionParams) {
        if (conditionParams != null && conditionParams.containsKey(ROLE_NAME) && conditionParams.containsKey(FIELD_NAME) && conditionParams.containsKey(SELECTED_USER_FIELD)) {
            return EasyMap.build(ROLE_NAME, extractSingleParam(conditionParams, ROLE_NAME), FIELD_NAME, extractSingleParam(conditionParams, FIELD_NAME),SELECTED_USER_FIELD,extractSingleParam(conditionParams, SELECTED_USER_FIELD));
        }

        return EasyMap.build();
    }

    private String getProjectRoleName(AbstractDescriptor descriptor) {
        if (!(descriptor instanceof ValidatorDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
        }

        ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;

        String role = (String) validatorDescriptor.getArgs().get(ROLE_NAME);
        if (role != null && role.trim().length() > 0)
            return role;
        else
            return NOT_DEFINED;
    }

    private List<String> getAllFields(){
        List<String> fields  = getCFFields();
        fields.addAll(getStandardFieldNameList());
        return fields;
    }

    private List<String> getCFFields() {
        List<String> fields = new ArrayList<String>();
        List<CustomField> customFields = customFieldManager.getCustomFieldObjects();
        for(CustomField f: customFields){
            fields.add(f.toString());
        }
        return fields;
    }

    public List<String> getStandardFieldNameList() {
        List<String> fields = new ArrayList<String>();
        fields.add ( IssueFieldConstants.AFFECTED_VERSIONS );
        fields.add ( IssueFieldConstants.ASSIGNEE );
        fields.add ( IssueFieldConstants.COMPONENTS );
        fields.add ( IssueFieldConstants.CREATED );
        fields.add ( IssueFieldConstants.DESCRIPTION );
        fields.add ( IssueFieldConstants.DUE_DATE );
        fields.add ( IssueFieldConstants.ENVIRONMENT );
        fields.add ( IssueFieldConstants.TIME_ESTIMATE );
        fields.add ( IssueFieldConstants.TIME_SPENT);
        fields.add ( IssueFieldConstants.FIX_FOR_VERSIONS );
        fields.add ( IssueFieldConstants.ISSUE_TYPE );
        fields.add ( IssueFieldConstants.ISSUE_KEY );
        fields.add ( IssueFieldConstants.TIME_ORIGINAL_ESTIMATE );
        fields.add ( IssueFieldConstants.PRIORITY );
        fields.add ( IssueFieldConstants.PROJECT );
        fields.add ( IssueFieldConstants.REPORTER );
        fields.add ( IssueFieldConstants.RESOLUTION );
        fields.add ( IssueFieldConstants.RESOLUTION_DATE );
        fields.add ( IssueFieldConstants.SECURITY );
        fields.add ( IssueFieldConstants.STATUS );
        fields.add ( IssueFieldConstants.SUMMARY );
        fields.add ( IssueFieldConstants.UPDATED );
        fields.add ( IssueFieldConstants.VOTES );
        return fields;
    } // getStandardFieldNameList

    private String getFieldName(AbstractDescriptor descriptor) {
        if (!(descriptor instanceof ValidatorDescriptor)) {
            throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
        }

        ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;

        String field = (String) validatorDescriptor.getArgs().get(FIELD_NAME);
        if (field != null && field.trim().length() > 0)
            return field;
        else
            return NOT_DEFINED;
    }

    public static String getUserFieldDisplayName(String field){
        if (field != null && field.startsWith("customfield_")){
            //It is a customfield
            CustomField cf = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(field);

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
            }else if (field != null && field.equals(CURRENT_USER)){
                return "Current User";
            }else{
                return "";
            }
        }
    }

    private List<CustomField> getUserCustomFields(){
        List<CustomField> customfields = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();

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
        fields.put(CURRENT_USER,"Current User");
        return fields;
    }

    private String getSelectedUserField(AbstractDescriptor descriptor) throws IllegalArgumentException{
        if (!(descriptor instanceof ValidatorDescriptor)){
            IllegalArgumentException exp = new IllegalArgumentException("Descriptor must be a ValidatorDescriptor.");
            throw exp;
        }

        ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
        Map args = validatorDescriptor.getArgs();
        return (String)args.get(SELECTED_USER_FIELD);
    }

}
