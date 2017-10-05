package com.go2group.jira.plugin.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/5/14
 * Time: 5:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberFormatAoServiceImpl implements NumberFormatAoService{

    private static final Logger log = LoggerFactory.getLogger(NumberFormatAoServiceImpl.class);

    private final ActiveObjects activeObjects;

    public NumberFormatAoServiceImpl(ActiveObjects activeObjects){
        this.activeObjects = activeObjects;
    }

    public NumberFormatEntity saveNumberFormat(Long fieldConfigId, Long customFieldId, NumberFormatType type, String format, Double lowerLimit, Double higherLimit){
        NumberFormatEntity entity = activeObjects.create(
                NumberFormatEntity.class,
                new DBParam("FIELD_CONFIG_ID", fieldConfigId),
                new DBParam("CUSTOM_FIELD_ID", customFieldId),
                new DBParam("TYPE", type.getType()),
                new DBParam("FORMAT", format),
                new DBParam("LOW_LIMIT", lowerLimit),
                new DBParam("HIGH_LIMIT", higherLimit)
        );
        return entity;
    }

    public NumberFormatEntity updateNumberFormatEntity(NumberFormatEntity entity, NumberFormatType type, String format, Double lowerLimit, Double higherLimit){
        entity.setType(type.getType());
        entity.setFormat(format);
        entity.setLowLimit(lowerLimit);
        entity.setHighLimit(higherLimit);
        entity.save();
        return entity;
    }

    public NumberFormatEntity getNumberFormatEntity(Long fieldConfigId, Long customFieldId){
        NumberFormatEntity[] entities = activeObjects.find(NumberFormatEntity.class,
                Query.select().where("CUSTOM_FIELD_ID = ? AND FIELD_CONFIG_ID = ?",customFieldId, fieldConfigId));
        if (entities == null || entities.length == 0) {
            return null;
        } else {
            NumberFormatEntity entity = entities[0];
            return entity;
        }
    }

    public void removeNumberFormatEntity(Long fieldConfigId, Long customFieldId){
        activeObjects.delete(activeObjects.find(NumberFormatEntity.class,"CUSTOM_FIELD_ID = ? AND FIELD_CONFIG_ID = ?",customFieldId, fieldConfigId));
    }
}
