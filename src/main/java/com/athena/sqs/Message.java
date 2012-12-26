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

import java.util.List;
import java.util.Map;

/**
 * This class is a wrapper for a message from queue.
 *
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 */
public class Message {
	private String id;
	private Map<String, String> mapData;
	private List<String> listData;
	
	public Message() {}

	/**
	 * Returns message id
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets message id
	 * @param messageId
	 */
	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getMapData() {
		return mapData;
	}

	public void setMapData(Map<String, String> mapData) {
		this.mapData = mapData;
	}

	public List<String> getListData() {
		return listData;
	}

	public void setListData(List<String> listData) {
		this.listData = listData;
	}

	@Override
	public String toString() {
		return "Message [messageId=" + id + ", mapData=" + mapData
				+ ", listData=" + listData + "]";
	}

	
}
