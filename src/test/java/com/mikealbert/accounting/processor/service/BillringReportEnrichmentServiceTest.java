package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.client.suitetalk.SuiteTalkCacheService;
import com.mikealbert.accounting.processor.vo.BillingReportTransactionVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;

@SpringBootTest
@DisplayName("Given transactions")
public class BillringReportEnrichmentServiceTest {
    @Resource BillingReportEnrichmentService billingReportEnrichmentService;

    @MockBean ServiceCache serviceCache;
    @MockBean SuiteTalkCacheService suiteTalkCacheService;

    static final String INTERNAL_ID = "0";
    static final String EXTERNAL_ID = "0";
    static final String NAME = "NAME";
    static final Date TRAN_DATE = new Date();
    static final Date MA_TRAN_DATE = new Date();    
    static String LEASE_TYPE_CE = "Closed End Lease Type";
    static final String STATE_LONG_NAME = "Ohio";
    static final String STATE_SHORT_NAME = "OH";

    @Test
    @DisplayName("when enriching transactions with driver info, the transactions' driver fields are populated")
    public void testEnrichWithDriverInfo() throws Exception {
        List<Map<String, Object>> mockStates = new ArrayList<>(0);

        Map<String, Object> mockState = new HashMap<>();
        mockState.put("fullName", "Ohio");
        mockState.put("shortname", "OH");
        mockStates.add(mockState);

        List<BillingReportTransactionVO> mockTransactionVOs = new ArrayList<>(0);
        mockTransactionVOs.add(
            new BillingReportTransactionVO()
                .setUnitInternalId(INTERNAL_ID)
                .setTranDate(TRAN_DATE)
                .setMaTransactionDate(MA_TRAN_DATE)
        );

        DriverUnitHistoryVO mockDriverUnitHistoryVO = new DriverUnitHistoryVO()
            .setDriverId(0L)
            .setDriverFirstName("Driver First Name")
            .setDriverLastName("Driver Last Name")
            .setDriverRechargeCode("driverRechargeCode")
            .setCostCenterCode("costCenterCode")
            .setCostCenterDescription("costCenterDescription")
            .setDriverAddressState(STATE_LONG_NAME);

        String expectedDriverName = String.format("%s, %s", mockDriverUnitHistoryVO.getDriverLastName(), mockDriverUnitHistoryVO.getDriverFirstName().substring(0, 1));            
                    
        when(serviceCache.findDuhByUnitInternalIdAndDate(any(), any())).thenReturn(mockDriverUnitHistoryVO);
        when(suiteTalkCacheService.getStates()).thenReturn(mockStates);

        List<BillingReportTransactionVO> actualTransactionVOs = billingReportEnrichmentService.enrichWithDriverInfo(mockTransactionVOs);

        actualTransactionVOs.stream()
            .forEach(txn -> {
                assertEquals(mockDriverUnitHistoryVO.getDriverId(), txn.getDriverId());
                assertEquals(expectedDriverName, txn.getDriverName());
                assertEquals(mockDriverUnitHistoryVO.getCostCenterCode(), txn.getDriverCostCenterCode());
                assertEquals(mockDriverUnitHistoryVO.getCostCenterDescription(), txn.getDriverCostCenterDescription());                
                assertEquals(mockDriverUnitHistoryVO.getDriverRechargeCode(), txn.getRechargeCode());
                assertEquals(STATE_SHORT_NAME, txn.getDriverAddressState());

            });

        verify(serviceCache, times(1)).findDuhByUnitInternalIdAndDate(eq(INTERNAL_ID), eq(TRAN_DATE));
    }   

    @Test
    @DisplayName("when enriching transactions that already have driver details, the existing transactions' driver details are not overwritten")
    public void testDoNotEnrichWithDriverInfo() throws Exception {
        List<BillingReportTransactionVO> mockTransactionVOs = new ArrayList<>(0);
        mockTransactionVOs.add(
            new BillingReportTransactionVO()
                .setUnitInternalId(INTERNAL_ID)
                .setTranDate(TRAN_DATE)
                .setMaTransactionDate(MA_TRAN_DATE)
                .setDriverId(-1L)
        );
                    
        List<BillingReportTransactionVO> actualTransactionVOs = billingReportEnrichmentService.enrichWithDriverInfo(mockTransactionVOs);

        assertEquals(mockTransactionVOs, actualTransactionVOs);

        verify(serviceCache, times(0)).findDuhByUnitInternalIdAndDate(any(), any());
    }       
}
