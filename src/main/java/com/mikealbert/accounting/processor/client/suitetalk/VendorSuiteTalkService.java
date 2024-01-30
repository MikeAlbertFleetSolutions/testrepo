package com.mikealbert.accounting.processor.client.suitetalk;

import com.mikealbert.accounting.processor.vo.VendorVO;

public interface VendorSuiteTalkService  {

	public VendorVO get(String internalId, String externalId) throws Exception;
	
	public void updateVendorExternalId(String vendorInternalId, String vendorExternalId) throws Exception;

	public void updateVendorExternalIdAndAddressExternalId(String vendorInternalId, String vendorExternalId, String addressInternalId, String addressExternalId) throws Exception;

}
