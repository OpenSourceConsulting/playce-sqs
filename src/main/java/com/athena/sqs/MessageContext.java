package com.athena.sqs;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;

/**
 * Context class that can be use in the module globally.
 * This includes error messages and delimeter
 *
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
@Service
@DependsOn("credentials")
public class MessageContext {
	private final Logger logger = Logger.getLogger(this.getClass());

	private @Autowired BasicAWSCredentials credentials;

	public static final String HEADER_DELIMITER = "|";
	public static final String DATA_DELIMITER = "*^";

	private AmazonSQSClient sqsClient;

	private static Map<String, String> queueNameMap = new HashMap<String, String>();

	public AmazonSQSClient getSqsClient() {
		return this.sqsClient;
	}

	@PostConstruct
	public void doConnect() throws Exception {
		try {
			AmazonSQSClient sqs = new AmazonSQSAsyncClient(credentials);
			//AmazonSQSClient sqs = new AmazonSQSClient(credentials);
			sqs.setEndpoint("http://sqs.eu-west-1.amazonaws.com");

	        logger.debug("Start Connection with Amazon SQS");
	        this.sqsClient = sqs;
	        logger.debug("Binding Amazon SQS successfully.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Gets real queue url from SQS
	 * @param queueName name of the queue
	 * @return real queue name
	 * @throws MessageException
	 */
	public String getQueue(String queueName) throws MessageException {
		String queueUrl = null;
        try {
        	queueUrl = queueNameMap.get(queueName);

        	if( queueUrl != null ) return queueUrl;

        	// If queue name is not exist, lookup queue.
	        GetQueueUrlRequest queue = new GetQueueUrlRequest(queueName);
	        GetQueueUrlResult queueResult = sqsClient.getQueueUrl(queue);
	        queueUrl =  queueResult.getQueueUrl();

        } catch (AmazonServiceException ase) {
            if(ase.getErrorCode().equals("AWS.SimpleQueueService.NonExistentQueue")) {
            	throw new MessageException(MessageFormat.format(MessageErrors.QUEUE_NOT_FOUND.getDescription(), queueName));
            }
        } catch (AmazonClientException ace) {
        	throw new MessageException(MessageFormat.format(MessageErrors.AMAZON_ERROR.getDescription(), ace.getMessage()));
        } catch (Exception e) {
        	
        }
        return queueUrl;
	}
}
