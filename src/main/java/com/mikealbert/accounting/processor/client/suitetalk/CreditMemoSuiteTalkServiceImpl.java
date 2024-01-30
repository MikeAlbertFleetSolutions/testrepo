package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mikealbert.accounting.processor.client.suiteanalytics.ItemSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suiteanalytics.SuiteAnalyticsCacheService;
import com.mikealbert.accounting.processor.enumeration.ClientCreditMemoFieldEnum;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.enumeration.TransactionFieldEnum;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoLineVO;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;
import com.mikealbert.constant.enumeration.ApplicationEnum;
import com.mikealbert.util.data.DataUtil;
import com.mikealbert.util.data.DateUtil;
import com.netsuite.webservices.platform.core_2023_2.BooleanCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.DateCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.LongCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.faults_2023_2.types.StatusDetailCodeType;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.transactions.customers_2023_2.CreditMemo;
import com.netsuite.webservices.transactions.customers_2023_2.CreditMemoItem;
import com.netsuite.webservices.transactions.customers_2023_2.CreditMemoItemList;

@Service("creditMemoSuiteTalkService")
public class CreditMemoSuiteTalkServiceImpl extends BaseSuiteTalkService implements CreditMemoSuiteTalkService {
	@Resource SuiteAnalyticsCacheService suiteAnalyticsCacheService;

	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Override
	public ClientCreditMemoVO get(String internalId, String externalId) throws Exception {       
		CreditMemo creditMemo = getCreditMemo(internalId, externalId);

		ClientCreditMemoVO clientCreditMemoVO = convertToCreditMemoVO(creditMemo);

		if(clientCreditMemoVO != null) {
			clientCreditMemoVO.setLines(loadLines(creditMemo));
			clientCreditMemoVO = resolveDocLineId(clientCreditMemoVO);
		}

		return clientCreditMemoVO;
	}

