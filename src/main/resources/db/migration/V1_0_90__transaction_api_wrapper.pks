CREATE OR REPLACE PACKAGE ACCT_APP.TRANSACTION_API_WRAPPER IS
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
-- 1102         19-AUG-2021    LAFS-3299                  Saket M           Add process_disposal_invoice procedure to process disposal data
-- 1103         03-SEP-2021    LAFS-3367                  Wayne S.          Added process_invoice_group_complete
-- 1104         04-DEC-2022    LAFS-10141                 Praveen k         Added process_close_po_inbound
--------------------------------------------------------------------------------------------------------------------------------

--   pkg_version := 1101 [05-AUG-2021]

  PROCEDURE process_client_credit_memo(pi_client_credit_memo IN BLOB);
  
  PROCEDURE process_invoice_group(pi_invoice_group IN BLOB);

  PROCEDURE process_client_deposit(pi_client_deposit IN BLOB); 

  PROCEDURE process_disposal_invoice(pi_disposal_invoice_group IN BLOB);

  PROCEDURE process_invoice_group_complete(pi_data IN BLOB);  
  
  PROCEDURE process_close_po_inbound(pi_doc_id IN NUMBER);

  PROCEDURE get_lease_type(v_unit_no IN fleet_masters.unit_no%type,  v_lease_type OUT VARCHAR2 );

END TRANSACTION_API_WRAPPER;
/