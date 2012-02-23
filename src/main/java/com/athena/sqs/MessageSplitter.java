package com.athena.sqs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Message splitter for exceeding the limit of 64K message.
 * 
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
public class MessageSplitter {

	public static final String UTF_8 = "UTF-8";
	// SQS limit is 64K, please refer to
	// http://aws.amazon.com/sqs/faqs/#How_do_I_configure_SQS_to_support_larger_message_sizes
	public static final int SQS_MAX_MESSAGE_SIZE = 1024 * 63 ; // 1K for header
	
	/**
	 * Split message to small size
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public static List<String> split(String message) throws IOException {
		List<String> chunkMessage = new ArrayList<String>();
		
		// get the content as a byte[]
		byte[] content = message.getBytes(UTF_8);

		// figure out how much content can be in each chunk
		int chunkSize = SQS_MAX_MESSAGE_SIZE;

		// create a byte[] for our max message size
		// we're going to repeatedly fill this and send the message while
		// content remains.
		byte[] bytes = new byte[SQS_MAX_MESSAGE_SIZE];

		// copy the header into the byte[], we'll only do this once

		// while there is content left, send a message
		System.out.println("Message Count : " + (content.length / chunkSize + 1));
		
		for (int i = 0; i < content.length; i += chunkSize) {
			// copy the smaller of the remaining bytes or the max chunkSize chunk
			// of content into the message array, then send the message. Form the 
			// message String from the appropriate portion of the array
			if (content.length - i < chunkSize) {
	
				System.arraycopy(content, i, bytes, 0, content.length - i);
				message = new String(bytes, 0, content.length - i, UTF_8);
	
			} else {
				//System.arraycopy(src, srcPos, dest, destPos, length)
				System.arraycopy(content, i, bytes, 0, chunkSize);
				message = new String(bytes,UTF_8);
			}
			chunkMessage.add(message);
		}
		return chunkMessage;

	}
	
}
