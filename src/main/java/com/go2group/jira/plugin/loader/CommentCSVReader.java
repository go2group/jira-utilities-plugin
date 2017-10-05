package com.go2group.jira.plugin.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.go2group.jira.plugin.util.JiraHomeUtil;

/**
 * 
 * @author muralidharan [Go2Group Inc.]
 *
 */

public class CommentCSVReader {

	private Logger log = Logger.getLogger(CommentCSVReader.class);
	
	public void doProcess(Map<String, List<String>> dataMap, String filename) throws FileNotFoundException, IOException{

		log.debug("Processing the Comment load...");
		
		//Open the file
		String fileLoc = JiraHomeUtil.getJiraImportDirectory() + File.separator + filename;

		log.debug("Reading from file : "+fileLoc);
		
		CsvReader csvReader = new CsvReader(new FileReader(fileLoc));
		
		//Just read this, no need to process the headers
		csvReader.readHeaders();
		
		int counter = 0;

		while (csvReader.readRecord()){

			String[] values = csvReader.getValues();
			
			log.trace("CSV Data read : "+Arrays.toString(values));
			
			parseRecord(values, dataMap);
			
			counter++;
		}
		
		log.debug("No of records processed : "+counter);
		//Close the file
		csvReader.close();
		
	}
	
	public static void main(String[] args) {
		
	}
	
	private void parseRecord(String[] values, Map<String, List<String>> dataMap){
		if (values != null && values.length > 0){
			
			List<String> bean = dataMap.get(values[0]);
			
			if (bean == null){
				bean = new ArrayList<String>();
				dataMap.put(values[0], bean);
			}

			for (int i=1; i<values.length; i++){
				if (values[i].trim().length() > 0){
					bean.add(values[i].trim());
				}
			}
		}
	}
	
}
