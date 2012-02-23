package com.athena.sqs;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Decoder;

/**
 * This class allows you to process a composite message by splitting it up,
 * and the re-aggregating the responses back into a single message.
 *
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
@Service
public class MessageAggregator {
	private final Logger logger = Logger.getLogger(this.getClass());
	// Store transaction id and message map
	private static ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> txMap = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>>();

	/**
	 * Aggregate splitted messages into single message.
	 * @param rawData base64 string
	 * @throws MessageException
	 */
	public void aggregate(String rawString) throws MessageException {
		try {
			BASE64Decoder decoder = new BASE64Decoder();

			int index = rawString.indexOf(MessageContext.DATA_DELIMITER);

			// 1. Split header
			String [] header = parseHeader(rawString.substring(0, index));
			String content = rawString.substring(index+2);


			// 2. Assign header value to local variable
			MessageTransferType transferType = MessageTransferType.valueOf(header[0]);
			String businessName = header[1];
			String transactionId = header[2];
			MessageSplitType splitType = MessageSplitType.valueOf(header[3]);
			int current = Integer.parseInt(header[4]);
			int total = Integer.parseInt(header[5]);

			// 3 Check Message Single
			switch(splitType) {
				case SINGLE :
					// TODO single message work
					doProcess(transactionId, new String(decoder.decodeBuffer(content)));
					return;
				case MULTI :
					break;
			}

			logger.debug("Transaction ID : " + transactionId);

			// 4. Check Message Order
			// If transaction id is not exist in txMap, create new tx map object
			if( !txMap.containsKey(transactionId) ) {
				ConcurrentHashMap<Integer, String> orderedMessages = new ConcurrentHashMap<Integer, String>();
				orderedMessages.put(current, content);

				txMap.put(transactionId, orderedMessages);
			} else {
				// Already same transaction message was inserted
				ConcurrentHashMap<Integer, String> orderedMessages = txMap.get(transactionId);
				orderedMessages.put(current, content);

				// Message compare
				if( orderedMessages.size() == total) {
					// All messages arrived
					Object[] key   = orderedMessages.keySet().toArray();
					Arrays.sort(key);

					String finalMessage = "";
					for(int i = 0; i < key.length; i++)   {
			            finalMessage += orderedMessages.get(key[i]);
					}


					logger.debug("===== [ " + transactionId + "] ======");
					logger.debug(new String(decoder.decodeBuffer(finalMessage)));
					boolean isDelete = txMap.remove(transactionId, orderedMessages);
					if( ! isDelete ) {
						throw new MessageException("Can't delete message from transaction map");
					}

					doProcess(transactionId, new String(decoder.decodeBuffer(finalMessage)));
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private String [] parseHeader(String header) {
		return header.split("\\|");
	}

	/**
	 * Process single message type.
	 * @param message
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public void doProcess(String transactionId, String rawString) throws MessageException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Message message = mapper.readValue(rawString, new TypeReference<Message>(){});
			logger.debug("=======================[MESSAGES]=============================");
			logger.debug(message);
			logger.debug("===========================================================");

		} catch(Exception e) {
			throw new MessageException("Error during processing message: " + e.getMessage());
		} finally {

		}
	}

}
