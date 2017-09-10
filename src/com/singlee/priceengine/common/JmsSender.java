package com.singlee.priceengine.common;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.JmsException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import com.singlee.webpageserver.MarketPriceGUI;

public class JmsSender {

    private JmsTemplate jmsTemplate;
    private boolean isClose = false;

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean isClose) {
        this.isClose = isClose;
    }

    public void sendMessage(final String text) throws JmsException {
        if (isClose()) {
            try {
                setJmsTemplate((JmsTemplate) MarketPriceGUI.atx.getBean("jmsTemplate"));
                setClose(false);
            } catch (Exception e) {
                throw new UncategorizedJmsException(e);
            }
        }
        jmsTemplate.setTimeToLive(5000);
        jmsTemplate.send(new MessageCreator() {
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
