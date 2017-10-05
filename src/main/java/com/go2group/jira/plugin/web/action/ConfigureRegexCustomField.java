package com.go2group.jira.plugin.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.go2group.jira.plugin.ao.RegexAoService;
import com.go2group.jira.plugin.ao.RegexEntity;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 4/18/14
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureRegexCustomField extends JiraWebActionSupport {

    private static Logger log = LoggerFactory.getLogger(ConfigureRegexCustomField.class);

    private Long customFieldId;

    private Long fieldConfigId;

    private Long fieldConfigSchemeId;

    private String configRegex;

    private String initialized;

    private SearchService searchService;

    private final RegexAoService regexService;

    private CustomFieldManager customfieldManager;

    private IssueManager issueManager;

    public ConfigureRegexCustomField(RegexAoService regexService, SearchService searchService,
                        CustomFieldManager customfieldManager, IssueManager issueManager) {
        this.searchService = searchService;
        this.regexService = regexService;
        this.customfieldManager = customfieldManager;
        this.issueManager = issueManager;
    }

    public String getConfigRegex() {
        return configRegex;
    }

    public void setConfigRegex(String configRegex) {
        this.configRegex = configRegex;
    }

    public Long getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public Long getFieldConfigId() {
        return fieldConfigId;
    }

    public void setFieldConfigId(Long fieldConfigId) {
        this.fieldConfigId = fieldConfigId;
    }

    public Long getFieldConfigSchemeId() {
        return fieldConfigSchemeId;
    }

    public void setFieldConfigSchemeId(Long fieldConfigSchemeId) {
        this.fieldConfigSchemeId = fieldConfigSchemeId;
    }

    public String getInitialized() {
        return initialized;
    }

    public void setInitialized(String initialized) {
        this.initialized = initialized;
    }

    @Override
    protected String doExecute() throws Exception {
        RegexEntity regexEntity = regexService.getRegularExpressionByCf(customFieldId);

        if(regexEntity == null){
            return INPUT;
        }else{
            setConfigRegex(regexEntity.getRegex());
            return INPUT;
        }
    }

    public String doConfig(){
        RegexEntity regexEntity = regexService.getRegularExpressionByCf(customFieldId);

        if(regexEntity == null){
            regexEntity = regexService.createRegularExpression(customFieldId, configRegex);

        }else{
            regexService.updateRegularExpression(customFieldId, configRegex);
        }

        log.debug("Configured Regular Expression Successfully : "+regexEntity.getRegex()+" for customfieldId "+regexEntity.getCustomFieldId());

        return getRedirect(getReturnUrl());

    }

}