DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Inserts the invoice-item mappings into xref for "Sales Tax - Driver Sales"
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         07-Dec-2020    LAFS-1657                  Wayne S.          Add item for Sales Tax - Driver Sales
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'INVOICE-ITEM'
      AND internal_value = '{"controlCode":"FLLICENSE","index":2}';

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'INVOICE-ITEM'
        AND internal_value = '{"controlCode":"FLLICENSE","index":2}'; 
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":2}', 'Sales Tax - Driver Sales');  
    
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

