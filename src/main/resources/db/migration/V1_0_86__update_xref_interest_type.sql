DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__update_xref_interest_type.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Update existing interest type, SOFR
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         22-Sep-2022    LAFS-10101                 Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  C_GROUP_NAME              CONSTANT VARCHAR2(200) := 'INTEREST_TYPE';
  C_INTERNAL_VALUE_SOFR     CONSTANT VARCHAR2(200) := 'SOFR';
  C_INTERNAL_VALUE_SOFR_1   CONSTANT VARCHAR2(200) := 'SOFR_1_MO';  
  
BEGIN

  UPDATE xref
    SET internal_value = C_INTERNAL_VALUE_SOFR_1
    WHERE group_name = C_GROUP_NAME
      AND internal_value = C_INTERNAL_VALUE_SOFR;
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

