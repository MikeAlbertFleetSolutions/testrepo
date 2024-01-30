DECLARE
---------------------------------------------------------------------------------------------------
--IMPORTANT NOTE FOR DEVELOPER: PLEASE SET INDEXES, CONSTRAINTS, GRANTS, COMMENTS, ETC BASED ON YOUR REQUIREMENTS
--
-- Filename : V1_0_55__Create_message_log.sql
--
-- Purpose  : Central place to persist message related data that can be accessed by distributed workloads 
--
-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -------------------------------------------------------------------------------------------
-- 1100         02-AUG-2021    LAFS-2874                  Wayne S.          Init
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
  C_SCHEMA_OWNER      CONSTANT   VARCHAR2(100) := UPPER('ACCT_APP');
  C_TABLE_NAME        CONSTANT   VARCHAR2(100) := UPPER('MESSAGE_LOG');
  C_SCHEMA_TABLE_NAME CONSTANT   VARCHAR2(200) := C_SCHEMA_OWNER || '.' || C_TABLE_NAME;

  v_exists            INTEGER;
  v_ddl               VARCHAR2(32000);

  --If table exists and you want to delete/recreate it then set it to true else false
  v_tab_exists_then_del       CONSTANT    BOOLEAN := FALSE;
BEGIN
  -- Check to see if Table exists
  SELECT count(1)
    INTO v_exists
    FROM all_tables
   WHERE table_name = C_TABLE_NAME
     AND owner = C_SCHEMA_OWNER; 

  IF v_exists = 1 THEN
    --This means table exists
    IF v_tab_exists_then_del THEN
      --Delete existing table
      v_ddl := 'DROP TABLE ' || C_SCHEMA_TABLE_NAME||' CASCADE CONSTRAINTS';
      EXECUTE IMMEDIATE v_ddl;
      dbms_output.put_line(v_ddl);
    ELSE
      --Do nothing
      dbms_output.put_line(C_SCHEMA_TABLE_NAME||' already exists. If you wish to recreate then change flag v_tab_exists_then_del to TRUE');
      RETURN;
    END IF;
  END IF;

  --Create Table
  v_ddl := 'CREATE TABLE ' || C_SCHEMA_TABLE_NAME || ' ( ';

  v_ddl := v_ddl||' MLG_ID         NUMBER NOT NULL, ';  
  v_ddl := v_ddl||' EVENT_NAME     VARCHAR2(30) NOT NULL, ';
  v_ddl := v_ddl||' MESSAGE_ID     VARCHAR2(100) NOT NULL, ';
  v_ddl := v_ddl||' START_DATE     TIMESTAMP DEFAULT LOCALTIMESTAMP, ';  
  v_ddl := v_ddl||' END_DATE       TIMESTAMP ';
  v_ddl := v_ddl||' )';
  dbms_output.put_line(v_ddl);
  EXECUTE IMMEDIATE v_ddl; 

  --***REQUIRED***Create Primary Key Constraint
  v_ddl := 'ALTER TABLE '||C_SCHEMA_TABLE_NAME||' ADD CONSTRAINT MLG_PK PRIMARY KEY (MLG_ID)';
  EXECUTE IMMEDIATE v_ddl;
  dbms_output.put_line(v_ddl);

  --***REQUIRED*** Create Table Comment
  v_ddl := 'COMMENT ON TABLE '||C_SCHEMA_TABLE_NAME||' IS ''Stores accounting event message related data ''';
  EXECUTE IMMEDIATE v_ddl;
  dbms_output.put_line(v_ddl);
  
  --***REQUIRED***Create Public Synonym
  v_ddl := 'CREATE OR REPLACE PUBLIC SYNONYM '||C_TABLE_NAME||' FOR '||C_SCHEMA_TABLE_NAME;
  EXECUTE IMMEDIATE v_ddl;
  dbms_output.put_line(v_ddl);

  --***REQUIRED***Grant select to below roles and other permissions as required
  v_ddl := 'GRANT SELECT on '||C_SCHEMA_TABLE_NAME||' TO WILLOW_USER, MAL_DEVELOPER, MAL_REPORTING, BIETL, REPORT, VISION, TAL, WILLOW2K';
  EXECUTE IMMEDIATE v_ddl;
  dbms_output.put_line(v_ddl);

  --***REQUIRED***Grant select to below roles and other permissions as required
  v_ddl := 'GRANT DELETE on '||C_SCHEMA_TABLE_NAME||' TO WILLOW2K';
  EXECUTE IMMEDIATE v_ddl;
  dbms_output.put_line(v_ddl);

END;
/