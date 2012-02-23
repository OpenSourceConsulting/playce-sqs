package com.athena.sqs;

/**
 * Enumeration class for exception message
 * 
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
public enum MessageErrors {
	QUEUE_NOT_FOUND("Queue name [{0}] is not found. Please contact technical architecT in your project"), 
	AMAZON_ERROR("Amazon Error : {0} "), 
	INTERNAL_ERROR("Component Error : {0} "),
	NOT_SUPPORT("Can't support this feature");

	private String description;

	MessageErrors(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
