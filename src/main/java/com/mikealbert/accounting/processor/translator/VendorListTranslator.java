package com.mikealbert.accounting.processor.translator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component("vendorListTranslator")
public class VendorListTranslator {
	public void translate(Exchange ex) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> vendorsIn = (List<Map<String, Object>>) ex.getIn().getBody();
		

		Map<String, List<Map<String, Object>>> vendorsOut = vendorsIn.stream()
		.collect(Collectors.groupingBy(vendorMap -> (String) vendorMap.get("account_code"), HashMap::new, Collectors.toList()));
		 
		ex.getIn().setBody(vendorsOut);
		
	}
}
