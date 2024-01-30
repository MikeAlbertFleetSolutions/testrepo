DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Inserts credit item xrefs based on the "AI from AI" item mapping updates
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         10-Aug-2020    LAFS-1108                   Wayne S.          Add inserts
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CREDIT-ITEM'; 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CREDIT-ITEM';
      
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CREDIT-ITEM', '{"controlCode":"FLMAINT","index":0}', 'Maintenance - Auto Integrate Fee');  
  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CREDIT-ITEM', '{"controlCode":"DEMOMAINT","index":0}', 'Maintenance - Auto Integrate Fee');  
  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CREDIT-ITEM', '{"controlCode":"STMAINT","index":0}', 'Maintenance - Auto Integrate Fee');  
  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CREDIT-ITEM', '{"controlCode":"UCMAINT","index":0}', 'Maintenance - Auto Integrate Fee');                      
    
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

