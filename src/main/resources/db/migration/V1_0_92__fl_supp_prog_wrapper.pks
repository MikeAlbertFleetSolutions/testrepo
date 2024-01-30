CREATE OR REPLACE PACKAGE ACCT_APP.FL_SUPP_PROG_WRAPPER IS
--------------------------------------------------------------------------------------------------------------------------------
-- Filename : FL_SUPP_PROG_WRAPPER.pks
--
-- Purpose  : Exposes Willow's core functionality around supplier progress history to accounting microservices.
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      ----------------------------------------------------
-- 1100         14-SEP-2020    LAFS 2.1                    Wayne S.          Initial
-- 1101         12-DEC-2022    LAFS 2.1                    Praveen K.          Initial
--------------------------------------------------------------------------------------------------------------------------------

--   pkg_version := 1100 [02-SEPT-2020]

  PROCEDURE cfg_sup_prog_ins(
    pi_doc_id                  IN               supplier_progress_history.doc_id%TYPE,
    pi_payment_date            IN               supplier_progress_history.action_date%TYPE,
    pi_payment_method          IN               external_accounts.payment_method%TYPE);
    
  
  PROCEDURE is_vehicle_paid(pi_fms_id IN NUMBER, is_paid OUT VARCHAR2 );  

END FL_SUPP_PROG_WRAPPER;
/

