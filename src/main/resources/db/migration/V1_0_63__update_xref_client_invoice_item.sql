DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__update_xref_client_invoice_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Rename an existing item
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         22-Sep-2021    LAFS-3688                   Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
BEGIN
      
    UPDATE xref
      SET external_value = 'Reclaims'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02150001000';
        
    UPDATE xref
      SET external_value = 'Fee - Other Fleet Services'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400015101';      
        
    UPDATE xref
      SET external_value = 'Fee - AutoTag'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400016101';              
        
    UPDATE xref
      SET external_value = 'Fee - Other Fleet Services'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400060101';        
        
    UPDATE xref
      SET external_value = 'Fee - Other Fleet Services'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400061101';              
        
    UPDATE xref
      SET external_value = 'Fee - New Vehicle'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400063101';
        
    UPDATE xref
      SET external_value = 'Fee - Other Fleet Services'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400064101';              
        
    UPDATE xref
      SET external_value = 'Fee - Other Fleet Services'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400065101';
        
    UPDATE xref
      SET external_value = 'Fee - AutoTag'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400070101';              
        
    UPDATE xref
      SET external_value = 'Open End - Lease Revenue'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400101100';
        
    UPDATE xref
      SET external_value = 'Open End - Lease Revenue'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02400102100';              
        
    UPDATE xref
      SET external_value = 'FinChrg'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02470004000';
        
    UPDATE xref
      SET external_value = 'Adjustment - AR Balance'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02490013100';              
        
    UPDATE xref
      SET external_value = 'Disposal - After Sale'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02560001500';        
        
    UPDATE xref
      SET external_value = 'Rebillable - Delivery'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02609003100';              
        
    UPDATE xref
      SET external_value = 'Rebillable - Title, License, Tax'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02610001101';
        
    UPDATE xref
      SET external_value = 'Non Rebillable - Title, License, Tax'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02610002100';              
        
    UPDATE xref
      SET external_value = 'Credit - Goodwill re: Fleet'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02612001100'; 
        
    UPDATE xref
      SET external_value = 'Rebillable - Fines'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02625001100';              
        
    UPDATE xref
      SET external_value = 'Rebillable - Postage'
      WHERE group_name = 'CLIENT-INVOICE-ITEM'
        AND internal_value = '02626003101';         
        
    COMMIT;
          
  DBMS_OUTPUT.PUT_LINE ('Data Updated Successfully');

END;
/

