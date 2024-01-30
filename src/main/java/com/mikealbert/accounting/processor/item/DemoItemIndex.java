package com.mikealbert.accounting.processor.item;

import com.mikealbert.accounting.processor.vo.TransactionLineVO;

import org.springframework.stereotype.Component;

@Component("demoItemIndex")
public class DemoItemIndex implements ItemIndex {

	@Override
	public Integer generate(TransactionLineVO<?> line) {
		Integer index = null;

		index = 0;

		return index;
	}
}
