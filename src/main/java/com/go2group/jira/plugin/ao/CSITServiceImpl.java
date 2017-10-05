package com.go2group.jira.plugin.ao;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


import com.atlassian.activeobjects.external.ActiveObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSITServiceImpl implements CSITService{

	private static final String NULLENTRY = "NULLENTRY";

	private final ActiveObjects ao;
	
	private Logger log = LoggerFactory.getLogger(CSITServiceImpl.class);
	
	public CSITServiceImpl(ActiveObjects ao) {
		this.ao = ao;
	}
	
	@Override
	public CSITEntity add(Long customfield, String issuetype, String optLvl1) throws DuplicateEntityException{
		return add(customfield, issuetype, optLvl1, NULLENTRY);
	}

	@Override
	public CSITEntity add(Long customfield, String issuetype, String optLvl1, String optLvl2) throws DuplicateEntityException{
		
		if (getEntity(customfield, issuetype, optLvl1, optLvl2) != null){
			//This indicates there is an entry already present
			throw new DuplicateEntityException("DuplicateEntity Present");
		}

		log.debug("Creating Options");
		
		//TODO -- Start again from here..
		CSITEntity entityExist = getNULLEntryOption(customfield, issuetype, optLvl1);
		
		if (entityExist != null){
			ao.delete(entityExist);
		}
		
		CSITEntity option = ao.create(CSITEntity.class);
		
		option.setCustomfield(customfield);
		option.setIssuetype(issuetype);
		option.setOptLvl1(optLvl1);
		option.setOptLvl2(optLvl2);
		
		option.save();
		
		return option;
	}
	
	@Override
	public void delete(Long customfield, String issuetype, String optLvl1) {
		ao.delete(ao.find(CSITEntity.class,"CUSTOMFIELD = ? AND ISSUETYPE = ? AND OPT_LVL1 = ?",customfield, issuetype,optLvl1));
	}
	
	@Override
	public void delete(Long customfield, String issuetype, String optLvl1, String optLvl2) {
		ao.delete(ao.find(CSITEntity.class,"CUSTOMFIELD = ? AND ISSUETYPE = ? AND OPT_LVL1 = ? AND OPT_LVL2 = ?",customfield, issuetype,optLvl1,optLvl2));
		
		CSITEntity[] optionsArr = getEntities(customfield, issuetype, optLvl1);
		
		if (optionsArr != null && optionsArr.length > 0){
			//No issues
		}else{
			try {
				add(customfield, issuetype, optLvl1);
			} catch (DuplicateEntityException e) {
				//Never possible to get this exception.. so supressing it here
			}
		}
	}
	
	private CSITEntity[] getEntities(Long customfield, String issuetype, String optLvl1){
		return ao.find(CSITEntity.class,"CUSTOMFIELD = ? AND ISSUETYPE = ? AND OPT_LVL1 = ?",customfield,issuetype,optLvl1);
	}

	private CSITEntity getEntity(Long customfield, String issuetype, String optLvl1, String optLvl2){
		CSITEntity[] optionsArr = ao.find(CSITEntity.class, "CUSTOMFIELD = ? AND ISSUETYPE = ? AND OPT_LVL1 = ? AND OPT_LVL2 = ?",customfield, issuetype,optLvl1,optLvl2);
		
		if (optionsArr != null && optionsArr.length > 0){
			return optionsArr[0]; //Only one such entry is possible
		}else{
			return null;
		}
	}
	
	private CSITEntity getNULLEntryOption(Long customfield, String issuetype, String optLvl1){
		CSITEntity[] optionsArr = ao.find(CSITEntity.class, "CUSTOMFIELD = ? AND ISSUETYPE = ? AND OPT_LVL1 = ? AND OPT_LVL2 = ?",customfield, issuetype,optLvl1,NULLENTRY);
		
		if (optionsArr != null && optionsArr.length > 0){
			return optionsArr[0]; //Only one such entry is possible
		}else{
			return null;
		}
	}
	
	@Override
	public Set<String> getOptionsLevel1(Long customfield, String issuetype) {
		CSITEntity[] optionsArr = ao.find(CSITEntity.class, "CUSTOMFIELD = ? AND ISSUETYPE = ?",customfield, issuetype);
		
		Set<String> returnSet = new TreeSet<String>();
		
		if (optionsArr != null && optionsArr.length > 0){
			for (CSITEntity option : optionsArr){
				returnSet.add(option.getOptLvl1());
			}
		}
		
		return returnSet;
	}

	@Override
	public Set<String> getOptionsLevel2(Long customfield, String issuetype, String optLvl1) {
		CSITEntity[] optionsArr = ao.find(CSITEntity.class, "CUSTOMFIELD = ? AND ISSUETYPE = ? AND OPT_LVL1 = ? AND OPT_LVL2 <> ? ",customfield, issuetype,optLvl1,NULLENTRY);
		
		Set<String> returnSet = new TreeSet<String>();
		
		if (optionsArr != null && optionsArr.length > 0){
			for (CSITEntity option : optionsArr){
				returnSet.add(option.getOptLvl2());
			}
		}
		
		return returnSet;
	}
	
	@Override
	public Map<String, Set<String>> getOptions(Long customfield, String issuetype) {
		CSITEntity[] optionsArr = ao.find(CSITEntity.class, "CUSTOMFIELD = ? AND ISSUETYPE = ?",customfield, issuetype);

		Map<String,Set<String>> optionsMap = new TreeMap<String, Set<String>>(); 
		
		if (optionsArr != null && optionsArr.length > 0){
			for (CSITEntity option : optionsArr){
				Set<String> stl2Set = optionsMap.get(option.getOptLvl1());
				
				if (stl2Set == null){
					Set<String> newSet = new TreeSet<String>();
					if (!NULLENTRY.equals(option.getOptLvl2())){
						newSet.add(option.getOptLvl2());
					}
					optionsMap.put(option.getOptLvl1(), newSet);
				}else{
					stl2Set.add(option.getOptLvl2());
				}
			}
		}
		
		return optionsMap;
	}
}
