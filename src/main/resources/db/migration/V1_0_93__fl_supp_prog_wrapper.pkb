CREATE OR REPLACE PACKAGE BODY ACCT_APP.FL_SUPP_PROG_WRAPPER IS
  C_MBSI_DRAFT           CONSTANT  willow_config.config_name%TYPE  := 'MBSI_DRAFT';
  C_MBSI_CHECK           CONSTANT  willow_config.config_name%TYPE  := 'MBSI_CHECK';
  
  C_PAYMENT_METHOD_WIRE  CONSTANT  external_accounts.payment_method%TYPE  := 'WIRE';  
  
--------------------------------------------------------------------------------------------------------------------------------
-- Filename : FL_SUPP_PROG_WRAPPER.pkb
--
--   pkg_version := 1100 [14-SEP-2020]
 
--------------------------------------------------------------------------------------------------------------------------------
-- Purpose  : Exposes Willow's core functionality around supplier progress history to accounting microservices.

--Input 
--Output

-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -----------------------------------------------------------------
-- 14-SEP-2020   LAFS Phase 2.1              Wayne S.          Initial
--------------------------------------------------------------------------------------------------------------------------------


---------------------------------------------------------------------------------------------
-- Purpose  : Logs vehicle payment to the supplier progress history, which is used by the 
--            business to track the progress of acquiring a vehicle.
--
-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
-- 14-SEP-2020    Phase 2.1                  Wayne S.          Initial
----------------------------------------------------------------------------------------------------------------------------------------------------------
  PROCEDURE cfg_sup_prog_ins(
    pi_doc_id                  IN               supplier_progress_history.doc_id%TYPE,
    pi_payment_date            IN               supplier_progress_history.action_date%TYPE,
    pi_payment_method          IN               external_accounts.payment_method%TYPE) AS
  ---------------------------------------------------------------------------------------------
    --Declare Section
    v_doc          doc%ROWTYPE;
    v_config_name  willow_config.config_name%TYPE;
    v_error_text   VARCHAR2(2001);
  BEGIN
    --Business Logic
    SELECT d.*
      INTO v_doc
      FROM doc d
      WHERE d.doc_id = pi_doc_id;
      
    IF pi_payment_method = C_PAYMENT_METHOD_WIRE THEN
      v_config_name := C_MBSI_DRAFT;
    ELSE
      v_config_name := C_MBSI_CHECK;
    END IF;
    
    fl_supp_prog.cfg_sup_prog_ins(v_doc.c_id, v_config_name, v_doc.doc_id, 'NETSUITE', pi_payment_date, pi_payment_date, v_error_text);    

  EXCEPTION
      WHEN OTHERS THEN
          willow2k.wms.raise_program_alert ('acct_app.fl_supp_prog_wrapper.cfg_sup_prog_ins',
                  'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE) || 'pi_doc_id = ' || pi_doc_id);
          RAISE;
  END cfg_sup_prog_ins;
  
  ---------------------------------------------------------------------------------------------
	-- Purpose  : Call to check is vehicle paid or not 
	--            
	--
	-- Modification history
	--
	-- Version Date   Log Number                 Changed By        Description/Reason for change
	-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
	-- 12-DEC-2022    LAFS-10141                 Praveen k         Initial
	----------------------------------------------------------------------------------------------------------------------------------------------------------

  
  PROCEDURE is_vehicle_paid(pi_fms_id IN NUMBER, is_paid OUT VARCHAR2 ) IS
	BEGIN
	    is_paid := quotation_mgr.is_vehicle_paid(pi_fms_id);    
	EXCEPTION
      WHEN OTHERS THEN
          willow2k.wms.raise_program_alert ('acct_app.fl_supp_prog_wrapper.is_vehicle_paid',
                  'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE) || 'pi_fms_id = ' || pi_fms_id);
          RAISE;	 
	  
	END is_vehicle_paid;

END FL_SUPP_PROG_WRAPPER;
/


