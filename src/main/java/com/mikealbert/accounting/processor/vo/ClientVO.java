package com.mikealbert.accounting.processor.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.accounting.processor.validation.ClientValidator;
import com.mikealbert.util.data.DateUtil;

@ClientValidator
public class ClientVO implements Serializable{
    private static final long serialVersionUID = -4693182495215801578L;

    private String internalId;
    
    private String externalId;

    private String entityId;

    private boolean person;
    
    private Long subsidiary; 

    private Date agingAsOfDate;

    private BigDecimal agingCurrent;

    private List<?> agingCurrentDetail; 

    private BigDecimal aging30;    

    private List<?> aging30Detail; 

    private BigDecimal aging60;

    private List<?> aging60Detail;     

    private BigDecimal aging90;

    private List<?> aging90Detail;         

    private BigDecimal aging91Plus;

    private List<?> aging91PlusDetail;         

    private ClientPaymentVO lastPayment;

    private Date lastPaymentDate;

    private BigDecimal lastPaymentAmount;

    private BigDecimal balance;

    private BigDecimal unappliedBalance;

    private BigDecimal depositBalance;

    private BigDecimal purchaseBalance;

    private String collectionStatus;

    private String collector;

    private NoteVO collectionNote;

    private Date asOfDate;

    @Size(max = 25, message = "accountCode length exceeded")    
    @NotNull(message = "accountCode is required")
    private String accountCode;

    @NotNull(message = "accountName is required")
    private String accountName;

    private String printOnCheckAs;

    @NotNull(message = "shortName is required")
    private String shortName;

    @Size(max = 25, message = "taxId length exceeded")
    private String taxId;

    private String email;

    @Size(max = 25, message = "phoneNumber length exceeded")
    private String phoneNumber;

    @Size(max = 25, message = "faxNumber length exceeded")
    private String faxNumber;    

    private String category;

    @NotNull(message = "currency is required")
    private String currency;

    private String parentInternalId;

    private String parentExternalId;    

    private String parentAccountCode;

    private String status;

    private boolean inactive;

    private String terms;

    private String creditStatus;

    private boolean financeCharge;

    private String creditManagementType;

    private Date lastCreditCheck;

    @Size(max = 25, message = "creditScore length exceeded")
    private String creditScore;
    
    @DecimalMin(value = "0", message = "creditLimit1 must be greater than 0")
    @DecimalMax(value = "999999999999.99", message = "creditLimit1 length exceeded")    
    private BigDecimal creditLimit1;

    @DecimalMin(value = "0", message = "creditLimit2 must be greater than 0")
    @DecimalMax(value = "999999999999.99", message = "creditLimit2 length exceeded")
    private BigDecimal creditLimit2;

    @Min(value = 0, message = "unitLimit1 must be greater than 0")
    @Max(value = 99999, message = "unitLimit1 length exceeded")
    private Long unitLimit1;

    @Min(value = 0, message = "unitLimit2 must be greater than 0")
    @Max(value = 99999, message = "unitLimit2 length exceeded") 
    private Long unitLimit2;

    @DecimalMin(value = "0", message = "capitalLimit1 must be greater than 0")
    @DecimalMax(value = "999999999999.99", message = "capitalLimit1 length exceeded")
    private BigDecimal capitalLimit1;

    @DecimalMin(value = "0", message = "capitalLimit2 must be greater than 0")
    @DecimalMax(value = "999999999999.99", message = "capitalLimit2 length exceeded")
    private BigDecimal capitalLimit2;

    @DecimalMin(value = "0", message = "purchaseCreditLimit must be greater than 0")
    @DecimalMax(value = "999999999999.99", message = "purchaseCreditLimit length exceeded")
    private BigDecimal purchaseCreditLimit;

    private String incorporationState;

    @DecimalMin(value = "0", message = "riskDepositAmount must be greater than 0")        
    @DecimalMax(value = "999999999999.99", message = "riskDepositAmount length exceeded")    
    private BigDecimal riskDepositAmount;

    private boolean suppressStatement;

    private boolean interimFinanceCharge;

    private boolean bankrupt;

    private boolean defaultBilling;

    private boolean defaultShipping;    

    private String addressInternalId;

    private String addressExternalId;

    @Size(max = 80, message = "address1 length exceeded")
    private String address1;

    @Size(max = 80, message = "address2 length exceeded")    
    private String address2;

    private String country;

    private String region;

