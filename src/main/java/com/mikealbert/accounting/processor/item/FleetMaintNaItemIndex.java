package com.mikealbert.accounting.processor.item;

import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.TransactionLineVO;
import com.mikealbert.constant.enumeration.ProductEnum;

@Component("fleetMaintNaItemIndex")
public class FleetMaintNaItemIndex implements ItemIndex {

	@Override
	public Integer generate(TransactionLineVO<?> line) {
		Integer index = null;

		if(line.getProductCode() == ProductEnum.DEMO || line.getProductCode() == ProductEnum.ST) {
			index = 1;
		} else {
			index = 0;
		}
 
		return index;
	}
}
