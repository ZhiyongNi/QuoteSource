package com.singlee.webpageserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import com.google.gson.Gson;

import com.singlee.priceengine.common.CommWriteFileWeb;
import com.singlee.pricesource.exception.CatchWebPageException;

public class MarketPriceCatch_Chinamoney { //http://www.chinamoney.com.cn

    private Logger logger = Logger.getRootLogger();
    private CommWriteFileWeb commWriteFileWeb;// 写文件
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
    private HashMap<String, String> map = new HashMap<String, String>();
    private String Quote_URL = "http://www.chinamoney.com.cn/webdata/fe/rmb_fx_spot.json";

    public void setCommWriteFileWeb(CommWriteFileWeb commWriteFileWeb) {
        this.commWriteFileWeb = commWriteFileWeb;
    }

    @SuppressWarnings("deprecation")
    public List<String> catchQuote() throws Exception {

        List<String> bocList = new ArrayList<String>();

        try {
            String QuoteJsonString = CatchWebUtil.getJsonbyURL(Quote_URL);
            Gson gson = new Gson();
            Quote_Chinamoney Quote_Instance = gson.fromJson(QuoteJsonString, Quote_Chinamoney.class);

            String s = CatchWebUtil.getHtmlbyURL(Quote_URL);

            if (null == Quote_Instance) {
                commWriteFileWeb.writeFile("已丢弃：抓取数据为空：" + new Date().toLocaleString() + "\r\n");
                return bocList;
            } else if (Quote_Instance.head.ts.isEmpty()) {
                commWriteFileWeb.writeFile("已丢弃：抓取数据有误：" + new Date().toLocaleString() + "|" + s + "\r\n");
                return bocList;
            }

            s = s.split("发布时间")[1].split("往日外汇牌价搜索")[0].trim();
            String[] ss = s.split("\n");
            String dateStr = format.format(new Date());
            String[][] ccys = {{"英镑", "1314|" + dateStr + "|GBP/CNY"}, {"港币", "1315|" + dateStr + "|HKD/CNY"}, {"美元", "1316|" + dateStr + "|USD/CNY"}, {"日元", "1323|" + dateStr + "|JPY/CNY"}, {"欧元", "1326|" + dateStr + "|EUR/CNY"}};
            for (int i = 0; i < ss.length; i++) {
                StringBuffer sb = new StringBuffer();
                for (int j = 0; j < ccys.length; j++) {
                    if (ss[i].trim().equals(ccys[j][0])) {
                        sb.append(ccys[j][1] + "|"
                                + ss[i + 2].trim() + "|"
                                + ss[i + 3].trim() + "|"
                                + ss[i + 1].trim() + "|"
                                + ss[i + 3].trim() + "|"
                                + ss[i + 4].trim() + "|"
                                + ss[i + 6].trim() + " " + ss[i + 7].trim() + ":000");
                        bocList.add(sb.toString());
                        if (format2.parse(ss[i + 6].trim()).before(format2.parse(format2.format(new Date())))) {
                            commWriteFileWeb.writeFile("已丢弃：抓取数据过期：" + ss[i].trim() + new Date().toLocaleString() + "|本次时间" + ss[i + 6].trim() + "|" + sb.toString() + "\r\n");
                            return new ArrayList<String>();
                        }
                        if (!map.containsKey(ss[i].trim())) {
                            map.put(ss[i].trim(), ss[i + 6].trim() + " " + ss[i + 7].trim() + ":000");
                        } else {

                            if (format.parse(ss[i + 6].trim() + " " + ss[i + 7].trim() + ":000").before(format.parse(map.get(ss[i].trim())))) {
                                commWriteFileWeb.writeFile("已丢弃：" + ss[i].trim() + new Date().toLocaleString() + "|上次时间" + map.get(ss[i].trim()) + "|本次时间" + ss[i + 6].trim() + " " + ss[i + 7].trim() + ":000" + "|" + sb.toString() + "\r\n");
                                return new ArrayList<String>();
                            }

                        }
                        map.put(ss[i].trim(), ss[i + 6].trim() + " " + ss[i + 7].trim() + ":000");
                        continue;
                    }
                }
            }
            return bocList;
        } catch (Exception e) {
            logger.error("无法访问中行报价网页或解析网页数据出错", e);
            throw new CatchWebPageException("无法访问中行报价网页或解析网页数据出错");
        }
    }

    public static void main(String[] arg0) throws Exception {
        Object O = new MarketPriceCatch_Chinamoney().catchQuote();
        System.out.println("com.singlee.webpageserver.MarketPriceCatch_BCHO.main()");

//    while (true) {
        //      List<String> list = new MarketPriceCatch_Chinamoney().catchQuote();
        //      for (int i = 0; i < list.size(); i++) {
        //          System.out.println(list.get(i));
        //      }
        //    }
    }

    private class Quote_Chinamoney {

        public class head {

            public String version;
            public String provider;
            public String req_code;
            public String rep_code;
            public String rep_message;
            public String ts;
            public String producer;
        }

        public class data {

            public String switchFlag;
            public String date;
            public String dateEN;
        }

        public class records {

            public String ccyPair;
            public String bidPrc;
            public String askPrc;
            public String midprice;
            public String time;

        }
        public head head;
        public data data;
        public List<records> records;

    }

}
