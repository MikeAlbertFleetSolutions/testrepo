DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_canada_province.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Inserts the CANADA_PROVINCE group mapping(s) into the xref table.
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         01-Feb-2021    LAFS-1903                  Wayne S.          Add item for PQ (W2k) to QC (NS) mapping
--              02-Feb-2021    LAFS-1903                  Wayne S.          Add items for NW to NT and NF to NL
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CANADA_PROVINCE';

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CANADA_PROVINCE'; 
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CANADA_PROVINCE', 'PQ', 'QC');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CANADA_PROVINCE', 'NW', 'NT');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CANADA_PROVINCE', 'NF', 'NL');  
    
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

