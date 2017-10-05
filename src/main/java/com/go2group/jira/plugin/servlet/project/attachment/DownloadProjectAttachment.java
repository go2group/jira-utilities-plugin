package com.go2group.jira.plugin.servlet.project.attachment;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.go2group.jira.plugin.ao.entity.ProjectAttachment;
import com.go2group.jira.plugin.ao.service.ProjectLevelAttachmentService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class DownloadProjectAttachment extends HttpServlet {

    private ProjectLevelAttachmentService projectLevelAttachmentService;
    private TemplateRenderer templateRenderer;
    private ProjectManager projectManager;

    public DownloadProjectAttachment(ProjectLevelAttachmentService projectLevelAttachmentService,
                                     TemplateRenderer templateRenderer,
                                     ProjectManager projectManager){
        this.projectLevelAttachmentService = projectLevelAttachmentService;
        this.templateRenderer = templateRenderer;
        this.projectManager = projectManager;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if(ComponentAccessor.getJiraAuthenticationContext().getUser() == null){
            String redirectPath = URLEncoder.encode(req.getServletPath() + "?" + req.getQueryString(), "UTF-8");
            resp.sendRedirect(ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/login.jsp" + "?permissionViolation=true&os_destination=" + redirectPath);
            return;
        }
        Map<String, Object> context = new HashMap();
        resp.setContentType("text/html;charset=utf-8");
        ProjectAttachment projectAttachment = null;
        String attachmentId = req.getParameter("attachmentId");

        context.put("project", projectManager.getProjectByCurrentKey(req.getParameter("project")));
        context.put("baseUrl", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));

        if(attachmentId == null || attachmentId.equals("")){
            context.put("error", "This attachment does not exist.");
            templateRenderer.render("/templates/action/project-attachments-action/project-attachments-action-error.vm", context, resp.getWriter());
        }
        projectAttachment = projectLevelAttachmentService.getProjectAttachment(Integer.parseInt(attachmentId));
        if(projectAttachment == null){
            context.put("error", "Project attachment does not exist.");
            templateRenderer.render("/templates/action/project-attachments-action/project-attachments-action-error.vm", context, resp.getWriter());
        }
        else {
            File file = new File(projectAttachment.getFileLocation());
            if (!file.exists()) {
                context.put("error", "File could not be found.");
                templateRenderer.render("/templates/action/project-attachments-action/project-attachments-action-error.vm", context, resp.getWriter());
            }

            ServletContext ctx = getServletContext();
            InputStream fis = new FileInputStream(file);
            String mimeType = ctx.getMimeType(file.getAbsolutePath());
            resp.setContentType(mimeType != null ? mimeType : "application/octet-stream");
            resp.setContentLength((int) file.length());
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

            ServletOutputStream os = resp.getOutputStream();
            byte[] bufferData = new byte[1024];
            int read = 0;
            while ((read = fis.read(bufferData)) != -1) {
                os.write(bufferData, 0, read);
            }
            os.flush();
            os.close();
            fis.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

}
