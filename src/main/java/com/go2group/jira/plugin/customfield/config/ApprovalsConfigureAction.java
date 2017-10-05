package com.go2group.jira.plugin.customfield.config;

import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;
import webwork.action.ActionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ApprovalsConfigureAction extends AbstractEditConfigurationItemAction
{
    private GenericConfigManager genericConfigManager;
    private String approvalFieldNames;

    public ApprovalsConfigureAction(ManagedConfigurationItemService managedConfigurationItemService, GenericConfigManager genericConfigManager)
    {
        super(managedConfigurationItemService);
        this.genericConfigManager = genericConfigManager;
    }

    protected String doExecute() throws Exception
    {
        Map<String, Object> values;

        Map actionParams = ActionContext.getParameters();
        if (actionParams.get("saveButton") == null)
        {
            String s = null;
            values = (Map<String, Object>) genericConfigManager.retrieve("options", getFieldConfigId().toString());
            if (values != null)
                s = (String) values.get("approvalFieldNames");
            setApprovalFieldNames(s);
            return SUCCESS;  // initial template display
        }
        
        values = new HashMap<String, Object>();
        values.put("approvalFieldNames", getApprovalFieldNames());
        
        // Parse the approval lines
        
        List<String> approvalNames = new ArrayList<String>();
        List<String> approvalStatuses = new ArrayList<String>();
        List<String> approvalFields = new ArrayList<String>();
        
        String[] lines = getApprovalFieldNames().replace("\\r","").split("\\n");
        for (String line : lines)
        {
            String[] s = line.split("\\|");
            if (s.length != 3)
                return SUCCESS; // error
            approvalNames.add(s[0].trim());
            approvalStatuses.add(s[1].trim());
            approvalFields.add(s[2].trim());
        }
        values.put("approvalNames", approvalNames);
        values.put("approvalStatuses", approvalStatuses);
        values.put("approvalFields", approvalFields);

        genericConfigManager.update("options", getFieldConfigId().toString(), values);

        return getRedirect("ViewCustomFields.jspa"); // Will redirect to returnURL is one is set
    }
    
    // Template methods
    
    public void setApprovalFieldNames(String s)
    {
        if (s == null)
            s = "";
        approvalFieldNames = s;
    }
    
    public String getApprovalFieldNames()
    {
        return approvalFieldNames;
    }
}