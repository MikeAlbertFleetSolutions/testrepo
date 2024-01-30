package com.mikealbert.accounting.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mikealbert.accounting.processor.BaseTest;
import com.mikealbert.accounting.processor.dao.XRefDAO;
import com.mikealbert.accounting.processor.entity.XRef;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;

@SpringBootTest
public class XRefServiceTest extends BaseTest{
	@Resource XRefService xRefService;

    @MockBean XRefDAO xRefDAO;
	    
    static final XRefGroupNameEnum GROUP_NAME = XRefGroupNameEnum.LEASE_TYPE;
    static final String INTERNAL_VALUE = "OE_LTD";
    static final String EXTERNAL_VALUE = "Open End Vehicle";

    @Test
    public void testGetByGroupName() throws Exception {   	
    	List<XRef> mockXRefs = new ArrayList<>();
    	mockXRefs.add(generateMockXRef());
    	mockXRefs.add(generateMockXRef());    	

		when(xRefDAO.findByGroupName(GROUP_NAME.getName())).thenReturn(mockXRefs);
		
		List<XRef> actualXRefs = xRefService.getByGroupName(GROUP_NAME);
    	
		assertEquals(mockXRefs.size(), actualXRefs.size(), "Incorrect list of XRefs");
    }
    
    @Test
    public void testGetExternalValue() throws Exception {
    	XRef expectedXRef = generateMockXRef();
    	
    	List<XRef> mockXRefs = new ArrayList<>();
    	mockXRefs.add(generateMockXRef());

		when(xRefDAO.findByGroupNameAndInternalValue(GROUP_NAME.getName(), INTERNAL_VALUE)).thenReturn(mockXRefs);
		
		String actualXRef = xRefService.getExternalValue(GROUP_NAME, INTERNAL_VALUE);
    	
		assertEquals(expectedXRef.getxRefPK().getExternalValue(), actualXRef, "Incorrect xref value");
    }
    
    @Test
    public void testGetExternalValueWithNoXRef() {  
		assertThrows(Exception.class, () -> {
	    	List<XRef> mockXRefs = new ArrayList<>();

			when(xRefDAO.findByGroupNameAndExternalValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockXRefs);
			
			xRefService.getExternalValue(null, "");			
		});	
    } 
    
    @Test 
    public void testGetExternalValueWithMuliptleXRef() throws Exception {
		assertThrows(Exception.class, () -> {
	    	List<XRef> mockXRefs = new ArrayList<>();
	    	mockXRefs.add(generateMockXRef());
	    	mockXRefs.add(generateMockXRef());    	

			when(xRefDAO.findByGroupNameAndInternalValue(GROUP_NAME.getName(), "OE_EQUIP")).thenReturn(mockXRefs);
			
			xRefService.getExternalValue(GROUP_NAME, "OE_EQUIP");			
		});	    	
    }
        
    @Test
    public void testGetInternalValue() throws Exception {
    	XRef expectedXRef = generateMockXRef();
    	
    	List<XRef> mockXRefs = new ArrayList<>();
    	mockXRefs.add(generateMockXRef());

		when(xRefDAO.findByGroupNameAndExternalValue(GROUP_NAME.getName(), EXTERNAL_VALUE)).thenReturn(mockXRefs);
		
		String actualXRef = xRefService.getInternalValue(GROUP_NAME, EXTERNAL_VALUE);
    	
		assertEquals(expectedXRef.getxRefPK().getInternalValue(), actualXRef, "Incorrect xref value");
    }
    
    @Test
    public void testGetInternalValueWithNoXRef() throws Exception {
		assertThrows(Exception.class, () -> {
	    	List<XRef> mockXRefs = new ArrayList<>();

			when(xRefDAO.findByGroupNameAndExternalValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockXRefs);
			
			xRefService.getInternalValue(null, "");			
		});
    	
    }
    
    @Test 
    public void testGetInternalValueWithMuliptleXRef() throws Exception {
		assertThrows(Exception.class, () -> {
	    	List<XRef> mockXRefs = new ArrayList<>();
	    	mockXRefs.add(generateMockXRef());
	    	mockXRefs.add(generateMockXRef());    	

			when(xRefDAO.findByGroupNameAndExternalValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(mockXRefs);
			
			xRefService.getExternalValue(null, "");			
		});    	    	
    }    
        
	private XRef generateMockXRef() {
		XRef xRef = new XRef(GROUP_NAME.getName(), INTERNAL_VALUE, EXTERNAL_VALUE);
		return xRef;		
	}
		
}