package com.mikealbert.accounting.processor.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.entity.Driver;
import com.mikealbert.accounting.processor.exception.NoDataFoundException;
import com.mikealbert.accounting.processor.vo.DriverUnitHistoryUpsertVO;
import com.mikealbert.constant.accounting.enumeration.AccountingNounEnum;
import com.mikealbert.constant.accounting.enumeration.EventEnum;
import com.mikealbert.constant.enumeration.ProductTypeEnum;
import com.mikealbert.util.data.StringUtil;

public class DriverDAOImpl extends GenericDAOImpl<Driver, Long> implements DriverDAOCustom {
	private static final long serialVersionUID = 6538040234088488317L;

	private final Logger LOG = LogManager.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	@Override
	public  List<DriverUnitHistoryUpsertVO> getDriverUnitHistoryDetail(DriverUnitHistoryUpsertVO duh) throws Exception {
		StringBuilder stmt;
		Query query;
		List<Object[]> records;
		Long entityId = null;
		List<DriverUnitHistoryUpsertVO> drvUpserts = new ArrayList<>();

		stmt = new StringBuilder();
		stmt.append("select product_type, effective_date, drv_drv_id, unit_no, ");
		stmt.append("first_name, last_name, account_code, cost_center, cost_center_desc, ");
		stmt.append(
				"address_line_1, address_line_2, town_description, region_desc, county_desc, postcode, country_desc, ");
		stmt.append(
				"sup_address_line_1, sup_address_line_2, sup_town_description, sup_region_desc, sup_county_desc, sup_postcode, sup_country_desc,  ");
		
		stmt.append(" recharge_code, fleet_ref ");
		
		switch(duh.getNoun()) {
		case DRIVER:
			entityId = duh.getDrvId();
			stmt.append("from table(driver_integration.getDuhByDrvIdAndEffDate(:entityId, :effectiveDate))");
			break;
		case DRIVER_ALLOCATION:
			entityId = duh.getDalId();
			stmt.append("from table(driver_integration.getDuhByDalIdAndEffDate(:entityId, :effectiveDate))");
			break;
		case QUOTE:
			if(EventEnum.ACCEPT.equals(duh.getEvent())) {
				entityId = duh.getQmdId();
				stmt.append("from table(driver_integration.getDuhByQmdIdAndEffDate(:entityId, :effectiveDate))");
			}
			else if(EventEnum.DRIVER_CHANGE.equals(duh.getEvent())) {
				entityId = duh.getQuoId();
				stmt.append("from table(driver_integration.getDuhByQuoIdAndEffDate(:entityId, :effectiveDate))");
			}
			break;
		case DOC:
			entityId = duh.getDocId();
			stmt.append("from table(driver_integration.getDuhByDocIdAndEffDate(:entityId, :effectiveDate))");
			break;
		case UNIT:
			entityId = duh.getFmsId();
			stmt.append("from table(driver_integration.getDuhByFmsIdAndEffDate(:entityId, :effectiveDate))");
			break;
		default:
			//Don't expect to drop here because we have controlled which nouns we are processing
			LOG.error(String.format("Error in DriverDAOImpl.getDriverUnitHistoryDetail:: Unexpected Noun found: ", duh.toString()));
			break;
		
		}

		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("entityId", entityId);
		query.setParameter("effectiveDate", duh.getEffectiveDate());

		records = (List<Object[]>) query.getResultList();
		
		if (records == null || records.size() == 0) {
			
			boolean UPSERT_FLEET_REF_NO_CHANGE = AccountingNounEnum.UNIT.equals(duh.getNoun());
			boolean UNIT_DRIVER_CHANGE = AccountingNounEnum.QUOTE.equals(duh.getNoun());
			boolean UNIT_DOC_CHANGE = AccountingNounEnum.DOC.equals(duh.getNoun());
			boolean DRIVER_ALLOCATION_CHANGE = AccountingNounEnum.DRIVER_ALLOCATION.equals(duh.getNoun());
			boolean DRIVER_CHANGE = AccountingNounEnum.DRIVER.equals(duh.getNoun());
			
			if (UPSERT_FLEET_REF_NO_CHANGE || UNIT_DRIVER_CHANGE || UNIT_DOC_CHANGE || DRIVER_ALLOCATION_CHANGE || DRIVER_CHANGE) {
				LOG.info(String.format("No record found in database for duh record: %s", duh.toString()));
				return drvUpserts;
			} else {
				throw new NoDataFoundException(String.format("No record found in database for duh record: %s", duh.toString()));
			}
		}
		
		for (Object[] record : records) {
			int i = 0;
			DriverUnitHistoryUpsertVO drvUpsert = new DriverUnitHistoryUpsertVO();
			drvUpsert.setProductType(ProductTypeEnum.valueOf((String)record[i]));			
			/*
			 * If we are processing a driver allocation event for a MAX unit we want to initialize the effective date with the from date
			 * This is done to handle lafs1894. For MAX units when we accept the quote that is when we create the driver_allocation record and put it on contract
			 * Also we move the DUH record for the first time in NS. If the date is in the past we want to make sure we start the 
			 * effectivity of DUH table from the beginning of the contract.
			 */
			if(AccountingNounEnum.DRIVER_ALLOCATION.compareTo(duh.getNoun()) == 0 && ProductTypeEnum.MAX.equals(drvUpsert.getProductType())) {
				drvUpsert.setEffectiveDate((Date)record[i += 1]);
			}
			else {
				//For all other scenarios we take create date of accounting events table as DUH effective date 
				drvUpsert.setEffectiveDate(duh.getEffectiveDate());
				i++;
			}

			drvUpsert.setDrvId(((BigDecimal) record[i += 1]).longValue());
			drvUpsert.setUnitNo((String) record[i += 1]);
			drvUpsert.setFirstName(StringUtil.trim((String) record[i += 1]));
			drvUpsert.setLastName(StringUtil.trim((String) record[i += 1]));
			drvUpsert.setAccountCode((String) record[i += 1]);
			drvUpsert.setCostCenter(StringUtil.trim((String) record[i += 1]));
			drvUpsert.setCostCenterDesc(StringUtil.trim((String) record[i += 1]));
			drvUpsert.getDriverAddress()
					.setAddressLine1(StringUtil.trim((String) record[i += 1]))
					.setAddressLine2(StringUtil.trim((String) record[i += 1]))
					.setTownDescription(StringUtil.trim((String) record[i += 1]))
					.setRegionCode(StringUtil.trim((String) record[i += 1]))
					.setCountyCode(StringUtil.trim((String) record[i += 1]))
					.setZipCode(StringUtil.trim((String) record[i += 1]))
					.setCountryCode(StringUtil.trim((String) record[i += 1]));
			drvUpsert.getSupplierAddress()
					.setAddressLine1(StringUtil.trim((String) record[i += 1]))
					.setAddressLine2(StringUtil.trim((String) record[i += 1]))
					.setTownDescription(StringUtil.trim((String) record[i += 1]))
					.setRegionCode(StringUtil.trim((String) record[i += 1]))
					.setCountyCode(StringUtil.trim((String) record[i += 1]))
					.setZipCode(StringUtil.trim((String) record[i += 1]))
					.setCountryCode(StringUtil.trim((String) record[i += 1]));
			
			drvUpsert.setCustRecordDuhDriverRechargeCode((String) record[i += 1]);
			drvUpsert.setCustRecordDuhFleetRefNo((String) record[i += 1]);
			
			drvUpsert.setAetId(duh.getAetId());
			drvUpserts.add(drvUpsert);
		}

		return drvUpserts;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public String getCurrentGaragedState(Long fmsId) {
		String state = null;

		StringBuilder stmt = new StringBuilder()
            .append(" SELECT dra.region ")
			.append("   FROM quotations quo ")
			.append("     JOIN quotation_models qmd ON qmd.quo_quo_id = quo.quo_id ")
			.append("     JOIN driver_addresses dra ON dra.drv_drv_id = quo.drv_drv_id  ")
			.append("   WHERE qmd.quote_status IN (3, 6) ")
			.append("     AND dra.address_type = 'GARAGED' ")
			.append("     AND ((qmd.fms_fms_id = :fmsId) ")
			.append("            OR qmd.unit_no IN (SELECT unit_no ")
			.append("                                 FROM fleet_masters fms ")
			.append("                                 WHERE fms.fms_id = :fmsId) ) ")
			.append("     AND NOT EXISTS( SELECT 1 ")
			.append("                       FROM driver_allocations da ")
			.append("                       WHERE da.drv_drv_id = quo.drv_drv_id ")
			.append("                         AND da.fms_fms_id = :fmsId) ");
					

			Stream<String> result =  entityManager.createNativeQuery(stmt.toString())
					.setParameter("fmsId", fmsId)
			        .getResultStream();

	        state = result.findFirst().orElse(null);
				
			return state;
	}

}