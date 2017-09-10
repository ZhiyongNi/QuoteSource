package com.singlee.priceengine.common;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class ExceptionSender {
	private JmsTemplate jmsExceptionTemplate;
	private Logger	logger= Logger.getLogger(this.getClass());//日志
	public void sendMessage(final String text){
		    jmsExceptionTemplate.setTimeToLive(5000);
		    jmsExceptionTemplate.send(new MessageCreator(){   
	        public Message createMessage(Session arg0) throws JMSException {   
	               return arg0.createTextMessage(text);   
	            }   
	               
	        });
	}

	public JmsTemplate getJmsExceptionTemplate() {
		return jmsExceptionTemplate;
	}

	public void setJmsExceptionTemplate(JmsTemplate jmsExceptionTemplate) {
		this.jmsExceptionTemplate = jmsExceptionTemplate;
	}


}
