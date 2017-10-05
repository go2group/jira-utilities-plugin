package com.go2group.jira.plugin.customfield;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.issue.customfields.converters.DoubleConverter;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.BulkEditBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/13/14
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class LastUpdatedByCustomField extends CalculatedCFType {
    private static final Logger log = LoggerFactory.getLogger(LastUpdatedByCustomField.class);

    private CustomFieldManager customFieldManager;

    protected LastUpdatedByCustomField(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager,
                                           DoubleConverter doubleConverter, CustomFieldManager customFieldManager) {
        super();
        this.customFieldManager = customFieldManager;
    }

    // Read only - not editable
    public String availableForBulkEdit(BulkEditBean bulkEditBean) {
        return "bulk.edit.unavailable";
    }

    @Override
    public String getStringFromSingularObject(Object singularObject) {
        return singularObject.toString();
    }

    @Override
    public Object getSingularObjectFromString(String string)
            throws FieldValidationException {
        return string;
    }

    @Override
    public ApplicationUser getValueFromIssue(CustomField field, Issue issue) {
        FieldConfig fieldConfig = field.getRelevantConfig(issue);
        //add what you need to the map here

        ApplicationUser lastUpdatedBy = null;

        ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager();
        ChangeHistoryComparator changeHistoryComparator = new ChangeHistoryComparator();
        List<ChangeHistory> history = changeHistoryManager.getChangeHistories(issue);
        if(history.size() > 0){
            Collections.sort(history, changeHistoryComparator);
            lastUpdatedBy = ((ChangeHistory) history.get(0)).getAuthorObject();
        }
        return lastUpdatedBy;
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

        return map;
    }

}