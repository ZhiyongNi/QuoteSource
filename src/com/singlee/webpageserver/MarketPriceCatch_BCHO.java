package com.singlee.webpageserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.singlee.priceengine.common.CommWriteFileWeb;
import com.singlee.pricesource.exception.CatchWebPageException;

public class MarketPriceCatch_BCHO {

    private Logger logger = Logger.getRootLogger();
    private CommWriteFileWeb commWriteFileWeb;// 写文件
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
    private HashMap<String, String> map = new HashMap<String, String>();
    private static String Quote_URL = "http://www.boc.cn/sourcedb/whpj/";

    public void setCommWriteFileWeb(CommWriteFileWeb commWriteFileWeb) {
        this.commWriteFileWeb = commWriteFileWeb;
    }

    @SuppressWarnings("deprecation")
    public List<String> catchQuote() throws Exception {

        List<String> QuoteList = new ArrayList<String>();

        /*try {
            String QuoteContentString = CatchWebUtil.getContentbyURL(Quote_URL);
            if (null == QuoteContentString) {
                commWriteFileWeb.writeFile("已丢弃：抓取数据为空：" + new Date().toLocaleString() + "\r\n");
            } else if (QuoteContentString.split("发布时间").length < 2
                    || null == QuoteContentString.split("发布时间")[1]
                    || "".equals(QuoteContentString.split("发布时间")[1])) {
                commWriteFileWeb.writeFile("已丢弃：抓取数据有误：" + new Date().toLocaleString() + "|" + QuoteContentString + "\r\n");
            }

            QuoteContentString = QuoteContentString.split("发布时间")[1].split("往日外汇牌价搜索")[0].trim();
            String[] ss = QuoteContentString.split("\n");
            String dateStr = format.format(new Date());
            String[][] ccys = {{"英镑", "1201|" + dateStr + "|GBP/CNY"}, {"港币", "1301|" + dateStr + "|HKD/CNY"}, {"美元", "1401|" + dateStr + "|USD/CNY"}, {"日元", "1901|" + dateStr + "|JPY/CNY"}, {"欧元", "2201|" + dateStr + "|EUR/CNY"}};
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
                        QuoteList.add(sb.toString());
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

        } catch (Exception e) {
            logger.error("无法访问中行报价网页或解析网页数据出错", e);
            throw new CatchWebPageException("无法访问中行报价网页或解析网页数据出错");
        }*/
        QuoteList.add("2201|2017-09-11 17:50:57:813|EUR / CNY | 757.23 | 787.06 | 781.57 | 787.06 | 787.06 | 2017 - 09 - 11 17:37:19:000");
        QuoteList.add("1201|2017-09-11 17:50:57:813|GBP / CNY | 831.09 | 863.83 | 857.81 | 863.83 | 865.12 | 2017 - 09 - 11 17:37:19:000");
        QuoteList.add("1301|2017-09-11 17:50:57:813|HKD / CNY | 82.65 | 83.64 | 83.32 | 83.64 | 83.64 | 2017 - 09 - 11 17:37:19:000");
        QuoteList.add("1901|2017-09-11 17:50:57:813|JPY / CNY | 5.8103 | 6.0392 | 5.9971 | 6.0392 | 6.0392 | 2017 - 09 - 11 17:37:19:000");
        QuoteList.add("1401|2017-09-11 17:50:57:813|USD / CNY | 645.96 | 653.92 | 651.31 | 653.92 | 653.92 | 2017 - 09 - 11 17:37:19:000");
        System.out.println(QuoteList);
        return QuoteList;
    }

    public static void main(String[] arg0) throws Exception {
        Object O = new MarketPriceCatch_BCHO().catchQuote();
        System.out.println("com.singlee.webpageserver.MarketPriceCatch_BCHO.main()");

        //while (true) {
        //  List<String> list = new MarketPriceCatch_BCHO().catchQuote();
        //for (int i = 0; i < list.size(); i++) {
        //  System.out.println(list.get(i));
        //}
        //}
    }
}
