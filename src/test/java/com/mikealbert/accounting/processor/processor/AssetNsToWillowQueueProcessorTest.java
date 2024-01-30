package com.mikealbert.accounting.processor.processor;

import com.mikealbert.accounting.processor.constants.CommonConstants;
import com.mikealbert.accounting.processor.service.AssetIntegrationService;
import com.mikealbert.accounting.processor.vo.NgAssetVO;
import com.mikealbert.accounting.processor.vo.NgAssetsPerUnitVO;
import com.mikealbert.util.data.DataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Test class for AssetNsToWillowQueueProcessor")
public class AssetNsToWillowQueueProcessorTest {

    @MockBean
    AssetIntegrationService assetIntegrationService;
    @Resource
    AssetNsToWillowQueueProcessor assetNsToWillowQueueProcessor;

    NgAssetVO mockNgAssetVO;
    NgAssetsPerUnitVO ngAssetsPerUnitVO;

    @BeforeEach
    private void generateMockNgAsset()
    {
        ngAssetsPerUnitVO = new NgAssetsPerUnitVO();
        ngAssetsPerUnitVO.setListNgAssetVO(new ArrayList<NgAssetVO>());
        ngAssetsPerUnitVO.setUnitNo("01028491");
        try {
            mockNgAssetVO = new NgAssetVO();
            mockNgAssetVO
                    .setMainVehicle("T")
                    .setNgAssetId(30246l)
                    .setNgAssetExtid(null)
                    .setSubsidiaryId(1l)
                    .setNgAssetName(DataUtil.substr("01028491", 0, 80))
                    .setStatusId(1l)
                    .setTypeId(309l)
                    .setCapitalizedAssetValueAtIn(BigDecimal.valueOf(40000.0))
                    .setAccumulatedDepreciation(BigDecimal.valueOf(0.0))
                    .setResidualValueEstimate(BigDecimal.valueOf(0.0))
                    .setAcquisitionDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020"))
                    .setInserviceDate(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2020"))
                    .setDisposalDate(null)
                    .setSourceTransactionId(183366l)
                    .setUnitName("01028491")
                    .setUsefulLifeAtInservice(36l)
                    .setNgAssetTypeName("Fleet-Open End")
                    .setInvoiceNo("1028491A")
                    .setUpdateControlCode("OE_LTD");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ngAssetsPerUnitVO.getListNgAssetVO().add(mockNgAssetVO);
    }

    @Test
    @DisplayName("Test if the lease type is client purchase")
    public void testIsClientPurchase() {
        when(assetIntegrationService.getLeaseType(anyString())).thenReturn(CommonConstants.CLIENT_PURCHASE);
        boolean isClientPurchase = assetNsToWillowQueueProcessor.isClientPurchase(ngAssetsPerUnitVO);
        assertTrue(isClientPurchase);
    }
}