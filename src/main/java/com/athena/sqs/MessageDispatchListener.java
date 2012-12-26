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


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;



/**
 *
<pre>
<?xml version="1.0" encoding="ISO-8859-1"?>
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
         http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd" version="2.4">
  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/your-config.xml</param-value>
  </context-param>

OR

  <servlet>
    <servlet-name>MessageInitializer</servlet-name>
    <servlet-class>com.athena.sqs.MessageListener</servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>/WEB-INF/your-config.xml</param-value>
    </init-param>
  </servlet>
</web-app>

</pre>
 */

public class MessageDispatchListener implements ServletContextListener {
	private final Logger logger = Logger.getLogger(this.getClass());

	//private @Autowired SQSExtractorConfig config;

	private String sqsConfigLocation;

	public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        sqsConfigLocation = servletContext.getInitParameter("sqsConfigLocation");



		String realPath = servletContext.getRealPath(sqsConfigLocation);
		if( logger.isDebugEnabled()) {
			logger.debug("****************** SQS Config ******************");
			logger.debug("LOCATION : " + sqsConfigLocation);
			logger.debug("REAL LOCATION : " + realPath);
			logger.debug("************************************************");
		}

		ApplicationContext applicationContext = (ApplicationContext)servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		// TODO : You can load your own application config here
    }


	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}
}
