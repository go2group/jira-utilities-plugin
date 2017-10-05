package com.go2group.jira.plugin.customfield;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.web.bean.BulkEditBean;
import com.go2group.jira.plugin.ao.UskAoService;
import com.go2group.jira.plugin.customfield.config.UniqueSequenceConfig;

/**
 * User: parthi
 */
public class UniqueSequenceKeyField extends GenericTextCFType {

    private static final Logger log = LoggerFactory.getLogger(UniqueSequenceKeyField.class);

    private final UskAoService uskService;
    
    protected UniqueSequenceKeyField(CustomFieldValuePersister customFieldValuePersister, GenericConfigManager genericConfigManager,
    								UskAoService uskService) {
        super(customFieldValuePersister, genericConfigManager);
        this.uskService = uskService;
    }

    // Read only - not editable
    public String availableForBulkEdit(BulkEditBean bulkEditBean) {
        return "bulk.edit.unavailable";
    }

    @Override
    public List<FieldConfigItemType> getConfigurationItemTypes() {
    	List<FieldConfigItemType> configurationItemTypes = new ArrayList<FieldConfigItemType>();
        configurationItemTypes.add(new UniqueSequenceConfig(uskService));
        return configurationItemTypes;
    }
    
}
