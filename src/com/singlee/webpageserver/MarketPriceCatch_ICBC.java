package com.singlee.webpageserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.singlee.priceengine.common.CommWriteFileWeb;
import com.singlee.pricesource.exception.CatchWebPageException;

public class MarketPriceCatch_ICBC {

    private Logger logger = Logger.getRootLogger();
    private CommWriteFileWeb commWriteFileWeb;// 写文件
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
    private HashMap<String, String> map = new HashMap<String, String>();

    public void setCommWriteFileWeb(CommWriteFileWeb commWriteFileWeb) {
        this.commWriteFileWeb = commWriteFileWeb;
    }

    public List<String> getJmsData(int bankid) throws CatchWebPageException {
        try {
//			List<String> list = dealBoc();
//			for(int i=0;i<list.size();i++){
//				System.out.println(list.get(i));
//				commWriteFileWeb.writeFile("已抓取：" + list.get(i).toString()+ "\r\n");
//			}
            return catchQuote();
        } catch (CatchWebPageException e) {
            logger.error(e.getMessage(), e);
            throw new CatchWebPageException(e.getMessage());
        } catch (Exception e) {
            logger.error("获取网页数据时出现异常！", e);
            throw new CatchWebPageException("获取网页数据时出现异常！");
        }
    }

    //中行
    @SuppressWarnings("deprecation")
    public List<String> catchQuote() throws Exception {

        List<String> bocList = new ArrayList<String>();

        try {
            String s = CatchWebUtil.getHtmlbyURL("http://www.boc.cn/sourcedb/whpj/");
            if (null == s || s.split("发布时间").length < 2
                    || null == s.split("发布时间")[1]
                    || "".equals(s.split("发布时间")[1])) {

                if (null == s) {
                    commWriteFileWeb.writeFile("已丢弃：抓取数据为空：" + new Date().toLocaleString() + "\r\n");
                } else {
                    commWriteFileWeb.writeFile("已丢弃：抓取数据有误：" + new Date().toLocaleString() + "|" + s + "\r\n");
                }

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
        while (true) {
            List<String> list = new MarketPriceCatch_ICBC().catchQuote();
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }
        }
    }
}
