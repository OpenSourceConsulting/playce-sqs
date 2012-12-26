/*
 * Copyright 2012 The Athena Project
 *
 * The Athena Project licenses this file to you licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.athena.sqs;


import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
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
	private final Logger logger = LoggerFactory.getLogger(getClass());

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
			logger.debug("Message is successfully sent to SQS [" + queueName + "]");

		}catch (MessageException e) {
			throw e;
		}catch (Exception e) {
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
		} catch (AmazonServiceException ase) {
            logger.error("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            logger.error("Error Message:    " + ase.getMessage());
            logger.error("HTTP Status Code: " + ase.getStatusCode());
            logger.error("AWS Error Code:   " + ase.getErrorCode());
            logger.error("Error Type:       " + ase.getErrorType());
            logger.error("Request ID:       " + ase.getRequestId());
            throw new MessageException(MessageFormat.format(MessageErrors.AMAZON_ERROR.getDescription(), ase.getMessage()));
        } catch (AmazonClientException ace) {
            logger.error("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
            throw new MessageException(MessageFormat.format(MessageErrors.AMAZON_ERROR.getDescription(), ace.getMessage()));
        } catch(IOException ioe) {
			throw new MessageException(MessageFormat.format(MessageErrors.INTERNAL_ERROR.getDescription(), ioe.getMessage()));
		} catch(Exception e) {
			throw new MessageException(MessageFormat.format(MessageErrors.INTERNAL_ERROR.getDescription(), e.getMessage()));
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
