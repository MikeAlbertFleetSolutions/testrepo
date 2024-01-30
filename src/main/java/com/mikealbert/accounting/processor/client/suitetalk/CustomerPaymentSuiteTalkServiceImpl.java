package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mikealbert.accounting.processor.exception.SuiteTalkDuplicateRecordException;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.service.XRefService;
import com.mikealbert.accounting.processor.vo.ClientPaymentApplyVO;
import com.mikealbert.accounting.processor.vo.ClientPaymentVO;
import com.mikealbert.util.data.DateUtil;
import com.netsuite.webservices.platform.common_2023_2.TransactionSearchBasic;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.CustomFieldRef;
import com.netsuite.webservices.platform.core_2023_2.InitializeRecord;
import com.netsuite.webservices.platform.core_2023_2.InitializeRef;
import com.netsuite.webservices.platform.core_2023_2.Record;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchDateField;
import com.netsuite.webservices.platform.core_2023_2.SearchEnumMultiSelectField;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectField;
import com.netsuite.webservices.platform.core_2023_2.types.InitializeRefType;
import com.netsuite.webservices.platform.core_2023_2.types.InitializeType;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.core_2023_2.types.SearchDateFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchEnumMultiSelectFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchMultiSelectFieldOperator;
import com.netsuite.webservices.platform.faults_2023_2.types.StatusDetailCodeType;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.platform.messages_2023_2.WriteResponse;
import com.netsuite.webservices.transactions.customers_2023_2.CreditMemo;
import com.netsuite.webservices.transactions.customers_2023_2.CreditMemoApply;
import com.netsuite.webservices.transactions.customers_2023_2.CustomerPayment;
import com.netsuite.webservices.transactions.customers_2023_2.CustomerPaymentApply;
import com.netsuite.webservices.transactions.customers_2023_2.DepositApplication;
import com.netsuite.webservices.transactions.customers_2023_2.DepositApplicationApply;
import com.netsuite.webservices.transactions.general_2023_2.JournalEntry;
import com.netsuite.webservices.transactions.sales_2023_2.TransactionSearch;

@Service("customerPaymentSuiteTalkService")
public class CustomerPaymentSuiteTalkServiceImpl extends BaseSuiteTalkService implements CustomerPaymentSuiteTalkService {
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource CustomerSuiteTalkService customerSuiteTalkService;
	@Resource XRefService xRefService;
		
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public void create(ClientPaymentVO paymentVO) throws Exception {
		WriteResponse response = service.getService().add(convertToCreateCustomerPayment(paymentVO));	
		if(!response.getStatus().isIsSuccess()) {
			if(response.getStatus().getStatusDetail(0).getCode() == StatusDetailCodeType.DUP_RCRD) {
				throw new SuiteTalkDuplicateRecordException(String.format("Error creating payment. ExternalId: %s", paymentVO.getExternalId()), response);				
			} else {
				throw new SuiteTalkException(String.format("Error creating payment. ExternalId: %s", paymentVO.getExternalId()), response);				
			}
		}
		
		LOG.info("Successfully created the Payment in the external accounting system. Payment -> {}", paymentVO);		
	}

