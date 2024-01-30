package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.client.suitetalk.DriverSuiteTalkService;
import com.mikealbert.accounting.processor.dao.DriverDAO;
import com.mikealbert.accounting.processor.entity.AccountingEvent;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryVO;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;
import com.mikealbert.util.data.DateUtil;

@SpringBootTest
@DisplayName("Test class for DriverService")
public class DriverServiceTest extends BaseTest{
	@Resource DriverService driverService;
	@MockBean DriverSuiteTalkService mockDrvSuiteTalkService;
	@MockBean DriverDAO mockDriverDao;
	
	List<AccountingEvent> mockEvents;
	List<DriverUnitHistoryUpsertVO> duhs;

	@BeforeEach
	private void getAccountingEvents() {
		mockEvents = new ArrayList<>();
		Date now = DateUtil.convertLocalDateToDate(LocalDate.now());
		
		AccountingEvent e = new AccountingEvent();
		e.setEntityId("1");
		e.setCreateDate(now);
		e.setEntity(AccountingNounEnum.DRIVER.name());
		e.setEvent(EventEnum.UPSERT.name());
		mockEvents.add(e);
		
		e.setEntityId("2");
		e.setCreateDate(now);
		e.setEntity(AccountingNounEnum.DRIVER.name());
		e.setEvent(EventEnum.UPSERT.name());
		mockEvents.add(e);
		
		e.setEntityId("3");
		e.setCreateDate(now);
		e.setEntity(AccountingNounEnum.DRIVER.name());
		e.setEvent(EventEnum.UPSERT.name());
		mockEvents.add(e);
		
		duhs = new ArrayList<>();
		DriverUnitHistoryUpsertVO duh = new DriverUnitHistoryUpsertVO();
		duh
			.setAetId(Long.valueOf("12345"))
			.setUnitNo("01010101")
			.setDrvId(12345l)
			.setNoun(AccountingNounEnum.DRIVER)
			.setEvent(EventEnum.DRIVER_CHANGE)
			.setDrvId(Long.valueOf("112233"))
			.setEffectiveDate(DateUtil.convertLocalDateToDate(LocalDate.now()))
			.setFirstName("FirstName")
			.setLastName("LastName")
			.getDriverAddress()
				.setAddressLine1("AddressLine1")
				.setTownDescription("Town")
				.setRegionCode("Ohio")
				.setZipCode("45241")
				.setCountryCode("Country");
		duhs.add(duh);

		DriverUnitHistoryUpsertVO duh1 = new DriverUnitHistoryUpsertVO();
		duh1
			.setAetId(Long.valueOf("23456"))
			.setUnitNo("11111111")
			.setDrvId(45678l)
			.setNoun(AccountingNounEnum.DRIVER_ALLOCATION)
			.setEvent(EventEnum.UPSERT)
			.setDalId(Long.valueOf("223344"))
			.setEffectiveDate(DateUtil.convertLocalDateToDate(LocalDate.now()))
			.setFirstName("FirstName")
			.setLastName("LastName")
			.getDriverAddress()
				.setAddressLine1("AddressLine1")
				.setTownDescription("Town")
				.setRegionCode("Ohio")
				.setZipCode("45241")
				.setCountryCode("Country");
		duhs.add(duh1);
	}
	
	@Test
	@DisplayName("Test method for initializeDriverUnitHistoryMethod")
	public void testInitializeDriverUnitHistoryRecords() throws Exception {
		driverService.initializeDriverUnitHistoryRecords(mockEvents).stream()
		.forEach(driverUnitHistoryUpsertVO -> { 
			assertNotNull(driverUnitHistoryUpsertVO.getDrvId());
			assertNotNull(driverUnitHistoryUpsertVO.getEffectiveDate());
			assertEquals(AccountingNounEnum.DRIVER, driverUnitHistoryUpsertVO.getNoun());
			assertEquals(EventEnum.UPSERT, driverUnitHistoryUpsertVO.getEvent());
		});
		
	}
	
	@Test
	@DisplayName("Test method for DriverUnitHistoryUpsert")
	public void testDriverUnitHistoryUpsert() throws Exception {
		String expectedOutcome = String.valueOf("Processed Driver Unit History records");		
		doNothing().when(mockDrvSuiteTalkService).upsertDriverUnitHistory(ArgumentMatchers.any());
		String actualOutcode = driverService.driverUnitHistoryUpsert(duhs);
		assertEquals(expectedOutcome, actualOutcode);
	}

