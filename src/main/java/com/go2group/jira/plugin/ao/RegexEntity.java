package com.go2group.jira.plugin.ao;

import net.java.ao.Entity;
import net.java.ao.schema.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 4/18/14
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RegexEntity extends Entity {

    @NotNull
    public long getCustomFieldId();
    public void setCustomFieldId(long id);

    @NotNull
    public String getRegex();
    public void setRegex(String regex);

}
