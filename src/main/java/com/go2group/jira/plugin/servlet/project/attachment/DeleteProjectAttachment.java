package com.go2group.jira.plugin.servlet.project.attachment;

import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.go2group.jira.plugin.ao.entity.ProjectAttachment;
import com.go2group.jira.plugin.ao.service.ProjectLevelAttachmentService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 29/05/14
 * Time: 6:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteProjectAttachment extends HttpServlet {

    private ProjectLevelAttachmentService projectLevelAttachmentService;
    private TemplateRenderer templateRenderer;
    private ProjectManager projectManager;
    private AvatarService avatarService;
    private UserManager userManager;

    public DeleteProjectAttachment(ProjectLevelAttachmentService projectLevelAttachmentService,
                                   TemplateRenderer templateRenderer,
                                   ProjectManager projectManager,
                                   AvatarService avatarService,
                                   UserManager userManager){
        this.projectLevelAttachmentService = projectLevelAttachmentService;
        this.templateRenderer = templateRenderer;
        this.projectManager = projectManager;
        this.avatarService = avatarService;
        this.userManager = userManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if(ComponentAccessor.getJiraAuthenticationContext().getUser() == null){
            String redirectPath = URLEncoder.encode(req.getServletPath() + "?" + req.getQueryString(), "UTF-8");
            resp.sendRedirect(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/login.jsp" + "?permissionViolation=true&os_destination=" + redirectPath);
            return;
        }
         Map context = new HashMap();
        resp.setContentType("text/html;charset=utf-8");

        ProjectAttachment projectAttachment = projectLevelAttachmentService.getProjectAttachment(Integer.parseInt(req.getParameter("attachmentId")));
        DateTimeFormatter dateTimeFormatter = ComponentAccessor.getComponentOfType(DateTimeFormatter.class);

        context.put("attachment", projectAttachment);
        context.put("attachmentId", req.getParameter("attachmentId"));
        context.put("project", projectManager.getProjectByCurrentKey(req.getParameter("project")));
        context.put("baseUrl", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));
        context.put("dateFormatter", dateTimeFormatter);
        context.put("avatarService", avatarService);
        context.put("userManager", userManager);
        templateRenderer.render("/templates/action/project-attachments-action/project-attachments-action-delete.vm", context, resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map context = new HashMap();
        resp.setContentType("text/html;charset=utf-8");

        try{
            projectLevelAttachmentService.deleteProjectAttachment(Integer.parseInt(req.getParameter("attachmentId")));
            resp.sendRedirect(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/browse/"+req.getParameter("project"));
        }
        catch(IOException exc){
           context.put("error", "Error deleting file." + exc.getMessage());
        }
    }

}