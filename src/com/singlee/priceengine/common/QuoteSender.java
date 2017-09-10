package com.singlee.priceengine.common;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class QuoteSender {
	private JmsTemplate jmsTemplate;
	
	public void sendMessage(final String text)throws JMSException{
//		    System.out.println("---Send:"+text);   
	        jmsTemplate.send(new MessageCreator(){   
	        public Message createMessage(Session arg0) throws JMSException {   
	               return arg0.createTextMessage(text);   
	            }   
	               
	        });   
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}
