package com.go2group.jira.plugin.listener;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventDispatchOption;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexingService;
import com.atlassian.jira.user.ApplicationUser;
import com.go2group.jira.plugin.ao.UskAoService;

public class USKListener implements InitializingBean, DisposableBean{

	private static Logger log = Logger.getLogger(USKListener.class);
	
	private final EventPublisher eventPublisher;
	
	private final CustomFieldManager customfieldManager;
	
	private final IssueManager issueManager;
	
	private final UskAoService uskService;
	
	public USKListener(EventPublisher eventPublisher, CustomFieldManager customfieldManager, IssueManager issueManager,
							UskAoService uskService) {
		this.eventPublisher = eventPublisher;
		this.customfieldManager = customfieldManager;
		this.issueManager = issueManager;
		this.uskService = uskService;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		eventPublisher.register(this);
	}

	@Override
	public void destroy() throws Exception {
		eventPublisher.unregister(this);
	}

	@EventListener
	public void createIssueEvent(IssueEvent event){
		
		if(event.getEventTypeId().equals(EventType.ISSUE_CREATED_ID)) {
			log.debug("Issuecreated event received on UIDListener for issue "+event.getIssue().getKey());

			log.debug("Subtask? :"+event.getIssue().isSubTask());
			log.debug("Issue type : "+event.getIssue().getIssueTypeObject().getName());
			
			if (!event.getIssue().isSubTask()){
				createUniqueSequence(event.getIssue(), event.getUser());
			}
		}
	}
	
	private void createUniqueSequence(Issue issue, ApplicationUser user){
		
		CustomFieldType usktype = getUniqueSequenceCFType();
		
		List<CustomField> cflist = customfieldManager.getCustomFieldObjects(issue);
		
		for (CustomField cf : cflist){
			log.debug("CFName : "+cf.getName());
			log.debug("CF - Relproject "+cf.getAssociatedProjectObjects());
			log.debug("CF - Issuetypes "+cf.getAssociatedIssueTypes());
			
			if(cf.getCustomFieldType().getKey().equals(usktype.getKey())){
				MutableIssue mIssue = issueManager.getIssueObject(issue.getId());
				mIssue.setCustomFieldValue(cf,uskService.getNextSequence(cf.getIdAsLong()));
				issueManager.updateIssue(user, mIssue, EventDispatchOption.DO_NOT_DISPATCH, false);
				
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
		}
	}
	
	private CustomFieldType getUniqueSequenceCFType(){
		CustomFieldType type = customfieldManager.getCustomFieldType("com.go2group.jira.plugin.jira-utilities:UniqueSequenceKey");
		log.debug("Type : "+ type);
		return type;
	}
}
