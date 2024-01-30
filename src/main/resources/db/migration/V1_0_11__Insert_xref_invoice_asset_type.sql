DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_invoice_asset_type.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Inserts the invoice-asset_type mappings into xref
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         16-Jun-2020    LAFS                       Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'INVOICE-ASSET-TYPE';

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'INVOICE-ASSET-TYPE'; 
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'CE_LTD', 'Fleet-Closed End');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'DE', 'Company Vehicles - Selling Depts');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'DEMO', 'Company Vehicles - Selling Depts');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'INVENTORY', 'Long Term Inventory');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'INV_EXCLUS', 'Inventory');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'INV_FLEX', 'Inventory');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'MAN_RENT', 'Fleet-Closed End');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'OE_EQUIP', 'Fleet-Equip');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'OE_IRENT', 'Fleet-Open End');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'OE_LTD', 'Fleet-Open End');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'REGM_LTD', 'Fleet-Closed End');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ASSET-TYPE', 'ST', 'Fleet-Rental');   

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

