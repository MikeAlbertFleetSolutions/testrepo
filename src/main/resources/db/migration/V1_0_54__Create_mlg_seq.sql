DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: PLEASE SET START WITH, MINVALUE, MAXVALUE, ETC BASED ON YOUR REQUIREMENTS
--
-- Filename : plg_seq.sql
-- Naming Convention : v1_0_54__Create_mlg_seq.sql
--
-- Purpose  : Sequence for MESSAGE_LOG table
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         02-AUG-2021    LAFS-2874                  Wayne S.          Init
---------------------------------------------------------------------------------------------------------------------------------------------------------------------

  C_SCHEMA_OWNER          CONSTANT   VARCHAR2(100) := UPPER('ACCT_APP');
  C_SEQUENCE_NAME         CONSTANT   VARCHAR2(100) := UPPER('MLG_SEQ');
  C_SCHEMA_SEQUENCE_NAME  CONSTANT   VARCHAR2(200) := C_SCHEMA_OWNER || '.' || C_SEQUENCE_NAME;

  v_exists            INTEGER;
  v_ddl               VARCHAR2(32000);

  --If SEQUENCE exists and you want to drop/recreate it then set it to true else false
  C_SEQ_EXISTS_THEN_DEL       CONSTANT    BOOLEAN := FALSE;
  
BEGIN
  -- Check to see if SEQUENCE exists
  SELECT count(1)
    INTO v_exists
    FROM all_sequences
   WHERE sequence_name = C_SEQUENCE_NAME
     AND sequence_owner = C_SCHEMA_OWNER; 

  IF v_exists = 1 THEN
    --This means SEQUENCE exists
    IF C_SEQ_EXISTS_THEN_DEL THEN
      --Delete existing SEQUENCE
      v_ddl := 'DROP SEQUENCE ' || C_SCHEMA_SEQUENCE_NAME;
      EXECUTE IMMEDIATE v_ddl;
      dbms_output.put_line(v_ddl);
    ELSE
      --Do nothing
      dbms_output.put_line(C_SCHEMA_SEQUENCE_NAME||' already exists. If you wish to recreate then change flag v_seq_exists_then_del to TRUE');
      RETURN;
    END IF;
  END IF;

  --Create SEQUENCE
  v_ddl := 'CREATE SEQUENCE ' || C_SCHEMA_SEQUENCE_NAME || '
          START WITH 1000
          MAXVALUE 999999999999999999999999999
          MINVALUE 1000
          NOCYCLE
          CACHE 20
          NOORDER';  

  dbms_output.put_line(v_ddl);
  EXECUTE IMMEDIATE v_ddl; 
  
  --***REQUIRED***Create Public Synonym
  v_ddl := 'CREATE OR REPLACE PUBLIC SYNONYM '||C_SEQUENCE_NAME||' FOR '||C_SCHEMA_SEQUENCE_NAME;
  EXECUTE IMMEDIATE v_ddl;
  dbms_output.put_line(v_ddl);
  
  --***REQUIRED***Grant select to below roles and other permissions as required
  v_ddl := 'GRANT SELECT on '||C_SEQUENCE_NAME||' TO WILLOW_USER, MAL_DEVELOPER, MAL_REPORTING, BIETL, REPORT, VISION, TAL';
  EXECUTE IMMEDIATE v_ddl;
  dbms_output.put_line(v_ddl);  

  END;