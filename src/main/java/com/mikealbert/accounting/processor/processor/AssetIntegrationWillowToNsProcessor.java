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

@Component("assetIntegrationWillowToNsProcessor")
public class AssetIntegrationWillowToNsProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(AssetIntegrationWillowToNsProcessor.class);
	
	@Resource AssetIntegrationService assetIntegrationService;
			
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start AssetIntegrationWillowToNsProcessor... ");
		String message = (String)ex.getMessage().getBody();
		String actionType = (String)ex.getMessage().getHeader(CommonCustomHeader.ACTION);

        ObjectMapper mapper = new ObjectMapper();
		
        
        if (AssetQueueSelectorEnum.PLACEINSERVICE.getName().equals(actionType)) {
        	AssetPlaceInServiceVO assetVO = mapper.readValue(message, AssetPlaceInServiceVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetIntegrationWillowToNsProcessor ActionType: %s AssetId: %s", 
            		actionType, assetVO.getAssetId()));
        	assetVO = assetIntegrationService.getAssetPlaceInServiceRecord(assetVO);
        	ex.getIn().setBody(assetVO);
            LOG.info(ex.getExchangeId() + " PUT AssetIntegrationWillowToNsProcessor: " + assetVO.toString());
        }
        
        else if (AssetQueueSelectorEnum.CREATE.getName().equals(actionType)) {
            AssetCreateVO assetVO = mapper.readValue(message, AssetCreateVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetIntegrationWillowToNsProcessor ActionType: %s AssetId: %s DocId: %s LineId: %s", 
            		actionType, assetVO.getAssetId(), assetVO.getInvoiceArDocId(), assetVO.getInvoiceArLineId()));
        	assetVO = assetIntegrationService.getAssetCreateRecord(assetVO);
        	ex.getIn().setBody(assetVO);
            LOG.info(ex.getExchangeId() + " PUT AssetIntegrationWillowToNsProcessor: " + assetVO.toString());
        }
        
        else if (AssetQueueSelectorEnum.UPDATE_TYPE.getName().equals(actionType)) {
            AssetTypeUpdateVO assetVO = mapper.readValue(message, AssetTypeUpdateVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetIntegrationWillowToNsProcessor ActionType: %s AssetId: %s", 
            		actionType, assetVO.getAssetId()));
        	assetVO = assetIntegrationService.getAssetTypeUpdate(assetVO);
        	ex.getIn().setBody(assetVO);
            LOG.info(ex.getExchangeId() + " PUT AssetIntegrationWillowToNsProcessor: " + assetVO.toString());
        }
        
        else if (AssetQueueSelectorEnum.CANCEL_PO_TO_STOCK.getName().equals(actionType)) {
            AssetCancelPoToStockVO assetVO = mapper.readValue(message, AssetCancelPoToStockVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetIntegrationWillowToNsProcessor ActionType: %s AssetId: %s", 
            		actionType, assetVO.getAssetId()));
            assetVO = assetIntegrationService.cancelPoToStock(assetVO);
        	ex.getIn().setBody(assetVO);
            LOG.info(ex.getExchangeId() + " PUT AssetIntegrationWillowToNsProcessor: " + assetVO.toString());
        }
		
        else if (AssetQueueSelectorEnum.DISPOSE.getName().equals(actionType)) {
            AssetDisposalVO assetVO = mapper.readValue(message, AssetDisposalVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetIntegrationWillowToNsProcessor ActionType: %s AssetId: %s", 
            		actionType, assetVO.getAssetId()));
            assetVO = assetIntegrationService.dispose(assetVO);
        	ex.getIn().setBody(assetVO);
            LOG.info(ex.getExchangeId() + " PUT AssetIntegrationWillowToNsProcessor: " + assetVO.toString());
        }
		
        else if (AssetQueueSelectorEnum.REVALUE.getName().equals(actionType)) {
            AssetRevalueVO assetVO = mapper.readValue(message, AssetRevalueVO.class);
            LOG.info(ex.getExchangeId() + String.format(" AssetIntegrationWillowToNsProcessor ActionType: %s AssetId: %s", 
            		actionType, assetVO.getAssetId()));
            assetVO = assetIntegrationService.revalue(assetVO);
        	ex.getIn().setBody(assetVO);
            LOG.info(ex.getExchangeId() + " PUT AssetIntegrationWillowToNsProcessor: " + assetVO.toString());
        }

	}
}