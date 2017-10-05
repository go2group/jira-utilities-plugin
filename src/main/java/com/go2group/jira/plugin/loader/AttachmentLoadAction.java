package com.go2group.jira.plugin.loader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.jira.web.util.AttachmentException;
import com.go2group.jira.plugin.util.JiraHomeUtil;

/**
 *
 * @author muralidharan [Go2Group Inc.]
 *
 */

public class AttachmentLoadAction extends JiraWebActionSupport{

    private static final long serialVersionUID = 1L;

    private String loadFileName;

    private String dateFormat;

    private String message;

    private AttachmentManager attachmentManager;

    private IssueManager issueManager;

    private UserUtil userUtil;

    private static Logger log = Logger.getLogger(AttachmentLoadAction.class);

    public AttachmentLoadAction(AttachmentManager attachmentManager, IssueManager issueManager,
                                UserUtil userUtil) {
        this.attachmentManager = attachmentManager;
        this.issueManager = issueManager;
        this.userUtil = userUtil;
    }

    @Override
    public String doDefault() throws Exception {

    	return SUCCESS;
    }

    @Override
    protected void doValidation() {
        if (loadFileName == null || loadFileName.trim().length() == 0){
            addErrorMessage("Load file name cannot be Empty/Blank");
        }

        if (dateFormat == null || dateFormat.trim().length() == 0){
            addErrorMessage("DateFormat cannot be Empty/Blank");
        }

    }

    @Override
    protected String doExecute() throws Exception {

        if (loadFileName != null){

            Map<String, List<String>> dataMap = new HashMap<String, List<String>>();

            AttachmentCSVReader reader = new AttachmentCSVReader();
            reader.doProcess(dataMap, loadFileName);

            String homeDir = JiraHomeUtil.getJiraHomeDirectory();

            int counter = 0;

            for (String issuekey : dataMap.keySet()){

                Issue issue = issueManager.getIssueObject(issuekey);

                if (issue == null){
                    log.warn("Invalid Issue Key received "+issuekey);
                    continue;
                }

                createAttachments(issue, dataMap.get(issuekey), homeDir);

                counter++;
            }

            setMessage("Execution Completed! No. of records successfully processed : "+counter);

        }else{
            //TODO test this - if this condition is reached
            log.debug("Invalid inputs received, skipping the process");
        }

        return SUCCESS;
    }

    private void createAttachments(Issue issue, List<String> attachments, String homeDir){

        log.info("Processing Attachments for Issue: "+issue.getKey());

        if (attachmentManager.attachmentsEnabled())
        {

            for (String attachment : attachments)
            {
                int firstIndex = attachment.indexOf(";");
                String dateStr = attachment.substring(0, firstIndex);
                attachment = attachment.substring(firstIndex+1);
                int secondIndex = attachment.indexOf(";");
                String author = attachment.substring(0, secondIndex);
                attachment = attachment.substring(secondIndex+1);

                //Take out the filename
                attachment = attachment.replaceAll("file://", "");

                Date date = new Date();

                try{
                    date = new SimpleDateFormat(dateFormat).parse(dateStr);
                }catch(Exception e){
                    log.warn("Unable to Parse the date "+dateStr+" for issue "+issue+".  Using the current date");
                }

                ApplicationUser user = userUtil.getUser(author);

                if (user == null){
                    log.warn("Unable to find the user "+author+" for issue "+issue+".  Using currently logged in user for creating comment");
                    author = getLoggedInUser().getName();
                }


                File attachmentFile = new File(homeDir + "/import/attachments/"+attachment);

                if (attachmentFile.exists() && attachmentFile.canRead())
                {
                    try
                    {
                        attachmentManager.createAttachmentCopySourceFile(attachmentFile, attachment, getMimetype(attachment), author, issue, Collections.EMPTY_MAP, date);

                    }
                    catch (AttachmentException e)
                    {
                        log.warn("Could not create attachment for issue " + issue.getKey() + " for file " + attachment, e);
                    }
                }
                else
                {
                    log.warn("Attachment not found or not readable. Could not create attachment for issue " + issue.getKey() + " for file " + attachment);
                }
            }
        }
    }

