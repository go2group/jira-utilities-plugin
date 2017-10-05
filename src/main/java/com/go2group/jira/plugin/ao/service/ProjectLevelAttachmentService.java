package com.go2group.jira.plugin.ao.service;

import com.go2group.jira.plugin.ao.entity.ProjectAttachment;

import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 26/05/14
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProjectLevelAttachmentService {
    ProjectAttachment createProjectAttachment(String projectKey, String uploadedBy, Date uploadedOn, String comments, String fileName, String mimeType, Long fileSize, String fileLocation);

    public ProjectAttachment getProjectAttachment(int id);

    public ArrayList<ProjectAttachment> getProjectAttachmentsFromProjectKey(String projectKey);

    void deleteProjectAttachment(int id) throws IOException;

}
