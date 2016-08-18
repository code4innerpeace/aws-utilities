package com.vb.services.database.rds;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.vb.services.identitymanagement.CustomAWSCredentialsProvider;

public class MySQLDBDriver {

	public static void main(String args[]) {
		
		AWSCredentialsProvider awsCredentialsProvider = CustomAWSCredentialsProvider.getProfileCredentialsProvider();
		MySQLDBUtils mysqlDBUtils = new MySQLDBUtils(awsCredentialsProvider);
		
		String dbInstanceIdentifier = "mysqltest1";
		String masterUserName = "mysqltest1";
		String masterPassword = "mysqltest1";
		System.out.println("===CREATING MYSQL DB===");
		mysqlDBUtils.createDefaultDBInstance(dbInstanceIdentifier, masterUserName, masterPassword);
		
	}
}
