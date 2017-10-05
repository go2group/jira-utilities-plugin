package com.go2group.jira.plugin.customfield.searcher;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.customfields.searchers.TextSearcher;
import com.atlassian.jira.issue.customfields.searchers.renderer.CustomFieldRenderer;
import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.issue.search.searchers.renderer.SearchRenderer;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.go2group.jira.plugin.customfield.CSITRenderer;

public class CSITSearcher extends TextSearcher{

	private static Logger log = Logger.getLogger(CSITSearcher.class);
	
	public CSITSearcher(FieldVisibilityManager fieldVisibilityManager, JqlOperandResolver jqlOperandResolver,
			CustomFieldInputHelper customFieldInputHelper) {
		super(fieldVisibilityManager, jqlOperandResolver, customFieldInputHelper);
	}

	@Override
	public SearchRenderer getSearchRenderer() {
		log.debug("getSearchRenderer");
		SearchRenderer r =  new CSITRenderer((CustomFieldRenderer)super.getSearchRenderer());
		return r;
	}
	
}
