DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__insert_xref_client_invoice_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Map W2K interest type, SOFR, to NetSuite/NetLessor interest rate
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         12-Sep-2022    LAFS-10024                 Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  C_GROUP_NAME              CONSTANT VARCHAR2(200) := 'INTEREST_TYPE';
  C_INTERNAL_VALUE_SOFR     CONSTANT VARCHAR2(200) := 'SOFR';
  C_INTERNAL_VALUE_BASKET   CONSTANT VARCHAR2(200) := 'BASKET L';  
  C_EXTERNAL_VALUE_SOFR     CONSTANT VARCHAR2(200) := '8';  
  C_EXTERNAL_VALUE_BASKET   CONSTANT VARCHAR2(200) := '7';    

  v_count   NUMBER := 0;
  
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = C_GROUP_NAME
      AND internal_value = C_INTERNAL_VALUE_SOFR;      

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = C_GROUP_NAME
        AND internal_value = C_INTERNAL_VALUE_SOFR;

    COMMIT;
  END IF;   

  INSERT INTO xref(group_name, internal_value, external_value) VALUES(C_GROUP_NAME, C_INTERNAL_VALUE_SOFR, C_EXTERNAL_VALUE_SOFR);

  UPDATE xref
    SET external_value = C_EXTERNAL_VALUE_BASKET
    WHERE group_name = C_GROUP_NAME
      AND internal_value = C_INTERNAL_VALUE_BASKET;    
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

