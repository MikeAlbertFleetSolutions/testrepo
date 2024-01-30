DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Update CREDIT_TERM xref for PURCHASE
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
    WHERE group_name = 'CLIENT_CREDIT_TERM'; 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CLIENT_CREDIT_TERM'; 
    COMMIT;
  END IF;


  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT_CREDIT_TERM', 'ASSET_MGMT', 'Asset Management Payment Terms');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT_CREDIT_TERM', 'NET90', 'Net 90');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT_CREDIT_TERM', 'PURCHASE', 'Net 10');  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT_CREDIT_TERM', 'STANDARD', '1st of Mo');  

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/
