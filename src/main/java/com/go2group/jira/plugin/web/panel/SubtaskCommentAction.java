package com.go2group.jira.plugin.web.panel;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;

import static com.atlassian.jira.datetime.DateTimeStyle.COMPLETE;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

public class SubtaskCommentAction extends AbstractIssueAction{

	private final List<Comment> comments;
	
	private final Issue issue;
	
	private final DateTimeFormatterFactory dateTimeFormatterFactory;
	
	public SubtaskCommentAction(IssueTabPanelModuleDescriptor descriptor, 
			Issue issue, List<Comment> comments, DateTimeFormatterFactory dateTimeFormatterFactory) {
		super(descriptor);
		this.issue = issue;
		this.comments = comments;
		this.dateTimeFormatterFactory = dateTimeFormatterFactory;
	}
	
	public Issue getIssue() {
		return issue;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	@Override
	protected void populateVelocityParams(Map params) {
		params.put("action", this);
		params.put("subtask", issue);
		params.put("comments", comments);
	}
    
	@Override
	public Date getTimePerformed() {
		return null;
	}

	@Override
	public boolean isDisplayActionAllTab() {
		return false;
	}
	
    public String formatDisplayHtml(Date date)
    {
        if (date == null)
        {
            return null;
        }

        DateTimeFormatter completeFormatter = dateTimeFormatter().withStyle(COMPLETE);
        return escapeHtml(completeFormatter.format(date));
    }

    public String formatIso8601Html(Date date)
    {
        if (date == null)
        {
            return null;
        }

        DateTimeFormatter iso8601Formatter = dateTimeFormatter().withStyle(DateTimeStyle.ISO_8601_DATE_TIME);
        return escapeHtml(iso8601Formatter.format(date));
    }

    protected DateTimeFormatter dateTimeFormatter()
    {
        return dateTimeFormatterFactory.formatter().forLoggedInUser();
    }

}
