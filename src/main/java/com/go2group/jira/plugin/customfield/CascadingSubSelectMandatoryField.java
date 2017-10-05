package com.go2group.jira.plugin.customfield;

import java.util.Collection;

import javax.annotation.Nullable;

import com.atlassian.jira.issue.customfields.impl.CascadingSelectCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.OptionUtils;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.ObjectUtils;

/**
 * author: parthi
 */
public class CascadingSubSelectMandatoryField extends CascadingSelectCFType {


    private final OptionsManager optionsManager;

    public CascadingSubSelectMandatoryField(OptionsManager optionsManager, CustomFieldValuePersister customFieldValuePersister,
                              GenericConfigManager genericConfigManager, JiraBaseUrls jiraBaseUrls) {
        super(optionsManager, customFieldValuePersister, genericConfigManager, jiraBaseUrls);
        this.optionsManager = optionsManager;
    }

    @Override
    public void validateFromParams(CustomFieldParams relevantParams, ErrorCollection errorCollectionToAddTo, FieldConfig config) {

        if (relevantParams == null || relevantParams.isEmpty()) {
            return;
        }

        String customFieldId = config.getCustomField().getId();
        Option parentOption;
        try {
            // Get the parent option
            parentOption = extractOptionFromParams(PARENT_KEY, relevantParams);
        } catch (FieldValidationException e) {
            parentOption = null;
        }
        if (parentOption != null) {
            Collection valuesForChild = relevantParams.getValuesForKey(CHILD_KEY);

            if(valuesForChild==null){
            	
            	String cfName = config.getCustomField().getName();
            	
            	if (cfName.indexOf("/") != -1){
            		//Indicates the naming follows slash pattern
            		cfName = cfName.split("/")[1];
            	}else{
            		//Otherwise use the field name followed by Field 2 to indicate the child option
            		cfName = cfName + " sub-select";
            	}
            	
                errorCollectionToAddTo.addError(customFieldId,cfName + " is required", ErrorCollection.Reason.VALIDATION_FAILED);
                
                return;
            }
        } else {
            super.validateFromParams(relevantParams, errorCollectionToAddTo, config);
            return;
        }
    }


    @Nullable
    private Option extractOptionFromParams(String key, CustomFieldParams relevantParams) throws FieldValidationException {
        Collection<String> params = relevantParams.getValuesForKey(key);
        if (params != null && !params.isEmpty()) {
            String selectValue = params.iterator().next();
            if (ObjectUtils.isValueSelected(selectValue) && selectValue != null) {
                return getOptionFromStringValue(selectValue);
            }
        }

        return null;
    }

    @Nullable
    private Option getOptionFromStringValue(String selectValue) throws FieldValidationException {
        final Long aLong = OptionUtils.safeParseLong(selectValue);
        if (aLong != null) {
            final Option option = optionsManager.findByOptionId(aLong);
            if (option != null) {
                return option;
            } else {
                throw new FieldValidationException("'" + aLong + "' is an invalid Option");
            }
        } else {
            throw new FieldValidationException("Value: '" + selectValue + "' is an invalid Option");
        }
    }
}
