package com.vb.services.scd;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import com.amazonaws.services.s3.model.SetBucketLoggingConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;

public class AWSS3ApiExplorer {
	
		private AmazonS3Client amazonS3Client;
		
		// Default constructor. Uses "default" profile under ~/.aws/credentials
		public AWSS3ApiExplorer() {
			this(null);
		}
		
		// Uses the profile passed. The profileName must exists under "~/.aws/credentials".
		public AWSS3ApiExplorer(String profileName) {
			
			AWSCredentialsProvider awsCredentialProvider = getProfileCredentialsProvider(profileName);
			this.amazonS3Client = new AmazonS3Client(awsCredentialProvider);
			
		}
		
		// This method returns the AWSCredentialsProvider.
		private AWSCredentialsProvider getProfileCredentialsProvider(String profileName) {
			
			ProfileCredentialsProvider profileCredentialsProvider;
			
			//Use "default" profile.
			if ( profileName == null ) {					
				profileCredentialsProvider = new ProfileCredentialsProvider();
			} else {
				profileCredentialsProvider = new ProfileCredentialsProvider(profileName);		
			}
			
			return profileCredentialsProvider;
		}
		
		// This method return S3 Bucket if bucketName is provided.
		private Bucket getBucket(String bucketName) {
			
			try {
				if( this.amazonS3Client.doesBucketExist(bucketName)) {
					List<Bucket> buckets = this.amazonS3Client.listBuckets();
					for(Bucket bucket: buckets) {
						if ( bucket.getName().equals(bucketName)) {
							return bucket;
						}
					}
				} else {
					throw new AmazonServiceException("S3 bucket : " + bucketName + " doesn't exist.");
				}
			}catch(AmazonServiceException ase) {
				ase.printStackTrace();
			}catch(AmazonClientException ace) {
				ace.printStackTrace();
			} 
			
			return null;
		}
		
