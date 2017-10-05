package com.go2group.jira.plugin.web.action;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.web.action.JiraWebActionSupport;

/**
 * Created by bhushan154 on 30/07/14.
 */
public class AssignGroups extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(BulkDeleteProjectWebworkModuleAction.class);
    private GroupManager groupManager;
    private ArrayList<Group> groups;
    private String[] selectedGroups;
    private String usernames;

    public AssignGroups(GroupManager groupManager){
        this.groupManager = groupManager;
        this.selectedGroups = new String[groupManager.getAllGroups().size()];
        getGroups();
    }

    public String[] getSelectedGroups() {
        return selectedGroups;
    }

    public ArrayList<Group> getSelectedGroupsAsArrayList() {
        ArrayList<Group> groups = new ArrayList<Group>();
        for(String name:getSelectedGroups()){
            if(name != null && name != "")
                groups.add(groupManager.getGroup(name));
        }
        return groups;
    }

    public void setSelectedGroups(String[] selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public Collection<Group> getGroups() {
        return groupManager.getAllGroups();
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    public String getUsernames() {
        return usernames;
    }

    public void setUsernames(String usernames) {
        this.usernames = usernames;
    }

    @Override
    public String doDefault() throws Exception {
    	return INPUT;
    }


    @Override
    public String doExecute() throws Exception {
        //Check if projects and roles are selected
        if(getSelectedGroupsAsArrayList().size() == 0){
            addErrorMessage("Please select atleast one group.");
            return INPUT;
        }
        //Check if usernames are entered
        if(getUsernames() == null || getUsernames().length() == 0){
            addErrorMessage("Please enter a single username or enter a comma separated list of usernames.");
            return INPUT;
        }
        //Iterate through each group
        ErrorCollection errorCollection = new SimpleErrorCollection();
        //Iterate through each role
        for(Group group: getSelectedGroupsAsArrayList()){
            for(String username:getUsernames().split(",")){
                username = username.trim();
                ApplicationUser user = getUserManager().getUserByName(username);
                if(user == null) {
                    errorCollection.addErrorMessage("User " + username + " not found. Could not be added to group " + group.getName());
                }
                else
                    groupManager.addUserToGroup(user, group);
            }
        }
        if(errorCollection.hasAnyErrors()){
            addErrorCollection(errorCollection);
            return ERROR;
        }
        return SUCCESS; //returns SUCCESS
    }
}