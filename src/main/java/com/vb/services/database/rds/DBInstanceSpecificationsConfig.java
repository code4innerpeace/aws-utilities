package com.vb.services.database.rds;

public interface DBInstanceSpecificationsConfig {
	
	public String getDbEngine();
	public void setDbEngine(String dbEngine);
	public String getLicenceModel();
	public void setLicenceModel(String licenceModel);
	public String getDbVersion();
	public void setDbVersion(String dbVersion);
	public String getDbInstanceClass();
	public void setDbInstanceClass(String dbInstanceClass);
	public Boolean getMultiAZDeployment();
	public void setMultiAZDeployment(Boolean multiAZDeployment);
	public String getStorageType();
	public void setStorageType(String storageType);
	public Integer getAllocatedStorage();
	public void setAllocatedStorage(Integer allocatedStorage);
	
	

}
