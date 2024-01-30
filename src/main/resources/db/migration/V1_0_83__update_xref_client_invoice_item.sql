DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__update_xref_client_invoice_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Rename an existing item
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         29-Aug-2022    LAFS-10079                 Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count                 NUMBER := 0;
  v_group_name            VARCHAR2(200) := 'CLIENT-INVOICE-ITEM';
  v_internal_value        VARCHAR2(200) := '01560045100';
  v_old_external_value    VARCHAR2(200) := 'Rebillable - Title, License, Tax';
  v_new_external_value    VARCHAR2(200) := 'Rebillable - UCC Filing';

BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = v_group_name
      AND internal_value = v_internal_value
      AND external_value = v_old_external_value; 

  IF v_count > 0 THEN
    UPDATE xref
      SET external_value = v_new_external_value
      WHERE group_name = v_group_name
        AND internal_value = v_internal_value      
        AND external_value = v_old_external_value; 

    COMMIT;
  END IF;
      
  DBMS_OUTPUT.PUT_LINE ('Data Updated Successfully');

END;
/