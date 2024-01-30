package com.mikealbert.accounting.processor.exception;

import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;

public class RetryableSuiteTalkException extends SuiteTalkException {
	private static final long serialVersionUID = -1335875089623694385L;
	
	public RetryableSuiteTalkException(String msg, WriteResponse response) throws Exception {
		super(msg, response);
	}
	
	public RetryableSuiteTalkException(String msg, ReadResponse response) throws Exception {
		super(msg, response);		
	}
	
	public RetryableSuiteTalkException(String msg) throws Exception {
		super(msg);		
	}
	
	public RetryableSuiteTalkException(String msg, Throwable e) throws Exception {
		super(msg, e);		
	}	
	
	public RetryableSuiteTalkException(Throwable e) throws Exception {
		super(e);		
	}	
	
}
