package com.mikealbert.accounting.processor.client.suitetalk;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mikealbert.accounting.processor.client.suiteanalytics.SuiteAnalyticsCacheService;
import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.ClientDepositApplicationVO;
import com.mikealbert.constant.enumeration.ApplicationEnum;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;
import com.netsuite.webservices.transactions.customers_2023_2.DepositApplication;

@Service("depositApplicationSuiteTalkService")
public class DepositApplicationSuiteTalkServiceImpl extends BaseSuiteTalkService implements DepositApplicationSuiteTalkService {
	@Resource SuiteTalkCacheService suiteTalkCacheService;
	@Resource SuiteAnalyticsCacheService suiteAnalyticsCacheService;
	@Resource CustomerSuiteTalkService customerSuiteTalkService;		
	
	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Override
	public ClientDepositApplicationVO get(String internalId, String externalId) throws Exception {
		DepositApplication depositApplication;

		ClientDepositApplicationVO clientDepositApplicationVO = null;

		depositApplication = getDepositApplication(internalId, externalId);

		clientDepositApplicationVO = convertToClientDepositApplicationVO(depositApplication);

		return clientDepositApplicationVO;
	}

	@Override
	public BigDecimal getAmountAppledToInvoice(String depositApplicationInternalId, String invoiceInternalId) throws Exception {
		DepositApplication depositApplications = getDepositApplication(depositApplicationInternalId, invoiceInternalId);

		return Arrays.asList(depositApplications.getApplyList().getApply()).stream()
				.filter(da -> da.getApply())
				.filter(da -> da.getDoc().equals(Long.valueOf(invoiceInternalId)))
				.map(da -> new BigDecimal(da.getAmount()))
				.findFirst()
				.orElseThrow();
	}	
	
	private DepositApplication getDepositApplication(String internalId, String externalId) throws Exception {
		ReadResponse readResponse;

		LOG.info("Get client deposit application from the accounting system ");
        
		RecordRef recRef = new RecordRef();
		recRef.setInternalId(internalId);
		recRef.setExternalId(StringUtils.hasText(externalId) ? externalId : null);
		recRef.setType(RecordType.depositApplication);

		readResponse = super.service.getService().get(recRef);		
		if(!readResponse.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Failed to retrieve the client's deposit application with internalId = %s and externalId = %s .", 
					internalId, externalId), readResponse);
		}		

		return (DepositApplication) readResponse.getRecord();	
	}

	private ClientDepositApplicationVO convertToClientDepositApplicationVO(DepositApplication depositApplication) throws Exception {
		return new ClientDepositApplicationVO(depositApplication.getInternalId(), depositApplication.getExternalId())
		        .setOrigin(depositApplication.getExternalId() == null ? ApplicationEnum.NETSUITE : ApplicationEnum.WILLOW);
	}
}