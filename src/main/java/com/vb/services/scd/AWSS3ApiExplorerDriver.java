package com.vb.services.scd;

import java.util.ArrayList;
import java.util.List;

public class AWSS3ApiExplorerDriver {
	
	public static void main(String args[]) {
		AWSS3ApiExplorer awsS3ApiExplorer = new AWSS3ApiExplorer();
		List<String> s3Buckets = new ArrayList<String>();
		//Add buckets.
		
		
		//Logging destination bucket name.
		String destinationBucketName = "";
		for(String bucket: s3Buckets) {
			System.out.println("====" + bucket + "====");
			awsS3ApiExplorer.createBucket(bucket);
			awsS3ApiExplorer.enableS3Logging(bucket, destinationBucketName);
			awsS3ApiExplorer.enableVersioning(bucket);
		}
		
	}
}
