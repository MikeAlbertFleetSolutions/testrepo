package com.mikealbert.accounting.processor.vo;

import com.mikealbert.accounting.processor.validation.VendorValidator;

@VendorValidator
public class VendorVO {
	
	private Long cId;
	
	private String accountType;
	
	private String accountCode;
	
	private String accountName;
	
	private String shortName;
	
	private String accStatus;
	
	private String email;
	
	private String taxIdNum;
	
	private String regName;
	
	private String entityId;
	
	private String groupCode;
	
	private String paymentMethod;
	
	private String phone;
	
	private String fax;
	
	private String orgizationType;
	
	private String paymentTerm;
	
	private String payeeName;
	
	private String currencyCode;
	
	private String paymentInd;
	
	private String upfitInd;
	
	private String internationalInd;
	
	private String webQuotesReqCcApproval;
	
	private String webQuotesReqFaApproval;
	
	private String taxInd;
	
	private String bankName;

	private Long bankAccountTypeId;
	
	private String bankAccountName;
	
	private String bankAccountNumber;
	
	private String bankSortCode;
	
	private String isDeliveringDealer;
	
	private String contactJobTitle;
	
	private String contactFirstname;
	
	private String contactLastName;
	
	private Long parentCId;
	
	private String parentAccountType;
	
	private String parentAccount;
	
	private boolean inactive;
	
	private boolean validTaxId;
	
	public VendorVO() {
		this.accountType = "S";
		this.currencyCode = "USD";
		this.paymentInd = "M";
		this.upfitInd = "N";
		this.internationalInd = "N";
		this.webQuotesReqCcApproval = "N";
		this.webQuotesReqFaApproval = "Y";
		this.taxInd = "N";
	}

	public Long getcId() {
		return cId;
	}

	public VendorVO setcId(Long cId) {
		this.cId = cId;
		return this;
	}

	public String getAccountType() {
		return accountType;
	}

