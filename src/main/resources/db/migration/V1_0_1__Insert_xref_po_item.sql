DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Inserts the po-item mappings into xref
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         14-Apr-2020    LAFS                       Wayne S.          Init
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'PO-ITEM';

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'PO-ITEM'; 
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,AM_LC,SALES,0', 'Purchase'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,CE_LTD,SALES,0', 'Closed End'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,DE,SALES,0', 'Demo'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,DEMO,SALES,0', 'Demo'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,FLMAINT,SALES,0', 'Maintenance v1'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,FLMAINTNA,SALES,0', 'Maintenance v1'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,FLTRANS,SALES,0', 'Transports'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,GRD_EXTRNL,SALES,0', 'GRD External Vendor'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,GRD_EXTRNL,SALES,1', 'Get Ready Delivery - Rental'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,GRD_INTRNL,SALES,0', 'Get Ready Delivery'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,GRD_INTRNL,SALES,1', 'Get Ready Delivery - Rental'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,INVENTORY,SALES,0', 'Inventory'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,INV_EXCLUS,SALES,0', 'Inventory Exclusive'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,INV_FLEX,SALES,0', 'Inventory Flex'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,MAN_RENT,SALES,0', 'Manual Rental'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,OE_EQUIP,SALES,0', 'Open End Equipment'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,OE_EQUIP,SALES,1', 'Open End Equipment CD'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,OE_IRENT,SALES,0', 'Open End Interim Rent'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,OE_IRENT,SALES,1', 'Open End Interim Rent CD'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,OE_LTD,SALES,0', 'Open End'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,OE_LTD,SALES,1', 'Open End CD'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,PRMAINT,SALES,0', 'Wholesale Purchase Maint'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,PUR_COSTPL,SALES,0', 'Purchase Cost Plus'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,PUR_INVADJ,SALES,0', 'Purchase Invoice Adj'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,PUR_RESALE,SALES,0', 'Purchase Resale'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,REGM_LTD,SALES,0', 'REG_M'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,ST,SALES,0', 'Short Term v1'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,STMAINT,SALES,0', 'Short Term v2'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,UCMAINT,SALES,0', 'Maint - MAX Fleet'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,UCMAINT,SALES,1', 'Used Car Maint'); 

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

