package com.mikealbert.accounting.processor.client.suitetalk;

import com.mikealbert.accounting.processor.vo.UnitVO;

public interface UnitSuiteTalkService {
	public String putUnit(UnitVO unitVO) throws Exception;
	public void deleteUnit(UnitVO unit) throws Exception;		
	public UnitVO fetchByExternalId(String externalId) throws Exception;	

}
