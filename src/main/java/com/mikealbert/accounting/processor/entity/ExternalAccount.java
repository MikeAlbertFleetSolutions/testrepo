package com.mikealbert.accounting.processor.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.mikealbert.util.data.DateUtil;


/**
 * The persistent class for the EXTERNAL_ACCOUNTS database table.
 * 
 */
@Entity
@Table(name="EXTERNAL_ACCOUNTS")
public class ExternalAccount extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ExternalAccountPK id;
	
	@Transient 
	private String entityId;

	@NotNull
	@Column(name="ACC_STATUS")
	private String accStatus;

	@NotNull
	@Column(name="ACCOUNT_NAME")
	private String accountName;

	@NotNull
	@Column(name="SHORT_NAME")
	private String shortName;
	
	@NotNull	
	@Column(name="CURRENCY_CODE")
	private String currencyCode;	
	
	@NotNull	
	@Column(name="WEB_QUOTES_REQ_CC_APPROVAL")
	private String webQuotesReqCcApproval;

	@NotNull	
	@Column(name="WEB_QUOTES_REQ_FA_APPROVAL")
	private String webQuotesReqFaApproval;	
	
	@NotNull	
	@Column(name="UPFIT_IND")
	private String upfitInd;
	
	@NotNull	
	@Column(name="PAYMENT_IND")
	private String paymentInd;	
	
	@NotNull	
	@Column(name="INTERNATIONAL_IND")
	private String internationalInd;

	@Column(name="TAX_REG_NO")
	private String taxRegNo;

	@Column(name="COMP_REG_NO")
	private String compRegNo;	
	
	@Column(name="REG_NAME")
	private String regName;		
	
	@Column(name="ORGANISATION_TYPE")
	private String organisationType;	

	@NotNull
	@Column(name="GROUP_CODE")
	private String groupCode;
	
	@Column(name="EMAIL")
	private String email;
		
	@Column(name="TELEPHONE_NUMBER")
	private String telephoneNumber;	
	
	@Column(name="FAX_CODE")
	private String faxCode;	
	
	@Column(name="PAYMENT_METHOD")
	private String paymentMethod;	
	
	@Column(name="CRT_C_ID")
	private Long crtCId;

	@Column(name="CRT_EXT_ACC_TYPE")
	private String crtExtAccType;
	
	@Column(name="CREDIT_TERMS_CODE")
	private String creditTermsCode;	
	
	@Column(name="PAYEE_NAME")
	private String payeeName;	
	
	@Column(name="TAX_IND")
	private String taxInd;	
	
	@Column(name="BANK_NAME")
	private String bankName;
	
	@Column(name="BANK_ACCOUNT_NAME")
	private String bankAccountName;

	@Column(name="BANK_ACCOUNT_NUMBER")
	private String bankAccountNumber;	
	
	@Column(name="BANK_SORT_CODE")
	private String bankSortCode;
	
	@Column(name="OCCUPATION")
	private String occupation;
	
	@Column(name="FIRST_NAME")
	private String firstName;
	
	@Column(name="LAST_NAME")
	private String lastName;

	@Column(name="PARENT_ACCOUNT_ENTITY")
	private Long parentAccountEntity;

	@Column(name="PARENT_ACCOUNT_TYPE")
	private String parentAccountType;
	
	@Column(name="PARENT_ACCOUNT")
	private String parentAccount;

	@Column(name="DATE_OPENED")
	private Date dateOpened;

	@Column(name="CRED_APPR_STATUS")
	private String credApprStatus;

	@Column(name="OVERDUE_INTEREST")
	private Long overdueInterest;

	@Column(name="CREDIT_MANAGEMENT_TYPE")
	private String creditManagementType;	

	@Column(name="DATE_LAST_CREDIT_CHECK")
	private Date dateLastCreditCheck;	

	@Column(name="CREDIT_LIMIT")
	private BigDecimal creditLimit;

	@Column(name="CREDIT_LIMIT_2")
	private BigDecimal creditLimit2;

	@Column(name="CREDIT_UNIT_1")
	private Long creditUnit1;

	@Column(name="CREDIT_UNIT_2")
	private Long creditUnit2;

	@Column(name="CAPITAL_LIMIT_1")
	private BigDecimal capitalLimit1;

	@Column(name="CAPITAL_LIMIT_2")
	private BigDecimal capitalLimit2;

	@Column(name="PURCHASE_CREDIT_LIMIT")
	private BigDecimal purchaseCreditLimit;

	@Column(name="CREDIT_SCORE")
	private String creditScore;

	@Column(name="INCORPORATION_STATE")
	private String incorporationState;

	@Column(name="RISK_DEPOSIT_AMT")
	private BigDecimal riskDepositAmt;
	
	@Column(name="UPFIT_FEE_CHGD")
	private String upfitFeeChgd;	

	@Column(name="BANKRUPT_IND")
	private String bankruptInd;

	@Column(name="TELEX_CODE")
	private String telexCode;

	@Column(name="PRINT_STATEMENT")
	private String printStatement;

	@OrderBy("eaaId ASC")	
    @OneToMany(mappedBy = "externalAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExtAccAddress> externalAccountAddresses;	
    
    @OneToMany(mappedBy = "externalAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExtAccBankAcc> externalAccountBankAccounts;
    
    @OneToMany(mappedBy = "externalAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Supplier> suppliers;	
    
    @Transient
    private boolean newAccount;
    
    @Transient
    private boolean validTaxId;
    
	public ExternalAccount() {}

	public ExternalAccount(ExternalAccountPK id) {
		this.id = id;
	}
	
	public ExternalAccount(ExternalAccountPK id, boolean newAccount) {
		this.id = id;
		this.newAccount = newAccount;
	}	
	
	public ExternalAccountPK getId() {
		return id;
	}

	public void setId(ExternalAccountPK id) {
		this.id = id;
	}
	
	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getAccStatus() {
		return accStatus;
	}

	public void setAccStatus(String accStatus) {
		this.accStatus = accStatus;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getWebQuotesReqCcApproval() {
		return webQuotesReqCcApproval;
	}

	public void setWebQuotesReqCcApproval(String webQuotesReqCcApproval) {
		this.webQuotesReqCcApproval = webQuotesReqCcApproval;
	}

	public String getWebQuotesReqFaApproval() {
		return webQuotesReqFaApproval;
	}

	public void setWebQuotesReqFaApproval(String webQuotesReqFaApproval) {
		this.webQuotesReqFaApproval = webQuotesReqFaApproval;
	}

	public String getUpfitInd() {
		return upfitInd;
	}

	public void setUpfitInd(String upfitInd) {
		this.upfitInd = upfitInd;
	}

	public String getPaymentInd() {
		return paymentInd;
	}

	public void setPaymentInd(String paymentInd) {
		this.paymentInd = paymentInd;
	}

	public String getInternationalInd() {
		return internationalInd;
	}

	public void setInternationalInd(String internationalInd) {
		this.internationalInd = internationalInd;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getTaxRegNo() {
		return taxRegNo;
	}

	public void setTaxRegNo(String taxRegNo) {
		this.taxRegNo = taxRegNo;
	}

	public String getCompRegNo() {
		return compRegNo;
	}

	public void setCompRegNo(String compRegNo) {
		this.compRegNo = compRegNo;
	}	
	
	public String getRegName() {
		return regName;
	}

	public void setRegName(String regName) {
		this.regName = regName;
	}

	public String getOrganisationType() {
		return organisationType;
	}

	public void setOrganisationType(String organisationType) {
		this.organisationType = organisationType;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
		
	public String getFaxCode() {
		return faxCode;
	}

	public void setFaxCode(String faxCode) {
		this.faxCode = faxCode;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	
	public String getPaymentMethod() {
		return paymentMethod;
	}
	
	public Long getCrtCId() {
		return crtCId;
	}

	public void setCrtCId(Long crtCId) {
		this.crtCId = crtCId;
	}

	public String getCrtExtAccType() {
		return crtExtAccType;
	}

	public void setCrtExtAccType(String crtExtAccType) {
		this.crtExtAccType = crtExtAccType;
	}

	public String getCreditTermsCode() {
		return creditTermsCode;
	}

	public void setCreditTermsCode(String creditTermsCode) {
		this.creditTermsCode = creditTermsCode;
	}
	
	public String getPayeeName() {
		return payeeName;
	}
	
	public String getTaxInd() {
		return taxInd;
	}

	public void setTaxInd(String taxInd) {
		this.taxInd = taxInd;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankAccountName() {
		return bankAccountName;
	}

	public void setBankAccountName(String bankAccountName) {
		this.bankAccountName = bankAccountName;
	}

	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getBankSortCode() {
		return bankSortCode;
	}

	public void setBankSortCode(String bankSortCode) {
		this.bankSortCode = bankSortCode;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getParentEntity() {
		return parentAccountEntity;
	}

	public void setParentEntity(Long parentEntity) {
		this.parentAccountEntity = parentEntity;
	}

	public String getParentAccountType() {
		return parentAccountType;
	}

	public void setParentAccountType(String parentAccountType) {
		this.parentAccountType = parentAccountType;
	}

	public String getParentAccount() {
		return parentAccount;
	}

	public void setParentAccount(String parentAccount) {
		this.parentAccount = parentAccount;
	}

	public List<ExtAccAddress> getExternalAccountAddresses() {
		return externalAccountAddresses;
	}

	public void setExternalAccountAddresses(List<ExtAccAddress> externalAccountAddresses) {
		this.externalAccountAddresses = externalAccountAddresses;
	}

	public List<ExtAccBankAcc> getExternalAccountBankAccounts() {
		return externalAccountBankAccounts;
	}

	public void setExternalAccountBankAccounts(List<ExtAccBankAcc> externalAccountBankAccounts) {
		this.externalAccountBankAccounts = externalAccountBankAccounts;
	}

	public List<Supplier> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
	}
	
	public boolean isNewAccount() {
		return newAccount;
	}

	public void setNewAccount(boolean newAccount) {
		this.newAccount = newAccount;
	}

	public boolean isValidTaxId() {
		return validTaxId;
	}

	public void setValidTaxId(boolean validTaxId) {
		this.validTaxId = validTaxId;
	}

	public Date getDateOpened() {
		return DateUtil.clone(dateOpened);
	}

	public void setDateOpened(Date dateOpened) {
		this.dateOpened = DateUtil.clone(dateOpened);
	}

	public String getCredApprStatus() {
		return credApprStatus;
	}

	public void setCredApprStatus(String credApprStatus) {
		this.credApprStatus = credApprStatus;
	}	

	public Long getOverdueInterest() {
		return overdueInterest;
	}

	public void setOverdueInterest(Long overdueInterest) {
		this.overdueInterest = overdueInterest;
	}

	public String getCreditManagementType() {
		return creditManagementType;
	}

	public void setCreditManagementType(String creditManagementType) {
		this.creditManagementType = creditManagementType;
	}	
	
	public Date getDateLastCreditCheck() {
		return DateUtil.clone(dateLastCreditCheck);
	}

	public void setDateLastCreditCheck(Date dateLastCreditCheck) {
		this.dateLastCreditCheck = DateUtil.clone(dateLastCreditCheck);
	}

	public BigDecimal getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}

	public BigDecimal getCreditLimit2() {
		return creditLimit2;
	}

	public void setCreditLimit2(BigDecimal creditLimit2) {
		this.creditLimit2 = creditLimit2;
	}

	public Long getCreditUnit1() {
		return creditUnit1;
	}

	public void setCreditUnit1(Long creditUnit1) {
		this.creditUnit1 = creditUnit1;
	}

	public Long getCreditUnit2() {
		return creditUnit2;
	}

	public void setCreditUnit2(Long creditUnit2) {
		this.creditUnit2 = creditUnit2;
	}

	public BigDecimal getCapitalLimit1() {
		return capitalLimit1;
	}

	public void setCapitalLimit1(BigDecimal capitalLimit1) {
		this.capitalLimit1 = capitalLimit1;
	}

	public BigDecimal getCapitalLimit2() {
		return capitalLimit2;
	}

	public void setCapitalLimit2(BigDecimal capitalLimit2) {
		this.capitalLimit2 = capitalLimit2;
	}

	public BigDecimal getPurchaseCreditLimit() {
		return purchaseCreditLimit;
	}

	public void setPurchaseCreditLimit(BigDecimal purchaseCreditLimit) {
		this.purchaseCreditLimit = purchaseCreditLimit;
	}

	public String getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(String creditScore) {
		this.creditScore = creditScore;
	}
	
	public String getIncorporationState() {
		return incorporationState;
	}

	public void setIncorporationState(String incorporationState) {
		this.incorporationState = incorporationState;
	}

	public BigDecimal getRiskDepositAmt() {
		return riskDepositAmt;
	}

	public void setRiskDepositAmt(BigDecimal riskDepositAmt) {
		this.riskDepositAmt = riskDepositAmt;
	}

	public String getUpfitFeeChgd() {
		return upfitFeeChgd;
	}

	public void setUpfitFeeChgd(String upfitFeeChgd) {
		this.upfitFeeChgd = upfitFeeChgd;
	}

	public String getBankruptInd() {
		return bankruptInd;
	}

	public void setBankruptInd(String bankruptInd) {
		this.bankruptInd = bankruptInd;
	}

	public String getTelexCode() {
		return telexCode;
	}

	public void setTelexCode(String telexCode) {
		this.telexCode = telexCode;
	}	

	public String getPrintStatement() {
		return printStatement;
	}

	public void setPrintStatement(String printStatement) {
		this.printStatement = printStatement;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(accStatus, accountName, bankAccountName, bankAccountNumber, bankName, bankSortCode,
				creditTermsCode, crtCId, crtExtAccType, currencyCode, email, entityId, externalAccountAddresses,
				externalAccountBankAccounts, faxCode, firstName, groupCode, id, internationalInd, lastName, occupation,
				organisationType, parentAccount, parentAccountEntity, parentAccountType, payeeName, paymentInd,
				paymentMethod, regName, shortName, suppliers, taxInd, taxRegNo, telephoneNumber, upfitInd,
				webQuotesReqCcApproval, webQuotesReqFaApproval);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExternalAccount other = (ExternalAccount) obj;
		return Objects.equals(accStatus, other.accStatus) && Objects.equals(accountName, other.accountName)
				&& Objects.equals(bankAccountName, other.bankAccountName)
				&& Objects.equals(bankAccountNumber, other.bankAccountNumber)
				&& Objects.equals(bankName, other.bankName) && Objects.equals(bankSortCode, other.bankSortCode)
				&& Objects.equals(creditTermsCode, other.creditTermsCode) && Objects.equals(crtCId, other.crtCId)
				&& Objects.equals(crtExtAccType, other.crtExtAccType)
				&& Objects.equals(currencyCode, other.currencyCode) && Objects.equals(email, other.email)
				&& Objects.equals(entityId, other.entityId)
				&& Objects.equals(externalAccountAddresses, other.externalAccountAddresses)
				&& Objects.equals(externalAccountBankAccounts, other.externalAccountBankAccounts)
				&& Objects.equals(faxCode, other.faxCode) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(groupCode, other.groupCode) && Objects.equals(id, other.id)
				&& Objects.equals(internationalInd, other.internationalInd) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(occupation, other.occupation)
				&& Objects.equals(organisationType, other.organisationType)
				&& Objects.equals(parentAccount, other.parentAccount)
				&& Objects.equals(parentAccountEntity, other.parentAccountEntity)
				&& Objects.equals(parentAccountType, other.parentAccountType)
				&& Objects.equals(payeeName, other.payeeName) && Objects.equals(paymentInd, other.paymentInd)
				&& Objects.equals(paymentMethod, other.paymentMethod) && Objects.equals(regName, other.regName)
				&& Objects.equals(shortName, other.shortName) && Objects.equals(suppliers, other.suppliers)
				&& Objects.equals(taxInd, other.taxInd) && Objects.equals(taxRegNo, other.taxRegNo)
				&& Objects.equals(telephoneNumber, other.telephoneNumber) && Objects.equals(upfitInd, other.upfitInd)
				&& Objects.equals(webQuotesReqCcApproval, other.webQuotesReqCcApproval)
				&& Objects.equals(webQuotesReqFaApproval, other.webQuotesReqFaApproval);
	}

	@Override
	public String toString() {
		return "ExternalAccount [id=" + id + ", entityId=" + entityId + ", accStatus=" + accStatus + ", accountName="
				+ accountName + ", shortName=" + shortName + ", currencyCode=" + currencyCode
				+ ", webQuotesReqCcApproval=" + webQuotesReqCcApproval + ", webQuotesReqFaApproval="
				+ webQuotesReqFaApproval + ", upfitInd=" + upfitInd + ", paymentInd=" + paymentInd
				+ ", internationalInd=" + internationalInd + ", taxRegNo=" + taxRegNo + ", regName=" + regName
				+ ", organisationType=" + organisationType + ", groupCode=" + groupCode + ", email=" + email
				+ ", telephoneNumber=" + telephoneNumber + ", faxCode=" + faxCode + ", paymentMethod=" + paymentMethod
				+ ", crtCId=" + crtCId + ", crtExtAccType=" + crtExtAccType + ", creditTermsCode=" + creditTermsCode
				+ ", payeeName=" + payeeName + ", taxInd=" + taxInd + ", bankName=" + bankName + ", bankAccountName="
				+ bankAccountName + ", bankAccountNumber=" + bankAccountNumber + ", bankSortCode=" + bankSortCode
				+ ", occupation=" + occupation + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", parentAccountEntity=" + parentAccountEntity + ", parentAccountType=" + parentAccountType
				+ ", parentAccount=" + parentAccount + ", externalAccountAddresses=" + externalAccountAddresses
				+ ", externalAccountBankAccounts=" + externalAccountBankAccounts.toString() + "]";
	}
	
}