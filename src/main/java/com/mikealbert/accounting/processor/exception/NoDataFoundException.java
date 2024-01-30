package com.mikealbert.accounting.processor.exception;

public class NoDataFoundException extends RuntimeException {	
	private static final long serialVersionUID = 1931708006552912402L;

	public NoDataFoundException(String msg){
		super(msg);		
	}
}
