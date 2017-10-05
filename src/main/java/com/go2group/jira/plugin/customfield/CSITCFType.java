package com.go2group.jira.plugin.customfield;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.go2group.jira.plugin.ao.CSITService;
import com.go2group.jira.plugin.customfield.config.CSITConfig;

public class CSITCFType extends GenericTextCFType{

    private static Logger log = LoggerFactory.getLogger(CSITCFType.class);
    private final CSITService csitService;

    protected CSITCFType(CustomFieldValuePersister customFieldValuePersister,
                         GenericConfigManager genericConfigManager, CSITService csitService) {
        super(customFieldValuePersister, genericConfigManager);
        this.csitService = csitService;
    }

    @Override
    public Map<String, Object> getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem) {
        Map<String, Object> velocityParams = new HashMap<String, Object>(super.getVelocityParameters(issue, field, fieldLayoutItem));

        log.debug("Velocity Parameters Invoked...");

        //Split and set the values if present
        //This is going to be a text field, so no worries it is a String for sure
        String value = (String)issue.getCustomFieldValue(field); //Its going to be Text Field extension, so String

        if (value != null && value.trim().length() > 0){
            String[] valueSplit = value.split(";");

            if (valueSplit.length == 2){
                velocityParams.put("option1value", valueSplit[0].trim());
                velocityParams.put("option2value", valueSplit[1].trim());
            }else if(valueSplit.length == 1){
                velocityParams.put("option1value", valueSplit[0].trim());
            }
        }

        //This is for the cascading select
        //issuetype is not available when invoked from IssueNavigator -- And below code not needed there as well
        if (issue.getIssueTypeObject() != null){
            velocityParams.put("optionsMap", csitService.getOptions(field.getIdAsLong(), issue.getIssueTypeObject().getId()));
        }
        return velocityParams;
    }

    @Override
    public String getChangelogString(CustomField field, String value) {
        String changelogString = super.getChangelogString(field, value);

        if (changelogString != null){
            changelogString = changelogString.replaceAll(";", "-");
        }
        return changelogString;
    }

    @Override
    public String getChangelogValue(CustomField field, String value) {
        String changelogValue = super.getChangelogValue(field, value);

        if (changelogValue != null){
            changelogValue = changelogValue.replaceAll(";", "-");
        }

        return changelogValue;
    }

    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes() {
        List<FieldConfigItemType> configurationItemTypes = new ArrayList<FieldConfigItemType>();
        configurationItemTypes.add(new CSITConfig());
        return configurationItemTypes;
    }

}
