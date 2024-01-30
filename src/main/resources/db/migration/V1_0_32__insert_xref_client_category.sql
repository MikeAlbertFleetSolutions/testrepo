DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Adds CLIENT_CATEGORY xref entries
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         12-Apr-2021    LAFS-405                   Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CLIENT-CATEGORY'; 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CLIENT-CATEGORY';
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'DRVR_SALES', 'Driver Sales');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'EMPLOYEE', 'Employee');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'FARMERS', 'Farmers');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'FEDEX', 'Fedex Ground');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'FLEET-NO', 'Fleet with No Units');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'FLEET-SPCL', 'Fleet with Special Requirements');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'FLEET-UNIT', 'Fleet with Units');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'INS-BROKER', 'Insurance');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'INSURANCE', 'Insurance');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'MA_LC', 'Purchase Product');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'PROMISSORY', 'Promissory Note');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'PURCHASING', 'Purchase Reclaims');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'WHOLESALE', 'Wholesale Vehicles');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-CATEGORY', 'YELLOW-EXP', 'Yellow Express');
                        

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

