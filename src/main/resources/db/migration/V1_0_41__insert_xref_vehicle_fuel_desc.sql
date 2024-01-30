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
-- 1100         30-Apr-2021    LAFS-2061                  Saket M.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'VEHICLE_FUEL_DESC'; 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'VEHICLE_FUEL_DESC'; 
    COMMIT;
  END IF;


  INSERT INTO xref(group_name, internal_value, external_value) VALUES('VEHICLE_FUEL_DESC', 'DIESEL', 'Diesel');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('VEHICLE_FUEL_DESC', 'ELECTRIC', 'Electric');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('VEHICLE_FUEL_DESC', 'GASOLINE HYBRID', 'Hybrid');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('VEHICLE_FUEL_DESC', 'NULL', 'NULL');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('VEHICLE_FUEL_DESC', '-', 'NULL');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('VEHICLE_FUEL_DESC', 'UNAVAILABLE', 'NULL');

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

