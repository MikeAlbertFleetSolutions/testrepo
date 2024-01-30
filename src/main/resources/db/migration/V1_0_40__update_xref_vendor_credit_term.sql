DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Rename credit_term group to vendor_credit_term
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
    WHERE group_name = 'CREDIT_TERM'; 

  IF v_count > 0 THEN
    UPDATE xref
      SET group_name = 'VENDOR_CREDIT_TERM'
      WHERE group_name = 'CREDIT_TERM';

    COMMIT;
  END IF;

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Updated Successfully');

END;
/

