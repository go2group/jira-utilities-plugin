package com.go2group.jira.plugin.util;

// open symphony

import java.util.ArrayList;import java.util.List;import org.apache.log4j.Logger;import com.atlassian.crowd.embedded.api.Group;import com.atlassian.jira.ComponentManager;import com.atlassian.jira.component.ComponentAccessor;import com.atlassian.jira.config.properties.ApplicationProperties;import com.atlassian.jira.project.Project;import com.atlassian.jira.security.groups.GroupManager;import com.atlassian.jira.user.ApplicationUser;import com.atlassian.jira.user.UserUtils;

/**
 * Contains utility methods for determining if a user can view the restricted
 * fields
 */
public final class RestrictedFieldUtility {

	/**
	 * logger
	 */
	private static final Logger log = Logger.getLogger(RestrictedFieldUtility.class);

	/**
	 * config properties
	 */
	private final ApplicationProperties properties;
	private final GroupManager groupManager;

	/**
	 * config property prefix (format = PREFIX.projectKey =
	 * user,user,group,group,...)
	 */
	private static final String PREFIX = "com.go2group.jira.plugin.restricted_fields.";

	/**
	 * list separator
	 */
	private static final String SEP = ",";

	/**
	 * ctor
	 */
	public RestrictedFieldUtility() {
		properties = ComponentAccessor.getApplicationProperties();
		groupManager = (GroupManager) ComponentManager.getComponentInstanceOfType(GroupManager.class);
	} // ctor

	/**
	 * determines if user can see fields in the project
	 */
	public boolean canView(Project p, ApplicationUser u) {
		boolean view = false;
		if (null == p || null == u) {
			log.debug("Null project - may be invoked from CF Config");
			return true;
		} // null project		if (null == u) {			log.warn("Null user");			return false;		} //null user		
		log.debug("Determining if user " + u.getName() + " can see fields in project " + p.getKey());

		// see if user is explicitly authorized
		String s = properties.getString(PREFIX + p.getKey());
		if (null != s) {
			log.debug("List of authorized user/groups: " + s);
			String[] usersAndGroups = s.split(SEP);
			for (String userOrGroup : usersAndGroups) {
				log.debug("Checking user/group: " + userOrGroup);				if (u.getName().equals(userOrGroup)) {
					log.debug("Found exact match for user " + u.getName());
					view = true;
					break;
				} // exact match
				else if (groupManager.groupExists(userOrGroup)) {
					Group g = groupManager.getGroupObject(userOrGroup);
					log.debug(userOrGroup + " is a group");
					if (this.groupManager.isUserInGroup(u, g)) {
						log.debug("Found user " + u.getName() + " in group " + g.getName());
						view = true;
						break;
					} // in group
				} // is a group
				else {
					log.debug("Ignoring user " + userOrGroup);
				} // other user
			} // all users/groups

		} // has configuration
		else {
			log.info("No authorized users/groups for project " + p.getKey());
		} // no configuration

		return view;
	} // canView

	/**
	 * loads authorized users for project
	 */
	public List<String> loadProjectUsers(Project p) {
		List<String> list = new ArrayList<String>();
		if (null != p) {
			log.debug("Loading users for project " + p.getKey());
			String s = properties.getString(PREFIX + p.getKey());
			if (null != s) {
				log.debug("List of authorized user/groups for project " + p.getKey() + ": " + s);
				String[] usersAndGroups = s.split(SEP);
				for (String userOrGroup : usersAndGroups) {
					log.debug("Checking user/group " + userOrGroup);
					if (UserUtils.userExists(userOrGroup)) {
						log.debug(userOrGroup + " is a user");
						list.add(userOrGroup);
					} // is user
				} // all users/groups
			} // has values
		} // has project

		return list;
	} // loadProjectUsers

	/**
	 * loads authorized users for project
	 */
	public List<String> loadProjectGroups(Project p) {
		List<String> list = new ArrayList<String>();
		if (null != p) {
			log.debug("Loading groups for project " + p.getKey());
			String s = properties.getString(PREFIX + p.getKey());
			if (null != s) {
				log.debug("List of authorized user/groups for project " + p.getKey() + ": " + s);
				String[] usersAndGroups = s.split(SEP);
				for (String userOrGroup : usersAndGroups) {
					log.debug("Checking user/group " + userOrGroup);
					if (groupManager.groupExists(userOrGroup)) {
						log.debug(userOrGroup + " is a group");
						list.add(userOrGroup);
					} // is group
					else {
						log.debug(userOrGroup + " not a group");
					} // not a group
				} // all users/groups
			} // has values
		} // has project

		return list;
	} // loadProjectGroups

	/**
	 * saves project config
	 */
	public void saveProjectConfig(Project p, List<String> users, List<String> groups) {
		if (null != p) {
			log.debug("Saving users/groups for project " + p.getKey());
			StringBuffer sb = new StringBuffer();
			log.debug("Saving " + users.size() + " users");
			for (String s : users) {
				sb.append(s.trim());
				sb.append(SEP);
			} // all users
			log.debug("Saving " + groups.size() + " groups");
			for (String s : groups) {
				sb.append(s.trim());
				sb.append(SEP);
			} // all groups
			properties.setString(PREFIX + p.getKey(), sb.toString());
		} // has values
		else {
			log.warn("Null project");
		} // no project
	} // saveProjectConfig

} // RestrictedFieldUtility
