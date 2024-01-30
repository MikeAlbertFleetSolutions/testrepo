package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.client.suitetalk.UnitSuiteTalkService;
import com.mikealbert.accounting.processor.service.UnitService;
import com.mikealbert.accounting.processor.vo.UnitVO;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component("unitProcessor")
public class UnitProcessor extends BaseProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(UnitProcessor.class);

	@Resource Environment env;
	@Resource UnitService unitService;
	@Resource UnitSuiteTalkService unitSuiteTalkService;
			
	@Override
	public void process(Exchange ex) throws Exception {
		LOG.info(ex.getExchangeId() + "Start UnitProcessor... ");

		UnitVO unit = (UnitVO) ex.getMessage().getBody();

		LOG.info(ex.getExchangeId() + " UnitProcessor... " + unit.toString());

		String response = unitService.upsertUnit(unit);

		LOG.info(ex.getExchangeId() + " POST UnitProcessor... " + response);

		ex.getIn().setBody(unit);

	}
	
	
}