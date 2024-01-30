package com.mikealbert.accounting.processor.splitter;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("listSplitter")
public class ListSplitter {
	static final long CHUNK_SIZE = 24999;
	
    final Logger LOGGER = LogManager.getLogger(this);
    
    /**
     * Splits a list of records into a new 2D list. Each list within the returned list is a predetermined chunk.
     * @param <T>
     * @param splitBody The list top split up into chunks
     * @return ArrayList of lists that represent the original list split up into chunks
     */
    public <T> List<List<T>> splitBody(List<T> splitBody) {
    	int rowCounter;
    	List<List<T>> chunks; 
    	
    	rowCounter = 0;
    	    	
    	chunks = new ArrayList<>();
    	chunks.add(new ArrayList<>());

    	for(T object : splitBody) {
    		rowCounter += 1;
    		
    		if(rowCounter > CHUNK_SIZE) {
    			rowCounter = 1;    			
    			chunks.add(new ArrayList<>());
    		} 
    		
    		chunks.get(chunks.size() - 1).add(object);    		
    	}
    	    	
    	return chunks;
    }
}
