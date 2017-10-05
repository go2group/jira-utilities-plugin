package com.go2group.jira.plugin.web.action;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class BulkDeleteProjectWebworkModuleAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(BulkDeleteProjectWebworkModuleAction.class);
    private ProjectManager projectManager;
    private Collection<Project> projects = new ArrayList<Project>();
    private String[] selectedProjects = new String[ComponentAccessor.getProjectManager().getProjectObjects().size()];

    public String[] getSelectedProjects() {
        return selectedProjects;
    }

    public void setSelectedProjects(String[] selectedProjects) {
        this.selectedProjects = selectedProjects;
    }

    public BulkDeleteProjectWebworkModuleAction(ProjectManager projectManager){
        this.projectManager = projectManager;
        getProjects();
    }

    public Collection<Project> getProjects() {
        projects = projectManager.getProjectObjects();
        return projects;
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String doDefault() throws Exception {
        
    	return INPUT;
    }


    @Override
    public String doExecute() throws Exception {
        for(String projectKey: getSelectedProjects()){
            Project projectToBeDeleted = projectManager.getProjectObjByKey(projectKey);
            projectManager.removeProject(projectToBeDeleted);
        }
        return SUCCESS; //returns SUCCESS
    }
}
