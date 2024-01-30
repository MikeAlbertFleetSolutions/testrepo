package com.mikealbert.accounting.processor.service;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.dao.ContractLineDAO;
import com.mikealbert.accounting.processor.entity.ContractLine;

@Service("contractService")
public class ContractServiceImpl extends BaseService implements ContractService{
	@Resource ContractLineDAO contractLineDAO;

	private final Logger LOG = LogManager.getLogger(this.getClass());

	@Value("${mafs.help.desk.email}")
	String emailTo;
	
	@Transactional(readOnly = true)
	@Override
	public void notify(Long contractLineId) {
		LOG.info("About to send email regarding in service date changing on contract line id {}", contractLineId);

		ContractLine line = contractLineDAO.findById(contractLineId).orElseThrow();
		
		String subject = String.format("con_id %d In Service Date changed", line.getConConId());
		String body = String.format("con_id %d cln_id %d In Service Date changed for unit %s", line.getConConId(), line.getClnId(), line.getFleetMaster().getUnitNo());

		LOG.info("Email subject: {} and body: {}", subject, body);
		
		super.sendtextEmail(emailTo, subject, body);
		
		LOG.info("Successfully sent email regarding in service date changing on contract id {}, contract line id {}, unit no {}", line.getConConId(), line.getClnId(), line.getFleetMaster().getUnitNo());		
	}
}	
