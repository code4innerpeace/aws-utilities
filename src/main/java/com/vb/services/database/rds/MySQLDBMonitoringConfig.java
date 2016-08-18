package com.vb.services.database.rds;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MySQLDBMonitoringConfig {
	
	private Boolean enableEnhancedMonitoring = true;
	private String monitoringRoleArn;
	// Valid Values: 0, 1, 5, 10, 15, 30, 60
	private Integer monitoringInterval = 60;
	private final Integer[] VALID_MONITORING_INTERVAL = { 0, 1, 5, 10, 15, 30, 60};
	
	public Boolean isEnableEnhancedMonitoring() {
		return enableEnhancedMonitoring;
	}


	public void setEnableEnhancedMonitoring(Boolean enableEnhancedMonitoring) {
		this.enableEnhancedMonitoring = enableEnhancedMonitoring;
	}


	public String getMonitoringRoleArn() {
		return monitoringRoleArn;
	}


	public void setMonitoringRoleArn(String monitoringRoleArn) {
		this.monitoringRoleArn = monitoringRoleArn;
	}


	public Integer getMonitoringInterval() {
		return monitoringInterval;
	}


	public void setMonitoringInterval(Integer monitoringInterval) {
		this.monitoringInterval = monitoringInterval;
	}
	
	// This methods validates if monitoring interval is valid.
	public Boolean isMonitoringIntervalValid(Integer monitoringInterval) throws IllegalArgumentException {
		Set<Integer> set = new HashSet<Integer>(Arrays.asList(VALID_MONITORING_INTERVAL));
		Boolean monitoringIntervalExists = set.contains(monitoringInterval);
		if ( monitoringIntervalExists ) {
			return monitoringIntervalExists;
		} else {
			throw new IllegalArgumentException("Monitoring interval value provided is invalid. It should be one of [0,1,5,10,15,30,60]");
		}
	}

}
