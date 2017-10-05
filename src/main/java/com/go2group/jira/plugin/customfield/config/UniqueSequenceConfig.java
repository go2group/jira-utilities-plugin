package com.go2group.jira.plugin.customfield.config;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.go2group.jira.plugin.ao.UniqueSequenceKey;
import com.go2group.jira.plugin.ao.UskAoService;

public class UniqueSequenceConfig implements FieldConfigItemType{

    private static Logger log = Logger.getLogger(UniqueSequenceConfig.class);

    private final UskAoService uskService;

    public UniqueSequenceConfig(UskAoService uskService) {
        this.uskService = uskService;
    }

    @Override
    public String getBaseEditUrl() {
        return "configureUSK.jspa";
    }

    @Override
    public Object getConfigurationObject(Issue issue, FieldConfig fieldConfig) {

        UniqueSequenceKey configuredKey = uskService.getUniqueSequenceKeyByCf(fieldConfig.getCustomField().getIdAsLong());

        if (configuredKey != null){
            return configuredKey.getKey();
        }else{
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Sequence Key";
    }

    @Override
    public String getDisplayNameKey() {
        return "Sequence Key";
    }

    @Override
    public String getObjectKey() {
        return "sequence";
    }

    @Override
    public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem) {

        UniqueSequenceKey keyConfig = uskService.getUniqueSequenceKeyByCf(fieldConfig.getCustomField().getIdAsLong());

        StringBuffer viewhtml = new StringBuffer();

        if (keyConfig == null){
            viewhtml.append("<br><br><span> No Unique Sequence Key configured </span>");
        }else{
            viewhtml.append("<br><br><span> Configured Unique Sequence Key : <b>"+keyConfig.getKey()+"</b></span>");
        }

        return viewhtml.toString();

    }
}
