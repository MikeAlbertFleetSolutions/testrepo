package com.mikealbert.accounting.processor.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.entity.FleetMaster;
import com.mikealbert.accounting.processor.entity.Quotation;
import com.mikealbert.accounting.processor.enumeration.LeaseAutoRenewal;
import com.mikealbert.accounting.processor.vo.AmendmentLeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseAccountingScheduleVO;
import com.mikealbert.accounting.processor.vo.LeaseVO;
import com.mikealbert.constant.enumeration.ProductTypeEnum;

public class QuotationDAOImpl extends GenericDAOImpl<Quotation, Long> implements QuotationDAOCustom{

	private static final long serialVersionUID = 6848713726559792072L;
	
	@Resource transient FleetMasterDAO fleetMasterDAO;

	@SuppressWarnings("unchecked")
	@Override
	public LeaseVO findLease(String qmdId) {
		LeaseVO leaseVO = null;
		StringBuilder stmt = new StringBuilder();
		List<Object[]> records;
		
		stmt.append("SELECT quo.quo_id || '-' || qmd.revision_no, to_char(quo.quo_id), prd.product_code, prd.product_type, qpr.itc_interest_type, ");
		stmt.append("     cln.start_date, cln.end_date, cln.in_serv_date, qmd.contract_period, ");
		stmt.append("     qmd.capital_contribution, qmd.amendment_eff_date, ");
		stmt.append("     DECODE(qpr.variable_rate, 'F', 'FALSE', 'TRUE'), ");
		stmt.append("      fms.unit_no, quo.account_code, qmd.interest_rate, qmd.cln_cln_id, qmd.depreciation_factor, ");
		stmt.append("      (SELECT sum(qel.residual_value) ");	
		stmt.append("         FROM quotation_elements qel ");
		stmt.append("         JOIN lease_elements lel ON (qel.lel_lel_id = lel.lel_id) ");
		stmt.append("        WHERE qel.qmd_qmd_id = qmd.qmd_id");	
		stmt.append("          AND lel.element_type = 'FINANCE' ) AS RESIDUAL_VALUE_ESTIMATE, ");	
		stmt.append("      NVL((SELECT qm_dep.\"VALUE\" FROM quote_model_deposits qm_dep ");
		stmt.append("        WHERE qm_dep.qmd_qmd_id = qmd.qmd_id), 0) AS REFUNDABLE_DEPOSIT_AMOUNT, ");
		stmt.append("      fms.fms_id  ");
		stmt.append("   FROM products prd, quotation_profiles qpr, quotations quo, quotation_models qmd, fleet_masters fms, contract_lines cln ");
		stmt.append("   WHERE quo.quo_id = qmd.quo_quo_id ");
		stmt.append("     AND qmd.qmd_id = :qmdId ");
		stmt.append("     AND qpr.qpr_id = quo.qpr_qpr_id ");
		stmt.append("     AND prd.product_code = qpr.prd_product_code ");
		stmt.append("     AND prd.product_code not in ('INV_EXCLUS', 'INV_FLEX', 'ST', 'DEMO', 'MAX', 'PUR_COSTPL', 'PUR_INVADJ') ");		
		stmt.append("     AND prd.product_type in ('OE', 'CE') ");
		stmt.append("     AND cln.qmd_qmd_id = qmd.qmd_id");
		stmt.append("     AND cln.fms_fms_id = fms.fms_id");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("qmdId", qmdId);
		LeaseAutoRenewal autoRenewal;
		
		records =  (List<Object[]>) query.getResultList();
		
		if(records.size() > 0) {
			leaseVO = new LeaseVO();
			for(Object[] record : records) {
				int i = 0;			
				leaseVO.setExternalId((String)record[i]);
				leaseVO.setName(leaseVO.getExternalId());
				leaseVO.setQuoId(Long.valueOf(((String)record[i+=1])));
			    leaseVO.setProductCode((String)record[i+=1]);
				leaseVO.setInternalProductType((String)record[i+=1]);
			    leaseVO.setExternalProductType(leaseVO.getInternalProductType());
			    leaseVO.setInterestType((String)record[i+=1]);			    
			    leaseVO.setCommencementDate((Date)record[i+=1]);
			    leaseVO.setEndDate((Date)record[i+=1]);
			    leaseVO.setInServiceDate((Date)record[i+=1]);
			    leaseVO.setTerm((BigDecimal)record[i+=1]);
			    leaseVO.setCapitalContribution((BigDecimal)record[i+=1]);
			    leaseVO.setEffectiveDate((Date)record[i+=1]);
			    leaseVO.setVariablePayment((String)record[i+=1]);
			    leaseVO.setUnitNo((String)record[i+=1]);
				leaseVO.setClientExternalId("1C"+(String)record[i+=1]);
				leaseVO.setInterestRate(((BigDecimal)record[i+=1]).doubleValue());
				leaseVO.setParentClnId(record[i+=1] != null ? Long.valueOf(String.valueOf(record[i])) : null);
				leaseVO.setDepreciationFactor(record[i+=1] != null ? ((BigDecimal)record[i]).doubleValue() : null);
			    leaseVO.setResidualValueEstimate((BigDecimal)record[i+=1]);
			    leaseVO.setDepositAmount((BigDecimal)record[i+=1]);
			    leaseVO.setFmsId(Long.valueOf(String.valueOf(record[i+=1])));
			    
			    autoRenewal = LeaseAutoRenewal.getLeaseAutoRenewalInstance(leaseVO.getExternalProductType());
			    leaseVO.setAutoRenewal(autoRenewal.isRenewalFlag());
			    leaseVO.setAutoRenewalTerm(autoRenewal.getTerm());
			}			
		}

		return leaseVO;
	}
	
