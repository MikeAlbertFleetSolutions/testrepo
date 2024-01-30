package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.LeaseService;
import com.mikealbert.accounting.processor.service.UnitService;
import com.mikealbert.accounting.processor.vo.LeaseVO;
import com.mikealbert.accounting.processor.vo.UnitVO;
import com.mikealbert.constant.enumeration.ProductTypeEnum;

@Component("leaseNewProcessor")
public class LeaseNewProcessor extends BaseProcessor implements Processor {
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Resource LeaseService leaseService;
	@Resource UnitService unitService;
			
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start LeaseNewProcessor... ");
        
		LeaseVO lease = (LeaseVO)ex.getMessage().getBody();
		
        LOG.info(ex.getExchangeId() + " LeaseNewProcessor Lease: " + lease.toString());
        
        String response = leaseService.upsertLease(lease);

		if(!isExtensionLease(lease) && isApplicableProduct(lease)) {			
			UnitVO unit = unitService.getUnitInfo(new UnitVO().setFmsId(lease.getFmsId()));
			unit.setCbv(lease.getClientCapitalCost());
			unitService.upsertUnit(unit);
		}		
	
        LOG.info(ex.getExchangeId() + " POST LeaseNewProcessor... " + response);
        
		ex.getIn().setBody(lease);
	}

	private boolean isExtensionLease(LeaseVO lease) {
		return lease.getParentExternalId()!= null ? true : false;
	}

	private boolean isApplicableProduct(LeaseVO lease) {
		switch(ProductTypeEnum.valueOf(lease.getInternalProductType())) {
			case OE:
			    return true;
			default:
				return false;
		}
	}
}
