/*QUARTZ*/
DROP TABLE IF EXISTS qrtz_calendars;
DROP TABLE IF EXISTS qrtz_fired_triggers;
DROP TABLE IF EXISTS qrtz_blob_triggers;
DROP TABLE IF EXISTS qrtz_cron_triggers;
DROP TABLE IF EXISTS qrtz_simple_triggers;
DROP TABLE IF EXISTS qrtz_simprop_triggers;
DROP TABLE IF EXISTS qrtz_triggers;
DROP TABLE IF EXISTS qrtz_job_details;
DROP TABLE IF EXISTS qrtz_paused_trigger_grps;
DROP TABLE IF EXISTS qrtz_locks;
DROP TABLE IF EXISTS qrtz_scheduler_state;

CREATE TABLE qrtz_job_details
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    JOB_CLASS_NAME   VARCHAR2(250) NOT NULL, 
    IS_DURABLE VARCHAR2(100) NOT NULL,
    IS_NONCONCURRENT VARCHAR2(100) NOT NULL,
    IS_UPDATE_DATA VARCHAR2(100) NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(100) NOT NULL,
    JOB_DATA BLOB NULL,
    CONSTRAINT QRTZ_JOB_DETAILS_PK PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);
CREATE TABLE qrtz_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    NEXT_FIRE_TIME NUMBER(13) NULL,
    PREV_FIRE_TIME NUMBER(13) NULL,
    PRIORITY NUMBER(13) NULL,
    TRIGGER_STATE VARCHAR2(16) NOT NULL,
    TRIGGER_TYPE VARCHAR2(8) NOT NULL,
    START_TIME NUMBER(13) NOT NULL,
    END_TIME NUMBER(13) NULL,
    CALENDAR_NAME VARCHAR2(200) NULL,
    MISFIRE_INSTR NUMBER(2) NULL,
    JOB_DATA BLOB NULL,
    CONSTRAINT QRTZ_TRIGGERS_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_TRIGGER_TO_JOBS_FK FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP) 
      REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP) 
);
CREATE TABLE qrtz_simple_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    REPEAT_COUNT NUMBER(7) NOT NULL,
    REPEAT_INTERVAL NUMBER(12) NOT NULL,
    TIMES_TRIGGERED NUMBER(10) NOT NULL,
    CONSTRAINT QRTZ_SIMPLE_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_SIMPLE_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_cron_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID VARCHAR2(80),
    CONSTRAINT QRTZ_CRON_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_CRON_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
      REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_simprop_triggers
  (          
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    STR_PROP_1 VARCHAR2(512) NULL,
    STR_PROP_2 VARCHAR2(512) NULL,
    STR_PROP_3 VARCHAR2(512) NULL,
    INT_PROP_1 NUMBER(10) NULL,
    INT_PROP_2 NUMBER(10) NULL,
    LONG_PROP_1 NUMBER(13) NULL,
    LONG_PROP_2 NUMBER(13) NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 VARCHAR2(1) NULL,
    BOOL_PROP_2 VARCHAR2(1) NULL,
    CONSTRAINT QRTZ_SIMPROP_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_SIMPROP_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
      REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_blob_triggers
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    CONSTRAINT QRTZ_BLOB_TRIG_PK PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    CONSTRAINT QRTZ_BLOB_TRIG_TO_TRIG_FK FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_calendars
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    CALENDAR_NAME  VARCHAR2(200) NOT NULL, 
    CALENDAR BLOB NOT NULL,
    CONSTRAINT QRTZ_CALENDARS_PK PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);
CREATE TABLE qrtz_paused_trigger_grps
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL, 
    CONSTRAINT QRTZ_PAUSED_TRIG_GRPS_PK PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_fired_triggers 
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    ENTRY_ID VARCHAR2(95) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    FIRED_TIME NUMBER(13) NOT NULL,
    SCHED_TIME NUMBER(13) NOT NULL,
    PRIORITY NUMBER(13) NOT NULL,
    STATE VARCHAR2(16) NOT NULL,
    JOB_NAME VARCHAR2(200) NULL,
    JOB_GROUP VARCHAR2(200) NULL,
    IS_NONCONCURRENT VARCHAR2(1) NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NULL,
    CONSTRAINT QRTZ_FIRED_TRIGGER_PK PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);
