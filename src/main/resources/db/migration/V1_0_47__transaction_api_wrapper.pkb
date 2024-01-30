CREATE OR REPLACE PACKAGE BODY TRANSACTION_API_WRAPPER IS
  
--------------------------------------------------------------------------------------------------------------------------------
-- Filename : TRANSACTION_API_WRAPPER.pkb
--
--   pkg_version := 1100 [28-JUN-2021]
 
--------------------------------------------------------------------------------------------------------------------------------
-- Purpose  : Exposes Willow's core functionality around accounting related transactions to accounting microservices.

--Input 
--Output

-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -----------------------------------------------------------------
-- 28-JUN-2021    LAFS-2036                  Wayne S.          Initial
--------------------------------------------------------------------------------------------------------------------------------

---------------------------------------------------------------------------------------------
-- Purpose  : Call Willow's API to create 'RECEIPT' doc and related records. 
--            
--
-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
-- 28-JUN-2021    LAFS-2036                  Wayne S.          Initial
----------------------------------------------------------------------------------------------------------------------------------------------------------
  PROCEDURE process_payment(pi_payment IN BLOB , 
                            po_doc_id OUT NUMBER) IS
    v_je                 JSON_ELEMENT_T;
    v_jo                 JSON_OBJECT_T;
    v_client_payment_ot  CLIENT_PAYMENT_OT;
    
    v_doc_no  doc.doc_no%TYPE;
    
  BEGIN
    IF(pi_payment IS NOT JSON) THEN
      RAISE_APPLICATION_ERROR(-20000, 'Input is not JSON');    
    END IF;
    
    v_je := JSON_ELEMENT_T.parse(pi_payment);

    IF(NOT v_je.is_object) THEN
      RAISE_APPLICATION_ERROR(-20000, 'JSON input is an array. Array is not supported');
    END IF;       
        
    v_jo := treat(v_je AS JSON_OBJECT_T);
    
    v_client_payment_ot := CLIENT_PAYMENT_OT(NULL, NULL, v_jo.get_string('tranId'), v_jo.get_string('clientExternalId'), 
        v_jo.get_date('tranDate'), v_jo.get_number('amount'), v_jo.get_string('memo'), v_jo.get_string('opCode'), null);
    
    if_process_payment.createClientReceipt(v_client_payment_ot); 
    
    po_doc_id := v_client_payment_ot.doc_id;

    proc_aa_test(sysdate || 'txnapi> docId', po_doc_id);
        
  EXCEPTION 
    WHEN OTHERS THEN
      wms.raise_program_alert ('acct_app.transaction_api_wrapper.process_payment', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));
      RAISE;
  END process_payment;

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
            
  EXCEPTION 
    WHEN OTHERS THEN
      wms.raise_program_alert ('acct_app.transaction_api_wrapper.process_payment', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));
      RAISE;
  END process_invoice_group;
  
END TRANSACTION_API_WRAPPER;
/


