package com.mikealbert.accounting.processor.processor;

import com.mikealbert.accounting.processor.vo.NgAssetsPerUnitVO;

import org.apache.camel.Exchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("assetCorrelationIdProcessor")
public class AssetCorrelationIdProcessor {
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public void process(Exchange ex) throws Exception {
		LOG.info(ex.getExchangeId() + this.getClass().getSimpleName() + " is started ...");

		NgAssetsPerUnitVO asset = (NgAssetsPerUnitVO)ex.getIn().getBody();
								
		ex.getIn().setHeader("JMSCorrelationID", asset.getUnitNo());
	    ex.getIn().setBody(ex.getIn().getBody());
	    
		LOG.info(ex.getExchangeId() + this.getClass().getSimpleName() + " completed id=" + asset.getUnitNo());
	}
}
