package com.mikealbert.accounting.processor.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.dao.XRefDAO;
import com.mikealbert.accounting.processor.entity.XRef;
import com.mikealbert.constant.accounting.enumeration.XRefGroupNameEnum;


@Service("xRefService")
public class XRefServiceImpl extends BaseService implements XRefService {
	@Resource XRefDAO xrefDAO;

	@Cacheable(value = "getExternalValue_cache")
	@Override
	public String getExternalValue(XRefGroupNameEnum groupName, String internalValue) throws Exception{
		List<XRef> xRefs = xrefDAO.findByGroupNameAndInternalValue(groupName.getName(), internalValue);

		if(xRefs == null || xRefs.isEmpty()) {
			throw new Exception(String.format("External value does not exist for %s:%s", groupName, internalValue));			
		}
		if(xRefs.size() != 1) {
			throw new Exception(String.format("Too many external values exists for %s:%s", groupName, internalValue));
		}
		
		return xRefs.get(0).getxRefPK().getExternalValue();
	}

	@Cacheable(value = "getInternalValue_cache")
	@Override
	public String getInternalValue(XRefGroupNameEnum groupName, String externalValue) throws Exception {
		List<XRef> xRefs = xrefDAO.findByGroupNameAndExternalValue(groupName.getName(), externalValue);

		if(xRefs == null || xRefs.isEmpty()) {
			throw new Exception(String.format("XRef does not exist for %s:%s ", groupName.getName(), externalValue));			
		}
		if(xRefs.size() != 1) {
			throw new Exception(String.format("Too many internal values exists for %s:%s ", groupName.getName(), externalValue));
		}
		
		return xRefs.get(0).getxRefPK().getInternalValue();
	}
	
	@Cacheable(value = "getByGroupNameAndExternalValue_cache")	
	@Override
	public List<XRef> getByGroupNameAndExternalValue(XRefGroupNameEnum groupName, String externalValue) {
		return Collections.unmodifiableList(xrefDAO.findByGroupNameAndExternalValue(groupName.getName(), externalValue));
	}

	@Cacheable(value = "getByGroupName_cache")
	@Override
	public List<XRef> getByGroupName(XRefGroupNameEnum groupName) {
		return Collections.unmodifiableList(xrefDAO.findByGroupName(groupName.getName()));
	}
	
	@Override 
	@Transactional
	public XRef createXRef(XRef xRef) {
		return xrefDAO.save(xRef);
	}

	@Override 
	@Transactional
	public void deleteXRef(XRef xRef) {
		xrefDAO.delete(xRef);
	}

}