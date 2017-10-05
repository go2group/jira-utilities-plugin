package com.go2group.jira.plugin.workflow.postfunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.google.common.base.Joiner;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/*
 This is the factory class responsible for dealing with the UI for the post-function.
 This is typically where you put default values into the velocity context and where you store user input.
 */

public class MultipleEventsFunctionFactory extends AbstractWorkflowPluginFactory implements
		WorkflowPluginFunctionFactory {

	public static final String EVENTS_FIELD = "eventsField";

	@Override
	protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
		velocityParams.put("events", ComponentAccessor.getEventTypeManager().getEventTypes());
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

		String eventsField = (String) functionDescriptor.getArgs().get(EVENTS_FIELD);
		if (eventsField != null) {
			String[] events = eventsField.split(",");
			List<EventType> eventTypes = new ArrayList<EventType>();
			for (String event : events) {
				eventTypes.add(ComponentAccessor.getEventTypeManager().getEventType(new Long(event)));
			}
			velocityParams.put("selectedEvents", eventTypes);
		}
	}

	public Map<String, ?> getDescriptorParams(Map<String, Object> formParams) {
		Map<String, Object> params = new HashMap<String, Object>();

		for (String key : formParams.keySet()) {
			System.out.println("Key:" + key + ", Value:" + formParams.get(key));
		}

		String[] eventsField = extractMultipleParams(formParams, EVENTS_FIELD);
		if (eventsField != null) {
			params.put(EVENTS_FIELD, Joiner.on(",").join(eventsField));
		}

		return params;
	}

	private String[] extractMultipleParams(final Map<String, Object> conditionParams, final String paramName) {
		if (conditionParams.containsKey(paramName)) {
			final Object argument = conditionParams.get(paramName);
			if (argument instanceof String[]) {
				return (String[]) argument;
			} else {
				throw new IllegalArgumentException("Argument '" + paramName + "' is not a String array.");
			}
		} else {
			throw new IllegalArgumentException("Cannot find expected argument '" + paramName + "' in parameters.");
		}
	}

}