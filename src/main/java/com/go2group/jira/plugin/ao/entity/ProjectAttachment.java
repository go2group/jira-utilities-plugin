package com.go2group.jira.plugin.ao.entity;

import net.java.ao.Entity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 26/05/14
 * Time: 6:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ProjectAttachment extends Entity {

    @AutoIncrement
    @NotNull
    @PrimaryKey("ID")
    public int getID();

    String getProjectKey();

    void setProjectKey();

    String getUploadedBy();

    void setUploadedBy(String uploadedBy);

    Date getUploadedOn();

    void setUploadedOn(Date uploadedOn);

    String getComments();

    void setComments(String comments);

    String getFileName();

    void setFileName(String fileName);

    String getMimeType();

    void setMimeType(String mimeType);

    Long getFileSize();

    void setFileSize(Long fileSize);

    String getFileLocation();

    void setFileLocation(String fileLocation);
}
