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
-- 1100         26-Aug-2022    LAFS-10077                 Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CLIENT-INVOICE-ITEM'
      AND internal_value = '01400014100'
      AND external_value = 'Fee - Cancellation'; 

  IF v_count > 0 THEN
    UPDATE xref
      SET external_value = 'Fee - Cancellation of Vehicle Order'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '01400014100'      
        AND external_value = 'Fee - Cancellation'; 

    COMMIT;
  END IF;
      
  DBMS_OUTPUT.PUT_LINE ('Data Updated Successfully');

END;
/