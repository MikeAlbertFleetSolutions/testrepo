package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.List;
import java.util.Map;

public interface StateSuiteTalkService  {
	public String getStateInternalIdByShortName(List<Map<String, Object>> states, String shortName);

	public String getStateInternalIdByLongName(List<Map<String, Object>> states, String longName);	

	public String getShortNameByLongName(List<Map<String, Object>> states, String longName);
}
