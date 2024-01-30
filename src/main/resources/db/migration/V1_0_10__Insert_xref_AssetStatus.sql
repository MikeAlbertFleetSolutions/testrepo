DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Inserts the po-item mappings into xref
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         04-June-2020    LAFS                       Saket M.          Creating a new script to map NS Asset Status to Willow Asset Status.
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
  v_group_name VARCHAR2(30) := 'ASSET-STATUS'; 
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = v_group_name;

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = v_group_name; 
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES(v_group_name, 'I', '1'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES(v_group_name, 'A', '2'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES(v_group_name, 'F', '3'); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES(v_group_name, 'D', '4'); 
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

