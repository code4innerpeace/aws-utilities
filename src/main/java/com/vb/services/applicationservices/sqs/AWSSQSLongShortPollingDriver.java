package com.vb.services.applicationservices.sqs;

public class AWSSQSLongShortPollingDriver {
	
	public static void main(String args[]) {
		AWSSQSLongShortPolling awsSQSLongShortPolling = new AWSSQSLongShortPolling();
		String queueName = "vasqueuepolling";
		
		// Queue created with default attributes. Short polling by default.
		awsSQSLongShortPolling.createQueue(queueName);
		
		// Queue created with default attributes. Long polling enabled.
		// awsSQSLongShortPolling.createQueue(queueName, 20l);
		String messageBody = "VAS Hello SQS";
		awsSQSLongShortPolling.sendMessage(queueName, messageBody);
		// Short Polling
		//awsSQSLongShortPolling.getMessage(queueName);
		
		// Enable Long polling on ReceiveMessage.
		awsSQSLongShortPolling.getMessage(queueName, 20l);
		awsSQSLongShortPolling.deleteQueue(queueName);
	}

}
