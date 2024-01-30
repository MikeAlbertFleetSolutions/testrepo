package com.mikealbert.accounting.processor.service;

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
import javax.transaction.Transactional;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.client.suiteanalytics.VendorSuiteAnalyticsService;
import com.mikealbert.accounting.processor.client.suitetalk.VendorSuiteTalkService;
import com.mikealbert.accounting.processor.dao.ExtAccAddressDAO;
import com.mikealbert.accounting.processor.dao.ExternalAccountDAO;
import com.mikealbert.accounting.processor.entity.ExtAccAddress;
import com.mikealbert.accounting.processor.entity.ExtAccBankAcc;
import com.mikealbert.accounting.processor.entity.ExternalAccount;
import com.mikealbert.accounting.processor.entity.ExternalAccountPK;
import com.mikealbert.accounting.processor.enumeration.PaymentMethodEnum;
import com.mikealbert.accounting.processor.enumeration.TaxFormEnum;
import com.mikealbert.accounting.processor.enumeration.VendorAddressFieldEnum;
import com.mikealbert.accounting.processor.enumeration.VendorFieldEnum;
import com.mikealbert.accounting.processor.exception.AddressLinkedToVehicleMovementException;
import com.mikealbert.accounting.processor.helper.InboundVendorDataHelper;
import com.mikealbert.accounting.processor.vo.TaxJurisdictionVO;
import com.mikealbert.accounting.processor.vo.VendorVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;
import com.mikealbert.util.data.DataUtil;

