package com.go2group.jira.plugin.workflow.validator;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import com.opensymphony.workflow.WorkflowException;

public class FieldMembershipValidator implements Validator {

    private CustomFieldManager customfieldManager;
    private GroupManager groupManager;
    private static final Logger log = LoggerFactory.getLogger(FieldMembershipValidator.class);

    public FieldMembershipValidator(CustomFieldManager customfieldManager, GroupManager groupManager) {
        this.customfieldManager = customfieldManager;
        this.groupManager = groupManager;
    }

    @Override
    public void validate(Map transientVars, Map args, PropertySet ps) throws InvalidInputException, WorkflowException {
        Issue issue = (Issue) transientVars.get("issue");
        String userfield = getUserField(args);
        String fieldvalue = null;

        if (userfield != null){
            if (userfield.startsWith("customfield_")){
                //Indicates Customfield
                CustomField cf = customfieldManager.getCustomFieldObject(userfield);
                if(issue.getCustomFieldValue(cf) != null){
                    fieldvalue = ((User)issue.getCustomFieldValue(cf)).getName(); //This field can only be of UserCF type
                }
            }else{
                if (userfield.equals(IssueFieldConstants.ASSIGNEE)){
                	ApplicationUser assignee = issue.getAssignee();

                    if (assignee != null){
                        fieldvalue = assignee.getName();
                    }
                }else if (userfield.equals(IssueFieldConstants.REPORTER)){
                	ApplicationUser reporter = issue.getReporter();

                    if (reporter != null){
                        fieldvalue = reporter.getName();
                    }
                }
            }

            if (fieldvalue == null){
                throw new InvalidInputException("No value set for field "+getUserFieldDisplayName(userfield));
            }else{
                //Validate the membership here
                if (!isMemberOf(fieldvalue, getGroup(args))){
                    throw new InvalidInputException(getUserFieldDisplayName(userfield) + " is not a member of group "+getGroup(args));
                }
            }
        }
    }

    private boolean isMemberOf(String user, String groups){
        boolean isMember = false;
        for(String group:groups.split(",")) {
            if(groupManager.isUserInGroup(user, group))
                isMember = true;
        }
        return isMember;
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

    private String getUserField(Map args){
        return (String)args.get("userfield");
    }

    private String getGroup(Map args){
        return (String)args.get("group");
    }

}
