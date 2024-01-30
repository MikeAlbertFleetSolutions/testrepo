package com.mikealbert.accounting.processor.client.suitetalk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Resource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.mikealbert.accounting.processor.TransactionBaseTest;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;


@DisplayName("Given a customer credit memo request")
@SpringBootTest
public class CreditMemoSuiteTalkServiceTest extends TransactionBaseTest{
	@Resource CreditMemoSuiteTalkService creditMemoSuiteTalkService;

	@DisplayName("when getting based on internal and/or external id, then the matching credit memo is returned")
	@Test
	public void testGet() throws Exception {
		ClientCreditMemoVO mockClientCreditMemoVO = super.generateMockClientCreditMemo(1)
		        .setGrouped(true)
				.setGroupNumber("xnnn");
		

		creditMemoSuiteTalkService.create(mockClientCreditMemoVO);

		ClientCreditMemoVO actualClientCreditMemoVO = creditMemoSuiteTalkService.get(null, mockClientCreditMemoVO.getExternalId()); 

		creditMemoSuiteTalkService.delete(actualClientCreditMemoVO);
		
		assertNotNull(actualClientCreditMemoVO.getInternalId());
		assertNotNull(actualClientCreditMemoVO.getExternalId());
		assertNotNull(actualClientCreditMemoVO.getTranId());
		assertNotNull(actualClientCreditMemoVO.getDocId());
		assertNotNull(actualClientCreditMemoVO.getDocLineId());
		assertNotNull(actualClientCreditMemoVO.getApplied());
		assertNotNull(actualClientCreditMemoVO.getTotal());
		assertNotNull(actualClientCreditMemoVO.getGroupNumber());
		
		assertTrue(actualClientCreditMemoVO.isGrouped());
		
		assertEquals(mockClientCreditMemoVO.getExternalId(), actualClientCreditMemoVO.getExternalId());

		for(int i = 0; i < mockClientCreditMemoVO.getLines().size(); i++) {
			assertEquals(mockClientCreditMemoVO.getLines().get(i).getDocId(), actualClientCreditMemoVO.getLines().get(i).getDocId());
			assertEquals(mockClientCreditMemoVO.getLines().get(i).getDocLineId(), actualClientCreditMemoVO.getLines().get(i).getDocLineId());			
			assertEquals(mockClientCreditMemoVO.getLines().get(i).getDriverId(), actualClientCreditMemoVO.getLines().get(i).getDriverId());
			assertEquals(mockClientCreditMemoVO.getLines().get(i).getDriverFirstName(), actualClientCreditMemoVO.getLines().get(i).getDriverFirstName());
			assertEquals(mockClientCreditMemoVO.getLines().get(i).getDriverLastName(), actualClientCreditMemoVO.getLines().get(i).getDriverLastName());
			assertEquals(mockClientCreditMemoVO.getLines().get(i).getDriverCostCenter(), actualClientCreditMemoVO.getLines().get(i).getDriverCostCenter());
			//assertEquals(mockClientCreditMemoVO.getLines().get(i).getDriverCostCenterDescription(), actualClientCreditMemoVO.getLines().get(i).getDriverCostCenterDescription());
			assertEquals(mockClientCreditMemoVO.getLines().get(i).getDriverRechargeCode(), actualClientCreditMemoVO.getLines().get(i).getDriverRechargeCode());
			assertEquals(mockClientCreditMemoVO.getLines().get(i).getDriverFleetRefNo(), actualClientCreditMemoVO.getLines().get(i).getDriverFleetRefNo());
		}
	}

	@Override
	protected String getUnitNo() {
		// TODO Auto-generated method stub
		return null;
	}

}
