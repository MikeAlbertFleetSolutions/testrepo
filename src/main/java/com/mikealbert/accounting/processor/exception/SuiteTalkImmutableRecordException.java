package com.mikealbert.accounting.processor.exception;

import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;

public class SuiteTalkImmutableRecordException extends SuiteTalkException {

	public SuiteTalkImmutableRecordException(String msg, WriteResponse response) throws Exception {
		super(msg, response);
	}
	
	public SuiteTalkImmutableRecordException(String msg, ReadResponse response) throws Exception {
		super(msg, response);		
	}
}
