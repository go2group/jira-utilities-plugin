package com.go2group.jira.plugin.customfield;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.web.bean.BulkEditBean;
import com.go2group.jira.plugin.ao.NumberFormatAoService;
import com.go2group.jira.plugin.ao.NumberFormatEntity;
import com.go2group.jira.plugin.ao.NumberFormatType;
import com.go2group.jira.plugin.customfield.config.NumberFormatCustomFieldConfig;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/5/14
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurableNumberFieldCustomField extends NumberCFType {
    private static final Logger log = LoggerFactory.getLogger(RegexBasedCustomField.class);

    private final NumberFormatAoService numberFormatAoService;


    protected ConfigurableNumberFieldCustomField(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager,
                                                 NumberFormatAoService numberFormatAoService, DoubleConverter doubleConverter) {
        super(customFieldValuePersister, doubleConverter, genericConfigManager);
        this.numberFormatAoService = numberFormatAoService;
    }

    // Read only - not editable
    public String availableForBulkEdit(BulkEditBean bulkEditBean) {
        return "bulk.edit.unavailable";
    }

    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes() {
        List<FieldConfigItemType> configurationItemTypes = new ArrayList<FieldConfigItemType>();
        configurationItemTypes.add(new NumberFormatCustomFieldConfig(numberFormatAoService));
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

        Object valueObject = issue.getCustomFieldValue(field);
        if(valueObject != null){
            Double value = (Double)valueObject;
            NumberFormatEntity entity = numberFormatAoService.getNumberFormatEntity(fieldConfig.getId(), field.getIdAsLong());
            //Whole number
            if(entity.getType().equals(NumberFormatType.WHOLE.getType())){
                if(!isWholeNumber(value)){
                    map.put("error", "Value is not a whole number.");
                }
            }
            //Range
            if(entity.getType().equals(NumberFormatType.RANGE.getType())){
                if(value < entity.getLowLimit() || value > entity.getHighLimit()){
                    map.put("error", "Value must be between the range of " + entity.getLowLimit() + " and " + entity.getHighLimit());
                }
            }
            //Decimal Format
            if(entity.getType().equals(NumberFormatType.DECIMAL.getType())){
                DecimalFormat format = new DecimalFormat(entity.getFormat());
                map.put("formattedValue", format.format(value.doubleValue()));
            }
        }

        return map;
    }

    private boolean isWholeNumber(Double value){
        if(Math.floor(value) != value.doubleValue())
            return false;
        return true;
    }
}
