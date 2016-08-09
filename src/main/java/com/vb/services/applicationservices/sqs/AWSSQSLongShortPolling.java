package com.vb.services.applicationservices.sqs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.InvalidAttributeNameException;
import com.amazonaws.services.sqs.model.InvalidIdFormatException;
import com.amazonaws.services.sqs.model.InvalidMessageContentsException;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.OverLimitException;
import com.amazonaws.services.sqs.model.QueueDeletedRecentlyException;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.QueueNameExistsException;
import com.amazonaws.services.sqs.model.ReceiptHandleIsInvalidException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import com.vb.services.identitymanagement.CustomAWSCredentialsProvider;

public class AWSSQSLongShortPolling {

	private AmazonSQSClient amazonSQSClient;
	
	
	private final String VISIBILITY_TIMEOUT = "30";
	private final String MESSAGE_RETENTION_PERIOD = "345600";
	private final String MAXIMUM_MESSAGE_SIZE = "262144";
	private final String DELAY_SECONDS = "0";
	private final String RECEIVE_MESSAGE_WAIT_TIME_SECONDS = "0";
	
	
	public AWSSQSLongShortPolling() {
		this(null);
	}
	
	public AWSSQSLongShortPolling(String profileName) {
		this.amazonSQSClient = new AmazonSQSClient(CustomAWSCredentialsProvider.getProfileCredentialsProvider(profileName));
	}
	
	// This method creates the queue with default queue attributes.
	public void createQueue(String queueName) {
		Map<String,String> defaultQueueAttributes = getDefaultQueueAttributes();
		createQueue(queueName,defaultQueueAttributes);
	}
	
	// This method creates the queue with provided queue attributes.
	public void createQueue(String queueName, Map<String, String> queueAttributes) {
		
		CreateQueueRequest createQueueRequest = new CreateQueueRequest().withQueueName(queueName);
		createQueueRequest.setAttributes(getDefaultQueueAttributes());
		String queueURL;
		try {
			this.amazonSQSClient.createQueue(createQueueRequest);
			System.out.println("Queue : " + queueName + " has been created sucessfully.");
		} catch(QueueNameExistsException qnee) {
			System.out.println("ERROR: queue : " + queueName + " already exists.");
			//queueURL = this.amazonSQSClient.getQueueUrl(queueName).getQueueUrl();
			queueURL = this.getQueueUrl(queueName);
			System.out.println("Queue URL : " + queueURL);
		} catch(QueueDeletedRecentlyException qdre) {
			System.out.println("ERROR: queue : " + queueName + " had been deleted recently. Please wait few minutes.");
			System.exit(1);
		}
	}
	
	// This method creates the queue with long polling.
	public void createQueue(String queueName, long longPollingTimeInSecsLessThan20) {
		if ( longPollingTimeInSecsLessThan20 <= 0 && longPollingTimeInSecsLessThan20 > 20) {
			throw new IllegalArgumentException("ERROR : Long polling time secs > 0 and <= 20");
		}
		Map<String, String> queueDefaultAttributes = getDefaultQueueAttributes();
		queueDefaultAttributes.put("ReceiveMessageWaitTimeSeconds", new Long(longPollingTimeInSecsLessThan20).toString());
		createQueue(queueName, queueDefaultAttributes);
	}
	
	// This method checks if queue exists or not.
	private boolean checkIfQueueAlreadyExists(String queueName) {
		boolean queueExists = false;
		GetQueueUrlRequest getQueueUrlRequest = new  GetQueueUrlRequest(queueName);
		try {
			this.amazonSQSClient.getQueueUrl(getQueueUrlRequest);
			queueExists = true;
		} catch(QueueDoesNotExistException qdnee) {
			System.out.println("Queue : " + queueName + " doesn't exist.");
		} 
		
		return queueExists;
		
	}
	
	// This method creates default Queue Attributes.
	public Map<String,String> getDefaultQueueAttributes() {
		
		Map<String,String> queueAttributes = new HashMap<String,String>();
		queueAttributes.put("VisibilityTimeout", VISIBILITY_TIMEOUT);
		queueAttributes.put("MessageRetentionPeriod", MESSAGE_RETENTION_PERIOD);
		queueAttributes.put("MaximumMessageSize", MAXIMUM_MESSAGE_SIZE);
		queueAttributes.put("DelaySeconds", DELAY_SECONDS);
		
		// Default long polling disabled.
		queueAttributes.put("ReceiveMessageWaitTimeSeconds", RECEIVE_MESSAGE_WAIT_TIME_SECONDS);
		return queueAttributes;
		
	}
	
