package com.mikealbert.accounting.processor.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.ParameterMode;
import javax.persistence.Query;

import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.accounting.processor.entity.Doc;
import com.mikealbert.accounting.processor.enumeration.AssetTypeEnum;
import com.mikealbert.accounting.processor.vo.AssetCreateVO;
import com.mikealbert.accounting.processor.vo.CreditLineVO;
import com.mikealbert.accounting.processor.vo.CreditVO;
import com.mikealbert.accounting.processor.vo.InvoiceLineVO;
import com.mikealbert.accounting.processor.vo.InvoiceVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderLineVO;
import com.mikealbert.accounting.processor.vo.PurchaseOrderVO;
import com.mikealbert.constant.accounting.enumeration.ControlCodeEnum;
import com.mikealbert.constant.enumeration.ProductEnum;
import com.mikealbert.util.data.DataUtil;

public class DocDAOImpl extends GenericDAOImpl<Doc, Long> implements DocDAOCustom {

	private static final long serialVersionUID = 5089047604128704021L;
	
	@Resource transient Environment env;

	
	@Override
	public BigDecimal getUnpaidPOTotal(Long quoId) {
		BigDecimal unPaidPOTotal = BigDecimal.ZERO;
		StringBuilder stmt = new StringBuilder();
				   
		stmt.append(" SELECT sum(d_po.total_doc_price) ");
		stmt.append("   FROM doc d_po ");
		stmt.append("  WHERE d_po.doc_type = 'PORDER' ");
		stmt.append("    AND d_po.source_code IN ('FLQUOTE', 'FLORDER') ");
		stmt.append("    AND d_po.generic_ext_id IN (");
		stmt.append("                     SELECT qmd_id FROM quotation_models ");
		stmt.append("                      WHERE quo_quo_id = :quoId) ");								
		stmt.append("    AND d_po.doc_status = 'R' ");
		stmt.append("    AND NOT EXISTS (SELECT 1 FROM doc_links dl ");
		stmt.append("                      JOIN doc d_ap on (dl.child_doc_id = d_ap.doc_id) ");
		stmt.append("                     WHERE d_po.doc_id = dl.parent_doc_id ");
		stmt.append("                       AND d_ap.doc_type = 'INVOICEAP') ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("quoId", quoId);
				
		unPaidPOTotal = (BigDecimal)query.getSingleResult();
		
		return (unPaidPOTotal == null ? BigDecimal.ZERO : unPaidPOTotal);
	}

	@SuppressWarnings("unchecked")
	@Override
	public InvoiceVO getInvoiceApHeaderByDocId(Long docId) {
		StringBuilder stmt;
		Query query;
		List<Object[]> records;
		InvoiceVO invoice = null;
		
		stmt = new StringBuilder();
		stmt.append("SELECT d.doc_id, d.update_control_code, d.doc_no, nvl(ea.parent_account, ea.account_code) as account_code, d.c_id, d.doc_date, d.doc_description, ");
		stmt.append("    eaa.eaa_id, d.total_doc_disc, d.op_code, ");		
		stmt.append("    (SELECT tp2.year ");
		stmt.append("       FROM time_periods tp2 ");
		stmt.append("       WHERE tp2.c_id = d.c_id ");
		stmt.append("         AND trunc(tp2.start_date) <= trunc(ai.disposal_date) ");
		stmt.append("         AND trunc(tp2.end_date) >= trunc(ai.disposal_date)) AS derived_disposal_fiscal_year, ");
		stmt.append("    (SELECT tp2.year ");
		stmt.append("       FROM time_periods tp2 ");
		stmt.append("       WHERE tp2.c_id = d.c_id ");
		stmt.append("         AND trunc(tp2.start_date) <= trunc(sysdate) ");
		stmt.append("         AND trunc(tp2.end_date) >= trunc(sysdate)) AS derived_current_fiscal_year");
		stmt.append("  FROM doc d ");
		stmt.append("  JOIN external_accounts ea ON (ea.c_id = d.c_id AND ea.account_type = d.account_type AND d.account_code = ea.account_code) ");
		stmt.append("  JOIN ext_acc_addresses eaa ON (eaa.c_id = ea.c_id AND eaa.account_type = ea.account_type AND eaa.account_code = ea.account_code AND eaa.address_type = 'POST' AND eaa.default_ind = 'Y') ");
		stmt.append("  JOIN dist ds ON (ds.doc_id = d.doc_id AND ds.dis_id IN (SELECT ds2.dis_id FROM dist ds2 WHERE ds2.doc_id = d.doc_id and rownum = 1)) ");
		stmt.append("  JOIN fleet_masters fms ON (fms.fms_id = ds.cdb_code_1) ");
		stmt.append("  LEFT JOIN asset_item ai ON (ai.fleet_id = fms.fms_id AND ai.add_on_seq = '000') ");
		stmt.append("  WHERE d.doc_type IN ('INVOICEAP', 'STOCKRCPT') ");
		stmt.append("    AND d.doc_id = :docId ");

		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("docId", docId);
		records =  (List<Object[]>) query.getResultList();

		for(Object[] record : records) {
			int i = 0;
		
			invoice = new InvoiceVO();
			invoice.setExternalId(((BigDecimal)record[i]).toString());
			invoice.setControlCode(record[i+=1] == null ? ControlCodeEnum.NONE : ControlCodeEnum.valueOf((String)record[i]));
			invoice.setTranId((String)record[i+=1]);
			invoice.setVendor((String)record[i+=1]);	
			invoice.setSubsidiary(((BigDecimal)record[i+=1]).longValue());
			invoice.setTranDate((Date)record[i+=1]);
			invoice.setMemo((String)record[i+=1]);
			invoice.setVendorEaaId(((BigDecimal)record[i+=1]).longValue());	
			invoice.setDiscount((BigDecimal)record[i+=1]);
			invoice.setOpCode((String)record[i+=1]);			
			invoice.setDisposalFiscalYear(record[i+=1] == null ? null : Long.valueOf((String)record[i]));
			invoice.setCurrentFiscalYear(Long.valueOf((String)record[i+=1]));
		}
		
		return invoice;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CreditVO getCreditApHeaderByDocId(Long docId) {
		StringBuilder stmt;
		Query query;
		List<Object[]> records;
		CreditVO credit = null;
		
		stmt = new StringBuilder()
		    .append("SELECT d.doc_id, d.update_control_code, d.doc_no, nvl(ea.parent_account, ea.account_code) as account_code, d.c_id, d.doc_date, d.doc_description, ")
		    .append("    eaa.eaa_id, d.op_code, ")
		    .append("    (SELECT tp2.year ")
		    .append("       FROM time_periods tp2 ")
		    .append("       WHERE tp2.c_id = d.c_id ")
		    .append("         AND trunc(tp2.start_date) <= trunc(ai.disposal_date) ")
		    .append("         AND trunc(tp2.end_date) >= trunc(ai.disposal_date)) AS derived_disposal_fiscal_year, ")
		    .append("    (SELECT tp2.year ")
		    .append("       FROM time_periods tp2 ")
		    .append("       WHERE tp2.c_id = d.c_id ")
		    .append("         AND trunc(tp2.start_date) <= trunc(sysdate) ")
		    .append("         AND trunc(tp2.end_date) >= trunc(sysdate)) AS derived_current_fiscal_year, ")
		    .append("    (SELECT d2.doc_id ")
		    .append("       FROM doc d2 ")
		    .append("       WHERE substr(d2.doc_no, 1, 22) = substr(d.doc_no, 1, length(d.doc_no) - 3) ")  // A bug exists in AI where they are not capping the parent invoice doc no to 22 chars.
		    .append("         AND d2.doc_id <> d.doc_id ")                                                 // As a solution, we match on at most 22 chars
		    .append("         AND d2.c_id = d.invoice_to_c_id ")
		    .append("         AND d2.account_type = d.invoice_to_account_type ")
		    .append("         AND d2.account_code = d.invoice_to_account_code ")
			.append("         AND d2.doc_type = 'INVOICEAP' ")
		    .append("         AND d2.op_code = d.op_code) AS parent_invoice_doc_id ")
		    .append("  FROM doc d ")
		    .append("  JOIN external_accounts ea ON (ea.c_id = d.c_id AND ea.account_type = d.account_type AND d.account_code = ea.account_code) ")
		    .append("  JOIN ext_acc_addresses eaa ON (eaa.c_id = ea.c_id AND eaa.account_type = ea.account_type AND eaa.account_code = ea.account_code AND eaa.address_type = 'POST' AND eaa.default_ind = 'Y') ")
		    .append("  JOIN dist ds ON (ds.doc_id = d.doc_id AND ds.dis_id IN (SELECT ds2.dis_id FROM dist ds2 WHERE ds2.doc_id = d.doc_id and rownum = 1)) ")
		    .append("  JOIN fleet_masters fms ON (fms.fms_id = ds.cdb_code_1) ")
		    .append("  LEFT JOIN asset_item ai ON (ai.fleet_id = fms.fms_id AND ai.add_on_seq = '000') ")
		    .append("  WHERE d.doc_type IN ('CREDITAP') ")
		    .append("    AND d.doc_id = :docId ");

		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("docId", docId);
		records =  (List<Object[]>) query.getResultList();

		for(Object[] record : records) {
			int i = 0;
		
			credit = new CreditVO();
			credit.setExternalId(((BigDecimal)record[i]).toString());
			credit.setControlCode(record[i+=1] == null ? ControlCodeEnum.NONE : ControlCodeEnum.valueOf((String)record[i]));
			credit.setTranId((String)record[i+=1]);
			credit.setVendor((String)record[i+=1]);	
			credit.setSubsidiary(((BigDecimal)record[i+=1]).longValue());
			credit.setTranDate((Date)record[i+=1]);
			credit.setMemo((String)record[i+=1]);
			credit.setVendorEaaId(((BigDecimal)record[i+=1]).longValue());	
			credit.setOpCode((String)record[i+=1]);			
			credit.setDisposalFiscalYear(record[i+=1] == null ? null : Long.valueOf((String)record[i]));
			credit.setCurrentFiscalYear(Long.valueOf((String)record[i+=1]));
			credit.setParentExternalId(((BigDecimal)record[i+=1]).longValue());			
		}
		
		return credit;
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<InvoiceLineVO> getInvoiceApLinesByDocId(Long docId, boolean isVehicleInvoice) {
		StringBuilder stmt;
		Query query;
		List<Object[]> records;
		List<InvoiceLineVO> lines = new ArrayList<>();
				
		stmt =  new StringBuilder();
		stmt.append("SELECT dl.line_description, nvl(dl.qty_invoice, 1), nvl(decode(d.update_control_code, 'GRD_INTRNL', dl.unit_cost, dl.unit_price), 0) as rate, fms.unit_no, nvl(fin.recharge_to_client_flag, 'N') as fine_recharge_yn, di.gl_code, ai.asset_type, ");		
		stmt.append(   isVehicleInvoice ? generateVehicleInvoiceProductCodeSQL() : generateNonVehicleInvoiceProductCodeSQL()); stmt.append(" as derived_product_code, ");
		stmt.append(   isVehicleInvoice ? generateVehicleInvoiceDriverIdSQL() : generateNonVehicleInvoiceDriverIdSQL()); stmt.append(" as derived_drv_id, ");				
		stmt.append(   isVehicleInvoice ? generateVehicleInvoiceClientSQL() : generateNonVehicleInvoiceClientSQL()); stmt.append(" as derived_client ");						
		stmt.append("  FROM doc d ");
		stmt.append("  JOIN docl dl ON (dl.doc_id = d.doc_id AND nvl(dl.product_code, 'x') NOT IN ('AI_SHP_FEE','AI_FLT_FEE')) ");
		stmt.append("  JOIN dist di ON (di.docl_doc_id = dl.doc_id AND di.docl_line_id = dl.line_id ) ");
		stmt.append("  JOIN fleet_masters fms ON (fms.fms_id = di.cdb_code_1) ");			
		stmt.append("  JOIN gl_code gc ON (di.gl_code = gc.code) ");
		stmt.append("  LEFT JOIN docl_analysis dl_a ON (dl.doc_id = dl_a.doc_id AND dl.line_id = dl_a.line_id) ");
		stmt.append("  LEFT JOIN fines fin ON (d.source_code = 'FLFINE' AND fin.fin_id = dl.generic_ext_id) ");
		stmt.append("  LEFT JOIN asset_item ai ON (ai.fleet_id = fms.fms_id AND ai.add_on_seq = '000') ");
		stmt.append("  WHERE d.doc_type IN ('INVOICEAP', 'STOCKRCPT', 'CREDITAP') ");
		stmt.append("    AND d.doc_id = :docId");

		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("docId", docId);
		records =  (List<Object[]>) query.getResultList();

		for(Object[] record : records) {
			int i = 0;
			
			InvoiceLineVO line = new InvoiceLineVO();
			line.setDescription((String)record[i]);
			line.setQuantity((BigDecimal)record[i+=1]);			
			line.setRate((BigDecimal)record[i+=1]);
			line.setUnit((String)record[i+=1]);	
			line.setRebillableClientFine(DataUtil.convertToBoolean((String)record[i+=1]));
			line.setGlCode((String)record[i+=1]);
			line.setAssetType(record[i+=1] == null ? AssetTypeEnum.NONE : AssetTypeEnum.valueOf((String)record[i]));
			line.setProductCode(record[i+=1] == null ? ProductEnum.NONE : ProductEnum.valueOf((String)record[i]));
			line.setDrvId(record[i+=1] == null ? null : ((BigDecimal)record[i]).longValue());
			line.setClient((String)record[i+=1]);
									
			lines.add(line);
				
		}		
				
		return lines;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CreditLineVO> getCreditApLinesByDocId(Long docId) {
		StringBuilder stmt;
		Query query;
		List<Object[]> records;
		List<CreditLineVO> lines = new ArrayList<>();
				
		stmt =  new StringBuilder();
		stmt.append("SELECT dl.line_description, nvl(dl.qty_invoice, 1), nvl(decode(d.update_control_code, 'GRD_INTRNL', dl.unit_cost, dl.unit_price), 0) as rate, fms.unit_no, nvl(fin.recharge_to_client_flag, 'N') as fine_recharge_yn, di.gl_code, ai.asset_type, ");		
		stmt.append(     generateNonVehicleInvoiceProductCodeSQL()); stmt.append(" as derived_product_code, ");
		stmt.append(     generateNonVehicleInvoiceDriverIdSQL()); stmt.append(" as derived_drv_id, ");				
		stmt.append(     generateNonVehicleInvoiceClientSQL()); stmt.append(" as derived_client ");						
		stmt.append("  FROM doc d ");
		stmt.append("  JOIN docl dl ON (dl.doc_id = d.doc_id) ");
		stmt.append("  JOIN dist di ON (di.docl_doc_id = dl.doc_id AND di.docl_line_id = dl.line_id ) ");
		stmt.append("  JOIN fleet_masters fms ON (fms.fms_id = di.cdb_code_1) ");			
		stmt.append("  JOIN gl_code gc ON (di.gl_code = gc.code) ");
		stmt.append("  LEFT JOIN docl_analysis dl_a ON (dl.doc_id = dl_a.doc_id AND dl.line_id = dl_a.line_id) ");
		stmt.append("  LEFT JOIN fines fin ON (d.source_code = 'FLFINE' AND fin.fin_id = dl.generic_ext_id) ");
		stmt.append("  LEFT JOIN asset_item ai ON (ai.fleet_id = fms.fms_id AND ai.add_on_seq = '000') ");
		stmt.append("  WHERE d.doc_type IN ('INVOICEAP', 'STOCKRCPT', 'CREDITAP') ");
		stmt.append("    AND d.doc_id = :docId");

		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("docId", docId);
		records =  (List<Object[]>) query.getResultList();

		for(Object[] record : records) {
			int i = 0;
			
			CreditLineVO line = new CreditLineVO();
			line.setDescription((String)record[i]);
			line.setQuantity((BigDecimal)record[i+=1]);			
			line.setRate((BigDecimal)record[i+=1]);
			line.setUnit((String)record[i+=1]);	
			line.setRebillableClientFine(DataUtil.convertToBoolean((String)record[i+=1]));
			line.setGlCode((String)record[i+=1]);
			line.setAssetType(record[i+=1] == null ? AssetTypeEnum.NONE : AssetTypeEnum.valueOf((String)record[i]));
			line.setProductCode(record[i+=1] == null ? ProductEnum.NONE : ProductEnum.valueOf((String)record[i]));
			line.setDrvId(record[i+=1] == null ? null : ((BigDecimal)record[i]).longValue());
			line.setClient((String)record[i+=1]);
									
			lines.add(line);
				
		}		
				
		return lines;
	}	
	
	public Long getPurchaseOrderDocIdByInvoiceDocId(Long invoiceDocId) {
		Long poDocId;
		
		StringBuilder stmt = new StringBuilder();
		stmt.append("SELECT d.doc_id ");
		stmt.append("  FROM doc d ");
		stmt.append("  WHERE d.doc_type = 'PORDER' ");
		stmt.append("    AND EXISTS (SELECT 1 ");
		stmt.append("                  FROM doc_links dlk ");
		stmt.append("                  WHERE dlk.child_doc_id = :invoiceDocId ");
		stmt.append("                    AND dlk.parent_doc_id = d.doc_id) ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("invoiceDocId", invoiceDocId);
		
		poDocId = ((BigDecimal)query.getSingleResult()).longValue();
		
		return poDocId;
		
	}

	@Override
	public Long getInvoiceArIdFromInvoiceApId(Long invoiceApId) {
		Long id;
		
		StringBuilder stmt = new StringBuilder();
		stmt.append("SELECT to_char(d.doc_id) ");
		stmt.append("  FROM doc d ");
		stmt.append("  WHERE d.doc_type = 'INVOICEAR' ");
		stmt.append("    AND EXISTS (SELECT 1 ");
		stmt.append("                  FROM doc_links dlk ");
		stmt.append("                  WHERE dlk.child_doc_id = d.doc_id ");
		stmt.append("                    AND dlk.parent_doc_id = :invoiceApId ) ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("invoiceApId", invoiceApId);
		
		try {
			id = (Long.valueOf((String)query.getSingleResult()));
		} catch(NoResultException nre) {
			id = null;
		}
		
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PurchaseOrderVO getPurchaseOrderHeaderByDocId(Long docId) {
		StringBuilder stmt;
		Query query;
		List<Object[]> records;
		PurchaseOrderVO po = null;
		
		stmt = new StringBuilder();
		stmt.append("SELECT d.doc_id, d.update_control_code, d.doc_no, nvl(ea.parent_account, ea.account_code) as account_code, d.c_id, d.posted_date, d.doc_description, eaa.eaa_id, decode(nvl(d.order_type, 'M'), 'M', 'Y', 'N') AS IS_MAIN, d.op_code, ");
		stmt.append("    (SELECT tp2.year ");
		stmt.append("       FROM time_periods tp2 ");
		stmt.append("       WHERE tp2.c_id = d.c_id ");
		stmt.append("         AND trunc(ai.disposal_date) BETWEEN trunc(tp2.start_date) AND trunc(tp2.end_date)) AS derived_disposal_fiscal_year, ");
		stmt.append("    (SELECT tp2.year ");
		stmt.append("       FROM time_periods tp2 ");
		stmt.append("       WHERE tp2.c_id = d.c_id ");
		stmt.append("         AND trunc(sysdate) BETWEEN trunc(tp2.start_date) AND trunc(tp2.end_date)) AS derived_current_fiscal_year ");
		stmt.append("  FROM doc d ");
		stmt.append("  JOIN external_accounts ea ON (ea.c_id = d.c_id AND ea.account_type = d.account_type AND d.account_code = ea.account_code) ");
		stmt.append("  JOIN ext_acc_addresses eaa ON (eaa.c_id = ea.c_id AND eaa.account_type = ea.account_type AND eaa.account_code = ea.account_code AND eaa.address_type = 'POST' AND eaa.default_ind = 'Y') ");
		stmt.append("  JOIN dist ds ON (ds.doc_id = d.doc_id AND ds.dis_id IN (SELECT ds2.dis_id FROM dist ds2 WHERE ds2.doc_id = d.doc_id and rownum = 1)) ");
		stmt.append("  JOIN fleet_masters fms ON (fms.fms_id = ds.cdb_code_1) ");
		stmt.append("  LEFT JOIN asset_item ai ON (ai.fleet_id = fms.fms_id AND ai.add_on_seq = '000') ");
		stmt.append("  WHERE d.doc_type IN ('PORDER') ");
		stmt.append("    AND d.doc_id = :docId ");

		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("docId", docId);
		records =  (List<Object[]>) query.getResultList();

		for(Object[] record : records) {
			int i = 0;
			
			po = new PurchaseOrderVO();
			po.setExternalId(((BigDecimal)record[i]).toString());
			po.setControlCode(record[i+=1] == null ? ControlCodeEnum.NONE : ControlCodeEnum.valueOf((String)record[i]));
			po.setTranId((String)record[i+=1]);
			po.setVendor((String)record[i+=1]);	
			po.setSubsidiary(((BigDecimal)record[i+=1]).longValue());
			po.setTranDate((Date)record[i+=1]);
			po.setMemo((String)record[i+=1]);
			po.setVendorEaaId(((BigDecimal)record[i+=1]).longValue());	
			po.setMain(DataUtil.convertToBoolean((String)record[i+=1]));
			po.setOpCode((String)record[i+=1]);			
			po.setDisposalFiscalYear(record[i+=1] == null ? null : Long.valueOf((String)record[i]));
			po.setCurrentFiscalYear(Long.valueOf((String)record[i+=1]));
		}
		
		return po;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PurchaseOrderLineVO getPurchaseOrderLineByDocId(Long docId) {
		StringBuilder stmt;
		Query query;
		List<Object[]> records;
		PurchaseOrderLineVO line = null;
				
		stmt =  new StringBuilder();
		stmt.append("SELECT qty_invoice, nvl(sum(exp_fob_cost), 0), unit_no, gl_code, asset_type, line_desc, derived_product_code, derived_drv_id, derived_client ");
		stmt.append("  FROM ( ");
		stmt.append("    SELECT 1 as qty_invoice, dl.exp_fob_cost, fms.unit_no, di.gl_code, ai.asset_type, ");
		stmt.append("        (DECODE(nvl(order_type, 'M'),'M', fms.unit_no, (SELECT fms.unit_no || ' ' || DECODE(nvl(order_type, 'M'),'M', null, dl2.line_description) ");
		stmt.append("                                                          FROM docl dl2 ");
		stmt.append("                                                          WHERE dl2.doc_id = d.doc_id ");
		stmt.append("                                                            AND dl2.line_id = (SELECT min(dl3.line_id) ");
		stmt.append("                                                                                 FROM docl dl3 ");
		stmt.append("                                                                                   WHERE dl3.doc_id = d.doc_id)))) as line_desc, ");
		stmt.append("        DECODE(d.source_code, 'FLORDER', 'INVENTORY',(SELECT prd2.product_code ");
		stmt.append("           FROM products prd2, quotation_profiles qpr2, quotations quo2, quotation_models qmd2  ");
		stmt.append("           WHERE prd2.product_code = qpr2.prd_product_code ");
		stmt.append("             AND qpr2.qpr_id = quo2.qpr_qpr_id ");
		stmt.append("             AND quo2.quo_id = qmd2.quo_quo_id ");
		stmt.append("             AND qmd2.qmd_id = d.generic_ext_id)) as derived_product_code, ");		
		stmt.append("        (SELECT quo2.drv_drv_id ");
		stmt.append("           FROM  quotations quo2, quotation_models qmd2 ");
		stmt.append("           WHERE quo2.quo_id = qmd2.quo_quo_id ");
		stmt.append("             AND qmd2.qmd_id = d.generic_ext_id) AS derived_drv_id, ");
		stmt.append("        (SELECT quo2.c_id || ',' || quo2.account_type || ',' || quo2.account_code ");
		stmt.append("           FROM  quotations quo2, quotation_models qmd2 ");
		stmt.append("           WHERE quo2.quo_id = qmd2.quo_quo_id ");
		stmt.append("             AND qmd2.qmd_id = d.generic_ext_id) AS derived_client ");
		stmt.append("    FROM doc d");
		stmt.append("    JOIN docl dl ON (dl.doc_id = d.doc_id) ");
		stmt.append("    JOIN dist di ON (di.docl_doc_id = dl.doc_id AND di.docl_line_id = dl.line_id ) ");
		stmt.append("    JOIN fleet_masters fms ON (fms.fms_id = di.cdb_code_1) ");
		stmt.append("    JOIN gl_code gc ON (di.gl_code = gc.code) ");
		stmt.append("    LEFT JOIN docl_analysis dl_a ON (dl.doc_id = dl_a.doc_id AND dl.line_id = dl_a.line_id) ");
		stmt.append("    LEFT JOIN fines fin ON (d.source_code = 'FLFINE' AND fin.fin_id = dl.generic_ext_id) ");
		stmt.append("    LEFT JOIN asset_item ai ON (ai.fleet_id = fms.fms_id AND ai.add_on_seq = '000') ");
		stmt.append("    WHERE d.doc_type IN ('PORDER') ");
		stmt.append("      AND d.doc_id = :docId ");
		stmt.append("  ) lines");
		stmt.append("  GROUP BY qty_invoice, unit_no, gl_code, asset_type, line_desc, derived_product_code, derived_drv_id, derived_client");

		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("docId", docId);
		records =  (List<Object[]>) query.getResultList();

		for(Object[] record : records) {
			int i = 0;
			
			line = new PurchaseOrderLineVO();
			line.setQuantity((BigDecimal)record[i]);			
			line.setRate((BigDecimal)record[i+=1]);
			line.setUnit((String)record[i+=1]);				
			line.setGlCode((String)record[i+=1]);
			line.setAssetType(record[i+=1] == null ? AssetTypeEnum.NONE : AssetTypeEnum.valueOf((String)record[i]));			
			line.setDescription((String)record[i+=1]);			
			line.setProductCode(record[i+=1] == null ? ProductEnum.NONE : ProductEnum.valueOf((String)record[i]));
			line.setDrvId(record[i+=1] == null ? null : ((BigDecimal)record[i]).longValue());
			line.setClient((String)record[i+=1]);				
		}		
				
		return line;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Long> getMaintenanceInvoiceIds(Date start, Date end) {
		StringBuilder stmt;		
		stmt =  new StringBuilder();
		stmt.append("SELECT d.doc_id ");
		stmt.append("  FROM doc d ");
		stmt.append("  WHERE d.op_code = 'AUTO_MI' ");
		stmt.append("    AND d.doc_type = 'INVOICEAP' ");
		stmt.append("    AND d.doc_status = 'P' ");
		stmt.append("    AND d.posted_date BETWEEN :start AND :end ");
		stmt.append("    AND EXISTS ( SELECT 1 ");
		stmt.append("                   FROM docl dl2 ");
		stmt.append("                   WHERE dl2.doc_id = d.doc_id ");
		stmt.append("                     AND nvl(dl2.product_code, 'x') NOT IN ('AI_SHP_FEE','AI_FLT_FEE')) ");	
		
		Stream<Long> result =  entityManager.createNativeQuery(stmt.toString())
				.setParameter("start", start)
				.setParameter("end", end)
				.getResultStream();
				
		List<Long> ids = result.collect(Collectors.toList());
				
		return ids;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Long> getMaintenanceCreditIds(Date start, Date end) {
		StringBuilder stmt;		
		stmt =  new StringBuilder();
		stmt.append("SELECT doc_id ");
		stmt.append("  FROM doc ");
		stmt.append("  WHERE op_code = 'AUTO_MI' ");
		stmt.append("    AND doc_type = 'CREDITAP' ");
		stmt.append("    AND doc_status = 'P' ");
		stmt.append("    AND posted_date BETWEEN :start AND :end ");		
		
		Stream<Long> result =  entityManager.createNativeQuery(stmt.toString())
				.setParameter("start", start)
				.setParameter("end", end)
				.getResultStream();
				
		List<Long> ids = result.collect(Collectors.toList());
				
		return ids;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public AssetCreateVO getAssetCreateData(AssetCreateVO assetVO) {
		StringBuilder stmt = new StringBuilder();
		List<Object[]> records;
		
		stmt.append("		SELECT dl.total_price, ");
		stmt.append("		       fms.unit_no||'-'||dl.line_description, ");
		stmt.append("		       fms.unit_no, fms.fms_id, fms.vin, ");
		stmt.append("		       dl.doc_date, d.c_id");
		stmt.append("		  FROM docl dl ");
		stmt.append( "        JOIN doc d ON (dl.doc_id = d.doc_id) ");
		stmt.append("		  JOIN dist di ON (di.docl_doc_id = dl.doc_id AND di.docl_line_id = dl.line_id) ");
		stmt.append("		  JOIN fleet_masters fms ON (fms.fms_id = di.cdb_code_1) ");
		stmt.append("		 WHERE dl.total_price <> 0 ");
		stmt.append("		   AND dl.doc_id = :docId ");
		stmt.append("		   AND dl.line_id = :lineId ");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("docId", assetVO.getInvoiceArDocId());
		query.setParameter("lineId", assetVO.getInvoiceArLineId());
		
		records = query.getResultList();
		
		for(Object[] record : records) {
			int i=0;

			assetVO.setInitialValue((BigDecimal)(record[i]));
			assetVO.setDescription((String)(record[i+=1]));
			assetVO.setUnitNo((String)(record[i+=1]));
			assetVO.setFleetId(Long.valueOf(String.valueOf(record[i+=1])));
			assetVO.setVin(String.valueOf(record[i+=1]));
			assetVO.setStartDate((Date)record[i+=1]);
			assetVO.setcId(String.valueOf(record[i+=1]));			
		}
		
		return assetVO;	
	}
	
	public Long findDrvIdByDocId(Long docId) {
		Long drvId;
		StringBuilder stmt = new StringBuilder();
		stmt.append("SELECT ");
		stmt.append(generateVehicleInvoiceDriverIdSQL());
		stmt.append(" AS drv_id ");
		stmt.append(" FROM doc d ");
		stmt.append("WHERE d.doc_id = :docId");
		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("docId", docId);
		drvId = ((BigDecimal)query.getSingleResult()).longValue();
		
		return drvId;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasDistRecord(Long docId) {
		StringBuilder stmt = new StringBuilder();
		stmt.append("SELECT 1 ");
		stmt.append("  FROM dist ");
		stmt.append("  WHERE doc_id = :docId ");

		Stream<BigDecimal> result =  entityManager.createNativeQuery(stmt.toString())
				.setParameter("docId", docId)
				.getResultStream();
				
		Long count = result.collect(Collectors.counting());
				
		return count > 0 ? true : false;
	}	
	
	private String generateVehicleInvoiceProductCodeSQL() {
		StringBuilder stmt =  new StringBuilder();;
		stmt.append("  DECODE(d.source_code, 'FLORDER', 'INVENTORY',(SELECT prd2.product_code ");
		stmt.append("     FROM products prd2, quotation_profiles qpr2, quotations quo2, quotation_models qmd2 ");
		stmt.append("     WHERE prd2.product_code = qpr2.prd_product_code ");
		stmt.append("       AND qpr2.qpr_id = quo2.qpr_qpr_id ");
		stmt.append("       AND quo2.quo_id = qmd2.quo_quo_id ");		
		stmt.append("       AND qmd2.qmd_id = d.generic_ext_id)) ");
		return stmt.toString();
	}
	
	private String generateVehicleInvoiceDriverIdSQL() {
		StringBuilder stmt =  new StringBuilder();
		
		stmt.append(" (SELECT quo2.drv_drv_id ");
		stmt.append("    FROM  quotations quo2, quotation_models qmd2 ");		
		stmt.append("    WHERE quo2.quo_id = qmd2.quo_quo_id ");		
		stmt.append("      AND qmd2.qmd_id = d.generic_ext_id) ");	
		
		return stmt.toString();		
	}
		
	private String generateVehicleInvoiceClientSQL() {
		StringBuilder stmt =  new StringBuilder();
		
		stmt.append(" (SELECT quo2.c_id || ',' || quo2.account_type || ',' || quo2.account_code ");		
		stmt.append("    FROM  quotations quo2, quotation_models qmd2 ");		
		stmt.append("    WHERE quo2.quo_id = qmd2.quo_quo_id ");		
		stmt.append("      AND qmd2.qmd_id = d.generic_ext_id) ");		
		
		return stmt.toString();		
	}
	
	private String generateNonVehicleInvoiceProductCodeSQL() {
		StringBuilder stmt =  new StringBuilder();
		
		stmt.append(" DECODE(d.source_code, ");
		stmt.append("         'FLORDER', 'INVENTORY', ");		
		stmt.append("         'FLFINE', (SELECT prd2.product_code ");
		stmt.append("                       FROM products prd2, quotation_profiles qpr2, quotations quo2, quotation_models qmd2 ");
		stmt.append("                       WHERE prd2.product_code = qpr2.prd_product_code ");
		stmt.append("                         AND qpr2.qpr_id = quo2.qpr_qpr_id ");
		stmt.append("                         AND quo2.quo_id = qmd2.quo_quo_id ");
		stmt.append("                         AND qmd2.qmd_id = ( SELECT cln3.qmd_qmd_id ");
		stmt.append("                                               FROM contract_lines cln3 ");
		stmt.append("                                               WHERE cln_id = fl_contract.GET_CONTRACT_LINE(fms.fms_id,(SELECT f4.offence_date FROM fines f4 WHERE f4.fin_id = dl.generic_ext_id)))), ");
		stmt.append("         (SELECT prd2.product_code ");
		stmt.append("            FROM products prd2, quotation_profiles qpr2, quotations quo2, quotation_models qmd2 ");
		stmt.append("            WHERE prd2.product_code = qpr2.prd_product_code ");
		stmt.append("              AND qpr2.qpr_id = quo2.qpr_qpr_id ");
		stmt.append("              AND quo2.quo_id = qmd2.quo_quo_id ");
		stmt.append("              AND qmd2.qmd_id = ( NVL((SELECT cln3.qmd_qmd_id ");
		stmt.append("                                         FROM contract_lines cln3 ");
		stmt.append("                                         WHERE cln_id = fl_contract.GET_CONTRACT_LINE(fms.fms_id, d.doc_date)), ");
		stmt.append("                                      (SELECT max(qmd3.qmd_id) ");
		stmt.append("                                         FROM quotation_models qmd3 ");
		stmt.append("                                         WHERE qmd3.acceptance_date IS NOT NULL ");
		stmt.append("                                           AND qmd3.fms_fms_id = fms.fms_id OR qmd3.unit_no = fms.unit_no))))) ");	
		
		return stmt.toString();
	}	
	
	private String generateNonVehicleInvoiceDriverIdSQL() {
		StringBuilder stmt =  new StringBuilder();
		
		stmt.append(" DECODE(d.source_code, ");
		stmt.append("        'FLORDER', to_number(null), ");		
		stmt.append("        'FLFINE', (SELECT min(dal2.drv_drv_id) ");
		stmt.append("                     FROM driver_allocations dal2 ");
		stmt.append("                     WHERE dal2.fms_fms_id = fms.fms_id ");
		stmt.append("                       AND trunc(fin.offence_date) BETWEEN trunc(dal2.from_date) AND trunc(nvl(dal2.to_date, sysdate))), ");
		stmt.append("         (NVL( (SELECT min(dal2.drv_drv_id) ");
		stmt.append("                  FROM driver_allocations dal2 ");
		stmt.append("                  WHERE dal2.fms_fms_id = fms.fms_id ");
		stmt.append("                    AND trunc(d.doc_date) BETWEEN trunc(dal2.from_date) AND trunc(nvl(dal2.to_date, sysdate))), ");
		stmt.append("                (SELECT quo2.drv_drv_id ");
		stmt.append("                   FROM quotations quo2, quotation_models qmd2 ");
		stmt.append("                   WHERE quo2.quo_id = qmd2.quo_quo_id ");
		stmt.append("                     AND qmd2.qmd_id IN (SELECT max(qmd3.qmd_id) ");
		stmt.append("                                           FROM quotation_models qmd3 ");
		stmt.append("                                           WHERE qmd3.acceptance_date IS NOT NULL ");
		stmt.append("                                             AND (qmd3.fms_fms_id = fms.fms_id OR qmd3.unit_no = fms.unit_no)))))) ");
		
		return stmt.toString();		
	}
	
	private String generateNonVehicleInvoiceClientSQL() {
		StringBuilder stmt =  new StringBuilder();
		
		stmt.append(" DECODE(d.source_code, ");
		stmt.append("        'FLORDER', null, ");
		stmt.append("        'FLFINE', (SELECT quo2.c_id || ',' || quo2.account_type || ',' || quo2.account_code ");
		stmt.append("                     FROM  quotations quo2, quotation_models qmd2 ");
		stmt.append("                     WHERE quo2.quo_id = qmd2.quo_quo_id ");
		stmt.append("                       AND qmd2.qmd_id = ( SELECT cln3.qmd_qmd_id ");
		stmt.append("                                             FROM contract_lines cln3 ");
		stmt.append("                                             WHERE cln_id = fl_contract.GET_CONTRACT_LINE(fms.fms_id,(SELECT f4.offence_date FROM fines f4 WHERE f4.fin_id = dl.generic_ext_id)))), ");
		stmt.append("        (SELECT quo2.c_id || ',' || quo2.account_type || ',' || quo2.account_code ");
		stmt.append("                FROM  quotations quo2, quotation_models qmd2 ");
		stmt.append("                WHERE quo2.quo_id = qmd2.quo_quo_id ");
		stmt.append("                  AND qmd2.qmd_id IN (NVL( (SELECT cln3.qmd_qmd_id ");
		stmt.append("                                              FROM contract_lines cln3  ");
		stmt.append("                                              WHERE cln_id = fl_contract.GET_CONTRACT_LINE(fms.fms_id, d.doc_date)), ");
		stmt.append("                                           (SELECT max(qmd3.qmd_id) ");
		stmt.append("                                              FROM quotation_models qmd3 ");
		stmt.append("                                              WHERE qmd3.acceptance_date IS NOT NULL ");		
		stmt.append("                                                AND (qmd3.fms_fms_id = fms.fms_id OR qmd3.unit_no = fms.unit_no)))))) ");
			
		return stmt.toString();		
	}

	@Override
	public BigDecimal getUnpaidPOTotalByFmsId(Long fmsId) {
		BigDecimal unPaidPOTotal = BigDecimal.ZERO;
		StringBuilder stmt = new StringBuilder();
				   
		stmt.append(" SELECT sum(d_po.total_doc_price) ");
		stmt.append("   FROM doc d_po ");
		stmt.append("  WHERE d_po.doc_type = 'PORDER' ");
		stmt.append("    AND d_po.source_code IN ('FLQUOTE', 'FLORDER') ");
		stmt.append("    AND d_po.doc_status = 'R' ");
		stmt.append("    AND EXISTS (SELECT 1 FROM dist WHERE dist.doc_id = d_po.doc_id AND dist.cdb_code_1 = TO_CHAR(:fmsId)) " ); 
		stmt.append("    AND NOT EXISTS (SELECT 1 FROM doc_links dl ");
		stmt.append("                      JOIN doc d_ap on (dl.child_doc_id = d_ap.doc_id) ");
		stmt.append("                     WHERE d_po.doc_id = dl.parent_doc_id ");
		stmt.append("                       AND d_ap.doc_type = 'INVOICEAP') ");

		
		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
				
		unPaidPOTotal = (BigDecimal)query.getSingleResult();
		
		return (unPaidPOTotal == null ? BigDecimal.ZERO : unPaidPOTotal);
	}

	@Override
	public void closeInternal(Long docId) {		
		entityManager.createStoredProcedureQuery("transaction_api_wrapper.process_close_po_inbound")
                .registerStoredProcedureParameter(1, Long.class, ParameterMode.IN)
                .setParameter(1, docId)
                .execute();        
	}		
}


