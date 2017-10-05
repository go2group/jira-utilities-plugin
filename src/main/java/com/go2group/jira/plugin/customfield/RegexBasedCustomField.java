package com.go2group.jira.plugin.customfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.web.bean.BulkEditBean;
import com.go2group.jira.plugin.ao.RegexAoService;
import com.go2group.jira.plugin.ao.RegexEntity;
import com.go2group.jira.plugin.customfield.config.RegexCustomFieldConfig;

public class RegexBasedCustomField extends GenericTextCFType {
    private static final Logger log = LoggerFactory.getLogger(RegexBasedCustomField.class);

    private final RegexAoService regexService;

    private Pattern pattern;
    private Matcher matcher;

    protected RegexBasedCustomField(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager,
                                    RegexAoService regexService) {
        super(customFieldValuePersister, genericConfigManager);
        this.regexService = regexService;
    }

    // Read only - not editable
    public String availableForBulkEdit(BulkEditBean bulkEditBean) {
        return "bulk.edit.unavailable";
    }

    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes() {
        List<FieldConfigItemType> configurationItemTypes = new ArrayList<FieldConfigItemType>();
        configurationItemTypes.add(new RegexCustomFieldConfig(regexService));
        return configurationItemTypes;
    }

    @Override
    public Map<String, Object> getVelocityParameters(final Issue issue,
                                                     final CustomField field,
                                                     final FieldLayoutItem fieldLayoutItem) {
        final Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);

        // This method is also called to get the default value, in
        // which case issue is null so we can't use it to add currencyLocale
        if (issue == null) {
            return map;
        }

        FieldConfig fieldConfig = field.getRelevantConfig(issue);
        //add what you need to the map here

        String value = (String)issue.getCustomFieldValue(field);
        RegexEntity regexEntity = regexService.getRegularExpressionByCf(field.getIdAsLong());
        if(regexEntity != null){
	        String REGEX = regexEntity.getRegex();
	        if(REGEX !=null && REGEX != ""){
	            pattern = Pattern.compile(REGEX);
	            if(value !=null && value != ""){
	                matcher = pattern.matcher(value);
	                if(matcher.matches())
	                    map.put("REGEX_CHECK","PASS");
	                else
	                    map.put("REGEX_CHECK", "FAIL");
	            }
	        }
        }

        return map;
    }
}