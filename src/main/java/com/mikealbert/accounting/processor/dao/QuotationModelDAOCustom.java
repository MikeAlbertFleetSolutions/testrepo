package com.mikealbert.accounting.processor.dao;

import com.mikealbert.constant.enumeration.QuoteModelPropertyEnum;

public interface QuotationModelDAOCustom {
	public String fetchQuotationModelPropertyValueByFmsId(Long fmsId, QuoteModelPropertyEnum property);
}