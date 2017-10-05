package com.go2group.jira.plugin.ao;

import net.java.ao.DBParam;
import net.java.ao.Query;


import com.atlassian.activeobjects.external.ActiveObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiUserPickerServiceImpl implements MultiUserPickerService{

	private static final Logger log = LoggerFactory.getLogger(MultiUserPickerServiceImpl.class);
	
	private final ActiveObjects activeObjects;
	
	public MultiUserPickerServiceImpl(ActiveObjects activeObjects) {
		this.activeObjects = activeObjects;
	}
	
	@Override
	public MultiUsrPkrEntity getConfig(String cfKey) {
		MultiUsrPkrEntity[] configs = activeObjects.find(MultiUsrPkrEntity.class,
                Query.select().where("CUSTOM_FIELD_KEY = ?", cfKey));
        if (configs != null && configs.length == 0) {
            return null;
        } else {
            return configs[0];
        }
	}

	@Override
	public void updateConfig(String cfKey, String groupsConfig) {
		
		MultiUsrPkrEntity entity = getConfig(cfKey); 
		
		if (entity == null){
			//Perform Create
	        activeObjects.create(MultiUsrPkrEntity.class,
	                new DBParam("CUSTOM_FIELD_KEY", cfKey), new DBParam("GROUP_CONFIG", groupsConfig));

		}else{
			//Perform Update
			entity.setGroupConfig(groupsConfig);
			entity.save();
		}
	}
	
}
