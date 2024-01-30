package com.mikealbert.accounting.processor.processor;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.client.suitetalk.AssetSuiteTalkService;
import com.mikealbert.accounting.processor.vo.AssetCancelPoToStockVO;
import com.mikealbert.accounting.processor.vo.AssetCreateVO;
import com.mikealbert.accounting.processor.vo.AssetDisposalVO;
import com.mikealbert.accounting.processor.vo.AssetPlaceInServiceVO;
import com.mikealbert.accounting.processor.vo.AssetRevalueVO;
import com.mikealbert.accounting.processor.vo.AssetTypeUpdateVO;
import com.mikealbert.constant.accounting.enumeration.AssetQueueSelectorEnum;
import com.mikealbert.constant.accounting.enumeration.CommonCustomHeader;

@Component("assetSuiteTalkWillowToNsProcessor")
public class AssetSuiteTalkWillowToNsProcessor implements Processor {
	private static final Logger LOG = LogManager.getLogger(AssetSuiteTalkWillowToNsProcessor.class);
	
	@Resource AssetSuiteTalkService assetSuiteTalkService;
			
	@Override
	public void process(Exchange ex) throws Exception {		
        LOG.info(ex.getExchangeId() + "Start AssetSuiteTalkWillowToNsProcessor... ");
        String actionType = (String)ex.getMessage().getHeader(CommonCustomHeader.ACTION);
		String response = "";
		        
        if (AssetQueueSelectorEnum.PLACEINSERVICE.getName().equals(actionType)) {
        	AssetPlaceInServiceVO assetVO = (AssetPlaceInServiceVO)ex.getMessage().getBody();
            LOG.info(ex.getExchangeId() + String.format(" AssetSuiteTalkWillowToNsProcessor AssetId: %s ActionType: %s", assetVO.getAssetId(), actionType));
        	response = assetSuiteTalkService.putAssetPlaceInServiceRecord(assetVO);
        }
        else if (AssetQueueSelectorEnum.CREATE.getName().equals(actionType)) {
        	AssetCreateVO assetVO = (AssetCreateVO)ex.getMessage().getBody();
            LOG.info(ex.getExchangeId() + String.format(" AssetSuiteTalkWillowToNsProcessor AssetId: %s ActionType: %s", assetVO.getAssetId(), actionType));
        	response = assetSuiteTalkService.putAssetCreateRecord(assetVO);
        }
        else if (AssetQueueSelectorEnum.UPDATE_TYPE.getName().equals(actionType)) {
        	AssetTypeUpdateVO assetVO = (AssetTypeUpdateVO)ex.getMessage().getBody();
            LOG.info(ex.getExchangeId() + String.format(" AssetSuiteTalkWillowToNsProcessor AssetId: %s ActionType: %s", assetVO.getAssetId(), actionType));
        	response = assetSuiteTalkService.putAssetTypeUpdateRecord(assetVO);
        }
        else if (AssetQueueSelectorEnum.CANCEL_PO_TO_STOCK.getName().equals(actionType)) {
        	AssetCancelPoToStockVO assetVO = (AssetCancelPoToStockVO)ex.getMessage().getBody();
            LOG.info(ex.getExchangeId() + String.format(" AssetSuiteTalkWillowToNsProcessor AssetId: %s ActionType: %s", assetVO.getAssetId(), actionType));
        	response = assetSuiteTalkService.cancelPoToStock(assetVO);
        }
        else if (AssetQueueSelectorEnum.DISPOSE.getName().equals(actionType)) {
        	AssetDisposalVO assetVO = (AssetDisposalVO)ex.getMessage().getBody();
            LOG.info(ex.getExchangeId() + String.format(" AssetSuiteTalkWillowToNsProcessor AssetId: %s ActionType: %s", assetVO.getAssetId(), actionType));
        	response = assetSuiteTalkService.dispose(assetVO);
        }
        else if (AssetQueueSelectorEnum.REVALUE.getName().equals(actionType)) {
        	AssetRevalueVO assetVO = (AssetRevalueVO)ex.getMessage().getBody();
            LOG.info(ex.getExchangeId() + String.format(" AssetSuiteTalkWillowToNsProcessor AssetId: %s ActionType: %s", assetVO.getAssetId(), actionType));
        	response = assetSuiteTalkService.revalue(assetVO);
        }
                
		ex.getIn().setBody(response);
	}
}