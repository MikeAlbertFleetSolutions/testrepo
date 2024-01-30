
[![CircleCI](https://circleci.com/gh/MikeAlbertFleetSolutions/accounting-processor.svg?style=svg&circle-token=5e3b3fd78030657380d0444fae1e685de4b5a5d9)](https://circleci.com/gh/MikeAlbertFleetSolutions/accounting-processor)
  
# accounting-processor

## Configuration

### Eclipse Project "Run/Debug Settings"
Set the project's program arguments to
```
--spring.config.location=file:///<file path to application.properties>
```
### Command line 
```
java -jar accounting-processor-0.0.1-SNAPSHOT.jar --spring.config.location=file:///C:/Apps/conf/accounting-processor/application.properties
```
## Post NetSuite Sandbox Refresh
1. Go to Gitlab/accounting-processor-{env}/application.properties and edit file.

   Keys will be regenerated in subsequent steps (changes will be made to the ##SuiteTalk section)  
   The following needs to be updated:       
      * mafs.suitetalk.consumer.key=  
      * mafs.suitetalk.consumer.secret=  
      * mafs.suitetalk.token.key=        
      * mafs.suitetalk.token.secret=
2. Edit 2 Integration Users  
  **First User:** 
    1. Log in to NetSuite SBn, Setup/User Roles/Manage Users  
    2. Edit "SuiteAnalytics Integration SuiteAnalytics Integration" User  
    3. Change email (Go to application.properties file and copy mafs.suiteanalytics.username)  
    5. Check "manually assign" and then copy mafs.suiteanalytics.password from application.properties file  
    6. Save  
    
   **Second User:** 
    1. Setup/User Roles/Manage Users
    2. Edit "SuiteTalk Integration SuiteTalk Integration" (Choose row with role "MA - SuiteTalk (Web Services Only)")
    3. Change email (Log in to another SBn for same and copy email from there.  Modify value to reference SBn environment that has been refreshed)
    4. Check "manually assign" and then copy mafs.suiteanalytics.password from application.properties file
    5. Save
3. Configure Integration  
   1. Go to Setup/Integration/Manage Integrations
   2. Select New 
   3. Name:  "MAFS SB Integration"
   4. Check "TBA: ISSUETOKEN ENDPOINT"
   5. Uncheck "TBA: AUTHORIZATION FLOW"
   6. Uncheck "AUTHORIZATION CODE GRANT" in OAuth 2.0 section
   7. Click Save
   8. Copy consumer key and paste to mafs.suitetalk.consumer.key= in application.properties file
   9. Copy consumer secret to mafs.suitetalk.consumer.secret= in application.properties file
4. Create Access Tokens  
   1. Setup/UserRoles/Access Tokens (Right click and choose new tab)  
      - Application Name: "MAFS SB Integration"
      - User: "SuiteTalk Integration"
      - Role: "MA - SuiteTalk (Web Services Only)"     
   2. Click Save  
   3. Copy token key to mafs.suitetalk.token.key= in application.properties file  
   4. Copy token secret to mafs.suitetalk.token.secret= in application.properties file  
   5. Commit changes to application.properties file (Message: Update SB tokens)  
5. Push build via CircleCI to the SBn environment. This will redeploy the app with the new application.properties updates.

## The procedure to execute the CircleCI build while skipping test cases is as follows
1. Upon logging into CircleCI, click on the project  and navigate to the filter section and choose the project name and branch from the drop down menu.
2. Navigate to the "Trigger Pipeline" option, where a pop-up will appear for adding parameters; proceed by clicking on "Add Parameter."
3. Add the following parameter values and then click on 'Trigger Pipeline':  
      - Parameter Type: boolean
      - Parameter Name: skipUnitTests
      - Parameter Value: true
    


