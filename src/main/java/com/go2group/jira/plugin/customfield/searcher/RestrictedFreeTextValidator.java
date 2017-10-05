package com.go2group.jira.plugin.customfield.searcher;

import com.atlassian.core.user.preferences.Preferences;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.jql.validator.FreeTextFieldValidator;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.query.clause.TerminalClause;

public class RestrictedFreeTextValidator extends FreeTextFieldValidator {

	public RestrictedFreeTextValidator(String indexField, JqlOperandResolver operandResolver) {
		super(indexField, operandResolver);
	}

	private static final String SEARCH_MODE_KEY = "user.search.mode";
	private static final String ADVANCED = "advanced";

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
