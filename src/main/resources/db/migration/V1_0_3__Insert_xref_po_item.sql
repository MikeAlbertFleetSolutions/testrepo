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
-- 1100         06-May-2020    LAFS                       Wayne S.          Changes were made to the items mapping doc, updating to reflect changes.
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

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'CE_LTD,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'DE,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'DEMO,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'FLMAINT,0', 'Maintenance - Rebillable');   
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'FLMAINT,1', 'Maintenance - Company Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'FLMAINT,2', 'Maintenance - Rental'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'FLMAINT,3', 'Maintenance - Non Rebillable');   
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'FLMAINTNA,0', 'Maintenance - Rebillable'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'FLTRANS,0', 'Delivery - Fleet');   
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'FLTRANS,1', 'Delivery - Used Cars');   
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'FLTRANS,2', 'Delivery - MAX Fleet'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'GRD_INTRNL,0', 'Delivery - Internal CD Fee'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'INVENTORY,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'INV_EXCLUS,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'INV_FLEX,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'MAN_RENT,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'OE_EQUIP,0', 'Equipment'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'OE_IRENT,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'OE_LTD,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PUR_COSTPL,0', 'Client Direct Purchase'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'PUR_INVADJ,0', 'Client Direct Purchase'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'REGM_LTD,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'ST,0', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'STMAINT,0', 'Maintenance - Rental'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'UCMAINT,0', 'Maint (UCMaint) - Fleet');   
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'UCMAINT,1', 'Maint (UCMaint) - Used Vehicle');   
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'UCMAINT,2', 'Maint (UCMaint) - Disposed Vehicle (CY)'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PO-ITEM', 'UCMAINT,3', 'Maint (UCMaint) - Disposed Vehicle (PY)'); 

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

