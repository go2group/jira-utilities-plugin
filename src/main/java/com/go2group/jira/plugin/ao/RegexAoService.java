package com.go2group.jira.plugin.ao;

/**
 * Created with IntelliJ IDEA.
 * User: bhushan154
 * Date: 4/18/14
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RegexAoService {

    RegexEntity createRegularExpression(long customFieldId, String regex);

    RegexEntity updateRegularExpression(long customFieldId, String regex);

    RegexEntity getRegularExpressionByCf(long customFieldId);

    void removeRegularExpressionForCf(long customFieldId);
}
