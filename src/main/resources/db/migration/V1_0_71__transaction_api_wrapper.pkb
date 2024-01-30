CREATE OR REPLACE PACKAGE BODY ACCT_APP.TRANSACTION_API_WRAPPER IS
  
--------------------------------------------------------------------------------------------------------------------------------
-- Filename : TRANSACTION_API_WRAPPER.pkb
--
--   pkg_version := 1101 [05-AUG-2021]
 
--------------------------------------------------------------------------------------------------------------------------------
-- Purpose  : Exposes Willow's core functionality around accounting related transactions to accounting microservices.

--Input 
--Output

-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -----------------------------------------------------------------
-- 28-JUN-2021    LAFS-2036                  Wayne S.          Initial
-- 05-AUG-2021                               Wayne S.          Remove process_payment as payment integration is no 
--                                                             longer needed. 
-- 03-SEPT-2021   LAFS-3367                  Wayne S.          Add process_invoice_group_complete
--------------------------------------------------------------------------------------------------------------------------------

---------------------------------------------------------------------------------------------
-- Purpose  : Call Willow's API to process updates made to the client's Credit Memo.
--            
--
-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
-- 09-AUG-2021    LAFS-2036, 2818            Wayne S.          Initial
-- 26-AUG-2021    LAFS-3245                  Wayne S.          Handle wrong acct period exception; log in w2k and suppress for caller.
-- 14-AUG-2021    LAFS-3245                  Wayne S.          Impelemented the change to call updateDepositApplicationInXslt
-- 05-OCT-2021    LAFS-3442                  Wayne S.          Update ungrouped credit memo group number with transactionNumber instead of tranId
-- 04-APR-2021    LAFS-4418                  Wayne S.          Handle exception -20012, don't raise to caller
----------------------------------------------------------------------------------------------------------------------------------------------------------
  PROCEDURE process_client_credit_memo(pi_client_credit_memo IN BLOB) IS
    v_je                 JSON_ELEMENT_T;
    v_jo                 JSON_OBJECT_T;
    
    EXCP_WRONG_PERIOD           EXCEPTION;
    EXCP_NO_PRINT_RUN_HEADER    EXCEPTION;
    
    PRAGMA EXCEPTION_INIT(EXCP_WRONG_PERIOD, -20011);
    PRAGMA EXCEPTION_INIT(EXCP_NO_PRINT_RUN_HEADER, -20012);

  BEGIN
    IF(pi_client_credit_memo IS NOT JSON) THEN
      RAISE_APPLICATION_ERROR(-20000, 'Input is not JSON');    
    END IF;

    v_je := JSON_ELEMENT_T.parse(pi_client_credit_memo);

    IF(NOT v_je.is_object) THEN
      RAISE_APPLICATION_ERROR(-20000, 'JSON input is an array. Array is not supported');
    END IF;       

    v_jo := treat(v_je AS JSON_OBJECT_T);
    
    --Only call this when the invoice is ungrouped
    IF v_jo.get_string('groupNumber') IS NULL OR lower(v_jo.get_string('groupNumber')) = 'null' OR v_jo.get_string('groupNumber') = '' THEN    
      acc_billing_generate_data.updateGroupNo(
        v_jo.get_number('docId'), 
        v_jo.get_number('docLineId'), 
        v_jo.get_string('transactionNumber'));
        
      acc_billing_generate_data.updateDepositApplicationInXslt(
          v_jo.get_number('docId'), 
          v_jo.get_number('docLineId'), 
          v_jo.get_number('total'));
    ELSE 
      acc_billing_generate_data.updateGroupNo(
        v_jo.get_number('docId'), 
        v_jo.get_number('docLineId'), 
        v_jo.get_string('groupNumber'));    
    END IF;

  EXCEPTION 
    WHEN EXCP_WRONG_PERIOD THEN
      wms.raise_program_alert ('acct_app.transaction_api_wrapper.process_client_credit_memo', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));    
    WHEN EXCP_NO_PRINT_RUN_HEADER THEN
      wms.raise_program_alert ('acct_app.transaction_api_wrapper.process_client_credit_memo', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));          
    WHEN OTHERS THEN
      wms.raise_program_alert ('acct_app.transaction_api_wrapper.process_client_credit_memo', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));
      RAISE;
  END process_client_credit_memo;
  
---------------------------------------------------------------------------------------------
-- Purpose  : Call Willow's API to update the Group No. on all the client's invoices for the 
--            given billing period. It is safe to assume that when the procedure is called that 
--            grouping of the client's invoice is complete in the external accounting system.
--            
--
-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
-- 28-JUN-2021    LAFS-2036, 2818            Wayne S.          Initial
-- 06-AUG-2021    LAFS-2036, 2818            Wayne S.          Call stub, acc_billing_generate_data.updateGroupNo
----------------------------------------------------------------------------------------------------------------------------------------------------------
  PROCEDURE process_invoice_group(pi_invoice_group IN BLOB) IS
    v_je                 JSON_ELEMENT_T;
    v_jo                 JSON_OBJECT_T;

  BEGIN
    IF(pi_invoice_group IS NOT JSON) THEN
      RAISE_APPLICATION_ERROR(-20000, 'Input is not JSON');    
    END IF;

    v_je := JSON_ELEMENT_T.parse(pi_invoice_group);

    IF(NOT v_je.is_object) THEN
      RAISE_APPLICATION_ERROR(-20000, 'JSON input is an array. Array is not supported');
    END IF;       

    v_jo := treat(v_je AS JSON_OBJECT_T);

    acc_billing_generate_data.updateGroupNo(
        v_jo.get_string('clientAccountCode'), 
        to_char(v_jo.get_date('accountingPeriodDate'), 'YYYY'), 
        to_char(v_jo.get_date('accountingPeriodDate'), 'MON'), 
        v_jo.get_string('groupNumber'));

  EXCEPTION 
    WHEN OTHERS THEN
      wms.raise_program_alert ('acct_app.transaction_api_wrapper.process_invoice_group', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));
      RAISE;
  END process_invoice_group;

