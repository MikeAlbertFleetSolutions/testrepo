package com.mikealbert.accounting.processor.client.suitetalk;

import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.vo.SuiteTalkDiagnostic;

@Service("suiteTalkDiagnosticService")
public class SuiteTalkDiagnosticServiceImpl extends BaseSuiteTalkService implements SuiteTalkDiagnosticService{
	public SuiteTalkDiagnostic getSuiteTalkDiagnostic() throws Exception {
		return super.getSuiteTalkDiagnostic();
	}	

}
