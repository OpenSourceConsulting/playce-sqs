package com.athena.sqs;

import java.io.IOException;
import java.util.Date;
import java.util.List;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

/**
 * Receives message from Amazon SQS. 
 * Amazon SQS supports only polling method to retrieve the message in queues.
 * 
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
@Service
@DependsOn("messageContext")
public class MessageReceiver {
	private final Logger logger = Logger.getLogger(this.getClass());
	private static final long DEFAULT_POLLING_INTERVAL = 3000;  // 3 seconds interval
	
	private long pollingFrequency;
	
	private @Autowired MessageContext messageContext;
	private @Autowired MessageAggregator aggregator;
	
	private AmazonSQSClient client;
	
	public MessageReceiver() {
		this(DEFAULT_POLLING_INTERVAL);
	}
	
	@PostConstruct
	public void initialize() {
		client = messageContext.getSqsClient();
	}
		
	
	public MessageReceiver(long pollingFrequency) {
		this.pollingFrequency = pollingFrequency;
	}
	
	public void doReceive(String queueName) throws Exception {
		String queueUrl = messageContext.getQueue(queueName);
		logger.debug("Receiving a message to [" + queueName + "][" + queueUrl);
		
		// Receive messages
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);

		while (true) {
			List<com.amazonaws.services.sqs.model.Message> messages = client.receiveMessage(receiveMessageRequest.withMaxNumberOfMessages(10)).getMessages();
			logger.debug(new Date() + " : message count : " + messages.size());
			
			if( messages.size() > 0) {
				
				for (com.amazonaws.services.sqs.model.Message message : messages) {
//	                logger.debug("  Message");
//	                logger.debug("    MessageId:     " + message.getMessageId());
//	                logger.debug("    ReceiptHandle: " + message.getReceiptHandle());
//	                logger.debug("    MD5OfBody:     " + message.getMD5OfBody());
//	                logger.debug("    Body:          " + message.getBody());
//	                for (Entry<String, String> entry : message.getAttributes().entrySet()) {
//	                    logger.debug("  Attribute");
//	                    logger.debug("    Name:  " + entry.getKey());
//	                    logger.debug("    Value: " + entry.getValue());
//	                }
					logger.debug("  Message");
					String body = message.getBody();
					
					aggregator.aggregate(body);						
					
	                client.deleteMessage(new DeleteMessageRequest(queueUrl, message.getReceiptHandle()));
	            }
				
			} else {
				logger.debug("Nothing found, trying again in 3 seconds");
				Thread.sleep(pollingFrequency);
			}
		}
	}

	/**
	 * Disconnect from sqs
	 * @throws IOException
	 */
	@PreDestroy
	public void doDisconnect() throws IOException {
		if( client != null ) client.shutdown();
	}
}
