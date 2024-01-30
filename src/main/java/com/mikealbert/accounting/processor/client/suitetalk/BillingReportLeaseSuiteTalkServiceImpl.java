package com.mikealbert.accounting.processor.client.suitetalk;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.accounting.processor.enumeration.CustomRecordEnum;
import com.mikealbert.accounting.processor.enumeration.FieldEnum;
import com.mikealbert.accounting.processor.enumeration.LeaseFieldEnum;
import com.mikealbert.accounting.processor.vo.BillingReportLeaseVO;
import com.mikealbert.util.data.DateUtil;
import com.mikealbert.webservice.suitetalk.enumeration.SavedSearchEnum;
import com.netsuite.webservices.platform.common_2023_2.CustomRecordSearchBasic;
import com.netsuite.webservices.platform.common_2023_2.CustomRecordSearchRowBasic;
import com.netsuite.webservices.platform.core_2023_2.ListOrRecordRef;
import com.netsuite.webservices.platform.core_2023_2.RecordRef;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnCustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.SearchColumnDateCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchCustomFieldList;
import com.netsuite.webservices.platform.core_2023_2.SearchDateCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchMultiSelectCustomField;
import com.netsuite.webservices.platform.core_2023_2.SearchRow;
import com.netsuite.webservices.platform.core_2023_2.types.GetCustomizationType;
import com.netsuite.webservices.platform.core_2023_2.types.SearchDateFieldOperator;
import com.netsuite.webservices.platform.core_2023_2.types.SearchMultiSelectFieldOperator;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecordSearch;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecordSearchAdvanced;
import com.netsuite.webservices.setup.customization_2023_2.CustomRecordSearchRow;

@Service("billingReportLeaseSuiteTalkService")
public class BillingReportLeaseSuiteTalkServiceImpl extends BaseSuiteTalkService implements BillingReportLeaseSuiteTalkService {

    @Resource SuiteTalkCacheService suiteTalkCacheService;

    @Override
    public BillingReportLeaseVO searchByUnitInternalIdAndEffectiveDate(String unitInternalId, Date effectiveDate) throws Exception {
       return convertToBillingReportLeaseVO(
           super.service.searchAdvancedX(convertToLeaseSearchAdvanced(unitInternalId, effectiveDate)) );
        
    }
    
    private CustomRecordSearchAdvanced convertToLeaseSearchAdvanced(String unitInternalId, Date effectiveDate) throws Exception {
		CustomRecordSearchAdvanced crsa = new CustomRecordSearchAdvanced();
		crsa.setSavedSearchScriptId(SavedSearchEnum.MA_LEASE.getId());

		CustomRecordSearch cs = new CustomRecordSearch();

		CustomRecordSearchBasic crsb = new CustomRecordSearchBasic();
		crsb.setRecType(new RecordRef(null, suiteTalkCacheService.searchForCustomXId(CustomRecordEnum.LEASE.getScriptId(), GetCustomizationType.customRecordType), null, null));

        SearchMultiSelectCustomField unitNoField = new SearchMultiSelectCustomField(null, LeaseFieldEnum.UNIT.getScriptId(), SearchMultiSelectFieldOperator.anyOf, new ListOrRecordRef[]{new ListOrRecordRef(null, unitInternalId, null, null)});
		SearchDateCustomField commencementDateField = new SearchDateCustomField(null, LeaseFieldEnum.COMMENCEMENT_DATE.getScriptId(), SearchDateFieldOperator.onOrBefore, null, DateUtil.convertToCalendar(effectiveDate), null);
		//SearchDateCustomField endDateField = new SearchDateCustomField(null, LeaseFieldEnum.WILLOW_END_DATE.getScriptId(), SearchDateFieldOperator.onOrAfter, null, DateUtil.convertToCalendar(effectiveDate), null);  
		
        //crsb.setCustomFieldList(new SearchCustomFieldList(new SearchCustomField[]{unitNoField, commencementDateField, endDateField}));
        crsb.setCustomFieldList(new SearchCustomFieldList(new SearchCustomField[]{unitNoField, commencementDateField}));

		cs.setBasic(crsb);
		crsa.setCriteria(cs);        
                
        return crsa;
    }

