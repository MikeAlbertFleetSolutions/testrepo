package com.mikealbert.accounting.processor.item;

import com.mikealbert.accounting.processor.vo.TransactionLineVO;

public interface ItemHelper <P extends TransactionLineVO<?>> {
	  String generateKey(P p) throws Exception;
}
