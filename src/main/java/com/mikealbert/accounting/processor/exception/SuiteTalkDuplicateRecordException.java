package com.mikealbert.accounting.processor.exception;

import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;

public class SuiteTalkDuplicateRecordException extends SuiteTalkException {
	private static final long serialVersionUID = -4632186864106634201L;

	public SuiteTalkDuplicateRecordException(String msg, WriteResponse response) throws Exception {
		super(msg, response);
	}
	
	public SuiteTalkDuplicateRecordException(String msg, ReadResponse response) throws Exception {
		super(msg, response);		
	}
}
