package com.vb.services.database.rds;

public class MySQLDBSettingsConfig {
	
	private String dbInstanceIdentifier;
	private String masterUserName;
	private String masterPassword;
	
	public String getDbInstanceIdentifier() {
		return dbInstanceIdentifier;
	}
	
	public void setDbInstanceIdentifier(String dbInstanceIdentifier) {
		this.dbInstanceIdentifier = dbInstanceIdentifier;
	}
	
	public String getMasterUserName() {
		return masterUserName;
	}
	
	public void setMasterUserName(String masterUserName) {
		this.masterUserName = masterUserName;
	}
	
	public String getMasterPassword() {
		return masterPassword;
	}
	
	public void setMasterPassword(String masterPassword) {
		if ( isMasterPasswordLengthValid(masterPassword)) {
			this.masterPassword = masterPassword;
		}
	}
	
	// Validate if masterPassword length greater than 8.
	private Boolean isMasterPasswordLengthValid(String masterPassword) throws IllegalArgumentException {
		Boolean isMasterPasswordLengthValid = true;
		if ( masterPassword.length() < 8 ) {
			throw new IllegalArgumentException("Master Password length should be greater than 8 characters");
		}
		return isMasterPasswordLengthValid;
	}
	

}
