package com.mikealbert.accounting.processor.item;

import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.TransactionLineVO;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.util.data.DataUtil;

import org.springframework.stereotype.Component;

@Component("fineItemIndex")
public class FineItemIndex implements ItemIndex {

	@Override
	public Integer generate(TransactionLineVO<?> line) {
		Integer index = null;
		
		if(line instanceof InvoiceLineVO) {
			if(line.getProductCode() == ProductEnum.ST) {
				index = 0;
			} else if (line.getProductCode() == ProductEnum.DEMO) {
				switch(DataUtil.nvl(line.getDepartment(), "NO-DEPARTMEN")) {
					case "Business Development Managers":
					case "Client Partnership Managers":
					case "Marketing":
					    index = 1;
					    break;
					default:
					    index = 2;
					    break;
				}
			} else if(((InvoiceLineVO)line).isRebillableClientFine()) {
				index = 3;
			} else {
				index = 4;
			}			
		}
					
		return index;
	}
}
