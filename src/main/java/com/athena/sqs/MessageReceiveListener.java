package com.athena.sqs;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * This class is a ContextListener for SQS receiver module.
 * Queue name must be in web.xml configuration and start listen from amazon SQS.
 *
 * @author Ji-Woong Choi(ienvyou@gmail.com)
 *
 */
public class MessageReceiveListener  implements ServletContextListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private MessageReceiver receiver;
	private String listenQueueName;

	public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        listenQueueName = servletContext.getInitParameter("listenQueueName");
        if( listenQueueName == null ) {
        	listenQueueName = "your_listen_queue_name";
        }

		ApplicationContext applicationContext = (ApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		receiver = applicationContext.getBean(MessageReceiver.class);

		// If you want to run multiple receivers, create tasks or change doReceive method to async.
		/*
		 * List<BizQueue> list = dao.getBizQueueList();
		 * for(BizQueue queue : list) {
		 *     BizQueueTask task = new BiaQueueTask(sqsContext, queue);
		 *     task.run(); // thread start.
		 * }
		 */
		try {
			logger.debug("***********************************************");
			logger.debug("SQS Message Receiver is starting");
			logger.debug("***********************************************");

			// Queue names are loaded from sqsContext.xml

		   List<String> queueList = (List<String>) applicationContext.getBean("initialQueueList");
		   
		   for( String queueName : queueList) {
			   receiver.doReceive(queueName);
			   Thread.sleep(2000);
		   }
		   
		   // Default listen queue start
		   receiver.doReceive(listenQueueName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		if( receiver != null ) {
			try {
				receiver.doDisconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
