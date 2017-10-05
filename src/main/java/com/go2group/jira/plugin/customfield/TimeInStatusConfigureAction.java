package com.go2group.jira.plugin.customfield;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import webwork.action.ActionContext;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;

@SuppressWarnings("serial")
public class TimeInStatusConfigureAction extends AbstractEditConfigurationItemAction
{
    private GenericConfigManager genericConfigManager;
    private ConstantsManager constantsManager;
    private String status;

    public TimeInStatusConfigureAction(GenericConfigManager genericConfigManager, ConstantsManager constantsManager,ManagedConfigurationItemService managedConfigurationItemService)
    {
        super(managedConfigurationItemService);
        this.genericConfigManager = genericConfigManager;
        this.constantsManager = constantsManager;
    }

    protected String doExecute() throws Exception
    {
        Map<String, Object> values;

        Map<?, ?> actionParams = ActionContext.getParameters();
        if (actionParams.get("saveButton") == null)
        {
            String s = null;
            values = (Map<String, Object>) genericConfigManager.retrieve("options", getFieldConfigId().toString());
            if (values != null)
                s = (String) values.get("status");
            setStatus(s);
            return SUCCESS;  // initial template display
        }
        
        // Save settings

        values = new HashMap<String, Object>();
        values.put("status", status);
        genericConfigManager.update("options", getFieldConfigId().toString(), values);

        return getRedirect("ViewCustomFields.jspa"); // Will redirect to returnURL if one is set
    }

    // Template methods
    
    public void setStatus(String s)
    {
        if (s == null)
            s = "";
        status = s;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public Collection<Status> getStatuses()
    {
        return constantsManager.getStatusObjects();
    }
}
