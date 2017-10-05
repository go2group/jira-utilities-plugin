package com.go2group.jira.plugin.ao.service.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.go2group.jira.plugin.ao.PriorityDueDate;
import com.go2group.jira.plugin.ao.entity.ProjectAttachment;
import com.go2group.jira.plugin.ao.service.ProjectLevelAttachmentService;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 26/05/14
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProjectLevelAttachmentServiceImpl implements ProjectLevelAttachmentService {

    private static final Logger log = LoggerFactory.getLogger(ProjectLevelAttachmentServiceImpl.class);

    private final ActiveObjects activeObjects;

    public ProjectLevelAttachmentServiceImpl(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    public ProjectAttachment createProjectAttachment(String projectKey,
                                                     String uploadedBy,
                                                     Date uploadedOn,
                                                     String comments,
                                                     String fileName,
                                                     String mimeType,
                                                     Long fileSize,
                                                     String fileLocation){
        return activeObjects.create(ProjectAttachment.class,
                    new DBParam("PROJECT_KEY", projectKey), new DBParam("UPLOADED_BY", uploadedBy),
                new DBParam("UPLOADED_ON", uploadedOn),new DBParam("COMMENTS", comments),
                new DBParam("FILE_NAME", fileName),new DBParam("MIME_TYPE", mimeType),
                new DBParam("FILE_SIZE", fileSize),new DBParam("FILE_LOCATION", fileLocation));

    }

    public ProjectAttachment getProjectAttachment(int id){
        return activeObjects.get(ProjectAttachment.class, id);
    }

    public ArrayList<ProjectAttachment> getProjectAttachmentsFromProjectKey(String projectKey){
        ProjectAttachment[] projectAttachmentsEntities = activeObjects.find(ProjectAttachment.class,
                Query.select().where("PROJECT_KEY = ?", projectKey));
        return new ArrayList<ProjectAttachment>(Arrays.asList(projectAttachmentsEntities));
    }

    public void deleteProjectAttachment(int id) throws IOException {
        ProjectAttachment attachment = activeObjects.get(ProjectAttachment.class, id);
        new File(attachment.getFileLocation()).delete();
        activeObjects.delete(attachment);

    }
}
