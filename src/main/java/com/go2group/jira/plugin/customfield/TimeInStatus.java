package com.go2group.jira.plugin.customfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.config.item.SettableOptionsConfigItem;
import com.atlassian.jira.issue.customfields.impl.ReadOnlyCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.security.JiraAuthenticationContext;

public class TimeInStatus extends ReadOnlyCFType
{
    private ConstantsManager constantsManager;
    private OptionsManager optionsManager;

    public TimeInStatus(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager, 
    		ConstantsManager constantsManager, OptionsManager optionsManager, 
    		TextFieldCharacterLengthValidator textFieldCharacterLengthValidator, JiraAuthenticationContext authenticationContext)
    {
        super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator, authenticationContext);
        this.constantsManager = constantsManager;
        this.optionsManager = optionsManager;
    }

    // Configuration options for the custom field

    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes()
    {
        // Add the "Edit Options" link on the custom fields Configure screen
        
        List<FieldConfigItemType> configurationItemTypes = new ArrayList<FieldConfigItemType>();
        configurationItemTypes.add(new ApprovalsCustomFieldConfigItem(this, optionsManager));
        return configurationItemTypes;
    }
    
    public class ApprovalsCustomFieldConfigItem extends SettableOptionsConfigItem
    {
        public ApprovalsCustomFieldConfigItem(CustomFieldType<String, String> customFieldType, OptionsManager optionsManager)
        {
            super(customFieldType, optionsManager);
        }

        public String getBaseEditUrl()
        {
            return "TimeInStatusConfigureAction.jspa";
        }

        public Object getConfigurationObject(Issue issue, FieldConfig config)
        {
            return genericConfigManager.retrieve("options", config.getId().toString());
        }

        public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem)
        {
            Map<String, String> values = (Map<String, String>) getConfigurationObject(null, fieldConfig);
            if (values == null)
                return "";
            
            String statusId = values.get("status");
            if (statusId == null || statusId.length() == 0)
                return "Status not set";
            
            return "Status = " + constantsManager.getStatusObject(statusId).getName();
        }
    }
}
