package com.mikealbert.accounting.processor.exception;

public class AddressLinkedToVehicleMovementException extends RuntimeException {	
	private static final long serialVersionUID = 7320894933379314639L;

	public AddressLinkedToVehicleMovementException(String msg){
		super(msg);		
	}
}
