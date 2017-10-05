package com.go2group.jira.plugin.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.atlassian.core.util.map.EasyMap;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

public class ResolutionValidatorFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory {
	public static final String FIELD = "field";
	public static final String FIELDS = "fields";
	public static final String RESOLUTION = "resolution";
	public static final String RESOLUTIONS = "resolutions";

	private final FieldManager fieldManager;
	private final List<Field> fields;
	private final Collection<Resolution> resolutions;

	public ResolutionValidatorFactory(FieldManager fieldManager) {
		this.fieldManager = fieldManager;

		this.fields = new ArrayList<Field>();

		this.resolutions = ComponentAccessor.getConstantsManager().getResolutionObjects();
		List<CustomField> cFields = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();

		this.fields.add(fieldManager.getField(IssueFieldConstants.ASSIGNEE));
		this.fields.add(fieldManager.getField(IssueFieldConstants.DESCRIPTION));
		this.fields.add(fieldManager.getField(IssueFieldConstants.ENVIRONMENT));
		this.fields.add(fieldManager.getField(IssueFieldConstants.PRIORITY));
		this.fields.add(fieldManager.getField(IssueFieldConstants.RESOLUTION));
		this.fields.add(fieldManager.getField(IssueFieldConstants.TIME_ORIGINAL_ESTIMATE));
		this.fields.add(fieldManager.getField(IssueFieldConstants.TIME_ESTIMATE));
		this.fields.add(fieldManager.getField(IssueFieldConstants.TIME_SPENT));
		this.fields.add(fieldManager.getField(IssueFieldConstants.AFFECTED_VERSIONS));
		this.fields.add(fieldManager.getField(IssueFieldConstants.FIX_FOR_VERSIONS));

		fields.addAll(cFields);
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForInput(Map velocityParams) {

		velocityParams.put(RESOLUTIONS, resolutions);
		velocityParams.put(FIELDS, fields);
	}

	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		getVelocityParamsForView(velocityParams, descriptor);
	}

	@SuppressWarnings("unchecked")
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		if (!(descriptor instanceof ValidatorDescriptor)) {
			throw new IllegalArgumentException("Descriptor must be a ValidatorDescriptor.");
		}

		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;

		String resolutionId = (String) validatorDescriptor.getArgs().get(RESOLUTION);
		Resolution resolution = ComponentAccessor.getConstantsManager().getResolutionObject(resolutionId);

		String fieldId = (String) validatorDescriptor.getArgs().get(FIELD);
		Field field = this.fieldManager.getField(fieldId);

		velocityParams.put(RESOLUTION, resolution);
		velocityParams.put(FIELD, field);
	}

	public Map getDescriptorParams(Map validatorParams) {
		// Process The map
		String resolution = extractSingleParam(validatorParams, RESOLUTION);
		String field = extractSingleParam(validatorParams, FIELD);
		return EasyMap.build(RESOLUTION, resolution, FIELD, field);
	}
}
