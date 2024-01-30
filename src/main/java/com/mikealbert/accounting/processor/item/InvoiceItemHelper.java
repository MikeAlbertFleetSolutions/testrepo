package com.mikealbert.accounting.processor.item;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;

import org.springframework.stereotype.Component;

@Component("invoiceItemHelper")
public class InvoiceItemHelper implements ItemHelper<InvoiceLineVO> {
	@Resource FineItemIndex fineItemIndex;
	@Resource LicenseItemIndex licenseItemIndex;
	@Resource DemoItemIndex demoItemIndex;	
	@Resource DemoMaintItemIndex demoMaintItemIndex;
	@Resource FleetMaintItemIndex fleetMaintItemIndex;
	@Resource FleetMaintNaItemIndex fleetMaintNaItemIndex;
	@Resource ShortTermMaintItemIndex shortTermMaintItemIndex;
	@Resource UsedCarMaintItemIndex usedCarMaintItemIndex;
	
	@Override
	public String generateKey(InvoiceLineVO line) throws Exception {
		Map<String, Object> key = new HashMap<>();
		key.put("controlCode", line.getHeader().getControlCode());

		switch(line.getHeader().getControlCode()) {
		case FLFINE:
			key.put("index", fineItemIndex.generate(line));
			break;
		case FLLICENSE:
			key.put("index", licenseItemIndex.generate(line));
			break;
		case DEMOMAINT:
			key.put("index", demoMaintItemIndex.generate(line));
			break;			
		case FLMAINT:
			key.put("index", fleetMaintItemIndex.generate(line));
			break;
		case FLMAINTNA:
			key.put("index", fleetMaintNaItemIndex.generate(line));
			break;
		case STMAINT:
			key.put("index", shortTermMaintItemIndex.generate(line));
			break;			
		case UCMAINT:
			key.put("index", usedCarMaintItemIndex.generate(line));
			break;				
		default:
			key.put("index", 0);
			break;
		}

		ObjectMapper mapper = new ObjectMapper();
		String retVal = mapper.writeValueAsString(key);

		return retVal;
	}		
}
