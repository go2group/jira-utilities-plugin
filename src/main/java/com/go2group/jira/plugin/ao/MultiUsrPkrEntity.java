package com.go2group.jira.plugin.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;

@Preload
public interface MultiUsrPkrEntity extends Entity{
    @NotNull
    public String getCustomFieldKey();
    public void setCustomFieldKey(String cfKey);

    public String getGroupConfig();
    public void setGroupConfig(String groups);

}
