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
