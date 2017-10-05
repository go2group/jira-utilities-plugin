package com.go2group.jira.plugin.web.action;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.projectroles.ProjectRoleService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActor;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.web.action.JiraWebActionSupport;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/7/14
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class AssignProjectRoles extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(BulkDeleteProjectWebworkModuleAction.class);
    private ProjectManager projectManager = ComponentAccessor.getProjectManager();
    private ProjectRoleManager projectRoleManager = ComponentAccessor.getComponentOfType(ProjectRoleManager.class);
    private Collection<Project> projects = new ArrayList<Project>();
    private String[] selectedProjects = new String[ComponentAccessor.getProjectManager().getProjectObjects().size()];
    private Collection<ProjectRole> roles = new ArrayList<ProjectRole>();
    private String[] selectedRoles = new String[projectRoleManager.getProjectRoles().size()];
    private ProjectRoleService projectRoleService = ComponentAccessor.getComponentOfType(ProjectRoleService.class);
    private String usernames;

    public String[] getSelectedProjects() {
        return selectedProjects;
    }

    public ArrayList<Project> getSelectedProjectsAsArrayList() {
        ArrayList<Project> projects = new ArrayList<Project>();
        for(String key:getSelectedProjects()){
            if(key != null && key != "")
                projects.add(projectManager.getProjectObjByKey(key));
        }
        return projects;
    }

    public void setSelectedProjects(String[] selectedProjects) {
        this.selectedProjects = selectedProjects;
    }

    public AssignProjectRoles(){
        getProjects();
        getRoles();
    }

    public Collection<Project> getProjects() {
    projects = projectManager.getProjectObjects();
    return projects;
}

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }

    public Collection<ProjectRole> getRoles() {
        roles = projectRoleManager.getProjectRoles();
        return roles;
    }

    public void setRoles(ArrayList<ProjectRole> roles) {
        this.roles = roles;
    }

    public String[] getSelectedRoles() {
        return selectedRoles;
    }

    public ArrayList<ProjectRole> getSelectedRolesAsArrayList() {
        ArrayList<ProjectRole> roles = new ArrayList<ProjectRole>();
        for(String role:getSelectedRoles()){
            if(role != null && role != "")
                roles.add(projectRoleManager.getProjectRole(new Long(role)));
        }
        return roles;
    }

    public void setSelectedRoles(String[] selectedRoles) {
        this.selectedRoles = selectedRoles;
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
        if(getSelectedProjectsAsArrayList().size() == 0 || getSelectedRolesAsArrayList().size() == 0){
            addErrorMessage("Please select a project and role(s)");
            return INPUT;
        }
        //Check if usernames are entered
        if(getUsernames() == null || getUsernames().length() == 0){
            addErrorMessage("Please select a single username or a comma separated list of usernames.");
            return INPUT;
        }
        //Iterate through each project
        ErrorCollection errorCollection = new SimpleErrorCollection();
        for(Project project:getSelectedProjectsAsArrayList()){
            //Iterate through each role
            for(ProjectRole role: getSelectedRolesAsArrayList()){
                projectRoleService.addActorsToProjectRole(getProjectRoleActors(getUsernames()), role, project, ProjectRoleActor.USER_ROLE_ACTOR_TYPE, errorCollection);
            }
        }
        if(errorCollection.hasAnyErrors()){
            addErrorCollection(errorCollection);
            return ERROR;
        }
        return SUCCESS; //returns SUCCESS
    }

    private Collection<String> getProjectRoleActors(String usernames){
        Collection<String> usernameCollection = new ArrayList<String>();
        for(String username:usernames.split(",")){
            usernameCollection.add(username);
        }
        return usernameCollection;
    }
}
