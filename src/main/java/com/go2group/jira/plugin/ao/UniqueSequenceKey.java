package com.go2group.jira.plugin.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

/**
 * User: parthi
 */
@Preload
//@Table("UniqueSequenceKey")
public interface UniqueSequenceKey extends Entity {

    @NotNull
    public long getCustomFieldId();
    public void setCustomFieldId(long id);

    @NotNull
    public String getKey();
    public void setKey(String key);

    @NotNull
    int getSequence();
    void setSequence(int sequence);

}
