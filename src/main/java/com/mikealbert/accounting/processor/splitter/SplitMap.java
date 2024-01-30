package com.mikealbert.accounting.processor.splitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("splitMap")
public class SplitMap {
	private final Logger LOG = LogManager.getLogger(this.getClass());	

	/**
	 * Splits a single map of items into a map of each item/key. 
	 * @param splitBody
	 * @return ArrayList of map
	 */
	public List<Map<String, List<?>>> splitBody(Map<String, List<?>> splitBody) {
		List<Map<String, List<?>>> entries = new ArrayList<Map<String, List<?>>>();
		
		LOG.info("splitMap is splitting message body...");
		
		for(Map.Entry<String, List<?>> entry : splitBody.entrySet()){
			Map<String, List<?>> entryMap = new HashMap<String, List<?>>();
			entryMap.put(entry.getKey(), entry.getValue());
			entries.add(entryMap);
		}
		
		LOG.info("splitMap split message size = " + entries.toString());		
		
		return entries;
	}
}

