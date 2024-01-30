package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.dao.AppLogDAO;
import com.mikealbert.accounting.processor.entity.AppLog;

@SpringBootTest
public class AppLogServiceTests extends BaseTest{
	@Resource AppLogService appLogService;
	
	@MockBean  AppLogDAO appLogDAO;	
	
	@Test
	public void testGetMasterList() {	
		AppLog expected = new AppLog();
		
		when(appLogDAO.findTopByNameOrderByPlgIdDesc(ArgumentMatchers.same("UNIT-TEST"))).thenReturn(expected);
		
		AppLog actual = appLogService.getLatestLog("UNIT-TEST");
		
		assertEquals(expected, actual, "Master list is empty");		
	}	
		
}
