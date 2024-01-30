package com.mikealbert.accounting.processor.client.suitetalk;

import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.exception.SuiteTalkException;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.netsuite.webservices.lists.accounting_2023_2.AccountingPeriod;
import com.netsuite.webservices.platform.common_2023_2.AccountingPeriodSearchBasic;
import com.netsuite.webservices.platform.core_2023_2.Record;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchResult;
import com.netsuite.webservices.platform.core_2023_2.SearchStringField;
import com.netsuite.webservices.platform.core_2023_2.types.RecordType;
import com.netsuite.webservices.platform.core_2023_2.types.SearchStringFieldOperator;
import com.netsuite.webservices.platform.messages_2023_2.ReadResponse;

@Service("accountingPeriodSuiteTalkService")
public class AccountingPeriodSuiteTalkServiceImpl extends BaseSuiteTalkService implements AccountingPeriodSuiteTalkService {
	
	@Override
	public AccountingPeriodVO get(String internalId) throws Exception {
		RecordRef recordRef = new RecordRef();
		recordRef.setType(RecordType.accountingPeriod);
		recordRef.setInternalId(internalId);

		ReadResponse response = super.service.getService().get(recordRef);
		if(!response.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Failed to get Accounting Period from the accounting system. internalId = %s", internalId), response);
		}

		AccountingPeriod accountingPeriod = (AccountingPeriod)response.getRecord();

		AccountingPeriodVO accountingPeriodVO = new AccountingPeriodVO()
				.setInternalId(accountingPeriod.getInternalId())
				.setName(accountingPeriod.getPeriodName())
				.setStart(accountingPeriod.getStartDate().getTime())
				.setEnd(accountingPeriod.getEndDate().getTime());

		return accountingPeriodVO;
	}

	@Override
	public AccountingPeriodVO getByName(String periodName) throws Exception {
		AccountingPeriodVO accountingPeriodVO = null;

		AccountingPeriodSearchBasic apsb = new AccountingPeriodSearchBasic();
		apsb.setPeriodName(new SearchStringField(periodName.replace("-", " "), SearchStringFieldOperator.is));

		SearchResult result = super.service.getService().search(apsb);
		if(!result.getStatus().isIsSuccess()) {
			throw new SuiteTalkException(String.format("Failed to get Accounting Period from the accounting system. internalId = %s", periodName), result);
		}

		if(result.getRecordList().getRecord() != null) {
			for(Record record : result.getRecordList().getRecord()) {
				AccountingPeriod accountingPeriod = (AccountingPeriod)record;
				
				accountingPeriodVO = new AccountingPeriodVO()
				.setInternalId(accountingPeriod.getInternalId())
				.setName(accountingPeriod.getPeriodName())
				.setStart(accountingPeriod.getStartDate().getTime())
				.setEnd(accountingPeriod.getEndDate().getTime());
			}
		}
		
		return accountingPeriodVO;
	}

}