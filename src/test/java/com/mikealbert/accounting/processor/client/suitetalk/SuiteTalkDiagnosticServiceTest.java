package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.vo.SuiteTalkDiagnostic;

@SpringBootTest
public class SuiteTalkDiagnosticServiceTest extends BaseTest {
	@Resource SuiteTalkDiagnosticService suiteTalkDiagnosticService;
	
	@Test
	public void testGetSuiteTalkDiagnostic() throws Exception {
		SuiteTalkDiagnostic diagnostic = suiteTalkDiagnosticService.getSuiteTalkDiagnostic();
		assertTrue(diagnostic.isLeaseRecord(), "Did not detect lease record");
		assertTrue(diagnostic.isLeaseModificationRecord(), "Did not detect lease modification record");
		assertTrue(diagnostic.isLeasePaymentRecord(), "Did not detect lease payment record");
		assertTrue(diagnostic.isUnitCustomSegment(), "Did not detect unit segment");
		assertTrue(diagnostic.isAssetTypeRecord(), "Did not detect asset type record");
		assertTrue(diagnostic.isAssetRecord(), "Did not detect asset record");
		
	}
	
}
