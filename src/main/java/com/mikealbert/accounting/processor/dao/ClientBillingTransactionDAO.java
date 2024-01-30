package com.mikealbert.accounting.processor.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.ClientBillingTransaction;

public interface ClientBillingTransactionDAO extends CrudRepository<ClientBillingTransaction, Long>, ClientBillingTransactionDAOCustom {
   List<ClientBillingTransaction> findByAccountCodeAndAccountingPeriod(String accountCode, String accountingPeriod);
   Optional<ClientBillingTransaction> findByTranIntIdAndLineNo(String internalId, Long lineNo);   
}


