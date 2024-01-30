package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;

@DisplayName("Given a request")
@SpringBootTest
public class StateSuiteTalkServiceTest extends BaseTest{
	@Resource StateSuiteTalkService stateSuiteTalkService;
	@Resource SuiteTalkCacheService suiteTalkCacheService;

	@DisplayName("when the short name of a state, then the internalId of the matched state is returned")
	@Test
	public void testStateInternalIdByShortName() throws Exception{

		String internalId = stateSuiteTalkService.getStateInternalIdByShortName(suiteTalkCacheService.getStates(), "OH");

		assertNotNull(internalId);
	}

	@DisplayName("when the long name of a state, then the internalId of the matched state is returned")
	@Test
	public void testStateInternalIdByLongName() throws Exception{

		String internalId = stateSuiteTalkService.getStateInternalIdByLongName(suiteTalkCacheService.getStates(), "Ohio");

		assertNotNull(internalId);
	}

	@Test
	public void testGetShortNameByLongName() throws Exception {
		final String LONG_NAME = "Ohio";
		final String EXPECTED_SHORT_NAME = "OH";

		String actualShortName =  stateSuiteTalkService.getShortNameByLongName(suiteTalkCacheService.getStates(), LONG_NAME);

		assertEquals(EXPECTED_SHORT_NAME, actualShortName);
	}

	@Test
	public void testGetShortNameByLongNameWithNullLongName() throws Exception {
		final String LONG_NAME = null;

		String actualShortName =  stateSuiteTalkService.getShortNameByLongName(suiteTalkCacheService.getStates(), LONG_NAME);

		assertNull(actualShortName);
	}

}