    private String getMimetype(String filename){
        if (filename.endsWith(".pdf") || filename.endsWith(".PDF")){
            return "application/pdf";
        }else if (filename.endsWith(".hqx") || filename.endsWith(".HQX")){
            return "application/mac-binhex40";
        }else if (filename.endsWith(".doc") || filename.endsWith(".DOC")){
            return "application/msword";
        }else if (filename.endsWith(".jpg") || filename.endsWith(".JPG")){
            return "image/jpeg";
        }else if (filename.endsWith(".zip") || filename.endsWith(".ZIP")){
            return "application/zip";
        }else if (filename.endsWith(".png") || filename.endsWith(".PNG")){
            return "image/png";
        }else if (filename.endsWith(".tif") || filename.endsWith(".TIF")
                || filename.endsWith(".tiff") || filename.endsWith(".TIFF")){
            return "image/tiff";
        }else if (filename.endsWith(".log") || filename.endsWith(".LOG")
                || filename.endsWith(".txt") || filename.endsWith(".TXT")){
            return "text/plain";
        }else if (filename.endsWith(".rtf") || filename.endsWith(".RTF")){
            return "text/rtf";
        }else if (filename.endsWith(".htm") || filename.endsWith(".HTM")
                || filename.endsWith(".html") || filename.endsWith(".HTML")){
            return "text/html";
        }else if (filename.endsWith(".docx") || filename.endsWith(".xlsx") ||
                filename.endsWith(".pptx") || filename.endsWith(".ppsx") ||
                filename.endsWith(".DOCX") || filename.endsWith(".XLSX") ||
                filename.endsWith(".PPTX") || filename.endsWith(".PPSX")){
            return "application/vnd.openxmlformats";
        }else if (filename.endsWith(".eps") || filename.endsWith(".EPS") ||
                filename.endsWith(".ps") || filename.endsWith(".PS") ||
                filename.endsWith(".ai") || filename.endsWith(".AI")){
            return "application/postscript";
        }else if (filename.endsWith(".mov") || filename.endsWith(".MOV")){
            return "video/quicktime";
        }else if (filename.endsWith(".swf") || filename.endsWith(".SWF")){
            return "application/x-shockwave-flash2-preview";
        }else if (filename.endsWith(".xml") || filename.endsWith(".XML")){
            return "text/xml";
        }else if (filename.endsWith(".avi") || filename.endsWith(".AVI")){
            return "video/x-msvideo";
        }else if (filename.endsWith(".ppt") || filename.endsWith(".PPT")){
            return "application/vnd.ms-powerpoint";
        }else if (filename.endsWith(".doc") || filename.endsWith(".DOC")){
            return "application/msword";
        }else if (filename.endsWith(".gz") || filename.endsWith(".GZ")
                || filename.endsWith(".tgz") || filename.endsWith(".TGZ")){
            return "application/x-gzip";
        }else if (filename.endsWith(".xls") || filename.endsWith(".XLS")
                || filename.endsWith(".csv") || filename.endsWith(".CSV")){
            return "application/vnd.ms-excel";
        }else if (filename.endsWith(".sit") || filename.endsWith(".SIT")){
            return "application/x-stuffit";
        }else if (filename.endsWith(".gif") || filename.endsWith(".GIF")){
            return "image/gif";
        }else if (filename.endsWith(".spl") || filename.endsWith(".SPL")){
            return "application/x-futuresplash";
        }else if (filename.endsWith(".sh") || filename.endsWith(".SH")){
            return "application/x-sh";
        }else if (filename.endsWith(".tar") || filename.endsWith(".TAR")){
            return "application/x-tar";
        }else if (filename.endsWith(".mpg") || filename.endsWith(".MPG")){
            return "video/mpeg";
        }else if (filename.endsWith(".vsd") || filename.endsWith(".VSD")){
            return "application/x-visio";
        }else if (filename.endsWith(".ppm") || filename.endsWith(".PPM")){
            return "image/x-portable-pixmap";
        }else{
            return "application/octet-stream";
        }
    }

    public String getJiraImportDirectory(){
        return JiraHomeUtil.getJiraImportDirectory();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLoadFileName() {
        return loadFileName;
    }

    public void setLoadFileName(String loadFileName) {
        this.loadFileName = loadFileName;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
