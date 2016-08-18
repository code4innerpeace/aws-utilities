package com.vb.services.database.rds;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.*;

public class MySQLDBUtils {
	
	private MySQLDB mysqlDB;
	private AmazonRDSClient amazonRDSClient;
	private CreateDBInstanceRequest createDBInstanceRequest;
	
	private final String DB_ENGINE = "MySQL";
	private final String DB_LICENSE_MODEL  = "general-public-license";
	private final String DB_VERSION = "5.6.27";
	private final String DB_INSTANCE_CLASS = "db.t1.micro";
	private final Boolean DB_MULTI_AZ_DEPLOYMENT = true;
	private final String DB_STORAGE_TYPE = "gp2";
	// Must be between 5 to 6144 GB.
	private final Integer DB_STORAGE_ALLOCATION_IN_GB = 5;
	
	public MySQLDBUtils(AWSCredentialsProvider awsCredentialsProvider) {
		this.amazonRDSClient = new AmazonRDSClient(awsCredentialsProvider);
		this.mysqlDB = new MySQLDB();
		this.createDBInstanceRequest = new CreateDBInstanceRequest();
	}
	
	// This method creates default MySQL DB for Test environment(Testing purpose).
	public void createDefaultDBInstance(String dbInstanceIdentifier, String masterUserName, String masterPassword) {
		createDefaultDBInstance(dbInstanceIdentifier, masterUserName, masterPassword, null);
	}
	
	// This method creates default MySQL DB for Test environment(Testing purpose).
	public void createDefaultDBInstance(String dbInstanceIdentifier, String masterUserName, String masterPassword, String dbName) {
		/**
		mysqlDB.setDbInstanceSpecificationsConfig(createDefaultDBInstanceSpecificationsConfig());
		mysqlDB.setDbSettingsConfig(createDBSettingsConfig(dbInstanceIdentifier, masterUserName, masterPassword));
		mysqlDB.setDbNetworkSecurityConfig(createDefaultDBNetworkSecurityConfig());
		mysqlDB.setDbDatabaseOptionsConfig(createDefaultDBDatabaseOptionsConfig(dbName));
		mysqlDB.setDbBackupConfig(createDefaultDBBackupConfig());
		mysqlDB.setDbMonitoringConfig(createDefaultDBMonitoringConfig());
		mysqlDB.setDbMaintenanceConfig(createDefaultDBMaintenanceConfig());
		*/
		// Setting DB instance specifications.
		updateMySQLDBInstanceSpecificationsConfig();
		updateMySQLDBSettingsConfig(dbInstanceIdentifier,masterUserName,masterPassword);
		
		// Create the DB Instance.
		try {
			this.amazonRDSClient.createDBInstance(createDBInstanceRequest);
		} catch(DBInstanceAlreadyExistsException diaee) {
			System.out.println("ERROR : DBInstanceAlreadyExistsException");
			diaee.printStackTrace();
			
		} catch(InsufficientDBInstanceCapacityException idice) {
			System.out.println("ERROR : InsufficientDBInstanceCapacityException");
			idice.printStackTrace();
			
		} catch(DBParameterGroupNotFoundException dpgnfe) {
			System.out.println("ERROR : DBParameterGroupNotFoundException");
			dpgnfe.printStackTrace();
			
		} catch(DBSecurityGroupNotFoundException dsgnfe) {
			System.out.println("ERROR : DBSecurityGroupNotFoundException");
			dsgnfe.printStackTrace();
			
		} catch(InstanceQuotaExceededException iqee) {
			System.out.println("ERROR : InstanceQuotaExceededException");
			iqee.printStackTrace();
			
		} catch(StorageQuotaExceededException sqee) {
			System.out.println("ERROR : StorageQuotaExceededException");
			sqee.printStackTrace();
			
		} catch(DBSubnetGroupNotFoundException dsgnfe) {
			System.out.println("ERROR : DBSubnetGroupNotFoundException");
			dsgnfe.printStackTrace();
			
		} catch(DBSubnetGroupDoesNotCoverEnoughAZsException dsgdnceae) {
			System.out.println("ERROR : DBSubnetGroupDoesNotCoverEnoughAZsException");
			dsgdnceae.printStackTrace();
			
		} catch(InvalidDBClusterStateException idcse) {
			System.out.println("ERROR : InvalidDBClusterStateException");
			idcse.printStackTrace();
			
		} catch(InvalidSubnetException ise) {
			System.out.println("ERROR : InvalidSubnetException");
			ise.printStackTrace();
			
		} catch(InvalidVPCNetworkStateException ivnse) {
			System.out.println("ERROR : InvalidVPCNetworkStateException");
			ivnse.printStackTrace();
			
		} catch(ProvisionedIopsNotAvailableInAZException pinaae) {
			System.out.println("ERROR : ProvisionedIopsNotAvailableInAZException");
			pinaae.printStackTrace();
			
		} catch(OptionGroupNotFoundException ognfe) {
			System.out.println("ERROR : OptionGroupNotFoundException");
			ognfe.printStackTrace();
			
		} catch(DBClusterNotFoundException dcnfe) {
			System.out.println("ERROR : DBClusterNotFoundException");
			dcnfe.printStackTrace();
			
		} catch(StorageTypeNotSupportedException stnse) {
			System.out.println("ERROR : StorageTypeNotSupportedException");
			stnse.printStackTrace();
			
		} catch(AuthorizationNotFoundException anfe) {
			System.out.println("ERROR : AuthorizationNotFoundException");
			anfe.printStackTrace();
			
		} catch(KMSKeyNotAccessibleException kknae) {
			System.out.println("ERROR : KMSKeyNotAccessibleException");
			kknae.printStackTrace();
			
		} catch(DomainNotFoundException dnfe) {
			System.out.println("ERROR : DomainNotFoundException");
			dnfe.printStackTrace();
			
		} 
		
		
		
	}
	
