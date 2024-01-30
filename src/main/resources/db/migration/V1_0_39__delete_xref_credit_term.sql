DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Delete the credit terms mapping that were inserted for client.
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         22-Apr-2021    LAFS-405                   Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CREDIT_TERM'
      AND internal_value IN ('PURCHASE', 'STANDARD'); 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CREDIT_TERM'
        AND internal_value IN ('PURCHASE', 'STANDARD');       
    COMMIT;
  END IF;

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Deleted Successfully');

END;
/

