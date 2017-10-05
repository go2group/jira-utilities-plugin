package com.go2group.jira.plugin.customfield;

/*
 * This custom field scans an issue's history and builds a list of approvals from the workflow transitions.
 * It assumes a mostly linear workflow with one step with a status of "Not Approved"
 * For each transition from a "Pending A" status it displays a line like "user date-time A" or "user date-time A Denied"
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.config.item.SettableOptionsConfigItem;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItem;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;

public class ApprovalsCustomField extends CalculatedCFType
{
    private ChangeHistoryManager changeHistoryManager;
    private GenericConfigManager genericConfigManager;
    private CustomFieldManager customFieldManager;
    private OptionsManager optionsManager;
    private UserManager userManager;
    DateTimeFormatter dateFormat;

    public ApprovalsCustomField(ChangeHistoryManager changeHistoryManager,
                                GenericConfigManager genericConfigManager,
                                CustomFieldManager customFieldManager,
                                UserManager userManager,
                                DateTimeFormatterFactory dateTimeFactory,
                                OptionsManager optionsManager)
    {
        this.changeHistoryManager = changeHistoryManager;
        this.genericConfigManager = genericConfigManager;
        this.customFieldManager = customFieldManager;
        this.userManager = userManager;
        this.optionsManager = optionsManager;
        dateFormat = dateTimeFactory.formatter().forLoggedInUser();
    }

    public Object getSingularObjectFromString(String string) throws FieldValidationException
    {
        if (string == null)
            return null;
        return string;
    }

    public String getStringFromSingularObject(Object singularObject)
    {
        return null;
    }

    public Object getValueFromIssue(CustomField customField, Issue issue)
    {
        return buildApprovals(customField, issue); // value here doesn't really matter, just need either a null or non-null. 
    }

    @Override
    public Map<String,Object> getVelocityParameters(Issue issue, CustomField customField, FieldLayoutItem fieldLayoutItem)
    {

        Map<String, Object> map = new HashMap<String, Object>();
    	map.put("approvals", buildApprovals(customField, issue));
        return map;
    }

    private List<Map<String, Object>> buildApprovals(CustomField customField, Issue issue)
    {
        FieldConfig fieldConfig = customField.getRelevantConfig(issue);
        if (fieldConfig == null)
            return null;

        List<FieldConfigItem> fieldConfigItems = fieldConfig.getConfigItems(); // This is the list based on TimeInStatus.getConfigurationItemTypes()
        Map<String, List<String>> options = (Map<String, List<String>>) fieldConfigItems.get(0).getType().getConfigurationObject(issue, fieldConfig);
        if (options == null)
            return null;  // Field not configured

        // First build the rows for the approvals table from the list of approval labels configured for this field

        List<Map<String, Object>> approvalRows = new ArrayList<Map<String, Object>>();

        List<String> approvalNames = (List<String>)options.get("approvalNames");
        List<String> approvalFields = (List<String>)options.get("approvalFields");
        if (approvalNames == null || approvalFields == null)
            return approvalRows; // Not configured

        int i = 0;
        for (String approvalName : approvalNames)
        {
            Map<String, Object> valueMap = new HashMap<String, Object>();
            valueMap.put("name", approvalName);
            valueMap.put("times", new ArrayList<String>());

            // Is the custom field that holds the approver user blank?

            String approverName = "";
            String approverFieldName = approvalFields.get(i);
            if (approverFieldName != null)
            {
                CustomField approverField = customFieldManager.getCustomFieldObjectByName(approverFieldName);
                if (approverField != null)
                {
                    Object approver = issue.getCustomFieldValue(approverField);
                    if (approver instanceof ApplicationUser)
                    {
                        approverName = ((ApplicationUser)approver).getDisplayName();;
                    }
                }
            }
            valueMap.put("approver", approverName);

            approvalRows.add(valueMap);
            i++;
        }

        List<String> statusNames = (List<String>) options.get("approvalStatuses");
        List<ChangeHistoryItem> changeHistory = changeHistoryManager.getAllChangeItems(issue);
        for (ChangeHistoryItem changeHistoryItem : changeHistory)
        {
            if ("status".equals(changeHistoryItem.getField()))
            {
                String oldStatus = ((String)changeHistoryItem.getFroms().values().toArray()[0]).trim(); // Get Status for workflow step we are coming from
                String newStatus = ((String)changeHistoryItem.getTos().values().toArray()[0]).trim();
                i = statusNames.indexOf(oldStatus);
                if (i != -1)
                {
                    if (!oldStatus.equals(newStatus)) // Skip transitions that go from status A back to A (the user was just editing the approver fields on the transition screen)
                    {
                        String userKey = changeHistoryItem.getUserKey();
                        String userFullName = "";
                        ApplicationUser user = userManager.getUserByKey(userKey);
                        if (user != null)
                            userFullName = user.getDisplayName();
                        String approvalLine = userFullName + " " + dateFormat.format(changeHistoryItem.getCreated());
                        if ("Not Approved".equals(newStatus)) // Add " Denied" if we went to the "Not Approved" step
                            approvalLine += " Denied";
                        List<String> lines = (List<String>) approvalRows.get(i).get("times");
                        lines.add(approvalLine);
                    }
                }
            }
        }

        if (approvalRows.size() == 0)
            return null;

        return approvalRows;
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
        public ApprovalsCustomFieldConfigItem(CustomFieldType customFieldType, OptionsManager optionsManager)
        {
            super(customFieldType, optionsManager);
        }

        public String getBaseEditUrl()
        {
            return "ApprovalsConfigureAction.jspa";
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

            String s = values.get("approvalFieldNames");
            if (s == null || s.length() == 0)
                return "No approvals configured.";

            return s.replace("\r\n", "<br>").replace("\n", "<br>");
        }
    }
}