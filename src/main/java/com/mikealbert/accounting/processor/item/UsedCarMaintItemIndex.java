package com.mikealbert.accounting.processor.item;

import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.enumeration.AssetTypeEnum;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.TransactionLineVO;

@Component("usedCarMaintItemIndex")
public class UsedCarMaintItemIndex implements ItemIndex {

	@Override
	public Integer generate(TransactionLineVO<?> line) {
		Integer index = null;

		if(line instanceof InvoiceLineVO || line instanceof PurchaseOrderLineVO) {

			if(!"01550024215".equals(line.getGlCode())) {
				AssetTypeEnum assetType = line instanceof InvoiceLineVO ? ((InvoiceLineVO)line).getAssetType() : ((PurchaseOrderLineVO)line).getAssetType();
				if(AssetTypeEnum.UC.equals(assetType)) {
					index = 1;		    				    		
				} else if(line.getHeader().getDisposalFiscalYear() != null && line.getHeader().getCurrentFiscalYear().equals(line.getHeader().getDisposalFiscalYear())) {
					index = 2;
				} else if(line.getHeader().getDisposalFiscalYear() != null) {
					index = 3;	
				} else {
					index = 4;				
				}
			} else {
				index = 0;				
			}

		}
 		
		return index;
	}
}
