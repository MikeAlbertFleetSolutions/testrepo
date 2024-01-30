package com.mikealbert.accounting.processor.helper;

import com.mikealbert.accounting.processor.enumeration.BusinessUnitEnum;
import com.mikealbert.constant.enumeration.ProductEnum;

public class BusinessUnitHelper {
	
	static public BusinessUnitEnum resolve(ProductEnum product) {
		BusinessUnitEnum businessUnit;
		
		switch(product) {
		case ST:
			businessUnit = BusinessUnitEnum.RENTAL;
			break;		
		default:
			businessUnit =  BusinessUnitEnum.FLEET_SOLUTIONS;
		}
		
		return businessUnit;
	}
}
