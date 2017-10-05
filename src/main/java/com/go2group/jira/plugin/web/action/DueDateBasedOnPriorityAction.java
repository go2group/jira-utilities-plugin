package com.go2group.jira.plugin.web.action;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.go2group.jira.plugin.ao.PriorityDueDate;
import com.go2group.jira.plugin.ao.PriorityDueDateService;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/23/14
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class DueDateBasedOnPriorityAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(BulkDeleteProjectWebworkModuleAction.class);
    private PriorityDueDateService priorityDueDateService;
    private ConstantsManager constantsManager;
    private Collection priorities = new ArrayList<Priority>();
    private ArrayList<PriorityDueDate> mapping;
    private String priority;
    private String days;
    private String priorityToDelete;
    private String result;

    public DueDateBasedOnPriorityAction(ConstantsManager constantsManager,
                                        PriorityDueDateService priorityDueDateService){
        this.constantsManager = constantsManager;
        this.priorityDueDateService = priorityDueDateService;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPriorityToDelete() {
        return priorityToDelete;
    }

    public void setPriorityToDelete(String priorityToDelete) {
        this.priorityToDelete = priorityToDelete;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public Collection getPriorities() {
        return constantsManager.getPriorityObjects();
    }

    public void setPriorities(ArrayList<Priority> priorities) {
        this.priorities = priorities;
    }

    public ArrayList<PriorityDueDate> getMapping() {
        return priorityDueDateService.getPriorityDueDateMapping();
    }

    public void setMapping(ArrayList<PriorityDueDate> mapping) {
        this.mapping = mapping;
    }

    @Override
    public String doDefault() throws Exception {
        return INPUT;
    }


    @Override
    public String doExecute() throws Exception {
        try{
            priorityDueDateService.createOrUpdatePriorityDueDateMapping(getPriority(), getIntegerValue(getDays()));
        }
        catch(NumberFormatException exc){
            addErrorMessage("Please enter valid number of days.");
            return INPUT;
        }
        return getRedirect("/secure/admin/PriorityDueDate!default.jspa?result=added");
    }

    public String doDelete() throws Exception{
        priorityDueDateService.deleteMappingForPriority(getPriorityToDelete());
        return getRedirect("/secure/admin/PriorityDueDate!default.jspa?result=deleted");
    }

    private Integer getIntegerValue(String value) throws NumberFormatException {
        return new Integer(value);
}

}
