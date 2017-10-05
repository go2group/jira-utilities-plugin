package com.go2group.jira.plugin.workflow.postfunction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.TransitionValidationResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.impl.MultiSelectCFType;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexingService;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.spi.WorkflowEntry;

/*
 This is the post-function class that gets executed at the end of the transition.
 Any parameters that were saved in your factory class will be available in the transientVars Map.
 */

public class ProgressWorkflowFunction extends AbstractJiraFunctionProvider {
    private static final String ID = "id";
    private static final String STATE = "state";
    private static final String OS_WORKFLOW_ENTRY = "OSWorkflowEntry";
    private static final Logger log = LoggerFactory.getLogger(ProgressWorkflowFunction.class);
    public static final String TRANSITION_FIELD = "transitionField";
    public static final String CUSTOM_FIELD = "customField";
    public static final String CUSTOM_FIELD_VALUE = "cfValue";

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {

        MutableIssue issue = getIssue(transientVars);
        String transitionId = (String) args.get(TRANSITION_FIELD);
        String cField = (String) args.get(CUSTOM_FIELD);
        String cfVal = (String) args.get(CUSTOM_FIELD_VALUE);

        if (StringUtils.isEmpty(transitionId)) {
            log.error("No transition defined in Progress Workflow post function");
        } else if (StringUtils.isEmpty(cField) || StringUtils.isEmpty(cfVal)) {
            log.error("Valid condition not defined in Progress Workflow post function");
        } else {
            // Evaluate transition
            CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(cField);
            if (validCondition(issue, customField, cfVal)) {
                // Use the transition to progress
                IssueService issueService = ComponentAccessor.getIssueService();
                ApplicationUser user = getCaller(transientVars, args);
                try {
                    OfBizDelegator delegator = ComponentAccessor.getOfBizDelegator();
                    List<GenericValue> osWorkflowEntries = delegator.findByAnd(OS_WORKFLOW_ENTRY,
                            MapBuilder.build(ID, issue.getWorkflowId()));
                    if (osWorkflowEntries != null && osWorkflowEntries.size() > 0) {
                        GenericValue osWorkflowEntry = osWorkflowEntries.get(0);
                        if (osWorkflowEntry.getInteger(STATE) == WorkflowEntry.CREATED) {
                            osWorkflowEntry.set(STATE, WorkflowEntry.ACTIVATED);
                            osWorkflowEntry.store();
                        }
                    }
                    TransitionValidationResult transitionValidationResult = issueService.validateTransition(user,
                            issue.getId(), new Integer(transitionId), issueService.newIssueInputParameters());
                    if (transitionValidationResult.isValid()) {
                        issueService.transition(user, transitionValidationResult);

                        //TODO Fixing the issue
                        //refresh from the updated issue
                        IssueManager issueManager = ComponentAccessor.getIssueManager();
                        Issue refreshedIssue = issueManager.getIssueObject(issue.getId());
                        getIssue(transientVars).setStatusObject(refreshedIssue.getStatusObject());

                        //reindex after this
                        try {
                        	IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService.class);
                        	if(issueIndexingService != null){
                        		issueIndexingService.reIndex(refreshedIssue);
                        	}
                        } catch (IndexException e) {
                            log.debug(e.getMessage(), e);
                            log.error(e.getMessage());
                        }

                    } else {
                        Map<String, String> errors = transitionValidationResult.getErrorCollection().getErrors();
                        Set<String> errorKeys = errors.keySet();
                        for (String errorKey : errorKeys) {
                            log.error(errors.get(errorKey));
                        }
                        Collection<String> errorMessages = transitionValidationResult.getErrorCollection()
                                .getErrorMessages();
                        for (String errorMessage : errorMessages) {
                            log.error(errorMessage);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error transitioning " + issue.getKey() + " using ID:" + transitionId, e);
                }
            }
        }
    }

    private boolean validCondition(MutableIssue issue, CustomField customField, String cfVal) {
        Object customFieldValue = issue.getCustomFieldValue(customField);
        if (customFieldValue != null) {
            if (customField.getCustomFieldType() instanceof GenericTextCFType) {
                return cfVal.equals(customFieldValue);
            } else if (customField.getCustomFieldType() instanceof SelectCFType) {
                return cfVal.equals(((Option) customFieldValue).toString());
            } else if (customField.getCustomFieldType() instanceof NumberCFType) {
                return ((Double) customFieldValue).equals(new Double(cfVal));
            } else if (customField.getCustomFieldType() instanceof MultiSelectCFType) {
                List<Option> vals = (List<Option>) customFieldValue;
                for (Option val : vals) {
                    if (val.toString().equals(cfVal)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}