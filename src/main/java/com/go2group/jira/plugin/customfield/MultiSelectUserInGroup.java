package com.go2group.jira.plugin.customfield;

/* Copyright (c) 2002-2008 Go2Group
 * All rights reserved.
 */

//Atlassian imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.FeatureManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.MultiSelectCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.rest.json.beans.JiraBaseUrls;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.ErrorCollection;

/**
 * Implement a custom field that shows the users in the specified groups, roles, or userids as
 * specified in the field's config screen.
 * @author doug.bass@go2group.com
 */
public class MultiSelectUserInGroup extends MultiSelectCFType
{
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(MultiSelectUserInGroup.class);

    private ProjectRoleManager projectRoleManager;

    private UserManager userManager;

    private GroupManager groupManager;

    /**
     * Constructor.  All of the arguments are passed by JIRA. This constructor is called once at Jira startup.
     */
    public MultiSelectUserInGroup(OptionsManager optionsManager, CustomFieldValuePersister valuePersister,
                                  GenericConfigManager genericConfigManager, JiraBaseUrls jiraBaseUrls, ProjectRoleManager projectRoleManager,
                                  UserManager userManager, GroupManager groupManager, SearchService searchService,
                                  FeatureManager featureManager, JiraAuthenticationContext jiraAuthenticationContext)
    {
        //super(optionsManager, valuePersister, genericConfigManager, jiraBaseUrls);
        super(optionsManager, valuePersister, genericConfigManager, jiraBaseUrls, searchService, featureManager, jiraAuthenticationContext);
        this.projectRoleManager = projectRoleManager;
        this.userManager = userManager;
        this.groupManager = groupManager;
    }

    /**
     * Put all the userids in the velocity map that match the groups, roles, or userids that are in the
     * select field configuration.
     * @param issue the issue or null
     * @param field the custom field
     * @parm fieldlayoutItem layout for the field
     * @return the userids to display on the edit screen
     */
    public Map getVelocityParameters(Issue issue, CustomField field, FieldLayoutItem fieldLayoutItem)
    {
        Map<String, Object> map = super.getVelocityParameters(issue, field, fieldLayoutItem);
    	map.put("values", getAllUsersFromConfig(issue, getConfigValues(issue, field)));

        return map;

    } // end method getVelocityParameters

    private List<String> getConfigValues(Issue issue, CustomField field) {
        List<String> result = new LinkedList<String>();
        FieldConfig fieldConfig = null;
        if (issue != null)
            fieldConfig  = field.getRelevantConfig(issue);
        com.atlassian.jira.issue.customfields.option.Options options = getOptions(fieldConfig, null);
        if (options != null) {
            for (Object option: options.getRootOptions())
                result.add(((com.atlassian.jira.issue.customfields.option.Option)option).getValue());
        }
        return result;
    }

    private Collection<String> getAllUsersFromConfig(Issue issue, List<String> configValues) {
        Set<String> result = new LinkedHashSet<String>();
        for (String groupOrRole: configValues) {
            ProjectRole role = projectRoleManager.getProjectRole(groupOrRole);
            if (role != null) {
                ProjectRoleActors actorList = projectRoleManager.getProjectRoleActors(role, issue.getProjectObject());
                Set<ApplicationUser> actors = actorList.getUsers();
                for (ApplicationUser u: actors) {
                    log.debug("Add the user " + u.getName() + " from the role " + groupOrRole);
                    result.add(u.getName());
                }
            }
            Group group = userManager.getGroup(groupOrRole);
            if (group != null) {
                log.debug("Add the users " + groupManager.getUsersInGroup(group) + " from the group " + groupOrRole);
                result.addAll(groupManager.getUserNamesInGroup(group));
            }
            ApplicationUser user = userManager.getUser(groupOrRole);
            if (user != null) {
                log.debug("Add the specified user " + groupOrRole);
                result.add(groupOrRole);
            }
        }
        List<String> x = new ArrayList<String>(result);

        Collections.sort(x);

        return x;
    }

    @Override
    public String getChangelogString(CustomField arg0, Collection<Option> arg1) {
        return null;
    }

    @Override
    public String getChangelogValue(CustomField arg0, Collection<Option> arg1) {
        return null;
    }


    /**
     * Don't allow the validator to run as the vlaues stored in the issue are userids, not
     * the configuration values (like group names).
     *
     * @param relevantParams parameter object of Strings
     * @param errorCollectionToAddTo errorCollection to which any errors should be added (never null)
     * @param config FieldConfig
     */
    public void validateFromParams(final CustomFieldParams relevantParams, final ErrorCollection errorCollectionToAddTo, final FieldConfig config)
    {}
}




