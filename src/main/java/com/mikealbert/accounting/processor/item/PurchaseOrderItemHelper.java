package com.mikealbert.accounting.processor.item;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;

@Component("purchaseOrderItemHelper")
public class PurchaseOrderItemHelper implements ItemHelper<PurchaseOrderLineVO> {
	@Resource FleetMaintItemIndex maintItemIndex;
	@Resource FleetMaintNaItemIndex maintNaItemIndex;
	@Resource UsedCarMaintItemIndex ucMaintItemIndex;
	
	@Override
	public String generateKey(PurchaseOrderLineVO line) throws Exception {
		Map<String, Object> key = new HashMap<>();
		key.put("controlCode", line.getHeader().getControlCode());

		switch(line.getHeader().getControlCode()) {
		case FLMAINT:
			key.put("index", maintItemIndex.generate(line));
			break;
		case FLMAINTNA:
			key.put("index", maintNaItemIndex.generate(line));
			break;			
		case UCMAINT:
			key.put("index", ucMaintItemIndex.generate(line));
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
