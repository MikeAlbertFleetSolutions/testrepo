package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service("stateSuiteTalkService")
public class StateSuiteTalkServiceImpl extends BaseSuiteTalkService implements StateSuiteTalkService {

	@Override
	public String getStateInternalIdByShortName(List<Map<String, Object>> states, String shortName) {
		Map<String, Object> state = states.stream()
		        .filter(s -> ((String)s.get("shortname")).equalsIgnoreCase(shortName))
				.findFirst()
				.orElse(null);

		return state == null ? null : (String)state.get("internalId");
	}

	@Override
	public String getStateInternalIdByLongName(List<Map<String, Object>> states, String longName) {
		Map<String, Object> state = states.stream()
		        .filter(s -> ((String)s.get("fullName")).equalsIgnoreCase(longName))
				.findFirst()
				.orElse(null);

		return state == null ? null : (String)state.get("internalId");
	}

	@Override
	public String getShortNameByLongName(List<Map<String, Object>> states, String longName) {
		Map<String, Object> state = states.stream()
		    .filter(s -> ((String)s.get("fullName")).equalsIgnoreCase(longName))
			.findFirst()
			.orElse(null);

		return state == null ? null : (String)state.get("shortname");
	}
			
}