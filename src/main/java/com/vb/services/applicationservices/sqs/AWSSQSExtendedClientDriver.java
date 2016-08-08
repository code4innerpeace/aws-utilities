package com.vb.services.applicationservices.sqs;

public class AWSSQSExtendedClientDriver {

	public static void main(String args[]) {
		AWSSQSExtendedClient awsSQSExtendedClient = new AWSSQSExtendedClient();
		String bucketName = "VASSQSExtendedS3Delete".toLowerCase();
		String queueName = "VASSQSExtendedQueueDelete".toLowerCase();
		
		awsSQSExtendedClient.createBucket(bucketName);
		awsSQSExtendedClient.createSQSQueue(queueName);
		awsSQSExtendedClient.sendMessageToTheQueue();
		awsSQSExtendedClient.readLargeMessages();
		awsSQSExtendedClient.deleteAWSResourcesCreated();
	}
}
