package com.go2group.jira.plugin.ao;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 5/5/14
 * Time: 5:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NumberFormatAoService {

    public NumberFormatEntity saveNumberFormat(Long fieldConfigId, Long customFieldId, NumberFormatType type, String format, Double lowerLimit, Double higherLimit);

    public NumberFormatEntity updateNumberFormatEntity(NumberFormatEntity entity, NumberFormatType type, String format, Double lowerLimit, Double higherLimit);

    public NumberFormatEntity getNumberFormatEntity(Long fieldConfigId, Long customFieldId);

    public void removeNumberFormatEntity(Long fieldConfigId, Long customFieldId);

}
