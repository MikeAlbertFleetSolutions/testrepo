DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Adds API-COUNTRY xref entries
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         15-Apr-2021    LAFS-405                   Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'API-COUNTRY'; 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'API-COUNTRY';
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('API-COUNTRY', 'CN', '_canada');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('API-COUNTRY', 'USA', '_unitedStates');


                        

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

