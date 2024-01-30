package com.mikealbert.accounting.processor.item;

import com.mikealbert.accounting.processor.vo.TransactionLineVO;
import com.mikealbert.util.data.DataUtil;

import org.springframework.stereotype.Component;

@Component("demoMaintItemIndex")
public class DemoMaintItemIndex implements ItemIndex {

	@Override
	public Integer generate(TransactionLineVO<?> line) {
		Integer index = null;

		if("01550024215".equals(line.getGlCode())) {
			index = 0;
		} else {
			switch(DataUtil.nvl(line.getDepartment(), "NO-DEPARTMENT")) {
				case "Business Development Managers":
				case "Client Partnership Managers":
				case "Marketing":
				    index = 1;
					break;
				default:
				    index = 2;
					break;
			}
		}
 
		return index;
	}
}
