package com.vb.services.applicationservices.sqs;

import java.util.Arrays;
import java.util.List;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteVersionRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.InvalidMessageContentsException;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDeletedRecentlyException;
import com.amazonaws.services.sqs.model.QueueNameExistsException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class AWSSQSExtendedClient {
	
		private AWSCredentialsProvider awsCredentialProvider;
		private AmazonS3 amazonS3Client;
		private String s3BucketName;
		private ExtendedClientConfiguration extendedClientConfiguration;
		private AmazonSQS sqsExtended;
		private String queueName;
		private String queueURL;
		
		// Default constructor. Uses "default" profile under ~/.aws/credentials
	
		public AWSSQSExtendedClient() {
			this(null);
		}
		
		// Uses the profile passed. The profileName must exists under "~/.aws/credentials".
		public AWSSQSExtendedClient(String profileName) {	
			this.awsCredentialProvider = getProfileCredentialsProvider(profileName);
			createAmazonS3Client();
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
		
		// Private Create AmazonS3 client.
		private void createAmazonS3Client() {
			this.amazonS3Client = new AmazonS3Client(this.awsCredentialProvider);
			System.out.println("Created AmazonS3Client..");
		}
		
		// This method creates the Buckets and configures S3 as back up for SQS messages > 256KB.
		public void createBucket(String s3BucketName) {
			try {
				this.s3BucketName = s3BucketName;
				this.amazonS3Client.createBucket(s3BucketName);
				System.out.println("S3 Bucket : " + s3BucketName + " has been created.");
				System.out.println("Configuring S3 Bucket : " + s3BucketName + " as backup for SQS messages > 256KB");
				configureS3AsBackupForSQSMessagesGreaterThan256KB();
			} catch(AmazonServiceException ase) {
				System.out.println("ERROR : " + s3BucketName + " couldn't be created.");
				ase.printStackTrace();
				System.exit(1);
			} catch(AmazonClientException ace) {
				ace.printStackTrace();
				System.exit(1);
			}
		}
		
		// Configures S3 as backup for SQS messages > 256KB.
		private void configureS3AsBackupForSQSMessagesGreaterThan256KB() {
			this.extendedClientConfiguration = new ExtendedClientConfiguration().withLargePayloadSupportEnabled(this.amazonS3Client, this.s3BucketName);
			AmazonSQSClient amazonSQSClient = new AmazonSQSClient(this.awsCredentialProvider);
			this.sqsExtended = new AmazonSQSExtendedClient(amazonSQSClient,this.extendedClientConfiguration);
			System.out.println("Confured S3 Bucket : " + this.s3BucketName + " as back up for SQS messages larger than 256KB.");
		}
		
		// Creating message greater than 256KB.
		public String getMessageGreaterThan256KB() {
			int stringLength = 500000;
		    char[] chars = new char[stringLength];
		    Arrays.fill(chars, 'V');
		    String myLongString = new String(chars);
		    return myLongString;
		}
		
		// Create SQS Queue.
		public void createSQSQueue(String queueName) {
			this.queueName = queueName;
			CreateQueueRequest createQueueRequest = new CreateQueueRequest().withQueueName(this.queueName);
			try {
				this.queueURL = this.sqsExtended.createQueue(createQueueRequest).getQueueUrl();
				System.out.println("QUEUE : " + this.queueName + " has been created. URL of the queue is : " + this.queueURL);
			}catch(QueueNameExistsException qnee) {
				System.out.println("ERROR : Queue " + queueName + " already exists.");
				qnee.printStackTrace();
			}catch(QueueDeletedRecentlyException qdre) {
				System.out.println("ERROR : Queue " + queueName + " had been deleted recently. Wait for few seconds to recreate it.");
				qdre.printStackTrace();
			}
		}
		
		// Send message greater than 256KB to queue.
		public void sendMessageToTheQueue() {
			String myLongString = getMessageGreaterThan256KB();
			SendMessageRequest sendMessageRequest = new SendMessageRequest(this.queueURL, myLongString);
			try {
				this.sqsExtended.sendMessage(sendMessageRequest);
				System.out.println("Large message greater than 256KB had been sent to the queue.");
			} catch(InvalidMessageContentsException imce) {
				System.out.println("ERROR InvalidMessageContentsException : sending message to Queue : " + this.queueName);
				imce.printStackTrace();
			}catch(UnsupportedOperationException uoe) {
				System.out.println("ERROR UnsupportedOperationException : sending message to Queue : " + this.queueName);
				uoe.printStackTrace();
			}
		}
		
		// read large messages from S3 using SQS Extended client.
		public void readLargeMessages() {
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(this.queueURL);
			List<Message> messages = this.sqsExtended.receiveMessage(receiveMessageRequest).getMessages();
			for (Message message : messages) {
			      System.out.println("\nMessage received:");
			      System.out.println("  ID: " + message.getMessageId());
			      System.out.println("  Receipt handle: " + message.getReceiptHandle());
			      System.out.println("  Message body (first 5 characters): " + message.getBody().substring(0, 5));
			    }
		}
		
		// Delete SQS queue and S3 Objects
		public void deleteAWSResourcesCreated() {
			System.out.println("Deleting AWS resources created for this POC.");
			deleteSQSQueue();
			deleteS3Bucket();
		}
		
		// Delete SQS queue.
		private void deleteSQSQueue() {
			DeleteQueueRequest deleteQueueRequest = new DeleteQueueRequest(this.queueURL);
			try {
				//this.sqsExtended.deleteQueue(deleteQueueRequest);
				new AmazonSQSClient(this.awsCredentialProvider).deleteQueue(deleteQueueRequest);
				System.out.println("Queue : " + this.queueName + " has been deleted.");
			} catch(AmazonServiceException ase) {
				System.out.println("ERROR : exception during deleting the queue : " + this.queueName);
				ase.printStackTrace();
			} catch(AmazonClientException ace) {
				System.out.println("ERROR : exception during deleting the queue : " + this.queueName);
				ace.printStackTrace();
			}
		}
		
		// Delete S3 bucket.
		private void deleteS3Bucket() {
			
			DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(this.s3BucketName);
			try {
				deleteS3BucketContents();
				this.amazonS3Client.deleteBucket(deleteBucketRequest);
				System.out.println("S3 Bucket : " + this.s3BucketName + " has been deleted.");
			}catch(AmazonServiceException ase) {
				System.out.println("ERROR : exception during deleting the s3bucket : " + this.s3BucketName);
				ase.printStackTrace();
			} catch(AmazonClientException ace) {
				System.out.println("ERROR : exception during deleting the s3bucket : " + this.s3BucketName);
				ace.printStackTrace();
			}
		}
		
		// Delete S3 Bucket contents
		private void deleteS3BucketContents() {
			deleteS3BucketContentsVersions();
			System.out.println("Deleting S3 Objects for the bucket: " + this.s3BucketName);
			ObjectListing objectListing = this.amazonS3Client.listObjects(this.s3BucketName);;
			boolean firstRun = true;
			do {
				if ( !firstRun) {
					objectListing = this.amazonS3Client.listNextBatchOfObjects(objectListing);
				}
				List<S3ObjectSummary> listObjectSummaries = objectListing.getObjectSummaries();
				for(S3ObjectSummary s3ObjectSummary: listObjectSummaries) {
					this.amazonS3Client.deleteObject(this.s3BucketName, s3ObjectSummary.getKey());
				}
				
			} while(objectListing.isTruncated());
		}
		
		// Delete S3 bucket contents versions.
		private void deleteS3BucketContentsVersions() {
			ListVersionsRequest listVersionsRequest = new ListVersionsRequest().withBucketName(this.s3BucketName);
			System.out.println("Deleting versions of S3 Objects for the bucket: " + this.s3BucketName);
			try {
				VersionListing versionListing = this.amazonS3Client.listVersions(listVersionsRequest);
				List<S3VersionSummary> listVersionSummaries = versionListing.getVersionSummaries();
				
				for(S3VersionSummary s3VersionSummary: listVersionSummaries) {
					DeleteVersionRequest deleteVersionRequest = new DeleteVersionRequest(this.s3BucketName, s3VersionSummary.getKey(), s3VersionSummary.getVersionId());
					this.amazonS3Client.deleteVersion(deleteVersionRequest);
				}
			}catch(AmazonServiceException ase) {
				System.out.println("ERROR : exception listing verions/deleting versions for the bucket : " + this.s3BucketName);
				ase.printStackTrace();
			} catch(AmazonClientException ace) {
				System.out.println("ERROR : exception listing verions/deleting versions for the bucket : " + this.s3BucketName);
				ace.printStackTrace();
			}
		}
			
			
		
			

}
