package com.singlee.priceengine.common;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class PeopleQuoteReceiver implements MessageListener {

    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage text = (TextMessage) message;
            try {
                System.out.println("Receive:" + text.getText());
            } catch (JMSException e) {
                System.out.println("获取消息发生异常!");
            }
        }
    }
}
