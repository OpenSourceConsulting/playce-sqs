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
