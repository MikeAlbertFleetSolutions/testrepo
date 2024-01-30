package com.mikealbert.accounting.processor.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.netsuite.webservices.platform.core_2023_2.SearchResult;
import com.netsuite.webservices.platform.core_2023_2.StatusDetail;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponseList;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;

public class SuiteTalkException extends RuntimeException {
	private static final long serialVersionUID = 8284963182199370139L;
	
	public SuiteTalkException(String msg, WriteResponse response) throws Exception {
		super(String.format("%s SuiteTalk Error Detail: %s", msg, flattenDetails(response.getStatus().getStatusDetail())));
	}
	
	public SuiteTalkException(String msg, ReadResponse response) throws Exception {
		super(String.format("%s SuiteTalk Error Detail: %s", msg, flattenDetails(response.getStatus().getStatusDetail())));		
	}

	public SuiteTalkException(String msg, SearchResult response) throws Exception {
		super(String.format("%s SuiteTalk Error Detail: %s", msg, flattenDetails(response.getStatus().getStatusDetail())));
	}	
	
	public SuiteTalkException(String msg, ReadResponseList response) throws Exception {
		super(String.format("%s SuiteTalk Error Detail: %s", msg, flattenDetails(response.getStatus().getStatusDetail())));
	}	

	public SuiteTalkException(String msg) throws Exception {
		super(msg);		
	}
	
	public SuiteTalkException(String msg, Throwable e) throws Exception {
		super(msg, e);		
	}
	
	public SuiteTalkException(Throwable e) throws Exception {
		super(e);		
	}	
	
	protected static String flattenDetails(StatusDetail[] details) {
		return Arrays
				.stream(details)
				.map(detail -> detail.getMessage())
				.collect(Collectors.toList())
				.toString();
	}	

}
