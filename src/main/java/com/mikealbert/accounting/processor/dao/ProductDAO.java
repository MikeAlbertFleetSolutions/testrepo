package com.mikealbert.accounting.processor.dao;

import org.springframework.data.repository.CrudRepository;

import com.mikealbert.accounting.processor.entity.Product;

public interface ProductDAO extends CrudRepository<Product, Long> {
	

}
