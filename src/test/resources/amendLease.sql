Insert into QUOTATION_PROFILES (QPR_ID, PRD_PRODUCT_CODE, ITC_INTEREST_TYPE, VARIABLE_RATE) Values (44590, 'OE_IRENT', 'PRIME', 'F');

Insert into QUOTATIONS (QUO_ID, QPR_QPR_ID, ACCOUNT_CODE) Values (517386, 44590, '00000000');

Insert into FLEET_MASTERS (FMS_ID, VIN, UNIT_NO, MDL_MDL_ID) Values (1273618, '1G1FW6S03K4131646', '01015347', 184807);

Insert into ASSET_ITEM 
  (ASSET_ID, C_ID, CODE, ADD_ON_SEQ, DESCRIPTION, STATUS, ASSET_TYPE, 
   CATEGORY_CODE, AGC_C_ID, DEP_CODE, LOCATION_CODE, GROUP_CODE, OP_CODE,
   INITIAL_VALUE, FLEET_ID, INVOICE_NO) Values 
  (496758, 1, 'OE_IRENT', '000', 'Test data', 'A', 'FL', 
   'OE_LTD', '1', 'OE_LTD', 'CUST', 'VEH', 'TEST',
   31947.84, 1273618, '318679');
Insert into ASSET_ITEM 
  (ASSET_ID, C_ID, CODE, ADD_ON_SEQ, DESCRIPTION, STATUS, ASSET_TYPE, 
   CATEGORY_CODE, AGC_C_ID, DEP_CODE, LOCATION_CODE, GROUP_CODE, OP_CODE,
   INITIAL_VALUE, FLEET_ID, INVOICE_NO) Values 
  (497861, 1, 'OE_IRENT', '001', 'Test data', 'A', 'FL', 
   'OE_LTD', '1', 'OE_LTD', 'CUST', 'VEH', 'TEST',
   350, 1273618, '1015347');
Insert into ASSET_ITEM 
  (ASSET_ID, C_ID, CODE, ADD_ON_SEQ, DESCRIPTION, STATUS, ASSET_TYPE, 
   CATEGORY_CODE, AGC_C_ID, DEP_CODE, LOCATION_CODE, GROUP_CODE, OP_CODE,
   INITIAL_VALUE, FLEET_ID) Values 
  (512927, 1, 'OE_IRENT', '002', 'Test data', 'A', 'FL', 
   'OE_LTD', '1', 'OE_LTD', 'CUST', 'VEH', 'TEST',
   145, 1273618);

Insert into QUOTATION_MODELS (QMD_ID, QUO_QUO_ID, QUOTE_STATUS, CAPITAL_CONTRIBUTION, RESIDUAL_VALUE, CONTRACT_PERIOD, REVISION_NO)
 Values (712242, 517386, '7', 3750, 14476.72, 12, 2);
 
Insert into quotation_step_structure (qmd_qmd_id, from_period, to_period) Values(712242, 1, 6);
Insert into quotation_step_structure (qmd_qmd_id, from_period, to_period) Values(712242, 7, 12);

Insert into QUOTATION_MODELS (QMD_ID, QUO_QUO_ID, QUOTE_STATUS, CAPITAL_CONTRIBUTION, RESIDUAL_VALUE, CONTRACT_PERIOD, REVISION_NO, AMENDMENT_EFF_DATE, CONTRACT_CHANGE_EVENT_PERIOD)
 Values (715773, 517386, '7', 3750, 14476.72, 12, 3, PARSEDATETIME('05/01/2019', 'MM/dd/yyyy'), 1);

Insert into quotation_step_structure (qmd_qmd_id, from_period, to_period) Values(715773, 1, 6);
Insert into quotation_step_structure (qmd_qmd_id, from_period, to_period) Values(715773, 7, 12);

