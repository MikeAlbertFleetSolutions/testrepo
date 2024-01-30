package com.mikealbert.accounting.processor.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.client.suitetalk.VendorBillSuiteTalkService;
import com.mikealbert.accounting.processor.client.suitetalk.VendorCreditSuiteTalkService;
import com.mikealbert.accounting.processor.dao.DocDAO;
import com.mikealbert.accounting.processor.entity.Doc;
import com.mikealbert.accounting.processor.vo.CreditLineVO;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;

@Service("creditService")
public class CreditServiceImpl extends TransactionService implements CreditService {
	@Resource DocDAO docDAO;
	@Resource XRefService xRefService;
	@Resource VendorBillSuiteTalkService vendorBillSuiteTalkService;
	@Resource VendorCreditSuiteTalkService vendorCreditSuiteTalkService;	
		
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	public CreditVO getCredit(Long docId) throws Exception {
		CreditVO credit = initializeCredit(docId);
		
		credit.getLines().stream()
		.map(line -> line.setHeader(credit))	
		.map(line -> absoluteRateValue(line))
		.map(line -> bindItem(line))
		.map(line -> (CreditLineVO)bindDepartment(line))  
		.map(line -> (CreditLineVO)patchDepartment(line))  		
		.map(line -> (CreditLineVO)bindBusinessUnit(line))
		.map(line -> (CreditLineVO)patchBusinessUnit(line))		
		.collect(Collectors.toList());		

		credit.setMemo(determineMemo(credit));
		
		return credit;
	}	
	
	@Override
	public void create(CreditVO credit) throws Exception {
		LOG.info("Sending credit {} to accounting system", credit.toString());
		vendorCreditSuiteTalkService.create(credit);
		LOG.info("Sending credit {} sent accounting system", credit.toString());		
	}
	
	@Transactional
	@Override
	public void updateGlAccToOne(Long docId) throws Exception {
		Doc credit = docDAO.findById(docId).orElse(null);
		credit.setGlAcc(1L);
		docDAO.save(credit);		
	}	
	
	@Override
	public List<Long> getMaintenanceCreditIds(Date start, Date end) {
		return docDAO.getMaintenanceCreditIds(start, end);
	}
		
	private CreditVO initializeCredit(Long docId) throws Exception {
		CreditVO credit = docDAO.getCreditApHeaderByDocId(docId);
		if(credit == null) throw new Exception(String.format("Credit header was not found for docId: %d", docId));
		
		List<CreditLineVO> lines = docDAO.getCreditApLinesByDocId(docId);
		if(lines.isEmpty()) throw new Exception(String.format("At least one line item is needed to create a vendor credit. No credit lines were not found for docId: %d", docId));
		
		credit.setApprovalDepartment(VendorBillSuiteTalkService.APPROVAL_DEPARTMENT);
		credit.setPayableAccount(VendorBillSuiteTalkService.PAYABLE_ACCOUNT);
		credit.setLines(lines);
		credit.setSubsidiary(Long.valueOf(xRefService.getExternalValue(XRefGroupNameEnum.COMPANY, String.valueOf(credit.getSubsidiary()))));
		credit.setVendorAddressInternalId(getVendorInternalAddressId(credit));
		
		return credit;
	}
	
	private CreditLineVO absoluteRateValue(CreditLineVO line) throws RuntimeException {
		return line.setRate(line.getRate().abs());
	}
	
	//TODO Assumption here is that credit will only have one line
	private String determineMemo(CreditVO header) throws RuntimeException {
		CreditLineVO line = header.getLines().get(0);
		return TransactionService.MAINTENANCE_AI_FEE_ITEM.equals(line.getItem()) ? MAINTENANCE_AI_FEE_MEMO : null;
	}	
			
}
