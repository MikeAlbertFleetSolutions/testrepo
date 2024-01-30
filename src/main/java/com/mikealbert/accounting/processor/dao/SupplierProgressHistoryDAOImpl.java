package com.mikealbert.accounting.processor.dao;

import java.util.Date;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mikealbert.accounting.processor.entity.SupplierProgressHistory;

public class SupplierProgressHistoryDAOImpl extends GenericDAOImpl<SupplierProgressHistory, Long> implements SupplierProgressHistoryDAOCustom {
	private static final long serialVersionUID = 2133122325953033269L;
	
	private final Logger LOG = LogManager.getLogger(this.getClass());
	
	@Override
	public void logVehicleVendorBillPayment(Long docId, Date paymentDate, String paymentMethod){
		String stmt = "BEGIN fl_supp_prog_wrapper.cfg_sup_prog_ins(?, ?, ?); END;";
		
		LOG.info("Making database call here: docId::{}, paymentDate::{}, paymentMethod::{}", docId, paymentDate, paymentMethod);
				
		Query query = entityManager.createNativeQuery(stmt);
    	query.setParameter(1, docId);
    	query.setParameter(2, paymentDate);
    	query.setParameter(3, paymentMethod);
    	query.executeUpdate();
    	
	}
	
	
}
