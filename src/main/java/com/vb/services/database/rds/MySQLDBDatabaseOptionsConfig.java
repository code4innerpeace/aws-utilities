package com.vb.services.database.rds;

public class MySQLDBDatabaseOptionsConfig {
	
	private String dbName;
	private Integer dbPort = 3306;
	private String dbParameterGroup = "default.mysql5.6";
	private String dbOptionsGroup = "default.mysql-5-6";
	private Boolean copyTagsToSnapshot = false;
	private Boolean enableEncryption = false;
	// if Enable encryption;
	private String kmsKeyId;
	
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public Integer getDbPort() {
		return dbPort;
	}
	public void setDbPort(Integer dbPort) {
		this.dbPort = dbPort;
	}
	public String getDbParameterGroup() {
		return dbParameterGroup;
	}
	public void setDbParameterGroup(String dbParameterGroup) {
		this.dbParameterGroup = dbParameterGroup;
	}
	public String getDbOptionsGroup() {
		return dbOptionsGroup;
	}
	public void setDbOptionsGroup(String dbOptionsGroup) {
		this.dbOptionsGroup = dbOptionsGroup;
	}
	public Boolean getCopyTagsToSnapshot() {
		return copyTagsToSnapshot;
	}
	public void setCopyTagsToSnapshot(Boolean copyTagsToSnapshot) {
		this.copyTagsToSnapshot = copyTagsToSnapshot;
	}
	public Boolean getEnableEncryption() {
		return enableEncryption;
	}
	public void setEnableEncryption(Boolean enableEncryption) {
		this.enableEncryption = enableEncryption;
	}
	public String getKmsKeyId() {
		return kmsKeyId;
	}
	public void setKmsKeyId(String kmsKeyId) {
		this.kmsKeyId = kmsKeyId;
	}
	
	

}
