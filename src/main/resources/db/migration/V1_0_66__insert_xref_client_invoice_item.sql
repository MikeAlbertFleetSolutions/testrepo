DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__insert_xref_client_invoice_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Remap 01560005500 and 02560005500 to Disposal - Auction Fees
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         06-Dec-2021    LAFS-4233                  Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CLIENT-INVOICE-ITEM'
      AND internal_value IN ('01560005500', '02560005500'); 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value IN ('01560005500', '02560005500'); 

    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','01560005500', 'Disposal - Auction Fees');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','02560005500', 'Disposal - Auction Fees');  
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

