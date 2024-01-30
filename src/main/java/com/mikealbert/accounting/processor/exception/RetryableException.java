package com.mikealbert.accounting.processor.exception;

public class RetryableException extends RuntimeException {	

	public RetryableException(String msg){
		super(msg);		
	}

	public RetryableException(String msg, Throwable e) throws Exception {
		super(msg, e);		
	}	
	
	public RetryableException(Throwable e) throws Exception {
		super(e);		
	}		
}
