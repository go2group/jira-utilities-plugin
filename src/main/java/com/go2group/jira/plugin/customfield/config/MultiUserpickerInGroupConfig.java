package com.go2group.jira.plugin.customfield.config;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.go2group.jira.plugin.ao.MultiUserPickerService;
import com.go2group.jira.plugin.ao.MultiUsrPkrEntity;

public class MultiUserpickerInGroupConfig implements FieldConfigItemType{

    private MultiUserPickerService multiUserPickerService;

    public MultiUserpickerInGroupConfig(MultiUserPickerService multiUserPickerService) {
        this.multiUserPickerService = multiUserPickerService;
    }

    @Override
    public String getBaseEditUrl() {
        return "ConfigureMultiUPInGroup!default.jspa";
    }

    @Override
    public Object getConfigurationObject(Issue arg0, FieldConfig arg1) {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Groups";
    }

    @Override
    public String getDisplayNameKey() {
        return "Groups";
    }

    @Override
    public String getObjectKey() {
        return "configuregroup";
    }

    @Override
    public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem) {

        MultiUsrPkrEntity entity = multiUserPickerService.getConfig(fieldConfig.getCustomField().getId());

        if(entity != null && entity.getGroupConfig() != null && entity.getGroupConfig().trim().length() > 0){
            return "<br><br><span><strong>"+entity.getGroupConfig()+"</strong></span>";

        }else{
            return "<br><br><span> No groups configured </span>";
        }
    }
}
