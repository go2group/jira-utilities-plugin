package com.go2group.jira.plugin.panels;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.plugin.projectpanel.ProjectTabPanel;
import com.atlassian.jira.plugin.projectpanel.impl.AbstractProjectTabPanel;
import com.atlassian.jira.project.browse.BrowseContext;
import com.atlassian.jira.user.util.UserManager;
import com.go2group.jira.plugin.ao.service.ProjectLevelAttachmentService;


public class ProjectLevelAttachmentsTabPanel extends AbstractProjectTabPanel implements ProjectTabPanel
{

    private final ActiveObjects activeObjects;
    private final ProjectLevelAttachmentService projectLevelAttachmentService;
    private AvatarService avatarService;
    private UserManager userManager;

    public ProjectLevelAttachmentsTabPanel(ActiveObjects activeObjects,
                                           ProjectLevelAttachmentService projectLevelAttachmentService,
                                           AvatarService avatarService,
                                           UserManager userManager){
        this.activeObjects = activeObjects;
        this.projectLevelAttachmentService = projectLevelAttachmentService;
        this.avatarService = avatarService;
        this.userManager = userManager;
    }

    private static final Logger log = LoggerFactory.getLogger(ProjectLevelAttachmentsTabPanel.class);

    public Map<String, Object> createVelocityParams(BrowseContext ctx){

        Map map = new HashMap();

        DateTimeFormatter dateTimeFormatter = ComponentAccessor.getComponentOfType(DateTimeFormatter.class);

        map.put("project", ctx.getProject());
        map.put("attachments", projectLevelAttachmentService.getProjectAttachmentsFromProjectKey(ctx.getProject().getKey()));
        map.put("dateFormatter", dateTimeFormatter);
        map.put("avatarService", avatarService);
        map.put("loggedInUser", ComponentAccessor.getJiraAuthenticationContext().getUser());
        map.put("userManager", userManager);


        return map;

    }

    public boolean showPanel(BrowseContext context)
    {
        return true;
    }
}
