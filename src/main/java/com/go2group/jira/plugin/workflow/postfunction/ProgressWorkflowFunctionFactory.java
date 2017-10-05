package com.go2group.jira.plugin.workflow.postfunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.impl.MultiSelectCFType;
import com.atlassian.jira.issue.customfields.impl.NumberCFType;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/*
 This is the factory class responsible for dealing with the UI for the post-function.
 This is typically where you put default values into the velocity context and where you store user input.
 */

public class ProgressWorkflowFunctionFactory extends AbstractWorkflowPluginFactory implements
		WorkflowPluginFunctionFactory {

	public static final String TRANSITION_FIELD = "transitionField";
	public static final String CUSTOM_FIELD = "customField";
	public static final String CUSTOM_FIELD_VALUE = "cfValue";

	@Override
	protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
		List<CustomField> validFields = new ArrayList<CustomField>();
		List<CustomField> customFieldObjects = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();
		for (CustomField customField : customFieldObjects) {
			CustomFieldType customFieldType = customField.getCustomFieldType();
			if (customFieldType instanceof GenericTextCFType || customFieldType instanceof SelectCFType
					|| customFieldType instanceof NumberCFType || customFieldType instanceof MultiSelectCFType) {
				validFields.add(customField);
			}
		}
		velocityParams.put("cFields", validFields);
	}

	@Override
	protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		getVelocityParamsForView(velocityParams, descriptor);
	}

	@Override
	protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
		if (!(descriptor instanceof FunctionDescriptor)) {
			throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
		}

		FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;

		String transitionId = (String) functionDescriptor.getArgs().get(TRANSITION_FIELD);
		String customField = (String) functionDescriptor.getArgs().get(CUSTOM_FIELD);
		String cfValue = (String) functionDescriptor.getArgs().get(CUSTOM_FIELD_VALUE);

		velocityParams.put(TRANSITION_FIELD, transitionId);
		velocityParams.put(CUSTOM_FIELD, customField);
		velocityParams.put(CUSTOM_FIELD_VALUE, cfValue);

		if (customField != null) {
			CustomField cField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(customField);
			velocityParams.put("cfName", cField.getName());
		}
	}

	public Map<String, ?> getDescriptorParams(Map<String, Object> formParams) {
		Map<String, String> params = new HashMap<String, String>();

		// Process The map
		String transitionId = extractSingleParam(formParams, TRANSITION_FIELD);
		String customField = extractSingleParam(formParams, CUSTOM_FIELD);
		String cfValue = extractSingleParam(formParams, CUSTOM_FIELD_VALUE);
		params.put(TRANSITION_FIELD, transitionId);
		params.put(CUSTOM_FIELD, customField);
		params.put(CUSTOM_FIELD_VALUE, cfValue);

		return params;
	}

}