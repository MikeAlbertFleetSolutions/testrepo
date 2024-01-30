package com.mikealbert.accounting.processor.enumeration;

public enum BillingReportTypeEnum {
    MAINTENANCE("FLINR501", "FLMAINT", "4"),
    MISCELLANEOUS("FLINR510", "FLMISC", "2"),
    PURCHASE("FLINR512", "AM_LC", "3"),	
    RENTAL("FLINR500", "FLBILLING", "1");

    String reportName;
    String maType;
    String internalId;

    private BillingReportTypeEnum(String reportName, String maType, String internalId) {
        this.reportName = reportName;
        this.maType = maType;
        this.internalId = internalId;
    }

	static public BillingReportTypeEnum getByReportName(String value) {
		for(BillingReportTypeEnum billingReportTypeEnum : values()) {
			if(billingReportTypeEnum.getReportName().equals(value.toUpperCase())) {
				return billingReportTypeEnum;
			}
		}
		throw new IllegalArgumentException("Unknown report name: " + value);
	}  
    
	static public BillingReportTypeEnum getByMaType(String value) {
		for(BillingReportTypeEnum billingReportTypeEnum : values()) {
			if(billingReportTypeEnum.getMaType().equals(value.toUpperCase())) {
				return billingReportTypeEnum;
			}
		}
		throw new IllegalArgumentException("Unknown MA Type: " + value);
	}     

	static public BillingReportTypeEnum getByInternalId(String value) {
		for(BillingReportTypeEnum billingReportTypeEnum : values()) {
			if(billingReportTypeEnum.getInternalId().equals(value.toUpperCase())) {
				return billingReportTypeEnum;
			}
		}
		throw new IllegalArgumentException("Unknown Internal Id: " + value);
	}      

    public String getReportName() {
        return reportName;
    }

    public String getMaType() {
        return maType;
    }    
    
    public String getInternalId() {
        return internalId;
    }        
}
