package com.mikealbert.accounting.processor.exception;

import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;

public class SuiteTalkNoRecordFoundException extends SuiteTalkException {

	public SuiteTalkNoRecordFoundException(String msg, WriteResponse response) throws Exception {
		super(msg, response);
	}
	
	public SuiteTalkNoRecordFoundException(String msg, ReadResponse response) throws Exception {
		super(msg, response);		
	}
}
