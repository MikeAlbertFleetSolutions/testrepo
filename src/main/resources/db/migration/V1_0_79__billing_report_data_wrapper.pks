CREATE OR REPLACE PACKAGE ACCT_APP.BILLING_REPORT_DATA_WRAPPER IS
--------------------------------------------------------------------------------------------------------------------------------
-- Filename : TRANSACTION_API_WRAPPER.pks
--
-- Purpose  : Exposes Willow's core functionality around accounting related transactions to accounting'S microservices.
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      ----------------------------------------------------
-- 1100         02-JUN-2022    LAFS-9839                  Wayne S.          Initial
-- 1101         11-AUG-2022    LAFS-9839                  Wayne S.          Add pi_force_yn parameter
--------------------------------------------------------------------------------------------------------------------------------

--   pkg_version := 1101 [11-AUG-2022]

  C_PACKAGE_NAME             CONSTANT        VARCHAR2(50) := 'ACCT_APP.BILLING_REPORT_DATA_WRAPPER';

  PROCEDURE merge_willow_data(pi_account_code IN VARCHAR2, pi_accounting_period IN VARCHAR2, pi_force_yn IN VARCHAR2);


END BILLING_REPORT_DATA_WRAPPER;
/
