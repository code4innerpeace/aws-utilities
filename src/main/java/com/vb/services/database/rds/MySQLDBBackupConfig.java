package com.vb.services.database.rds;

public class MySQLDBBackupConfig {
	
	// Value should be between 0 and 35;
	private Integer retentionPeriod = 7;
	//Must be in the format hh24:mi-hh24:mi.
	//Times should be in Universal Coordinated Time (UTC).
	// set default to "No preference".
	private String preferredBackupWindow;
	
	public Integer getRetentionPeriod() {
		return retentionPeriod;
	}
	public void setRetentionPeriod(Integer retentionPeriod) {
		this.retentionPeriod = retentionPeriod;
	}
	public String getPreferredBackupWindow() {
		return preferredBackupWindow;
	}
	public void setPreferredBackupWindow(String preferredBackupWindow) {
		this.preferredBackupWindow = preferredBackupWindow;
	}
	
	
		
		
}
