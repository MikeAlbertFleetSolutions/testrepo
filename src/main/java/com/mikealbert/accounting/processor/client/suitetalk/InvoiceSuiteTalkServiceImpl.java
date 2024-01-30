package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mikealbert.accounting.processor.client.suiteanalytics.ItemSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suiteanalytics.SuiteAnalyticsCacheService;
import com.mikealbert.accounting.processor.enumeration.BusinessUnitEnum;
import com.mikealbert.accounting.processor.enumeration.ClientInvoiceFieldEnum;
import com.mikealbert.accounting.processor.enumeration.CustomListEnum;
import com.mikealbert.accounting.processor.enumeration.CustomRecordEnum;
import com.mikealbert.accounting.processor.enumeration.CustomSegmentEnum;
import com.mikealbert.accounting.processor.enumeration.TransactionFieldEnum;
import com.mikealbert.accounting.processor.exception.SuiteTalkDuplicateRecordException;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.service.XRefService;
import com.mikealbert.accounting.processor.vo.ClientInvoiceLineVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceVO;
import com.mikealbert.constant.accounting.enumeration.TransactionTypeEnum;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;
import com.mikealbert.constant.enumeration.ApplicationEnum;
import com.mikealbert.util.data.DataUtil;
import com.mikealbert.util.data.DateUtil;
import com.mikealbert.webservice.suitetalk.enumeration.CustomFormEnum;
import com.netsuite.webservices.platform.common_2023_2.TransactionSearchBasic;
import com.netsuite.webservices.platform.core_2023_2.BooleanCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.DateCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.DoubleCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.InitializeRecord;
import com.netsuite.webservices.platform.core_2023_2.InitializeRef;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.LongCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.SearchEnumMultiSelectField;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectField;
import com.netsuite.webservices.platform.core_2023_2.SelectCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.types.InitializeRefType;
import com.netsuite.webservices.platform.core_2023_2.types.InitializeType;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.core_2023_2.types.SearchEnumMultiSelectFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchMultiSelectFieldOperator;
import com.netsuite.webservices.platform.faults_2023_2.types.StatusDetailCodeType;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.transactions.sales_2023_2.Invoice;
import com.netsuite.webservices.transactions.sales_2023_2.InvoiceItem;
import com.netsuite.webservices.transactions.sales_2023_2.InvoiceItemList;
import com.netsuite.webservices.transactions.sales_2023_2.TransactionSearch;
import com.netsuite.webservices.transactions.sales_2023_2.types.TransactionStatus;
import com.netsuite.webservices.transactions.sales_2023_2.types.TransactionType;

