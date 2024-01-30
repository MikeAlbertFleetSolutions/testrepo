CREATE OR REPLACE PACKAGE ACCT_APP.JURISDICTION_WRAPPER IS
---------------------------------------------------------------------------------------------
-- Filename : JURISDICTION_WRAPPER.pks
--
-- Purpose  : Wraps the jurisdiction package to create a seperation between the jurisdiction package and ACC_APP.
--            Logic specific to ACCT_APP can be implemented in this package without affecting other applications
--            that utilize the jurisdiction package.

-- Modification history
--
-- Version      Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------  ------------   ----------                 ------------      -----------------------------------------------------------------------------------------
-- 1100         10-DEC-2020    LAFS-1637                  Wayne S.          Initial
---------------------------------------------------------------------------------------------------------------------------------------------------------------------

--   pkg_version := 1100 [10-DEC-2020]

  FUNCTION find(
    pi_country   IN city_zip_codes.country_code%TYPE,
    pi_region    IN city_zip_codes.region_code%TYPE,
    pi_county    IN city_zip_codes.county_code%TYPE,
    pi_city      IN city_zip_codes.city_code%TYPE,
    pi_zip       IN city_zip_codes.zip_code%TYPE) RETURN city_zip_code_t;

END JURISDICTION_WRAPPER;
/