	@Override
	public void update(ClientPaymentVO paymentVO) throws Exception {		 
		WriteResponse writeResponse = service.getService().update(convertToUpdateCustomerPayment(paymentVO));
		if(!writeResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error occurred while updating payment internalId=%s, externalId=%s", 
					paymentVO.getInternalId(), paymentVO.getExternalId()), writeResponse);
		}
		
		LOG.info("Successfully updated the externalIds for payment -> {}", paymentVO);
	}	

	@Override
	public ClientPaymentVO getPayment(String internalId, String externalId) throws Exception {
		LOG.info("Get Payment from accounting system ");
        
		CustomerPayment customerPayment = getCustomerPayment(internalId, externalId);

		ClientPaymentVO paymentVO = convertToClientPaymentVO(customerPayment);

		return paymentVO;
	}

	public ClientPaymentApplyVO getPaymentApply(String invoiceInternalId, String paymentInternalId, String paymentExternalId, String paymentType) throws Exception {
		ClientPaymentApplyVO paymentVO = null;
        
		switch(paymentType) {
			case "creditmemo":
			    CreditMemo creditMemo = getCreditMemo(paymentInternalId, paymentExternalId);
			    paymentVO = convertToClientPaymentApplyVO(invoiceInternalId, creditMemo);				
				break;
			case "depositapplication":
			    DepositApplication depositApplication = getCustomerDepositApplication(paymentInternalId, paymentExternalId);
			    paymentVO = convertToClientPaymentApplyVO(invoiceInternalId, depositApplication);				
				break;
			case "customerpayment":
			    CustomerPayment customerPayment = getCustomerPayment(paymentInternalId, paymentExternalId);
			    paymentVO = convertToClientPaymentApplyVO(invoiceInternalId, customerPayment);
				break;
			case "journalentry":
			    JournalEntry journalEntry = getJournalEntry(paymentInternalId, paymentExternalId);
			    paymentVO = convertToClientPaymentApplyVO(invoiceInternalId, journalEntry);
				break;
			default:
                throw new SuiteTalkException(String.format("Unsupported payment type. invoice internalId %s, paymentInternalId %s, paymentExternalId %s. paymentType %s ", invoiceInternalId, paymentInternalId, paymentExternalId, paymentType));
		}

		return paymentVO;		
	}	
			
	@Override
	public void delete(ClientPaymentVO paymentVO) throws Exception {
		RecordRef recordRef = new RecordRef(null, paymentVO.getInternalId(), paymentVO.getExternalId() == null ? null : paymentVO.getExternalId().toString(), RecordType.customerPayment);
		WriteResponse response = service.getService().delete(recordRef, null);
		
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException("Error deleting customer payment", response);
		}		
	}

	@Override
	public ClientPaymentVO getLastPayment(String clientInternalId, String clientExternalId) throws Exception {
		ClientPaymentVO clientPaymentVO = null;
		 
		List<Record> records = super.service.searchX(convertToPaymentTransactionSearch(clientInternalId, clientExternalId));

		CustomerPayment latestCustomerPayment = records.stream()
				.map(record -> (CustomerPayment)record)
				.sorted(Comparator.comparing(CustomerPayment::getTranDate, Comparator.reverseOrder())
						.thenComparing(CustomerPayment::getCreatedDate, Comparator.reverseOrder())
						.thenComparing(CustomerPayment::getInternalId, Comparator.reverseOrder()))
				.findFirst()
				.orElse(null);

		clientPaymentVO = convertToClientPaymentVO((latestCustomerPayment));
		

		return clientPaymentVO;
	}

	private TransactionSearch convertToPaymentTransactionSearch(String clientInternalId, String clientExternalId) {
		TransactionSearch ts = new TransactionSearch();
		TransactionSearchBasic tsb = new TransactionSearchBasic();

		SearchEnumMultiSelectField transType = new SearchEnumMultiSelectField();
		transType.setOperator(SearchEnumMultiSelectFieldOperator.anyOf);
		transType.setSearchValue(new String[] {"_customerPayment"});
		tsb.setType(transType);

		SearchDateField tranDateSearchField = new SearchDateField();
		tranDateSearchField.setOperator(SearchDateFieldOperator.onOrBefore);
		tranDateSearchField.setSearchValue(Calendar.getInstance());
		tsb.setTranDate(tranDateSearchField);

		SearchMultiSelectField clientSearchField = new SearchMultiSelectField();
		clientSearchField.setOperator(SearchMultiSelectFieldOperator.anyOf);
		clientSearchField.setSearchValue(new RecordRef[]{new RecordRef(null, clientInternalId, clientExternalId, RecordType.customer)});
		tsb.setEntity(clientSearchField);

		ts.setBasic(tsb);
		return ts;
	}	

	private CustomerPayment convertToCreateCustomerPayment(ClientPaymentVO paymentVO) throws Exception {
		List<CustomFieldRef> paymentCustomFields = new ArrayList<>(0);
		CustomerPayment customerPayment = null;

		InitializeRef clientRef = new InitializeRef();
		clientRef.setType(InitializeRefType.customer);
		clientRef.setExternalId(paymentVO.getClientExternalId());
		
		InitializeRecord paymentInitRec = new InitializeRecord();
		paymentInitRec.setType(InitializeType.customerPayment);
		paymentInitRec.setReference(clientRef);

		ReadResponse customerPaymentResponse = service.getService().initialize(paymentInitRec);
		if(!customerPaymentResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error initializing Customer Payment. %s ", paymentVO.toString()), customerPaymentResponse);
		}

	    customerPayment = (CustomerPayment)customerPaymentResponse.getRecord();
		customerPayment.setExternalId(paymentVO.getExternalId() == null ? null : paymentVO.getExternalId().toString());
		customerPayment.setAccount(new RecordRef(paymentVO.getPayableAccount(), null, null, RecordType.account));
		customerPayment.setTranDate(DateUtil.convertToCalendar(paymentVO.getTranDate()));
		customerPayment.setPayment(paymentVO.getAmount().doubleValue());
		customerPayment.setMemo(paymentVO.getMemo());
		customerPayment.setPnRefNum(paymentVO.getReference());
		customerPayment.setPaymentMethod(new RecordRef(paymentVO.getPaymentMethod(), super.service.searchPaymentMethodInternalIdByName(paymentVO.getPaymentMethod()), null, RecordType.paymentMethod));
		customerPayment.setUndepFunds(true);
				
		customerPayment.setCustomFieldList(new CustomFieldList(paymentCustomFields.toArray(CustomFieldRef[]::new)));  
		
		return customerPayment;
	}

	private CustomerPayment convertToUpdateCustomerPayment(ClientPaymentVO paymentVO) throws Exception {
		ReadResponse readResponse = super.service.getService().get(new RecordRef(null, paymentVO.getInternalId(), paymentVO.getExternalId() == null ? null : paymentVO.getExternalId().toString(), RecordType.customerPayment));
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Error occurred while retriving customer payment internalId=%s and externalId=%s", paymentVO.getInternalId(), paymentVO.getExternalId()), readResponse);			
		}

		CustomerPayment customerPayment = (CustomerPayment)readResponse.getRecord();
		customerPayment.setExternalId(paymentVO.getExternalId() == null ? null : paymentVO.getExternalId().toString());
		customerPayment.setPaymentMethod(new RecordRef(paymentVO.getPaymentMethod(), super.service.searchPaymentMethodInternalIdByName(paymentVO.getPaymentMethod()), null, RecordType.paymentMethod)); // TODO This is a hack around NS not persisting the Payment Method assigned in the create.
		
		return customerPayment;
	}	

	private ClientPaymentVO convertToClientPaymentVO(CustomerPayment customerPayment) throws Exception {
		ClientPaymentVO paymentVO = new ClientPaymentVO();
		paymentVO.setInternalId(customerPayment.getInternalId());
		paymentVO.setExternalId(customerPayment.getExternalId());
		paymentVO.setMemo(customerPayment.getMemo());
		paymentVO.setTranId(customerPayment.getTranId());
		paymentVO.setTranDate(customerPayment.getTranDate().getTime());
		paymentVO.setClientExternalId(customerSuiteTalkService.getCustomer(customerPayment.getCustomer().getInternalId(), null, false).getExternalId()); //TODO Takes time
		paymentVO.setAmount(BigDecimal.valueOf(customerPayment.getPayment()));
		paymentVO.setAppliedAmount(BigDecimal.valueOf(customerPayment.getApplied()));
		paymentVO.setUnAppliedAmount(BigDecimal.valueOf(customerPayment.getUnapplied()));
		paymentVO.setReference(customerPayment.getPnRefNum());
		return paymentVO;
	}
	
	private ClientPaymentApplyVO convertToClientPaymentApplyVO(String invoiceInternalId, JournalEntry journalEntry) throws Exception {
		return new ClientPaymentApplyVO()
		    .setInternalId(journalEntry.getInternalId())
		    .setTranId(journalEntry.getTranId())
		    .setTranDate(journalEntry.getTranDate().getTime());
	}

	private ClientPaymentApplyVO convertToClientPaymentApplyVO(String invoiceInternalId, CustomerPayment customerPayment) throws Exception {
		ClientPaymentApplyVO paymentVO = null;

		CustomerPaymentApply customerPaymentApply = Arrays.asList(customerPayment.getApplyList().getApply()).stream()
	            .filter(payment -> payment.getApply())
				.filter(payment -> payment.getDoc().equals(Long.parseLong(invoiceInternalId)))
				.findFirst()
				.orElse(null);

		if(customerPaymentApply != null) {
			paymentVO = new ClientPaymentApplyVO()
					.setInternalId(customerPayment.getInternalId())
					.setExternalId(customerPayment.getExternalId())
					.setStatus(customerPayment.getStatus())
					.setMemo(customerPayment.getMemo())
					.setTranId(customerPayment.getTranId())
					.setTranDate(customerPayment.getTranDate().getTime())
					.setInvoiceInternalId(customerPaymentApply.getDoc().toString())
					.setInvoiceTranId(customerPaymentApply.getRefNum())
					.setAppliedAmount(customerPaymentApply.getAmount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(customerPaymentApply.getAmount()));			
		}

		return paymentVO;
	}

	private ClientPaymentApplyVO convertToClientPaymentApplyVO(String invoiceInternalId, CreditMemo creditMemo) throws Exception {
		ClientPaymentApplyVO paymentVO = null;

		CreditMemoApply creditMemoApply = Arrays.asList(creditMemo.getApplyList().getApply()).stream()
	            .filter(payment -> payment.getApply())
				.filter(payment -> payment.getDoc().equals(Long.parseLong(invoiceInternalId)))
				.findFirst()
				.orElse(null);

		if(creditMemoApply != null) {
			paymentVO = new ClientPaymentApplyVO()
					.setInternalId(creditMemo.getInternalId())
					.setExternalId(creditMemo.getExternalId())
					.setStatus(creditMemo.getStatus())
					.setMemo(creditMemo.getMemo())
					.setTranId(creditMemo.getTranId())
					.setTranDate(creditMemo.getTranDate().getTime())
					.setInvoiceInternalId(creditMemoApply.getDoc().toString())
					.setInvoiceTranId(creditMemoApply.getRefNum())
					.setAppliedAmount(creditMemoApply.getAmount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(creditMemoApply.getAmount()));			
		}

		return paymentVO;
	}	

	private ClientPaymentApplyVO convertToClientPaymentApplyVO(String invoiceInternalId, DepositApplication depositApplication) throws Exception {
		ClientPaymentApplyVO paymentVO = null;

		DepositApplicationApply depositApplicationApply = Arrays.asList(depositApplication.getApplyList().getApply()).stream()
	            .filter(payment -> payment.getApply())
				.filter(payment -> payment.getDoc().equals(Long.parseLong(invoiceInternalId)))
				.findFirst()
				.orElse(null);

		if(depositApplicationApply != null) {
			paymentVO = new ClientPaymentApplyVO()
					.setInternalId(depositApplication.getInternalId())
					.setExternalId(depositApplication.getExternalId())
					.setStatus(depositApplication.getStatus())
					.setMemo(depositApplication.getMemo())
					.setTranId(depositApplication.getTranId())
					.setTranDate(depositApplication.getTranDate().getTime())
					.setInvoiceInternalId(depositApplicationApply.getDoc().toString())
					.setInvoiceTranId(depositApplicationApply.getRefNum())
					.setAppliedAmount(depositApplicationApply.getAmount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(depositApplicationApply.getAmount()));			
		}

		return paymentVO;
	}	

	private CustomerPayment getCustomerPayment(String internalId, String externalId) throws Exception {
		ReadResponse readResponse;

		RecordRef recRef = new RecordRef();
		recRef.setInternalId(internalId);
		recRef.setExternalId(StringUtils.hasText(externalId) ? externalId : null);
		recRef.setType(RecordType.customerPayment);

		readResponse = super.service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException (String.format("Failed to retrieve customer payment with internalId = %s .", internalId), readResponse);
		}	
		
		return (CustomerPayment) readResponse.getRecord();	
	}
	
	private JournalEntry getJournalEntry(String internalId, String externalId) throws Exception {
		ReadResponse readResponse;

		RecordRef recRef = new RecordRef();
		recRef.setInternalId(internalId);
		recRef.setExternalId(StringUtils.hasText(externalId) ? externalId : null);
		recRef.setType(RecordType.journalEntry);

		readResponse = super.service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException (String.format("Failed to retrieve journal entry with internalId = %s .", internalId), readResponse);
		}	
		
		return (JournalEntry) readResponse.getRecord();	
	}

	private CreditMemo getCreditMemo(String internalId, String externalId) throws Exception {
		ReadResponse readResponse;

		RecordRef recRef = new RecordRef();
		recRef.setInternalId(internalId);
		recRef.setExternalId(StringUtils.hasText(externalId) ? externalId : null);
		recRef.setType(RecordType.creditMemo);

		readResponse = super.service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException (String.format("Failed to retrieve customer credit memo with internalId = %s .", internalId), readResponse);
		}	
		
		return (CreditMemo) readResponse.getRecord();	
	}	
	
	private DepositApplication getCustomerDepositApplication(String internalId, String externalId) throws Exception {
		ReadResponse readResponse;

		RecordRef recRef = new RecordRef();
		recRef.setInternalId(internalId);
		recRef.setExternalId(StringUtils.hasText(externalId) ? externalId : null);
		recRef.setType(RecordType.depositApplication);

		readResponse = super.service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException (String.format("Failed to retrieve customer deposit application with internalId = %s .", internalId), readResponse);
		}	
		
		return (DepositApplication) readResponse.getRecord();	
	}	
}