DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : DB_SCRIPT.sql
-- Naming Convention : ins_xref_company.sql
--
-- Purpose  : Inserts the W2K to NS company mapping
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         11-AUG-2020    LAFS-1106                  Saket M.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  C_GROUP_NAME    CONSTANT   VARCHAR2(200) := UPPER('ASSET-DEP');


BEGIN 

DELETE FROM ACCT_APP.XREF WHERE GROUP_NAME = C_GROUP_NAME;

INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'DE-DE', 'Company Vehicles - Selling Depts');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'DE-DEMO', 'Company Vehicles - Selling Depts');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'FL-CE_LTD', 'Fleet-Closed End');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'FL-MAN_RENT', 'Fleet-Closed End');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'FL-REGM_LTD', 'Fleet-Closed End');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'FL-OE_EQUIP', 'Fleet-Equip');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'FL-OE_IRENT', 'Fleet-Open End');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'FL-OE_LTD', 'Fleet-Open End');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'ST-ST', 'Fleet-Rental');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'INV-INV_EXCLUS', 'Inventory');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'INV-INVENTORY', 'Inventory');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'LT-CE_LTD', 'Long Term Inventory');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'LT-DE', 'Long Term Inventory');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'LT-INV_EXCLUS', 'Long Term Inventory');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'LT-INVENTORY', 'Long Term Inventory');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'LT-LT_LTD', 'Long Term Inventory');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'LT-OE_IRENT', 'Long Term Inventory');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'LT-OE_LTD', 'Long Term Inventory');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'LT-ST', 'Long Term Inventory - Rental');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'UC-CE_LTD', 'Used Cars');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'UC-DE', 'Used Cars');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'UC-DEMO', 'Used Cars');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'UC-INVENTORY', 'Used Cars');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'UC-MAN_RENT', 'Used Cars');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'UC-OE_IRENT', 'Used Cars');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'UC-OE_LTD', 'Used Cars');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'UC-REGM_LTD', 'Used Cars');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'UC-ST', 'Used Cars-Rental');
INSERT INTO ACCT_APP.XREF(GROUP_NAME, INTERNAL_VALUE, EXTERNAL_VALUE)     VALUES(C_GROUP_NAME, 'ST-INVENTORY', 'Fleet-Rental');

COMMIT;

END;

/

