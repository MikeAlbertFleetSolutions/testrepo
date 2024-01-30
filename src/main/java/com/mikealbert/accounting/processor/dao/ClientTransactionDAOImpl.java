package com.mikealbert.accounting.processor.dao;

import java.sql.Blob;
import java.text.SimpleDateFormat;

import javax.persistence.ParameterMode;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikealbert.accounting.processor.entity.Doc;
import com.mikealbert.accounting.processor.vo.ClientCreditMemoVO;
import com.mikealbert.accounting.processor.vo.ClientInvoiceDepositVO;
import com.mikealbert.accounting.processor.vo.ClientTransactionGroupVO;
import com.mikealbert.accounting.processor.vo.DisposalInvoiceVO;
import com.mikealbert.util.data.DateUtil;

public class ClientTransactionDAOImpl extends GenericDAOImpl<Doc, Long> implements ClientTransactionDAOCustom {
    private final Logger LOG = LogManager.getLogger(this.getClass());	
    
    @Override
    public void processCreditMemo(ClientCreditMemoVO clientCreditMemoVO) throws Exception {
        
        LOG.info("PRE processCreditMemo {} ", clientCreditMemoVO);

        String jsonClientPaymentVO = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME))
                .writeValueAsString(clientCreditMemoVO);

        entityManager.createStoredProcedureQuery("transaction_api_wrapper.process_client_credit_memo")
                .registerStoredProcedureParameter(1, Blob.class, ParameterMode.IN)
                .setParameter(1, new SerialBlob(jsonClientPaymentVO.getBytes("UTF-8")))
                .execute();

        LOG.info("POST processCreditMemo {} ", clientCreditMemoVO);                
    }

    @Override
    public void processTransactionGroup(ClientTransactionGroupVO clientTransactionGroupVO) throws Exception {
        LOG.info("PRE processTransactionGroup {} ", clientTransactionGroupVO);

        String jsonClientInvoiceVO = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME))
                .writeValueAsString(clientTransactionGroupVO);

        entityManager.createStoredProcedureQuery("transaction_api_wrapper.process_invoice_group")
                .registerStoredProcedureParameter(1, Blob.class, ParameterMode.IN)
                .setParameter(1, new SerialBlob(jsonClientInvoiceVO.getBytes("UTF-8")))
                .execute();                
                
        LOG.info("POST processTransactionGroup {} ", clientTransactionGroupVO);                
    }

    @Override
    public void processTransactionGroupComplete() throws Exception {
        LOG.info("PRE processTransactionGroupComplete");

        // String jsonClientInvoiceVO = new ObjectMapper()
        //         .setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME))
        //         .writeValueAsString(new HashMap<String, Object>());

        // entityManager.createStoredProcedureQuery("transaction_api_wrapper.process_invoice_group_complete")
        //         .registerStoredProcedureParameter(1, Blob.class, ParameterMode.IN)
        //         .setParameter(1, new SerialBlob(jsonClientInvoiceVO.getBytes("UTF-8")))
        //         .execute();                
                
        LOG.info("Temporarily commented out code to call transaction_api_wrapper.process_invoice_group_complete");

        LOG.info("POST processTransactionGroupComplete");
    }    

    @Override
    public void processInvoiceDeposit(ClientInvoiceDepositVO clientInvoiceDepositVO) throws Exception {
        LOG.info("PRE processInvoiceDeposit {} ", clientInvoiceDepositVO);

        String jsonClientInvoiceDepositVO = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME))
                .writeValueAsString(clientInvoiceDepositVO);
                
        entityManager.createStoredProcedureQuery("transaction_api_wrapper.process_client_deposit")
                .registerStoredProcedureParameter(1, Blob.class, ParameterMode.IN)
                .setParameter(1, new SerialBlob(jsonClientInvoiceDepositVO.getBytes("UTF-8")))
                .execute();                
                
        LOG.info("POST processInvoiceDeposit {} ", clientInvoiceDepositVO);
    }

    @Override
    public void processDisposalInvoiceInfo(DisposalInvoiceVO disposalInvoiceVO) throws Exception {
        LOG.info("PRE processDisposalInvoiceInfo {} ", disposalInvoiceVO);

        String jsonDisposalInvoiceVO = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(DateUtil.PATTERN_DATE_TIME))
                .writeValueAsString(disposalInvoiceVO);
        
        entityManager.createStoredProcedureQuery("transaction_api_wrapper.process_disposal_invoice")
                .registerStoredProcedureParameter(1, Blob.class, ParameterMode.IN)
                .setParameter(1, new SerialBlob(jsonDisposalInvoiceVO.getBytes("UTF-8")))
                .execute();  
                
        LOG.info("POST processDisposalInvoiceInfo {} ", disposalInvoiceVO);                
    }

}
