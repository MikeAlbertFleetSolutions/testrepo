package com.mikealbert.accounting.processor.processor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("jsonToMapProcessor")
public class JSONToMapProcessor extends BaseProcessor implements Processor {		
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public void process(Exchange ex) throws Exception {						
		LOG.info(ex.getExchangeId() + "PRE JSONToMapProcessor... ");

		String messageJSON = (String)ex.getMessage().getBody();
				
		Map<String, Object> messageMap = super.convertJsonToObjectMap(messageJSON);

												               
		ex.getIn().setBody(messageMap);

        LOG.info(ex.getExchangeId() + " POST JSONToMapProcessor completed ... " + messageJSON);
	}
			
}
 