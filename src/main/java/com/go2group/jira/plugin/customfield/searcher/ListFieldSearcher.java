package com.go2group.jira.plugin.customfield.searcher;

import com.atlassian.jira.issue.customfields.searchers.MultiSelectSearcher;
import com.atlassian.jira.util.JiraComponentFactory;
import com.atlassian.jira.util.JiraComponentLocator;

public class ListFieldSearcher extends MultiSelectSearcher {

	public ListFieldSearcher() {
		super(new JiraComponentLocator(), JiraComponentFactory.getInstance());
	}

}
