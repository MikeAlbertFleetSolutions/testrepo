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
-- 1100         10-Feb-2023    HELP-5528                  Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CLIENT-INVOICE-ITEM'
      AND internal_value IN ('01120039000');

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value IN ('01120039000');

    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','01120039000', 'Rebillable - Fuel');
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/
