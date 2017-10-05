package com.go2group.jira.plugin.util;

import java.util.Comparator;

import com.atlassian.jira.issue.fields.SearchableField;

public class FieldComparator implements Comparator<SearchableField>{

	@Override
	public int compare(SearchableField o1, SearchableField o2) {
		
		if (o1 != null && o2 != null){
			return o1.getName().compareTo(o2.getName());
		}else{
			return 0;
		}
	}
}
