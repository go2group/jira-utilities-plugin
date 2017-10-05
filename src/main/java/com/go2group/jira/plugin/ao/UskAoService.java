package com.go2group.jira.plugin.ao;

import com.atlassian.activeobjects.tx.Transactional;

/**
 * User: parthi
 */
@Transactional
public interface UskAoService {

    UniqueSequenceKey createUniqueSequenceKey(long customFieldId, String key);

    String  getNextSequence(long customFieldId);

    UniqueSequenceKey getUniqueSequenceKeyByCf(long customFieldId);

    void removeUniqueSequenceKey(long customFieldId);
}
