package com.vb.services.database.rds;

public class MySQLDB {
	
	// DB INSTANCE DETAILS
	private MySQLDBInstanceSpecificationsConfig dbInstanceSpecificationsConfig;
	
	// DB SETTINGS
	private MySQLDBSettingsConfig dbSettingsConfig;
	
	// NETWORK AND SECURITY 
	private MySQLDBNetworkSecurityConfig dbNetworkSecurityConfig;
	
	// DATABASE OPTIONS
	private MySQLDBDatabaseOptionsConfig dbDatabaseOptionsConfig;
	
	// Backup 
	private MySQLDBBackupConfig dbBackupConfig;
	
	// Monitoring :-
	private MySQLDBMonitoringConfig dbMonitoringConfig;
	
	// Maintenance
	private MySQLDBMaintenanceConfig dbMaintenanceConfig;
	
	
	
	public MySQLDB() {		
	}
	
	
	
	public MySQLDBInstanceSpecificationsConfig getDbInstanceSpecificationsConfig() {
		return dbInstanceSpecificationsConfig;
	}



	public void setDbInstanceSpecificationsConfig(MySQLDBInstanceSpecificationsConfig dbInstanceSpecificationsConfig) {
		this.dbInstanceSpecificationsConfig = dbInstanceSpecificationsConfig;
	}



	public MySQLDBSettingsConfig getDbSettingsConfig() {
		return dbSettingsConfig;
	}



	public void setDbSettingsConfig(MySQLDBSettingsConfig dbSettingsConfig) {
		this.dbSettingsConfig = dbSettingsConfig;
	}



	public MySQLDBNetworkSecurityConfig getDbNetworkSecurityConfig() {
		return dbNetworkSecurityConfig;
	}



	public void setDbNetworkSecurityConfig(MySQLDBNetworkSecurityConfig dbNetworkSecurityConfig) {
		this.dbNetworkSecurityConfig = dbNetworkSecurityConfig;
	}



	public MySQLDBDatabaseOptionsConfig getDbDatabaseOptionsConfig() {
		return dbDatabaseOptionsConfig;
	}



	public void setDbDatabaseOptionsConfig(MySQLDBDatabaseOptionsConfig dbDatabaseOptionsConfig) {
		this.dbDatabaseOptionsConfig = dbDatabaseOptionsConfig;
	}



	public MySQLDBBackupConfig getDbBackupConfig() {
		return dbBackupConfig;
	}



	public void setDbBackupConfig(MySQLDBBackupConfig dbBackupConfig) {
		this.dbBackupConfig = dbBackupConfig;
	}



	public MySQLDBMonitoringConfig getDbMonitoringConfig() {
		return dbMonitoringConfig;
	}



	public void setDbMonitoringConfig(MySQLDBMonitoringConfig dbMonitoringConfig) {
		this.dbMonitoringConfig = dbMonitoringConfig;
	}



	public MySQLDBMaintenanceConfig getDbMaintenanceConfig() {
		return dbMaintenanceConfig;
	}



	public void setDbMaintenanceConfig(MySQLDBMaintenanceConfig dbMaintenanceConfig) {
		this.dbMaintenanceConfig = dbMaintenanceConfig;
	}

	
	

}
