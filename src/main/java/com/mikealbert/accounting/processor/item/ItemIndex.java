package com.mikealbert.accounting.processor.item;

import com.mikealbert.accounting.processor.vo.TransactionLineVO;

public interface ItemIndex {
	Integer generate(TransactionLineVO<?> line);
}
