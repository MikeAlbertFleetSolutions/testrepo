DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql 
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Remap invoice AP items
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         07-Dec-2021    LAFS-3443                  Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
BEGIN

  UPDATE xref
    SET external_value = 'Rebillable - Fines'
    WHERE group_name = 'INVOICE-ITEM'
      AND internal_value = '{"controlCode":"FLFINE","index":3}';

  UPDATE xref
    SET external_value = 'Rebillable - Title, License, Tax'
    WHERE group_name = 'INVOICE-ITEM'
      AND internal_value = '{"controlCode":"INVOICE","index":0}'; 

  UPDATE xref
    SET external_value = 'Rebillable - Title, License, Tax'
    WHERE group_name = 'INVOICE-ITEM'
      AND internal_value = '{"controlCode":"FLLICENSE","index":3}';

  UPDATE xref
    SET external_value = 'Non Rebillable - Title, License, Tax'
    WHERE group_name = 'INVOICE-ITEM'
      AND internal_value = '{"controlCode":"FLLICENSE","index":4}'; 

  COMMIT;           

END;
/