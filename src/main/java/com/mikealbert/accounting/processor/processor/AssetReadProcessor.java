package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.service.AssetIntegrationService;
import com.mikealbert.accounting.processor.vo.AssetCancelPoToStockVO;
import com.mikealbert.accounting.processor.vo.AssetCreateVO;
import com.mikealbert.accounting.processor.vo.AssetDisposalVO;
import com.mikealbert.accounting.processor.vo.AssetPlaceInServiceVO;
import com.mikealbert.accounting.processor.vo.AssetRevalueVO;
import com.mikealbert.accounting.processor.vo.AssetTypeUpdateVO;
import com.mikealbert.constant.accounting.enumeration.AssetQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.CommonCustomHeader;

@Component("assetReadProcessor")
public class AssetReadProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(AssetReadProcessor.class);
	
	@Resource AssetIntegrationService assetIntegrationService;
			
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start AssetReadProcessor... ");
		String message = (String)ex.getMessage().getBody();
		String action = (String)ex.getMessage().getHeader(CommonCustomHeader.ACTION);
		String response = "";

        ObjectMapper mapper = new ObjectMapper();
		
        
        if (AssetQueueSelectorEnum.PLACEINSERVICE.getName().equals(action)) {
        	AssetPlaceInServiceVO assetVO = mapper.readValue(message, AssetPlaceInServiceVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetReadProcessor action: %s AssetId: %s", 
            		action, assetVO.getAssetId()));
        	assetVO = assetIntegrationService.getAssetPlaceInServiceRecord(assetVO);
    		response = mapper.writeValueAsString(assetVO);
        }
        
        else if (AssetQueueSelectorEnum.CREATE.getName().equals(action)) {
            AssetCreateVO assetVO = mapper.readValue(message, AssetCreateVO.class);
            LOG.info(ex.getExchangeId() + String.format(" %s action: %s AssetId: %s DocId: %s LineId: %s", 
            		this.getClass().getName(),  action, assetVO.getAssetId(), assetVO.getInvoiceArDocId(), assetVO.getInvoiceArLineId()));
        	assetVO = assetIntegrationService.getAssetCreateRecord(assetVO);
    		response = mapper.writeValueAsString(assetVO);
        }
        
        else if (AssetQueueSelectorEnum.UPDATE_TYPE.getName().equals(action)) {
            AssetTypeUpdateVO assetVO = mapper.readValue(message, AssetTypeUpdateVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetReadProcessor action: %s AssetId: %s", 
            		action, assetVO.getAssetId()));
        	assetVO = assetIntegrationService.getAssetTypeUpdate(assetVO);
    		response = mapper.writeValueAsString(assetVO);
        }
        
        else if (AssetQueueSelectorEnum.CANCEL_PO_TO_STOCK.getName().equals(action)) {
            AssetCancelPoToStockVO assetVO = mapper.readValue(message, AssetCancelPoToStockVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetReadProcessor action: %s AssetId: %s", 
            		action, assetVO.getAssetId()));
            assetVO = assetIntegrationService.cancelPoToStock(assetVO);
    		response = mapper.writeValueAsString(assetVO);
        }
		
        else if (AssetQueueSelectorEnum.DISPOSE.getName().equals(action)) {
            AssetDisposalVO assetVO = mapper.readValue(message, AssetDisposalVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetReadProcessor action: %s AssetId: %s", 
            		action, assetVO.getAssetId()));
            assetVO = assetIntegrationService.dispose(assetVO);
    		response = mapper.writeValueAsString(assetVO);
        }
        
        else if (AssetQueueSelectorEnum.REVALUE.getName().equals(action)) {
            AssetRevalueVO assetVO = mapper.readValue(message, AssetRevalueVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetReadProcessor action: %s AssetId: %s revalueContext: %s", action, assetVO.getAssetId(), assetVO.getRevalueContext()));
            assetVO = assetIntegrationService.revalue(assetVO);
    		response = mapper.writeValueAsString(assetVO);
        }        
    	ex.getIn().setBody(response);
        LOG.info(String.format("%s %s: %s", ex.getExchangeId(), this.getClass().getName(), response));

	}
}
