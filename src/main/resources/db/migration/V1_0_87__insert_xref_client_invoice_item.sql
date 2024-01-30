DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__insert_xref_client_invoice_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Add new AR mappings
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         04-Nov-2022    HELP-510, 1005, 1156       Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CLIENT-INVOICE-ITEM'
      AND internal_value IN ('02700005100', '01400229100', '02610004100');      

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value IN ('02700005100', '01400229100', '02610004100');

    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','02700005100', 'Rebillable - Property Taxes');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','01400229100', 'Rebillable - TollGuard');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','02610004100', '_LeaseCall_InRate');
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/
