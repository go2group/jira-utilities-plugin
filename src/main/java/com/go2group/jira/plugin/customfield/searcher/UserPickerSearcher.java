package com.go2group.jira.plugin.customfield.searcher;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.issue.customfields.converters.UserConverter;
import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.jql.resolver.UserResolver;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.FieldVisibilityManager;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/15/14
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserPickerSearcher extends com.atlassian.jira.issue.customfields.searchers.UserPickerSearcher {
      public UserPickerSearcher(UserResolver userResolver, JqlOperandResolver operandResolver, JiraAuthenticationContext context, UserSearchService userSearchService, CustomFieldInputHelper customFieldInputHelper, UserManager userManager, FieldVisibilityManager fieldVisibilityManager){
          super(userResolver, operandResolver, context, ComponentManager.getComponent(UserConverter.class), userSearchService, customFieldInputHelper, userManager, fieldVisibilityManager);
      }
}
