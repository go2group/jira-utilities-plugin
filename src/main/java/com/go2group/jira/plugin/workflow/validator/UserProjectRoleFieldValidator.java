package com.go2group.jira.plugin.workflow.validator;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.go2group.jira.plugin.util.JiraSystemFieldName;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import com.opensymphony.workflow.WorkflowException;

public class UserProjectRoleFieldValidator implements Validator {

    private final CustomFieldManager customFieldManager;
    private final GroupManager groupManager;

    private String selected_fields;
    private String selected_role;
    private String userfield;

    private static final Logger log = LoggerFactory.getLogger(UserProjectRoleFieldValidator.class);
    private final JiraSystemFieldName systemFieldName;


    public UserProjectRoleFieldValidator(CustomFieldManager customFieldManager,
                                         GroupManager groupManager,
                                         JiraSystemFieldName systemFieldName) {
        this.customFieldManager = customFieldManager;
        this.groupManager = groupManager;
        this.systemFieldName = systemFieldName;
    }

    public void validate(Map transientVars, Map args, PropertySet ps)
            throws InvalidInputException, WorkflowException {
        Issue issue = (Issue) transientVars.get("issue");

        selected_fields = (String) args.get(UserProjectRoleFieldValidatorFactory.FIELD_NAME);
        selected_role = (String) args.get(UserProjectRoleFieldValidatorFactory.ROLE_NAME);
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
                }else if (userfield.equals(UserProjectRoleFieldValidatorFactory.CURRENT_USER)){
                	ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();

                    if (currentUser != null){
                        user = currentUser;
                    }
                }
            }
        }

        if(user!= null){
            if(ComponentAccessor.getJiraAuthenticationContext().getUser().getUsername().equals(user.getName())){
                Collection<ProjectRole> roles = ComponentAccessor.getComponentOfType(ProjectRoleManager.class).getProjectRoles(user, issue.getProjectObject());
                for (ProjectRole role : roles) {
                    if (role.getName().equals(selected_role)) {
                        for(String field:selected_fields.split(",")) {
                            CustomField customField = customFieldManager.getCustomFieldObjectByName(field);

                            if (customField != null) {
                                // Check if the custom field value is empty
                                checkValueEmpty(field, issue.getCustomFieldValue(customField));
                            }
                            checkStandardValue(issue, field);
                        }
                    }
                }
            }
        }else{
            throwUserNotFoundError();
        }
    }

    private void checkStandardValue( Issue issue, String selected_field) throws InvalidInputException{
        if ( IssueFieldConstants.AFFECTED_VERSIONS.equals ( selected_field ) ) {
            if (issue.getAffectedVersions().isEmpty()){
                throwError(selected_field);
            }
            return;
        } // affect version
        else if ( IssueFieldConstants.ASSIGNEE.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getAssignee());
            checkValueEmpty(selected_field, issue.getAssignee().getName() );
            return;
        } // assignee
        else if ( IssueFieldConstants.COMPONENTS.equals ( selected_field ) ) {
            if (issue.getComponentObjects().isEmpty()){
                throwError(selected_field);
            }
            return;
        } // components
        else if ( IssueFieldConstants.CREATED.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getCreated());
            return;
        } // created
        else if ( IssueFieldConstants.DESCRIPTION.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getDescription());
            return;
        } // description

        else if ( IssueFieldConstants.DUE_DATE.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getDueDate());
            return;
        } // due date
        else if ( IssueFieldConstants.ENVIRONMENT.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getEnvironment());
            return;

        } // environment
        else if ( IssueFieldConstants.TIME_ESTIMATE.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getEstimate());
            return;

        } // estimate
        else if ( IssueFieldConstants.TIME_SPENT.equals (selected_field)) {
            checkValueEmpty(selected_field, issue.getTimeSpent());
            return;
        } // work log
        else if ( IssueFieldConstants.FIX_FOR_VERSIONS.equals ( selected_field ) ) {
            if (issue.getFixVersions().isEmpty()){
                throwError(selected_field);
            }
            return;
        } // fix versions
        else if ( IssueFieldConstants.ISSUE_TYPE.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getIssueTypeObject());
            checkValueEmpty(selected_field, issue.getIssueTypeObject().getName());
            return;

        } // type
        else if ( IssueFieldConstants.ISSUE_KEY.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getKey());
            return;
        } // key
        else if ( IssueFieldConstants.TIME_ORIGINAL_ESTIMATE.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getOriginalEstimate());
            return;
        } // estimate
        else if ( IssueFieldConstants.PRIORITY.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getPriorityObject());
            checkValueEmpty(selected_field, issue.getPriorityObject().getName());
            return;

        } // priority
        else if ( IssueFieldConstants.PROJECT.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getProjectObject());
            checkValueEmpty(selected_field, issue.getProjectObject().getName());
            return;
        } // project
        else if ( IssueFieldConstants.REPORTER.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getReporter());
            checkValueEmpty(selected_field, issue.getReporter().getName());
            return;
        } // reporter
        else if ( IssueFieldConstants.RESOLUTION.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getResolutionObject());
            checkValueEmpty(selected_field, issue.getResolutionObject().getName() );
            return;
        } // resolution
        else if ( IssueFieldConstants.RESOLUTION_DATE.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getResolutionDate());
            return;
        } // resolution date
        else if ( IssueFieldConstants.SECURITY.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getSecurityLevelId());
            //checkValueEmpty(issue.getSecurityLevel().get("name"));
            return;
        } // security
        else if ( IssueFieldConstants.STATUS.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getStatusObject());
            checkValueEmpty(selected_field, issue.getStatusObject().getName());
            return;
        } // status
        else if ( IssueFieldConstants.SUMMARY.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getSummary());
            return;
        } // summary
        else if ( IssueFieldConstants.UPDATED.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getUpdated());
            return;
        } // updated
        else if ( IssueFieldConstants.VOTES.equals ( selected_field ) ) {
            checkValueEmpty(selected_field, issue.getVotes());
            return;
        } // votes

    }

    private void checkValueEmpty(String selected_field, Object value) throws InvalidInputException{
        if (value == null || value.toString().trim().isEmpty()) {
            throwError(selected_field);
        }
    }

    private void throwError(String selected_field) throws InvalidInputException {
        throw new InvalidInputException("The field: "
                + systemFieldName.getSystemFieldName(selected_field) + " can't be empty for the user ("+ UserProjectRoleFieldValidatorFactory.getUserFieldDisplayName(userfield) +")  under project role " + selected_role );
    }

    private String getUserField(Map args){
        return (String)args.get(UserProjectRoleFieldValidatorFactory.SELECTED_USER_FIELD);
    }

    private void throwUserNotFoundError() throws InvalidInputException {
        throw new InvalidInputException(UserProjectRoleFieldValidatorFactory.getUserFieldDisplayName(userfield) + " field cannot be empty. This value is required to perform this transition." );
    }
}
