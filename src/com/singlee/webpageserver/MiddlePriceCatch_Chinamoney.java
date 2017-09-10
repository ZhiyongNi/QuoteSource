package com.singlee.webpageserver;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.singlee.priceengine.common.CommWriteFileWeb;
import java.util.Map;

public class MiddlePriceCatch_Chinamoney {

    private Logger logger = Logger.getRootLogger();
    private CommWriteFileWeb commWriteFileWeb;// 写文件
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
    private HashMap<String, String> map = new HashMap<String, String>();
    private static String Quote_URL = "http://www.chinamoney.com.cn/fe/static/html/column/basecurve/rmbparity/latestRMBParity.html";

    public void setCommWriteFileWeb(CommWriteFileWeb commWriteFileWeb) {
        this.commWriteFileWeb = commWriteFileWeb;
    }

    @SuppressWarnings("deprecation")
    public static Map<String, String> catchQuote() {
        Map<String, String> map = new HashMap<String, String>();
        String QuoteJsonString = CatchWebUtil.getContentbyURL(Quote_URL);
        String[] ss = QuoteJsonString.trim().split("\n");
        map.put("date", ss[2].trim());
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].trim().equals("美元/人民币")) {
                map.put("USD", ss[i + 1].trim());
            }
            if (ss[i].trim().equals("港元/人民币")) {
                map.put("HKD", ss[i + 1].trim());
            }
            if (ss[i].trim().equals("100日元/人民币")) {
                map.put("JPY", ss[i + 1].trim());
            }
            if (ss[i].trim().equals("欧元/人民币")) {
                map.put("EUR", ss[i + 1].trim());
            }
            if (ss[i].trim().equals("英镑/人民币")) {
                map.put("GBP", ss[i + 1].trim());
            }
        }
        return map;
    }

   
    public static void main(String[] arg0) throws Exception {
        catchQuote();
        System.out.println("com.singlee.webpageserver.MiddlePriceCatch_Chinamoney.main()");
        //while (true) {
        //    List<String> list = new MiddlePriceCatch_Chinamoney().dealBoc();
        //    for (int i = 0; i < list.size(); i++) {
        //        System.out.println(list.get(i));
        //    }
        //   }
    }
}
