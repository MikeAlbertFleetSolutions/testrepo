package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.OfferHistory;

public interface OfferHistoryDAO extends CrudRepository<OfferHistory, Long> {

	public List<OfferHistory> findByDrqDrqIdAndAcceptInd(Long drqDrqId, String acceptInd);
}
