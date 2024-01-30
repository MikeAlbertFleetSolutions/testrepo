DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Removes all existing maintenance item xrefs and replaces them with "AI from AI" item mapping updates
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         10-Aug-2020    LAFS-1108                   Wayne S.          Delete all existing maint items and replace with updates from items mapping file
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'INVOICE-ITEM'    
      AND internal_value like '%MAINT%'; 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'INVOICE-ITEM'    
        AND internal_value like '%MAINT%'; 
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLMAINT","index":0}', 'Maintenance - Auto Integrate Fee');              
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLMAINT","index":1}', 'Maintenance - Rebillable');      
  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLMAINTNA","index":0}', 'Maintenance - Rebillable');          
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLMAINTNA","index":1}', 'Maintenance - Rental');            

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"DEMOMAINT","index":0}', 'Maintenance - Auto Integrate Fee');              
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"DEMOMAINT","index":1}', 'Maintenance - Company Vehicle');          
  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"STMAINT","index":0}', 'Maintenance - Auto Integrate Fee');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"STMAINT","index":1}', 'Maintenance - Rental');          
  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"UCMAINT","index":0}', 'Maintenance - Auto Integrate Fee');          
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"UCMAINT","index":1}', 'Maint (UCMaint) - Used Vehicle');                
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"UCMAINT","index":2}', 'Maint (UCMaint) - Disposed Vehicle (CY)');          
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"UCMAINT","index":3}', 'Maint (UCMaint) - Disposed Vehicle (PY)');                  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"UCMAINT","index":4}', 'Maint (UCMaint) - Fleet');                    
    
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

