package com.go2group.jira.plugin.web.panel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.Lists;

public class SubtaskAttachmentTabPanel extends AbstractIssueTabPanel {

    private final AttachmentManager attachmentManager;

    private final DateTimeFormatterFactory dateTimeFormatterFactory;

    private static final Logger log = LoggerFactory.getLogger(SubtaskAttachmentTabPanel.class);

    public SubtaskAttachmentTabPanel(AttachmentManager attachmentManager,
                                     DateTimeFormatterFactory dateTimeFormatterFactory) {
        this.attachmentManager = attachmentManager;
        this.dateTimeFormatterFactory = dateTimeFormatterFactory;
    }

    @Override
    public List getActions(Issue issue, ApplicationUser user) {
        List<IssueAction> subtaskAttachments = new ArrayList<IssueAction>();

        Collection<Issue> subtasks = issue.getSubTaskObjects();

        for (Issue st : subtasks) {

            List<Attachment> attachments = attachmentManager.getAttachments(st);

            if (attachments != null && attachments.size() > 0) {
                SubtaskAttachmentAction stAttachmentAction = new SubtaskAttachmentAction(descriptor, st, attachments,
                        dateTimeFormatterFactory);
                subtaskAttachments.add(stAttachmentAction);
            }
        }

        if (subtaskAttachments.isEmpty())
        {
            GenericMessageAction action = new GenericMessageAction("There are no attachments yet on the Sub-Tasks");
            return Lists.newArrayList(action);
        }else{
            GenericMessageAction action = new GenericMessageAction("");
            subtaskAttachments.add(action);
        }

        return subtaskAttachments;
    }

    @Override
    public boolean showPanel(Issue issue, ApplicationUser user) {
        if (issue.isSubTask()) {
            return false;
        } else {
            return true;
        }
    }
}
