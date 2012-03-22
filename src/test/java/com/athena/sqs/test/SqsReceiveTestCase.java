package com.athena.sqs.test;


import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.athena.sqs.MessageReceiver;

public class SqsReceiveTestCase {
	private ApplicationContext context;

	@Before
	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext("./sqsContext.xml");
	}


	@Test
	public void receiveLargeMessage() throws Exception {
		MessageReceiver receiver = context.getBean(MessageReceiver.class);
		receiver.doReceive("vhub_sqs_codex");
	}
}