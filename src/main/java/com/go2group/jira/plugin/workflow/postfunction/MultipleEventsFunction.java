package com.go2group.jira.plugin.workflow.postfunction;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/*
 This is the post-function class that gets executed at the end of the transition.
 Any parameters that were saved in your factory class will be available in the transientVars Map.
 */

public class MultipleEventsFunction extends AbstractJiraFunctionProvider {
    private static final Logger log = LoggerFactory.getLogger(MultipleEventsFunction.class);
    public static final String EVENTS_FIELD = "eventsField";

    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue issue = getIssue(transientVars);
        ApplicationUser user = getCaller(transientVars, args);
        String eventsField = (String) args.get(EVENTS_FIELD);
        if (eventsField != null) {
            String[] events = eventsField.split(",");
            for (String event : events) {
                ComponentAccessor.getIssueEventManager().dispatchEvent(new Long(event), issue, Collections.EMPTY_MAP,
                        user);
                log.debug("Throwing event:" + event);
            }
        }
    }
}