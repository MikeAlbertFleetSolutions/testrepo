DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Adds CLIENT-CREDIT-STATUS xref entries
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
    WHERE group_name = 'CLIENT-CREDIT-STATUS'; 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CLIENT-CREDIT-STATUS';
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CREDIT-STATUS', 'A', 'Approved');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CREDIT-STATUS', 'R', 'Review Required');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CREDIT-STATUS', 'U', 'Unapproved');    

                        

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

