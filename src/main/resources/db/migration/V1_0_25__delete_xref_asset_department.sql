DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Accounting renamed item 'Maintenance - Company Vehicle' to 'Maintenance - Company Vehicle (G&A)'. This script makes the same change in XREF
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         03-Nov-2020    LAFS-1011                  Saket M.          Delete ASSET-DEPARTMENT data from XREF as we will get NS internalId from NS
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
BEGIN
    DELETE FROM xref
      WHERE group_name = 'ASSET-DEPARTMENT';
    
    COMMIT;

    DBMS_OUTPUT.PUT_LINE ('Asset Detpartment data deleted from Xref Successfully');
    
END;
/

