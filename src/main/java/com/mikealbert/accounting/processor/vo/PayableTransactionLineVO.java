package com.mikealbert.accounting.processor.vo;

public abstract class PayableTransactionLineVO<T> extends TransactionLineVO<T> {	
	public abstract String getClient();
	
	public abstract Long getDrvId();    

}
