package com.go2group.jira.plugin.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 4/18/14
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegexAoServiceImpl implements RegexAoService {

    private static final Logger log = LoggerFactory.getLogger(RegexAoServiceImpl.class);

    private final ActiveObjects activeObjects;

    public RegexAoServiceImpl(ActiveObjects activeObjects) {
        log.debug("SlaCalcAoServiceImpl() activeObjects {0} "+ activeObjects);

        this.activeObjects = activeObjects;
    }

    @Override
    public RegexEntity createRegularExpression(long customFieldId, String regex) {
        return activeObjects.create(RegexEntity.class,
                new DBParam("CUSTOM_FIELD_ID", customFieldId), new DBParam("REGEX", regex));
    }

    @Override
    public RegexEntity updateRegularExpression(long customFieldId, String regex){
        RegexEntity[] regexEntities = activeObjects.find(RegexEntity.class,
                Query.select().where("CUSTOM_FIELD_ID = ?", customFieldId));
        if (regexEntities == null || regexEntities.length == 0) {
            return null;
        } else {
            RegexEntity entityToUpdate = regexEntities[0];
            entityToUpdate.setRegex(regex);
            entityToUpdate.save();
            return entityToUpdate;
        }
    }

    @Override
    public RegexEntity getRegularExpressionByCf(long customFieldId) {
        RegexEntity[] regexEntities = activeObjects.find(RegexEntity.class,
                Query.select().where("CUSTOM_FIELD_ID = ?", customFieldId));
        if (regexEntities == null || regexEntities.length == 0) {
            return null;
        } else {
            return regexEntities[0];
        }
    }

    @Override
    public void removeRegularExpressionForCf(long customFieldId) {
        activeObjects.delete(activeObjects.find(RegexEntity.class,"CUSTOM_FIELD_ID = ?",customFieldId));
    }
}
