package com.mikealbert.accounting.processor.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mikealbert.accounting.processor.client.suiteanalytics.CustomerSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.AgingTransactionSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.CustomerPaymentSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.CustomerSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.SuiteTalkCacheService;
import com.mikealbert.accounting.processor.dao.ExternalAccountDAO;
import com.mikealbert.accounting.processor.entity.ExtAccAddress;
import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.entity.ExternalAccountPK;
import com.mikealbert.accounting.processor.enumeration.ClientFieldEnum;
import com.mikealbert.accounting.processor.enumeration.PaymentMethodEnum;
import com.mikealbert.accounting.processor.vo.ClientVO;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;
import com.mikealbert.constant.enumeration.AccountStatusEnum;
import com.mikealbert.constant.enumeration.AccountTypeEnum;
import com.mikealbert.constant.enumeration.CurrencyCodeEnum;
import com.mikealbert.util.data.DataUtil;

@Service("clientService")
public class ClientServiceImpl extends AccountService implements ClientService {
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource CustomerSuiteAnalyticsService customerSuiteAnalyticsService;
	@Resource CustomerSuiteTalkService customerSuiteTalkService;
	@Resource AgingTransactionSuiteTalkService agingTransactionSuiteTalkService;
	@Resource CustomerPaymentSuiteTalkService customerPaymentSuiteTalkService;
	@Resource XRefService xRefService;
	@Resource TaxJurisdictionService taxJurisdictionService;	
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource ClientInvoiceService clientInvoiceService;

