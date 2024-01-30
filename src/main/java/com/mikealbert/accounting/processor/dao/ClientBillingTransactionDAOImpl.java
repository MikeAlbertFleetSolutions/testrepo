package com.mikealbert.accounting.processor.dao;

import javax.persistence.ParameterMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mikealbert.accounting.processor.entity.Doc;

public class ClientBillingTransactionDAOImpl extends GenericDAOImpl<Doc, Long> implements ClientBillingTransactionDAOCustom {
    private final Logger LOG = LogManager.getLogger(this.getClass());	
    
    @Override
    public void mergeInternalData(String accountCode, String accountingPeriod, boolean force) throws Exception {
        
        LOG.info("PRE mergeInternalData accountCode: {}, accounting period: {}, force : {}", accountCode, accountingPeriod, force);

        entityManager.createStoredProcedureQuery("billing_report_data_wrapper.merge_willow_data")
                .registerStoredProcedureParameter(1, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(2, String.class, ParameterMode.IN)
                .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)
                .setParameter(1, accountCode)
                .setParameter(2, accountingPeriod)
                .setParameter(3, force ? "Y" : "N")
                .execute();

        LOG.info("POST mergeInternalData accountCode: {}, accounting period: {}, force : {}", accountCode, accountingPeriod, force);
    }

}
