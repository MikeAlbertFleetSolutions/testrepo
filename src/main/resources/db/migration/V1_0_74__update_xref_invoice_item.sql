DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__update_xref_client_invoice_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Rename an existing items
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         27-May-2022    LAFS-9942                  Klaus O.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CLIENT-INVOICE-ITEM'
      AND internal_value = '01210025000';

  IF v_count > 0 THEN
    UPDATE xref
      SET external_value = 'Car Sale Adjustment'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '01210025000'; 

    COMMIT;
  END IF;
      
  DBMS_OUTPUT.PUT_LINE ('Data Updated Successfully');

END;
/