package com.mikealbert.accounting.processor.client.address.scrubber;

import com.mikealbert.webservice.address.scrubber.component.vo.CleansedAddress;
import com.mikealbert.webservice.address.scrubber.component.vo.RawAddress;

public interface AddressScrubberService {
	public CleansedAddress scrub(RawAddress rawAddress) throws Exception;
}
