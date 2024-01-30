package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mikealbert.accounting.processor.enumeration.ClientFieldEnum;
import com.mikealbert.accounting.processor.enumeration.CustomListEnum;
import com.mikealbert.accounting.processor.exception.SuiteTalkDuplicateRecordException;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.service.XRefService;
import com.mikealbert.accounting.processor.vo.ClientVO;
import com.mikealbert.accounting.processor.vo.NoteVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;
import com.mikealbert.util.data.DataUtil;
import com.mikealbert.util.data.DateUtil;
import com.mikealbert.webservice.address.scrubber.component.enumeration.CountryEnum;
import com.mikealbert.webservice.suitetalk.enumeration.RecordTypeEnum;
import com.netsuite.webservices.lists.relationships_2023_2.Customer;
import com.netsuite.webservices.lists.relationships_2023_2.CustomerAddressbook;
import com.netsuite.webservices.lists.relationships_2023_2.CustomerAddressbookList;
import com.netsuite.webservices.lists.relationships_2023_2.CustomerSearch;
import com.netsuite.webservices.lists.relationships_2023_2.CustomerSearchAdvanced;
import com.netsuite.webservices.lists.relationships_2023_2.CustomerSearchRow;
import com.netsuite.webservices.platform.common_2023_2.Address;
import com.netsuite.webservices.platform.common_2023_2.CustomerSearchBasic;
import com.netsuite.webservices.platform.common_2023_2.NoteSearchRowBasic;
import com.netsuite.webservices.platform.common_2023_2.types.Country;
import com.netsuite.webservices.platform.core_2023_2.BooleanCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.DateCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.DoubleCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.LongCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnDateField;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnStringField;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectField;
import com.netsuite.webservices.platform.core_2023_2.SearchResult;
import com.netsuite.webservices.platform.core_2023_2.SelectCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.StringCustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.core_2023_2.types.SearchMultiSelectFieldOperator;
import com.netsuite.webservices.platform.faults_2023_2.types.StatusDetailCodeType;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;

@Service("customerSuiteTalkService")
public class CustomerSuiteTalkServiceImpl extends BaseSuiteTalkService implements CustomerSuiteTalkService {
	@Resource StateSuiteTalkService stateSuiteTalkService;
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource XRefService xRefService;
	
