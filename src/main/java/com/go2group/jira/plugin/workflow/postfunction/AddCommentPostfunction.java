package com.go2group.jira.plugin.workflow.postfunction;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

public class AddCommentPostfunction extends AbstractJiraFunctionProvider{

    private static final Logger log = LoggerFactory.getLogger(AddCommentPostfunction.class);

    private final CommentManager commentManager;

    public AddCommentPostfunction(CommentManager commentManager) {
        this.commentManager = commentManager;
    }

    @Override
    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {

        Issue issue = getIssue(transientVars);
        ApplicationUser user = getCallerUserFromArgs(transientVars, args);
        String configuredComment = (String)args.get("comment");
        log.debug("Configured Comment : "+ configuredComment);
        commentManager.create(issue, user, configuredComment, false);
        log.debug("Comment added successfully");
    }
}
