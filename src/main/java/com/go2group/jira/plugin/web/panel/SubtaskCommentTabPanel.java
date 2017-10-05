package com.go2group.jira.plugin.web.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.CommentPermissionManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.Lists;

public class SubtaskCommentTabPanel extends AbstractIssueTabPanel{

    private final CommentManager commentManager;

    private final DateTimeFormatterFactory dateTimeFormatterFactory;

    private static final Logger log = LoggerFactory.getLogger(SubtaskAttachmentTabPanel.class);

    public SubtaskCommentTabPanel(CommentManager commentManager, CommentPermissionManager commentPermissionManager,
                                  IssueManager issueManager, FieldLayoutManager fieldLayoutManager, RendererManager rendererManager,
                                  DateTimeFormatter dateTimeFormatter, DateTimeFormatterFactory dateTimeFormatterFactory) {
        this.commentManager = commentManager;
        this.dateTimeFormatterFactory = dateTimeFormatterFactory;
    }

    @Override
    public List getActions(Issue issue, ApplicationUser user) {
        List<IssueAction> subtaskComments = new ArrayList<IssueAction>();

        Collection<Issue> subtasks = issue.getSubTaskObjects();

        for (Issue st : subtasks){

            List<Comment> comments = commentManager.getCommentsForUser(st, ComponentAccessor.getUserManager().getUserByName(user.getName()));

            if (comments != null && comments.size() > 0){
                SubtaskCommentAction stCommentAction = new SubtaskCommentAction(descriptor,st,comments,dateTimeFormatterFactory);
                subtaskComments.add(stCommentAction);
            }
        }

        if (subtaskComments.isEmpty())
        {
            GenericMessageAction action = new GenericMessageAction("There are no comments yet on the Sub-Tasks");
            return Lists.newArrayList(action);
        }else{
            GenericMessageAction action = new GenericMessageAction("");
            subtaskComments.add(action);
        }

        return subtaskComments;
    }

    @Override
    public boolean showPanel(Issue issue, ApplicationUser remoteUser) {
        //Display this panel only to issues and not subtasks

        if (issue.isSubTask()){
            return false;
        }else{
            return true;
        }

    }
}
