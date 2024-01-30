DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--sql
-- Filename : v1_0_81__update_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Inserts the po-item mappings into xref
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         19-Aug-2022    LAFS-10064                 Wayne S.          Update 'A' mapping as NetGain upgrade changed the internal id to 7
-- 1101         23-Aug-2022    LAFS-10064                 Wayne S.          Update 'A' mapping as NetGain upgrade changed the internal ids to 2, 6, 7
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count                 NUMBER := 0;
  v_group_name            VARCHAR2(200) := 'ASSET-STATUS';
  v_new_internal_value    VARCHAR2(200) := 'A';
  v_ext_in_service_val    VARCHAR2(200) := '2';
  v_ext_capitalized_val   VARCHAR2(200) := '6';  
  v_ext_ready_to_cap_val  VARCHAR2(200) := '7';
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = v_group_name
      AND external_value IN (v_ext_in_service_val, v_ext_ready_to_cap_val, v_ext_capitalized_val);

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = v_group_name
        and external_value IN (v_ext_in_service_val, v_ext_ready_to_cap_val, v_ext_capitalized_val);
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES(v_group_name, v_new_internal_value, v_ext_in_service_val); 
  INSERT INTO xref(group_name, internal_value, external_value) VALUES(v_group_name, v_new_internal_value, v_ext_ready_to_cap_val);   
  INSERT INTO xref(group_name, internal_value, external_value) VALUES(v_group_name, v_new_internal_value, v_ext_capitalized_val);     
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/