	@Override
	public Boolean isPriorAmendmentExist(String qmdId, Long quoId) {
		Boolean amendmentExist = Boolean.FALSE;
		StringBuilder stmt = new StringBuilder();
		Integer result = 0;
		
		stmt.append("SELECT 1 FROM quotation_models qmd ");
		stmt.append("  JOIN contract_lines cln ON (cln.qmd_qmd_id = qmd.qmd_id) ");
		stmt.append("  JOIN contracts con ON (cln.con_con_id = con.con_id ) ");
		stmt.append(" WHERE qmd.quo_quo_id = :quoId ");
		stmt.append("   AND qmd.qmd_id < :qmdId ");
		stmt.append("   AND quotation_report_data.get_contract_source(qmd.qmd_id, con.contract_no, cln.rev_no) = 'A' ");
		stmt.append("   AND accounting_integration.isPrevFinanceAmendExist(qmd_qmd_id) = '1' "); //TODO this package needs to be wrapped in acct_app; do not call willow packages directly from this microservice
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("quoId", quoId);
		query.setParameter("qmdId", qmdId);
		
		try {
			result = ((BigDecimal)query.getSingleResult()).intValue();
			if(result == 1) {
				//Single prior amendment found
				amendmentExist = Boolean.TRUE;
			}
		}
		catch(NoResultException ex) {
			//No prior Amendment found
			amendmentExist = Boolean.FALSE;
		}
		catch(NonUniqueResultException ex) {
			//Multiple prior amendments found
			amendmentExist = Boolean.TRUE;
		}
		
		return amendmentExist;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public LeaseVO findLeaseAccountingSchedule(LeaseVO leaseVO, String qmdId) {
		StringBuilder stmt = new StringBuilder();
		List<Object[]> records;
		BigDecimal prevAmount = BigDecimal.ZERO;
		
		stmt.append("SELECT qsc.trans_date, sum(qsc.amount) ");
		stmt.append("  FROM quotation_schedules qsc  ");
		stmt.append("  JOIN quotation_elements qel on (qsc.qel_qel_id = qel.qel_id) ");
		stmt.append("  JOIN lease_elements lel on (qel.lel_lel_id = lel.lel_id) ");
		stmt.append("  JOIN quotation_models qmd ON (qel.qmd_qmd_id = qmd.qmd_id) ");
		stmt.append(" WHERE qel.qmd_qmd_id = :qmdId");
		stmt.append("   AND lel.element_type = 'FINANCE' ");
		stmt.append("   AND qsc.payment_ind != 'L' ");
		stmt.append("   AND TRUNC(NVL(qmd.amendment_eff_date, TO_DATE('01/01/2000', 'MM/DD/YYYY'))) <= TRUNC(qsc.trans_date) ");
		
		//For Closed-End Units, we do not want to include last schedule
		if(ProductTypeEnum.CE.toString().equalsIgnoreCase(leaseVO.getExternalProductType())) {
			stmt.append(" AND qsc.trans_date < (SELECT MAX(trans_date) ");
			stmt.append("                     FROM quotation_schedules qsc2 ");
			stmt.append("                     JOIN quotation_elements qel2 on (qsc2.qel_qel_id = qel2.qel_id) ");
			stmt.append("                    WHERE qel2.qmd_qmd_id = :qmdId) ");
		}
		
		stmt.append(" GROUP BY trans_date ");
		stmt.append(" ORDER BY trans_date");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("qmdId", qmdId);
		
		records =  (List<Object[]>) query.getResultList();
		
		if(records.size() > 0) {
			for(Object[] record : records) {
				LeaseAccountingScheduleVO leaseAccountingSchedule = new LeaseAccountingScheduleVO();
				int i = 0;
				
				leaseAccountingSchedule.setTransDate((Date)record[i]);
				leaseAccountingSchedule.setAmount((BigDecimal)record[i+=1]);
				
				if(!leaseAccountingSchedule.getAmount().equals(prevAmount)) {
					leaseVO.getLeaseAccountingSchedule().add(leaseAccountingSchedule);
					prevAmount = leaseAccountingSchedule.getAmount();
				}
			}			
		}

		return leaseVO;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Long> findQuoIdsByClnIds(List<Long> clnIds) {	
		StringBuilder stmt = new StringBuilder();		
		stmt.append("SELECT DISTINCT qmd.quo_quo_id ");
		stmt.append("  FROM quotation_models qmd ");
		stmt.append("  JOIN contract_lines cln ON cln.qmd_qmd_id = qmd.qmd_id AND cln.cln_id IN :clnIds");

		Stream<Long> result =  entityManager.createNativeQuery(stmt.toString())
				.setParameter("clnIds", clnIds)
				.getResultStream();
				
		List<Long> ids = result.collect(Collectors.toList());
			
		return ids;
	}
	
	/*
	 * This method returns ExternalId of the first QmdId which was put on Contract for this Quotation
	 */
	@Override
	public String findParentExternalIdByQmdId(String qmdId) {
		String parentExternalId = "";
		StringBuilder stmt = new StringBuilder();
		
		stmt.append("SELECT qmd.quo_quo_id||'-'||qmd.revision_no ");
		stmt.append("  FROM quotation_models qmd ");
		stmt.append(" WHERE qmd_id = (SELECT MIN(cln2.qmd_qmd_id) ");
		stmt.append("                       FROM contract_lines cln1 ");
		stmt.append("                       JOIN contract_lines cln2 ON (cln1.con_con_id = cln2.con_con_id) ");
		stmt.append("                      WHERE cln1.qmd_qmd_id = :qmdId )");
				
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("qmdId", qmdId);
		
		parentExternalId = (String)query.getSingleResult();		
		return parentExternalId;
	}

	/*
	 * This method returns ExternalId of the first contract line which was put on Contract
	 */
	@Override
	public String findParentExternalIdByClnId(Long clnId) {
		String parentExternalId = "";
		StringBuilder stmt = new StringBuilder()
				.append("SELECT qmd.quo_quo_id||'-'||qmd.revision_no ")
				.append("  FROM quotation_models qmd ")
				.append(" WHERE qmd_id = (SELECT MIN(cln2.qmd_qmd_id) ")
				.append("                   FROM contract_lines cln1 ")
				.append("                   JOIN contract_lines cln2 ON (cln1.con_con_id = cln2.con_con_id) ")
				.append("                   WHERE cln1.cln_id = :clnId )");
				
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("clnId", clnId);
		
		parentExternalId = (String)query.getSingleResult();		
		return parentExternalId;
	}		
	
	/*
	 * This method returns the QmdId which was on contract just before the passed in QmdId
	 */
	@Override
	public String findPreviousQmdId(String qmdId) {
		String prevQmdId = "";
		StringBuilder stmt = new StringBuilder();
		
		stmt.append("SELECT max(cln.qmd_qmd_id) ");
		stmt.append("  FROM contract_lines cln ");
		stmt.append(" WHERE con_con_id = (SELECT MAX(cln1.con_con_id) FROM contract_lines cln1 ");
		stmt.append("                      WHERE cln1.qmd_qmd_id = :qmdId )");
		stmt.append("   AND cln.qmd_qmd_id < :qmdId ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("qmdId", qmdId);
		
		prevQmdId =  ((Object)query.getSingleResult()).toString();		
		return prevQmdId;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AmendmentLeaseAccountingScheduleVO> getNonRechargeAmendLeaseAccountingSchedule(String qmdId, String previousQmdId) {
		StringBuilder stmt = new StringBuilder();
		List<Object[]> records;
		List<AmendmentLeaseAccountingScheduleVO> amendmentLeaseAcctScheduleVOs = new ArrayList<>();
		
		stmt.append("WITH updated_qel as ( ");
		stmt.append("  select qel.rental, qda.dac_dac_id ");
		stmt.append("    from quotation_elements qel ");
		stmt.append("    join quotation_dealer_accessories qda ON (qel.qda_qda_id = qda.qda_id) ");
		stmt.append("   where qel.qmd_qmd_id = :qmdId ");
		stmt.append("     and qel.qda_qda_id is not null ");
		stmt.append("     and qda.driver_recharge_yn = 'N'");
		stmt.append(" minus ");
		stmt.append(" select qel.rental, qda.dac_dac_id ");
		stmt.append("   from quotation_elements qel ");
		stmt.append("   join quotation_dealer_accessories qda ON (qel.qda_qda_id = qda.qda_id) ");
		stmt.append("  where qel.qmd_qmd_id = :previousQmdId ");
		stmt.append("    and qel.qda_qda_id is not null ");
		stmt.append("    and qda.driver_recharge_yn = 'N')");
		stmt.append(" select qel.qel_id, qel.residual_value, ");
		stmt.append("        qda.total_price, qda.driver_recharge_yn,  ");
		stmt.append("        qsc.amount, qsc.trans_date");
		stmt.append("   from quotation_elements qel ");
		stmt.append("   join Updated_qel uq ON (qel.RENTAL = uq.rental) ");
		stmt.append("   join quotation_dealer_accessories qda ON (qel.qda_qda_id = qda.qda_id and uq.dac_dac_id = qda.dac_dac_id) ");
		stmt.append("   join quotation_schedules qsc ON (qsc.qel_qel_id = qel.qel_id) ");
		stmt.append("  where qel.qmd_qmd_id = :qmdId");
		stmt.append("    and qsc.trans_date = (select min(qsc1.trans_date) from quotation_schedules qsc1 ");
		stmt.append("                           where qsc1.qel_qel_id = qel.qel_id ");
		stmt.append("                             and no_of_units > 0) ");
		stmt.append("    and no_of_units > 0 ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("qmdId", qmdId);
		query.setParameter("previousQmdId", previousQmdId);
		
		records =  (List<Object[]>) query.getResultList();
		
		if(records.size() > 0) {
			for(Object[] record : records) {
				int i = 0;
				AmendmentLeaseAccountingScheduleVO amendmentLeaseAcctScheduleVO = new AmendmentLeaseAccountingScheduleVO();
				amendmentLeaseAcctScheduleVO.setQelId((BigDecimal)record[i]);
				amendmentLeaseAcctScheduleVO.setResidual((BigDecimal)record[i+=1]);
				amendmentLeaseAcctScheduleVO.setRechargeAmount((BigDecimal)record[i+=1]);
				amendmentLeaseAcctScheduleVO.setRechargeInd((String)record[i+=1]);
				amendmentLeaseAcctScheduleVO.setMonthlyLeaseAmount((BigDecimal)record[i+=1]);
				amendmentLeaseAcctScheduleVO.setTransDate((Date)record[i+=1]);
				
				amendmentLeaseAcctScheduleVOs.add(amendmentLeaseAcctScheduleVO);
			}
		}
		
		return amendmentLeaseAcctScheduleVOs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AmendmentLeaseAccountingScheduleVO> getRechargeAmendLeaseAccountingSchedule(String qmdId, String previousQmdId) {
		StringBuilder stmt = new StringBuilder();
		List<Object[]> records;
		List<AmendmentLeaseAccountingScheduleVO> amendmentLeaseAcctScheduleVOs = new ArrayList<>();
		
		stmt.append("WITH updated_qda as ( ");
		stmt.append("  select qda.dac_dac_id, qda.total_price ");
		stmt.append("    from quotation_dealer_accessories qda ");
		stmt.append("   where qda.qmd_qmd_id = :qmdId ");
		stmt.append("     and qda.driver_recharge_yn = 'Y'");
		stmt.append(" minus ");
		stmt.append(" select qda.dac_dac_id, qda.total_price ");
		stmt.append("   from quotation_dealer_accessories qda ");
		stmt.append("  where qda.qmd_qmd_id = :previousQmdId ");
		stmt.append("    and qda.driver_recharge_yn = 'Y')");
		stmt.append(" select qda.residual_amt, ");
		stmt.append("        qda.recharge_amount, qda.driver_recharge_yn  ");
		stmt.append("   from quotation_dealer_accessories qda ");
		stmt.append("   join Updated_qda uqda ON (qda.dac_dac_id = uqda.dac_dac_id AND qda.total_price = uqda.total_price) ");
		stmt.append("  where qda.qmd_qmd_id = :qmdId");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("qmdId", qmdId);
		query.setParameter("previousQmdId", previousQmdId);
		
		records =  (List<Object[]>) query.getResultList();
		
		if(records.size() > 0) {
			for(Object[] record : records) {
				int i = 0;
				AmendmentLeaseAccountingScheduleVO amendmentLeaseAcctScheduleVO = new AmendmentLeaseAccountingScheduleVO();
				amendmentLeaseAcctScheduleVO.setResidual((BigDecimal)record[i]);
				amendmentLeaseAcctScheduleVO.setRechargeAmount((BigDecimal)record[i+=1]);
				amendmentLeaseAcctScheduleVO.setRechargeInd((String)record[i+=1]);
				
				amendmentLeaseAcctScheduleVOs.add(amendmentLeaseAcctScheduleVO);
			}
		}
		
		return amendmentLeaseAcctScheduleVOs;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LeaseAccountingScheduleVO> getRemainingStepSchedule(String qmdId, List<BigDecimal> qels, BigDecimal totalMonthlyLeaseAmount) {
		StringBuilder stmt = new StringBuilder();
		List<Object[]> records;
		List<LeaseAccountingScheduleVO> leaseAccountingScheduleVOs = new ArrayList<>();
		BigDecimal prevAmount = totalMonthlyLeaseAmount; //Initializing Previous amount with totalMonthlyAmount
		
		StringBuilder qelList = new StringBuilder("(");
		for(BigDecimal qel : qels) {
			qelList.append(qel.toString()).append(", ");
		}
		//Remove last comma ,
		qelList.deleteCharAt(qelList.length()-2);
		qelList.append(")");
		
		stmt.append("SELECT trans_date, sum(amount)");
		stmt.append("  FROM quotation_schedules qsc ");
		stmt.append(" WHERE qsc.payment_ind <> 'L' ");
		stmt.append("   AND qsc.qel_qel_id IN ");
		stmt.append(qelList);
		stmt.append("   AND qsc.trans_date > (SELECT min(qsc1.trans_date) ");
		stmt.append("                           FROM quotation_schedules qsc1 ");
		stmt.append("                          WHERE qsc1.qel_qel_id IN ");
		stmt.append(qelList);
		stmt.append("                         ) ");
		stmt.append(" GROUP BY qsc.trans_date ");
		stmt.append(" ORDER BY qsc.trans_date ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		
		records = (List<Object[]>) query.getResultList();
		
		for(Object[] record : records) {
			int i = 0;
			LeaseAccountingScheduleVO leaseAccountingScheduleVO = new LeaseAccountingScheduleVO();
			leaseAccountingScheduleVO.setTransDate((Date)record[i]);
			leaseAccountingScheduleVO.setAmount((BigDecimal) record[i+=1]);
			if(!prevAmount.equals(leaseAccountingScheduleVO.getAmount())) {
				leaseAccountingScheduleVOs.add(leaseAccountingScheduleVO);
				prevAmount = leaseAccountingScheduleVO.getAmount();
			}
		}
		
		return leaseAccountingScheduleVOs;
	}
	
	/*@Override
	public BigDecimal findLeaseAssetFairValueOriginal(Long fmsId, String qmdId, String productType) {
		BigDecimal fairValue;
		StringBuilder stmt = new StringBuilder();
		
		stmt.append("WITH AI_TOTAL AS( ");
		stmt.append("  SELECT decode(:productType, 'OE',  ai.current_value_tax, 'CE', ai.current_value_book) as CURRENT_VALUE ");
		stmt.append("    FROM asset_item ai ");
		stmt.append("    JOIN quotation_dealer_accessories qda ON ((qda.dac_dac_id = ai.dac_dac_id)) ");
		stmt.append("   WHERE ai.fleet_id = :fmsId");
		stmt.append("     AND qda.qmd_qmd_id = :qmdId");
		stmt.append("   UNION  ");
		stmt.append("  SELECT decode(:productType, 'OE',  ai.current_value_tax, 'CE', ai.current_value_book) as CURRENT_VALUE ");
		stmt.append("    FROM asset_item ai ");
		stmt.append("   WHERE ai.fleet_id = :fmsId ");
		stmt.append("     AND ai.dac_dac_id IS NULL) ");
		stmt.append(" SELECT sum(current_value) FROM AI_TOTAL");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
		query.setParameter("qmdId", qmdId);
		query.setParameter("productType", productType);
		
		fairValue = (BigDecimal)query.getSingleResult();
		
		return fairValue;
		
	}*/

	@Override
	public BigDecimal findLeaseAssetFairValueRevision(Long fmsId, String qmdId, String productType) {
		BigDecimal fairValue;
		StringBuilder stmt = new StringBuilder();
		
		stmt.append("WITH AI_TOTAL AS( ");
		stmt.append("  SELECT decode(:productType, 'OE',  ai.current_value_tax, 'CE', ai.current_value_book) as CURRENT_VALUE ");
		stmt.append("    FROM asset_item ai ");
		stmt.append("    JOIN quotation_dealer_accessories qda ON ((qda.dac_dac_id = ai.dac_dac_id)) ");
		stmt.append("   WHERE ai.fleet_id = :fmsId");
		stmt.append("     AND qda.qmd_qmd_id = :qmdId");
		stmt.append("   UNION ALL ");
		stmt.append("  SELECT decode(:productType, 'OE',  ai.current_value_tax, 'CE', ai.current_value_book) as CURRENT_VALUE ");
		stmt.append("    FROM asset_item ai ");
		stmt.append("   WHERE ai.fleet_id = :fmsId ");
		stmt.append("     AND ai.dac_dac_id IS NULL) ");
		stmt.append(" SELECT sum(current_value) FROM AI_TOTAL");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
		query.setParameter("qmdId", qmdId);
		query.setParameter("productType", productType);
		
		fairValue = (BigDecimal)query.getSingleResult();
		
		return fairValue;
		
	}

/*	@Override
	public BigDecimal findLeaseAssetFairValueAmendment(Long fmsId, String qmdId, String previousQmdId, String productType) {
		BigDecimal fairValue;
		StringBuilder stmt = new StringBuilder();
				
		stmt.append("WITH updated_qel AS ( ");
		stmt.append("  SELECT qda.dac_dac_id ");
		stmt.append("    from quotation_dealer_accessories qda ");
		stmt.append("   where qda.qmd_qmd_id = :qmdId ");
		stmt.append("   MINUS  ");
		stmt.append("  SELECT qda.dac_dac_id ");
		stmt.append("    from quotation_dealer_accessories qda ");
		stmt.append("   where qda.qmd_qmd_id = :previousQmdId) ");
		stmt.append(" SELECT sum(decode(:productType, 'OE',  ai.current_value_tax, 'CE', ai.current_value_book)) ");
		stmt.append("   FROM asset_item ai ");
		stmt.append("   JOIN updated_qel uq on (uq.dac_dac_id = ai.dac_dac_id) ");
		stmt.append("  WHERE ai.fleet_id = :fmsId");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
		query.setParameter("qmdId", qmdId);
		query.setParameter("previousQmdId", previousQmdId);
		query.setParameter("productType", productType);
		
		
		fairValue = (BigDecimal)query.getSingleResult();
		
		return fairValue;
		
	}*/

	@Override
	public BigDecimal getCustomerCapCost(Long qmdId) {
		BigDecimal capCost = BigDecimal.ZERO;
		StringBuilder stmt = new StringBuilder();
		
		stmt.append(" SELECT SUM(qel.capital_cost) ");
  		stmt.append("   FROM quotation_elements qel ");
  		stmt.append("   JOIN lease_elements lel ON (qel.lel_lel_id = lel.lel_id) ");
  		stmt.append("  WHERE qel.qmd_qmd_id = :qmdId ");
  		stmt.append("    AND lel.element_type = 'FINANCE' ");
		
  		Query query = entityManager.createNativeQuery(stmt.toString());
  		query.setParameter("qmdId", qmdId);
  		
  		capCost = (BigDecimal)query.getSingleResult();
  		
  		return (capCost == null ? BigDecimal.ZERO : capCost);
	}
	
	public BigDecimal getCustomerCapCostAmendment(Long qmdId, Long previousQmdId) {
			BigDecimal capCost = BigDecimal.ZERO;
			StringBuilder stmt = new StringBuilder();
			
			stmt.append("WITH updated_qel AS ( ");   
			stmt.append(" SELECT qda.dac_dac_id ");	    
			stmt.append("   FROM quotation_dealer_accessories qda ");	    
			stmt.append("  WHERE qda.qmd_qmd_id = :qmdId ");	    
			stmt.append("    AND qda.driver_recharge_yn = 'N' ");	    
			stmt.append(" MINUS ");
			stmt.append(" SELECT qda.dac_dac_id ");	    
			stmt.append("   FROM quotation_dealer_accessories qda ");
			stmt.append("  WHERE qda.qmd_qmd_id = :previousQmdId ");
			stmt.append("    AND qda.driver_recharge_yn = 'N') ");
			stmt.append("  SELECT SUM(qel.capital_cost) ");
			stmt.append("    FROM Updated_qel uq");
			stmt.append("    JOIN quotation_dealer_accessories qda ON (uq.dac_dac_id = qda.dac_dac_id) ");
			stmt.append("    JOIN quotation_elements qel ON (qel.qda_qda_id = qda.qda_id) ");
			stmt.append("   WHERE qel.qmd_qmd_id = :qmdId ");
						
	  		Query query = entityManager.createNativeQuery(stmt.toString());
	  		query.setParameter("qmdId", qmdId);
	  		query.setParameter("previousQmdId", previousQmdId);
	  		
	  		capCost = (BigDecimal)query.getSingleResult();
	  		
	  		return (capCost == null ? BigDecimal.ZERO : capCost);
		}
	
	public Boolean isUnitOnContract(String unitNo) {
		List<FleetMaster> fmss = fleetMasterDAO.findByUnitNo(unitNo);
		
		if(fmss.size() > 0)
			return this.isUnitOnContract(fmss.get(0).getFmsId());
		else
			return Boolean.FALSE;
	}

	public Boolean isUnitOnContract(Long fmsId) {
		String temp;
		StringBuilder stmt = new StringBuilder();
		
		stmt.append("SELECT 'Y' FROM contract_lines cln ");
		stmt.append("  JOIN quotation_models qmd ON (qmd.qmd_id = cln.qmd_qmd_id) ");
		stmt.append(" WHERE cln.fms_fms_id = :fmsId ");
		stmt.append("   AND qmd.quote_status = '6' ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
		
		try {
			temp = String.valueOf(query.getSingleResult());
		} catch(NoResultException NrEx) {
			temp = String.valueOf("N");
		}
		
		return ("Y".equals(temp) ? Boolean.TRUE : Boolean.FALSE );
	}
	
	public BigDecimal getResidualByQmdId(Long qmdId) {
		BigDecimal residual;
		StringBuilder stmt = new StringBuilder();
		
		stmt.append("SELECT sum(qel.residual_value) ");	
		stmt.append("  FROM quotation_elements qel ");
		stmt.append("  JOIN lease_elements lel ON (qel.lel_lel_id = lel.lel_id) ");
		stmt.append(" WHERE qel.qmd_qmd_id = :qmdId");	
		stmt.append("   AND lel.element_type = 'FINANCE'");	
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("qmdId", qmdId);
		
		try {
			residual = (BigDecimal)query.getSingleResult();
		} catch(NoResultException NrEx) {
			residual = BigDecimal.ZERO;
		}
		
		return residual;
	}
	
	@Override
	public String getProductCodeByQuoId(Long quoId) {
		String id;
		
		StringBuilder stmt = new StringBuilder();
		stmt.append(" SELECT prd.product_code ");
		stmt.append("   FROM products prd ");
		stmt.append("     JOIN quotation_profiles qpr ON qpr.prd_product_code = prd.product_code ");
		stmt.append("     JOIN quotations quo ON quo.qpr_qpr_id = qpr.qpr_id AND quo.quo_id = :quoId ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("quoId", quoId);
		
		id = ((String)query.getSingleResult());
		
		return id;
	}	
}