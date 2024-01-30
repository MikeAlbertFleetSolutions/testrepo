package com.mikealbert.accounting.processor.item;

import com.mikealbert.accounting.processor.vo.TransactionLineVO;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.util.data.DataUtil;

import org.springframework.stereotype.Component;

@Component("fleetMaintItemIndex")
public class FleetMaintItemIndex implements ItemIndex {

	@Override
	public Integer generate(TransactionLineVO<?> line) {
		Integer index = null;

		if(!"01550024215".equals(line.getGlCode())) {
			if(line.getProductCode() != ProductEnum.DEMO && line.getProductCode() != ProductEnum.ST) {
				index = 1;				
			} else {
				if(line.getProductCode() == ProductEnum.ST) {
					index = 2;
				} else {
					switch(DataUtil.nvl(line.getDepartment(), "NO-DEPARTMENT")) {
						case "Business Development Managers":
						case "Client Partnership Managers":
						case "Marketing":
							index = 3;
							break;
						default:
							index = 4;
							break;
					}
				}
			}
		} else {
			index = 0;
		}
 
		return index;
	}
}