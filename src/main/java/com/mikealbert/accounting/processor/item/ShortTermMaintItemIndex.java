package com.mikealbert.accounting.processor.item;

import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.vo.TransactionLineVO;

@Component("shortTermMaintItemIndex")
public class ShortTermMaintItemIndex implements ItemIndex {

	@Override
	public Integer generate(TransactionLineVO<?> line) {
		Integer index = null;

		if(!"01550024215".equals(line.getGlCode())) {
			index = 1;				
		} else {
			index = 0;
		}
 
		return index;
	}
}
