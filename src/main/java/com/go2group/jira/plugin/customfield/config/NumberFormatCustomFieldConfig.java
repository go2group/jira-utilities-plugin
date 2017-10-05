package com.go2group.jira.plugin.customfield.config;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.go2group.jira.plugin.ao.NumberFormatAoService;
import com.go2group.jira.plugin.ao.NumberFormatEntity;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/5/14
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberFormatCustomFieldConfig implements FieldConfigItemType {
    private static Logger log = Logger.getLogger(UniqueSequenceConfig.class);

    private final NumberFormatAoService numberFormatAoService;

    public NumberFormatCustomFieldConfig(NumberFormatAoService numberFormatAoService) {
        this.numberFormatAoService = numberFormatAoService;
    }

    @Override
    public String getBaseEditUrl() {
        return "configureNumberFormatCustomField.jspa";
    }

    @Override
    public Object getConfigurationObject(Issue issue, FieldConfig fieldConfig) {

        NumberFormatEntity entity = numberFormatAoService.getNumberFormatEntity(fieldConfig.getId(), fieldConfig.getCustomField().getIdAsLong());

        if (entity != null){
            return entity;
        }else{
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return "Configured Format";
    }

    @Override
    public String getDisplayNameKey() {
        return "Configured Format";
    }

    @Override
    public String getObjectKey() {
        return "configuredNumberFormat";
    }

    @Override
    public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem) {

        NumberFormatEntity entity = numberFormatAoService.getNumberFormatEntity(fieldConfig.getId(), fieldConfig.getCustomField().getIdAsLong());

        StringBuffer viewhtml = new StringBuffer();

        if (entity == null){
            viewhtml.append("<br><br><span> No number format configured </span>");
        }else{
            viewhtml.append("<br><br><span> Configured Number Format Type : <b>"+entity.getType()+"</b></span>");
        }

        return viewhtml.toString();

   }
}