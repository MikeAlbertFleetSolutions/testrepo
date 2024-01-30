package com.mikealbert.accounting.processor.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.mikealbert.accounting.processor.entity.QuotationModel;
import com.mikealbert.constant.enumeration.QuoteModelPropertyEnum;

public class QuotationModelDAOImpl extends GenericDAOImpl<QuotationModel, Long> implements QuotationModelDAOCustom {	
	
	@Override
	public String fetchQuotationModelPropertyValueByFmsId(Long fmsId, QuoteModelPropertyEnum property) {
		String propValue;

		StringBuilder stmt = new StringBuilder("")
				.append(" SELECT qmpv.property_value ")
				.append("   FROM quote_model_property_values qmpv ")
				.append("     JOIN quote_model_properties qmp ON qmp.qmp_id = qmpv.qmp_qmp_id")
				.append("     JOIN quotation_models qmd ON qmd.qmd_id = qmpv.qmd_qmd_id ")
				.append("     JOIN contract_lines cln ON (cln.qmd_qmd_id = qmd.qmd_id) ")
				.append("     JOIN fleet_masters fms ON (fms.fms_id = cln.fms_fms_id) ")
				.append("   WHERE fms.fms_id = :fmsId ")
				.append("     AND qmp.name = :propertyName")
				.append("     AND cln.cln_id = (SELECT max(cln1.cln_id) FROM contract_lines cln1 ")
				.append("     WHERE cln1.fms_fms_id = fms.fms_id) ");
		
		Query query = entityManager.createNativeQuery(stmt.toString())
				.setParameter("fmsId", fmsId)
				.setParameter("propertyName", property.name());
		
		try { 
			propValue = ((String)query.getSingleResult());
		} catch(NoResultException nre) {
			propValue = null;
		}
		
		return propValue;
	}	

}