	@Test
	@DisplayName("Test null effectiveDate results in error")
	public void testEmptyEffectiveDateResultsInError() throws Exception {
		duhs = duhs.parallelStream()
					.map(duh -> {
						duh.setEffectiveDate(null);
						return duh;
					})
					.collect(Collectors.toList());		
		assertThrows(Exception.class, () -> {
			driverService.driverUnitHistoryUpsert(duhs);
		});
	}

	@Test
	@DisplayName("Test null firstName results in error")
	public void testEmptyFirstNameResultsInError() throws Exception {
		duhs = duhs.parallelStream()
				.map(duh -> {
					duh.setFirstName(null);
					return duh;
				})
				.collect(Collectors.toList());		
		assertThrows(Exception.class, () -> {
			driverService.driverUnitHistoryUpsert(duhs);
		});
	}
	

	@Test
	@DisplayName("Test null lastName results in error")
	public void testEmptyLastNameResultsInError() throws Exception {
		duhs = duhs.parallelStream()
				.map(duh -> {
					duh.setLastName(null);
					return duh;
				})
				.collect(Collectors.toList());		
		assertThrows(Exception.class, () -> {
			driverService.driverUnitHistoryUpsert(duhs);
		});
	}
	
	@Test
	@DisplayName("Test null driver address results in error")
	public void testEmptyDriverAddressResultsInError() throws Exception {
		duhs = duhs.parallelStream()
				.map(duh -> {
					duh.setDriverAddress(null);
					return duh;
				})
				.collect(Collectors.toList());		
		assertThrows(Exception.class, () -> {
			driverService.driverUnitHistoryUpsert(duhs);
		});
	}

	@Test
	@DisplayName("Test null driver address line1 results in error")
	public void testEmptyDriverAddressLine1ResultsInError() throws Exception {
		duhs = duhs.parallelStream()
				.map(duh -> {
					duh.getDriverAddress().setAddressLine1(null);
					return duh;
				})
				.collect(Collectors.toList());		
		assertThrows(Exception.class, () -> {
			driverService.driverUnitHistoryUpsert(duhs);
		});
	}

	@Test
	@DisplayName("Test null driver address city results in error")
	public void testEmptyDriverAddressCityResultsInError() throws Exception {
		duhs = duhs.parallelStream()
				.map(duh -> {
					duh.getDriverAddress().setTownDescription(null);
					return duh;
				})
				.collect(Collectors.toList());		
		assertThrows(Exception.class, () -> {
			driverService.driverUnitHistoryUpsert(duhs);
		});
	}

	@Test
	@DisplayName("Test null driver address region results in error")
	public void testEmptyDriverAddressRegionResultsInError() throws Exception {
		duhs = duhs.parallelStream()
				.map(duh -> {
					duh.getDriverAddress().setRegionCode(null);
					return duh;
				})
				.collect(Collectors.toList());		
		assertThrows(Exception.class, () -> {
			driverService.driverUnitHistoryUpsert(duhs);
		});
	}

	@Test
	@DisplayName("Test null driver address zipcode results in error")
	public void testEmptyDriverAddressZipcodeResultsInError() throws Exception {
		duhs = duhs.parallelStream()
				.map(duh -> {
					duh.getDriverAddress().setZipCode(null);
					return duh;
				})
				.collect(Collectors.toList());		
		assertThrows(Exception.class, () -> {
			driverService.driverUnitHistoryUpsert(duhs);
		});
	}

	@Test
	@DisplayName("Test null driver address countrycode results in error")
	public void testEmptyDriverAddressCountryCodeResultsInError() throws Exception {
		duhs = duhs.parallelStream()
				.map(duh -> {
					duh.getDriverAddress().setCountryCode(null);
					return duh;
				})
				.collect(Collectors.toList());		
		assertThrows(Exception.class, () -> {
			driverService.driverUnitHistoryUpsert(duhs);
		});
	}

	@Test
	@DisplayName("when a DUH lookup criteria is missing, then the result is null")
	public void testReadDuhByUnitInternalIdAndDate() throws Exception {
		when(mockDrvSuiteTalkService.readDuhByUnitInternalIdAndDate(any(), any())).thenReturn(new DriverUnitHistoryVO());

		assertNull(driverService.readDuhByUnitInternalIdAndDate("0", null));
		assertNull(driverService.readDuhByUnitInternalIdAndDate(null, new Date()));
	}
}
