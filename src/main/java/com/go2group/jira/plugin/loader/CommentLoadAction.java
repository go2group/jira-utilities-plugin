package com.go2group.jira.plugin.loader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.go2group.jira.plugin.util.JiraHomeUtil;

/**
 *
 * @author muralidharan [Go2Group Inc.]
 *
 */

public class CommentLoadAction extends JiraWebActionSupport{

    private static final long serialVersionUID = 1L;

    private String loadFileName;

    private String dateFormat;

    private String message;

    private CommentManager commentManager;

    private IssueManager issueManager;

    private UserUtil userUtil;

    private static Logger log = Logger.getLogger(CommentLoadAction.class);

    public CommentLoadAction(CommentManager commentManager, IssueManager issueManager, UserUtil userUtil) {
        this.commentManager = commentManager;
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
            addErrorMessage("Date format cannot be Empty/Blank");
        }
    }

    @Override
    protected String doExecute() throws Exception {

        if (loadFileName != null){

            Map<String, List<String>> dataMap = new HashMap<String, List<String>>();

            CommentCSVReader reader = new CommentCSVReader();
            reader.doProcess(dataMap, loadFileName);

            int counter = 0;

            for (String issuekey : dataMap.keySet()){

                Issue issue = issueManager.getIssueObject(issuekey);

                if (issue == null){
                    log.warn("Invalid Issue Key received "+issuekey);
                    continue;
                }

                createComments(issue, dataMap.get(issuekey));

                counter++;
            }

            setMessage("Execution Completed! No. of records successfully processed : "+counter);

        }else{
            //TODO Check if this condition is reached
            log.debug("Invalid inputs received, skipping the process");
        }

        return SUCCESS;
    }

    private void createComments(Issue issue, List<String> comments){

        log.info("Processing Comments for Issue: "+issue.getKey());

        for (String comment : comments)
        {
            int firstIndex = comment.indexOf(";");
            String dateStr = comment.substring(0, firstIndex);
            comment = comment.substring(firstIndex+1);
            int secondIndex = comment.indexOf(";");
            String author = comment.substring(0, secondIndex);
            comment = comment.substring(secondIndex+1);

            Date date = new Date();

            try{
                date = new SimpleDateFormat(dateFormat).parse(dateStr);
            }catch(Exception e){
                log.warn("Unable to Parse the date "+dateStr+" for issue "+issue+".  Using the current date");
            }

            ApplicationUser user = userUtil.getUserObject(author);

            if (user == null){
                log.warn("Unable to find the user "+author+" for issue "+issue+".  Using currently logged in user for creating comment");
                author = getLoggedInUser().getName();
            }

            commentManager.create(issue, author, comment, null, null, date, false);
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
