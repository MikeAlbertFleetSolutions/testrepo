DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__insert_xref_client_invoice_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Remap 01560005500 and 02560005500 to Disposal - Auction Fees
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         29-Aug-2022    LAFS-10081                 Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'CLIENT-INVOICE-ITEM'
      AND internal_value IN ('01610004100', '01610005100', '01610006100', '01700005100');      

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value IN ('01610004100', '01610005100', '01610006100', '01700005100');      

    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','01610004100', '_LeaseCall_InRate');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','01610005100', 'Rebillable - Sales Tax');  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','01610006100', 'Rebillable - Third-Party Pass-Thru');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('CLIENT-INVOICE-ITEM','01700005100', 'Rebillable - Property Taxes');    
  
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

