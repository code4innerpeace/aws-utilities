package com.vb.services.database.rds;

public class MySQLDBMaintenanceConfig {

	private Boolean autoMinorVersionUpgrade = true;
	
	// set default to "No preference".
	private String preferredMaintenanceWindow;
	
	public Boolean getAutoMinorVersionUpgrade() {
		return autoMinorVersionUpgrade;
	}
	public void setAutoMinorVersionUpgrade(Boolean autoMinorVersionUpgrade) {
		this.autoMinorVersionUpgrade = autoMinorVersionUpgrade;
	}
	public String getPreferredMaintenanceWindow() {
		return preferredMaintenanceWindow;
	}
	public void setPreferredMaintenanceWindow(String preferredMaintenanceWindow) {
		this.preferredMaintenanceWindow = preferredMaintenanceWindow;
	}
	
	
}
