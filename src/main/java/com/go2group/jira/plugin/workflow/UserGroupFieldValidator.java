package com.go2group.jira.plugin.workflow;

import java.util.Collection;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
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

public class UserGroupFieldValidator implements Validator {

    private final CustomFieldManager customFieldManager;
    private final GroupManager groupManager;

    private String selected_field;
    private String selected_group;
    private String userfield;

    public UserGroupFieldValidator(CustomFieldManager customFieldManager, GroupManager groupManager) {
        this.customFieldManager = customFieldManager;
        this.groupManager = groupManager;
    }

    public void validate(Map transientVars, Map args, PropertySet ps)
            throws InvalidInputException, WorkflowException {
        Issue issue = (Issue) transientVars.get("issue");

        selected_field = (String) args.get(UserGroupFieldValidatorFactory.FIELD_NAME);
        selected_group = (String) args.get(UserGroupFieldValidatorFactory.GROUP_NAME);

        userfield = getUserField(args);

        ApplicationUser user = null;

        if (userfield != null){
            if (userfield.startsWith("customfield_")){
                //Indicates Customfield
                CustomField cf = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(userfield);
                user = ((ApplicationUser)issue.getCustomFieldValue(cf)); //This field can only be of UserCF type
            }else{
                if (userfield.equals(IssueFieldConstants.ASSIGNEE)){
                	ApplicationUser assignee = issue.getAssignee();

                    if (assignee != null){
                        user = assignee;
                    }
                }else if (userfield.equals(IssueFieldConstants.REPORTER)){
                	ApplicationUser reporter = issue.getReporter();

                    if (reporter != null){
                        user = reporter;
                    }
                }else if (userfield.equals(UserGroupFieldValidatorFactory.CURRENT_USER)){
                	ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

                    if (currentUser != null){
                        user = currentUser;
                    }
                }
            }
        }

        if(user!= null){
            if(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() == user){
                Collection<String> groups = this.groupManager.getGroupNamesForUser(user);
                for (String group : groups) {
                    if (group.equals(selected_group)) {
                        CustomField customField = customFieldManager.getCustomFieldObjectByName(selected_field);

                        if (customField != null) {
                            // Check if the custom field value is empty
                            checkValueEmpty(issue.getCustomFieldValue(customField));
                        }
                        checkStandardValue(issue);
                    }
                }
            }
        }else{
            throwUserNotFoundError();
        }
    }