@Service("vendorService")
public class VendorServiceImpl extends BaseService implements VendorService {
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource ExtAccAddressDAO extAccAddressDAO;
	@Resource VendorSuiteAnalyticsService vendorSuiteAnalyticsService;
	@Resource VendorSuiteTalkService vendorSuiteTalkService;
	@Resource XRefService xRefService;
	@Resource Validator validator;
	@Resource TaxJurisdictionService taxJurisdictionService;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());	
	
	private static final String CHILD_VENDOR_FLAG = "CHILD_VENDOR_FLAG";
	private static final String CHILD_PARENT_ACCOUNT_C_ID = "CHILD_PARENT_ACCOUNT_C_ID";
	private static final String CHILD_PARENT_ACCOUNT_TYPE = "CHILD_PARENT_ACCOUNT_TYPE";	
	private static final String CHILD_PARENT_ACCOUNT_CODE = "CHILD_PARENT_ACCOUNT_CODE";	
	private static final String OLD_W9_ADDRESS_TOKEN = "OLDW9";


	/**
	 * Obfuscates the data based upon the database algorithms.
	 * The database algorithm is used to ensure downstream apps, 
	 * using the same algorithm, can decrypt the data.
	 * 
	 * @param data to obfuscate
	 */
	@Override
	public String desencrypt(String data) {
		return externalAccountDAO.desencrypt(data);
	}

	/**
	 * Creates or Updates a vendor account in willow based upon inbound vendor changes
	 * 
	 * @param Map<String, String> map of the inbound vendor data
	 * @return ExternalAccount or null when vendor does not have an address
	 */
	@Override
	@Transactional
	public ExternalAccount upsertVendor(Map<String, String> vendorMap, List<Map<String, Object>> addresses) throws Exception {		
		VendorVO vendor = null;
		ExternalAccount account = null;
		ExternalAccount persistedAccount;
		List<Map<String, Object>> childAddresses = null;
		Map<String, String> childVendorMap = null;
		List<Map<String, Object>> childVendorAddresses = null;
		Map<String, String> mappedNewAddressIds;
		
		childAddresses = filterChildAddresses(addresses);
		
		if(addresses == null || addresses.isEmpty() || (childAddresses.size() == 1 && addresses.size() == 1)) {
			LOG.warn("Account {} could not be updated because it does not have an address. At lease one address is required.", vendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName()));
			return null;
		}

		vendor = bindVendor(vendorMap);
		super.validate(vendor);
		
		account = bindAccount(vendor);
		account = preSequenceAddressCode(account);
		account = bindNewAddress(account, filterNewAddresses(account, addresses));
		account = bindUpdatedAddresses(account, filterUpdatedAddresses(addresses));
		account = bindBankAccounts(account, vendorMap);				
		account = resequenceAddressCode(account);			
		account = setDefaultAddress(account, addresses);
		
		mappedNewAddressIds = mapNewAddressIds(account);
		
		//LOG.info("Saving Account: " + account.toString());
		persistedAccount = externalAccountDAO.save(account);
		
		for(Map<String, Object> address : childAddresses) {
			address.replace(VendorAddressFieldEnum.CHILD_VENDOR.getName(), "F");			
			
			childVendorMap = bindChildVendorMap(persistedAccount, vendorMap, address);
			
			childVendorAddresses = new ArrayList<>(0);
			childVendorAddresses.add(address);
			
			upsertVendor(childVendorMap, childVendorAddresses);			
		}
		
		updateVendorIds(persistedAccount, vendorMap, mappedNewAddressIds);
			
		return persistedAccount;
	}
		
	/**
	 * Determines which address(es) have been deleted from Vendor (parent only) account. 
	 * 
	 * @param ExternalAccount vendor's account
	 * @param List<Map<String, Object>> inbound vendor addresses
	 * @return All the address ids that have been removed from the vendor and child vendors.
	 */
	public List<Long> reconcileDeletedAddresses(ExternalAccount account, List<Map<String, Object>> addresses) throws Exception {
		List<Long> deletedAddressIds = new ArrayList<>(0);
		
		if(account == null || addresses == null || addresses.isEmpty()) return deletedAddressIds;
				
		filterDeletedAddresses(account.getExternalAccountAddresses(), addresses).stream()
		.forEach(accountAddress -> {
			try {
				deleteAddress((accountAddress));
				deletedAddressIds.add(accountAddress.getEaaId());
			} catch(AddressLinkedToVehicleMovementException e) {
				LOG.info("Could not delete account {} address eaaId {}. Error: {}", accountAddress.getExternalAccount().getId().getAccountCode(), accountAddress.getEaaId() , e.getMessage());
			}
		});
			
		return deletedAddressIds;
	}
	
	/**
	 * Closes the vendor child accounts (and respective suppliers) that are no longer in the inbound vendor address list
	 * 
	 * @param ExternalAccount vendor's account
	 * @param List<Map<String, Object>> inbound vendor addresses
	 * @return List<ExternalAccounts> closed vendor accounts
	 */
	@Transactional
	public List<ExternalAccount> closeChildAccounts(ExternalAccount account, List<Map<String, Object>> addresses) throws Exception {
		List<ExtAccAddress> relatedAddresses;
		List<Long> deletedAddressIds = new ArrayList<>(0);
		List<ExternalAccount> closedAccounts = new ArrayList<>(0);
			
		if(account == null) return closedAccounts;
		
		account = externalAccountDAO.findById(account.getId()).orElse(null);
		
		if(account == null || account.getExternalAccountAddresses().isEmpty() || addresses == null || addresses.isEmpty()) return closedAccounts;
		
		relatedAddresses = extAccAddressDAO.findByRelatedAccountId(account.getId().getCId(), account.getId().getAccountType(), account.getId().getAccountCode());
		filterDeletedAddresses(relatedAddresses, addresses).stream()
		.filter(accountAddress -> "POST".equals(accountAddress.getAddressType()) && "Y".equals(accountAddress.getDefaultInd())) 
		.forEach(accountAddress -> deletedAddressIds.add(accountAddress.getEaaId()));
		
		if(!deletedAddressIds.isEmpty()) {
			closedAccounts = externalAccountDAO.findByExternalAccountAddresses(deletedAddressIds);
			closedAccounts.stream()
			.filter(acct -> acct.getParentAccount() != null)
			.forEach(childAccount -> { 
				childAccount.setAccStatus("C");
				childAccount.setParentEntity(null);
				childAccount.setParentAccountType(null);
				childAccount.setParentAccount(null);					
				DataUtil.getNullSafeStream(childAccount.getSuppliers()).forEach(supp -> supp.setInactiveInd("Y"));
			});
			
			externalAccountDAO.saveAll(closedAccounts);			
		}
						
		return closedAccounts;		
	}
		    
	/**
	 * Resets the default address based on current state of the account
	 * 
	 * @param ExternalAccount
	 * @return ExternalAccount with default address set
	 * @exception Exception
	 */
	@Transactional
	public ExternalAccount resetDefaultAddress(ExternalAccount account, List<Map<String, Object>> addresses) throws Exception {
		account = externalAccountDAO.findById(account.getId()).orElse(null);
		account = setDefaultAddress(account, addresses);
		account = externalAccountDAO.save(account);
		return account;		
	}
	
	/**
	 * Retrieves the address(es) of the inbound vendor from the external system
	 * 
	 * @param vendorMap map of inbound vendor data
	 * @return List<Map<String, Object>> of the inbound address(es)
	 * @throws Exception
	 */
    public List<Map<String, Object>> getAddresses(Map<String, String> vendorMap) throws Exception {
    	return vendorSuiteAnalyticsService.getAddresses(vendorMap.get(VendorFieldEnum.ENTITY_ID.getName()));
    }
    
	/**
	 * Retrieves all vendor addresses from the external system 
	 * 
	 * @return List<Map<String, Object>> of external system's vendor addresses
	 * @throws Exception
	 */
    public List<Map<String, Object>> getAddresses() throws Exception {
    	return vendorSuiteAnalyticsService.getAddresses();
    }

	/**
	 * Retrieves vendor's address from the external system based on the external id
	 * @param String externalId MAFS identifier, eaaId, to the vendor address in the external system
	 * @return List<Map<String, Object>> of external system vendor's address
	 * @throws Exception
	 */
	@Override
	public List<Map<String, Object>> getAddresses(String externalId) throws Exception {
    	return vendorSuiteAnalyticsService.getAddressesByExternalId(externalId);
	}

	@Override
	public List<Map<String, Object>> getVendors(Date from, Date to) throws Exception {
		return vendorSuiteAnalyticsService.getVendors(from, to);
	}	
	
    /**
     * Sets the default indicator flag on the vendor account's address
     * 
     * @param account
     * @param addresses List of addresses in the accounting system for this vendor. Assumes addresses are up-to-date.
     * @return ExternalAccount
     * @throws Exception
     */
	public ExternalAccount setDefaultAddress(ExternalAccount account, List<Map<String, Object>> addresses) throws Exception {
		ExtAccAddress w9Address;
		int size = account.getExternalAccountAddresses().size();
		
		if(size == 0) return account;
		
		if(size == 1) {
			account.getExternalAccountAddresses().stream()
			.forEach(address -> address.setDefaultInd("Y"));
		} else {
			w9Address = account.getExternalAccountAddresses().stream()
					.filter(address -> address.isDefaultBilling())
					.findAny()
					.orElse(null);

			account.getExternalAccountAddresses().stream()
			.peek(address -> address.setDefaultInd(null))	
			.filter(address -> isDefaultable(address, addresses))
			.filter(address -> !address.isDefaultBilling())			
			.reduce((first, second) -> second)
			.orElse(w9Address).setDefaultInd("Y");
		}
		
		return account;
	}	
	
	/**
	 * Extracts the child address(es) from the list
	 * 
	 * @param vendorAddresses
	 * @return List of only the child address(es)
	 */
	private List<Map<String, Object>> filterChildAddresses(List<Map<String, Object>> vendorAddresses) {
		return DataUtil.getNullSafeStream(vendorAddresses)
				.filter(address -> DataUtil.convertToBoolean((String) address.get(VendorAddressFieldEnum.CHILD_VENDOR.getName())))
				.collect(Collectors.toList());
	}
		
	/**
	 * A new address is an inbound vendor address that does not exist in the willow database.
	 * 
	 * @param ExternalAccount
	 * @param vendorAddresses inbound address from external system
	 * @return Filtered list containing only new address to be added to the vendor's account
	 */
	private List<Map<String, Object>> filterNewAddresses(ExternalAccount account, List<Map<String, Object>> vendorAddresses) {
		List<Map<String, Object>> newVendorAddresses;

		newVendorAddresses = vendorAddresses.stream()
				.filter(address -> !DataUtil.convertToBoolean((String) address.get(VendorAddressFieldEnum.CHILD_VENDOR.getName())))
				.collect(Collectors.toList());

		newVendorAddresses.removeIf(vendorAddress -> account.getExternalAccountAddresses().stream()
				.anyMatch(accountAddress -> vendorAddress.get(VendorAddressFieldEnum.EXTERNAL_ID.getName()) != null && accountAddress.getEaaId().equals(Long.parseLong(String.valueOf(vendorAddress.get(VendorAddressFieldEnum.EXTERNAL_ID.getName()))))));

		newVendorAddresses.stream()
		.forEach(vendorAddress -> vendorAddress.replace(VendorAddressFieldEnum.EXTERNAL_ID.getName(), null));

		return newVendorAddresses;
	}

	private List<Map<String, Object>> filterUpdatedAddresses(List<Map<String, Object>> vendorAddresses) {
		return vendorAddresses.stream()
				.filter(address -> !DataUtil.convertToBoolean((String) address.get(VendorAddressFieldEnum.CHILD_VENDOR.getName())))				
				.filter(address -> address.get(VendorAddressFieldEnum.EXTERNAL_ID.getName()) != null ? true : false)
				.collect(Collectors.toList());
	}
	
	private List<ExtAccAddress> filterDeletedAddresses(List<ExtAccAddress> accountAddresses, List<Map<String, Object>> vendorAddresses) {
		accountAddresses.removeIf(accountAddress -> vendorAddresses.stream()
				.anyMatch(address ->  accountAddress.getEaaId().equals(Long.parseLong(String.valueOf(address.get(VendorAddressFieldEnum.EXTERNAL_ID.getName()))))));
		
		return accountAddresses;
	}

	/**
	 * Maps the new address(es) code and external systems' ids.
	 *  
	 * @param persistedAccount The ExternalAccount saved to the database
	 * @param account The ExternalAccount that was sent to be saved to the database
	 * @return Map<String, String> contains the address code to external system address id for each address
	 */
	private Map<String, String> mapNewAddressIds(ExternalAccount account) {
		Map<String, String> id = new HashMap<>();
		
		account.getExternalAccountAddresses().stream()
		.filter(address -> address.isNewAddress())
		.forEach(address -> {
			id.put(address.getAddressCode(), address.getInternalId());;
		});
		
		return id;
	}
	
	/**
	 * Updates the address'es external id field in the external system
	 * 
	 * @param ExternalAccount
	 * @param vendor
	 * @param addressIds map of address code to external system's internal id
	 * @throws Exception
	 */
	private void updateVendorIds(ExternalAccount account, Map<String, String> vendor, Map<String, String> addressIds) throws Exception {
		String parentAccountCode = vendor.containsKey(CHILD_PARENT_ACCOUNT_CODE) ? vendor.get(CHILD_PARENT_ACCOUNT_CODE) :  vendor.get(VendorFieldEnum.ACCOUNT_CODE.getName());
		
		account.getExternalAccountAddresses().stream()
		.filter(address -> addressIds.containsKey(address.getAddressCode()))
		.forEach(address -> {
			try {
				vendorSuiteTalkService.updateVendorExternalIdAndAddressExternalId(vendor.get(VendorFieldEnum.ENTITY_ID.getName()), parentAccountCode, addressIds.get(address.getAddressCode()), String.valueOf(address.getEaaId()));
			} catch (Exception e) {
				LOG.error("ERRORR" + e.getMessage());
				throw new RuntimeException(e);
			}			
		});				
	}
		
	private Map<String, String> bindChildVendorMap(ExternalAccount parentAccount, Map<String, String> vendorMap, Map<String, Object> addressMap) {
		ExternalAccount account = null;
		Map<String, String> childVendorMap;
		Long eaaId; 
		String childAccountCode;
		
		if(addressMap.get(VendorAddressFieldEnum.EXTERNAL_ID.getName()) == null) {			
			childAccountCode = generateAccountCode();			
		} else {
			eaaId = Long.parseLong(String.valueOf(addressMap.get(VendorAddressFieldEnum.EXTERNAL_ID.getName())));
			account = externalAccountDAO.findByExternalAccountAddress(eaaId);			
			childAccountCode = parentAccount.getId().getAccountCode().equals(account.getId().getAccountCode()) ? generateAccountCode() : account.getId().getAccountCode();			
		}
				
		childVendorMap =  new HashMap<String, String>(vendorMap);
		childVendorMap.put(VendorFieldEnum.EXTERNAL_ID.getName(), account == null ? null : account.getId().getAccountCode());
		childVendorMap.put(CHILD_VENDOR_FLAG, "true");
		childVendorMap.put(CHILD_PARENT_ACCOUNT_C_ID, String.valueOf(parentAccount.getId().getCId()));		
		childVendorMap.put(CHILD_PARENT_ACCOUNT_TYPE, parentAccount.getId().getAccountType());
		childVendorMap.put(CHILD_PARENT_ACCOUNT_CODE, parentAccount.getId().getAccountCode());	
		childVendorMap.replace(VendorFieldEnum.ACCOUNT_CODE.getName(), childAccountCode);		
		childVendorMap.replace(VendorFieldEnum.PAYEE_NAME.getName(), (String)addressMap.get(VendorAddressFieldEnum.ATTENTION.getName()));
		
		return childVendorMap;		
	}
	
	private String generateAccountCode() {
		String accountCode = externalAccountDAO.nextChildAccountCode();
		accountCode = String.format("%s%s%s", CHILD_ACCOUNT_CODE_PREFIX, accountCode, CHILD_ACCOUNT_CODE_SUFFIX);		
		return accountCode;
	}
	
	private VendorVO bindVendor(Map<String, String> vendorMap) throws Exception {

		//Child accounts do not have a corresponding entity in the accounting system
		if(vendorMap.get(CHILD_PARENT_ACCOUNT_C_ID) == null) {
			vendorMap = bridgeVendorMap(vendorMap, vendorSuiteTalkService.get(vendorMap.get(VendorFieldEnum.ENTITY_ID.getName()), null));
		}
		
		String payeeOrCompanyName = vendorMap.get(VendorFieldEnum.PAYEE_NAME.getName()) == null ? vendorMap.get(VendorFieldEnum.ACCOUNT_NAME.getName()) : vendorMap.get(VendorFieldEnum.PAYEE_NAME.getName());
		String cId = xRefService.getInternalValue(XRefGroupNameEnum.COMPANY, vendorMap.get(VendorFieldEnum.C_ID.getName()));
		String paymentMethod = xRefService.getInternalValue(XRefGroupNameEnum.PAYMENT_METHOD, vendorMap.get(VendorFieldEnum.PAYMENT_METHOD.getName()).toUpperCase());
		String paymentTerm = xRefService.getInternalValue(XRefGroupNameEnum.VENDOR_CREDIT_TERM, vendorMap.get(VendorFieldEnum.PAYMENT_TERM.getName()).toUpperCase());
		
		VendorVO vendor = new VendorVO()
				.setParentCId(vendorMap.get(CHILD_PARENT_ACCOUNT_C_ID) == null ? null : Long.valueOf(vendorMap.get(CHILD_PARENT_ACCOUNT_C_ID)))
				.setParentAccountType(vendorMap.get(CHILD_PARENT_ACCOUNT_TYPE))
				.setParentAccount(vendorMap.get(CHILD_PARENT_ACCOUNT_CODE))
				.setEntityId(vendorMap.get(VendorFieldEnum.ENTITY_ID.getName()))
				.setcId(Long.valueOf(cId))
				.setAccountCode(vendorMap.get(VendorFieldEnum.ACCOUNT_CODE.getName()))
				.setAccountName(DataUtil.substr(payeeOrCompanyName, 0, 80))
				.setShortName(DataUtil.substr(payeeOrCompanyName, 0, 25))
				.setPayeeName(DataUtil.substr(payeeOrCompanyName, 0, 80))		
				.setAccStatus(DataUtil.convertToBoolean(vendorMap.get(VendorFieldEnum.INACTIVE.getName())) ? "C" : "O")
				.setTaxIdNum(desencrypt(InboundVendorDataHelper.convertTaxId(vendorMap)))
				.setValidTaxId(!InboundVendorDataHelper.convertTaxId(vendorMap).equals(InboundVendorDataHelper.DEFAULT_TAX_ID))
				.setRegName(vendorMap.get(VendorFieldEnum.COMPANY_NAME.getName()))
				.setEntityId(vendorMap.get(VendorFieldEnum.ENTITY_ID.getName()))
				.setGroupCode(DataUtil.convertToBoolean(vendorMap.get(VendorFieldEnum.GROUP_CODE.getName())) ? TaxFormEnum.MISC.getValue() : TaxFormEnum.NONE.getValue())
				.setPaymentMethod(paymentMethod)
				.setPhone(vendorMap.get(VendorFieldEnum.PHONE.getName()))
				.setFax(vendorMap.get(VendorFieldEnum.FAX.getName()))
				.setOrgizationType(vendorMap.get(VendorFieldEnum.CATEGORY.getName()) == null ? "OTHER" : vendorMap.get(VendorFieldEnum.CATEGORY.getName()).toUpperCase())  //TOD xRef here?
				.setPaymentTerm(paymentTerm)
				.setBankName(vendorMap.get(VendorFieldEnum.BANK_NAME.getName()))
				.setBankAccountName(vendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName()))
				.setBankAccountNumber(vendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName()))
				.setBankSortCode(vendorMap.get(VendorFieldEnum.BANK_NUMBER.getName()))
				.setIsDeliveringDealer(vendorMap.get(VendorFieldEnum.DELIVERING_DEALER.getName()))
				.setContactJobTitle(vendorMap.get(VendorFieldEnum.CONTACT_JOB_TITLE.getName()))
				.setContactFirstname(vendorMap.get(VendorFieldEnum.CONTACT_FIRST_NAME.getName()))
				.setContactLastName(vendorMap.get(VendorFieldEnum.CONTACT_LAST_NAME.getName()))
				.setInactive(DataUtil.convertToBoolean(vendorMap.get(VendorFieldEnum.INACTIVE.getName())));
		
		return vendor;		
	}

	/**
	 * Used to load the vendor mapp with details from the API instead of directly from the external system's database.
	 * This is a temporary until we refactor to use of the VendorVO and remove map.
	 * 
	 * @param vendorMap to enrich with the details from the external system's API
	 * @param vendorVO containing the vendor details from the external system's API
	 * @return vendorMap containing the vendor details
	 */
	private Map<String, String> bridgeVendorMap(Map<String, String> vendorMap, VendorVO vendorVO) {
		vendorMap.put(VendorFieldEnum.EXTERNAL_ID.getName(), vendorVO.getAccountCode());  //TODO There should be an property named specifically externalId
		vendorMap.put(VendorFieldEnum.C_ID.getName(), vendorVO.getcId().toString());
		vendorMap.put(VendorFieldEnum.ACCOUNT_NAME.getName(), vendorVO.getAccountName());
		vendorMap.put(VendorFieldEnum.INACTIVE.getName(), String.valueOf(vendorVO.isInactive()));
		vendorMap.put(VendorFieldEnum.EMAIL.getName(), vendorVO.getEmail());
		vendorMap.put(VendorFieldEnum.TAX_ID.getName(), vendorVO.getTaxIdNum());
		vendorMap.put(VendorFieldEnum.PAYMENT_METHOD.getName(), vendorVO.getPaymentMethod());
		vendorMap.put(VendorFieldEnum.GROUP_CODE.getName(), vendorVO.getGroupCode());
		vendorMap.put(VendorFieldEnum.FAX.getName(), vendorVO.getFax());
		vendorMap.put(VendorFieldEnum.PHONE.getName(), vendorVO.getPhone());
		vendorMap.put(VendorFieldEnum.CATEGORY.getName(), vendorVO.getOrgizationType());
		vendorMap.put(VendorFieldEnum.PAYMENT_TERM.getName(), vendorVO.getPaymentTerm());
		vendorMap.put(VendorFieldEnum.PAYEE_NAME.getName(), vendorVO.getPayeeName());
		vendorMap.put(VendorFieldEnum.BANK_NAME.getName(), vendorVO.getBankName());
		vendorMap.put(VendorFieldEnum.BANK_ACCOUNT_NAME.getName(), vendorVO.getBankAccountName());
		vendorMap.put(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName(), vendorVO.getBankAccountNumber());
		vendorMap.put(VendorFieldEnum.BANK_ACCOUNT_TYPE_ID.getName(), vendorVO.getBankAccountTypeId() == null ? null : vendorVO.getBankAccountTypeId() .toString());
		vendorMap.put(VendorFieldEnum.BANK_NUMBER.getName(), vendorVO.getBankSortCode());
		vendorMap.put(VendorFieldEnum.DELIVERING_DEALER.getName(), vendorVO.getIsDeliveringDealer());
		vendorMap.put(VendorFieldEnum.CONTACT_JOB_TITLE.getName(), vendorVO.getContactJobTitle());
		vendorMap.put(VendorFieldEnum.CONTACT_FIRST_NAME.getName(), vendorVO.getContactFirstname());
		vendorMap.put(VendorFieldEnum.CONTACT_LAST_NAME.getName(), vendorVO.getContactLastName());
		vendorMap.put(VendorFieldEnum.COMPANY_NAME.getName(), vendorVO.getRegName());		

		return vendorMap;
	}
	
	private ExternalAccount bindAccount(VendorVO vendor) throws Exception {								
		ExternalAccountPK pk = new ExternalAccountPK(vendor.getcId(), vendor.getAccountType(), vendor.getAccountCode());
		ExternalAccount account = externalAccountDAO.findById(pk).orElse(new ExternalAccount(pk, true));

		account.setParentEntity(vendor.getParentCId());
		account.setParentAccountType(vendor.getParentAccountType());
		account.setParentAccount(vendor.getParentAccount());
		account.setEntityId(vendor.getEntityId());
		account.setAccStatus(vendor.getAccStatus());
		account.setEmail(null);		
		account.setTelephoneNumber(vendor.getPhone());
		account.setFaxCode(vendor.getFax());
		account.setTaxRegNo(vendor.getTaxIdNum());
		account.setRegName(vendor.getRegName());
		account.setAccountName(vendor.getAccountName());
		account.setShortName(vendor.getShortName());
		account.setPayeeName(vendor.getPayeeName());		
		account.setCurrencyCode(vendor.getCurrencyCode());
		account.setPaymentInd(vendor.getPaymentInd());
		account.setUpfitInd(vendor.getUpfitInd());
		account.setInternationalInd(vendor.getInternationalInd());
		account.setWebQuotesReqCcApproval(vendor.getWebQuotesReqCcApproval());
		account.setWebQuotesReqFaApproval(vendor.getWebQuotesReqFaApproval());
		account.setGroupCode(vendor.getGroupCode());
		account.setPaymentMethod(vendor.getPaymentMethod());
		account.setOrganisationType(vendor.getOrgizationType());
		account.setCrtCId(vendor.getcId());
		account.setCrtExtAccType(vendor.getAccountType());
		account.setCreditTermsCode(vendor.getPaymentTerm());
		account.setTaxInd(vendor.getTaxInd());
		account.setValidTaxId(vendor.isValidTaxId());
		account.setOccupation(vendor.getContactJobTitle());
		account.setFirstName(vendor.getContactFirstname());
		account.setLastName(vendor.getContactLastName());
		account.setAccStatus(vendor.isInactive() ? "C" : "O");
		
		if(PaymentMethodEnum.ACH.getValue().equals(account.getPaymentMethod()) || PaymentMethodEnum.INTERCOMPANY.getValue().equals(account.getPaymentMethod())) {
			account.setBankName(vendor.getBankName());
			account.setBankAccountName(vendor.getBankAccountName());
			account.setBankAccountNumber(vendor.getBankAccountNumber());
			account.setBankSortCode(vendor.getBankSortCode());			
		} else {
			account.setBankName(null);
			account.setBankAccountName(null);
			account.setBankAccountNumber(null);
			account.setBankSortCode(null);			
		}
		
		if(account.getExternalAccountAddresses() == null) {
			account.setExternalAccountAddresses(new ArrayList<>());			
		} 
		
		if(account.getExternalAccountBankAccounts() == null) {
			account.setExternalAccountBankAccounts(new ArrayList<>());			
		}
		

		DataUtil.getNullSafeStream(account.getSuppliers())
		.forEach(supplier -> supplier.setInactiveInd(vendor.isInactive() ? "Y" : supplier.getInactiveInd()));
		
		return account;
	}
	
	private ExternalAccount bindNewAddress(ExternalAccount vendor, List<Map<String, Object>> addressMaps) throws Exception {		
		int counter = 1000;
		for(Map<String, Object> addressMap : addressMaps) {
			boolean isW9 = "T".equalsIgnoreCase((String)addressMap.get(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName())) ? true : false;
			isW9 = isW9 && vendor.getParentAccount() == null;
			
			String internalId = String.valueOf(addressMap.get(VendorAddressFieldEnum.INTERNAL_ID.getName()));
			String addressType = "POST";
			String addressCode = String.valueOf(counter += 1);
			String addressLine1 = (String) addressMap.get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName());
			String addressLine2 = (String) addressMap.get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName());
			String addressLine3 = (String) addressMap.get(VendorAddressFieldEnum.ADDRESS_LINE_3.getName());
			String country = (String) addressMap.get(VendorAddressFieldEnum.COUNTRY.getName());
			String region = (String) addressMap.get(VendorAddressFieldEnum.STATE.getName());
			String county = (String) addressMap.get(VendorAddressFieldEnum.COUNTY.getName());			
			String city = (String) addressMap.get(VendorAddressFieldEnum.CITY.getName());		
			String zip = InboundVendorDataHelper.convertZip(addressMap);
			String rawZip = (String) addressMap.get(VendorAddressFieldEnum.ZIP.getName());
									
			TaxJurisdictionVO jurisdiction = taxJurisdictionService.find(country, region, county, city, zip, rawZip);				
			if(jurisdiction == null) {
				throw new Exception(String.format("Could not locate GEO Code for vendor account %s based on input - country: %s, region: %s, city: %s, zip: %s", vendor.getId().getAccountCode(), country, region, city, zip ));
			}			
			
			ExtAccAddress address = new ExtAccAddress();
			address.setNewAddress(true);
			address.setInternalId(internalId);
			address.setDefaultBilling(isW9);
			address.setAddressCode(addressCode);
			address.setAddressType(addressType);
			address.setAddressLine1(addressLine1);
			address.setAddressLine2(addressLine2);
			address.setAddressLine3(addressLine3);			
			address.setCountry(jurisdiction.getCountry());
			address.setRegion(jurisdiction.getRegion());
			address.setCountyCode(jurisdiction.getCounty());
			address.setTownCity(jurisdiction.getCity());
			address.setPostcode(rawZip);
			address.setGeoCode(jurisdiction.getGeoCode9());
			address.setExternalAccount(vendor);
			
			vendor.getExternalAccountAddresses().add(address);
		}
		
		return vendor;
	}
	
	private ExternalAccount bindUpdatedAddresses(ExternalAccount vendor, List<Map<String, Object>> addressMaps) throws Exception {				
		for(Map<String, Object> addressMap : addressMaps) {
			boolean isW9 = "T".equalsIgnoreCase((String)addressMap.get(VendorAddressFieldEnum.DEFAULT_BILLING_ADDRESS.getName())) ? true : false;
			Long externalId = addressMap.get(VendorAddressFieldEnum.EXTERNAL_ID.getName()) == null ? null : Long.valueOf((String)addressMap.get(VendorAddressFieldEnum.EXTERNAL_ID.getName()));
			
			for(ExtAccAddress address : vendor.getExternalAccountAddresses()) {		
				if(address.getEaaId().equals(externalId)) {
					String addressType = "POST";					
					String addressLine1 = (String) addressMap.get(VendorAddressFieldEnum.ADDRESS_LINE_1.getName());
					String addressLine2 = (String) addressMap.get(VendorAddressFieldEnum.ADDRESS_LINE_2.getName());
					String addressLine3 = (String) addressMap.get(VendorAddressFieldEnum.ADDRESS_LINE_3.getName());
					String country = (String) addressMap.get(VendorAddressFieldEnum.COUNTRY.getName());
					String region = (String) addressMap.get(VendorAddressFieldEnum.STATE.getName());
					String county = (String) addressMap.get(VendorAddressFieldEnum.COUNTY.getName());					
					String city = (String) addressMap.get(VendorAddressFieldEnum.CITY.getName());		
					String zip = InboundVendorDataHelper.convertZip(addressMap);
					String rawZip = (String) addressMap.get(VendorAddressFieldEnum.ZIP.getName());
																							
					TaxJurisdictionVO jurisdiction = taxJurisdictionService.find(country, region, county, city, zip, rawZip);									
					if(jurisdiction == null) {
						throw new Exception(String.format("Could not locate GEO Code for vendor account %s based on input - country: %s, region: %s, county: %s, city: %s, zip: %s", vendor.getId().getAccountCode(), country, region, county, city, zip ));						
					}
					
					address.setDefaultBilling(isW9);
					address.setAddressType(addressType);					
					address.setAddressLine1(addressLine1);
					address.setAddressLine2(addressLine2);
					address.setAddressLine3(addressLine3);					
					address.setCountry(jurisdiction.getCountry());
					address.setRegion(jurisdiction.getRegion());
					address.setCountyCode(jurisdiction.getCounty());
					address.setTownCity(jurisdiction.getCity());
					address.setPostcode(rawZip);
					address.setGeoCode(jurisdiction.getGeoCode9());	
					
					break;
				}
			}					
		}
		
		return vendor;
	}	
		
	private ExternalAccount bindBankAccounts(ExternalAccount vendor, Map<String, String> vendorMap) throws Exception {
		vendor.getExternalAccountBankAccounts().clear();
		
	    if(vendorMap.get(VendorFieldEnum.BANK_ACCOUNT_TYPE_ID.getName()) != null) {
			if( PaymentMethodEnum.ACH.getValue().equals(vendor.getPaymentMethod()) || PaymentMethodEnum.INTERCOMPANY.getValue().equals(vendor.getPaymentMethod())) {
				String defaultInd = "Y";
				String bankName = vendorMap.get(VendorFieldEnum.BANK_NAME.getName());
				String bankAccountName = vendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NAME.getName());
				String bankAccountNumber = vendorMap.get(VendorFieldEnum.BANK_ACCOUNT_NUMBER.getName());
				String bankSortCode = vendorMap.get(VendorFieldEnum.BANK_NUMBER.getName());
				Long bankAccountTypeId = InboundVendorDataHelper.convertBankAccountTypeId(vendorMap);

				ExtAccBankAcc eaBankAccount = new ExtAccBankAcc();
				eaBankAccount.setBankAccountName(bankAccountName);
				eaBankAccount.setBankName(bankName);
				eaBankAccount.setBankSortCode(bankSortCode);
				eaBankAccount.setBankAccountNumber(bankAccountNumber);
				eaBankAccount.setDefaultInd(defaultInd);
				eaBankAccount.setExternalAccount(vendor);
				eaBankAccount.setEabaBaId(bankAccountTypeId);
				vendor.getExternalAccountBankAccounts().add(eaBankAccount);					
			}
	    }
	    	    
		return vendor;
	}	

	/**
	 * Do to the unique constraint on the address code, this method resets 
	 * the codes to temporary values so that later they can be re-sequenced to 
	 * their proper values. Not doing this will result in unique constraint 
	 * errors.
	 */
	private ExternalAccount preSequenceAddressCode(ExternalAccount account) {
		Queue<Integer> reverseSequence = new LinkedList<>();
		
		IntStream.rangeClosed(1, account.getExternalAccountAddresses().size())
		.forEach(code -> {
			reverseSequence.add((code + 100) * -1);			
		});
		
		account.getExternalAccountAddresses().stream()
		.forEach(address -> {	
			if("W9".equalsIgnoreCase(address.getAddressCode())) {
				address.setAddressCode(OLD_W9_ADDRESS_TOKEN);
			} else {				
				address.setAddressCode(String.valueOf(reverseSequence.poll()));
			} 
		});
				
		return account;
	}
	
	/**
	 * Re-sequences each of the vendor account's address code.
	 *  
	 * @param ExternalAccount account 
	 * @return ExternalAccount account with updated address codes 
	 */
	private ExternalAccount resequenceAddressCode(ExternalAccount account) {
		Queue<Integer> fowardSequence = new LinkedList<>();
		IntStream.rangeClosed(1, account.getExternalAccountAddresses().size())
		.forEach(code -> {
			fowardSequence.add(code);			
		});

		account.getExternalAccountAddresses().stream()
		.forEach(address -> {	
			if(address.isDefaultBilling() && account.isValidTaxId()) {
				address.setAddressCode("W9");
			} else {
				address.setAddressCode(String.valueOf(fowardSequence.poll()));
			}
		});
				
		return account;
	}
		
	/**
	 * Determines whether the account address is eligible to become the default.
	 * @param accountAddress Internal account address
	 * @param vendorAddresses List of vendor address from external system
	 * @return boolean indicating whether the account address is eligible to be default
	 */
	private boolean isDefaultable(ExtAccAddress accountAddress, List<Map<String, Object>> vendorAddresses) {
		
		if(accountAddress.getEaaId() == null) return true;
		
		List<Map<String, Object>> filteredAddresses = vendorAddresses.stream()
				.filter(address -> address.get(VendorAddressFieldEnum.EXTERNAL_ID.getName()) != null)
				.filter(address -> Long.valueOf((String)address.get(VendorAddressFieldEnum.EXTERNAL_ID.getName())).equals(accountAddress.getEaaId()))				
				.collect(Collectors.toList());
		
		return filteredAddresses != null && !filteredAddresses.isEmpty() ? true : false;
	}
	
	/**
	 * Deletes the account's address only when it is not linked to a vehicle movement
	 * @param accountAddress
	 * @exception AddressLinkedToVehicleMovementException raised when address is linked
	 */
	private void deleteAddress(ExtAccAddress accountAddress) throws AddressLinkedToVehicleMovementException {
		if(!extAccAddressDAO.isLinkedToVehicleMovement(accountAddress.getEaaId())) {
			extAccAddressDAO.deleteById(accountAddress.getEaaId());			
		} else {
			throw new AddressLinkedToVehicleMovementException("Address linked to vehicle movement");
		}
	}	
}
