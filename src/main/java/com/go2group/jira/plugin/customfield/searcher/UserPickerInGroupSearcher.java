package com.go2group.jira.plugin.customfield.searcher;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.issue.customfields.converters.UserConverter;
import com.atlassian.jira.issue.customfields.searchers.UserPickerSearcher;
import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.jql.resolver.UserResolver;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.EmailFormatter;
import com.atlassian.jira.web.FieldVisibilityManager;

/**
 * This searcher is needed for a type 2 plugin due to
 * http://jira.atlassian.com/browse/JRA-18986, ie, a wrapper around the user
 * converter is needed
 * 
 * 20110825 updated for JIRA 4.3.4
 * 
 * @author doug
 * 
 */
public class UserPickerInGroupSearcher extends UserPickerSearcher {

	public UserPickerInGroupSearcher(UserResolver userResolver, JqlOperandResolver operandResolver,
			JiraAuthenticationContext context, UserSearchService userSearchService, CustomFieldInputHelper customFieldInputHelper,
			UserManager userManager, FieldVisibilityManager fieldVisibilityManager, EmailFormatter emailFormatter) {
		super(userResolver, operandResolver, context, ComponentManager.getComponent(UserConverter.class), userSearchService, customFieldInputHelper,
				userManager, fieldVisibilityManager, emailFormatter);
	}
}
