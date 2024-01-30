DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Adds CLIENT-CREDIT-MANAGEMENT-TYPE xref entries
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         13-Apr-2021    LAFS-405                   Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CLIENT-CREDIT-MANAGEMENT-TYPE'; 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CLIENT-CREDIT-MANAGEMENT-TYPE';
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CREDIT-MANAGEMENT-TYPE', 'S', 'Hierarchy / Share Limits');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CREDIT-MANAGEMENT-TYPE', 'I', 'No Hierarchy');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CREDIT-MANAGEMENT-TYPE', 'P', 'Parent');    


                        

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