	// This method update queue Attributes
	public void updateQueueAttributes(String queueName, Map<String, String> queueAttributes) {

		try { 
			//String queueUrl = this.amazonSQSClient.getQueueUrl(queueName).getQueueUrl();
			String queueUrl = this.getQueueUrl(queueName);
			
			if ( queueUrl == null ) {
				throw new NullPointerException("ERROR : QueueUrl is null.");
			}
			SetQueueAttributesRequest setQueueAttributesRequest = new SetQueueAttributesRequest()
																	.withQueueUrl(queueUrl)
																	.withAttributes(queueAttributes);
			this.amazonSQSClient.setQueueAttributes(setQueueAttributesRequest);
		} catch (QueueDoesNotExistException qdnee) {
				System.out.println("Queue : " + queueName + " doesn't exist.");
		} catch(AmazonServiceException ase) {
			System.out.println("ERROR : Couldn't update queue : " + queueName + " properties.");
			ase.printStackTrace();
		} catch(AmazonClientException ace) {
			System.out.println("ERROR : Couldn't update queue : " + queueName + " properties.");
			ace.printStackTrace();
		}
	}
		
	// This method sends message to queue.
	public void sendMessage(String queueName, String messageBody) {
		int delaySeconds = 0;
		sendMessage(queueName, messageBody, delaySeconds);
	}
	
