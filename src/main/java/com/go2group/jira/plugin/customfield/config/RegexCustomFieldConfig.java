package com.go2group.jira.plugin.customfield.config;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.go2group.jira.plugin.ao.RegexAoService;
import com.go2group.jira.plugin.ao.RegexEntity;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 4/18/14
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegexCustomFieldConfig implements FieldConfigItemType {
    private static Logger log = Logger.getLogger(UniqueSequenceConfig.class);

    private final RegexAoService regexService;

    public RegexCustomFieldConfig(RegexAoService regexService) {
        this.regexService = regexService;
    }

    @Override
    public String getBaseEditUrl() {
        return "configureRegexCustomField.jspa";
    }

    @Override
    public Object getConfigurationObject(Issue issue, FieldConfig fieldConfig) {

        RegexEntity configuredRegex = regexService.getRegularExpressionByCf(fieldConfig.getCustomField().getIdAsLong());

        if (configuredRegex != null){
            return configuredRegex.getRegex();
        }else{
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Regular Expression";
    }

    @Override
    public String getDisplayNameKey() {
        return "Regular Expression";
    }

    @Override
    public String getObjectKey() {
        return "regex";
    }

    @Override
    public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem) {

        RegexEntity regexEntity = regexService.getRegularExpressionByCf(fieldConfig.getCustomField().getIdAsLong());

        StringBuffer viewhtml = new StringBuffer();

        if (regexEntity == null){
            viewhtml.append("<br><br><span> No regular expression configured </span>");
        }else{
            viewhtml.append("<br><br><span> Configured Regular Expression : <b>"+regexEntity.getRegex()+"</b></span>");
        }

        return viewhtml.toString();

    }
}
