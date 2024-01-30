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
-- 1100         05-Jan-2022    LAFS-4355                  Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
BEGIN

  UPDATE xref
    SET external_value = 'Non Rebillable - Fines'
    WHERE group_name = 'INVOICE-ITEM'
      AND internal_value = '{"controlCode":"FLFINE","index":4}';

  COMMIT;           

END;
/