    private void checkStandardValue( Issue issue) throws InvalidInputException{
        if ( IssueFieldConstants.AFFECTED_VERSIONS.equals ( selected_field ) ) {
            if (issue.getAffectedVersions().isEmpty()){
                throwError();
            }
            return;
        } // affect version
        else if ( IssueFieldConstants.ASSIGNEE.equals ( selected_field ) ) {
            checkValueEmpty(issue.getAssignee());
            checkValueEmpty(issue.getAssignee().getName() );
            return;
        } // assignee
        else if ( IssueFieldConstants.COMPONENTS.equals ( selected_field ) ) {
            if (issue.getComponentObjects().isEmpty()){
                throwError();
            }
            return;
        } // components
        else if ( IssueFieldConstants.CREATED.equals ( selected_field ) ) {
            checkValueEmpty(issue.getCreated());
            return;
        } // created
        else if ( IssueFieldConstants.DESCRIPTION.equals ( selected_field ) ) {
            checkValueEmpty(issue.getDescription());
            return;
        } // description

        else if ( IssueFieldConstants.DUE_DATE.equals ( selected_field ) ) {
            checkValueEmpty(issue.getDueDate());
            return;
        } // due date
        else if ( IssueFieldConstants.ENVIRONMENT.equals ( selected_field ) ) {
            checkValueEmpty(issue.getEnvironment());
            return;

        } // environment
        else if ( IssueFieldConstants.TIME_ESTIMATE.equals ( selected_field ) ) {
            checkValueEmpty(issue.getEstimate());
            return;

        } // estimate
        else if ( IssueFieldConstants.TIME_SPENT.equals (selected_field)) {
            checkValueEmpty(issue.getTimeSpent());
            return;
        } // work log
        else if ( IssueFieldConstants.FIX_FOR_VERSIONS.equals ( selected_field ) ) {
            if (issue.getFixVersions().isEmpty()){
                throwError();
            }
            return;
        } // fix versions
        else if ( IssueFieldConstants.ISSUE_TYPE.equals ( selected_field ) ) {
            checkValueEmpty(issue.getIssueTypeObject());
            checkValueEmpty(issue.getIssueTypeObject().getName());
            return;

        } // type
        else if ( IssueFieldConstants.ISSUE_KEY.equals ( selected_field ) ) {
            checkValueEmpty(issue.getKey());
            return;
        } // key
        else if ( IssueFieldConstants.TIME_ORIGINAL_ESTIMATE.equals ( selected_field ) ) {
            checkValueEmpty(issue.getOriginalEstimate());
            return;
        } // estimate
        else if ( IssueFieldConstants.PRIORITY.equals ( selected_field ) ) {
            checkValueEmpty(issue.getPriorityObject());
            checkValueEmpty(issue.getPriorityObject().getName());
            return;

        } // priority
        else if ( IssueFieldConstants.PROJECT.equals ( selected_field ) ) {
            checkValueEmpty(issue.getProjectObject());
            checkValueEmpty(issue.getProjectObject().getName());
            return;
        } // project
        else if ( IssueFieldConstants.REPORTER.equals ( selected_field ) ) {
            checkValueEmpty(issue.getReporter());
            checkValueEmpty(issue.getReporter().getName());
            return;
        } // reporter
        else if ( IssueFieldConstants.RESOLUTION.equals ( selected_field ) ) {
            checkValueEmpty(issue.getResolutionObject());
            checkValueEmpty(issue.getResolutionObject().getName() );
            return;
        } // resolution
        else if ( IssueFieldConstants.RESOLUTION_DATE.equals ( selected_field ) ) {
            checkValueEmpty(issue.getResolutionDate());
            return;
        } // resolution date
        else if ( IssueFieldConstants.SECURITY.equals ( selected_field ) ) {
            checkValueEmpty(issue.getSecurityLevelId());
            //checkValueEmpty(issue.getSecurityLevel().get("name"));
            return;
        } // security
        else if ( IssueFieldConstants.STATUS.equals ( selected_field ) ) {
            checkValueEmpty(issue.getStatusObject());
            checkValueEmpty(issue.getStatusObject().getName());
            return;
        } // status
        else if ( IssueFieldConstants.SUMMARY.equals ( selected_field ) ) {
            checkValueEmpty(issue.getSummary());
            return;
        } // summary
        else if ( IssueFieldConstants.UPDATED.equals ( selected_field ) ) {
            checkValueEmpty(issue.getUpdated());
            return;
        } // updated
        else if ( IssueFieldConstants.VOTES.equals ( selected_field ) ) {
            checkValueEmpty(issue.getVotes());
            return;
        } // votes

    }

    private void checkValueEmpty(Object value) throws InvalidInputException{
        if (value == null || value.toString().trim().isEmpty()) {
            throwError();
        }
    }

    private void throwError() throws InvalidInputException {
        throw new InvalidInputException("The field: "
                + selected_field + " can't be empty for the user ("+ UserGroupFieldValidatorFactory.getUserFieldDisplayName(userfield) +") under group " + selected_group );
    }

    private String getUserField(Map args){
        return (String)args.get(UserGroupFieldValidatorFactory.SELECTED_USER_FIELD);
    }

    private void throwUserNotFoundError() throws InvalidInputException {
        throw new InvalidInputException(UserGroupFieldValidatorFactory.getUserFieldDisplayName(userfield) + " field cannot be empty. This value is required to perform this transition." );
    }

}
