package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.TaxJurisdictionService;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;
import com.mikealbert.constant.enumeration.JurisdictionFieldEnum;

@Component("taxJurisdictionFinderProcessor")
public class TaxJurisdictionFinderProcessor extends BaseProcessor implements Processor {
	@Resource TaxJurisdictionService taxJurisdictionService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
			
	@Override
	public void process(Exchange ex) throws Exception {		
		String message = (String)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " START TaxJurisdictionFinderProcessor... " + message);	
        
		Map<String, String> map = super.convertJsonToMap(message);
		
		TaxJurisdictionVO taxJurisdiction =  taxJurisdictionService.find(
				map.get(JurisdictionFieldEnum.COUNTRY.getName()), 
				map.get(JurisdictionFieldEnum.REGION.getName()), 
				map.get(JurisdictionFieldEnum.COUNTY.getName()), 
				map.get(JurisdictionFieldEnum.CITY.getName()), 
				map.get(JurisdictionFieldEnum.POSTAL_CODE.getName()),
				map.get(JurisdictionFieldEnum.POSTAL_CODE.getName()));
			
		String response = new ObjectMapper().writeValueAsString(taxJurisdiction);
		
        LOG.info(ex.getExchangeId() + " POST TaxJurisdictionFinderProcessor... " + response);
        
		ex.getIn().setBody(response);
	}
}
