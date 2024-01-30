package com.mikealbert.accounting.processor.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.UnitRegistration;

public interface UnitRegistrationDAO extends CrudRepository<UnitRegistration, Long> {

    public List<UnitRegistration> findByFmsFmsId(Long fmsId);
}
