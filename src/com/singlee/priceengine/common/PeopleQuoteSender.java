package com.singlee.priceengine.common;

import java.util.Date;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class PeopleQuoteSender {

    private JmsTemplate peopleQuoteTemplate;

    public void sendMessage(final String text) throws JMSException {
        System.out.println("---Send:" + text + "   " + new Date().toLocaleString());
        peopleQuoteTemplate.send(new MessageCreator() {

            public Message createMessage(Session arg0) throws JMSException {
                return arg0.createTextMessage(text);
            }
        });
    }

    public JmsTemplate getPeopleQuoteTemplate() {
        return peopleQuoteTemplate;
    }

    public void setPeopleQuoteTemplate(JmsTemplate peopleQuoteTemplate) {
        this.peopleQuoteTemplate = peopleQuoteTemplate;
    }

}
