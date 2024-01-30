package com.mikealbert.accounting.processor.client.address.scrubber;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mikealbert.webservice.address.scrubber.AddressScrubberClient;
import com.mikealbert.webservice.address.scrubber.component.vo.CleansedAddress;
import com.mikealbert.webservice.address.scrubber.component.vo.RawAddress;
import com.mikealbert.webservice.address.scrubber.vertex.VertexAddressScrubberClient;

@Service("addressScrubberService")
public class AddressScrubberServiceImpl implements AddressScrubberService {

	@Value( "${mafs.vertex.secret}" )
	String secret;

	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	private AddressScrubberClient scrubber;
	
	@PostConstruct
	public void init() {
		this.scrubber = new VertexAddressScrubberClient(secret);		
	}
	
	public CleansedAddress scrub(RawAddress rawAddress) throws Exception {
		LOG.info("AddressScrubberServiceImpl dirty {}  ", rawAddress);
		
		scrubber = new VertexAddressScrubberClient(secret);
		CleansedAddress cleansedAddress = scrubber.scrubWithAddressLineToggle(rawAddress);
		
		LOG.info("AddressScrubberServiceImpl cleansed {}  ", cleansedAddress);		
		
		return cleansedAddress;
	}

}
