package com.go2group.jira.plugin.servlet.project.attachment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.JiraProperties;
import com.atlassian.jira.config.properties.JiraSystemProperties;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.go2group.jira.plugin.ao.service.ProjectLevelAttachmentService;
import com.go2group.jira.plugin.util.JiraHomeUtil;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 29/05/14
 * Time: 6:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadProjectAttachment extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger(UploadProjectAttachment.class);

    private TemplateRenderer templateRenderer;
    private ProjectManager projectManager;
    private JiraAuthenticationContext jiraAuthenticationContext;
    private ProjectLevelAttachmentService projectLevelAttachmentService;
    private final JiraProperties jiraSystemProperties;
    private final FastDateFormat TMP_FOLDER_FORMATTER;
    private final String FS;
    private final String DIR_NAME;
    private AvatarService avatarService;

    public UploadProjectAttachment(TemplateRenderer templateRenderer,
                                   ProjectManager projectManager,
                                   ProjectLevelAttachmentService projectLevelAttachmentService,
                                   AvatarService avatarService){
        this.templateRenderer = templateRenderer;
        this.projectManager = projectManager;
        this.jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        this.projectLevelAttachmentService = projectLevelAttachmentService;
        this.avatarService = avatarService;
        this.jiraSystemProperties = JiraSystemProperties.getInstance();
        this.TMP_FOLDER_FORMATTER = FastDateFormat.getInstance("yyyyMMddhhmmssSSS");
        this.FS = jiraSystemProperties.getProperty("file.separator");
        this.DIR_NAME = TMP_FOLDER_FORMATTER.format(new Date());
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
        context.put("project", projectManager.getProjectByCurrentKey(req.getParameter("project")));
        context.put("baseUrl", ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL));
        context.put("avatarService", avatarService);
        templateRenderer.render("/templates/action/project-attachments-action/project-attachments-action-edit.vm", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Boolean errorExists = false;
        String redirectUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/plugins/servlet/uploadProjectAttachments?project=" +projectManager.getProjectByCurrentKey(req.getParameter("project")).getKey();
        Map<String, Object> context = new HashMap();
        resp.setContentType("text/html;charset=utf-8");
        Project project = projectManager.getProjectByCurrentKey(req.getParameter("project"));
        context.put("project", project);
        context.put("redirectUrl", redirectUrl);
        try {
            //Initialize all required fields
            String comments = new String();
            String uploadedBy = new String();
            String fileName = new String();
            String mimeType = new String();
            Long fileSize = null;
            Date uploadedOn = new Date();
            File uploadedFile = null;

            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
            for (FileItem item : items) {
                if(item.isFormField()){
                    if(item.getFieldName().equals("comments"))
                        comments = item.getString();
                }
                if (!item.isFormField()){
                    // Process form file field (input type="file").
                    InputStream filecontent = item.getInputStream();
                    uploadedBy = jiraAuthenticationContext.getUser().getName();
                    uploadedOn = new Date();
                    fileName = item.getName();
                    /* JUP-70 - Start*/
                    /*
                     * We need just the file name so fetching the name without back slashes.
                     */
                    if(fileName != null){
                    	if(fileName.indexOf("\\") >= 0 ){
                    		fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                    	}else if(fileName.indexOf("/") >= 0 ){
                        	fileName = fileName.substring(fileName.lastIndexOf("/"));
                        }
                    }
                    /* JUP-70 - end */
                    mimeType = item.getContentType();
                    fileSize = item.getSize();
                    uploadedFile = getFile(project, fileName);
                    item.write(uploadedFile);
                }
            }

            try{
                //Validate
                validateAttachmentData(comments, uploadedBy, fileName, mimeType, fileSize, uploadedFile);
                //Save
                projectLevelAttachmentService.createProjectAttachment(project.getKey(), uploadedBy, uploadedOn, comments, fileName, mimeType, fileSize, uploadedFile.getPath());
            }
            catch(FileUploadValidationException exc){
                exc.printStackTrace();
                context.put("error", exc.getMessage());
                errorExists = true;
            }
            catch(Exception exc){
                exc.printStackTrace();
                context.put("error", exc.getMessage());
                errorExists = true;
            }
            //Save attachment

        } catch (FileUploadException exc) {
            exc.printStackTrace();
            context.put("error", "Cannot parse multipart request. Please contact your administrator.");
            errorExists = true;
        } catch(IOException exc){
            exc.printStackTrace();
            errorExists = true;
            context.put("error", "Unable to create attachment. Please check if you have selected a file. Please contact your administrator.");
        } catch(Exception exc){
            exc.printStackTrace();
            errorExists = true;
            context.put("error", "Unknown exception creating attachment. Please contact your administrator.");
        }

        if(errorExists)
            templateRenderer.render("/templates/action/project-attachments-action/project-attachments-action-error.vm", context, resp.getWriter());
        else
            resp.sendRedirect(req.getContextPath() + "/browse/" + project.getKey() + "?selectedTab=com.go2group.jira.plugin.jira-utilities:project-level-attachments-tab-panel");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> context = new HashMap();
        resp.setContentType("text/html;charset=utf-8");

        templateRenderer.render("/templates/action/project-attachments-action/project-attachments-action-error.vm", context, resp.getWriter());
    }

    private File getFile(Project project, String fileName) throws IOException{
        String DIRECTORY_PATH = JiraHomeUtil.getJiraHomeDirectory() + FS + "project_attachments" + FS + project.getKey() + FS + DIR_NAME;
        String FILE_PATH = DIRECTORY_PATH + FS + fileName;
        File directory = new File(DIRECTORY_PATH);
        if(!directory.exists())
            directory.mkdirs();
        File file = new File(FILE_PATH);
        if(!file.exists()){
            file.createNewFile();
        }
        return file;
    }

    public void validateAttachmentData(String comments,
                                       String uploadedBy,
                                       String fileName,
                                       String mimeType,
                                       Long fileSize,
                                       File uploadedFile) throws FileUploadValidationException{

        if(fileSize > new Long(ComponentAccessor.getApplicationProperties().getDefaultBackedString(APKeys.JIRA_ATTACHMENT_SIZE)))
            throw new FileUploadValidationException("File size exceeds to allowed limit.");

    }
}

class FileUploadValidationException extends Exception{
    private String message;

    public FileUploadValidationException(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}