	static final Long OVERDUE_INTEREST_PERCENT_ZERO = 12L;
	static final Long OVERDUE_INTEREST_PERCENT_EIGHTEEN = 1L;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());	

	/**
	 * Retrieves the client from the external system.
	 * 
	 * @param String ExternaId of the client. 
	 * @return ClientVO the found client
	 */
	@Override
	public ClientVO get(String externalId, boolean loadPurchaseBalance) throws Exception {
		return customerSuiteTalkService.getCustomer(null, externalId, false);
	}

	/** 
	 * Retrieves the clients from the external system that have been created and/or updated
	 * between the specified from and to dates.
	 * 
	 * @param Date from start date
	 * @param Date to end date 
	 * @return List of maps, each containing identifiers to the client 
	*/
	@Override
	public List<Map<String, Object>> getClients(Date from, Date to) throws Exception {
		return customerSuiteAnalyticsService.getCustomers(from, to);
	}

	/**
	 * Processes client creation or update(s) from the external system.
	 * 
	 * @param Map identifiers to the client that will be processed 
	 * @return {@link com.mikealbert.accounting.processor.entity.ExternalAccount ExternalAccount} created or updated 
	 */
	@Transactional
	@Override
	public ExternalAccount process(Map<String, String> client) throws Exception {
		String internalId = (String)client.get(ClientFieldEnum.INTERNAL_ID.getScriptId());
		String externalId = (String)client.get(ClientFieldEnum.EXTERNAL_ID.getScriptId());		

		ClientVO clientVO = customerSuiteTalkService.getCustomer(internalId, externalId, true);
		super.validate(clientVO);
		if(!hasDefaultBillingAddress(clientVO)) {
			LOG.info("Customer {} does not have a default billing address", clientVO.getAccountCode());
			return null;
		}

		ExternalAccount account = bindAccount(clientVO, isNewClient(clientVO));
		account = preSequenceAddressCode(account);
		account = bindAccountAddress(account, clientVO, isNewClient(clientVO));
		account = resequenceAddressCode(account);
		account = externalAccountDAO.save(account);
		
		updateExternalIds(account, clientVO);

		LOG.info("Succesfully updated account: " + account);
		
		return account;
	}
	
	/** 
	 * Generates the parent message maps from the child client map, only when the client is/was actually a child.
	 * 
	 * @param Map client message map
	 * @return Map parent map only when client is a child
	 */
	@Transactional
	@Override
	public List<Map<String, String>> getClientParents(Map<String, String> clientMap) throws Exception {
		List<Map<String, String>> parents = new ArrayList<>();
		Map<String, String> parent = null;

		String clientExternalId = (String)clientMap.get(ClientFieldEnum.EXTERNAL_ID.getScriptId());

		if(!StringUtils.hasText(clientExternalId)) return parents;
		
		ClientVO clientVO = customerSuiteTalkService.getCustomer(null, clientExternalId, true);
		if(clientVO != null && StringUtils.hasText(clientVO.getParentInternalId())) {
			parent = new HashMap<>();
			parent.put(ClientFieldEnum.INTERNAL_ID.getScriptId(), clientVO.getParentInternalId());
			parent.put(ClientFieldEnum.EXTERNAL_ID.getScriptId(), clientVO.getParentExternalId());
			parent.put(ClientFieldEnum.ACCOUNT_CODE.getScriptId(), clientVO.getParentAccountCode());

			parents.add(parent);			
		}

		ExternalAccount account = externalAccountDAO.findById(new ExternalAccountPK(1L, AccountTypeEnum.C.name(), clientExternalId.replace("1C", ""))).orElse(null);
		if(account != null 
		        && StringUtils.hasText(account.getParentAccount()) 
				&& !account.getParentAccount().equals(clientVO == null ? null : clientVO.getParentAccountCode()) ){
			parent = new HashMap<>();
			parent.put(ClientFieldEnum.INTERNAL_ID.getScriptId(), null);
			parent.put(ClientFieldEnum.EXTERNAL_ID.getScriptId(), customerSuiteTalkService.formatExternalId(account.getParentAccount()));
			parent.put(ClientFieldEnum.ACCOUNT_CODE.getScriptId(), account.getParentAccount());

			parents.add(parent);			
		}
		
		return parents;
	}	

	/**
	 * Parse out the account code from the external id. The external id passed in is expected to
	 * be formatted. At the time of writting, the format is {cId}{accountType}{accountCode}
	 * 
	 * @param String the formatted external id containing the account code
	 * @return String the parsed out account code
	 */
	@Override
	public String parseAccountCodeFromExternalId(String formattedExternalId) throws Exception {
		if(!StringUtils.hasText(formattedExternalId)
		        || !formattedExternalId.startsWith(CustomerSuiteTalkService.CUSTOMER_EXTERNAL_ID_PREFIX)) {
			throw new Exception(String.format("Invalid formatted external Id %s", formattedExternalId));
		}

		return formattedExternalId.replace(CustomerSuiteTalkService.CUSTOMER_EXTERNAL_ID_PREFIX, "");
	}

	@Override
	public String formatExternalId(String accountCode) throws Exception {
		return customerSuiteTalkService.formatExternalId(accountCode);
	}

	/**
	 * Finds all active clients that have an account balance
	 *
	 *  @return List of active clients that have an account balance, latest collection note included
	 */
	@Override
	public List<ClientVO> findWithBalance() throws Exception {
		List<ClientVO> clientVOs = customerSuiteTalkService.findAllActive().stream()
				.filter(clientVO -> hasBalance(clientVO))
				.collect(Collectors.toList());

		List<ClientVO> result = new ArrayList<>(0);
		for(ClientVO clientVO : clientVOs) {
			result.add(customerSuiteTalkService.enrichWithNotes(clientVO));
		}

		LOG.info(String.format("Found %d clients with balance", result.size()));

		return result;
	}

	private boolean hasBalance(ClientVO clientVO) {
		return clientVO.getBalance().compareTo(BigDecimal.ZERO) != 0
				|| clientVO.getUnappliedBalance().compareTo(BigDecimal.ZERO) != 0
				|| clientVO.getAgingCurrent().compareTo(BigDecimal.ZERO) != 0
				|| clientVO.getAging30().compareTo(BigDecimal.ZERO) != 0
				|| clientVO.getAging60().compareTo(BigDecimal.ZERO) != 0
				|| clientVO.getAging90().compareTo(BigDecimal.ZERO) != 0
				|| clientVO.getAging91Plus().compareTo(BigDecimal.ZERO) != 0;
	}	

	@Override
	public List<ClientVO> findActive() throws Exception {
		return customerSuiteTalkService.findAllActive();
	}

	private void updateExternalIds(ExternalAccount account, ClientVO clientVO) throws Exception {
		if(clientVO.getExternalId() == null || clientVO.getExternalId().isBlank()
		        || clientVO.getAddressExternalId() == null || clientVO.getAddressExternalId().isBlank()) {
			
			clientVO
			        .setExternalId(customerSuiteTalkService.formatExternalId(account.getId().getAccountCode()))
			        .setAddressExternalId(getDefaultPostAddress(account).getEaaId().toString());

			customerSuiteTalkService.update(clientVO);
		}
	}

	private ExternalAccount bindAccount(ClientVO clientVO, boolean isNewClient) throws Exception{
		ExternalAccount account; 
		
		if(isNewClient) {
			account = new ExternalAccount(new ExternalAccountPK(1L, AccountTypeEnum.C.name(), clientVO.getAccountCode()));
			account.setCrtCId(1L);			
			account.setCrtExtAccType(AccountTypeEnum.C.name());			
			account.setCurrencyCode(CurrencyCodeEnum.USD.name());
			account.setWebQuotesReqCcApproval("N");
			account.setWebQuotesReqFaApproval("Y");
			account.setUpfitInd("N");
			account.setPaymentInd("M");
			account.setInternationalInd("N");
			account.setTaxInd("Y");
			account.setDateOpened(new Date());
			account.setPaymentMethod(PaymentMethodEnum.CHECK.name());
			account.setExternalAccountBankAccounts(new ArrayList<>());
			account.setSuppliers(new ArrayList<>());			
		} else {
			account = externalAccountDAO.findById(new ExternalAccountPK(1L, AccountTypeEnum.C.name(), parseAccountCodeFromExternalId(clientVO.getExternalId())))
			    .orElseThrow( () -> new Exception(String.format("Unable to find client's external account record %s", clientVO)));
		}

		account.setAccountName(DataUtil.clip(clientVO.getAccountName(), 80));
		account.setShortName(DataUtil.clip(clientVO.getShortName(), 25));
		account.setRegName(DataUtil.clip(clientVO.getAccountName(), 80));
		account.setCompRegNo(clientVO.getTaxId() == null || clientVO.getTaxId().isBlank() ? null : externalAccountDAO.desencrypt(clientVO.getTaxId()));
		account.setParentEntity(clientVO.getParentAccountCode() == null || clientVO.getParentAccountCode().isBlank() ? null : 1L);
		account.setParentAccountType(clientVO.getParentAccountCode() == null || clientVO.getParentAccountCode().isBlank() ? null : AccountTypeEnum.C.name());
		account.setParentAccount(clientVO.getParentAccountCode() == null || clientVO.getParentAccountCode().isBlank() ? null : parseAccountCodeFromExternalId(clientVO.getParentExternalId()));
		account.setTelexCode(determineEmail(clientVO));
		account.setTelephoneNumber(clientVO.getPhoneNumber());
		account.setFaxCode(clientVO.getFaxNumber());
		account.setAccStatus(determineAccountStatus(clientVO).name());
		account.setCreditTermsCode(clientVO.getTerms() == null || clientVO.getTerms().isBlank() ? null : xRefService.getInternalValue(XRefGroupNameEnum.CLIENT_CREDIT_TERM, clientVO.getTerms()));
		account.setGroupCode(clientVO.getCategory() == null || clientVO.getCategory().isBlank() ? null : xRefService.getInternalValue(XRefGroupNameEnum.CLIENT_CATEGORY, clientVO.getCategory()));
		account.setCredApprStatus(clientVO.getCreditStatus() == null || clientVO.getCreditStatus().isBlank() ? null : xRefService.getInternalValue(XRefGroupNameEnum.CLIENT_CREDIT_STATUS, clientVO.getCreditStatus()));
		account.setOverdueInterest(clientVO.isFinanceCharge() ? OVERDUE_INTEREST_PERCENT_EIGHTEEN : OVERDUE_INTEREST_PERCENT_ZERO);
		account.setCreditManagementType(clientVO.getCreditManagementType() == null || clientVO.getCreditManagementType().isBlank() ? null : xRefService.getInternalValue(XRefGroupNameEnum.CLIENT_CREDIT_MANAGEMENT_TYPE, clientVO.getCreditManagementType()));
		account.setDateLastCreditCheck(clientVO.getLastCreditCheck());
		account.setCreditLimit(clientVO.getCreditLimit1());
		account.setCreditLimit2(clientVO.getCreditLimit2());
		account.setCreditUnit1(clientVO.getUnitLimit1());
		account.setCreditUnit2(clientVO.getUnitLimit2());
		account.setCapitalLimit1(clientVO.getCapitalLimit1());
		account.setCapitalLimit2(clientVO.getCapitalLimit2());
		account.setPurchaseCreditLimit(clientVO.getPurchaseCreditLimit());
		account.setCreditScore(clientVO.getCreditScore() == null ? null : clientVO.getCreditScore().toString());
		account.setIncorporationState(clientVO.getIncorporationState());
		account.setRiskDepositAmt(clientVO.getRiskDepositAmount());
		account.setPrintStatement(clientVO.isSuppressStatement() ? "N" : "Y");
		account.setUpfitFeeChgd(clientVO.isInterimFinanceCharge() ? "Y" : "N");
		account.setBankruptInd(clientVO.isBankrupt() ? "Y" : "N");
	
		return account;
	}

	private ExternalAccount bindAccountAddress(ExternalAccount account, ClientVO clientVO, boolean isNewClient) throws Exception {
		ExtAccAddress address;

		//TODO This jurisdiction lookup goes away after migrating Willow from Q to O Series
		String country = xRefService.getInternalValue(XRefGroupNameEnum.API_COUNTRY, clientVO.getCountry());
		TaxJurisdictionVO jurisdiction = taxJurisdictionService.find(country, clientVO.getRegion(), clientVO.getCounty(), clientVO.getCity(), clientVO.getPostalCode(), clientVO.getPostalCode());
		if(jurisdiction == null) {
			throw new Exception(
				String.format("Could not locate GEO Code for client account %s based on input - country: %s, region: %s, county: %s, city: %s, zip: %s", clientVO.getAccountCode(), country, clientVO.getRegion(), clientVO.getCounty(), clientVO.getCity(), clientVO.getPostalCode()));
		}
		
		if(isNewClient){
			address = new ExtAccAddress();
			address.setExternalAccount(account);
			account.setExternalAccountAddresses(new ArrayList<>());
			account.getExternalAccountAddresses().add(address);		
		} else if(clientVO.getAddressExternalId() == null || clientVO.getAddressExternalId().isBlank()) {
			address = new ExtAccAddress();		
			address.setExternalAccount(account);	
			account.getExternalAccountAddresses().add(address);
		} else {
			address = account.getExternalAccountAddresses().stream()
			        .filter(a -> a.getEaaId().equals(Long.parseLong(clientVO.getAddressExternalId())))
				    .findFirst()
				    .orElseThrow();
		}

		account.getExternalAccountAddresses().stream()
		        .forEach(a -> {
					a.setDefaultBilling(false);
					a.setDefaultInd(null);					
				});

		address.setDefaultBilling(true);
		address.setAddressCode("1");
		address.setAddressType("POST");	
		address.setDefaultInd("Y");
		address.setAddressLine1(DataUtil.clip(clientVO.getAddress1(), 80));
		address.setAddressLine2(DataUtil.clip(clientVO.getAddress2(), 80));
		address.setCountry(jurisdiction.getCountry());
		address.setRegion(jurisdiction.getRegion());
		address.setCountyCode(jurisdiction.getCounty());
		address.setTownCity(jurisdiction.getCity());
		address.setPostcode(clientVO.getPostalCode());
		address.setGeoCode(jurisdiction.getGeoCode9());

		return account;
	}

	private AccountStatusEnum determineAccountStatus(ClientVO clientVO) {
		AccountStatusEnum status = AccountStatusEnum.C;

		if(!clientVO.isInactive()) {
			status = AccountStatusEnum.O;
		}

		return status;
	}

	private String determineEmail(ClientVO clientVO) {
		String email = clientVO.getEmail();

		if(email != null && email.length() > 25) {
			email = null;
		}

		return email;
	}

	private ExtAccAddress getDefaultPostAddress(ExternalAccount account) {
		return account.getExternalAccountAddresses().stream()
		        .filter(address -> DataUtil.convertToBoolean(address.getDefaultInd()) && "POST".equals(address.getAddressType()))
				.findFirst()
				.orElse(null);
	}

	private boolean isNewClient(ClientVO clientVO) {
		boolean isNew = false;

		if(clientVO.getExternalId() == null || clientVO.getExternalId().isBlank()) {
			isNew = true;
		}

		return isNew;
	}

	/**
	 * Do to the unique constraint on the address code, this method resets 
	 * the codes to temporary values so that later they can be re-sequenced to 
	 * their proper values. Not doing this will result in unique constraint 
	 * errors.
	 */
	private ExternalAccount preSequenceAddressCode(ExternalAccount account) {
		Queue<Integer> reverseSequence = new LinkedList<>();
		
		if(account.getExternalAccountAddresses() != null) {

		    IntStream.rangeClosed(1, account.getExternalAccountAddresses().size())
		            .forEach(code -> reverseSequence.add((code + 100) * -1));
		
		    account.getExternalAccountAddresses().stream()
		           .forEach(address -> address.setAddressCode(String.valueOf(reverseSequence.poll())));
		}
				
		return account;
	}
	
	/**
	 * Re-sequences each of the account's address code.
	 *  
	 * @param ExternalAccount account 
	 * @return ExternalAccount account with updated address codes 
	 */
	private ExternalAccount resequenceAddressCode(ExternalAccount account) {
		Queue<Integer> fowardSequence = new LinkedList<>();
		IntStream.rangeClosed(2, account.getExternalAccountAddresses().size())
                .forEach(code -> fowardSequence.add(code));

		account.getExternalAccountAddresses().stream()
		.forEach(address -> {	
			if(address.isDefaultBilling()) {
				address.setAddressCode("1");
			} else {
				address.setAddressCode(String.valueOf(fowardSequence.poll()));
			}
		});
				
		return account;
	}
	
	private boolean hasDefaultBillingAddress(ClientVO clientVO) {
		return clientVO.getAddressInternalId() == null || clientVO.getAddressInternalId().isBlank() ? false : true;
	}
	
}
