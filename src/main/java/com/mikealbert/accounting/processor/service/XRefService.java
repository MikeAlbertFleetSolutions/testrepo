package com.mikealbert.accounting.processor.service;

import java.util.List;

import com.mikealbert.accounting.processor.entity.XRef;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;

public interface XRefService {
		
	String getExternalValue(XRefGroupNameEnum groupName, String internalValue) throws Exception;
	String getInternalValue(XRefGroupNameEnum groupName, String externalValue) throws Exception;
		
	List<XRef> getByGroupName(XRefGroupNameEnum groupName);		
	List<XRef> getByGroupNameAndExternalValue(XRefGroupNameEnum groupName, String externalValue);
	
	XRef createXRef(XRef xRef);
	void deleteXRef(XRef xRef);	
	
}
