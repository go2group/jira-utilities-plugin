package com.go2group.jira.plugin.ao;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/23/14
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PriorityDueDateService {

    PriorityDueDate createOrUpdatePriorityDueDateMapping(String priorityName, Integer days);

    public PriorityDueDate getPriorityDueDateMapping(String priorityName);

    public ArrayList<PriorityDueDate> getPriorityDueDateMapping();

    void deleteMappingForPriority(String priorityName);
}
