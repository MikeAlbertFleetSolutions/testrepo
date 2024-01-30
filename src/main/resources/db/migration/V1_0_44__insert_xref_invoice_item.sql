DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql 
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Add Maintenance - Rental item mapping for ST AI Invoice
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         01-Jun-2021    LAFS-2921                  Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'INVOICE-ITEM'    
      AND internal_value IN ('{"controlCode":"FLMAINT","index":3}', '{"controlCode":"FLMAINT","index":4}'); 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'INVOICE-ITEM'    
        AND internal_value IN ('{"controlCode":"FLMAINT","index":3}', '{"controlCode":"FLMAINT","index":4}'); 

    COMMIT;
  END IF;

  --These two were deleted from previous migration...1.0.42
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLMAINT","index":3}', 'Maintenance - Company Vehicle (Selling)');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLMAINT","index":4}', 'Maintenance - Company Vehicle (G&A)');  
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

