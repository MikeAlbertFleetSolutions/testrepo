package com.mikealbert.accounting.processor.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.annotation.Resource;

import com.mikealbert.accounting.processor.client.suitetalk.AccountingPeriodSuiteTalkService;
import com.mikealbert.accounting.processor.vo.AccountingPeriodVO;
import com.mikealbert.constant.enumeration.MonthEnum;
import com.mikealbert.util.data.DateUtil;

import org.springframework.stereotype.Service;

@Service("accountingPeriodService")
public class AccountingPeriodServiceImpl extends BaseService implements AccountingPeriodService {

	@Resource AccountingPeriodSuiteTalkService accountingPeriodSuiteTalkService;

	/**
	 * Retrieve the Accounting Period from the accounting system.
	 * 
	 * @param internalId The accounting system's identifier for the accounting period
	 *  
	 * @return the accounting period represented as {@link AccountingPeriodVO} 
	 */
	@Override
	public AccountingPeriodVO get(String internalId) throws Exception {
		return accountingPeriodSuiteTalkService.get(internalId);
	}

	/**
	 * Retrieve the Accounting Period from the accounting system.
	 * Expected format is MON YYYY or MON-YYYY
	 * 
	 * @param name The accounting system's accounting period name
	 *  
	 * @return the accounting period represented as {@link AccountingPeriodVO} 
	 */
	@Override
	public AccountingPeriodVO getByName(String name) throws Exception {
		return accountingPeriodSuiteTalkService.getByName(name);
	}

	/**
	 * Retrieve a list of Accounting Periods that are in the specified range
	 * * Expected period name format is MON YYYY or MON-YYYY
	 * 
	 * @param startPeriodName The start accounting period name
	 * @param endPeriodName The end accounting period name
	 * 
	 * @return list of accounting periods, see {@link AccountingPeriodVO} 
	 */
	@Override
	public List<AccountingPeriodVO> getByNameRange(String startPeriodName, String endtPeriodName) throws Exception {
		long monthsBetween = monthsBetween(startPeriodName, endtPeriodName);
		
		return periodNames(startPeriodName, monthsBetween).stream()
		    .map(n -> {
				try{
					return accountingPeriodSuiteTalkService.getByName(n);
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			})
			.collect(Collectors.toList());
	}

	private long monthsBetween(String startPeriodName, String endPeriodName) {		
		String[] startPeriodNameParts = startPeriodName.replace("-", " ").split(" ");
		String[] endPeriodNameParts = endPeriodName.replace("-", " ").split(" ");

		String startPeriodDateString = String.format("%s-%s-%s", startPeriodNameParts[1],  MonthEnum.getMonthByShortName(startPeriodNameParts[0]).getPosition(), "01");
		String endPeriodDateString = String.format("%s-%s-%s", endPeriodNameParts[1],  MonthEnum.getMonthByShortName(endPeriodNameParts[0]).getPosition(), "01");		

		long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.parse(startPeriodDateString), LocalDate.parse(endPeriodDateString));

		return monthsBetween;
	}

	private List<String> periodNames(String startPeriodName, long numberOfMonthsToInclude) {
		String[] startPeriodNameParts = startPeriodName.replace("-", " ").split(" ");

		List<String> periodNames = LongStream.rangeClosed(0, numberOfMonthsToInclude)
		    .mapToObj(i -> {
			    try{
				    Date date = DateUtil.addMonths(DateUtil.convertToDate(String.format("%s-%s", startPeriodNameParts[1], MonthEnum.getMonthByShortName(startPeriodNameParts[0]).getPosition()), DateUtil.PATTERN_YEAR_MONTH), i);
				    String dateString = DateUtil.convertToString(date, "MM-yyyy");
				    String periodNameParts[] = dateString.replace("-", " ").split(" ");
				    String periodName = String.format("%s-%s", MonthEnum.getMonthByPosition(periodNameParts[0]).getShortName(), periodNameParts[1]);
				    return periodName;
			    }catch(Exception e) {
				    throw new RuntimeException(e);
			    }
		    })
		    .collect(Collectors.toList());

		return periodNames;
	}
}