	@Override
	public void create(ClientCreditMemoVO clientCreditMemoVO) throws Exception {
		WriteResponse response = super.service.getService().add(convertToCreateCreditMemo(clientCreditMemoVO));
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Failed to create credit memo with internalId = %s, externalId = %s .", clientCreditMemoVO.getInternalId(), clientCreditMemoVO.getExternalId()), response);
		}	
	}

	@Override
	public void delete(ClientCreditMemoVO clientCreditMemoVO) throws Exception {
		RecordRef recordRef = convertToDeleteCreditMemo(clientCreditMemoVO);
		
		WriteResponse response = service.getService().delete(recordRef, null);		
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail()[0].getCode().equals(StatusDetailCodeType.SSS_MISSING_REQD_ARGUMENT)) {			
				LOG.warn("Delete Credit Memo handled error from accounting system: ExternalId {} - {} ", clientCreditMemoVO, response.getStatus().getStatusDetail()[0].getMessage());			
			} else {
				throw new SuiteTalkException("Error deleting credit memo", response);
			}
		}	
	}

	//TODO Generalize
	private String lookupItemInternalId(ClientCreditMemoLineVO line) throws Exception {
		String internalId = null;

		try {
			internalId = (String)suiteAnalyticsCacheService.getItem(line.getItem()).get(ItemSuiteAnalyticsService.INTERNAL_ID); //TODO This a temporary fix. Item should be retrieved from API.
			if(internalId == null) {
				throw new Exception("Item does not exist");
			}
		} catch(Exception e) {
			throw new RemoteException(String.format("Error occurred in while searching for item %s in the external system", line.getItem()), e);			
		}

		return internalId;
	}	

	private CreditMemo getCreditMemo(String internalId, String externalId) throws Exception {
		ReadResponse readResponse;

		RecordRef recRef = new RecordRef();
		recRef.setInternalId(StringUtils.hasText(internalId) ? internalId : null);
		recRef.setExternalId(StringUtils.hasText(externalId) ? externalId : null);
		recRef.setType(RecordType.creditMemo);

		readResponse = super.service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Failed to retrieve customer credit memo with internalId = %s, externalId = %s .", internalId, externalId), readResponse);
		}	
		
		return (CreditMemo) readResponse.getRecord();			
	}

	private ClientCreditMemoVO convertToCreditMemoVO(CreditMemo creditMemo) {
		String docId = super.getCustomFieldValue(creditMemo.getCustomFieldList(), ClientCreditMemoFieldEnum.DOC_ID); 
		String docLineId = super.getCustomFieldValue(creditMemo.getCustomFieldList(), ClientCreditMemoFieldEnum.DOC_LINE_ID);

		ClientCreditMemoVO clientCreditMemoVO = new ClientCreditMemoVO();
		clientCreditMemoVO.setInternalId(creditMemo.getInternalId());
		clientCreditMemoVO.setExternalId(creditMemo.getExternalId());
		clientCreditMemoVO.setTranId(creditMemo.getTranId());
		clientCreditMemoVO.setGrouped(DataUtil.convertToBoolean(super.getCustomFieldValue(creditMemo.getCustomFieldList(), ClientCreditMemoFieldEnum.GROUP_INVOICE)));
		clientCreditMemoVO.setGroupNumber(super.getCustomFieldValue(creditMemo.getCustomFieldList(), ClientCreditMemoFieldEnum.GROUP_NUMBER));
		clientCreditMemoVO.setDocId(docId == null ? null : Long.valueOf(docId));
		clientCreditMemoVO.setDocLineId(docLineId == null ? null : Long.valueOf(docLineId));
		clientCreditMemoVO.setApplied(creditMemo.getApplied() == null ? BigDecimal.ZERO : BigDecimal.valueOf(creditMemo.getApplied()));
		clientCreditMemoVO.setTotal(creditMemo.getTotal() == null ? BigDecimal.ZERO : BigDecimal.valueOf(creditMemo.getTotal()));
		clientCreditMemoVO.setOrigin(creditMemo.getExternalId() == null ? ApplicationEnum.NETSUITE : ApplicationEnum.WILLOW);

		return clientCreditMemoVO;
	}

	private CreditMemo convertToCreateCreditMemo(ClientCreditMemoVO clientCreditMemoVO) throws Exception {
		List<Object> customFields = new ArrayList<>(0);
		List<Object> itemCustomFields = new ArrayList<>(0);
		
		RecordRef customerRef = new RecordRef();
		customerRef.setType(RecordType.customer);
		customerRef.setExternalId(clientCreditMemoVO.getClientExternalId());

		CreditMemo creditMemo = new CreditMemo();
		creditMemo.setExternalId(clientCreditMemoVO.getExternalId());
		creditMemo.setEntity(customerRef);

		if(clientCreditMemoVO.getDocId() != null) {
			StringCustomFieldRef docId = new StringCustomFieldRef();
			docId.setScriptId(ClientCreditMemoFieldEnum.DOC_ID.getScriptId());
			docId.setValue(clientCreditMemoVO.getDocId().toString());
			customFields.add(docId);
		}
		
		if(clientCreditMemoVO.getDocLineId() != null) {
			StringCustomFieldRef docLineId = new StringCustomFieldRef();
			docLineId.setScriptId(ClientCreditMemoFieldEnum.DOC_LINE_ID.getScriptId());
			docLineId.setValue(clientCreditMemoVO.getDocLineId().toString());
			customFields.add(docLineId);
		}	

		if(clientCreditMemoVO.isGrouped()) {
			BooleanCustomFieldRef groupInvoice = new BooleanCustomFieldRef();
			groupInvoice.setScriptId(ClientCreditMemoFieldEnum.GROUP_INVOICE.getScriptId());
			groupInvoice.setValue(clientCreditMemoVO.isGrouped());
			customFields.add(groupInvoice);
		}
		if(clientCreditMemoVO.getGroupNumber() != null) {
			StringCustomFieldRef groupNumber = new StringCustomFieldRef();
			groupNumber.setScriptId(ClientCreditMemoFieldEnum.GROUP_NUMBER.getScriptId());
			groupNumber.setValue(clientCreditMemoVO.getGroupNumber());
			customFields.add(groupNumber);			
		}

		creditMemo.setCustomFieldList(new CustomFieldList(customFields.toArray(CustomFieldRef[]::new)));  

		List<CreditMemoItem> creditMemoItems = new ArrayList<>(0);
		for(ClientCreditMemoLineVO line : clientCreditMemoVO.getLines()) {

			CreditMemoItem item = new CreditMemoItem();						
			item.setItem(new RecordRef(null, lookupItemInternalId(line), null, RecordType.inventoryItem));
			item.setRate(line.getRate().toString());
			item.setDescription(line.getDescription());			
			item.setQuantity(line.getQuantity() == null ? null : line.getQuantity().doubleValue());				

			if(line.getDocId() != null) {
				LongCustomFieldRef lineDocId = new LongCustomFieldRef();
				lineDocId.setScriptId(ClientCreditMemoFieldEnum.MA_DOC_ID.getScriptId());
				lineDocId.setValue(line.getDocId());
				itemCustomFields.add(lineDocId);
			}

			if(line.getDocLineId() != null) {
				LongCustomFieldRef lineDocLineId = new LongCustomFieldRef();
				lineDocLineId.setScriptId(ClientCreditMemoFieldEnum.MA_LINE_ID.getScriptId());
				lineDocLineId.setValue(line.getDocLineId());
				itemCustomFields.add(lineDocLineId);
			}

			if(line.getMonthServiceDate() != null) {
				DateCustomFieldRef monthServiceDate = new DateCustomFieldRef();
				monthServiceDate.setScriptId(TransactionFieldEnum.MONTH_SERVICE_DATE.getScriptId());
				monthServiceDate.setValue(DateUtil.convertToCalendar(line.getMonthServiceDate()));
				itemCustomFields.add(monthServiceDate);
			}

			if(line.getTransactionLineDate() != null) {
				DateCustomFieldRef transactionLineDate = new DateCustomFieldRef();
				transactionLineDate.setScriptId(TransactionFieldEnum.MA_TRANSACTION_DATE.getScriptId());
				transactionLineDate.setValue(DateUtil.convertToCalendar(line.getTransactionLineDate()));
				itemCustomFields.add(transactionLineDate);
			}
					
			if(line.getDriverId() != null) {
				LongCustomFieldRef driverid = new LongCustomFieldRef();
				driverid.setScriptId(TransactionFieldEnum.DRIVER_ID.getScriptId());
				driverid.setValue(line.getDriverId());
				itemCustomFields.add(driverid);
			}

			if(line.getDriverFirstName() != null) {
				StringCustomFieldRef driverFirstName = new StringCustomFieldRef();
				driverFirstName.setScriptId(TransactionFieldEnum.DRIVER_FIRST_NAME.getScriptId());
				driverFirstName.setValue(line.getDriverFirstName());
				itemCustomFields.add(driverFirstName);
			}

			if(line.getDriverLastName() != null) {
				StringCustomFieldRef driverLastName = new StringCustomFieldRef();
				driverLastName.setScriptId(TransactionFieldEnum.DRIVER_LAST_NAME.getScriptId());
				driverLastName.setValue(line.getDriverLastName());
				itemCustomFields.add(driverLastName);
			}

			if(line.getDriverState() != null) {
				StringCustomFieldRef driverState = new StringCustomFieldRef();
				driverState.setScriptId(TransactionFieldEnum.DRIVER_STATE.getScriptId());
				driverState.setValue(line.getDriverState());
				itemCustomFields.add(driverState);
			}

			if(line.getDriverCostCenter() != null) {
				StringCustomFieldRef driverCostCenter = new StringCustomFieldRef();
				driverCostCenter.setScriptId(TransactionFieldEnum.DRIVER_COST_CENTER.getScriptId());
				driverCostCenter.setValue(line.getDriverCostCenter());
				itemCustomFields.add(driverCostCenter);
			}

			if(line.getDriverCostCenterDescription() != null) {
				StringCustomFieldRef driverCostCenterDesc = new StringCustomFieldRef();
				driverCostCenterDesc.setScriptId(TransactionFieldEnum.DRIVER_COST_CENTER_DESCRIPTION.getScriptId());
				driverCostCenterDesc.setValue(line.getDriverCostCenterDescription());
				itemCustomFields.add(driverCostCenterDesc);
			}

			if(line.getDriverRechargeCode() != null) {
				StringCustomFieldRef driverRecharge = new StringCustomFieldRef();
				driverRecharge.setScriptId(TransactionFieldEnum.DRIVER_RECHARGE_CODE.getScriptId());
				driverRecharge.setValue(line.getDriverRechargeCode());
				itemCustomFields.add(driverRecharge);
			}

			if(line.getDriverFleetRefNo() != null) {
				StringCustomFieldRef driverFleetRefNo= new StringCustomFieldRef();
				driverFleetRefNo.setScriptId(TransactionFieldEnum.DRIVER_FLEET_REF_NO.getScriptId());
				driverFleetRefNo.setValue(line.getDriverFleetRefNo());
				itemCustomFields.add(driverFleetRefNo);
			}
						
			item.setCustomFieldList(new CustomFieldList(itemCustomFields.toArray(CustomFieldRef[]::new)));

			creditMemoItems.add(item);
		}		
		creditMemo.setItemList(new CreditMemoItemList(creditMemoItems.toArray(CreditMemoItem[]::new), true));
		return creditMemo;
	}	

	private RecordRef convertToDeleteCreditMemo(ClientCreditMemoVO clientCreditMemoVO) {
		RecordRef recordRef = new RecordRef(null, clientCreditMemoVO.getInternalId(), clientCreditMemoVO.getExternalId(), RecordType.creditMemo);
		return recordRef;
	}
	
	private List<ClientCreditMemoLineVO> loadLines(CreditMemo creditMemo) throws Exception {
		List<ClientCreditMemoLineVO> lines = new ArrayList<>(0);

		if(creditMemo.getItemList() == null) return lines;

		for(CreditMemoItem item : creditMemo.getItemList().getItem()) {

			String docIdString = super.getCustomFieldValue(item.getCustomFieldList(), ClientCreditMemoFieldEnum.MA_DOC_ID);
			Long docId = docIdString == null ? null : Long.valueOf(docIdString);
	
			String docLineIdString = super.getCustomFieldValue(item.getCustomFieldList(), ClientCreditMemoFieldEnum.MA_LINE_ID);
			Long docLineId = docLineIdString == null ? null : Long.valueOf(docLineIdString);

			String description = super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.INVOICE_DESCRIPTION);
			if(description == null) {
				description = item.getDescription();
			} 
			
			String driverIdString = super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.DRIVER_ID);
			Long driverId = driverIdString == null ? null : Long.valueOf(driverIdString);
			
			ClientCreditMemoLineVO line = new ClientCreditMemoLineVO()
			    .setItem(item.getItem().getName())
				.setUnit(super.getFieldValueName(super.getCustomFieldRef(CustomSegmentEnum.UNIT_NO.getScriptId(), item.getCustomFieldList())))
				.setDescription(description)
				.setQuantity(item.getQuantity() == null ? BigDecimal.ZERO : new BigDecimal(item.getQuantity()))
				.setRate(item.getRate() == null ? BigDecimal.ZERO : new BigDecimal(item.getRate()))
				.setDocId(docId)
				.setDocLineId(docLineId)
				.setDriverId(driverId)
				.setDriverFirstName(super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.DRIVER_FIRST_NAME))
				.setDriverLastName(super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.DRIVER_LAST_NAME))
				.setDriverState(super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.DRIVER_STATE))
				.setDriverCostCenter(super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.DRIVER_COST_CENTER))
				.setDriverCostCenterDescription(super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.DRIVER_COST_CENTER_DESCRIPTION))
				.setDriverRechargeCode(super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.DRIVER_RECHARGE_CODE))
				.setDriverFleetRefNo(super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.DRIVER_FLEET_REF_NO));
			
			lines.add(line);
		}

		return lines;
	}

	/**
	 * This function is temporary in that for backwards compatibility between releases we need 
	 * to set the header doc line id with the line's doc line id when avaialable. 
	 * Otherwise, the header doc id is used. Having doc line id at the header is old and 
	 * will go away once we release phase 4.1.
	 * @param clientCreditMemoVO
	 * @return ClientCreditMemoVO
	*/
	private ClientCreditMemoVO resolveDocLineId(ClientCreditMemoVO clientCreditMemoVO) {
		if(!clientCreditMemoVO.getLines().isEmpty()
		        && clientCreditMemoVO.getLines().get(0).getDocLineId() != null) {
			clientCreditMemoVO.setDocLineId(clientCreditMemoVO.getLines().get(0).getDocLineId());
		}

		return clientCreditMemoVO;
	}

}