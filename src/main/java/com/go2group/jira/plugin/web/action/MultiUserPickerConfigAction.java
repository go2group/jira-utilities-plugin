package com.go2group.jira.plugin.web.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.go2group.jira.plugin.ao.MultiUserPickerService;
import com.go2group.jira.plugin.ao.MultiUsrPkrEntity;

public class MultiUserPickerConfigAction extends JiraWebActionSupport{

    private static Logger log = LoggerFactory.getLogger(MultiUserPickerConfigAction.class);

    private Long customFieldId;

    private Long fieldConfigId;

    private Long fieldConfigSchemeId;

    private String configGroups;

    private final MultiUserPickerService mupService;

    public MultiUserPickerConfigAction(MultiUserPickerService mupService) {
        this.mupService = mupService;
    }

    public String getConfigGroups() {
        return configGroups;
    }

    public void setConfigGroups(String configGroups) {
        this.configGroups = configGroups;
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

    @Override
    protected String doExecute() throws Exception {

        mupService.updateConfig("customfield_"+customFieldId, configGroups);

        return SUCCESS;
    }

    public String doDefault(){

        MultiUsrPkrEntity mupEntity = mupService.getConfig("customfield_"+customFieldId);

        if (mupEntity != null){
            setConfigGroups(mupEntity.getGroupConfig());
        }

        return SUCCESS;

    }
}
