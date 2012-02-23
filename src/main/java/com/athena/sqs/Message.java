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
