package com.go2group.jira.plugin.ao;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/5/14
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public enum NumberFormatType {
    WHOLE("WHOLE"), RANGE("RANGE"), DECIMAL("DECIMAL");

    private String type;

    private NumberFormatType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}
