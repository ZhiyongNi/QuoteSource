package com.singlee.webpageserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.htmlparser.beans.StringBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.singlee.priceengine.common.ExceptionSender;
import com.singlee.priceengine.common.PeopleQuoteSender;

/**
 *
 * 功能:人行价格定时服务
 *
 * 杭州新利软件有限公司 2010 版权所有.
 *
 * @author 郑少华 Aug 9, 2010 7:56:21 PM
 * @version 1.0
 */
public class MiddlePriceServer {

    public static void main(String[] arg0) {
        AbstractApplicationContext atx = new ClassPathXmlApplicationContext(new String[]{"config/server.xml"});
        MiddlePriceConfig MiddlePriceConfig_Instance = (MiddlePriceConfig) atx.getBean("peoplePriceConfig");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, MiddlePriceConfig_Instance.getHour());
        c.set(Calendar.MINUTE, MiddlePriceConfig_Instance.getMinute());
        c.set(Calendar.SECOND, MiddlePriceConfig_Instance.getSecond());
        Timer peoplePriceTimer = new Timer();
        peoplePriceTimer.scheduleAtFixedRate(new MiddlePriceCatchTimeTask(), c.getTime(),
                MiddlePriceConfig_Instance.getTime());
    }
}

/**
 *
 * 功能:人行报价抓取
 *
 * 杭州新利软件有限公司 2010 版权所有.
 *
 * @author 郑少华 Aug 11, 2010 1:03:43 PM
 * @version 1.0
 */
class MiddlePriceCatchTimeTask extends TimerTask {

    ApplicationContext atx = new ClassPathXmlApplicationContext(new String[]{"config/PeopleQuoteSender.xml"});
    PeopleQuoteSender jmsSender = (PeopleQuoteSender) atx.getBean("peopleQuoteSender");
    public static ApplicationContext atxEx = new ClassPathXmlApplicationContext(new String[]{"config/exceptionSender.xml"});
    public static ExceptionSender exceptionSender = (ExceptionSender) atxEx.getBean("exceptionSender");

    @Override
    public void run() {
        int count = 0;
        boolean issend = false;
        while (issend == false && count < 10) {
            try {
                //Map<String, String> map = dealDataNew();
                Map<String, String> QuoteMap = MiddlePriceCatch_Chinamoney.catchQuote();
                if (QuoteMap.size() != 0) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(QuoteMap.get("date") + "&" + "USD:" + QuoteMap.get("USD") + "#" + "HKD:"
                            + QuoteMap.get("HKD") + "#" + "JPY:" + QuoteMap.get("JPY") + "#"
                            + "EUR:" + QuoteMap.get("EUR") + "#" + "GBP:"
                            + QuoteMap.get("GBP"));
                    jmsSender.sendMessage(sb.toString());
                    writeData(sb.toString());
                    issend = true;
                }

                Thread.sleep(1000 * 60 * 5);
                count++;
                if (issend == false) {
                    exceptionSender.sendMessage("010#无法获取人行报价#" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
                }

            } catch (Exception e) {
                exceptionSender.sendMessage("010#无法获取人行报价#" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
                count++;
                e.printStackTrace();

            }
        }
    }

    public static void writeData(String quote) {

        File file = new File("./quoteLog/peoplePrice.txt");
        String s = new String();
        String s1 = new String();
        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedReader input = new BufferedReader(new FileReader(file));
            while ((s = input.readLine()) != null) {
                s1 += s + "\r\n";
            }
            input.close();
            s1 += quote;
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(s1);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MiddlePriceConfig {

    private int hour;
    private int minute;
    private int second;
    private int time;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

}