		// This method creates S3 Bucket
		public Bucket createBucket(String bucketName) {
			Bucket bucket = null;
			try {
				bucket = this.amazonS3Client.createBucket(bucketName);
				Thread.sleep(3000);
			} catch(AmazonServiceException ase) {
				ase.printStackTrace();
			} catch(AmazonClientException ace) {
				ace.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return bucket;
		}
		
		// This method creates S3 Bucket
		public Bucket createBucket(String bucketName, Region region) {
			Bucket bucket = null;
			try {
				bucket = this.amazonS3Client.createBucket(bucketName, region);
			} catch(AmazonServiceException ase) {
				ase.printStackTrace();
			} catch(AmazonClientException ace) {
				ace.printStackTrace();
			} 
			return bucket;		
		}
		
		// This method creates S3 Bucket
		public Bucket createBucket(String bucketName, String region) {
			Bucket bucket = null;
			try {
				bucket = this.amazonS3Client.createBucket(bucketName, region);
			} catch(AmazonServiceException ase) {
				ase.printStackTrace();
			} catch(AmazonClientException ace) {
				ace.printStackTrace();
			} 
			return bucket;		
		}
		
		// This method creates multiple S3 Bucket.
		public List<Bucket> createMultipleBuckets(List<String> bucketNames) {
			List<Bucket> multipleBuckets = new ArrayList<Bucket>();
			for( Bucket bucket: multipleBuckets) {
				multipleBuckets.add(bucket);
			}
			return multipleBuckets;
		}
		
		// This method enables logging on particular S3 bucket.
		// Caller of method doesn't provide logging destination S3 bucket name, so we will create one with "<bucketName>-logs" and default prefix "/logs".
		public void enableS3Logging(String bucketName) {
			String destinationBucketName = bucketName + "-s3-logs";
			String logFilePrefix = "logs/";
			
			if ( this.amazonS3Client.doesBucketExist(destinationBucketName) ) {
				String exceptionMessage = "Please provide S3 logging destination bucket Name. We are trying to create destination S3 bucket with name : " + destinationBucketName + " but this bucket already exists.";
				throw new AmazonServiceException(exceptionMessage);
			} else {
				this.createBucket(destinationBucketName);
				System.out.println("Logging Destination S3 bucket : " + destinationBucketName + " had been created.");
				this.enableS3Logging(bucketName, destinationBucketName, logFilePrefix);
			}
			
		}
		
		
		// This method enables logging on particular S3 bucket.
		// Caller of method doesn't provide logging prefix, so we will use default prefix "/logs".
		public void enableS3Logging(String bucketName,String destinationBucketName) {
			String logFilePrefix = "logs/";
			this.enableS3Logging(bucketName,destinationBucketName,logFilePrefix);
		}
		
		// This method enables logging on particular S3 Bucket.
		// Caller of the method provides logging bucket name and log file prefix.
		public void enableS3Logging(String bucketName, String destinationBucketName, String logFilePrefix) {
			try {
				if( this.amazonS3Client.doesBucketExist(bucketName)) {
					
					boolean doesDestinationBucketExists = this.amazonS3Client.doesBucketExist(destinationBucketName);
					if ( !doesDestinationBucketExists ) {
						throw new AmazonServiceException("Log Destination bucket : " + destinationBucketName + " doesn't exist.");
					}
					
					//Enable Log Delivery ACL on destination bucket.
					SetBucketAclRequest setBucketAclRequest = this.getLogDeliveryBucketAclRequest(destinationBucketName);
					this.setBucketAcl(setBucketAclRequest);
					BucketLoggingConfiguration bucketLoggingConfiguration = new BucketLoggingConfiguration(destinationBucketName,logFilePrefix);
					SetBucketLoggingConfigurationRequest setBucketLoggingConfigurationRequest = new SetBucketLoggingConfigurationRequest(bucketName,bucketLoggingConfiguration);
					this.amazonS3Client.setBucketLoggingConfiguration(setBucketLoggingConfigurationRequest);
					System.out.println("Logging had been enabled on : " + bucketName + " and logs are being written to S3 destination bucket : " + destinationBucketName + " folder : " + logFilePrefix);
					
				} else {
					throw new AmazonServiceException("S3 bucket : " + bucketName + " doesn't exist.");
				}
			}catch(AmazonServiceException ase) {
				ase.printStackTrace();
			}catch(AmazonClientException ace) {
				ace.printStackTrace();
			} 
		}
		
		// This method enables logging on all S3 buckets.
		
		// Set S3 Bucket ACL
		public void setBucketAcl(SetBucketAclRequest setBucketAclRequest) {
			try {
				this.amazonS3Client.setBucketAcl(setBucketAclRequest);
				System.out.println("Create bucket acl request.");
			}catch(AmazonServiceException ase) {
				ase.printStackTrace();
			}catch(AmazonClientException ace) {
				ace.printStackTrace();
			} 
		}
		
		// Create and get Log Delivery Bucket ACL Request
		private SetBucketAclRequest getLogDeliveryBucketAclRequest(String bucketName) {
			SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(bucketName, CannedAccessControlList.LogDeliveryWrite);
			System.out.println("Created log delivery bucket acl request.");
			return setBucketAclRequest;
		}
		
		// This method enables versioning.
		public void enableVersioning(String bucketName) {
				this.enableSuspendVersioning(bucketName, true);
				System.out.println("Versioning had been enabled on bucket : " + bucketName);
		}
		
		// This method disables versioning.
		public void suspendVersioning(String bucketName) {
			this.enableSuspendVersioning(bucketName, false);
			System.out.println("Versioning had been disabled on bucket : " + bucketName);
		}
		
		// This method enables or disables versioning based on boolean flag passed on.
		private void enableSuspendVersioning(String bucketName, boolean enableVersioning) {
			
			BucketVersioningConfiguration bucketVersioningConfiguration = new BucketVersioningConfiguration();
			if ( enableVersioning ) {
				bucketVersioningConfiguration.setStatus(BucketVersioningConfiguration.ENABLED);
			} else {
				bucketVersioningConfiguration.setStatus(BucketVersioningConfiguration.SUSPENDED);
			}
			
			
			SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(bucketName, bucketVersioningConfiguration);
			
			try {
				this.amazonS3Client.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);
			} catch(AmazonServiceException ase) {
				ase.printStackTrace();
			}catch(AmazonClientException ace) {
				ace.printStackTrace();
			} 
			
		}
}
