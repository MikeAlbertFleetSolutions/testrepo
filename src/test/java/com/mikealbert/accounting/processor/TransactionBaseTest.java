package com.mikealbert.accounting.processor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.stream.IntStream;

import com.mikealbert.accounting.processor.enumeration.BusinessUnitEnum;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoLineVO;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;
import com.mikealbert.accounting.processor.vo.CreditLineVO;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;

public abstract class TransactionBaseTest extends BaseTest{
	protected static long VENDOR_C_ID = 1;
	protected String VENDOR_ACCOUNT_TYPE = "S";
	protected String VENDOR_ACCOUNT_CODE = "00166163";
	protected String APPROVAL_DEPARTMENT = "Accounting";
	protected String VEHICLE_ITEM = "Vehicle";
	protected String RENT_ITEM = "Rent";
	protected String DISCOUNT_ITEM = "Discount"; 
	protected String DEPARTMENT_RENTAL = "Rental";
	protected BusinessUnitEnum CLASSIFICATION = BusinessUnitEnum.RENTAL;
	protected Long SUBSIDIARY = 1L;	
	protected final String PAYABLE_ACCOUNT = "20010 Total Accounts Payable : Accounts Payable";	
	
	protected PurchaseOrderVO generateMockPO(int numberOfLines) {
		PurchaseOrderVO po = new PurchaseOrderVO();
		po.setExternalId(String.valueOf(-1 * System.currentTimeMillis()));
		po.setVendor(VENDOR_ACCOUNT_CODE);
		po.setVendorAddressInternalId(7501L);
		po.setTranDate(new Date());
		po.setTranId("PON" + po.getExternalId());
		po.setApprovalDepartment(APPROVAL_DEPARTMENT);
		po.setAutoApprove(true);
		po.setUnit(this.getUnitNo()); 
		po.setControlCode(ControlCodeEnum.ST);
		po.setMain(true);
		po.setTaxDetailOverride(true);
		
		PurchaseOrderLineVO line = new PurchaseOrderLineVO();
		line.setItem(VEHICLE_ITEM);
		line.setQuantity(BigDecimal.ONE);
		line.setRate(new BigDecimal("1.00"));
		line.setDepartment(DEPARTMENT_RENTAL);
		line.setBusinessUnit(CLASSIFICATION);
		line.setDescription("My Shiny Line Item Description");	
		line.setUnit(this.getUnitNo());
		po.getLines().add(line);

		return po;
	}
	
	protected InvoiceVO generateInvoice(int numberOfLines) {return generateInvoice(numberOfLines, false);}
	protected InvoiceVO generateInvoice(int numberOfLines, boolean withDiscount) {
		InvoiceVO invoice = new InvoiceVO();
		invoice.setExternalId(String.valueOf(-1 * System.currentTimeMillis()));
		invoice.setSubsidiary(SUBSIDIARY);
		invoice.setTranId(invoice.getExternalId().toString());
		invoice.setTranDate(new Date());
		invoice.setVendor(VENDOR_ACCOUNT_CODE);
		invoice.setVendorAddressInternalId(7501L);
		invoice.setMemo("Test Memo");
		invoice.setApprovalDepartment(APPROVAL_DEPARTMENT);
		invoice.setAutoApprove(true);
		invoice.setPayableAccount(PAYABLE_ACCOUNT);
		invoice.setControlCode(ControlCodeEnum.ST);	
		invoice.setDiscount(new BigDecimal(0.25));

		IntStream.range(0, numberOfLines)
		.forEach(idx -> {
			InvoiceLineVO line = new InvoiceLineVO(invoice);
			line.setItem(VEHICLE_ITEM);
			line.setQuantity(BigDecimal.ONE);
			line.setRate(new BigDecimal("1.00"));
			line.setDepartment(DEPARTMENT_RENTAL);
			line.setBusinessUnit(CLASSIFICATION);
			line.setLocation("");
			line.setDescription(String.format("Line %d Description ", idx));
			line.setUnit(this.getUnitNo());	

			invoice.getLines().add(line);
		});
		
		if(withDiscount) {
			InvoiceLineVO line = new InvoiceLineVO(invoice);
			line.setItem(DISCOUNT_ITEM);
			line.setRate(new BigDecimal(-0.25));
			line.setDescription(String.format(DISCOUNT_ITEM));
			line.setUnit(this.getUnitNo());				

			invoice.getLines().add(line);			
		}


		return invoice;
	}
	
	protected CreditVO generateInvoiceCredit(int numberOfLines) {
		CreditVO credit = new CreditVO();
		credit.setExternalId(String.valueOf(-1 * System.currentTimeMillis()));
		credit.setSubsidiary(SUBSIDIARY);
		credit.setTranId(credit.getExternalId().toString());
		credit.setTranDate(new Date());
		credit.setVendor(VENDOR_ACCOUNT_CODE);
		credit.setVendorAddressInternalId(7501L);
		credit.setMemo("Test Memo");
		credit.setApprovalDepartment(APPROVAL_DEPARTMENT);
		credit.setAutoApprove(true);
		credit.setPayableAccount(PAYABLE_ACCOUNT);
		credit.setControlCode(ControlCodeEnum.ST);
		credit.setVendorEaaId(1L);

		IntStream.range(0, numberOfLines)
		.forEach(idx -> {
			CreditLineVO line = new CreditLineVO(credit);
			line.setItem(VEHICLE_ITEM);
			line.setQuantity(BigDecimal.ONE);
			line.setRate(new BigDecimal("-1.00"));
			line.setDepartment(DEPARTMENT_RENTAL);
			line.setBusinessUnit(CLASSIFICATION);
			line.setLocation("");
			line.setDescription(String.format("Line %d Description ", idx));
			line.setUnit(this.getUnitNo());	

			credit.getLines().add(line);
		});


		return credit;
	}	
	
	protected ClientCreditMemoVO generateMockClientCreditMemo(int numberOfLines) throws Exception {
		Thread.sleep(5);
		String uid = "-" + String.valueOf(System.currentTimeMillis());
		
		ClientCreditMemoVO mockClientCreditMemoVO = new ClientCreditMemoVO()
		        .setExternalId( String.valueOf(uid))
				.setClientExternalId("1C00032816")
				.setDocId(0L)
				.setDocLineId(0L)
				.setGrouped(false);

		IntStream.range(0, numberOfLines)
			.forEach(idx -> {				
				ClientCreditMemoLineVO mockClientCreditMemoLineVO = new ClientCreditMemoLineVO()
						.setItem("Open End - Lease Revenue")
						.setDescription("Monthly Rental")
						.setQuantity(BigDecimal.ONE)
						.setRate(BigDecimal.ONE)
						.setDocId(-1L)
					    .setDocLineId(-1L)
						.setMonthServiceDate(new Date())
						.setTransactionLineDate(new Date())
						.setDriverId(1L)
						.setDriverFirstName("John")
						.setDriverLastName("Doe")
						.setDriverState("OH")
						.setDriverCostCenter("UT-Cost Center")
						.setDriverCostCenterDescription("UT-Cost Center Description")
						.setDriverRechargeCode("UT-Recharge Code")
						.setDriverFleetRefNo("UT-Fleet Ref No");

				mockClientCreditMemoVO.getLines().add(mockClientCreditMemoLineVO);
		    });

		return mockClientCreditMemoVO;
	}
		
	protected abstract String getUnitNo();
}
