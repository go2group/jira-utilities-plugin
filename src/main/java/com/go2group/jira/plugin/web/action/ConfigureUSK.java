package com.go2group.jira.plugin.web.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexingService;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.order.SortOrder;
import com.go2group.jira.plugin.ao.UniqueSequenceKey;
import com.go2group.jira.plugin.ao.UskAoService;

/**
 * User: parthi
 */
public class ConfigureUSK extends JiraWebActionSupport {

    private static Logger log = LoggerFactory.getLogger(ConfigureUSK.class);

    private Long customFieldId;

    private Long fieldConfigId;

    private Long fieldConfigSchemeId;

    private String configKey;

    private String initialized;

    private SearchService searchService;

    private final UskAoService uskService;

    private CustomFieldManager customfieldManager;

    private IssueManager issueManager;

    public ConfigureUSK(UskAoService uskService, SearchService searchService,
                        CustomFieldManager customfieldManager, IssueManager issueManager) {
        this.searchService = searchService;
        this.uskService = uskService;
        this.customfieldManager = customfieldManager;
        this.issueManager = issueManager;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public Long getCustomFieldId() {
        return customFieldId;
    }

    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public Long getFieldConfigId() {
        return fieldConfigId;
    }

    public void setFieldConfigId(Long fieldConfigId) {
        this.fieldConfigId = fieldConfigId;
    }

    public Long getFieldConfigSchemeId() {
        return fieldConfigSchemeId;
    }

    public void setFieldConfigSchemeId(Long fieldConfigSchemeId) {
        this.fieldConfigSchemeId = fieldConfigSchemeId;
    }

    public String getInitialized() {
        return initialized;
    }

    public void setInitialized(String initialized) {
        this.initialized = initialized;
    }

    @Override
    protected String doExecute() throws Exception {

        UniqueSequenceKey keyConfig = uskService.getUniqueSequenceKeyByCf(customFieldId);
    	
        if(keyConfig == null){
            return INPUT;
        }else{
            setConfigKey(keyConfig.getKey());
            return SUCCESS;
        }
    }

    public String doConfig(){

        UniqueSequenceKey keyconfig = uskService.createUniqueSequenceKey(customFieldId, configKey);

        log.debug("Configured Key Successfully : "+keyconfig.getKey()+" for customfieldId "+keyconfig.getCustomFieldId());

        return getRedirect(getReturnUrl());

    }

    public String doInitialize(){

        CustomField uskCF = customfieldManager.getCustomFieldObject(customFieldId);

        List<Issue> issues = getEligibleIssues(uskCF);

        for (Issue issue : issues){
            createUniqueSequence(issue, uskCF);
        }

        setInitialized("Yes");

        return SUCCESS;
    }

    public boolean allowInitialize(){
        CustomField uskCF = customfieldManager.getCustomFieldObject(customFieldId);

        List<Issue> issues = getEligibleIssues(uskCF);

        if (issues != null && issues.size() > 0){
            return true;
        }else{
            return false;
        }
    }

    private void createUniqueSequence(Issue issue, CustomField uskCF){
        MutableIssue mIssue = issueManager.getIssueObject(issue.getId());
        mIssue.setCustomFieldValue(uskCF,uskService.getNextSequence(uskCF.getIdAsLong()));
        issueManager.updateIssue(getLoggedInUser(), mIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
        try {
        	IssueIndexingService issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService.class);
        	if(issueIndexingService != null){
        		issueIndexingService.reIndex(mIssue);
        	}
        } catch (IndexException e) {
            log.debug(e.getMessage(), e);
            log.error(e.getMessage());
        }

    }


    private Long[] getRelevantProjects(CustomField uskCF){
        List<Long> projectIds = new ArrayList<Long>();

        for (Project p : uskCF.getAssociatedProjectObjects()){
            projectIds.add(p.getId());
        }

        return projectIds.toArray(new Long[0]);
    }

    private String[] getRelevantIssuetypes(CustomField uskCF){
        List<String> issuetypeIds = new ArrayList<String>();

        for (IssueType issueType : uskCF.getAssociatedIssueTypes()){

            if (issueType != null){
                issuetypeIds.add(issueType.getId());
            }
        }

        return issuetypeIds.toArray(new String[0]);
    }

    private List<Issue> getEligibleIssues(CustomField uskCF){

        log.debug("Associated Projects : "+uskCF.getAssociatedProjectObjects());

        log.debug("Associated IT : "+uskCF.getAssociatedIssueTypes());


        try{
            JqlQueryBuilder builder = JqlQueryBuilder.newBuilder();

            JqlClauseBuilder clauseBuilder = builder.where().customField(uskCF.getIdAsLong()).isEmpty();

            if (uskCF.getAssociatedProjectObjects().size() > 0){
                clauseBuilder = clauseBuilder.and().project(getRelevantProjects(uskCF));
            }

            if (getRelevantIssuetypes(uskCF).length > 0){
                clauseBuilder = clauseBuilder.and().issueType(getRelevantIssuetypes(uskCF));
            }

            builder.orderBy().createdDate(SortOrder.ASC);

            Query query = builder.buildQuery();

            log.debug("Executing Query : "+query.toString());

            SearchResults results = searchService.search(getLoggedInUser(), query, PagerFilter.getUnlimitedFilter());

            return results.getIssues();

        }catch(SearchException ex){
            log.debug(ex.getMessage(), ex);
            log.error(ex.getMessage());
            return null;
        }
    }

}
