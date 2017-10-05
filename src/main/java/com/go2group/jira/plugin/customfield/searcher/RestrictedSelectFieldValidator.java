package com.go2group.jira.plugin.customfield.searcher;

import com.atlassian.core.user.preferences.Preferences;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.jql.util.JqlSelectOptionsUtil;
import com.atlassian.jira.jql.validator.SelectCustomFieldValidator;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper.BeanFactory;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.query.clause.TerminalClause;

public class RestrictedSelectFieldValidator extends SelectCustomFieldValidator {

	private static final String SEARCH_MODE_KEY = "user.search.mode";
	private static final String ADVANCED = "advanced";

	public RestrictedSelectFieldValidator(CustomField customField, JqlSelectOptionsUtil jqlSelectOptionsUtil,
			JqlOperandResolver jqlOperandResolver, BeanFactory beanFactory) {
		super(customField, jqlSelectOptionsUtil, jqlOperandResolver, beanFactory);
	}

	@Override
	public MessageSet validate(ApplicationUser user, TerminalClause clause) {
		MessageSet messages = super.validate(user, clause);

		String searchMode = getSearchMode(user);
		if (searchMode != null && searchMode.equals(ADVANCED)) {
			messages.addErrorMessage("Restricted fields are not allowed in Advanced search");
		} 

		return messages;
	}

	/**
	 * @return the current user's search mode or {@link #BASIC} if it's not set.
	 */
	public String getSearchMode(ApplicationUser user) {
		// Not in the session? Try in user preferences.
		final Preferences preferences = getPreferences(user);
		if (preferences != null) {
			String searchMode = preferences.getString(SEARCH_MODE_KEY);
			if (searchMode != null) {
				return searchMode;
			}
		}

		return null;
	}

	/**
	 * @return the current user's preferences or {@code null} if anonymous.
	 */
	private Preferences getPreferences(ApplicationUser user) {
		if (user != null) {
			return ComponentAccessor.getUserPreferencesManager().getPreferences(user);
		} else {
			return null;
		}
	}

}
