package com.vb.services.scd;

import java.util.ArrayList;
import java.util.List;

public class AWSS3ApiExplorerDriver {
	
	public static void main(String args[]) {
		AWSS3ApiExplorer awsS3ApiExplorer = new AWSS3ApiExplorer("admin");
		awsS3ApiExplorer.listAllBuckets();
		
		/**
		List<String> s3Buckets = new ArrayList<String>();
		//Add buckets.
		s3Buckets.add("testBucket");
		
		
		//Logging destination bucket name.
		
		String destinationBucketName = "";
		for(String bucket: s3Buckets) {
			System.out.println("====" + bucket + "====");
			
			// Create Bucket
			awsS3ApiExplorer.createBucket(bucket);
			
			// Enable Logging
			awsS3ApiExplorer.enableS3Logging(bucket, destinationBucketName);
			
			// Enable Versioning
			awsS3ApiExplorer.enableVersioning(bucket);
			
			// Attach Custom Policy
			String customPolicy = getPolicyTemplate(bucket);
			awsS3ApiExplorer.attachPolicy(bucket, customPolicy);
		}
		*/
			
	}
	
	// This method use default policy template and replaces 'BUCKET' with bucket name provided and returns new custom policy template.
	public static String getPolicyTemplate(String bucketName) {
		String policyTemplate = String.join(System.getProperty("line.separator"),
								"{",
								"\"Version\":\"2012-10-17\",",
								"\"Statement\":[",
								"{",
								"\"Sid\":\"AddPerm\",",
								"\"Effect\":\"Allow\",",
								"\"Principal\": \"*\",",
								"\"Action\":[\"s3:GetObject\"],",
								"\"Resource\":[\"arn:aws:s3:::BUCKET/*\"]",
								"}",
								"]",
								"}"	
							);
		
		String customPolicy = policyTemplate.replaceAll("BUCKET", bucketName);
		//System.out.println("Custom Policy : " + customPolicy);
								
		return customPolicy;
	}
}