CREATE TABLE qrtz_scheduler_state 
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
    CHECKIN_INTERVAL NUMBER(13) NOT NULL,
    CONSTRAINT QRTZ_SCHEDULER_STATE_PK PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);
CREATE TABLE qrtz_locks
  (
    SCHED_NAME VARCHAR2(120) NOT NULL,
    LOCK_NAME  VARCHAR2(40) NOT NULL, 
    CONSTRAINT QRTZ_LOCKS_PK PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

/* REGION_CODES */
DROP TABLE IF EXISTS region_codes;

CREATE TABLE REGION_CODES (	
  COUNTRY_CODE  VARCHAR2(80),
  REGION_CODE   VARCHAR2(80),
  REGION_DESC   VARCHAR2(80)
);

/* TOWN_CITY_CODES */
DROP TABLE IF EXISTS TOWN_CITY_CODES;

CREATE TABLE TOWN_CITY_CODES (	
  COUNTRY_CODE      VARCHAR2(80), 
  REGION_CODE       VARCHAR2(80),
  COUNTY_CODE       VARCHAR2(80),  
  TOWN_NAME         VARCHAR2(80),
  TOWN_DESCRIPTION  VARCHAR2(80)
);

/* CITY_ZIP_CODES */
DROP TABLE IF EXISTS CITY_ZIP_CODES;

CREATE TABLE CITY_ZIP_CODES (	
  COUNTRY_CODE  VARCHAR2(80), 
  REGION_CODE   VARCHAR2(80), 
  COUNTY_CODE   VARCHAR2(80), 
  CITY_CODE     VARCHAR2(80), 
  ZIP_CODE      VARCHAR2(25), 
  ZIP_CODE_END  VARCHAR2(25), 
  GEO_CODE      VARCHAR2(25)
);

/* VEH_MOVEMENT_ADDR_LINKS */
DROP TABLE IF EXISTS VEH_MOVEMENT_ADDR_LINKS;
CREATE TABLE VEH_MOVEMENT_ADDR_LINKS (	
  VMAL_ID      NUMBER, 
  EAA_EAA_ID   NUMBER 
);

/* QUOTE_MODEL_DEPOSITS */
DROP TABLE IF EXISTS QUOTE_MODEL_DEPOSITS;
CREATE TABLE QUOTE_MODEL_DEPOSITS (	
  QMD_QMD_ID      NUMBER(12,0),
  "VALUE"         NUMBER(30,5)
);

/* QUOTE_MODEL_PROPERTIES */
DROP TABLE IF EXISTS QUOTE_MODEL_PROPERTIES;
CREATE TABLE QUOTE_MODEL_PROPERTIES (	
  QMP_ID           NUMBER(12,0),
  NAME             VARCHAR2(40),
  DESCRIPTION      VARCHAR2(80),
  VERSIONTS        DATE
);

/* QUOTE_MODEL_PROPERTY_VALUES */
DROP TABLE IF EXISTS QUOTE_MODEL_PROPERTY_VALUES;
CREATE TABLE QUOTE_MODEL_PROPERTY_VALUES (	
  QMPV_ID             NUMBER(12,0),
  QMP_QMP_ID          NUMBER(12,0),
  QMD_QMD_ID          NUMBER(12,0),
  PROPERTY_VALUE      VARCHAR2(40),
  VERSIONTS           DATE
);
   
/* POLLING_LOG*/
insert into app_log (plg_id, name, payload, create_date)
  values(NEXTVAL('PLG_SEQ'), 'TEST-JOB', '{}', CURRENT_TIMESTAMP - 365);
  
insert into app_log (plg_id, name, payload, create_date)
  values(NEXTVAL('PLG_SEQ'), 'TEST-JOB', '{}', CURRENT_TIMESTAMP);
  
/* EXTERNAL_ACCOUNTS */
insert into external_accounts(C_ID, ACCOUNT_TYPE, ACCOUNT_CODE, ACC_STATUS,  ACCOUNT_NAME, SHORT_NAME, CURRENCY_CODE, INTERNATIONAL_IND, PAYMENT_IND, UPFIT_IND, WEB_QUOTES_REQ_CC_APPROVAL, WEB_QUOTES_REQ_FA_APPROVAL, EMAIL, TAX_REG_NO, TELEPHONE_NUMBER, GROUP_CODE, ORGANISATION_TYPE)
  values(1, 'S', '00000000', 'O', 'ACME INC.', 'ACME INC.', 'USD', 'N', 'M', 'N', 'N', 'Y', 'no-reply@mikealbert.com', '00-0000000', '000-000-0000', '1099-NONE', 'CORP');

insert into external_accounts(C_ID, ACCOUNT_TYPE, ACCOUNT_CODE, ACC_STATUS,  ACCOUNT_NAME, SHORT_NAME, CURRENCY_CODE, INTERNATIONAL_IND, PAYMENT_IND, UPFIT_IND, WEB_QUOTES_REQ_CC_APPROVAL, WEB_QUOTES_REQ_FA_APPROVAL, EMAIL, TAX_REG_NO, TELEPHONE_NUMBER, GROUP_CODE, ORGANISATION_TYPE, PARENT_ACCOUNT_ENTITY, PARENT_ACCOUNT_TYPE, PARENT_ACCOUNT)
  values(1, 'S', 'C0000000', 'O', 'ACME INC. CHILD CO.', 'ACME INC.', 'USD', 'N', 'M', 'N', 'N', 'Y', 'no-reply@mikealbert.com', '00-0000000', '000-000-0000', '1099-NONE', 'CORP', 1, 'S', '00000000');
  
insert into external_accounts(C_ID, ACCOUNT_TYPE, ACCOUNT_CODE, ACC_STATUS,  ACCOUNT_NAME, SHORT_NAME, CURRENCY_CODE, INTERNATIONAL_IND, PAYMENT_IND, UPFIT_IND, WEB_QUOTES_REQ_CC_APPROVAL, WEB_QUOTES_REQ_FA_APPROVAL, EMAIL, TAX_REG_NO, TELEPHONE_NUMBER, GROUP_CODE, ORGANISATION_TYPE)
  values(1, 'C', '00000001', 'O', 'Widgets R Us LLC', 'Widgets R Us LLC', 'USD', 'N', 'M', 'N', 'N', 'Y', 'no-reply@mikealbert.com', '00-0000001', '111-111-1111', '1099-NONE', 'CORP');  

insert into external_accounts(C_ID, ACCOUNT_TYPE, ACCOUNT_CODE, ACC_STATUS,  ACCOUNT_NAME, SHORT_NAME, CURRENCY_CODE, INTERNATIONAL_IND, PAYMENT_IND, UPFIT_IND, WEB_QUOTES_REQ_CC_APPROVAL, WEB_QUOTES_REQ_FA_APPROVAL, EMAIL, TAX_REG_NO, TELEPHONE_NUMBER, GROUP_CODE, ORGANISATION_TYPE, PARENT_ACCOUNT_ENTITY, PARENT_ACCOUNT_TYPE, PARENT_ACCOUNT)
  values(1, 'C', '00000002', 'O', 'Widgets R Us LLC North', 'Widgets R Us LLC North', 'USD', 'N', 'M', 'N', 'N', 'Y', 'no-reply@mikealbert.com', '00-0000001', '222-222-2222', '1099-NONE', 'CORP', 1, 'C', '00000001');  
    
/* EXT_ACC_ADDRESSES */ 
insert into ext_acc_addresses(EAA_ID, C_ID, ACCOUNT_TYPE, ACCOUNT_CODE, DEFAULT_IND, ADDRESS_CODE, STREET_NO, ADDRESS_LINE_1, ADDRESS_LINE_2, POSTCODE, GEO_CODE, COUNTRY, REGION, COUNTY_CODE, TOWN_CITY, ADDRESS_TYPE)
  values(NEXTVAL('EAA_ID_SEQ'), 1, 'S', '00000000', 'Y', 'W9', null, 'Vendor Address Line 1 ', 'Vendor Address Line 2', '45241', 'GEO CODE', 'USA', 'OH', '061', 'EVENDALE', 'POST');
  
insert into ext_acc_addresses(EAA_ID, C_ID, ACCOUNT_TYPE, ACCOUNT_CODE, DEFAULT_IND, ADDRESS_CODE, STREET_NO, ADDRESS_LINE_1, ADDRESS_LINE_2, POSTCODE, GEO_CODE, COUNTRY, REGION, COUNTY_CODE, TOWN_CITY, ADDRESS_TYPE)
  values(NEXTVAL('EAA_ID_SEQ'), 1, 'S', 'C0000000', 'Y', 'W9', null, 'Vendor Address Line 1 ', 'Vendor Address Line 2', '45241', 'GEO CODE', 'USA', 'OH', '061', 'EVENDALE', 'POST');  
      
insert into ext_acc_addresses(EAA_ID, C_ID, ACCOUNT_TYPE, ACCOUNT_CODE, DEFAULT_IND, ADDRESS_CODE, STREET_NO, ADDRESS_LINE_1, ADDRESS_LINE_2, POSTCODE, GEO_CODE, COUNTRY, REGION, COUNTY_CODE, TOWN_CITY, ADDRESS_TYPE)
  values(NEXTVAL('EAA_ID_SEQ'), 1, 'C', '00000001', 'Y', 'W9', null, 'Client Address Line 1 ', 'Client Address Line 2', '45241', 'GEO CODE', 'USA', 'OH', '061', 'EVENDALE', 'POST');

/* REGION_CODES */
insert into region_codes(country_code, region_code, region_desc)
  values('CN', 'ON', 'ONTARIO');
  
/* TOWN_CITY_CODES */
insert into town_city_codes(country_code, region_code, county_code, town_name, town_description)
  values('CN', 'ON', '015', 'MISSISSAUGA', 'Mississauga');
  
/* CITY_ZIP_CODES */
insert into city_zip_codes(country_code, region_code, county_code, city_code, zip_code, zip_code_end, geo_code)
  values('USA', 'OH', '017', 'WEST CHESTER', '45069', '45069', '4736');
  
insert into city_zip_codes(country_code, region_code, county_code, city_code, zip_code, zip_code_end, geo_code)
  values('USA', 'OH', '017', 'WEST CHESTER', '45011', '45011', '4736');  
  
insert into city_zip_codes(country_code, region_code, county_code, city_code, zip_code, zip_code_end, geo_code)
  values('USA', 'OH', '061', 'SYMMES TOWNSHIP', '45249', '45249', '0021');
  
insert into city_zip_codes(country_code, region_code, county_code, city_code, zip_code, zip_code_end, geo_code)
  values('CN', 'ON', '015', 'MISSISSAUGA', 'L5L', 'L5L', '0569');  

/* XREF */
insert into xref(group_name, internal_value, external_value)
values('LEASE_TYPE', 'CE_LTD', '1');

insert into xref(group_name, internal_value, external_value)
values('LEASE_TYPE', 'OE_LTD', '3');

insert into xref(group_name, internal_value, external_value)
values('LEASE_TYPE', 'OE_EQUIP', '2');

insert into xref(group_name, internal_value, external_value)
values('INTEREST_TYPE', 'LIBOR_30', '3');

insert into xref(group_name, internal_value, external_value)
values('COMPANY', '1', '1');

insert into xref(group_name, internal_value, external_value)
values('COMPANY', '2', '4');

insert into xref(group_name, internal_value, external_value)
values('CURRENCY', 'USD', '1');

insert into xref(group_name, internal_value, external_value)
values('PAYMENT_METHOD', 'CHECKDAILY', 'CHECK');

insert into xref(group_name, internal_value, external_value)
values('PAYMENT_METHOD', 'EFTDAILY', 'ACH');

insert into xref(group_name, internal_value, external_value)
values('PAYMENT_METHOD', 'WIRE', 'WIRE');

insert into xref(group_name, internal_value, external_value)
values('PAYMENT_METHOD', 'WIRE', 'AUTO DEBIT');

insert into xref(group_name, internal_value, external_value)
values('PAYMENT_METHOD', 'EFTOTHER', 'INTERCOMPANY');

insert into xref(group_name, internal_value, external_value)
values('VENDOR_CREDIT_TERM', 'IMMED', 'IMMED');

insert into xref(group_name, internal_value, external_value)
values('VENDOR_CREDIT_TERM', 'NET 15', 'Net 15');

insert into xref(group_name, internal_value, external_value)
values('COUNTRY', 'USA', 'US');

insert into xref(group_name, internal_value, external_value)
values('COUNTRY', 'CN', 'CA');

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ITEM', '{"controlCode":"ST","index":0}', 'Vehicle');

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":0}', 'Title & License - Company Vehicle (Selling)');

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":1}', 'Title & License - Company Vehicle (G&A)');

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":2}', 'Sales Tax - Driver Sales');

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":3}', 'Title & License - Rebillable');

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ITEM', '{"controlCode":"FLLICENSE","index":4}', 'Title & License - Non Rebillable');

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ITEM', '{"controlCode":"CE_LTD","index":0}', 'Vehicle');

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ITEM', '{"controlCode":"FLMAINT","index":0}', 'Maintenance - Auto Integrate Fee'); 

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ITEM', '{"controlCode":"GRD_INTRNL","index":0}', 'Delivery - Internal PDI');

insert into xref(group_name, internal_value, external_value)
values('PURCHASE-ORDER-ITEM', '{"controlCode":"ST","index":0}', 'Vehicle');

insert into xref(group_name, internal_value, external_value)
values('CREDIT-ITEM', '{"controlCode":"FLMAINT","index":0}', 'Maintenance - Auto Integrate Fee');

insert into xref(group_name, internal_value, external_value)
values('INVOICE-ASSET-TYPE', 'CE_LTD', 'Fleet-Closed End');

insert into xref(group_name, internal_value, external_value)
values('CANADA_PROVINCE', 'PQ', 'QC');

insert into xref(group_name, internal_value, external_value)
values('CLIENT_CREDIT_TERM', 'STANDARD', '1st of Mo');

insert into xref(group_name, internal_value, external_value)
values('CLIENT-CATEGORY', 'YELLOW-EXP', 'Yellow Express');

insert into xref(group_name, internal_value, external_value)
values('CLIENT-CREDIT-STATUS', 'R', 'Review Required');

insert into xref(group_name, internal_value, external_value)
values('CLIENT-CREDIT-MANAGEMENT-TYPE', 'I', 'No Hierarchy');

insert into xref(group_name, internal_value, external_value)
values('API-COUNTRY', 'CN', '_canada');

insert into xref(group_name, internal_value, external_value)
values('API-COUNTRY', 'USA', '_unitedStates');

/* SUPPLIERS */
insert into SUPPLIERS(SUP_ID, EA_C_ID, EA_ACCOUNT_TYPE, EA_ACCOUNT_CODE)
  values(NEXTVAL('SUP_SEQ'), 1, 'S', '00000000');
  
--Product Setup
INSERT INTO products (product_code, product_type) VALUES ('CE_MAL', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('CE_LTD', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('REGM_MAL', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('REGM_LTD', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('GFL', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('ST', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('DEMO', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('INV_EXCLUS', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('INV_FLEX', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('MAN_RENT', 'CE');
INSERT INTO products (product_code, product_type) VALUES ('INVENTORY', 'INV');
INSERT INTO products (product_code, product_type) VALUES ('MAX', 'MAX');
INSERT INTO products (product_code, product_type) VALUES ('OE_MAL', 'OE');
INSERT INTO products (product_code, product_type) VALUES ('OE_LTD', 'OE');
INSERT INTO products (product_code, product_type) VALUES ('OE_IRENT', 'OE');
INSERT INTO products (product_code, product_type) VALUES ('OE_EQUIP', 'OE');
INSERT INTO products (product_code, product_type) VALUES ('AM_LC', 'PUR');
INSERT INTO products (product_code, product_type) VALUES ('AM', 'PUR');
INSERT INTO products (product_code, product_type) VALUES ('PUR_COSTPL', 'PUR');
INSERT INTO products (product_code, product_type) VALUES ('PUR_INVADJ', 'PUR');

Insert into LEASE_ELEMENTS (LEL_ID, ELEMENT_TYPE) Values (32, 'DISP_SERV');
Insert into LEASE_ELEMENTS (LEL_ID, ELEMENT_TYPE) Values (2, 'FINANCE');
Insert into LEASE_ELEMENTS (LEL_ID, ELEMENT_TYPE) Values (4, 'FINANCE');


--Make/Model Setup
INSERT INTO make_model_ranges (mrg_id, make_model_desc) VALUES (NEXTVAL('MRG_SEQ'), 'Colorado');
INSERT INTO model_mark_years (mmy_id, model_mark_year_desc) VALUES (2523, '2020');
INSERT INTO makes (mak_id, make_desc) VALUES (12, 'Chevrolet');
INSERT INTO models (mdl_id, model_desc, mak_mak_id, mmy_mmy_id, mrg_mrg_id) 
  VALUES (184807, '2020 Chevrolet Colorado WT 4x2 Extended Cab 6 ft. box 128.3 in. WB (12M53)', 12, 2523, CURRVAL('MRG_SEQ'));

--QuotationStepStructure Verification
 INSERT INTO quotation_step_structure (qmd_qmd_id, from_period, to_period) VALUES(12345, 1, 12);
 INSERT INTO quotation_step_structure (qmd_qmd_id, from_period, to_period) VALUES(12345, 13, 24);

 /* COST_CENTRES_CODES */
INSERT INTO COST_CENTRE_CODES(cost_centre_code, ea_c_id, ea_account_type, ea_account_code, description)
  VALUES('C-3PO', 1, 'C', '00000001', 'C-3PO Description');
  
INSERT INTO COST_CENTRE_CODES(cost_centre_code, ea_c_id, ea_account_type, ea_account_code, description)
  VALUES('C-3PO NOW', 1, 'C', '00000001', 'C-3PO NOW Description');
  
INSERT INTO COST_CENTRE_CODES(cost_centre_code, ea_c_id, ea_account_type, ea_account_code, description)
  VALUES('C-3PO 30', 1, 'C', '00000001', 'C-3PO 30 Description');
  
INSERT INTO COST_CENTRE_CODES(cost_centre_code, ea_c_id, ea_account_type, ea_account_code, description)
  VALUES('C-3PO 60', 1, 'C', '00000001', 'C-3PO 60 Description');  
  
/* DRIVER_COST_CENTRES */
INSERT INTO DRIVER_COST_CENTRES(drcc_id, drv_drv_id, cocc_c_id, cocc_account_type, cocc_account_code, cost_centre_code, effective_to_date)
  VALUES(NEXTVAL('DRCC_SEQ'), 1, 1, 'C', '00000001', 'C-3PO', null);
   
INSERT INTO DRIVER_COST_CENTRES(drcc_id, drv_drv_id, cocc_c_id, cocc_account_type, cocc_account_code, cost_centre_code, effective_to_date)
  VALUES(NEXTVAL('DRCC_SEQ'), 1, 1, 'C', '00000001', 'C-3PO NOW', CURRENT_TIMESTAMP);
   
INSERT INTO DRIVER_COST_CENTRES(drcc_id, drv_drv_id, cocc_c_id, cocc_account_type, cocc_account_code, cost_centre_code, effective_to_date)
  VALUES(NEXTVAL('DRCC_SEQ'), 1, 1, 'C', '00000001', 'C-3PO 30', CURRENT_TIMESTAMP - 30);
   
INSERT INTO DRIVER_COST_CENTRES(drcc_id, drv_drv_id, cocc_c_id, cocc_account_type, cocc_account_code, cost_centre_code, effective_to_date)
  VALUES(NEXTVAL('DRCC_SEQ'), 1, 1, 'C', '00000001', 'C-3PO 60', CURRENT_TIMESTAMP - 60);
   
/* DOC */
INSERT INTO DOC(doc_id, doc_type, gl_acc, doc_status, posted_date, op_code)
  VALUES(1, 'PORDER', 0, 'O', null, 'test');  
  
INSERT INTO DOC(doc_id, doc_type, gl_acc, doc_status, posted_date, op_code)
  VALUES(2, 'INVOICEAP', 0, 'P', CURRENT_TIMESTAMP, 'AUTO_MI');
  
INSERT INTO DOC(doc_id, doc_type, gl_acc, doc_status, posted_date, op_code)
  VALUES(3, 'CREDITAP', 0, 'P', CURRENT_TIMESTAMP, 'AUTO_MI');  
  
INSERT INTO DOC(doc_id, doc_type, gl_acc, doc_status, posted_date, op_code)
  VALUES(4, 'INVOICEAR', 0, 'O', null, 'test'); 
  
/* DOC_LINKS */  
INSERT INTO DOC_LINKS(parent_doc_id, child_doc_id)
  VALUES(1, 2);
  
INSERT INTO DOC_LINKS(parent_doc_id, child_doc_id)
  VALUES(2, 3);  
  
INSERT INTO DOC_LINKS(parent_doc_id, child_doc_id)
  VALUES(2, 4);    

/* DOCL */
INSERT INTO DOCL(doc_id, line_id, product_code)
  VALUES(2, 1, '000-001');
INSERT INTO DOCL(doc_id, line_id, product_code)
  VALUES(2, 2, 'AI_SHP_FEE');
INSERT INTO DOCL(doc_id, line_id, product_code)
  VALUES(2, 3, 'AI_FLT_FEE');  

  /* DIST */
INSERT INTO DIST(dis_id, doc_id)
  VALUES(1, 1);
  
/* QUOTATION_PROFILES */
INSERT INTO QUOTATION_PROFILES(QPR_ID, PRD_PRODUCT_CODE)
  VALUES (1, 'CE_MAL');  
  
/* QUOTATIONS */
INSERT INTO QUOTATIONS(QUO_ID, QPR_QPR_ID, ACCOUNT_CODE, DRV_DRV_ID)
  VALUES (1, 1, '00000000', 1);
INSERT INTO QUOTATIONS(QUO_ID, QPR_QPR_ID, ACCOUNT_CODE, DRV_DRV_ID)
  VALUES (2, 1, '11111111', 2);  
  
/* QUOTATION_MODELS */
INSERT INTO QUOTATION_MODELS(QMD_ID, QUO_QUO_ID, INTEREST_RATE, FMS_FMS_ID, QUOTE_STATUS, REVISION_NO, DEPRECIATION_FACTOR)
  VALUES (1, 1, 1, 1, 3, 1, 1.6666667);  
INSERT INTO QUOTATION_MODELS(QMD_ID, QUO_QUO_ID, INTEREST_RATE, FMS_FMS_ID, QUOTE_STATUS, REVISION_NO, DEPRECIATION_FACTOR)
  VALUES (2, 2, 2, 2, 6, 2, null); 

/* QUOTE_MODEL_DEPOSITS */
INSERT INTO QUOTE_MODEL_DEPOSITS(QMD_QMD_ID)
  VALUES(1);  
  
/* FLEET_MASTERS */
INSERT INTO FLEET_MASTERS (FMS_ID, VIN, UNIT_NO, MDL_MDL_ID) 
  VALUES (1, 'AAAAAAAAABBBBBBBB', '00000000', 184807);
INSERT INTO FLEET_MASTERS (FMS_ID, VIN, UNIT_NO, MDL_MDL_ID) 
  VALUES (2, 'BBBBBBBBBAAAAAAAA', '11111111', 184807);  
/* CONTRACT_LINES */  
INSERT INTO CONTRACT_LINES(CLN_ID, QMD_QMD_ID, FMS_FMS_ID)
  VALUES(1, 1, 1);
INSERT INTO CONTRACT_LINES(CLN_ID, QMD_QMD_ID, FMS_FMS_ID)
  VALUES(2, 2, 2); 
  
/* ACCOUNTING_EVENTS */
INSERT INTO ACCOUNTING_EVENTS(aet_id, entity, entity_id, event, op_code, create_date)
  VALUES(NEXTVAL('AET_SEQ'), 'CONTRACT', '1', 'STOP_BILLING', 'UNIT-TEST', CURRENT_TIMESTAMP);  
INSERT INTO ACCOUNTING_EVENTS(aet_id, entity, entity_id, event, op_code, create_date)
  VALUES(NEXTVAL('AET_SEQ'), 'CONTRACT', '2', 'STOP_BILLING', 'UNIT-TEST', CURRENT_TIMESTAMP);   
  
/* VEH_MOVEMENT_ADDR_LINKS */
INSERT INTO VEH_MOVEMENT_ADDR_LINKS (vmal_id, eaa_eaa_id)
  VALUES(1, 1);

/* MESSAGE_LOG */
INSERT INTO message_log (mlg_id, event_name, message_id, start_date, end_date)
  VALUES(NEXTVAL('MLG_SEQ'), 'GROUP_TRANSACTION', 'TEST1-MESSAGE-ID', CURRENT_TIMESTAMP, null);
INSERT INTO message_log (mlg_id, event_name, message_id, start_date, end_date)
  VALUES(NEXTVAL('MLG_SEQ'), 'GROUP_TRANSACTION', 'TEST2-MESSAGE-ID', CURRENT_TIMESTAMP - 1, CURRENT_TIMESTAMP);  

/* CLIENT_BILLING_TRANSACTIONS */
INSERT INTO CLIENT_BILLING_TRANSACTIONS (cbt_id, tran_int_id, line_no, account_code, account_name, accounting_period)
  VALUES(NEXTVAL('CBT_SEQ'), '0', 1, '00000000', 'TEST ACCOUNT NAME', 'JAN-2022'); 

/* UNIT_REGISTRATIONS */
INSERT INTO UNIT_REGISTRATIONS (urg_id, fms_fms_id, plate_expiry_date, region_code)
  VALUES(1, 1, CURRENT_TIMESTAMP - 1, 'FL');
INSERT INTO UNIT_REGISTRATIONS (urg_id, fms_fms_id, plate_expiry_date, region_code)
  VALUES(2, 1, CURRENT_TIMESTAMP, 'NY');
INSERT INTO UNIT_REGISTRATIONS (urg_id, fms_fms_id, plate_expiry_date, region_code)
  VALUES(3, 1, CURRENT_TIMESTAMP + 1, 'OH');
INSERT INTO UNIT_REGISTRATIONS (urg_id, fms_fms_id, plate_expiry_date, region_code)
  VALUES(4, 1, CURRENT_TIMESTAMP + 1, 'OH');  

/* DRIVER_ADDRESSES */
INSERT INTO DRIVER_ADDRESSES(dra_id, drv_drv_id, region, address_type)
  VALUES(1, 1, 'OH', 'GARAGED');

/* QUOTE_MODEL_PROPERTIES */
INSERT INTO QUOTE_MODEL_PROPERTIES(qmp_id, name, description, versionts)
  VALUES(1, 'PLB_TYPE', 'PLB Type Description', CURRENT_TIMESTAMP);

/* QUOTE_MODEL_PROPERTY_VALUES */
INSERT INTO QUOTE_MODEL_PROPERTY_VALUES(qmpv_id, qmp_qmp_id, qmd_qmd_id, property_value, versionts)
  VALUES(1, 1, 1, 'Fleet Management Company', CURRENT_TIMESTAMP);

