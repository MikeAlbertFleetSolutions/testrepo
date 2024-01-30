CREATE OR REPLACE PACKAGE BODY  ACCT_APP.JURISDICTION_WRAPPER IS
---------------------------------------------------------------------------------------------
-- Filename : JURISDICTION_WRAPPER.pkb
--
--   pkg_version := 1100 [10-DEC-2020]
 
---------------------------------------------------------------------------------------------
-- Purpose  : Wraps the jurisdiction package to create a separation between the jurisdiction package and ACCT_APP.
--            Logic specific to ACCT_APP can be implemented in this package without affecting other applications
--            that utilize the jurisdiction package.

--Input 
--Output

-- Modification history
--
-- Version Date   Log Number                 Changed By        Description/Reason for change
-- -----------    ------------               ----------        -------------------------------------------------------------------------------------------
-- 10-DEC-2020    LAFS-1637                  Wayne S.          Initial
----------------------------------------------------------------------------------------------------------------------------------------------------------

  ---------------------------------------------------------------------------------------------------------
  -- Purpose:
  --     Retrieves the jurisdiction via city_zip_code data based on, at the time of writing, city and zip.
  --  Assumptions:
  --     It is assumed that the data passed to this function will come from Vertex O series address scrubber.
  --  Parameters:
  --     pi_country: 'USA' or 'CN'
  --     pi_region: 
  --         Region can be abbreviated or spelled out, e.g 'OH' or 'Ohio'. 
  --         Value is case insensitive.
  --     pi_county: 
  --         County must be spelled out, e.g 'Butler'. Value is case insensitive.
  --     pi_city:   
  --         City must be spelled out, e.g 'West Chester'. Value is case insensitive.  
  --     pi_zip: 
  --         For USA, zip must contain the 9 digits for Canadian zip the first three characters are needed.   
  --  Returns:
  --     city_zip_code_t - 
  --         A collection jurisdiction_ot object types that identifies jurisdiction data.
  --         In must cases, there will only be one record. When there are many records, use SELECT DISTINCT.
  --  Exception:
  --     Caller must handle exception
  --
  --  Date            Bug             Changed By            Description/Reason for change
  --  ----------      -----------     ---------------       -----------------------------------------------
  --  12/10/20        LAFS-1642       Wayne S.              Initial Version
  ---------------------------------------------------------------------------------------------------------
  FUNCTION find(
    pi_country   IN city_zip_codes.country_code%TYPE,
    pi_region    IN city_zip_codes.region_code%TYPE,
    pi_county    IN city_zip_codes.county_code%TYPE,
    pi_city      IN city_zip_codes.city_code%TYPE,
    pi_zip       IN city_zip_codes.zip_code%TYPE) RETURN city_zip_code_t IS

    r_city_zip_code  city_zip_code_t;
  BEGIN
    r_city_zip_code := jurisdiction.find(pi_country, pi_region, pi_county, pi_city, pi_zip);
    RETURN r_city_zip_code;
  EXCEPTION 
    WHEN OTHERS THEN
      wms.raise_program_alert ('acct_app.jurisdiction_wrapper.find', 'W2K-99999', 'ERROR', SQLCODE, SQLERRM (SQLCODE));
  END find;

END JURISDICTION_WRAPPER;
/

--Grants
GRANT EXECUTE ON JURISDICTION TO MAL_DEVELOPER;
GRANT EXECUTE on JURISDICTION to VISION_USER;



