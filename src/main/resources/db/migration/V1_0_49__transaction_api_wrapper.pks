CREATE OR REPLACE PACKAGE TRANSACTION_API_WRAPPER IS
--------------------------------------------------------------------------------------------------------------------------------
-- Filename : TRANSACTION_API_WRAPPER.pks
--
-- Purpose  : Exposes Willow's core functionality around accounting related transactions to accounting microservices.
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      ----------------------------------------------------
-- 1100         28-JUN-2021    LAFS-2036                  Wayne S.          Initial
-- 1101         05-AUG-2021                               Wayne S.          Remove process_payment as payment integration is no 
--                                                                          longer needed.
--------------------------------------------------------------------------------------------------------------------------------

--   pkg_version := 1101 [05-AUG-2021]

  PROCEDURE process_client_credit_memo(pi_client_credit_memo IN BLOB);
  
  PROCEDURE process_invoice_group(pi_invoice_group IN BLOB);

  PROCEDURE process_client_deposit(pi_client_deposit IN BLOB); 

END TRANSACTION_API_WRAPPER;
/

