package com.vb.services.database.rds;

public class MySQLDBNetworkSecurityConfig {
	
	private String vpcName;
	private String vpcId;
	private String subnetGroupName;
	private String subnetGroupId;
	private Boolean publicablyAccessible = false;
	private String vpcSecurityGroupName;
	private String vpcSecurityID;
	
	public String getVpcName() {
		return vpcName;
	}
	public void setVpcName(String vpcName) {
		this.vpcName = vpcName;
	}
	public String getVpcId() {
		return vpcId;
	}
	public void setVpcId(String vpcId) {
		this.vpcId = vpcId;
	}
	public String getSubnetGroupName() {
		return subnetGroupName;
	}
	public void setSubnetGroupName(String subnetGroupName) {
		this.subnetGroupName = subnetGroupName;
	}
	public String getSubnetGroupId() {
		return subnetGroupId;
	}
	public void setSubnetGroupId(String subnetGroupId) {
		this.subnetGroupId = subnetGroupId;
	}
	public Boolean getPublicablyAccessible() {
		return publicablyAccessible;
	}
	public void setPublicablyAccessible(Boolean publicablyAccessible) {
		this.publicablyAccessible = publicablyAccessible;
	}
	public String getVpcSecurityGroupName() {
		return vpcSecurityGroupName;
	}
	public void setVpcSecurityGroupName(String vpcSecurityGroupName) {
		this.vpcSecurityGroupName = vpcSecurityGroupName;
	}
	public String getVpcSecurityID() {
		return vpcSecurityID;
	}
	public void setVpcSecurityID(String vpcSecurityID) {
		this.vpcSecurityID = vpcSecurityID;
	}
	
	

}
