package com.go2group.jira.plugin.web.panel;

import static com.atlassian.jira.datetime.DateTimeStyle.COMPLETE;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.atlassian.core.util.FileSize;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.atlassian.jira.web.util.FileIconBean;
import com.atlassian.jira.web.util.FileIconBean.FileIcon;

public class SubtaskAttachmentAction extends AbstractIssueAction{

	private final Issue subtask;
	
	private final List<Attachment> attachments;
	
	private final DateTimeFormatterFactory dateTimeFormatterFactory;
	
	private final FileIconBean fileIconBean;
	
	public SubtaskAttachmentAction(IssueTabPanelModuleDescriptor descriptor, Issue subtask, List<Attachment> attachments,
			DateTimeFormatterFactory dateTimeFormatterFactory) {
		super(descriptor);
		this.subtask = subtask;
		this.attachments = attachments;
		this.dateTimeFormatterFactory = dateTimeFormatterFactory;
		this.fileIconBean = ComponentAccessor.getComponentOfType(FileIconBean.class);
	}

	@Override
	public Date getTimePerformed() {
		return null;
	}

	@Override
	protected void populateVelocityParams(Map params) {
		params.put("action", this);
		params.put("subtask", subtask);
		params.put("attachments", attachments);
	}
	
	@Override
	public boolean isDisplayActionAllTab() {
		return false;
	}
	
	public String getFileIcon(String filename, String mimetype){
		FileIcon icon = fileIconBean.getFileIcon(filename, mimetype);
		
		if (icon != null){
			return icon.getIcon();
		}else{
			return "file.gif";
		}
	}

	public String formatSize(Long size){
		return FileSize.format(size);
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