---------------------------------------------------------------------------------------------
-- Purpose  : Call Willow's API to 'Dist Complete' invoicing It is safe to assume that when 
--            the procedure is called that grouping of the all client invoices have been 
--            completed in the external accounting system.
--            
--
-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
-- 03-SEP-2021    LAFS-3367                  Wayne S.          Initial
----------------------------------------------------------------------------------------------------------------------------------------------------------
  PROCEDURE process_invoice_group_complete(pi_data IN BLOB) IS
    v_je                 JSON_ELEMENT_T;
    v_jo                 JSON_OBJECT_T;

  BEGIN
    IF(pi_data IS NOT JSON) THEN
      RAISE_APPLICATION_ERROR(-20000, 'Input is not JSON');    
    END IF;

    v_je := JSON_ELEMENT_T.parse(pi_data);

    IF(NOT v_je.is_object) THEN
      RAISE_APPLICATION_ERROR(-20000, 'JSON input is an array. Array is not supported');
    END IF;       

    v_jo := treat(v_je AS JSON_OBJECT_T);

    acc_billing_generate_data.distCompleteInvoiceRun();

  EXCEPTION 
    WHEN OTHERS THEN
      wms.raise_program_alert ('acct_app.transaction_api_wrapper.process_invoice_group_complete', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));
      RAISE;
  END process_invoice_group_complete;  

---------------------------------------------------------------------------------------------
-- Purpose  : Call Willow's API to update the grouped invoice with the deposit amount applied.
--            
--
-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
-- 17-AUG-2021    LAFS-3245                  Wayne S.          Initial
----------------------------------------------------------------------------------------------------------------------------------------------------------
  PROCEDURE process_client_deposit(pi_client_deposit IN BLOB) IS
    v_je                 JSON_ELEMENT_T;
    v_jo                 JSON_OBJECT_T;

  BEGIN
    IF(pi_client_deposit IS NOT JSON) THEN
      RAISE_APPLICATION_ERROR(-20000, 'Input is not JSON');    
    END IF;

    v_je := JSON_ELEMENT_T.parse(pi_client_deposit);

    IF(NOT v_je.is_object) THEN
      RAISE_APPLICATION_ERROR(-20000, 'JSON input is an array. Array is not supported');
    END IF;       

    v_jo := treat(v_je AS JSON_OBJECT_T);

    acc_billing_generate_data.updateDepositApplicationInXslt(
        v_jo.get_number('docId'), 
        v_jo.get_number('docLineId'), 
        v_jo.get_number('amountApplied'));

  EXCEPTION 
    WHEN OTHERS THEN
      wms.raise_program_alert ('acct_app.transaction_api_wrapper.process_client_deposit', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));
      RAISE;
  END process_client_deposit;   
  
---------------------------------------------------------------------------------------------
-- Purpose  : Call Willow's API to update the Group No. on all the client's invoices for the 
--            given billing period. It is safe to assume that when the procedure is called that 
--            grouping of the client's invoice is complete in the external accounting system.
--            
--
-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
-- 19-AUG-2021    LAFS-3299                  Saket M.          Initial
-- 21-SEP-2021    LAFS-3433                  Saket M.          Removed lineid from the arguments
----------------------------------------------------------------------------------------------------------------------------------------------------------
  PROCEDURE process_disposal_invoice(pi_disposal_invoice_group IN BLOB) IS
    v_je                 JSON_ELEMENT_T;
    v_jo                 JSON_OBJECT_T;

  BEGIN
    IF(pi_disposal_invoice_group IS NOT JSON) THEN
      RAISE_APPLICATION_ERROR(-20000, 'Input is not JSON');    
    END IF;

    v_je := JSON_ELEMENT_T.parse(pi_disposal_invoice_group);

    IF(NOT v_je.is_object) THEN
      RAISE_APPLICATION_ERROR(-20000, 'JSON input is an array. Array is not supported');
    END IF;       

    v_jo := treat(v_je AS JSON_OBJECT_T);

    accounting_integration.processDisposalInvoice(
        v_jo.get_string('docId'), 
        v_jo.get_string('status'), 
        v_jo.get_date('paidInFullDate'));

  EXCEPTION 
    WHEN OTHERS THEN
      wms.raise_program_alert ('acct_app.transaction_api_wrapper.process_disposal_invoice', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));
      RAISE;
  END process_disposal_invoice;


END TRANSACTION_API_WRAPPER;
/