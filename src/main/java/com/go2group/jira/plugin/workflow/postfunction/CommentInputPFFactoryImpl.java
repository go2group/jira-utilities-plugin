package com.go2group.jira.plugin.workflow.postfunction;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommentInputPFFactoryImpl extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory
{
	private static final Logger log = LoggerFactory.getLogger(CommentInputPFFactoryImpl.class);
	
    protected void getVelocityParamsForInput(Map velocityParams)
    {
    }

    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor)
    {
        getVelocityParamsForView(velocityParams,descriptor);
    }

    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor)
    {
        velocityParams.put("enteredComment", getEnteredComment(descriptor));
    }

    public Map getDescriptorParams(Map conditionParams)
    {
    	log.debug("CONDITION PARAM : "+conditionParams);
    	Map<Object,Object> retMap = new HashMap<Object, Object>();
    	retMap.put("comment", extractSingleParam(conditionParams, "comment"));
    	return retMap;
    }

    private String getEnteredComment(AbstractDescriptor descriptor)
    {
        if (!(descriptor instanceof FunctionDescriptor))
        {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        }
        FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
        String comment = (String)functionDescriptor.getArgs().get("comment");
        log.debug("Comment Param : "+comment);
        return comment;
    }
}
