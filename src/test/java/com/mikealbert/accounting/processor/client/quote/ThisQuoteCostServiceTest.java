package com.mikealbert.accounting.processor.client.quote;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.annotation.Resource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.CapitalCostVO;

@DisplayName("Given a qmdId")
@SpringBootTest
public class ThisQuoteCostServiceTest extends BaseTest{
    @Resource ThisQuoteCostService thisQuoteCostService;

    static final Long QMD_ID = 848950L;

    @Disabled("Want work from CircleCI")
    @Test
    public void testThisQuoteCost() throws Exception {
        CapitalCostVO cost = thisQuoteCostService.thisQuoteCost(QMD_ID);

        assertNotNull(cost.getTotalCostToPlaceInServiceDeal());
        assertNotNull(cost.getTotalCostToPlaceInServiceCustomer());
    }
    
}
