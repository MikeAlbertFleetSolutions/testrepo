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
-- 1100         27-May-2021    LAFS-2898                  Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'INVOICE-ITEM'    
      AND external_value = 'Maintenance - Rental'; 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'INVOICE-ITEM'    
        AND external_value like 'Maintenance - Rental'; 
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLMAINT","index":2}', 'Maintenance - Rental');
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

