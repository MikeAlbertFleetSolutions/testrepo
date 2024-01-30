package com.mikealbert.accounting.processor;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import com.mikealbert.accounting.processor.service.OraSessionService;

@Component
public class AppInfoContributor implements InfoContributor {
	@Resource OraSessionService oraSessionServcie;
	
	@Value("${server.port}") String serverPort;	
	@Value("${mafs.suitetalk.version}") String mafsSuitetalkVersion;
	@Value("${mafs.suitetalk.account.id}") String mafsSuiteTalkId;
	
	@Override
	public void contribute(Builder builder) {
		Map<String, String> info = new HashMap<>();
		
		try {
			info.put("Microservice host", InetAddress.getLocalHost().getHostName());
		} catch (Exception e) {
			info.put("Microservice host", "unknown");			
		}		

		info.put("Microservice Port", serverPort);
		info.put("Database Version", oraSessionServcie.getDBRefreshdate());		
		info.put("SuiteTalk Verision", mafsSuitetalkVersion);		
		info.put("SuiteTalk Account Id", mafsSuiteTalkId);
		
		builder.withDetail("Environment", info); 
	}

}