Insert into QUOTATION_MODEL_ACCESSORIES (QMA_ID, QMD_QMD_ID, RECHARGE_AMOUNT) Values (3067432, 715773, 0);
Insert into QUOTATION_MODEL_ACCESSORIES (QMA_ID, QMD_QMD_ID, RECHARGE_AMOUNT) Values (3067433, 715773, 0);
Insert into QUOTATION_MODEL_ACCESSORIES (QMA_ID, QMD_QMD_ID, RECHARGE_AMOUNT) Values (3067434, 715773, 0);
Insert into QUOTATION_MODEL_ACCESSORIES (QMA_ID, QMD_QMD_ID, RECHARGE_AMOUNT) Values (3067435, 715773, 0);
Insert into QUOTATION_DEALER_ACCESSORIES (QDA_ID, QMD_QMD_ID, DRIVER_RECHARGE_YN, RECHARGE_AMOUNT) Values (1234567, 715773, 'Y', 10);
Insert into QUOTATION_ELEMENTS (QEL_ID, RENTAL, RESIDUAL_VALUE, LEL_LEL_ID, QMD_QMD_ID) Values (3887054, 15734.64, 14476.72, 2, 715773);
Insert into QUOTATION_ELEMENTS (QEL_ID, RENTAL, LEL_LEL_ID, QMD_QMD_ID) Values (3887055, 0, 32, 715773);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510413, 3887054, 0, 'F', PARSEDATETIME('05/01/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510414, 3887054, 1309.22, 'F', PARSEDATETIME('05/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510417, 3887054, 1309.22, 'R', PARSEDATETIME('06/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510419, 3887054, 1309.22, 'R', PARSEDATETIME('07/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510421, 3887054, 1309.22, 'R', PARSEDATETIME('08/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510423, 3887054, 1309.22, 'R', PARSEDATETIME('09/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510425, 3887054, 1309.22, 'R', PARSEDATETIME('10/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510427, 3887054, 1313.22, 'R', PARSEDATETIME('11/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510429, 3887054, 1313.22, 'R', PARSEDATETIME('12/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510431, 3887054, 1313.22, 'R', PARSEDATETIME('01/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510433, 3887054, 1313.22, 'R', PARSEDATETIME('02/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510435, 3887054, 1313.22, 'R', PARSEDATETIME('03/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510437, 3887054, 1313.22, 'R', PARSEDATETIME('04/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510439, 3887054, 14476.72, 'L', PARSEDATETIME('04/30/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510415, 3887055, 0, 'F', PARSEDATETIME('05/01/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510416, 3887055, 0, 'F', PARSEDATETIME('05/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510418, 3887055, 0, 'R', PARSEDATETIME('06/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510420, 3887055, 0, 'R', PARSEDATETIME('07/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510422, 3887055, 0, 'R', PARSEDATETIME('08/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510424, 3887055, 0, 'R', PARSEDATETIME('09/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510426, 3887055, 0, 'R', PARSEDATETIME('10/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510428, 3887055, 0, 'R', PARSEDATETIME('11/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510430, 3887055, 0, 'R', PARSEDATETIME('12/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510432, 3887055, 0, 'R', PARSEDATETIME('01/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510434, 3887055, 0, 'R', PARSEDATETIME('02/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510436, 3887055, 0, 'R', PARSEDATETIME('03/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(63510438, 3887055, 0, 'R', PARSEDATETIME('04/02/2020', 'MM/dd/yyyy'), 1);

Insert into QUOTATION_MODELS (QMD_ID, QUO_QUO_ID, QUOTE_STATUS, CAPITAL_CONTRIBUTION, RESIDUAL_VALUE, CONTRACT_PERIOD, REVISION_NO, AMENDMENT_EFF_DATE, CONTRACT_CHANGE_EVENT_PERIOD)
 Values (733916, 517386, '6', 3750, 14476.72, 12, 4, PARSEDATETIME('12/01/2019', 'MM/dd/yyyy'), 8);
Insert into quotation_step_structure (qmd_qmd_id, from_period, to_period) Values(733916, 1, 6);
Insert into quotation_step_structure (qmd_qmd_id, from_period, to_period) Values(733916, 7, 12);
Insert into QUOTATION_MODEL_ACCESSORIES (QMA_ID, QMD_QMD_ID, RECHARGE_AMOUNT) Values (3198645, 733916, 0);
Insert into QUOTATION_MODEL_ACCESSORIES (QMA_ID, QMD_QMD_ID, RECHARGE_AMOUNT) Values (3198646, 733916, 0);
Insert into QUOTATION_MODEL_ACCESSORIES (QMA_ID, QMD_QMD_ID, RECHARGE_AMOUNT) Values (3198647, 733916, 0);
Insert into QUOTATION_MODEL_ACCESSORIES (QMA_ID, QMD_QMD_ID, RECHARGE_AMOUNT) Values (3198648, 733916, 0);
Insert into QUOTATION_DEALER_ACCESSORIES (QDA_ID, QMD_QMD_ID, RECHARGE_AMOUNT, DAC_DAC_ID, DRIVER_RECHARGE_YN, TOTAL_PRICE) 
 Values (967853, 733916, 0, 860302, 'N', 145);
Insert into QUOTATION_ELEMENTS (QEL_ID, RENTAL, RESIDUAL_VALUE, LEL_LEL_ID, QMD_QMD_ID) Values (4084757, 15734.64, 14476.72, 2, 733916);
Insert into QUOTATION_ELEMENTS (QEL_ID, RENTAL, LEL_LEL_ID, QMD_QMD_ID) Values (4084758, 0, 32, 733916);
Insert into QUOTATION_ELEMENTS (QEL_ID, RENTAL, RESIDUAL_VALUE, LEL_LEL_ID, QMD_QMD_ID, QDA_QDA_ID) Values (4084759, 78.84, 72.5, 2, 733916, 967853);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623343, 4084757, 0, 'F', PARSEDATETIME('05/01/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623344, 4084757, 1309.22, 'F', PARSEDATETIME('05/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623347, 4084757, 1309.22, 'R', PARSEDATETIME('06/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623349, 4084757, 1309.22, 'R', PARSEDATETIME('07/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623351, 4084757, 1309.22, 'R', PARSEDATETIME('08/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623353, 4084757, 1309.22, 'R', PARSEDATETIME('09/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623355, 4084757, 1309.22, 'R', PARSEDATETIME('10/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623357, 4084757, 1313.22, 'R', PARSEDATETIME('11/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623359, 4084757, 1313.22, 'R', PARSEDATETIME('12/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623362, 4084757, 1313.22, 'R', PARSEDATETIME('01/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623365, 4084757, 1313.22, 'R', PARSEDATETIME('02/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623368, 4084757, 1313.22, 'R', PARSEDATETIME('03/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623371, 4084757, 1313.22, 'R', PARSEDATETIME('04/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623374, 4084757, 14476.72, 'L', PARSEDATETIME('04/30/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623345, 4084758, 0, 'F', PARSEDATETIME('05/01/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623346, 4084758, 0, 'F', PARSEDATETIME('05/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623348, 4084758, 0, 'R', PARSEDATETIME('06/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623350, 4084758, 0, 'R', PARSEDATETIME('07/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623352, 4084758, 0, 'R', PARSEDATETIME('08/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623354, 4084758, 0, 'R', PARSEDATETIME('09/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623356, 4084758, 0, 'R', PARSEDATETIME('10/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623358, 4084758, 0, 'R', PARSEDATETIME('11/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623360, 4084758, 0, 'R', PARSEDATETIME('12/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623363, 4084758, 0, 'R', PARSEDATETIME('01/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623366, 4084758, 0, 'R', PARSEDATETIME('02/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623369, 4084758, 0, 'R', PARSEDATETIME('03/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623372, 4084758, 0, 'R', PARSEDATETIME('04/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623361, 4084759, 6.57, 'R', PARSEDATETIME('12/02/2019', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623364, 4084759, 6.57, 'R', PARSEDATETIME('01/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623367, 4084759, 6.57, 'R', PARSEDATETIME('02/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623370, 4084759, 6.57, 'R', PARSEDATETIME('03/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623373, 4084759, 6.57, 'R', PARSEDATETIME('04/02/2020', 'MM/dd/yyyy'), 1);
Insert into QUOTATION_SCHEDULES(QSC_ID, QEL_QEL_ID, AMOUNT, PAYMENT_IND, TRANS_DATE, NO_OF_UNITS)Values(68623375, 4084759, 72.5, 'L', PARSEDATETIME('04/30/2020', 'MM/dd/yyyy'), 1);


Insert into CONTRACT_LINES (CLN_ID, CON_CON_ID, START_DATE, END_DATE, IN_SERV_DATE, FMS_FMS_ID, QMD_QMD_ID)
 Values (562073, 371994, PARSEDATETIME('05/01/2019', 'MM/dd/yyyy'), PARSEDATETIME('04/30/2020', 'MM/d/yyyy'), PARSEDATETIME('04/10/2019', 'MM/dd/yyyy'), 1273618, 712242);
Insert into CONTRACT_LINES (CLN_ID, CON_CON_ID, START_DATE, END_DATE, IN_SERV_DATE, FMS_FMS_ID, QMD_QMD_ID)
 Values (573439, 371994, PARSEDATETIME('05/01/2019', 'MM/dd/yyyy'), PARSEDATETIME('04/30/2020', 'MM/dd/yyyy'), PARSEDATETIME('04/10/2019', 'MM/dd/yyyy'), 1273618, 715773);
Insert into CONTRACT_LINES (CLN_ID, CON_CON_ID, START_DATE, END_DATE, IN_SERV_DATE, FMS_FMS_ID, QMD_QMD_ID)
 Values (583160, 371994, PARSEDATETIME('05/01/2019', 'MM/dd/yyyy'), PARSEDATETIME('04/30/2020', 'MM/dd/yyyy'), PARSEDATETIME('04/10/2019', 'MM/dd/yyyy'), 1273618, 733916);