	public VendorVO setAccountType(String accountType) {
		this.accountType = accountType;
		return this;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public VendorVO setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountName() {
		return accountName;
	}

	public VendorVO setAccountName(String accountName) {
		this.accountName = accountName;
		return this;
	}

	public String getShortName() {
		return shortName;
	}

	public VendorVO setShortName(String shortName) {
		this.shortName = shortName;
		return this;
	}

	public String getAccStatus() {
		return accStatus;
	}

	public VendorVO setAccStatus(String accStatus) {
		this.accStatus = accStatus;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public VendorVO setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getTaxIdNum() {
		return taxIdNum;
	}

	public VendorVO setTaxIdNum(String taxIdNum) {
		this.taxIdNum = taxIdNum;
		return this;
	}

	public String getRegName() {
		return regName;
	}

	public VendorVO setRegName(String regName) {
		this.regName = regName;
		return this;
	}

	public String getEntityId() {
		return entityId;
	}

	public VendorVO setEntityId(String entityId) {
		this.entityId = entityId;
		return this;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public VendorVO setGroupCode(String groupCode) {
		this.groupCode = groupCode;
		return this;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public VendorVO setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public VendorVO setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public String getFax() {
		return fax;
	}

	public VendorVO setFax(String fax) {
		this.fax = fax;
		return this;
	}

	public String getOrgizationType() {
		return orgizationType;
	}

	public VendorVO setOrgizationType(String orgizationType) {
		this.orgizationType = orgizationType;
		return this;
	}

	public String getPaymentTerm() {
		return paymentTerm;
	}

	public VendorVO setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
		return this;
	}

	public String getPayeeName() {
		return payeeName;
	}

	public VendorVO setPayeeName(String payeeName) {
		this.payeeName = payeeName;
		return this;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public VendorVO setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
		return this;
	}

	public String getPaymentInd() {
		return paymentInd;
	}

	public VendorVO setPaymentInd(String paymentInd) {
		this.paymentInd = paymentInd;
		return this;
	}

	public String getUpfitInd() {
		return upfitInd;
	}

	public VendorVO setUpfitInd(String upfitInd) {
		this.upfitInd = upfitInd;
		return this;
	}

	public String getInternationalInd() {
		return internationalInd;
	}

	public VendorVO setInternationalInd(String internationalInd) {
		this.internationalInd = internationalInd;
		return this;
	}

	public String getWebQuotesReqCcApproval() {
		return webQuotesReqCcApproval;
	}

	public VendorVO setWebQuotesReqCcApproval(String webQuotesReqCcApproval) {
		this.webQuotesReqCcApproval = webQuotesReqCcApproval;
		return this;
	}

	public String getWebQuotesReqFaApproval() {
		return webQuotesReqFaApproval;
	}

	public VendorVO setWebQuotesReqFaApproval(String webQuotesReqFaApproval) {
		this.webQuotesReqFaApproval = webQuotesReqFaApproval;
		return this;
	}

	public String getTaxInd() {
		return taxInd;
	}

	public VendorVO setTaxInd(String taxInd) {
		this.taxInd = taxInd;
		return this;
	}

	public String getBankName() {
		return bankName;
	}

	public VendorVO setBankName(String bankName) {
		this.bankName = bankName;
		return this;
	}

	public Long getBankAccountTypeId() {
		return bankAccountTypeId;
	}

	public VendorVO setBankAccountTypeId(Long bankAccountTypeId) {
		this.bankAccountTypeId = bankAccountTypeId;
		return this;
	}

	public String getBankAccountName() {
		return bankAccountName;
	}

	public VendorVO setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
		return this;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public VendorVO setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
		return this;
	}

	public String getBankSortCode() {
		return bankSortCode;
	}

	public VendorVO setBankSortCode(String bankSortCode) {
		this.bankSortCode = bankSortCode;
		return this;
	}

	public String getIsDeliveringDealer() {
		return isDeliveringDealer;
	}

	public VendorVO setIsDeliveringDealer(String isDeliveringDealer) {
		this.isDeliveringDealer = isDeliveringDealer;
		return this;
	}

	public String getContactJobTitle() {
		return contactJobTitle;
	}

	public VendorVO setContactJobTitle(String contactJobTitle) {
		this.contactJobTitle = contactJobTitle;
		return this;
	}

	public String getContactFirstname() {
		return contactFirstname;
	}

	public VendorVO setContactFirstname(String contactFirstname) {
		this.contactFirstname = contactFirstname;
		return this;
	}

	public String getContactLastName() {
		return contactLastName;
	}

	public VendorVO setContactLastName(String contactLastName) {
		this.contactLastName = contactLastName;
		return this;
	}

	public Long getParentCId() {
		return parentCId;
	}

	public VendorVO setParentCId(Long parentCId) {
		this.parentCId = parentCId;
		return this;
	}

	public String getParentAccountType() {
		return parentAccountType;
	}

	public VendorVO setParentAccountType(String parentAccountType) {
		this.parentAccountType = parentAccountType;
		return this;
	}

	public String getParentAccount() {
		return parentAccount;
	}

	public VendorVO setParentAccount(String parentAccount) {
		this.parentAccount = parentAccount;
		return this;
	}

	public boolean isInactive() {
		return inactive;
	}

	public VendorVO setInactive(boolean inactive) {
		this.inactive = inactive;
		return this;
	}

	public boolean isValidTaxId() {
		return validTaxId;
	}

	public VendorVO setValidTaxId(boolean validTaxId) {
		this.validTaxId = validTaxId;
		return this;
	}

	@Override
	public String toString() {
		return "VendorVO [accStatus=" + accStatus + ", accountCode=" + accountCode + ", accountName=" + accountName
				+ ", accountType=" + accountType + ", bankAccountName=" + bankAccountName + ", bankAccountNumber="
				+ bankAccountNumber + ", bankAccountTypeId=" + bankAccountTypeId + ", bankName=" + bankName
				+ ", bankSortCode=" + bankSortCode + ", cId=" + cId + ", contactFirstname=" + contactFirstname
				+ ", contactJobTitle=" + contactJobTitle + ", contactLastName=" + contactLastName + ", currencyCode="
				+ currencyCode + ", email=" + email + ", entityId=" + entityId + ", fax=" + fax + ", groupCode="
				+ groupCode + ", inactive=" + inactive + ", internationalInd=" + internationalInd
				+ ", isDeliveringDealer=" + isDeliveringDealer + ", orgizationType=" + orgizationType
				+ ", parentAccount=" + parentAccount + ", parentAccountType=" + parentAccountType + ", parentCId="
				+ parentCId + ", payeeName=" + payeeName + ", paymentInd=" + paymentInd + ", paymentMethod="
				+ paymentMethod + ", paymentTerm=" + paymentTerm + ", phone=" + phone + ", regName=" + regName
				+ ", shortName=" + shortName + ", taxIdNum=" + taxIdNum + ", taxInd=" + taxInd + ", upfitInd="
				+ upfitInd + ", validTaxId=" + validTaxId + ", webQuotesReqCcApproval=" + webQuotesReqCcApproval
				+ ", webQuotesReqFaApproval=" + webQuotesReqFaApproval + "]";
	}
	
}
