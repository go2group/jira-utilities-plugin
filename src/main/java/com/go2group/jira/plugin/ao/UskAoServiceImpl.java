package com.go2group.jira.plugin.ao;


import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: parthi
 */
public class UskAoServiceImpl implements UskAoService {

    private static final Logger log = LoggerFactory.getLogger(UskAoServiceImpl.class);
    
    private final ActiveObjects activeObjects;

    public UskAoServiceImpl(ActiveObjects activeObjects) {
        log.debug("SlaCalcAoServiceImpl() activeObjects {0} "+ activeObjects);

        this.activeObjects = activeObjects;
    }

    @Override
    public UniqueSequenceKey createUniqueSequenceKey(long customFieldId, String key) {
        return activeObjects.create(UniqueSequenceKey.class,
                new DBParam("CUSTOM_FIELD_ID", customFieldId), new DBParam("KEY", key), new DBParam("SEQUENCE", 0));
    }

    @Override
    public synchronized String getNextSequence(long customFieldId) {
        UniqueSequenceKey[] uniqueSequenceKey = activeObjects.find(UniqueSequenceKey.class, Query.select().where("CUSTOM_FIELD_ID = ?", customFieldId));
        if (uniqueSequenceKey == null || uniqueSequenceKey.length == 0) {
            return null;
        } else {
            if (uniqueSequenceKey[0].getKey() == null) {
                return null;
            } else {
                // long road to ensure its all done and dusted transactionaly before we pass the next sequence to the caller
                int currentSeq = uniqueSequenceKey[0].getSequence();
                uniqueSequenceKey[0].setSequence(currentSeq + 1);
                uniqueSequenceKey[0].save();
                return uniqueSequenceKey[0].getKey() + "#" + uniqueSequenceKey[0].getSequence();//# is used to enable quick search
            }
        }
    }

    @Override
    public UniqueSequenceKey getUniqueSequenceKeyByCf(long customFieldId) {
        UniqueSequenceKey[] uniqueSequenceKey = activeObjects.find(UniqueSequenceKey.class,
                Query.select().where("CUSTOM_FIELD_ID = ?", customFieldId));
        if (uniqueSequenceKey == null || uniqueSequenceKey.length == 0) {
            return null;
        } else {
            return uniqueSequenceKey[0];
        }
    }

    @Override
    public void removeUniqueSequenceKey(long customFieldId) {
        activeObjects.delete(activeObjects.find(UniqueSequenceKey.class,"CUSTOM_FIELD_ID = ?",customFieldId)); 
    }
}