@Service("invoiceSuiteTalkService")
public class InvoiceSuiteTalkServiceImpl extends BaseSuiteTalkService implements InvoiceSuiteTalkService {
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource SuiteAnalyticsCacheService suiteAnalyticsCacheService;
	@Resource CustomerSuiteTalkService customerSuiteTalkService;
	@Resource XRefService xRefService;
		
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public void create(ClientInvoiceVO clientInvoiceVO) throws Exception {
		WriteResponse response = service.getService().add(convertToCreateInvoice(clientInvoiceVO));	
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail(0).getCode() == StatusDetailCodeType.DUP_RCRD) {
				throw new SuiteTalkDuplicateRecordException(String.format("Error creating client invoice. ExternalId: %s", clientInvoiceVO.getExternalId()), response);				
			} else {
				throw new SuiteTalkException(String.format("Error creating client invoice. ExternalId: %s", clientInvoiceVO.getExternalId()), response);
			}
		}
		
		LOG.info("Successfully created the Client Invoice in the external accounting system. Invoice -> {}", clientInvoiceVO);
	}

	@Override
	public ClientInvoiceVO get(String internalId, String externalId) throws Exception {
		Invoice invoice = getCustomerInvoice(internalId, externalId);

		ClientInvoiceVO clientInvoiceVO = convertToClientInvoiceVO(invoice);
		//clientInvoiceVO.setClientExternalId(customerSuiteTalkService.getCustomer(invoice.getEntity().getInternalId(), null).getExternalId()); //TODO Remove this or replace with internalId
		clientInvoiceVO.setLines(loadLines(invoice));

		return clientInvoiceVO;
	}

	@Override
	public void delete(ClientInvoiceVO clientInvoiceVO) throws Exception {
		RecordRef recordRef = new RecordRef(null, clientInvoiceVO.getInternalId(), clientInvoiceVO.getExternalId() == null ? null : clientInvoiceVO.getExternalId().toString(), RecordType.invoice);
		WriteResponse response = service.getService().delete(recordRef, null);
		
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail()[0].getCode().equals(StatusDetailCodeType.SSS_MISSING_REQD_ARGUMENT)) {			
				LOG.warn("Delete Customer Invoice handled error from accounting system: ExternalId {} - {} ", clientInvoiceVO, response.getStatus().getStatusDetail()[0].getMessage());			
			} else {
				throw new SuiteTalkException("Error deleting customer invoice", response);
			}

			
		}		
	}

	@Override
	public List<ClientInvoiceVO> findOustanding(String clientInternalId, String clientExternalId, String maType) throws Exception {
		return super.service.searchX(convertToTransactionSearch(clientInternalId, clientExternalId, maType)).stream()
		    .map(invoice -> {
				try{
				    return convertToClientInvoiceVO((Invoice)invoice);
				}catch(Exception e){
					throw new RuntimeException(e);
				}})
			.collect(Collectors.toList());
	}

	@Override
	public ClientInvoiceVO convertToClientInvoiceVO(Invoice invoice) throws Exception {
		String docId = super.getCustomFieldValue(invoice.getCustomFieldList(), ClientInvoiceFieldEnum.DOC_ID); 
		String docLineId = super.getCustomFieldValue(invoice.getCustomFieldList(), ClientInvoiceFieldEnum.DOC_LINE_ID);

		return new ClientInvoiceVO(invoice.getInternalId(), invoice.getExternalId())
		        .setClientInternalId(invoice.getEntity().getInternalId())
				.setClientExternalId(invoice.getEntity().getExternalId())
		        .setSubsidiary(Long.parseLong(xRefService.getInternalValue(XRefGroupNameEnum.COMPANY, invoice.getSubsidiary().getInternalId())))
				.setType(TransactionTypeEnum.CLIENT_INVOICE)
				.setGrouped(DataUtil.convertToBoolean(super.getCustomFieldValue(invoice.getCustomFieldList(), ClientInvoiceFieldEnum.GROUP_INVOICE)))
				.setGroupNumber(super.getCustomFieldValue(invoice.getCustomFieldList(), ClientInvoiceFieldEnum.GROUP_NUMBER))		
				.setTranId(invoice.getTranId())
				.setTranDate(invoice.getTranDate().getTime())
				.setDueDate(DataUtil.nvl(invoice.getDueDate().getTime(), invoice.getTranDate().getTime()))
				.setMemo(invoice.getMemo())
				.setPayableAccount(invoice.getAccount().getName())
				.setTotal(invoice.getTotal() == null ? BigDecimal.ZERO : new BigDecimal(invoice.getTotal()))
				.setApplied(invoice.getAmountPaid() == null ? BigDecimal.ZERO : new BigDecimal(invoice.getAmountPaid()))
				.setBalance(invoice.getAmountRemaining() == null ? BigDecimal.ZERO : new BigDecimal(invoice.getAmountRemaining()))
				.setClientExternalId(invoice.getEntity().getExternalId())
				.setDaysOverdue(calculateDaysOverdue(invoice.getDueDate().getTime()))
				.setStatus(invoice.getStatus())
				.setDocId(docId == null ? null : Long.valueOf(docId))
				.setDocLineId(docLineId == null ? null : Long.valueOf(docLineId))
				.setMaType(super.getCustomFieldValue(invoice.getCustomFieldList(), ClientInvoiceFieldEnum.MA_TYPE))
				.setAsOfDate(new Date())
				.setOrigin(invoice.getExternalId() == null ? ApplicationEnum.NETSUITE : ApplicationEnum.WILLOW);
	}	

	private Invoice convertToCreateInvoice(ClientInvoiceVO clientInvoiceVO) throws Exception {
		List<CustomFieldRef> invoiceCustomFields = new ArrayList<>(0);
		Invoice invoice = null;

		InitializeRef clientRef = new InitializeRef();
		clientRef.setType(InitializeRefType.customer);
		clientRef.setExternalId(clientInvoiceVO.getClientExternalId());
		
		InitializeRecord invoiceInitRec = new InitializeRecord();
		invoiceInitRec.setType(InitializeType.invoice);
		invoiceInitRec.setReference(clientRef);

		ReadResponse invoiceResponse = service.getService().initialize(invoiceInitRec);
		if(!invoiceResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error initializing Customer Invoice. %s ", clientInvoiceVO), invoiceResponse);
		}

	    invoice = (Invoice)invoiceResponse.getRecord();
		invoice.setExternalId(clientInvoiceVO.getExternalId() == null ? null : clientInvoiceVO.getExternalId().toString());
		invoice.setAccount(new RecordRef(clientInvoiceVO.getPayableAccount(), null, null, RecordType.account));
		invoice.setTranDate(DateUtil.convertToCalendar(clientInvoiceVO.getTranDate()));
		invoice.setDueDate(DateUtil.convertToCalendar(clientInvoiceVO.getDueDate()));
		invoice.setStatus("open");
		invoice.setCustomForm(new RecordRef(null, CustomFormEnum.MA_PRODUCT_INVOICE.getInternalId(), null, null));
			
		BooleanCustomFieldRef groupInvoice = new BooleanCustomFieldRef();
		groupInvoice.setScriptId(ClientInvoiceFieldEnum.GROUP_INVOICE.getScriptId());
		groupInvoice.setValue(clientInvoiceVO.isGrouped());
		invoiceCustomFields.add(groupInvoice);

		BooleanCustomFieldRef autoApprove = new BooleanCustomFieldRef();
		autoApprove.setScriptId(ClientInvoiceFieldEnum.MA_AUTO_APPROVE.getScriptId());
		autoApprove.setValue(clientInvoiceVO.isAutoApprove());
		invoiceCustomFields.add(autoApprove);		

		if(clientInvoiceVO.getDocId() != null){
			StringCustomFieldRef docId = new StringCustomFieldRef();
			docId.setScriptId(ClientInvoiceFieldEnum.DOC_ID.getScriptId());
			docId.setValue(clientInvoiceVO.getDocId().toString());
			invoiceCustomFields.add(docId);
		}

		if(clientInvoiceVO.getDocLineId() != null) {
			StringCustomFieldRef docLineId = new StringCustomFieldRef();
			docLineId.setScriptId(ClientInvoiceFieldEnum.DOC_LINE_ID.getScriptId());
			docLineId.setValue(clientInvoiceVO.getDocLineId().toString());
			invoiceCustomFields.add(docLineId);	
		}
		
		if(clientInvoiceVO.getMaType() != null) {
			SelectCustomFieldRef maType = new SelectCustomFieldRef();
			maType.setScriptId(ClientInvoiceFieldEnum.MA_TYPE.getScriptId());
			maType.setValue(new ListOrRecordRef(null, super.service.searchForCustomListItemId(CustomListEnum.MA_TYPE.getScriptId(), clientInvoiceVO.getMaType()), null, null));		
			invoiceCustomFields.add(maType);	
		}

		if(clientInvoiceVO.getGroupNumber() != null) {
			StringCustomFieldRef groupNumber = new StringCustomFieldRef();
			groupNumber.setScriptId(ClientInvoiceFieldEnum.GROUP_NUMBER.getScriptId());
			groupNumber.setValue(clientInvoiceVO.getGroupNumber());
			invoiceCustomFields.add(groupNumber);			
		}		

		if(clientInvoiceVO.getSkipApproval() != null) {
			SelectCustomFieldRef skipApproval = new SelectCustomFieldRef();
			skipApproval.setScriptId(ClientInvoiceFieldEnum.SKIP_APPROVAL.getScriptId());
			skipApproval.setValue(new ListOrRecordRef(null, super.service.searchForCustomListItemId(CustomListEnum.SKIP_APPROVAL.getScriptId(), clientInvoiceVO.getSkipApproval()), null, null));		
			invoiceCustomFields.add(skipApproval);
		}

		invoice.setCustomFieldList(new CustomFieldList(invoiceCustomFields.toArray(CustomFieldRef[]::new)));  

		List<Object> itemCustomFields;
		List<InvoiceItem> invoiceItems = new ArrayList<>(0);
		for(ClientInvoiceLineVO line : clientInvoiceVO.getLines()) {
			itemCustomFields = new ArrayList<>(0);

			InvoiceItem item = new InvoiceItem();
			item.setItem(new RecordRef(null, lookupItemInternalId(line), null, RecordType.inventoryItem));
			item.setRate(line.getRate().toString());
			item.setQuantity(line.getQuantity() == null ? null : line.getQuantity().doubleValue());	

			if(line.getDocId() != null) {
				LongCustomFieldRef lineDocId = new LongCustomFieldRef();
				lineDocId.setScriptId(ClientInvoiceFieldEnum.MA_DOC_ID.getScriptId());
				lineDocId.setValue(line.getDocId());
				itemCustomFields.add(lineDocId);
			}

			if(line.getDocLineId() != null) {
				LongCustomFieldRef lineDocLineId = new LongCustomFieldRef();
				lineDocLineId.setScriptId(ClientInvoiceFieldEnum.MA_LINE_ID.getScriptId());
				lineDocLineId.setValue(line.getDocLineId());
				itemCustomFields.add(lineDocLineId);
			}

			if(line.getLinePaidAmount() != null) {
				DoubleCustomFieldRef linePaidAmount = new DoubleCustomFieldRef();
				linePaidAmount.setScriptId(TransactionFieldEnum.LINE_PAID_AMOUNT.getScriptId());
				linePaidAmount.setValue(line.getLinePaidAmount().doubleValue());
				itemCustomFields.add(linePaidAmount);
			}

			if(line.getMaType() != null) {
				SelectCustomFieldRef maType = new SelectCustomFieldRef();
				maType.setScriptId(TransactionFieldEnum.LINE_MA_TYPE.getScriptId());
				maType.setValue(new ListOrRecordRef(null, super.service.searchForCustomListItemId(CustomListEnum.MA_TYPE.getScriptId(), line.getMaType()), null, null));		
				itemCustomFields.add(maType);	
			}

			if(line.getDescription() != null) {
				SelectCustomFieldRef invoiceDescription = new SelectCustomFieldRef();
				invoiceDescription.setScriptId(TransactionFieldEnum.INVOICE_DESCRIPTION.getScriptId());
				invoiceDescription.setValue(new ListOrRecordRef(null, suiteTalkCacheService.searchForCustomRecordId(CustomRecordEnum.INVOICE_DESCRIPTION.getScriptId(), line.getDescription()), null, null));
				itemCustomFields.add(invoiceDescription);
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
			
			item.setCustomFieldList(new CustomFieldList(itemCustomFields.toArray(CustomFieldRef[]::new)));

			invoiceItems.add(item);
		}		
		invoice.setItemList(new InvoiceItemList(invoiceItems.toArray(InvoiceItem[]::new), true));		
		

		return invoice;
	}
	
	// TODO Generalize
	private String lookupItemInternalId(ClientInvoiceLineVO clientInvoiceLineVO) throws Exception {
		String internalId = null;

		try {
			internalId = (String)suiteAnalyticsCacheService.getItem(clientInvoiceLineVO.getItem()).get(ItemSuiteAnalyticsService.INTERNAL_ID); //TODO This a temporary fix. Item should be retrieved from API.
			if(internalId == null) {
				throw new Exception("Item does not exist");
			}
		} catch(Exception e) {
			throw new RemoteException(String.format("Error occurred in while searching for item %s in the external system", clientInvoiceLineVO.getItem()), e);			
		}

		return internalId;
	}
	
	private List<ClientInvoiceLineVO> loadLines(Invoice invoice) throws Exception {
		if(invoice.getItemList() == null) return new ArrayList<>(0);

		List<ClientInvoiceLineVO> lines = new ArrayList<>(0);
		for(InvoiceItem item : invoice.getItemList().getItem()){
			String linePaidAmountString = super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.LINE_PAID_AMOUNT);
			BigDecimal linePaidAmount = linePaidAmountString ==  null ? BigDecimal.ZERO : new BigDecimal(linePaidAmountString).setScale(2);
			
			String docIdString = super.getCustomFieldValue(item.getCustomFieldList(), ClientInvoiceFieldEnum.MA_DOC_ID);
			Long docId = docIdString == null ? null : Long.valueOf(docIdString);

			String docLineIdString = super.getCustomFieldValue(item.getCustomFieldList(), ClientInvoiceFieldEnum.MA_LINE_ID);
			Long docLineId = docLineIdString == null ? null : Long.valueOf(docLineIdString);

			String description = super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.INVOICE_DESCRIPTION);
			if(description == null) {
				description = item.getDescription();
			} 

			String maType = super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.LINE_MA_TYPE);

			String driverIdString = super.getCustomFieldValue(item.getCustomFieldList(), TransactionFieldEnum.DRIVER_ID);
			Long driverId = driverIdString == null ? null : Long.valueOf(driverIdString);
			
			ClientInvoiceLineVO line = new ClientInvoiceLineVO()
					.setItem(item.getItem().getName()) //TODO Will this name ever change? How do we use this as an identifier? 
					.setUnit(super.getFieldValueName(super.getCustomFieldRef(CustomSegmentEnum.UNIT_NO.getScriptId(), item.getCustomFieldList())))
					.setDescription(description)
					.setDepartment(item.getDepartment() == null ? null : item.getDepartment().getName())
					.setBusinessUnit(item.get_class() == null ? null : BusinessUnitEnum.getEnum(item.get_class().getName()))
					.setRate(item.getRate() == null ? BigDecimal.ZERO : new BigDecimal(item.getRate()))
					.setQuantity(item.getQuantity() == null ? BigDecimal.ZERO : new BigDecimal(item.getQuantity()))
					.setAmount(item.getAmount() == null ? BigDecimal.ZERO : new BigDecimal(item.getAmount()))
					.setLinePaidAmount(linePaidAmount)
					.setDocId(docId)
					.setDocLineId(docLineId)
					.setMaType(maType)
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

	private Invoice getCustomerInvoice(String internalId, String externalId) throws Exception {
		ReadResponse readResponse;

		LOG.info("Get client invoice from the accounting system ");
        
		RecordRef recRef = new RecordRef();
		recRef.setInternalId(internalId);
		recRef.setExternalId(StringUtils.hasText(externalId) ? externalId : null);
		recRef.setType(RecordType.invoice);

		readResponse = super.service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Failed to retrieve the client's invoice with internalId = %s and externalId = %s .", 
					internalId, externalId), readResponse);
		}		

		return (Invoice) readResponse.getRecord();	
	}
	
	private Long calculateDaysOverdue(Date dueDate) throws Exception {
		Date now = DateUtil.now(DateUtil.PATTERN_DATE);
		Long daysOverdue = now.getTime() - dueDate.getTime();

		return TimeUnit.DAYS.convert(daysOverdue, TimeUnit.MILLISECONDS);
	}

	private TransactionSearch convertToTransactionSearch(String clientInternalId, String clientExternalId, String maType) throws Exception {

		TransactionSearchBasic tsb = new TransactionSearchBasic();

		SearchEnumMultiSelectField transType = new SearchEnumMultiSelectField();
		transType.setOperator(SearchEnumMultiSelectFieldOperator.anyOf);
		transType.setSearchValue(new String[] {TransactionType.__invoice});
		tsb.setType(transType);

		SearchEnumMultiSelectField statusSearchField = new SearchEnumMultiSelectField();
		statusSearchField.setOperator(SearchEnumMultiSelectFieldOperator.anyOf);
		statusSearchField.setSearchValue(new String[]{TransactionStatus.__invoiceOpen});
		tsb.setStatus(statusSearchField);

		SearchMultiSelectField clientSearchField = new SearchMultiSelectField();
		clientSearchField.setOperator(SearchMultiSelectFieldOperator.anyOf);
		clientSearchField.setSearchValue(new RecordRef[]{new RecordRef(null, clientInternalId, clientExternalId, RecordType.customer)});
		tsb.setEntity(clientSearchField);	
		
		List<Object> customFields = new ArrayList<>();	

		SearchMultiSelectCustomField maTypeSearchField = new SearchMultiSelectCustomField();
		maTypeSearchField.setScriptId(ClientInvoiceFieldEnum.MA_TYPE.getScriptId());
		maTypeSearchField.setOperator(SearchMultiSelectFieldOperator.anyOf);		
		maTypeSearchField.setSearchValue(new ListOrRecordRef[]{new ListOrRecordRef(null, super.service.searchForCustomListItemId(CustomListEnum.MA_TYPE.getScriptId(), maType), null, null)});
		customFields.add(maTypeSearchField);
				
		SearchCustomFieldList customFieldList = new SearchCustomFieldList(customFields.toArray(SearchCustomField[]::new));

		tsb.setCustomFieldList(customFieldList);	
			
		TransactionSearch ts = new TransactionSearch();
		ts.setBasic(tsb);
		
		return ts;
	}
	
}