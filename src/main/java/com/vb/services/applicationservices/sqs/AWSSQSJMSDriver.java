package com.vb.services.applicationservices.sqs;

public class AWSSQSJMSDriver {

	public static void main(String args[]) {
		
		AWSSQSJMSTextMessageProducerConsumer awsSQSJMSTextMessageProducerConsumer = new AWSSQSJMSTextMessageProducerConsumer();
		String queueName = "VASTestQueue";
		String message = "Hello VAS!";
		
		//Sending Message to queue.
		sendMessage(awsSQSJMSTextMessageProducerConsumer,queueName,message);
		
		//Consuming message from queue.
		consumeMessages(awsSQSJMSTextMessageProducerConsumer);
	}
	
	// Send Messages.
	private static void sendMessage(AWSSQSJMSTextMessageProducerConsumer awsSQSJMSTextMessageProducerConsumer,String queueName, String message) {
		System.out.println("===Creating Messages===");
		awsSQSJMSTextMessageProducerConsumer.createSQSConnectionFactory();
		awsSQSJMSTextMessageProducerConsumer.createSQSConnection();
		awsSQSJMSTextMessageProducerConsumer.createSession();
		awsSQSJMSTextMessageProducerConsumer.setQueueName(queueName);
		awsSQSJMSTextMessageProducerConsumer.createQueue();
		awsSQSJMSTextMessageProducerConsumer.createProducer();
		awsSQSJMSTextMessageProducerConsumer.sendMessage(message);
	}
	
	// Consume Messages.
	private static void consumeMessages(AWSSQSJMSTextMessageProducerConsumer awsSQSJMSTextMessageProducerConsumer) {
		
	}
}
