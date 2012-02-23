package com.athena.sqs;


import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;


/**
 * All developers will use this class to send query message that have to insert data into each region or global database.
 * This has various send methods that allows you to handle message flexibly.
 *
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */

@Service
@DependsOn("messageContext")
public class MessageDispatcher {
	private final Logger logger = Logger.getLogger(this.getClass());

	private @Autowired MessageContext messageContext;

	private BASE64Encoder encoder = new BASE64Encoder();

	private AmazonSQSClient client;

	@PostConstruct
	public void initialize() {
		client = messageContext.getSqsClient();
	}

	public void doProcess(String queueName, Message message) throws MessageException {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			// Message to JSON String
			String jsonString = mapper.writeValueAsString(message);
			if( logger.isDebugEnabled()) {
				logger.debug("*************************************************************");
				logger.debug(jsonString);
				logger.debug("*************************************************************");
			}

			doSend(queueName, jsonString);
			logger.debug("Message is successfulyy sent to SQS [" + queueName + "]");

		} catch (Exception e) {
			throw new MessageException(MessageFormat.format(MessageErrors.INTERNAL_ERROR.getDescription(), e.getMessage()));
		} finally{

		}
	}

	/**
	 * Send message to amazon sqs
	 * @param queueName
	 * @param messages
	 * @throws MessageException
	 */
	public void doSend(String queueName, String jsonString) throws MessageException {
		String transactionId = UUID.randomUUID().toString();

		try {
		
			logger.debug("Getting Queue URL from Amazon [" + queueName + "]");
			String queueUrl = messageContext.getQueue(queueName);
			logger.debug("Sending a message to [" + queueName + "][" + queueUrl + "]");


			// if message is small enough to be sent as one message, do it
			if (jsonString.getBytes(MessageSplitter.UTF_8).length <= MessageSplitter.SQS_MAX_MESSAGE_SIZE) {
				String header = makeHeader(MessageTransferType.JSON, "athena", transactionId, true, 1, 1 );

				logger.debug("This is smaller message");
				logger.debug("[HEADER] : " + header);

				String singleMessage = header + encoder.encodeBuffer(jsonString.getBytes());
				client.sendMessage(new SendMessageRequest(queueUrl, singleMessage));
				logger.debug("Single message sent successfully");

			} else {
				logger.debug("This is larger than " + MessageSplitter.SQS_MAX_MESSAGE_SIZE);
				List<String> messageList = MessageSplitter.split(encoder.encodeBuffer(jsonString.getBytes()));

				int current = 1;
				int total = messageList.size();

				String header = null;
				String chunkedMessage = null;
				for( String target : messageList) {
					header = makeHeader(MessageTransferType.JSON, "athena", transactionId, false, current++, total );
					chunkedMessage = header + target;
					client.sendMessage(new SendMessageRequest(queueUrl,chunkedMessage));
					logger.debug(chunkedMessage);
				}
				logger.debug("Complex message sent successfully");
			}
		} catch(IOException ioe) {
			new MessageException(MessageFormat.format(MessageErrors.INTERNAL_ERROR.getDescription(), ioe.getMessage()));
		} catch(Exception e) {
			new MessageException(MessageFormat.format(MessageErrors.INTERNAL_ERROR.getDescription(), e.getMessage()));
		} finally {
			
		}
	}

	

	/**
	 * Send file to amazon sqs
	 * @param message
	 */
	public void doSend(String queueName, File message) throws MessageException {
		throw new MessageException("Send file is not supported");
	}

	/**
	 * Create SQS Message header
	 * @param transferType
	 * @param businessName
	 * @param transactionId
	 * @param isSingle
	 * @param current
	 * @param total
	 * @return
	 */
	private String makeHeader(MessageTransferType transferType, String businessName, String transactionId, boolean isSingle, int current, int total) {
		StringBuilder header = new StringBuilder();

		header.append(transferType.toString()).append(MessageContext.HEADER_DELIMITER);
		header.append(businessName).append(MessageContext.HEADER_DELIMITER);
		header.append(transactionId).append(MessageContext.HEADER_DELIMITER);

		if( isSingle ) {
			header.append("SINGLE").append(MessageContext.HEADER_DELIMITER);
		} else {
			header.append("MULTI").append(MessageContext.HEADER_DELIMITER);
		}
		header.append(current).append(MessageContext.HEADER_DELIMITER);
		header.append(total).append(MessageContext.DATA_DELIMITER);
		return header.toString();
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
