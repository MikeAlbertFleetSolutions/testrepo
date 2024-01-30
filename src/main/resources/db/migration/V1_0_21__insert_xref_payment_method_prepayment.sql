DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Inserts new payment method mapping, Prepayment
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         31-Aug-2020    LAFS-1178                  Wayne S.          Add inserts
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count         NUMBER := 0;
  C_GROUP_NAME      CONSTANT xref.external_value%TYPE := 'PAYMENT_METHOD';  
  C_EXTERNAL_VALUE  CONSTANT xref.external_value%TYPE := 'Prepayment';
  C_INTERNAL_VALUE  CONSTANT xref.external_value%TYPE := 'WIRE';  
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE external_value = C_EXTERNAL_VALUE
      AND group_name IN (C_GROUP_NAME);     

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE external_value = C_EXTERNAL_VALUE
        AND group_name IN (C_GROUP_NAME); 
    COMMIT;
  END IF;                       
    
  INSERT INTO xref(group_name, internal_value, external_value) VALUES(C_GROUP_NAME, C_INTERNAL_VALUE, C_EXTERNAL_VALUE);
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

