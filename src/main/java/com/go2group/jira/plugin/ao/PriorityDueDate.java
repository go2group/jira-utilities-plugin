package com.go2group.jira.plugin.ao;

import net.java.ao.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/23/14
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PriorityDueDate extends Entity {

    String getPriorityName();

    void setPriorityName(String priorityName);

    Integer getDays();

    void setDays(Integer days);
}