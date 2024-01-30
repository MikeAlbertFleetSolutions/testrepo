package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;

import com.mikealbert.accounting.processor.vo.ClientDepositApplicationVO;

public interface DepositApplicationSuiteTalkService  {		
	public ClientDepositApplicationVO get(String internalId, String externalId) throws Exception;

	@Deprecated(forRemoval = true)
	public BigDecimal getAmountAppledToInvoice(String depositApplicationInternalId, String invoiceInternalId) throws Exception;
}
