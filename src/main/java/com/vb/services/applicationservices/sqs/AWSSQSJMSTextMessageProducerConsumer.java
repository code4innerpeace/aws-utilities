package com.vb.services.applicationservices.sqs;



import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

public class AWSSQSJMSTextMessageProducerConsumer {
	
		private SQSConnectionFactory sqsConnectionFactory;
		private AWSCredentialsProvider awsCredentialProvider;
		private SQSConnection sqsConnection;
		private String queueName;
		private String queueURL;
		private Session session;
		private MessageProducer producer;
		private MessageConsumer consumer;
	
		// Default constructor. Uses "default" profile under ~/.aws/credentials
		public AWSSQSJMSTextMessageProducerConsumer() {
			this(null);
		}
		
		// Uses the profile passed. The profileName must exists under "~/.aws/credentials".
		public AWSSQSJMSTextMessageProducerConsumer(String profileName) {	
			awsCredentialProvider = getProfileCredentialsProvider(profileName);
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
		
		// Create SQSConnectionFactory
		public void createSQSConnectionFactory() {
			this.sqsConnectionFactory = SQSConnectionFactory.builder()
										.withRegion(Region.getRegion(Regions.US_EAST_1))
										.withAWSCredentialsProvider(this.awsCredentialProvider)
										.build();
			System.out.println("Created SQSConnectionFactory...");
		}
		
		// Get SQSConnectionFactory
		public SQSConnectionFactory getSQSConnectionFactory() {
			return this.sqsConnectionFactory;
		}
		
		// Create the connection.
		public void createSQSConnection() {
			try {
				this.sqsConnection = this.sqsConnectionFactory.createConnection();
				System.out.println("Created SQSConnection....");
			} catch (JMSException e) {
				System.out.println("ERROR : SQSConnection couldn't be created.");
				e.printStackTrace();
			}
		}
		
		// Get the connection.
		public SQSConnection getSQSConnection() {
			return this.sqsConnection;
		}
		
		// Set queue name.
		public void setQueueName(String queueName) {
			this.queueName = queueName;
		}
		
		// Get queue name.
		public String getQueueName() {
			return this.queueName;
		}
		
		// Create the queue.
		public void createQueue() {
			AmazonSQSMessagingClientWrapper amazonSQSMessagingClientWrapper = getAmazonSQSMessagingClientWrapper();
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(this.queueName);
			try {
				if ( !checkIfQueueExists() ) {	
					CreateQueueResult createQueueResult = amazonSQSMessagingClientWrapper.createQueue(createQueueRequest);
					this.queueURL = createQueueResult.getQueueUrl();
					System.out.println("Queue : " + this.queueName + " had been created.");
				}
				else {
					System.out.println("Queue : " + this.queueName + " already exits, so using it.");
					this.queueURL = amazonSQSMessagingClientWrapper.getQueueUrl(this.queueName).getQueueUrl();
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR : Couldn't create queue : " + this.queueName);
				e.printStackTrace();
			}
		}
		
		// Check if queue exists.
		private boolean checkIfQueueExists() {
			boolean queueExists = false;
			AmazonSQSMessagingClientWrapper amazonSQSMessagingClientWrapper = getAmazonSQSMessagingClientWrapper();
			try {
				queueExists = amazonSQSMessagingClientWrapper.queueExists(this.queueName); 
				if ( queueExists ) {
					return queueExists;
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR : Couldn't check if queue exists or not.");
				e.printStackTrace();
			}
			return queueExists;
		}
		
		// Get AmazonSQSMessagingClientWrapper client.
		private AmazonSQSMessagingClientWrapper getAmazonSQSMessagingClientWrapper() {
			return this.sqsConnection.getWrappedAmazonSQSClient();
		}
		
		// Create session.
		public void createSession() {
			try {
				this.session = this.sqsConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR : Couldn't create SESSION.");
				e.printStackTrace();
			}
			
		}
		
		// Get the session.
		public Session getSession() {
			return this.getSession();
		}
		
		// Create Producer
		public void createProducer() {
			try {
				this.producer = this.session.createProducer(this.session.createQueue(this.queueName));
				if ( this.producer == null ) {
					System.out.println("Producer is null.");
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		// Get Producer
		public MessageProducer getProducer() {
			return this.producer;
		}
		
		public void sendMessage(String message) {
			try {
				TextMessage textMessage = this.session.createTextMessage(message);
				System.out.println("Created TextMessage...");
				
				this.producer.send(textMessage);
				System.out.println("Text Message had been sent..");
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR : couldn't create text message");
				e.printStackTrace();
			}
		}

		// Consumer Code.
		
}
