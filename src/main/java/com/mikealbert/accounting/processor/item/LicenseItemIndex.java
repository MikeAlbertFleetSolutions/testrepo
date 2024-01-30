package com.mikealbert.accounting.processor.item;

import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.TransactionLineVO;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.util.data.DataUtil;

import org.springframework.stereotype.Component;

@Component("licenseItemIndex")
public class LicenseItemIndex implements ItemIndex {
    static final String SALES_TAX_GL_CODE = "01220010000";
	
	@Override
	public Integer generate(TransactionLineVO<?> line) {
		Integer index = null;

		if(line instanceof InvoiceLineVO) {
			if(line.getProductCode() == ProductEnum.DEMO) {
				switch(DataUtil.nvl(line.getDepartment(), "NO-DEPARTMENT")) {
					case "Business Development Managers":
					case "Client Partnership Managers":
					case "Marketing":
					    index = 0;
						break;
					default:
					    index = 1;
						break;
				}
			} else if(DataUtil.nvl(line.getGlCode(), "NO-GL-CODE").equals(SALES_TAX_GL_CODE)) {
				index = 2;				
			} else if(((InvoiceLineVO)line).isRebillableLicenseFee()) {
				index = 3;				
			} else {
				index = 4;				
			}		

		}
 
		return index;
	}
}
