package com.go2group.jira.plugin.customfield;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webwork.action.Action;

import com.atlassian.jira.issue.customfields.searchers.renderer.CustomFieldRenderer;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.search.SearchContext;
import com.atlassian.jira.issue.search.searchers.renderer.SearchRenderer;
import com.atlassian.jira.issue.transport.FieldValuesHolder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.query.Query;

public class CSITRenderer implements SearchRenderer{

	private CustomFieldRenderer cfRenderer;
	
	private static Logger log = LoggerFactory.getLogger(CSITRenderer.class);
	
	public CSITRenderer(CustomFieldRenderer cfRenderer) {
		this.cfRenderer = cfRenderer;
	}

	public String getEditHtml(ApplicationUser user, SearchContext searchContext, FieldValuesHolder fieldValuesHolder,
			Map<?, ?> displayParameters, Action action) {

		String value = "";
		
		for (Object key : fieldValuesHolder.keySet()){
			log.debug("Received Key : "+key);
			Object entry = fieldValuesHolder.get(key);
			if (entry instanceof CustomFieldParams){
				log.debug("Instance of CustomFieldParams");
				CustomFieldParams params = (CustomFieldParams) entry;
				List<String> valueList = (List<String>)params.getKeysAndValues().get(null);
				
				if (valueList != null && valueList.size() > 0){
					value = valueList.get(0);
				}
			}
		}
		
		log.debug("Received Value : "+value);
		
		Map<String,Object> velocityParams = new HashMap<String, Object>();
		
		if (value.trim().length() > 0){
			
			String[] splitStr = value.trim().split(";");
			
			if (splitStr.length == 2){
				velocityParams.put("type1value", splitStr[0]);
				velocityParams.put("type2value", splitStr[1]);
			}else if (splitStr.length == 1){
				velocityParams.put("type1value", splitStr[0]);
			}
		}
		
		return cfRenderer.getEditHtml(searchContext, fieldValuesHolder, displayParameters, action, velocityParams);
	}
	
	public String getViewHtml(ApplicationUser user, SearchContext searchContext, FieldValuesHolder fieldValuesHolder,
			Map<?, ?> displayParameters, Action action) {

		String value = "";
		
		for (Object key : fieldValuesHolder.keySet()){
			log.debug("Received Key : "+key);
			Object entry = fieldValuesHolder.get(key);
			if (entry instanceof CustomFieldParams){
				log.debug("Instance of CustomFieldParams");
				CustomFieldParams params = (CustomFieldParams) entry;
				List<String> valueList = (List<String>)params.getKeysAndValues().get(null);
				
				if (valueList != null && valueList.size() > 0){
					value = valueList.get(0);
				}
			}
		}
		
		log.debug("Received Value : "+value);
		
		Map<String,Object> velocityParams = new HashMap<String, Object>();
		
		if (value.trim().length() > 0){
			
			String[] splitStr = value.trim().split(";");
			
			if (splitStr.length == 2){
				velocityParams.put("type1value", splitStr[0]);
				velocityParams.put("type2value", splitStr[1]);
			}else if (splitStr.length == 1){
				velocityParams.put("type1value", splitStr[0]);
			}
		}

		return cfRenderer.getViewHtml(searchContext, fieldValuesHolder, displayParameters, action, velocityParams);
	}
	
	public boolean isRelevantForQuery(ApplicationUser user, Query query) {
		return cfRenderer.isRelevantForQuery(user, query);
	}
	
	public boolean isShown(ApplicationUser user, SearchContext searchContext) {
		return cfRenderer.isShown(user, searchContext);
	}

}