	static final String COLLECTION_NOTE_TYPE = "Collections";
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public void create(ClientVO clientVO) throws Exception {
		WriteResponse response = service.getService().add(convertToCreateCustomer(clientVO));	
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail(0).getCode() == StatusDetailCodeType.DUP_RCRD) {
				throw new SuiteTalkDuplicateRecordException(String.format("Error creating customer. ExternalId: %s", clientVO.getExternalId()), response);				
			} else {
				throw new SuiteTalkException(String.format("Error creating customer. ExternalId: %s", clientVO.getExternalId()), response);				
			}
		}
		
		LOG.info("Successfully created the Customer in the external accounting system. Customer -> {}", clientVO);		
	}

	@Override
	public void update(ClientVO clientVO) throws Exception {		 
		WriteResponse writeResponse = service.getService().update(convertToUpdateCustomer(clientVO));
		if(!writeResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error occurred while updating customer internalId=%s, externalId=%s, and address externalId = %s.", 
			        clientVO.getInternalId(), clientVO.getExternalId(), clientVO.getAddressExternalId()), writeResponse);			
		}
		
		LOG.info("Successfully updated the externalIds for customer -> {}", clientVO);			
	}	

	@Override
	public ClientVO getCustomer(String internalId, String externalId, boolean includeParent) throws Exception {
		LOG.info("Get Customer from accounting system ");
        
		RecordRef recRef = new RecordRef();
		recRef.setInternalId(internalId);
		recRef.setExternalId(StringUtils.hasText(externalId) ? externalId : null);
		recRef.setType(RecordType.customer);

		ReadResponse readResponse = super.service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Failed to retrieve customer with internalId = %s and/or externalId = %s .", internalId, externalId), readResponse);
		}		

		Customer customer = (Customer) readResponse.getRecord();	
		
		ClientVO clientVO = convertToClientVO(customer);

		if(includeParent) {
			clientVO = enrichWithParent(customer, clientVO);
		}
					
		LOG.info("Found Customer: {}", clientVO.toString());

		return clientVO;
	}

	@Override
	public List<ClientVO> findAllActive() throws Exception {
		List<Customer> customers = suiteTalkCacheService.getActiveClients();

		List<ClientVO> clientVOs = new ArrayList<>(0);
		for(Customer customer : customers) {
			clientVOs.add(convertToClientVO(customer));
		}

		LOG.info(String.format("Found %d active clients", clientVOs.size()));

		return clientVOs;
	}	

	@Override
	public void delete(ClientVO clientVO) throws Exception {
		RecordRef recordRef = new RecordRef(null, clientVO.getInternalId(), clientVO.getExternalId(), RecordType.customer);
		WriteResponse response = service.getService().delete(recordRef, null);
		
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException("Error deleting customer", response);
		}		
	}

	@Override
	public String formatExternalId(String accountCode) throws Exception {
		if(!StringUtils.hasText(accountCode)) throw new Exception("Client does not have an account code. ");

		return accountCode.startsWith(CUSTOMER_EXTERNAL_ID_PREFIX) ? accountCode : CUSTOMER_EXTERNAL_ID_PREFIX.concat(accountCode);
	}	

	private ClientVO convertToClientVO(Customer customer) throws Exception {			
		CustomerAddressbook addressBook = getDefaultBillingAddress(customer);
		Address address = addressBook == null ? null : addressBook.getAddressbookAddress();

		ClientVO clientVO = new ClientVO()
		        .setInternalId(customer.getInternalId())
			    .setExternalId(customer.getExternalId())
			    .setEntityId(customer.getEntityId())
			    .setPerson(customer.getIsPerson())
			    .setSubsidiary(Long.parseLong(customer.getSubsidiary().getInternalId()))
			    .setAccountCode(customer.getEntityId())
			    .setAccountName(customer.getCompanyName())
			    .setShortName(customer.getCompanyName())
			    .setPrintOnCheckAs(customer.getPrintOnCheckAs())
				.setTaxId(customer.getVatRegNumber() != null ? customer.getVatRegNumber() : customer.getDefaultTaxReg() == null ? null : customer.getDefaultTaxReg().getName())
			    .setEmail(customer.getEmail())
			    .setPhoneNumber(customer.getPhone())
				.setFaxNumber(customer.getFax())
			    .setCategory(customer.getCategory() == null ? null : customer.getCategory().getName())
				.setCurrency(customer.getCurrency().getName())
			    .setStatus(customer.getEntityStatus().getName())
			    .setInactive(customer.getIsInactive())
			    .setTerms(customer.getTerms() == null ? null : customer.getTerms().getName())
			    .setCreditStatus(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.CREDIT_STATUS))
			    .setLastCreditCheck(DateUtil.convertToDate(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.LAST_CREDIT_CHECK_DATE), DateUtil.PATTERN_DATE))
				.setCreditScore(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.CREDIT_SCORE))
			    .setCreditLimit1(DataUtil.convertToBigDecimal(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.CREDIT_LIMIT_1)))
			    .setCreditLimit2(DataUtil.convertToBigDecimal(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.CREDIT_LIMIT_2)))
			    .setUnitLimit1(DataUtil.convertToLong(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.UNIT_LIMIT_1)))
			    .setUnitLimit2(DataUtil.convertToLong(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.UNIT_LIMIT_2)))
			    .setCapitalLimit1(DataUtil.convertToBigDecimal(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.CAPITAL_LIMIT_1)))
			    .setCapitalLimit2(DataUtil.convertToBigDecimal(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.CAPITAL_LIMIT_2)))
			    .setPurchaseCreditLimit(DataUtil.convertToBigDecimal(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.PURCHASE_CREDIT_LIMIT)))
			    .setIncorporationState(stateSuiteTalkService.getShortNameByLongName(suiteTalkCacheService.getStates(), super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.INCORPORATE_STATE)))
			    .setRiskDepositAmount(DataUtil.convertToBigDecimal(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.RISK_DEPARTMENT_AMOUNT)))
			    .setSuppressStatement(DataUtil.convertToBoolean(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.SUPPRESS_STATEMENT)))				
			    .setInterimFinanceCharge(DataUtil.convertToBoolean(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.INTERIM_FINANCE_CHARGE)))
				.setFinanceCharge(!DataUtil.convertToBoolean(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.SUPPRESS_FINANCE_CHARGE)))
			    .setBankrupt(DataUtil.convertToBoolean(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.BANKRUPT_INDICATOR)))
				.setBalance(new BigDecimal(customer.getBalance()))
				.setUnappliedBalance(DataUtil.nvl(DataUtil.convertToBigDecimal(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.UNAPPLIED_BALANCE)), BigDecimal.ZERO))
				.setDepositBalance(new BigDecimal(customer.getDepositBalance()))
				.setAgingAsOfDate(new Date())
				.setAgingCurrent(new BigDecimal(customer.getAging()))
				.setAging30(new BigDecimal(customer.getAging1()))
				.setAging60(new BigDecimal(customer.getAging2()))
				.setAging90(new BigDecimal(customer.getAging3()))
				.setAging91Plus(new BigDecimal(customer.getAging4()))
			    .setAddressInternalId(addressBook == null ? null : addressBook.getInternalId())
			    .setAddressExternalId(address == null ? null : super.getCustomFieldValue(address.getCustomFieldList(), ClientFieldEnum.ADDRESS_EXTERNAL_ID))
			    .setAddress1(address == null ? null : address.getAddr1())
			    .setAddress2(address == null ? null : address.getAddr2())
			    .setCountry(address == null ? null : address.getCountry().getValue())
			    .setRegion(address == null ? null : address.getState())
			    .setCounty(address == null ? null : super.getCustomFieldValue(address.getCustomFieldList(), ClientFieldEnum.ADDRESS_COUNTY))
			    .setCity(address == null ? null : address.getCity())
			    .setPostalCode(address == null ? null : address.getZip())
			    .setBankAccountNumber("")
			    .setBankName("")
			    .setBankNumber("")
			    .setBankType("")
				.setLastPaymentDate(DateUtil.convertToDate(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.LAST_PAYMENT_DATE), DateUtil.PATTERN_DATE_TIME))
				.setLastPaymentAmount(DataUtil.nvl(DataUtil.convertToBigDecimal(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.LAST_PAYMENT_AMOUNT)), BigDecimal.ZERO))
				.setCollectionStatus(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.COLLECTION_STATUS))
				.setCollector(super.getCustomFieldValue(customer.getCustomFieldList(), ClientFieldEnum.COLLECTOR))
				.setAsOfDate(new Date());
				
		return clientVO;
	}

	private Customer convertToCreateCustomer(ClientVO clientVO) throws Exception {
		List<CustomFieldRef> customerCustomFields = new ArrayList<>(0);
		List<CustomFieldRef> addressCustomFields = new ArrayList<>(0);
		Customer customer = new Customer();

		customer.setExternalId(clientVO.getExternalId());
		customer.setIsPerson(clientVO.isPerson());
		customer.setPrintOnCheckAs(clientVO.getPrintOnCheckAs());
		customer.setCompanyName(clientVO.getAccountName());
		customer.setEmail(clientVO.getEmail());
		customer.setPhone(clientVO.getPhoneNumber());
		customer.setFax(clientVO.getFaxNumber());		
		customer.setVatRegNumber(clientVO.getTaxId());		

		customer.setSubsidiary(new RecordRef(null, xRefService.getExternalValue(XRefGroupNameEnum.COMPANY, String.valueOf(clientVO.getSubsidiary())), null,  RecordType.subsidiary));
		customer.setParent(new RecordRef(null, null, clientVO.getParentAccountCode(), RecordType.customer));
		customer.setEntityStatus(new RecordRef(clientVO.getStatus(), null, null, RecordType.customerStatus));
		customer.setCategory(new RecordRef(null, suiteTalkCacheService.searchCustomerCategoryInternalIdByName(clientVO.getCategory()), null, RecordType.customerCategory));	
		customer.setTerms(new RecordRef(null, suiteTalkCacheService.searchTermInternalIdByName(clientVO.getTerms()), null, RecordType.term));
		customer.setCurrency(new RecordRef(clientVO.getCurrency(), null, null, RecordType.currency));
	
		BooleanCustomFieldRef financeCharge = new BooleanCustomFieldRef();
		financeCharge.setScriptId(ClientFieldEnum.SUPPRESS_FINANCE_CHARGE.getScriptId());
		financeCharge.setValue(clientVO.isFinanceCharge());
		customerCustomFields.add(financeCharge);

		BooleanCustomFieldRef suppressStatement = new BooleanCustomFieldRef();
		suppressStatement.setScriptId(ClientFieldEnum.SUPPRESS_STATEMENT.getScriptId());
		suppressStatement.setValue(clientVO.isSuppressStatement());
		customerCustomFields.add(suppressStatement);

		BooleanCustomFieldRef interimFinanceCharge = new BooleanCustomFieldRef();
		interimFinanceCharge.setScriptId(ClientFieldEnum.INTERIM_FINANCE_CHARGE.getScriptId());
		interimFinanceCharge.setValue(clientVO.isInterimFinanceCharge());
		customerCustomFields.add(interimFinanceCharge);

		BooleanCustomFieldRef bankrupt = new BooleanCustomFieldRef();
		bankrupt.setScriptId(ClientFieldEnum.BANKRUPT_INDICATOR.getScriptId());
		bankrupt.setValue(clientVO.isBankrupt());
		customerCustomFields.add(bankrupt);

		if(clientVO.getCreditLimit1() != null) {
		    DoubleCustomFieldRef creditLimit1 = new DoubleCustomFieldRef();
		    creditLimit1.setScriptId(ClientFieldEnum.CREDIT_LIMIT_1.getScriptId());
		    creditLimit1.setValue(clientVO.getCreditLimit1().doubleValue());
		    customerCustomFields.add(creditLimit1);			
		}

		if(clientVO.getCreditLimit2() != null) {
		    DoubleCustomFieldRef creditLimit2 = new DoubleCustomFieldRef();
		    creditLimit2.setScriptId(ClientFieldEnum.CREDIT_LIMIT_2.getScriptId());
		    creditLimit2.setValue(clientVO.getCreditLimit2().doubleValue());
		    customerCustomFields.add(creditLimit2);			
		}

		if(clientVO.getUnitLimit1() != null) {
		    LongCustomFieldRef unitLimit1 = new LongCustomFieldRef();
		    unitLimit1.setScriptId(ClientFieldEnum.UNIT_LIMIT_1.getScriptId());
		    unitLimit1.setValue(clientVO.getUnitLimit1().longValue());
		    customerCustomFields.add(unitLimit1);			
		}		

		if(clientVO.getUnitLimit2() != null) {
		    LongCustomFieldRef unitLimit2 = new LongCustomFieldRef();
		    unitLimit2.setScriptId(ClientFieldEnum.UNIT_LIMIT_2.getScriptId());
		    unitLimit2.setValue(clientVO.getUnitLimit2().longValue());
		    customerCustomFields.add(unitLimit2);			
		}		

		if(clientVO.getCapitalLimit1() != null) {
		    DoubleCustomFieldRef capitalLimit1 = new DoubleCustomFieldRef();
		    capitalLimit1.setScriptId(ClientFieldEnum.CAPITAL_LIMIT_1.getScriptId());
		    capitalLimit1.setValue(clientVO.getCapitalLimit1().doubleValue());
		    customerCustomFields.add(capitalLimit1);	
		}

		if(clientVO.getCapitalLimit2() != null) {
		    DoubleCustomFieldRef capitalLimit2 = new DoubleCustomFieldRef();
		    capitalLimit2.setScriptId(ClientFieldEnum.CAPITAL_LIMIT_2.getScriptId());
		    capitalLimit2.setValue(clientVO.getCapitalLimit2().doubleValue());
		    customerCustomFields.add(capitalLimit2);	
		}

		if(clientVO.getPurchaseCreditLimit() != null) {
		    DoubleCustomFieldRef purchaseCreditLimit = new DoubleCustomFieldRef();
		    purchaseCreditLimit.setScriptId(ClientFieldEnum.PURCHASE_CREDIT_LIMIT.getScriptId());
		    purchaseCreditLimit.setValue(clientVO.getPurchaseCreditLimit().doubleValue());
		    customerCustomFields.add(purchaseCreditLimit);			
		}
		
		if(clientVO.getCreditScore() != null) {
		    DoubleCustomFieldRef creditScore = new DoubleCustomFieldRef();
		    creditScore.setScriptId(ClientFieldEnum.CREDIT_SCORE.getScriptId());
		    creditScore.setValue(clientVO.getPurchaseCreditLimit().doubleValue());
		    customerCustomFields.add(creditScore);
		}
		
		if(clientVO.getCreditStatus() != null) {
		    SelectCustomFieldRef creditApprovalStatus = new SelectCustomFieldRef();
		    creditApprovalStatus.setScriptId(ClientFieldEnum.CREDIT_STATUS.getScriptId());
		    creditApprovalStatus.setValue(new ListOrRecordRef(null, super.service.searchForCustomListItemId(CustomListEnum.CREDIT_STATUS.getScriptId(), clientVO.getCreditStatus()), null, null));		
		    customerCustomFields.add(creditApprovalStatus);	
		}	

		if(clientVO.getCreditManagementType() != null) {
		    SelectCustomFieldRef creditManagementType = new SelectCustomFieldRef();
		    creditManagementType.setScriptId(ClientFieldEnum.CREDIT_MANAGEMENT_TYPE.getScriptId());
		    creditManagementType.setValue(new ListOrRecordRef(null, super.service.searchForCustomListItemId(CustomListEnum.CREDIT_MANAGEMENT_TYPE.getScriptId(), clientVO.getCreditManagementType()), null, null));
		    customerCustomFields.add(creditManagementType);		
		}

		if(clientVO.getIncorporationState() != null) {
		    SelectCustomFieldRef incorporationState = new SelectCustomFieldRef();
		    incorporationState.setScriptId(ClientFieldEnum.INCORPORATE_STATE.getScriptId());
		    incorporationState.setValue(new ListOrRecordRef(null, stateSuiteTalkService.getStateInternalIdByLongName(suiteTalkCacheService.getStates(), clientVO.getIncorporationState()), null, RecordTypeEnum.STATE.getId()));
		    customerCustomFields.add(incorporationState);
		}

		if(clientVO.getLastCreditCheck() != null) {
		    DateCustomFieldRef lastCreditCheckDate = new DateCustomFieldRef();
		    lastCreditCheckDate.setScriptId(ClientFieldEnum.LAST_CREDIT_CHECK_DATE.getScriptId());
		    lastCreditCheckDate.setValue(DateUtil.convertToCalendar(clientVO.getLastCreditCheck()));	
		    customerCustomFields.add(lastCreditCheckDate);
		}

		if(clientVO.getRiskDepositAmount() != null) {
		    DoubleCustomFieldRef riskDepositAmount = new DoubleCustomFieldRef();
		    riskDepositAmount.setScriptId(ClientFieldEnum.RISK_DEPARTMENT_AMOUNT.getScriptId());
		    riskDepositAmount.setValue(clientVO.getRiskDepositAmount().doubleValue());
		    customerCustomFields.add(riskDepositAmount);		
		}

		if(clientVO.getCollectionStatus() != null) {
			SelectCustomFieldRef collectionStatus = new SelectCustomFieldRef();
			collectionStatus.setScriptId(ClientFieldEnum.COLLECTION_STATUS.getScriptId());
			collectionStatus.setValue(new ListOrRecordRef(null, super.service.searchForCustomListItemId(CustomListEnum.COLLECTION_STATUS.getScriptId(), clientVO.getCollectionStatus()), null, null));
			customerCustomFields.add(collectionStatus);
		}

		customer.setCustomFieldList(new CustomFieldList(customerCustomFields.toArray(CustomFieldRef[]::new)));  

		Address address = new Address();
		address.setAddr1(clientVO.getAddress1());
		address.setAddr2(clientVO.getAddress2());
		address.setCountry(clientVO.getCountry().equals(CountryEnum.UNITED_STATES.getName()) ? Country._unitedStates : clientVO.getCountry().equals(CountryEnum.CANADA.getName()) ? Country._canada : null);
		address.setState(clientVO.getRegion());
		address.setCity(clientVO.getCity());
		address.setZip(clientVO.getPostalCode());

		StringCustomFieldRef addressExternalId = new StringCustomFieldRef();
		addressExternalId.setScriptId(ClientFieldEnum.ADDRESS_EXTERNAL_ID.getScriptId());
		addressExternalId.setValue(clientVO.getAddressExternalId());
		addressCustomFields.add(addressExternalId);

		StringCustomFieldRef county = new StringCustomFieldRef();
		county.setScriptId(ClientFieldEnum.ADDRESS_COUNTY.getScriptId());
		county.setValue(clientVO.getCounty());
		addressCustomFields.add(county);

		StringCustomFieldRef vertexCountry = new StringCustomFieldRef();
		vertexCountry.setScriptId(ClientFieldEnum.ADDRESS_VERTEX_COUNTRY.getScriptId());
		vertexCountry.setValue(clientVO.getCountry());
		addressCustomFields.add(vertexCountry);		

		address.setCustomFieldList(new CustomFieldList(addressCustomFields.toArray(CustomFieldRef[]::new)));  

		customer.setAddressbookList(new CustomerAddressbookList());
		customer.getAddressbookList().setAddressbook(new CustomerAddressbook[]{new CustomerAddressbook()});
		customer.getAddressbookList().getAddressbook(0).setAddressbookAddress(address);
		
		return customer;
	}

	private Customer convertToUpdateCustomer(ClientVO clientVO) throws Exception {
		ReadResponse readResponse = super.service.getService().get(new RecordRef(null, clientVO.getInternalId(), clientVO.getExternalId(), RecordType.customer));
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error occurred while retriving customer externalId internalId=%s and externalId=%s. ",  clientVO.getInternalId(), clientVO.getExternalId()), readResponse);			
		}

		Customer customer = (Customer)readResponse.getRecord();
		customer.setExternalId(formatExternalId(clientVO.getAccountCode()));
		customer.getAddressbookList().setReplaceAll(false);

		List<Object> customFields = new ArrayList<>(0);
		StringCustomFieldRef addressExternalId = new StringCustomFieldRef();
		addressExternalId.setScriptId(ClientFieldEnum.ADDRESS_EXTERNAL_ID.getScriptId());
		addressExternalId.setValue(clientVO.getAddressExternalId());
		customFields.add(addressExternalId);
						
		Address address = new Address();
		address.setInternalId(clientVO.getAddressInternalId());
		address.setCustomFieldList(new CustomFieldList(customFields.toArray(CustomFieldRef[]::new)));

		for(CustomerAddressbook book : customer.getAddressbookList().getAddressbook()) {
			if(book.getInternalId().equals(clientVO.getAddressInternalId())) {
				book.setAddressbookAddress(address);
			}
		}

		return customer;
	}	

	private ClientVO enrichWithParent(Customer customer, ClientVO clientVO) throws Exception {
		ClientVO parent = customer.getParent() == null ? null : getCustomer(customer.getParent().getInternalId(), customer.getParent().getExternalId(), true);

		return clientVO 
		        .setParentInternalId(parent == null ? null : parent.getInternalId())
		        .setParentExternalId(parent == null ? null : parent.getExternalId())
		        .setParentAccountCode(parent == null ? null : parent.getAccountCode())
				.setCreditManagementType(determineCreditManagementType(customer, parent));
	}

	public ClientVO enrichWithNotes(ClientVO clientVO) throws Exception {
		return clientVO 
		        .setCollectionNote(getLastCollectionNote(clientVO.getInternalId(), clientVO.getExternalId()));
	}	
	
	private CustomerAddressbook getDefaultBillingAddress(Customer customer) {
		if(customer.getAddressbookList() == null) return null;

		return Arrays.asList(customer.getAddressbookList().getAddressbook()).stream()
		        .filter(address -> address.getDefaultBilling())
		        .findFirst()
		        .orElse(null);
	}
	
	private String determineCreditManagementType(Customer customer, ClientVO parent) throws Exception {
		String type = null;

		if(parent != null) {
			type = "Hierarchy / Share Limits";
		} else {
			List<Customer> customers = service.searchCustomersByParent(null, customer.getInternalId(), null).stream()
			        .filter(c -> !c.getInternalId().equals(customer.getInternalId()))
					.collect(Collectors.toList());

			if(customers.size() > 0) {
				type = "Parent";
			} else {
				type = "No Hierarchy";
			}
		}

		return type;
	}

	private NoteVO getLastCollectionNote(String customerInternalId, String customerExternalId) throws Exception {
		CustomerSearchBasic csb = new CustomerSearchBasic();
		csb.setInternalId(new SearchMultiSelectField(new RecordRef[]{new RecordRef(null, customerInternalId, customerExternalId, RecordType.customer)}, SearchMultiSelectFieldOperator.anyOf)); 

		CustomerSearch cs = new CustomerSearch();
		cs.setBasic(csb);

		CustomerSearchRow csr = new CustomerSearchRow();

		NoteSearchRowBasic nsrb = new NoteSearchRowBasic();
		nsrb.setNote(new SearchColumnStringField[]{new SearchColumnStringField()});
		nsrb.setNoteDate(new SearchColumnDateField[]{new SearchColumnDateField()});
		nsrb.setTitle(new SearchColumnStringField[]{new SearchColumnStringField()});
		nsrb.setNoteType(new SearchColumnStringField[]{new SearchColumnStringField()});
		csr.setUserNotesJoin(nsrb);
	
		CustomerSearchAdvanced csa = new CustomerSearchAdvanced();
		csa.setCriteria(cs);
		csa.setColumns(csr);

		SearchResult result = null;
		try {
			result = super.service.getService().search(csa);
		} catch(Exception e) {
			LOG.warn("Error searching for customer notes. customerInternalId {} and customerExternalId {}. Error detail: ", customerInternalId, customerExternalId, e);
			
			return new NoteVO()
					.setCreateDate(new Date())
					.setComment("ERROR");
		}

		if(!result.getStatus().isIsSuccess()) {
			throw new SuiteTalkException("Error searching for customer notes", result);
		}
		
		return Arrays.asList(result.getSearchRowList().getSearchRow()).stream()
		        .filter(resultSR -> ((CustomerSearchRow)resultSR).getUserNotesJoin().getNoteDate()[0].getSearchValue() != null)		
		        .filter(resultSR -> ((CustomerSearchRow)resultSR).getUserNotesJoin().getNote()[0].getSearchValue() != null)		
		        .filter(resultSR -> ((CustomerSearchRow)resultSR).getUserNotesJoin().getNoteType() != null)						
		        .filter(resultSR -> ((CustomerSearchRow)resultSR).getUserNotesJoin().getNoteType()[0].getSearchValue().equalsIgnoreCase(COLLECTION_NOTE_TYPE))
				.map(resultSR -> {
					    return new NoteVO()
						        .setCreateDate(((CustomerSearchRow)resultSR).getUserNotesJoin().getNoteDate()[0].getSearchValue().getTime())
								.setComment(((CustomerSearchRow)resultSR).getUserNotesJoin().getNote()[0].getSearchValue());
				})
				.max(Comparator.comparing(NoteVO::getCreateDate))
				.orElse(null);	
	}
		
}