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
--------------------------------------------------------------------------------------------------------------------------------

--   pkg_version := 1100 [28-JUN-2021]

  PROCEDURE process_payment(pi_payment IN BLOB , po_doc_id OUT NUMBER);
  PROCEDURE process_invoice_group(pi_invoice_group IN BLOB);

END TRANSACTION_API_WRAPPER;
/