	// This method creates default DBInstanceSpecificationsConfig.
	private MySQLDBInstanceSpecificationsConfig createDefaultDBInstanceSpecificationsConfig() {
		// DB Engine Version set to : 5.6.27;
		// DB Instance class : db.t2.micro
		// Multi AZ deployment :- No
		// Allocated Storage :- 5GB
		
		System.out.println("===CREATING MySQLDBInstanceSpecificationsConfig===");
		
		MySQLDBInstanceSpecificationsConfig dbInstanceSpecificationsConfig = new MySQLDBInstanceSpecificationsConfig();
		dbInstanceSpecificationsConfig.setDbEngine(DB_ENGINE);
		dbInstanceSpecificationsConfig.setLicenceModel(DB_LICENSE_MODEL);
		dbInstanceSpecificationsConfig.setDbVersion(DB_VERSION);
		dbInstanceSpecificationsConfig.setDbInstanceClass(DB_INSTANCE_CLASS);
		dbInstanceSpecificationsConfig.setMultiAZDeployment(DB_MULTI_AZ_DEPLOYMENT);
		dbInstanceSpecificationsConfig.setStorageType(DB_STORAGE_TYPE);
		dbInstanceSpecificationsConfig.setAllocatedStorage(DB_STORAGE_ALLOCATION_IN_GB);
		
		System.out.println("===CREATED MySQLDBInstanceSpecificationsConfig===");
		
		return dbInstanceSpecificationsConfig;
	}
	
	// This method creates DBSettingsConfig.
	private MySQLDBSettingsConfig createDBSettingsConfig(String dbInstanceIdentifier, String masterUserName, String masterPassword) {
		System.out.println("===CREATING MySQLDBSettingsConfig===");
		
		MySQLDBSettingsConfig mysqlDBSettingsConfig = new MySQLDBSettingsConfig();
		mysqlDBSettingsConfig.setDbInstanceIdentifier(dbInstanceIdentifier);
		mysqlDBSettingsConfig.setMasterUserName(masterUserName);
		mysqlDBSettingsConfig.setMasterPassword(masterPassword);
		
		System.out.println("===CREATED MySQLDBSettingsConfig===");
		
		return mysqlDBSettingsConfig;
	}

	// This method creates DBNetworkSecurityConfig.
	private MySQLDBNetworkSecurityConfig createDefaultDBNetworkSecurityConfig() {
		// VPC Name :- 
		// Subnet Name :-
		// Publicly Accessible :- No
		// Availability Zone :- No preference
		// VPC Security Group :- 
		return null;
	}
	
	// This method creates DBDatabaseOptionsConfig.
	private MySQLDBDatabaseOptionsConfig createDefaultDBDatabaseOptionsConfig(String dbName) {
		// Database Name :- parameter.
		// DB Port :- 3306
		// DB Parameter Group :- default.mysql.5.6
		// DB Options Group :- default:mysql-5-6
		// Copy Tags to snapshots :- false.
		// Enable encryption :- No.
		return null;
		
	}
	
	// This method creates DBBackupConfig.
	private MySQLDBBackupConfig createDefaultDBBackupConfig() {
		// Backup retention period :- 7
		// Backup window :- No Preference.
		return null;
	}
	
