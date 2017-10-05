package com.go2group.jira.plugin.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.go2group.jira.plugin.ao.NumberFormatAoService;
import com.go2group.jira.plugin.ao.NumberFormatEntity;
import com.go2group.jira.plugin.ao.NumberFormatType;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/5/14
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureNumberFormatCustomField extends JiraWebActionSupport {

    private static Logger log = LoggerFactory.getLogger(ConfigureRegexCustomField.class);

    private Long customFieldId;

    private Long fieldConfigId;

    private Long fieldConfigSchemeId;

    private NumberFormatEntity entity;

    private String type;

    private String format;

    private Double lowerLimit;

    private Double higherLimit;

    private String initialized;

    private SearchService searchService;

    private final NumberFormatAoService numberFormatAoService;

    private CustomFieldManager customfieldManager;

    private IssueManager issueManager;

    public ConfigureNumberFormatCustomField(NumberFormatAoService numberFormatAoService, SearchService searchService,
                                     CustomFieldManager customfieldManager, IssueManager issueManager) {
        this.searchService = searchService;
        this.numberFormatAoService = numberFormatAoService;
        this.customfieldManager = customfieldManager;
        this.issueManager = issueManager;
    }

    public NumberFormatEntity getEntity() {
        return entity;
    }

    public void setEntity(NumberFormatEntity entity) {
        this.entity = entity;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Double getHigherLimit() {
        return higherLimit;
    }

    public void setHigherLimit(Double higherLimit) {
        this.higherLimit = higherLimit;
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
        NumberFormatEntity entity = numberFormatAoService.getNumberFormatEntity(fieldConfigId, customFieldId);

        if(entity == null){
            return INPUT;
        }else{
            setEntity(entity);
            return INPUT;
        }
    }

    public String doConfig(){

        NumberFormatEntity entity = numberFormatAoService.getNumberFormatEntity(fieldConfigId, customFieldId);
        setEntity(entity);

        //Perform validation
        if(getType().equals(NumberFormatType.DECIMAL.getType()) && ( getFormat() == null || getFormat().equals(""))){
            addErrorMessage("Format is required for decimal type. Please enter a format and try again.");
            return INPUT;
        }
        if(getType().equals(NumberFormatType.RANGE.getType()) && ( getLowerLimit() == null || getLowerLimit().equals("") || getHigherLimit() == null || getHigherLimit().equals(""))){
            addErrorMessage("Lower limit and higher limit is required for range type. Please enter a lower limit and higher limit and try again.");
            return INPUT;
        }


        if(entity == null){
            entity = numberFormatAoService.saveNumberFormat(fieldConfigId, customFieldId, NumberFormatType.valueOf(getType()), getFormat(), getLowerLimit(), getHigherLimit());

        }else{
            numberFormatAoService.updateNumberFormatEntity(entity, NumberFormatType.valueOf(getType()), getFormat(), getLowerLimit(), getHigherLimit());
        }

        log.debug("Configured Regular Expression Successfully : for customfieldId "+entity.getCustomFieldId());

        return getRedirect(getReturnUrl());
    }

}