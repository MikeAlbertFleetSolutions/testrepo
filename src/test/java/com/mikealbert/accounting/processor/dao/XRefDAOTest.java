package com.mikealbert.accounting.processor.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.entity.XRef;

@DataJpaTest
public class XRefDAOTest extends BaseTest{
	@Resource XRefDAO xRefDAO;
		
    static final String GROUP_NAME = "LEASE_TYPE";
    static final String INTERNAL_VALUE = "OE_LTD";
    static final String EXTERNAL_VALUE = "3";
    
	@Test
	public void testFindByGroupName() {

		List<XRef> xRefs = xRefDAO.findByGroupName(GROUP_NAME);
		
		assertTrue(xRefs.size() > 0, "XRefs were not found");
	}
	
	@Test
	public void testFindByGroupNameAndInternalValue() {

		List<XRef> xRefs = xRefDAO.findByGroupNameAndInternalValue(GROUP_NAME, INTERNAL_VALUE);
		
		assertEquals(1, xRefs.size(), "XRef value was not found");
	}
	
	@Test
	public void testFindByGroupNameAndExtetrnalValue() {

		List<XRef> xRefs = xRefDAO.findByGroupNameAndExternalValue(GROUP_NAME, EXTERNAL_VALUE);
		
		assertEquals(1, xRefs.size(), "XRef value was not found");
	}
		
}
