package com.go2group.jira.plugin.ao;

import net.java.ao.Entity;
import net.java.ao.schema.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/5/14
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NumberFormatEntity extends Entity {

    @NotNull
    public long getFieldConfigId();
    public void setFieldConfigId(long id);

    @NotNull
    public long getCustomFieldId();
    public void setCustomFieldId(long id);

    @NotNull
    public String getType();
    public void setType(String type);

    public String getFormat();
    public void setFormat(String format);

    //Used when range type is used
    public Double getLowLimit();
    public void setLowLimit(Double lowLimit);

    //Used when range type is used
    public Double getHighLimit();
    public void setHighLimit(Double highLimit);
}
