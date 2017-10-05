package com.go2group.jira.plugin.listener;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.go2group.jira.plugin.ao.PriorityDueDate;
import com.go2group.jira.plugin.ao.PriorityDueDateService;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/23/14
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class DueDateBasedOnPriority implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(DueDateBasedOnPriority.class);

    private final EventPublisher eventPublisher;
    private final IssueService issueService;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final ConstantsManager constantsManager;
    private PriorityDueDateService priorityDueDateService;

    /**
     * Constructor.
     * @param eventPublisher injected {@code EventPublisher} implementation.
     */
    public DueDateBasedOnPriority(EventPublisher eventPublisher, IssueService issueService,
                                  JiraAuthenticationContext jiraAuthenticationContext,
                                  ConstantsManager constantsManager,
                                  PriorityDueDateService priorityDueDateService) {
        this.eventPublisher = eventPublisher;
        this.issueService = issueService;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.constantsManager = constantsManager;
        this.priorityDueDateService = priorityDueDateService;
    }

    /**
     * Called when the plugin has been enabled.
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // register ourselves with the EventPublisher
        eventPublisher.register(this);
    }

    /**
     * Called when the plugin is being disabled or removed.
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        // unregister ourselves with the EventPublisher
        eventPublisher.unregister(this);
    }

    /**
     * Receives any {@code IssueEvent}s sent by JIRA.
     * @param issueEvent the IssueEvent passed to us
     */
    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        Long eventTypeId = issueEvent.getEventTypeId();
        DateTimeFormatter dateTimeFormatter = ComponentAccessor.getComponent(DateTimeFormatter.class);
        Issue issue = issueEvent.getIssue();
        Priority priority = issue.getPriorityObject();
        // if it's an event we're interested in, log it
        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID) || eventTypeId.equals(EventType.ISSUE_UPDATED_ID)) {
            updateIssue(issue, priority);
        }
    }

    private void updateIssue(Issue issue, Priority priority){
        DateTime dueDateOnPriority = getDueDateOnPriority(issue, priority.getName());
        if(dueDateOnPriority != null){
            IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
            try{
                issueInputParameters.setDueDate(getDate(dueDateOnPriority));
                IssueService.UpdateValidationResult updateValidationResult = issueService.validateUpdate(jiraAuthenticationContext.getLoggedInUser(), issue.getId(), issueInputParameters);
                if(updateValidationResult.isValid()){
                    IssueService.IssueResult updateResult = issueService.update(jiraAuthenticationContext.getLoggedInUser(), updateValidationResult);
                    if(!updateResult.isValid()){
                        log.error("Error updating issue. " + updateResult.getErrorCollection().toString(), updateResult.getErrorCollection());
                    }
                }
                else {
                    log.error("Error updating issue. " + updateValidationResult.getErrorCollection().toString(), updateValidationResult.getErrorCollection());
                }
            }
            catch(DateFormatException exc){
                log.error(exc.getMessage());
            }
        }
    }

    private DateTime getDueDateOnPriority(Issue issue, String priority){
        DateTime now = new DateTime();
        PriorityDueDate priorityDueDate = priorityDueDateService.getPriorityDueDateMapping(priority);
        if(priorityDueDate != null){
            //JUP-63 Due date should be set based on issue created date
            return now.withDayOfYear(new DateTime(issue.getCreated()).getDayOfYear() + priorityDueDate.getDays());
        }
        return null;
    }

    private String getDate(DateTime value) throws DateFormatException {
        if(value != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("d/MMM/yy");
            return dateFormatter.format(value.toDate());
        }
        else{
            return null;
        }
    }

    class DateFormatException extends Exception{
        public String message;

        public DateFormatException(String value){
            this.message = "Unable to parse date.";
        }

        public String getMessage(){return message;}
    }

}

