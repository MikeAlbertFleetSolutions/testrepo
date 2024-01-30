package com.mikealbert.accounting.processor.item;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.vo.CreditLineVO;

@Component("creditItemHelper")
public class CreditItemHelper implements ItemHelper<CreditLineVO> {
	
	@Override
	public String generateKey(CreditLineVO line) throws Exception {
		Map<String, Object> key = new HashMap<>();
		key.put("controlCode", line.getHeader().getControlCode());
		key.put("index", 0);

		ObjectMapper mapper = new ObjectMapper();
		String retVal = mapper.writeValueAsString(key);

		return retVal;
	}		
}
