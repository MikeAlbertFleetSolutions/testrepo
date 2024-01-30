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
-- 1100         01-Mar-2021    LAFS-1264                   Wayne S.          Initial
--              05-Apr-2-21    LAFS-1264                   Wayne S.          Re-lablel the new items: selling and GA
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_count
    FROM xref
    WHERE group_name = 'INVOICE-ITEM'    
      AND (internal_value like '%DEMOMAINT%' OR internal_value like '%FLFINE%' OR internal_value like '%FLLICENSE%'); 

  IF v_count > 0 THEN
    DELETE FROM xref
      WHERE group_name = 'INVOICE-ITEM'    
        AND (internal_value like '%DEMOMAINT%' OR internal_value like '%FLFINE%' OR internal_value like '%FLLICENSE%'); 
    COMMIT;
  END IF;

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLFINE","index":0}', 'Fines - Rental');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLFINE","index":1}', 'Fines - Company Vehicle (Selling)');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLFINE","index":2}', 'Fines - Company Vehicle (G&A)');  
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLFINE","index":3}', 'Fines - Rebillable');    
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLFINE","index":4}', 'Fines - Non Rebillable');      
  
    
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"DEMOMAINT","index":0}', 'Maintenance - Auto Integrate Fee');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"DEMOMAINT","index":1}', 'Maintenance - Company Vehicle (Selling)');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"DEMOMAINT","index":2}', 'Maintenance - Company Vehicle (G&A)');  

  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":0}', 'Title & License - Company Vehicle (Selling)');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":1}', 'Title & License - Company Vehicle (G&A)');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":2}', 'Sales Tax - Driver Sales');
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":3}', 'Title & License - Rebillable');    
  INSERT INTO xref(group_name, internal_value, external_value) VALUES('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":4}', 'Title & License - Non Rebillable');          

  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

