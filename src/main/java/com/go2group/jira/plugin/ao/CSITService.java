package com.go2group.jira.plugin.ao;

import java.util.Map;
import java.util.Set;

import com.atlassian.activeobjects.tx.Transactional;

@Transactional
public interface CSITService {

	CSITEntity add(Long customfield, String issuetype, String optLvl1) throws DuplicateEntityException;
	
	CSITEntity add(Long customfield, String issuetype, String optLvl1, String optLvl2) throws DuplicateEntityException;
	
	Map<String, Set<String>> getOptions(Long customfield, String issuetype);
	
	Set<String> getOptionsLevel1(Long customfield, String issuetype);
	
	Set<String> getOptionsLevel2(Long customfield, String issuetype, String optLvl1);
	
	void  delete(Long customfield, String issuetype, String optLvl1);
	
	void  delete(Long customfield, String issuetype, String optLvl1, String optLvl2);
}
