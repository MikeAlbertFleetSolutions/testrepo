package com.mikealbert.accounting.processor.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.stereotype.Component;

@Component("fileNameHandler")
public class FileNameHandler {
	public static final String FILE_NAME_KEY = "fileName";
	
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss.SSS");
	
	@Handler
	public String generate(Exchange ex) {
		String fileName = (String)ex.getIn().getHeader(FileNameHandler.FILE_NAME_KEY);
		
		if(fileName == null || fileName.isBlank()) 
			return String.format("%s.csv", dateFormat.format(Calendar.getInstance().getTime()));
		else
			return String.format("%s_%s.csv", ex.getIn().getHeader("fileName"), dateFormat.format(Calendar.getInstance().getTime()));
		
	}

}
