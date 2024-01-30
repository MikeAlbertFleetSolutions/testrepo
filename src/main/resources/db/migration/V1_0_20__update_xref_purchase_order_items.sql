DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: **** SCRIPT HAS TO BE RE-EXECUTABLE ****
--
-- Filename : vn_n_n__Insert_xref_po_item.sql
-- Naming Convention : TaskNumber_Purpose.sql
--
-- Purpose  : Updates the purchase order and invoice item mapping external value for GRD_INTRNL
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         21-Aug-2020    LAFS-1108                   Wayne S.          Add inserts
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  v_old_po_count    NUMBER := 0;
  v_new_po_count    NUMBER := 0;
  v_invoice_count   NUMBER := 0;
BEGIN
  SELECT count(1)
    INTO v_old_po_count
    FROM xref
    WHERE internal_value LIKE '%GRD_INTRNL%'
      AND group_name in ('PO-ITEM');
      
  SELECT count(1)
    INTO v_new_po_count
    FROM xref
    WHERE internal_value LIKE '%GRD_INTRNL%'
      AND group_name in ( 'PURCHASE-ORDER-ITEM');
      
  SELECT count(1)
    INTO v_invoice_count
    FROM xref
    WHERE internal_value LIKE '%GRD_INTRNL%'
      AND group_name in ('INVOICE-ITEM');      

  IF v_old_po_count > 0 THEN
    UPDATE xref
        SET external_value = 'Delivery - Internal PDI'
      WHERE internal_value LIKE '%GRD_INTRNL%'
        AND group_name in ('PO-ITEM');   
  END IF;
  
  IF v_new_po_count > 0 THEN
    UPDATE xref
        SET external_value = 'Delivery - Internal PDI'
      WHERE internal_value LIKE '%GRD_INTRNL%'
        AND group_name in ('PURCHASE-ORDER-ITEM');   
  END IF;
  
  IF v_invoice_count > 0 THEN
    UPDATE xref
        SET external_value = 'Delivery - Internal PDI'
      WHERE internal_value LIKE '%GRD_INTRNL%'
        AND group_name in ('INVOICE-ITEM');   
  END IF;                       
    
  COMMIT;
      
  DBMS_OUTPUT.PUT_LINE ('Data Inserted Successfully');

END;
/

