CREATE OR REPLACE PACKAGE BODY ACCT_APP.BILLING_REPORT_DATA_WRAPPER IS
---------------------------------------------------------------------------------------------
-- Filename : BILLING_REPORT_DATA_WRAPPER.pkb
--
--   pkg_version := 1100 [02-JUN-2022]
 
---------------------------------------------------------------------------------------------
-- Purpose  : Exposes Willow's core functionality around accounting related transactions to accounting'S microservices.

-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
-- 02-JUN-2022    LAFS-9839                  Wayne S.          Initial
----------------------------------------------------------------------------------------------------------------------------------------------------------


  ---------------------------------------------------------------------------------------------
  -- Purpose  : Call to merge Willow's data into the client's billing transactions for a specific billing period
  --            
  --
  -- Modification history
  --
  -- Version Date   Log Number                 Changed By        Description/Reason for change
  -- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
  -- 02-JUN-2022    LAFS-9839                  Wayne S.          Initial
  ----------------------------------------------------------------------------------------------------------------------------------------------------------
  PROCEDURE merge_willow_data(pi_account_code IN VARCHAR2, pi_billing_period IN VARCHAR2) IS
  BEGIN
    --billing_report_data.merge_willow_data(pi_account_code, pi_billing_period);
    null;
  EXCEPTION 
    WHEN OTHERS THEN
      wms.raise_program_alert (C_PACKAGE_NAME || 'merge_willow_data', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));
      RAISE;
  END merge_willow_data;

END BILLING_REPORT_DATA_WRAPPER;
/