    private String county;
    
    private String city;

    private String postalCode;

    private String bankAccountNumber;

    private String bankName;

    private String bankNumber;

    private String bankType;

    
    public ClientVO() {}

    public String getInternalId() {
        return internalId;
    }

    public ClientVO setInternalId(String internalId) {
        this.internalId = internalId;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public ClientVO setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public String getEntityId() {
        return entityId;
    }

    public ClientVO setEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public boolean isPerson() {
        return person;
    }

    public ClientVO setPerson(boolean person) {
        this.person = person;
        return this;
    }
    
    public Long getSubsidiary() {
        return subsidiary;
    }

    public ClientVO setSubsidiary(Long subsidiary) {
        this.subsidiary = subsidiary;
        return this;
    }
    
    public String getAccountCode() {
        return accountCode;
    }

    public ClientVO setAccountCode(String accountCode) {
        this.accountCode = accountCode;
        return this;
    }

    public String getAccountName() {
        return accountName;
    }

    public ClientVO setAccountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public String getPrintOnCheckAs() {
        return printOnCheckAs;
    }

    public ClientVO setPrintOnCheckAs(String printOnCheckAs) {
        this.printOnCheckAs = printOnCheckAs;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ClientVO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ClientVO setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public ClientVO setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public ClientVO setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public ClientVO setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String getParentAccountCode() {
        return parentAccountCode;
    }

    public ClientVO setParentAccountCode(String parentAccountCode) {
        this.parentAccountCode = parentAccountCode;
        return this;
    }

    public String getParentInternalId() {
        return parentInternalId;
    }

    public ClientVO setParentInternalId(String parentInternalId) {
        this.parentInternalId = parentInternalId;
        return this;
    }

    public String getParentExternalId() {
        return parentExternalId;
    }

    public ClientVO setParentExternalId(String parentExternalId) {
        this.parentExternalId = parentExternalId;
        return this;
    }    
    
    public String getStatus() {
        return status;
    }

    public ClientVO setStatus(String status) {
        this.status = status;
        return this;
    }

    public boolean isInactive() {
        return inactive;
    }

    public ClientVO setInactive(boolean inactive) {
        this.inactive = inactive;
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public ClientVO setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getTaxId() {
        return taxId;
    }

    public ClientVO setTaxId(String taxId) {
        this.taxId = taxId;
        return this;
    }

    public String getTerms() {
        return terms;
    }

    public ClientVO setTerms(String terms) {
        this.terms = terms;
        return this;
    }

    public String getCreditStatus() {
        return creditStatus;
    }

    public ClientVO setCreditStatus(String creditStatus) {
        this.creditStatus = creditStatus;
        return this;
    }

    public boolean isFinanceCharge() {
        return financeCharge;
    }

    public ClientVO setFinanceCharge(boolean financeCharge) {
        this.financeCharge = financeCharge;
        return this;
    }

    public String getCreditManagementType() {
        return creditManagementType;
    }

    public ClientVO setCreditManagementType(String creditManagementType) {
        this.creditManagementType = creditManagementType;
        return this;
    }

    public Date getLastCreditCheck() {
        return DateUtil.clone(lastCreditCheck);
    }

    public ClientVO setLastCreditCheck(Date lastCreditCheck) {
        this.lastCreditCheck = DateUtil.clone(lastCreditCheck);
        return this;
    }

    public BigDecimal getCreditLimit1() {
        return creditLimit1;
    }

    public ClientVO setCreditLimit1(BigDecimal creditLimit1) {
        this.creditLimit1 = creditLimit1;
        return this;
    }

    public BigDecimal getCreditLimit2() {
        return creditLimit2;
    }

    public ClientVO setCreditLimit2(BigDecimal creditLimit2) {
        this.creditLimit2 = creditLimit2;
        return this;
    }

    public Long getUnitLimit1() {
        return unitLimit1;
    }

    public ClientVO setUnitLimit1(Long unitLimit1) {
        this.unitLimit1 = unitLimit1;
        return this;
    }

    public Long getUnitLimit2() {
        return unitLimit2;
    }

    public ClientVO setUnitLimit2(Long unitLimit2) {
        this.unitLimit2 = unitLimit2;
        return this;
    }

    public String getCreditScore() {
        return creditScore;
    }

    public ClientVO setCreditScore(String creditScore) {
        this.creditScore = creditScore;
        return this;
    }

    public BigDecimal getCapitalLimit1() {
        return capitalLimit1;
    }

    public ClientVO setCapitalLimit1(BigDecimal capitalLimit1) {
        this.capitalLimit1 = capitalLimit1;
        return this;
    }

    public BigDecimal getCapitalLimit2() {
        return capitalLimit2;
    }

    public ClientVO setCapitalLimit2(BigDecimal capitalLimit2) {
        this.capitalLimit2 = capitalLimit2;
        return this;
    }

    public BigDecimal getPurchaseCreditLimit() {
        return purchaseCreditLimit;
    }

    public ClientVO setPurchaseCreditLimit(BigDecimal purchaseCreditLimit) {
        this.purchaseCreditLimit = purchaseCreditLimit;
        return this;
    }

    public String getIncorporationState() {
        return incorporationState;
    }

    public ClientVO setIncorporationState(String incorporationState) {
        this.incorporationState = incorporationState;
        return this;
    }

    public BigDecimal getRiskDepositAmount() {
        return riskDepositAmount;
    }

    public ClientVO setRiskDepositAmount(BigDecimal riskDepositAmount) {
        this.riskDepositAmount = riskDepositAmount;
        return this;
    }

    public boolean isSuppressStatement() {
        return suppressStatement;
    }

    public ClientVO setSuppressStatement(boolean suppressStatement) {
        this.suppressStatement = suppressStatement;
        return this;
    }

    public boolean isInterimFinanceCharge() {
        return interimFinanceCharge;
    }

    public ClientVO setInterimFinanceCharge(boolean interimFinanceCharge) {
        this.interimFinanceCharge = interimFinanceCharge;
        return this;
    }

    public boolean isBankrupt() {
        return bankrupt;
    }

    public ClientVO setBankrupt(boolean bankrupt) {
        this.bankrupt = bankrupt;
        return this;
    }

    public boolean isDefaultBilling() {
        return defaultBilling;
    }

    public ClientVO setDefaultBilling(boolean defaultBilling) {
        this.defaultBilling = defaultBilling;
        return this;
    }

    public boolean isDefaultShipping() {
        return defaultShipping;
    }

    public ClientVO setDefaultShipping(boolean defaultShipping) {
        this.defaultShipping = defaultShipping;
        return this;
    }      

    public String getAddressInternalId() {
        return addressInternalId;
    }

    public ClientVO setAddressInternalId(String addressInternalId) {
        this.addressInternalId = addressInternalId;
        return this;
    }

    public String getAddressExternalId() {
        return addressExternalId;
    }

    public ClientVO setAddressExternalId(String addressExternalId) {
        this.addressExternalId = addressExternalId;
        return this;
    }

    public String getAddress1() {
        return address1;
    }

    public ClientVO setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public String getAddress2() {
        return address2;
    }

    public ClientVO setAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public ClientVO setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public ClientVO setRegion(String region) {
        this.region = region;
        return this;
    }

    public String getCounty() {
        return county;
    }

    public ClientVO setCounty(String county) {
        this.county = county;
        return this;
    }

    public String getCity() {
        return city;
    }

    public ClientVO setCity(String city) {
        this.city = city;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public ClientVO setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public ClientVO setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
        return this;
    }

    public String getBankName() {
        return bankName;
    }

    public ClientVO setBankName(String bankName) {
        this.bankName = bankName;
        return this;
    }

    public String getBankNumber() {
        return bankNumber;
    }

    public ClientVO setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
        return this;
    }

    public String getBankType() {
        return bankType;
    }

    public ClientVO setBankType(String bankType) {
        this.bankType = bankType;
        return this;
    }
    
    public Date getAgingAsOfDate() {
        return DateUtil.clone(agingAsOfDate);
    }

    public ClientVO setAgingAsOfDate(Date agingAsOfDate) {
        this.agingAsOfDate = DateUtil.clone(agingAsOfDate);
        return this;
    }

    public BigDecimal getAgingCurrent() {
        return agingCurrent;
    }

    public ClientVO setAgingCurrent(BigDecimal agingCurrent) {
        this.agingCurrent = agingCurrent;
        return this;
    }

    public BigDecimal getAging30() {
        return aging30;
    }

    public ClientVO setAging30(BigDecimal aging30) {
        this.aging30 = aging30;
        return this;        
    }

    public BigDecimal getAging60() {
        return aging60;
    }

    public ClientVO setAging60(BigDecimal aging60) {
        this.aging60 = aging60;
        return this;        
    }

    public BigDecimal getAging90() {
        return aging90;
    }

    public ClientVO setAging90(BigDecimal aging90) {
        this.aging90 = aging90;
        return this;        
    }

    public BigDecimal getAging91Plus() {
        return aging91Plus;
    }

    public ClientVO setAging91Plus(BigDecimal aging91Plus) {
        this.aging91Plus = aging91Plus;
        return this;        
    }

    public List<?> getAgingCurrentDetail() {
        return agingCurrentDetail;
    }

    public ClientVO setAgingCurrentDetail(List<?> agingCurrentDetail) {
        this.agingCurrentDetail = agingCurrentDetail;
        return this;
    }

    public List<?> getAging30Detail() {
        return aging30Detail;
    }

    public ClientVO setAging30Detail(List<?> aging30Detail) {
        this.aging30Detail = aging30Detail;
        return this;
    }
    
    public List<?> getAging60Detail() {
        return aging60Detail;
    }

    public ClientVO setAging60Detail(List<?> aging60Detail) {
        this.aging60Detail = aging60Detail;
        return this;
    }

    public List<?> getAging90Detail() {
        return aging90Detail;
    }

    public ClientVO setAging90Detail(List<?> aging90Detail) {
        this.aging90Detail = aging90Detail;
        return this;
    }

    public List<?> getAging91PlusDetail() {
        return aging91PlusDetail;
    }

    public ClientVO setAging91PlusDetail(List<?> aging91PlusDetail) {
        this.aging91PlusDetail = aging91PlusDetail;
        return this;
    }

    public ClientPaymentVO getLastPayment() {
        return lastPayment;
    }

    public ClientVO setLastPayment(ClientPaymentVO lastPayment) {
        this.lastPayment = lastPayment;
        return this;
    }

    public Date getLastPaymentDate() {
        return DateUtil.clone(lastPaymentDate);
    }

    public ClientVO setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = DateUtil.clone(lastPaymentDate);
        return this;
    }

    public BigDecimal getLastPaymentAmount() {
        return lastPaymentAmount;
    }

    public ClientVO setLastPaymentAmount(BigDecimal lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
        return this;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    
    public ClientVO setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public BigDecimal getUnappliedBalance() {
        return unappliedBalance;
    }

    public ClientVO setUnappliedBalance(BigDecimal unappliedBalance) {
        this.unappliedBalance = unappliedBalance;
        return this;
    }

    public BigDecimal getDepositBalance() {
        return depositBalance;
    }

    public ClientVO setDepositBalance(BigDecimal depositBalance) {
        this.depositBalance = depositBalance;
        return this;
    }

    public BigDecimal getPurchaseBalance() {
        return purchaseBalance;
    }

    public ClientVO setPurchaseBalance(BigDecimal purchaseBalance) {
        this.purchaseBalance = purchaseBalance;
        return this;
    }
  
    public String getCollectionStatus() {
        return collectionStatus;
    }

    public ClientVO setCollectionStatus(String collectionStatus) {
        this.collectionStatus = collectionStatus;
        return this;
    }

    public String getCollector() {
        return collector;
    }

    public ClientVO setCollector(String collector) {
        this.collector = collector;
        return this;
    }
   
    public NoteVO getCollectionNote() {
        return collectionNote;
    }

    public ClientVO setCollectionNote(NoteVO collectionNote) {
        this.collectionNote = collectionNote;
        return this;
    }

    public Date getAsOfDate() {
        return DateUtil.clone(asOfDate);
    }

    public ClientVO setAsOfDate(Date asOfDate) {
        this.asOfDate = DateUtil.clone(asOfDate);
        return this;
    }    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountCode == null) ? 0 : accountCode.hashCode());
        result = prime * result + ((accountName == null) ? 0 : accountName.hashCode());
        result = prime * result + ((address1 == null) ? 0 : address1.hashCode());
        result = prime * result + ((address2 == null) ? 0 : address2.hashCode());
        result = prime * result + ((addressExternalId == null) ? 0 : addressExternalId.hashCode());
        result = prime * result + ((addressInternalId == null) ? 0 : addressInternalId.hashCode());
        result = prime * result + ((aging30 == null) ? 0 : aging30.hashCode());
        result = prime * result + ((aging30Detail == null) ? 0 : aging30Detail.hashCode());
        result = prime * result + ((aging60 == null) ? 0 : aging60.hashCode());
        result = prime * result + ((aging60Detail == null) ? 0 : aging60Detail.hashCode());
        result = prime * result + ((aging90 == null) ? 0 : aging90.hashCode());
        result = prime * result + ((aging90Detail == null) ? 0 : aging90Detail.hashCode());
        result = prime * result + ((aging91Plus == null) ? 0 : aging91Plus.hashCode());
        result = prime * result + ((aging91PlusDetail == null) ? 0 : aging91PlusDetail.hashCode());
        result = prime * result + ((agingAsOfDate == null) ? 0 : agingAsOfDate.hashCode());
        result = prime * result + ((agingCurrent == null) ? 0 : agingCurrent.hashCode());
        result = prime * result + ((agingCurrentDetail == null) ? 0 : agingCurrentDetail.hashCode());
        result = prime * result + ((balance == null) ? 0 : balance.hashCode());
        result = prime * result + ((bankAccountNumber == null) ? 0 : bankAccountNumber.hashCode());
        result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
        result = prime * result + ((bankNumber == null) ? 0 : bankNumber.hashCode());
        result = prime * result + ((bankType == null) ? 0 : bankType.hashCode());
        result = prime * result + (bankrupt ? 1231 : 1237);
        result = prime * result + ((capitalLimit1 == null) ? 0 : capitalLimit1.hashCode());
        result = prime * result + ((capitalLimit2 == null) ? 0 : capitalLimit2.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((collectionNote == null) ? 0 : collectionNote.hashCode());
        result = prime * result + ((collectionStatus == null) ? 0 : collectionStatus.hashCode());
        result = prime * result + ((collector == null) ? 0 : collector.hashCode());
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((county == null) ? 0 : county.hashCode());
        result = prime * result + ((creditLimit1 == null) ? 0 : creditLimit1.hashCode());
        result = prime * result + ((creditLimit2 == null) ? 0 : creditLimit2.hashCode());
        result = prime * result + ((creditManagementType == null) ? 0 : creditManagementType.hashCode());
        result = prime * result + ((creditScore == null) ? 0 : creditScore.hashCode());
        result = prime * result + ((creditStatus == null) ? 0 : creditStatus.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + (defaultBilling ? 1231 : 1237);
        result = prime * result + (defaultShipping ? 1231 : 1237);
        result = prime * result + ((depositBalance == null) ? 0 : depositBalance.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
        result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
        result = prime * result + ((faxNumber == null) ? 0 : faxNumber.hashCode());
        result = prime * result + (financeCharge ? 1231 : 1237);
        result = prime * result + (inactive ? 1231 : 1237);
        result = prime * result + ((incorporationState == null) ? 0 : incorporationState.hashCode());
        result = prime * result + (interimFinanceCharge ? 1231 : 1237);
        result = prime * result + ((internalId == null) ? 0 : internalId.hashCode());
        result = prime * result + ((lastCreditCheck == null) ? 0 : lastCreditCheck.hashCode());
        result = prime * result + ((lastPayment == null) ? 0 : lastPayment.hashCode());
        result = prime * result + ((lastPaymentAmount == null) ? 0 : lastPaymentAmount.hashCode());
        result = prime * result + ((lastPaymentDate == null) ? 0 : lastPaymentDate.hashCode());
        result = prime * result + ((parentAccountCode == null) ? 0 : parentAccountCode.hashCode());
        result = prime * result + ((parentExternalId == null) ? 0 : parentExternalId.hashCode());
        result = prime * result + ((parentInternalId == null) ? 0 : parentInternalId.hashCode());
        result = prime * result + (person ? 1231 : 1237);
        result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
        result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
        result = prime * result + ((printOnCheckAs == null) ? 0 : printOnCheckAs.hashCode());
        result = prime * result + ((purchaseBalance == null) ? 0 : purchaseBalance.hashCode());
        result = prime * result + ((purchaseCreditLimit == null) ? 0 : purchaseCreditLimit.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime * result + ((riskDepositAmount == null) ? 0 : riskDepositAmount.hashCode());
        result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((subsidiary == null) ? 0 : subsidiary.hashCode());
        result = prime * result + (suppressStatement ? 1231 : 1237);
        result = prime * result + ((taxId == null) ? 0 : taxId.hashCode());
        result = prime * result + ((terms == null) ? 0 : terms.hashCode());
        result = prime * result + ((unappliedBalance == null) ? 0 : unappliedBalance.hashCode());
        result = prime * result + ((unitLimit1 == null) ? 0 : unitLimit1.hashCode());
        result = prime * result + ((unitLimit2 == null) ? 0 : unitLimit2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientVO other = (ClientVO) obj;
        if (accountCode == null) {
            if (other.accountCode != null)
                return false;
        } else if (!accountCode.equals(other.accountCode))
            return false;
        if (accountName == null) {
            if (other.accountName != null)
                return false;
        } else if (!accountName.equals(other.accountName))
            return false;
        if (address1 == null) {
            if (other.address1 != null)
                return false;
        } else if (!address1.equals(other.address1))
            return false;
        if (address2 == null) {
            if (other.address2 != null)
                return false;
        } else if (!address2.equals(other.address2))
            return false;
        if (addressExternalId == null) {
            if (other.addressExternalId != null)
                return false;
        } else if (!addressExternalId.equals(other.addressExternalId))
            return false;
        if (addressInternalId == null) {
            if (other.addressInternalId != null)
                return false;
        } else if (!addressInternalId.equals(other.addressInternalId))
            return false;
        if (aging30 == null) {
            if (other.aging30 != null)
                return false;
        } else if (!aging30.equals(other.aging30))
            return false;
        if (aging30Detail == null) {
            if (other.aging30Detail != null)
                return false;
        } else if (!aging30Detail.equals(other.aging30Detail))
            return false;
        if (aging60 == null) {
            if (other.aging60 != null)
                return false;
        } else if (!aging60.equals(other.aging60))
            return false;
        if (aging60Detail == null) {
            if (other.aging60Detail != null)
                return false;
        } else if (!aging60Detail.equals(other.aging60Detail))
            return false;
        if (aging90 == null) {
            if (other.aging90 != null)
                return false;
        } else if (!aging90.equals(other.aging90))
            return false;
        if (aging90Detail == null) {
            if (other.aging90Detail != null)
                return false;
        } else if (!aging90Detail.equals(other.aging90Detail))
            return false;
        if (aging91Plus == null) {
            if (other.aging91Plus != null)
                return false;
        } else if (!aging91Plus.equals(other.aging91Plus))
            return false;
        if (aging91PlusDetail == null) {
            if (other.aging91PlusDetail != null)
                return false;
        } else if (!aging91PlusDetail.equals(other.aging91PlusDetail))
            return false;
        if (agingAsOfDate == null) {
            if (other.agingAsOfDate != null)
                return false;
        } else if (!agingAsOfDate.equals(other.agingAsOfDate))
            return false;
        if (agingCurrent == null) {
            if (other.agingCurrent != null)
                return false;
        } else if (!agingCurrent.equals(other.agingCurrent))
            return false;
        if (agingCurrentDetail == null) {
            if (other.agingCurrentDetail != null)
                return false;
        } else if (!agingCurrentDetail.equals(other.agingCurrentDetail))
            return false;
        if (balance == null) {
            if (other.balance != null)
                return false;
        } else if (!balance.equals(other.balance))
            return false;
        if (bankAccountNumber == null) {
            if (other.bankAccountNumber != null)
                return false;
        } else if (!bankAccountNumber.equals(other.bankAccountNumber))
            return false;
        if (bankName == null) {
            if (other.bankName != null)
                return false;
        } else if (!bankName.equals(other.bankName))
            return false;
        if (bankNumber == null) {
            if (other.bankNumber != null)
                return false;
        } else if (!bankNumber.equals(other.bankNumber))
            return false;
        if (bankType == null) {
            if (other.bankType != null)
                return false;
        } else if (!bankType.equals(other.bankType))
            return false;
        if (bankrupt != other.bankrupt)
            return false;
        if (capitalLimit1 == null) {
            if (other.capitalLimit1 != null)
                return false;
        } else if (!capitalLimit1.equals(other.capitalLimit1))
            return false;
        if (capitalLimit2 == null) {
            if (other.capitalLimit2 != null)
                return false;
        } else if (!capitalLimit2.equals(other.capitalLimit2))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (collectionNote == null) {
            if (other.collectionNote != null)
                return false;
        } else if (!collectionNote.equals(other.collectionNote))
            return false;
        if (collectionStatus == null) {
            if (other.collectionStatus != null)
                return false;
        } else if (!collectionStatus.equals(other.collectionStatus))
            return false;
        if (collector == null) {
            if (other.collector != null)
                return false;
        } else if (!collector.equals(other.collector))
            return false;
        if (country == null) {
            if (other.country != null)
                return false;
        } else if (!country.equals(other.country))
            return false;
        if (county == null) {
            if (other.county != null)
                return false;
        } else if (!county.equals(other.county))
            return false;
        if (creditLimit1 == null) {
            if (other.creditLimit1 != null)
                return false;
        } else if (!creditLimit1.equals(other.creditLimit1))
            return false;
        if (creditLimit2 == null) {
            if (other.creditLimit2 != null)
                return false;
        } else if (!creditLimit2.equals(other.creditLimit2))
            return false;
        if (creditManagementType == null) {
            if (other.creditManagementType != null)
                return false;
        } else if (!creditManagementType.equals(other.creditManagementType))
            return false;
        if (creditScore == null) {
            if (other.creditScore != null)
                return false;
        } else if (!creditScore.equals(other.creditScore))
            return false;
        if (creditStatus == null) {
            if (other.creditStatus != null)
                return false;
        } else if (!creditStatus.equals(other.creditStatus))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (defaultBilling != other.defaultBilling)
            return false;
        if (defaultShipping != other.defaultShipping)
            return false;
        if (depositBalance == null) {
            if (other.depositBalance != null)
                return false;
        } else if (!depositBalance.equals(other.depositBalance))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (entityId == null) {
            if (other.entityId != null)
                return false;
        } else if (!entityId.equals(other.entityId))
            return false;
        if (externalId == null) {
            if (other.externalId != null)
                return false;
        } else if (!externalId.equals(other.externalId))
            return false;
        if (faxNumber == null) {
            if (other.faxNumber != null)
                return false;
        } else if (!faxNumber.equals(other.faxNumber))
            return false;
        if (financeCharge != other.financeCharge)
            return false;
        if (inactive != other.inactive)
            return false;
        if (incorporationState == null) {
            if (other.incorporationState != null)
                return false;
        } else if (!incorporationState.equals(other.incorporationState))
            return false;
        if (interimFinanceCharge != other.interimFinanceCharge)
            return false;
        if (internalId == null) {
            if (other.internalId != null)
                return false;
        } else if (!internalId.equals(other.internalId))
            return false;
        if (lastCreditCheck == null) {
            if (other.lastCreditCheck != null)
                return false;
        } else if (!lastCreditCheck.equals(other.lastCreditCheck))
            return false;
        if (lastPayment == null) {
            if (other.lastPayment != null)
                return false;
        } else if (!lastPayment.equals(other.lastPayment))
            return false;
        if (lastPaymentAmount == null) {
            if (other.lastPaymentAmount != null)
                return false;
        } else if (!lastPaymentAmount.equals(other.lastPaymentAmount))
            return false;
        if (lastPaymentDate == null) {
            if (other.lastPaymentDate != null)
                return false;
        } else if (!lastPaymentDate.equals(other.lastPaymentDate))
            return false;
        if (parentAccountCode == null) {
            if (other.parentAccountCode != null)
                return false;
        } else if (!parentAccountCode.equals(other.parentAccountCode))
            return false;
        if (parentExternalId == null) {
            if (other.parentExternalId != null)
                return false;
        } else if (!parentExternalId.equals(other.parentExternalId))
            return false;
        if (parentInternalId == null) {
            if (other.parentInternalId != null)
                return false;
        } else if (!parentInternalId.equals(other.parentInternalId))
            return false;
        if (person != other.person)
            return false;
        if (phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        } else if (!phoneNumber.equals(other.phoneNumber))
            return false;
        if (postalCode == null) {
            if (other.postalCode != null)
                return false;
        } else if (!postalCode.equals(other.postalCode))
            return false;
        if (printOnCheckAs == null) {
            if (other.printOnCheckAs != null)
                return false;
        } else if (!printOnCheckAs.equals(other.printOnCheckAs))
            return false;
        if (purchaseBalance == null) {
            if (other.purchaseBalance != null)
                return false;
        } else if (!purchaseBalance.equals(other.purchaseBalance))
            return false;
        if (purchaseCreditLimit == null) {
            if (other.purchaseCreditLimit != null)
                return false;
        } else if (!purchaseCreditLimit.equals(other.purchaseCreditLimit))
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        } else if (!region.equals(other.region))
            return false;
        if (riskDepositAmount == null) {
            if (other.riskDepositAmount != null)
                return false;
        } else if (!riskDepositAmount.equals(other.riskDepositAmount))
            return false;
        if (shortName == null) {
            if (other.shortName != null)
                return false;
        } else if (!shortName.equals(other.shortName))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (subsidiary == null) {
            if (other.subsidiary != null)
                return false;
        } else if (!subsidiary.equals(other.subsidiary))
            return false;
        if (suppressStatement != other.suppressStatement)
            return false;
        if (taxId == null) {
            if (other.taxId != null)
                return false;
        } else if (!taxId.equals(other.taxId))
            return false;
        if (terms == null) {
            if (other.terms != null)
                return false;
        } else if (!terms.equals(other.terms))
            return false;
        if (unappliedBalance == null) {
            if (other.unappliedBalance != null)
                return false;
        } else if (!unappliedBalance.equals(other.unappliedBalance))
            return false;
        if (unitLimit1 == null) {
            if (other.unitLimit1 != null)
                return false;
        } else if (!unitLimit1.equals(other.unitLimit1))
            return false;
        if (unitLimit2 == null) {
            if (other.unitLimit2 != null)
                return false;
        } else if (!unitLimit2.equals(other.unitLimit2))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ClientVO [accountCode=" + accountCode + ", accountName=" + accountName + ", address1=" + address1
                + ", address2=" + address2 + ", addressExternalId=" + addressExternalId + ", addressInternalId="
                + addressInternalId + ", aging30=" + aging30 + ", aging30Detail=" + aging30Detail + ", aging60="
                + aging60 + ", aging60Detail=" + aging60Detail + ", aging90=" + aging90 + ", aging90Detail="
                + aging90Detail + ", aging91Plus=" + aging91Plus + ", aging91PlusDetail=" + aging91PlusDetail
                + ", agingAsOfDate=" + agingAsOfDate + ", agingCurrent=" + agingCurrent + ", agingCurrentDetail="
                + agingCurrentDetail + ", balance=" + balance + ", bankAccountNumber=" + bankAccountNumber
                + ", bankName=" + bankName + ", bankNumber=" + bankNumber + ", bankType=" + bankType + ", bankrupt="
                + bankrupt + ", capitalLimit1=" + capitalLimit1 + ", capitalLimit2=" + capitalLimit2 + ", category="
                + category + ", city=" + city + ", collectionNote=" + collectionNote + ", collectionStatus="
                + collectionStatus + ", collector=" + collector + ", country=" + country + ", county=" + county
                + ", creditLimit1=" + creditLimit1 + ", creditLimit2=" + creditLimit2 + ", creditManagementType="
                + creditManagementType + ", creditScore=" + creditScore + ", creditStatus=" + creditStatus
                + ", currency=" + currency + ", defaultBilling=" + defaultBilling + ", defaultShipping="
                + defaultShipping + ", depositBalance=" + depositBalance + ", email=" + email + ", entityId=" + entityId
                + ", externalId=" + externalId + ", faxNumber=" + faxNumber + ", financeCharge=" + financeCharge
                + ", inactive=" + inactive + ", incorporationState=" + incorporationState + ", interimFinanceCharge="
                + interimFinanceCharge + ", internalId=" + internalId + ", lastCreditCheck=" + lastCreditCheck
                + ", lastPayment=" + lastPayment + ", lastPaymentAmount=" + lastPaymentAmount + ", lastPaymentDate="
                + lastPaymentDate + ", parentAccountCode=" + parentAccountCode + ", parentExternalId="
                + parentExternalId + ", parentInternalId=" + parentInternalId + ", person=" + person + ", phoneNumber="
                + phoneNumber + ", postalCode=" + postalCode + ", printOnCheckAs=" + printOnCheckAs
                + ", purchaseBalance=" + purchaseBalance + ", purchaseCreditLimit=" + purchaseCreditLimit + ", region="
                + region + ", riskDepositAmount=" + riskDepositAmount + ", shortName=" + shortName + ", status="
                + status + ", subsidiary=" + subsidiary + ", suppressStatement=" + suppressStatement + ", taxId="
                + taxId + ", terms=" + terms + ", unappliedBalance=" + unappliedBalance + ", unitLimit1=" + unitLimit1
                + ", unitLimit2=" + unitLimit2 + "]";
    }

}
