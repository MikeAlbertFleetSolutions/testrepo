package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class CapitalCostVO implements Serializable {
    private BigDecimal totalCostToPlaceInServiceDeal;
    private BigDecimal totalCostToPlaceInServiceCustomer;
    
    public CapitalCostVO() {}

    public BigDecimal getTotalCostToPlaceInServiceDeal() {
        return totalCostToPlaceInServiceDeal;
    }

    public CapitalCostVO setTotalCostToPlaceInServiceDeal(BigDecimal totalCostToPlaceInServiceDeal) {
        this.totalCostToPlaceInServiceDeal = totalCostToPlaceInServiceDeal;
        return this;
    }

    public BigDecimal getTotalCostToPlaceInServiceCustomer() {
        return totalCostToPlaceInServiceCustomer;
    }

    public CapitalCostVO setTotalCostToPlaceInServiceCustomer(BigDecimal totalCostToPlaceInServiceCustomer) {
        this.totalCostToPlaceInServiceCustomer = totalCostToPlaceInServiceCustomer;
        return this;
    }
    
    @Override
    public String toString() {
        return "CapitalCostVO [totalCostToPlaceInServiceDeal=" + totalCostToPlaceInServiceDeal
                + ", totalCostToPlaceInServiceCustomer=" + totalCostToPlaceInServiceCustomer + "]";
    }
}
