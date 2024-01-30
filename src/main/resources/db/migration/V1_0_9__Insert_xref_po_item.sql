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
-- 1100         12-May-2020    LAFS                       Wayne S.          Updates based on removal of FLTRANS in items master mapping document.
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'PURCHASE-ORDER-ITEM';

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'PURCHASE-ORDER-ITEM'; 
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"CE_LTD","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"DE","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"DEMO","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"FLMAINT","index":0}', 'Maintenance - Non Rebillable'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"FLMAINT","index":1}', 'Maintenance - Company Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"FLMAINT","index":2}', 'Maintenance - Rental'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"FLMAINT","index":3}', 'Maintenance - Rebillable'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"FLMAINTNA","index":0}', 'Maintenance - Rebillable'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"FLMAINTNA","index":1}', 'Maintenance - Company Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"FLMAINTNA","index":2}', 'Maintenance - Rental'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"GRD_INTRNL","index":0}', 'Delivery - Internal CD Fee'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"INVENTORY","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"INV_EXCLUS","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"INV_FLEX","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"MAN_RENT","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"OE_EQUIP","index":0}', 'Equipment'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"OE_IRENT","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"OE_LTD","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"PUR_COSTPL","index":0}', 'Client Direct Purchase'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"PUR_INVADJ","index":0}', 'Client Direct Purchase'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"REGM_LTD","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"ST","index":0}', 'Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"STMAINT","index":0}', 'Maintenance - Rental'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"UCMAINT","index":0}', 'Maint (UCMaint) - Fleet'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"UCMAINT","index":1}', 'Maint (UCMaint) - Used Vehicle'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"UCMAINT","index":2}', 'Maint (UCMaint) - Disposed Vehicle (CY)'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('PURCHASE-ORDER-ITEM', '{"controlCode":"UCMAINT","index":3}', 'Maint (UCMaint) - Disposed Vehicle (PY)');   

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

