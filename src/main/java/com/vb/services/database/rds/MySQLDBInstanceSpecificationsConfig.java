package com.vb.services.database.rds;

public class MySQLDBInstanceSpecificationsConfig implements DBInstanceSpecificationsConfig {
	
	private String dbEngine = "mysql";
	private String licenceModel = "general-public-license";
	private String dbVersion = "5.6.27";
	private String dbInstanceClass = "db.t1.micro";
	private Boolean multiAZDeployment = true;
	private String storageType = "gp2";
	//Must be between 5 and 6144.
	private Integer allocatedStorage = 10;
	
	public MySQLDBInstanceSpecificationsConfig() {
	}
	
	public MySQLDBInstanceSpecificationsConfig(String dbEngine, String licenceModel,String dbVersion,String dbInstanceClass, Boolean multiAZDeployment, String storageType,Integer allocatedStorage  ) {
		this.dbEngine = dbEngine;
		this.licenceModel = licenceModel;
		this.dbVersion = dbVersion;
		this.dbInstanceClass = dbInstanceClass;
		this.multiAZDeployment = multiAZDeployment;
		this.storageType = storageType;
		this.allocatedStorage = allocatedStorage;
	}
	
	
	
	public String getDbEngine() {
		return dbEngine;
	}
	public void setDbEngine(String dbEngine) {
		this.dbEngine = dbEngine;
	}
	public String getLicenceModel() {
		return licenceModel;
	}
	public void setLicenceModel(String licenceModel) {
		this.licenceModel = licenceModel;
	}
	public String getDbVersion() {
		return dbVersion;
	}
	public void setDbVersion(String dbVersion) {
		this.dbVersion = dbVersion;
	}
	public String getDbInstanceClass() {
		return dbInstanceClass;
	}
	public void setDbInstanceClass(String dbInstanceClass) {
		this.dbInstanceClass = dbInstanceClass;
	}
	public Boolean getMultiAZDeployment() {
		return multiAZDeployment;
	}
	public void setMultiAZDeployment(Boolean multiAZDeployment) {
		this.multiAZDeployment = multiAZDeployment;
	}
	public String getStorageType() {
		return storageType;
	}
	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}
	public Integer getAllocatedStorage() {
		return allocatedStorage;
	}
	public void setAllocatedStorage(Integer allocatedStorage) {
		if ( validateAllocatedStorage(allocatedStorage) ) {
			this.allocatedStorage = allocatedStorage;
		}
	}
	
	// This method checks if the size is between 5 GB and 6144 GB
	private Boolean validateAllocatedStorage(Integer allocatedStorage) throws IllegalArgumentException {
		Boolean allocatedStorageValid = true;
		if (!( allocatedStorage >= 5 && allocatedStorage <= 6144 )) {
			throw new IllegalArgumentException("Value of allocated storage must be between 5 and 6144 GB");
		}
		return allocatedStorageValid;
	}
	
	

}
