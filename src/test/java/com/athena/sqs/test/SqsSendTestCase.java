package com.athena.sqs.test;

import java.util.ArrayList;
import java.util.List;


import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.athena.sqs.Message;
import com.athena.sqs.MessageDispatcher;
import com.athena.sqs.MessageException;

public class SqsSendTestCase {

	private ApplicationContext context;
	private MessageDispatcher dispatcher;
	
	@Before
	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext("./sqsContext.xml");
		dispatcher = context.getBean(MessageDispatcher.class);
	}
	
	public String makeText(long messageSize) {
		String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int N = alphabet.length();

        java.util.Random r = new java.util.Random();

        StringBuilder builder = new StringBuilder();
        for( int i = 0 ; i < messageSize ; i++) {
            builder.append(alphabet.charAt(r.nextInt(N)));
        }
        return builder.toString();
	}
		
	@Test 
	public void sendMapData() throws BeansException, MessageException {
		        
	}
	
	@Test
	public void sendListData() throws BeansException, MessageException {
		Message message = new Message();
		message.setId("HELLO_ATHENA");
		
		List<String> data = new ArrayList<String>();
		data.add(makeText(1024*3)); // 3k message
		message.setListData(data);
		
		
		dispatcher.doProcess("athena-queue", message);
	}


}
