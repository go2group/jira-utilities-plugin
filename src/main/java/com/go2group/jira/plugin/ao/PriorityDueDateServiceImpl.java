package com.go2group.jira.plugin.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/23/14
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class PriorityDueDateServiceImpl implements PriorityDueDateService{

    private static final Logger log = LoggerFactory.getLogger(PriorityDueDateServiceImpl.class);

    private final ActiveObjects activeObjects;

    public PriorityDueDateServiceImpl(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    public PriorityDueDate createOrUpdatePriorityDueDateMapping(String priorityName, Integer days){
        PriorityDueDate[] priorityDueDateEntities = activeObjects.find(PriorityDueDate.class,
                Query.select().where("PRIORITY_NAME = ?", priorityName));
        if (priorityDueDateEntities == null || priorityDueDateEntities.length == 0) {
            return activeObjects.create(PriorityDueDate.class,
                    new DBParam("PRIORITY_NAME", priorityName), new DBParam("DAYS", days));
        } else {
            PriorityDueDate entityToUpdate = priorityDueDateEntities[0];
            entityToUpdate.setDays(days);
            entityToUpdate.save();
            return entityToUpdate;
        }
    }

    public ArrayList<PriorityDueDate> getPriorityDueDateMapping(){
        PriorityDueDate[] entities = activeObjects.find(PriorityDueDate.class);
        return new ArrayList<PriorityDueDate>(Arrays.asList(entities));
    }

    public PriorityDueDate getPriorityDueDateMapping(String priorityName){
        PriorityDueDate[] priorityDueDateEntities = activeObjects.find(PriorityDueDate.class,
                Query.select().where("PRIORITY_NAME = ?", priorityName));
        if (priorityDueDateEntities == null || priorityDueDateEntities.length == 0) {
            return null;
        } else {
            PriorityDueDate entity = priorityDueDateEntities[0];
            return entity;
        }
    }


    public void deleteMappingForPriority(String priorityName){
        activeObjects.delete(activeObjects.find(PriorityDueDate.class,"PRIORITY_NAME = ?",priorityName));
    }
}
