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
-- 1100         28-Apr-2020    LAFS                       Wayne S.          Changes were made to the items mapping doc, updating to reflect changes.
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

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,AM_LC,SALES,,0', 'Client Direct Purchase'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,CE_LTD,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,DE,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,DEMO,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,FLMAINTNA,SALES,,0', 'Maintenance - Rechargeable'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,GRD_INTRNL,SALES,,0', 'Delivery'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,GRD_INTRNL,SALES,,1', 'Delivery - Rental'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,INVENTORY,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,INV_EXCLUS,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,INV_FLEX,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,MAN_RENT,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,OE_EQUIP,SALES,,0', 'Equipment'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,OE_IRENT,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,OE_LTD,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,PUR_COSTPL,SALES,,0', 'Client Direct Purchase'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,PUR_INVADJ,SALES,,0', 'Client Direct Purchase'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,PUR_RESALE,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,REGM_LTD,SALES,,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PORDER,ST,SALES,,0', 'Vehicle');    

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/