	// This method creates DBMonitoringConfig
	private MySQLDBMonitoringConfig createDefaultDBMonitoringConfig() {
		// Enable Enhanced Monitoring :- Yes
		// Monitoring role :- Default.
		// Granuality :- 60
		return null;
	}
	
	// 
	private MySQLDBMaintenanceConfig createDefaultDBMaintenanceConfig() {
		// Auto Minor Version Upgrade :- Yes.
		// Maintenance Window :- No Preference.
		return null;
	}

	// This method update MySQL DB Instance specifications.
	private void updateMySQLDBInstanceSpecificationsConfig() {
		
		MySQLDBInstanceSpecificationsConfig mysqlDBInstanceSpecificationsConfig = createDefaultDBInstanceSpecificationsConfig();
		this.mysqlDB.setDbInstanceSpecificationsConfig(mysqlDBInstanceSpecificationsConfig);
		
		try {
			this.createDBInstanceRequest.setEngine(this.mysqlDB.getDbInstanceSpecificationsConfig().getDbEngine());
			this.createDBInstanceRequest.setLicenseModel(this.mysqlDB.getDbInstanceSpecificationsConfig().getLicenceModel());
			this.createDBInstanceRequest.setEngineVersion(this.mysqlDB.getDbInstanceSpecificationsConfig().getDbVersion());
			this.createDBInstanceRequest.setDBInstanceClass(this.mysqlDB.getDbInstanceSpecificationsConfig().getDbInstanceClass());
			this.createDBInstanceRequest.setMultiAZ(this.mysqlDB.getDbInstanceSpecificationsConfig().getMultiAZDeployment());
			this.createDBInstanceRequest.setStorageType(this.mysqlDB.getDbInstanceSpecificationsConfig().getStorageType());
			this.createDBInstanceRequest.setAllocatedStorage(this.mysqlDB.getDbInstanceSpecificationsConfig().getAllocatedStorage());
		} catch(AmazonServiceException ase) {
			System.out.println("ERROR : error during MySQLDBInstanceSpecificationsConfig setup.");
			System.out.println("Caught an AmazonServiceException");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
			ase.printStackTrace();
		} catch(AmazonClientException ace) {
			System.out.println("ERROR : error during MySQLDBInstanceSpecificationsConfig setup.");
			System.out.println("Caught an AmazonClientException");
            System.out.println("Error Message: " + ace.getMessage());
			ace.printStackTrace();
		}
	}
	
	// This method update MySQL Settings.
	private void updateMySQLDBSettingsConfig(String dbInstanceIdentifier, String masterUserName, String masterPassword) {
		MySQLDBSettingsConfig mysqlDBSettingsConfig = createDBSettingsConfig(dbInstanceIdentifier, masterUserName, masterPassword);
		this.mysqlDB.setDbSettingsConfig(mysqlDBSettingsConfig);
		
		try {
			this.createDBInstanceRequest.setDBInstanceIdentifier(this.mysqlDB.getDbSettingsConfig().getDbInstanceIdentifier());
			this.createDBInstanceRequest.setMasterUsername(this.mysqlDB.getDbSettingsConfig().getMasterUserName());
			this.createDBInstanceRequest.setMasterUserPassword(this.mysqlDB.getDbSettingsConfig().getMasterPassword());
		}catch(AmazonServiceException ase) {
			System.out.println("ERROR : error during MySQLDBSettingsConfig setup.");
			System.out.println("Caught an AmazonServiceException");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
			ase.printStackTrace();
		} catch(AmazonClientException ace) {
			System.out.println("ERROR : error during MySQLDBSettingsConfig setup.");
			System.out.println("Caught an AmazonClientException");
            System.out.println("Error Message: " + ace.getMessage());
			ace.printStackTrace();
		}
		
	}
	
	// This method updates MySQL network and security settings.
	private void updateMySQLDBNetworkSecurityConfig() {
		
		MySQLDBNetworkSecurityConfig mysqlDBNetworkSecurityConfig = new MySQLDBNetworkSecurityConfig();
		this.mysqlDB.setDbNetworkSecurityConfig(mysqlDBNetworkSecurityConfig);
		
		try {
			
		}catch(AmazonServiceException ase) {
			System.out.println("ERROR : error during MySQLDBNetworkSecurityConfig setup.");
			System.out.println("Caught an AmazonServiceException");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
			ase.printStackTrace();
		} catch(AmazonClientException ace) {
			System.out.println("ERROR : error during MySQLDBNetworkSecurityConfig setup.");
			System.out.println("Caught an AmazonClientException");
            System.out.println("Error Message: " + ace.getMessage());
			ace.printStackTrace();
		}
	}
	

}
