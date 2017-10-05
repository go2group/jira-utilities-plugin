package com.go2group.jira.plugin.web.action;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.go2group.jira.plugin.ao.CSITEntity;
import com.go2group.jira.plugin.ao.CSITService;
import com.go2group.jira.plugin.ao.DuplicateEntityException;

public class ConfigureCSIT extends JiraWebActionSupport{

    private static Logger log = LoggerFactory.getLogger(ConfigureCSIT.class);

    private static final String CONFIGURE = "configure";
    private static final String VIEW1 = "view1";
    private static final String VIEW2 = "view2";

    private final ConstantsManager constantsManager;

    private final CSITService csitService;

    private String issuetype;

    private String optLvl1;

    private String optLvl2;

    private Long customFieldId;

    private String returnUrl;

    public ConfigureCSIT(ConstantsManager constantsManager, CSITService csitService) {
        this.constantsManager = constantsManager;
        this.csitService = csitService;
    }

    @Override
    public String doDefault() throws Exception {
    	return CONFIGURE;
    }

    public String doViewOptLvl1(){
        return VIEW1;
    }

    public String doViewOptLvl2(){
        return VIEW2;
    }

    public String doAddOptLvl1(){

        log.debug("Adding Option Level 1 with value "+optLvl1+" for issuetype "+issuetype);

        CSITEntity option;
        try {
            option = csitService.add(customFieldId, issuetype, optLvl1);
            log.debug("Created Option Level 1 with ID : "+option.getID());
        } catch (DuplicateEntityException e) {
            log.debug(e.toString());
            addErrorMessage("Duplicate Entry, Option already present");
        }

        return VIEW1;
    }

    public String doAddOptLvl2(){

        log.debug("Adding Option Level 2 with value "+optLvl2+" for issuetype "+issuetype + " and subtype 1"+optLvl1);

        CSITEntity option;
        try {
            option = csitService.add(customFieldId, issuetype, optLvl1, optLvl2);
            log.debug("Created Option Level 2 with ID : "+option.getID());
        } catch (DuplicateEntityException e) {
            log.debug(e.toString());
            addErrorMessage("Duplicate Entry, Option already present");
        }

        return VIEW2;
    }

    public String doDeleteOptLvl1(){
        csitService.delete(customFieldId, issuetype, optLvl1);
        return VIEW1;
    }

    public String doDeleteOptLvl2(){
        csitService.delete(customFieldId, issuetype, optLvl1, optLvl2);
        return VIEW2;
    }

    public Set<String> getOptionsLevel1(){
        return csitService.getOptionsLevel1(customFieldId, issuetype);
    }

    public Set<String> getOptionsLevel2(){
        return csitService.getOptionsLevel2(customFieldId, issuetype, optLvl1);
    }

    public Collection<IssueType> getIssuetypes(){
        return constantsManager.getRegularIssueTypeObjects();
    }

    public IssueType getIssuetypeObject(String id){
        return constantsManager.getIssueTypeObject(id);
    }

    public String getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(String issuetype) {
        this.issuetype = issuetype;
    }

    public String getOptLvl1() {
        return optLvl1;
    }

    public void setOptLvl1(String subtype1) {
        this.optLvl1 = subtype1;
    }

    public String getOptLvl2() {
        return optLvl2;
    }

    public void setOptLvl2(String subtype2) {
        this.optLvl2 = subtype2;
    }

    public Long getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
}
