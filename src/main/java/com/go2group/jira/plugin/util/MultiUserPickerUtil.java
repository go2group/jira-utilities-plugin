package com.go2group.jira.plugin.util;

/* Copyright (c) 2002-2008 Go2Group
 * All rights reserved.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserUtils;
import com.go2group.jira.plugin.ao.MultiUserPickerService;
import com.go2group.jira.plugin.ao.MultiUsrPkrEntity;

/**
 * The POJO for the properties file, ie, a list of custom fields and their valid group name
 * @author doug
 *
 */
public class MultiUserPickerUtil {
	private static final Logger log = Logger.getLogger(MultiUserPickerUtil.class);

	public static boolean exists(String fieldName, MultiUserPickerService multiuserpickerService){
		MultiUsrPkrEntity entity = multiuserpickerService.getConfig(fieldName);
		
		if (entity == null || entity.getGroupConfig() == null || entity.getGroupConfig().trim().length() == 0){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Return true if the user is in a valid group specified in the properties file via the name of the custom field
	 * @param fieldName custom field name, eg, customfield_10000
	 * @return the group name or null if not found
	 */
	public static boolean isUserInGroup(String fieldName, String user, MultiUserPickerService multiuserpickerService) {
		try {
			return isUserInGroup(fieldName, UserUtils.getUser(user), multiuserpickerService);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Return true if the user is in a valid group specified in the properties file via the name of the custom field
	 * @param fieldName custom field name, eg, customfield_10000
	 * @return the group name or null if not found
	 */
	public static boolean isUserInGroup(String fieldName, ApplicationUser user, MultiUserPickerService multiuserpickerService) {
		
		if (user == null){
			return false;
		}
		
		MultiUsrPkrEntity entity = multiuserpickerService.getConfig(fieldName);
		
		if (entity == null || entity.getGroupConfig() == null){
			return false;
		}
		
		List<String> validGroups = new ArrayList<String>();

		String groupNames[] = entity.getGroupConfig().split(",");
		
		for (int i=0; i<groupNames.length; i++){
			if (groupNames[i] != null && groupNames[i].trim().length() > 0){
				validGroups.add(groupNames[i].trim());
			}
		}
		
		if (validGroups.size() == 0){
			return false;
		}
		
		Collection<String> userInGroups = ComponentAccessor.getGroupManager().getGroupNamesForUser(user);
		
		for (String validGroup: validGroups) {
			if (userInGroups.contains(validGroup)){
				return true;
			}
		}

		return false;
	}
}
