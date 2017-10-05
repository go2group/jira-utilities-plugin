package com.go2group.jira.plugin.workflow.postfunction;

import java.sql.Timestamp;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class UpdateCreatedDatePostfunction extends AbstractJiraFunctionProvider{

    private static final Logger log = LoggerFactory.getLogger(UpdateCreatedDatePostfunction.class);
    private IssueManager issueManager;

    public UpdateCreatedDatePostfunction(IssueManager issueManager) {
        this.issueManager = issueManager;
    }

    @Override
    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue issue = getIssue(transientVars);
        ApplicationUser user = getCallerUserFromArgs(transientVars, args);
        issue.setCreated(new Timestamp(System.currentTimeMillis()));
        issueManager.updateIssue(user, issue, EventDispatchOption.DO_NOT_DISPATCH, false);
    }
}
