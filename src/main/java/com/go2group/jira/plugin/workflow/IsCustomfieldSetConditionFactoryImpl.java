package com.go2group.jira.plugin.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IsCustomfieldSetConditionFactoryImpl extends AbstractWorkflowPluginFactory implements WorkflowPluginConditionFactory
{
	private static final Logger log = LoggerFactory.getLogger(IsCustomfieldSetConditionFactoryImpl.class);
	
    private final CustomFieldManager customfieldManager;
    
    public IsCustomfieldSetConditionFactoryImpl(CustomFieldManager customfieldManager)
    {
        this.customfieldManager = customfieldManager;
    }

    protected void getVelocityParamsForInput(Map velocityParams)
    {
        velocityParams.put("customfields", customfieldManager.getCustomFieldObjects());
        velocityParams.put("checkparamvalues",getCheckParamValues());
    }

    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor)
    {
        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams,descriptor);
    }

    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor)
    {
        velocityParams.put("selectedCF", getSelectedCF(descriptor));
        velocityParams.put("selectedCheckParam", getSelectedCheckParam(descriptor));
    }

    public Map getDescriptorParams(Map conditionParams)
    {
    	log.debug("CONDITION PARAM : "+conditionParams);
    	
    	Map<Object,Object> retMap = new HashMap<Object, Object>();
    	
    	retMap.put("customfield", extractSingleParam(conditionParams, "customfield"));
    	retMap.put("checkparam", extractSingleParam(conditionParams, "checkparam"));
    	
    	return retMap;
    }

    private CustomField getSelectedCF(AbstractDescriptor descriptor)
    {
        if (!(descriptor instanceof ConditionDescriptor))
        {
            throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
        }

        ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;

        String cf = (String)conditionDescriptor.getArgs().get("customfield");

        log.debug("CF Param : "+cf);
        
        return customfieldManager.getCustomFieldObject(cf);
    }

    private String getSelectedCheckParam(AbstractDescriptor descriptor)
    {
        if (!(descriptor instanceof ConditionDescriptor))
        {
            throw new IllegalArgumentException("Descriptor must be a ConditionDescriptor.");
        }

        ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;

        String chckparam = (String)conditionDescriptor.getArgs().get("checkparam");

        log.debug("CHECK Param : "+chckparam);
        
        return chckparam;
    }
    
    private String getSingleValueString(String[] strArr){
    	if (strArr != null && strArr.length > 0){
    		return strArr[0];
    	}else{
    		return "";
    	}
    }

    private List<String> getCheckParamValues(){
    	List<String> list = new ArrayList<String>();
    	list.add("Set");
    	list.add("Not Set");

    	return list;
    }
    
}
