package com.go2group.jira.plugin.customfield.config;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;

public class CSITConfig implements FieldConfigItemType {

    @Override
    public String getBaseEditUrl() {
        return "ConfigureCSIT!default.jspa";
    }

    @Override
    public Object getConfigurationObject(Issue arg0, FieldConfig arg1) {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Options";
    }

    @Override
    public String getDisplayNameKey() {
        return "Options";
    }

    @Override
    public String getObjectKey() {
        return "configureoption";
    }

    @Override
    public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem) {
    	return "";
    }

}
