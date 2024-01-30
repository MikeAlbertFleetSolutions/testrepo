package com.mikealbert.accounting.processor.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;

public interface GenericDAO<T, ID> extends Serializable {

	T loadById(ID id);

	void persist(T entity) throws Exception;

	void update(T entity) throws Exception;

	List<T> loadAll();

	T getReference(ID id);

	public void setEntityManager(EntityManager entityManager);
	
	public PersistenceUnitUtil getPersistenceUnitUtil();

}