    private BillingReportLeaseVO convertToBillingReportLeaseVO(List<SearchRow> searchResult) {
        BillingReportLeaseVO retVO = null;

        CustomRecordSearchRow effSearchRow = searchResult.stream()
            .map(r -> (CustomRecordSearchRow)r)
            .sorted(Comparator.comparing(r ->  parseDateField(r.getBasic().getCustomFieldList(), LeaseFieldEnum.COMMENCEMENT_DATE), Comparator.reverseOrder()))
            .findFirst()
            .orElse(null);

        if(effSearchRow != null) {
            retVO = new BillingReportLeaseVO()
                .setInternalId(effSearchRow.getBasic().getInternalId()[0].getSearchValue().getInternalId())
                .setExternalId(effSearchRow.getBasic().getExternalId()[0].getSearchValue().getExternalId())
                .setName(effSearchRow.getBasic().getName()[0].getSearchValue())
                .setAltname(effSearchRow.getBasic().getAltName()[0].getSearchValue())
                .setLeaseType(parseLeaseType(effSearchRow))
                .setCommencementDate(parseDateField(effSearchRow.getBasic().getCustomFieldList(), LeaseFieldEnum.COMMENCEMENT_DATE))
                .setEndDate(parseDateField(effSearchRow.getBasic().getCustomFieldList(), LeaseFieldEnum.WILLOW_END_DATE))
                .setUnitInternalId(parseUnitInternalId(effSearchRow))
                .setUnitExternalId(parseUnitExternalId(effSearchRow))
                .setUnitNo(parseUnitNo(effSearchRow));
        }

        return retVO;
    }

    private Date parseDateField(SearchColumnCustomFieldList fieldList, FieldEnum fieldEnum) {
		return Arrays.asList(fieldList.getCustomField()).stream()
		    .filter(r -> (r instanceof SearchColumnDateCustomField))
			.map(r -> (SearchColumnDateCustomField) r)
			.filter(r -> r.getScriptId().equalsIgnoreCase(fieldEnum.getScriptId()))
			.map(r -> r.getSearchValue().getTime())
			.findFirst()
			.orElse(null);
	}

	private String parseUnitInternalId(CustomRecordSearchRow row) {
        String retVal = null;	

		if(row.getCustomSearchJoin() == null) return retVal;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(row.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals("custrecord_200_cseg_mafs_unit"))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

        if(crsrb != null) {
            retVal = crsrb.getInternalId()[0].getSearchValue().getInternalId();
        }

        return retVal;            
	}	 
    
	private String parseUnitExternalId(CustomRecordSearchRow row) {
        String retVal = null;	

		if(row.getCustomSearchJoin() == null) return retVal;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(row.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals("custrecord_200_cseg_mafs_unit"))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

        if(crsrb != null) {
            retVal = crsrb.getExternalId()[0].getSearchValue().getExternalId();
        }

        return retVal;            
	}  
    
	private String parseUnitNo(CustomRecordSearchRow row) {
        String retVal = null;	

		if(row.getCustomSearchJoin() == null) return retVal;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(row.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals("custrecord_200_cseg_mafs_unit"))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

        if(crsrb != null) {
            retVal = crsrb.getName()[0].getSearchValue();
        }

        return retVal;            
	}  
    
	private String parseLeaseType(CustomRecordSearchRow row) {
        String retVal = null;	

		if(row.getCustomSearchJoin() == null) return retVal;

		CustomRecordSearchRowBasic crsrb = Arrays.asList(row.getCustomSearchJoin()).stream()
			.filter(r -> r.getCustomizationRef().getScriptId().equals(LeaseFieldEnum.LEASE_TYPE.getScriptId()))
			.map(r -> (CustomRecordSearchRowBasic) r.getSearchRowBasic() )
			.findFirst()
			.orElse(null);

        if(crsrb != null) {
            retVal = crsrb.getName()[0].getSearchValue();
        }

        return retVal;            
	}     
}
