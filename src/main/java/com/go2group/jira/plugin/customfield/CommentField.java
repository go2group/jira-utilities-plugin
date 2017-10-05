package com.go2group.jira.plugin.customfield;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.RendererManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.JiraKeyUtils;

public class CommentField extends GenericTextCFType {

    private final CommentManager commentManager;
    private final JiraAuthenticationContext authenticationContext;
    private final FieldLayoutManager fieldLayoutManager;
    private final RendererManager rendererManager;

    protected CommentField(CustomFieldValuePersister customFieldValuePersister,
                           GenericConfigManager genericConfigManager, CommentManager commentManager,
                           JiraAuthenticationContext authenticationContext, FieldLayoutManager fieldLayoutManager,
                           RendererManager rendererManager) {
        super(customFieldValuePersister, genericConfigManager);
        this.commentManager = commentManager;
        this.authenticationContext = authenticationContext;
        this.fieldLayoutManager = fieldLayoutManager;
        this.rendererManager = rendererManager;
    }

    private static final Logger log = LoggerFactory.getLogger(CommentField.class);

    @Override
    public Map<String, Object> getVelocityParameters(final Issue issue, final CustomField field,
                                                     final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);

        ApplicationUser user = this.authenticationContext.getLoggedInUser();
        if(issue.isCreated()){
            List<Comment> comments = this.commentManager.getCommentsForUser(issue, user);
            if (this.commentManager.getCommentsForUser(issue, user) != null && this.commentManager.getCommentsForUser(issue, user).size() > 0) {
                map.put("comments", comments);
                Map<Long, String> formattedComments = new HashMap<Long, String>();
                for (Comment comment : comments) {
                    formattedComments.put(comment.getId(), getHtmlValue(comment.getBody(), issue, comment));
                }
                map.put("formattedComments", formattedComments);
            }
        }
        return map;
    }

    /**
     * Retrieves the html formatted value.
     * <p/>
     * A simple string (with linked bug keys displayed) is returned if a
     * rendered version cannot be generated.
     *
     * @return String the html formatted value.
     */
    public String getHtmlValue(String value, Issue issue, Comment comment) {
        // Try to generate rendered values for description
        try {
            FieldLayout fieldLayout = fieldLayoutManager.getFieldLayout(issue);
            FieldLayoutItem fieldLayoutItem = fieldLayout.getFieldLayoutItem(IssueFieldConstants.COMMENT);
            String rendererType = (fieldLayoutItem != null) ? fieldLayoutItem.getRendererType() : null;

            String renderedContent = rendererManager.getRenderedContent(rendererType, value,
                    issue.getIssueRenderContext());
            return renderedContent.replaceAll("<p>", "<p style='margin-top:0;margin-bottom:10px;'>");
        } catch (Exception e) {
            log.warn("Unable to produce rendered version of comment on " + issue.getKey(), e);
            return JiraKeyUtils.linkBugKeys(value);
        }
    }
}