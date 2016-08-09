package com.vb.services.applicationservices.sqs;

public class AWSSQSLongShortPollingDriver {
	
	public static void main(String args[]) {
		AWSSQSLongShortPolling awsSQSLongShortPolling = new AWSSQSLongShortPolling();
		
		
		String queueName = "vasqueuepolling";
		String deadLetterQueueName = queueName + "-deadletterqueue";
		
		// Queue created with default attributes. Short polling by default.
		awsSQSLongShortPolling.createQueue(queueName);
		
		// Create dead letter queue.
		awsSQSLongShortPolling.createQueue(deadLetterQueueName);
		
		// Attached dead letter queue existing SQS queue.
		awsSQSLongShortPolling.enableDeadLetterQueue(5, deadLetterQueueName);
		
		// Queue created with default attributes. Long polling enabled.
		// awsSQSLongShortPolling.createQueue(queueName, 20l);
		String messageBody = "VAS Hello SQS";
		
		// Message without delay or message timer
		awsSQSLongShortPolling.sendMessage(queueName, messageBody);
		
		// Message with delay or message timer enabled. Delayed by 30 secs. Can be between 0 and 900.
		// awsSQSLongShortPolling.sendMessage(queueName, messageBody, 30);
		// Short Polling
		//awsSQSLongShortPolling.getMessage(queueName);
		
		// Enable Long polling on ReceiveMessage.
		awsSQSLongShortPolling.getMessage(queueName, 20l);
		
		// Deleting queue.
		//awsSQSLongShortPolling.deleteQueue(queueName);
		
		// Deleting deadletter queue.
		//awsSQSLongShortPolling.deleteQueue(deadLetterQueueName);
	
	}

}
