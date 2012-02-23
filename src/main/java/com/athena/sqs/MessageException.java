package com.athena.sqs;

/**
 * Declares global SQS exception information
 * 
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
public class MessageException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public MessageException() {}
	
	public MessageException(String message) {
		super(message);
	}
	
}
