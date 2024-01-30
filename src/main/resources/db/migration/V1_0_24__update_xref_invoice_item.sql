DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Accounting renamed item 'Maintenance - Company Vehicle' to 'Maintenance - Company Vehicle (G&A)'. This script makes the same change in XREF
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         10-Aug-2020    LAFS-1268                  Wayne S.          Update XREF with external value of 'Maintenance - Company Vehicle'
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE external_value = 'Maintenance - Company Vehicle'; 

  IF v_count > 0 THEN
    UPDATE xref
      SET external_value = 'Maintenance - Company Vehicle (G&A)'
      WHERE external_value = 'Maintenance - Company Vehicle';
    
    COMMIT;

    DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');    
  END IF;
     
END;
/

