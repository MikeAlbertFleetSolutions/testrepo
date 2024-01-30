package com.mikealbert.accounting.processor.dao;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.CityZipCode;
import com.mikealbert.accounting.processor.entity.CityZipCodePK;


public interface CityZipCodeDAO extends CrudRepository<CityZipCode, CityZipCodePK>, CityZipCodeDAOCustom {
}