	// This method sends message with delay.
	public void sendMessage(String queueName, String messageBody, int delaySeconds) {
		
		if ( delaySeconds < 0 && delaySeconds > 900) {
			throw new IllegalArgumentException("DelaySeconds can be >0 and <= 900");
		}
		try { 
			//String queueUrl = this.amazonSQSClient.getQueueUrl(queueName).getQueueUrl();
			String queueUrl = this.getQueueUrl(queueName);
			if ( queueUrl == null ) {
				throw new NullPointerException("ERROR : QueueUrl is null.");
			}
			SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, messageBody);			
			// Delay message with delaySecs > 0.
			if ( delaySeconds > 0 ) {
				System.out.println("Sending message is delayed by : " + delaySeconds + " seconds, as delaySeconds paramater had been provided.");
				sendMessageRequest.setDelaySeconds(delaySeconds);
			} else {
				System.out.println("Sending message with out any delay.");
			}
			SendMessageResult sendMessageResult = this.amazonSQSClient.sendMessage(sendMessageRequest);
			System.out.println("Message had been sent to the queue : " + queueName);
			System.out.println("Message Id : " + sendMessageResult.getMessageId());
			 
		} catch (QueueDoesNotExistException qdnee) {
			System.out.println("Queue : " + queueName + " doesn't exist.");
			qdnee.printStackTrace();
		} catch(InvalidMessageContentsException imce) {
			System.out.println("ERROR : Invalid Message Contents Exception");
			imce.printStackTrace();
		} catch(UnsupportedOperationException uoe) {
			System.out.println("ERROR : Unsupported Operation Exception.");
			uoe.printStackTrace();
		} catch(AmazonServiceException ase) {
			System.out.println("ERROR : Couldn't update queue : " + queueName + " properties.");
			ase.printStackTrace();
		} catch(AmazonClientException ace) {
			System.out.println("ERROR : Couldn't update queue : " + queueName + " properties.");
			ace.printStackTrace();
		}
	}
	
	// This method returns true if longPolling is enabled.
	public boolean isLongPollingEnabled(String queueName) {
		boolean longPollingEnabled = false;
		String queueUrl = this.getQueueUrl(queueName); 
		if ( queueUrl == null ) {
			throw new NullPointerException("ERROR : QueueUrl is null.");
		}
		GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(queueUrl);

		try {
			
			Map<String,String> queueAttributes = this.getQueueAttributesAll(queueName);
			if ( queueAttributes == null) {
				throw new NullPointerException("queueAttributes is null.");
			}
			System.out.println("queueAttributes : " + queueAttributes);
			String stringLongPollingTimeInSecs = queueAttributes.get("ReceiveMessageWaitTimeSeconds");
			if ( stringLongPollingTimeInSecs != null ) {
				long longPollingTimeInSecs = new Long(stringLongPollingTimeInSecs);
			
				if ( longPollingTimeInSecs > 0 && longPollingTimeInSecs <= 20) {
					longPollingEnabled = true;
				}
			} else {
				throw new NullPointerException("stringLongPollingTimeInSecs is null");
			}
		} catch( InvalidAttributeNameException iane) {
			System.out.println("ERROR :  Invalid Attribute Name Exception");
			iane.printStackTrace();
		}
		
		return longPollingEnabled;
	}
	
	// This method returns queue url.
	public String getQueueUrl(String queueName) {
		String queueUrl = null;
	
		try { 
			 queueUrl = this.amazonSQSClient.getQueueUrl(queueName).getQueueUrl();
		}catch (QueueDoesNotExistException qdnee) {
			System.out.println("Queue : " + queueName + " doesn't exist.");
			qdnee.printStackTrace();
		}
		return queueUrl;
	}
	
	// This method receives message from Queue.
	public void getMessage(String queueName) {
		String queueUrl = this.getQueueUrl(queueName);
		if ( queueUrl == null ) {
			throw new NullPointerException("ERROR : QueueUrl is null.");
		}
		ReceiveMessageRequest receiveMessageRequest  = new ReceiveMessageRequest(queueUrl);
		System.out.println("Receiving messages using short polling");
		processMessage(queueUrl, receiveMessageRequest);
	}
	
	// This method receives message from Queue, enable long polling if not enabled on Queue.
	public void getMessage(String queueName, long longPollingTimeInSecs) {
		String queueUrl = this.getQueueUrl(queueName);
		ReceiveMessageRequest receiveMessageRequest  = new ReceiveMessageRequest(queueUrl);
		if ( longPollingTimeInSecs <= 0 && longPollingTimeInSecs > 0) {
			throw new IllegalArgumentException("Long polling time in secs should > 0 and <=20");
		}
		
		if ( this.isLongPollingEnabled(queueName) ) {
			System.out.println("Long polling already enabled, nothing to do.");
		} else {
			System.out.println("Updating ReceiveMessage with long polling : " + longPollingTimeInSecs);
			receiveMessageRequest.setWaitTimeSeconds(Integer.valueOf((int)longPollingTimeInSecs));
		}
		System.out.println("Receiving messages using long polling");
		processMessage(queueUrl,receiveMessageRequest);
		
	}
	
	// This method process the messages.
	private void processMessage(String queueUrl, ReceiveMessageRequest receiveMessageRequest) {
		try {
			ReceiveMessageResult receiveMessageResult = this.amazonSQSClient.receiveMessage(receiveMessageRequest);
			List<Message> listMessages = receiveMessageResult.getMessages();
			System.out.println("Processing the messages..");
			for(Message message: listMessages) {
				 System.out.println("  Message");
	             System.out.println("    MessageId:     " + message.getMessageId());
	             System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
	             System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
	             System.out.println("    Body:          " + message.getBody());
			}
			
			String messageReceiptHandle = listMessages.get(0).getReceiptHandle();
			deleteMessage(queueUrl, messageReceiptHandle);
		} catch(OverLimitException ole) {
			System.out.println("ERROR : OverLimitException");
			ole.printStackTrace();
		}
	}
	
	// This message deletes the message.
	private void deleteMessage(String queueUrl, String messageReceiptHandle) {
		System.out.println("Processed the message, now deleting it...");
		DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(queueUrl,messageReceiptHandle);
		try {
			this.amazonSQSClient.deleteMessage(deleteMessageRequest);
		} catch(InvalidIdFormatException iife) {
			System.out.println("ERROR : Deleting the message.");
			iife.printStackTrace();
		} catch(ReceiptHandleIsInvalidException rhiie) {
			System.out.println("ERROR : Deleting the message.");
			rhiie.printStackTrace();
		}
	}

	// This method returns queue attribtues mentioned.
	public Map<String,String>  getQueueAttributes(String queueName, List<String> attributeNames) {
		String queueUrl = this.getQueueUrl(queueName);
		Map<String,String> queueAttributes;
		GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(queueUrl,attributeNames);
		try {
			GetQueueAttributesResult getQueueAttributesResult = this.amazonSQSClient.getQueueAttributes(getQueueAttributesRequest);
			return getQueueAttributesResult.getAttributes();
		}catch( InvalidAttributeNameException iane) {
			System.out.println("ERROR :  Invalid Attribute Name Exception");
			iane.printStackTrace();
		}
		return null;
	}
	
	// This method returns all queue attributes.
	public Map<String,String>  getQueueAttributesAll(String queueName) {
		
		String queueUrl = this.getQueueUrl(queueName);
		List<String> attributeNames = new ArrayList<String>();
		attributeNames.add("All");
		return getQueueAttributes( queueName,attributeNames);
	}
	
	// This method deletes the queue.
	public void deleteQueue(String queueName) {
		String queueUrl = this.getQueueUrl(queueName);
		DeleteQueueRequest deleteQueueRequest = new DeleteQueueRequest(queueUrl);
		try {
			this.amazonSQSClient.deleteQueue(deleteQueueRequest);
			System.out.println("Queue : " + queueName + " had been deleted.");
		}catch(AmazonServiceException ase) {
			System.out.println("ERROR : Couldn't delete queue : " + queueName);
			ase.printStackTrace();
		} catch(AmazonClientException ace) {
			System.out.println("ERROR : Couldn't delete queue : " + queueName );
			ace.printStackTrace();
		}
	}
	
}
