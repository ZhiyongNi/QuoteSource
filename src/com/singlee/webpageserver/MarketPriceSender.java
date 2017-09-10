package com.singlee.webpageserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.springframework.jms.JmsException;
import com.singlee.priceengine.common.CommWriteFile;
import com.singlee.priceengine.common.ExceptionSender;
import com.singlee.priceengine.common.JmsSender;

/**
 * 黄正良 发送数据
 *
 */
public class MarketPriceSender {

    private List QuoteData_List = new ArrayList();
    private Logger logger = Logger.getLogger(this.getClass());// 日志

    private boolean isServerStart = true;// 判断线程是否启动
    private boolean isServerStop = true;// 判断线程是否关闭

    private JmsSender jmsSender;// jms发送类
    private ExceptionSender exSender;// jms异常发送类
    private CommWriteFile commWriteFile;// 写文件

    public MarketPriceSender() {
    }

    /**
     * 黄正良 发送数据
     */
    public void catchQuoteData() throws Exception {// 解析网页源获得要发送的数据
        switch (MarketPriceServer.serverCode) {
            case 382:// 中行
                QuoteData_List = new MarketPriceCatch_BCHO().catchQuote();
                break;
            case 441:// 工行
                QuoteData_List = new MarketPriceCatch_ICBC().catchQuote();
                break;
            case 442:// 农行
                QuoteData_List = new MarketPriceCatch_ABCI().catchQuote();
                break;
            case 511:// 货币网
                QuoteData_List = new MarketPriceCatch_Chinamoney().catchQuote();
                break;
        }
        logger.debug("主线程调用getJmsData()抓取网页结束……");
        for (int i = 0; i < QuoteData_List.size(); i++) {
            String msg = getShowMsg(QuoteData_List.get(i).toString());

            logger.debug("主线程开始将组装抓取到的数据写入文件");
            try {
                // 将数据写入文件
                commWriteFile.writeFile(msg);
                logger.debug("主线程开始将组装抓取到的数据写入文件完成！");
            } catch (Exception e) {
                sendExMsg("000", "写文件出现异常详情请查看日志！", e);
            }

            logger.debug("主线程开始将组装抓取到的数据在界面显示");
            showQuoteMsg(msg);
            logger.debug("主线程开始将组装抓取到的数据在界面显示完成");

        }
        logger.debug("主线程将组装抓取到的数据写入文件并显示完成……");
    }

    public void sendQuoteData() throws Exception {
        String quotes = getQuotes(QuoteData_List);
        if (quotes == null) {
            logger.debug("主线程开始休眠……");
            threadWait(new Object(), MarketPriceServer.reCatchPageTime);// 重新抓取数据的时间
            logger.debug("主线程休眠完成……");
        } else {
            logger.debug("主线程开始发送组装的数据……");
            sendMessage(quotes);
            logger.debug("主线程发送组装的数据完成……");

            logger.debug("主线程将jms通信状况在界面显示……");
            logger.debug("主线程将jms通信状况在界面显示完成……");
        }
    }

    /**
     * 黄正良 发送异常信息到jms
     *
     * @param code
     * @param msg
     * @param e
     */
    public void sendExMsg(String code, String msg, Exception e) {
        e.printStackTrace();
        logger.error(msg, e);
        try {
            exSender.sendMessage(code + "#" + MarketPriceServer.serverName + msg + "#" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        } catch (Exception ex) {
            logger.error(ex);
            showErrorMsg("JMS通信出现异常！");
        }
    }

    /**
     * 黄正良 线程等待
     *
     * @param obj
     * @param time
     */
    public void threadWait(Object obj, final int time) {
        try {
            synchronized (obj) {
                obj.wait(time);
            }
        } catch (Exception e) {
            logger.debug("管理线程休眠发生异常……");
            logger.error(e);
        }
    }

    /**
     * 黄正良 发送jms数据
     */
    public void sendMessage(String msg) throws JmsException {
        getJmsSender().sendMessage(msg);
    }

    /**
     * 黄正良 swing界面显示错误信息
     *
     * @param errMsg
     */
    public void showErrorMsg(final String errMsg) {
        logger.debug(errMsg);
        if (MarketPriceGUI.exMsg != null) {
            SwingUtilities.invokeLater(new Thread() {

                public void run() {
                    if (MarketPriceGUI.exMsg.getLineCount() > MarketPriceServer.cls)// 超过多少行清屏一次
                    {
                        MarketPriceGUI.exMsg.setText("");
                    }
                    MarketPriceGUI.exMsg.requestFocus();
                    MarketPriceGUI.exMsg.setCaretPosition(MarketPriceGUI.exMsg
                            .getDocument().getLength());
                    MarketPriceGUI.exMsg.append(""
                            + MarketPriceServer.serverName
                            + errMsg
                            + " 异常时间："
                            + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(new Date())) + "\r\n");
                }
            });
        }
    }

    /**
     * 黄正良 swing界面显示报价信息
     *
     * @param quoteMsg
     */
    public void showQuoteMsg(final String quoteMsg) {
        if (MarketPriceGUI.txtBaoJiaMsg != null) {
            SwingUtilities.invokeLater(new Thread() {

                public void run() {
                    if (MarketPriceGUI.txtBaoJiaMsg.getLineCount() > MarketPriceServer.cls)// 超过多少行清屏
                    {
                        MarketPriceGUI.txtBaoJiaMsg.setText("");
                    }
                    MarketPriceGUI.txtBaoJiaMsg.requestFocus();
                    MarketPriceGUI.txtBaoJiaMsg
                            .setCaretPosition(MarketPriceGUI.txtBaoJiaMsg
                                    .getDocument().getLength());
                    MarketPriceGUI.txtBaoJiaMsg.append(quoteMsg);
                }
            });
        }
    }

    /**
     * 黄正良
     *
     * @param 解析获取报价
     * @return
     */
    public String getQuotes(List list) {
        try {
            if (list == null || list.size() < 1) {
                showErrorMsg("获取网页源的数据为空！");
                return null;
            } else {
                // 显示获取网页数据条数
                showErrorMsg("已获取" + list.size() + "条网页源的数据！");

                logger.debug("主线程开始组装抓取到的数据……");
                StringBuilder s = new StringBuilder();
                Iterator it = list.iterator();// 遍历解析到的网页源的数据并组装成发送的数据
                while (it.hasNext()) { // 将报价数据添加到字符串中
                    Object quote = it.next();
                    if (quote != null) {
                        s.append(quote.toString().trim() + "#");
                    }

                }
                logger.debug("主线程组装抓取到的数据完成……");

                if (s != null && !"".equals(s.toString().trim())) {
                    s = new StringBuilder(MarketPriceServer.serverCode
                            + "&"
                            + s.delete(s.length() - 1, s.length())
                                    .toString());
                    logger.debug("主线程判断组装抓取到的数据是完整的正常返回");
                    return s.toString();
                } else {
                    logger.debug("主线程判断组装抓取到的数据不是完整的返回为空");
                    return null;
                }

            }
        } catch (Exception e) {
            logger.error("组装数据时发生异常返数据为空", e);
            return null;
        }
    }

    /**
     * 黄正良 获取发送报价
     *
     * @param s
     * @return
     */
    public String getShowMsg(String s) {
        String str[] = s.split("\\|");
        StringBuffer msg = new StringBuffer("已发送：");
        for (String m : str) {
            msg.append(m + "  ");
        }
        msg.append("\r\n");
        return msg.toString();
    }

    public CommWriteFile getCommWriteFile() {
        return commWriteFile;
    }

    public void setCommWriteFile(CommWriteFile commWriteFile) {
        this.commWriteFile = commWriteFile;
    }

    public ExceptionSender getExSender() {
        return exSender;
    }

    public void setExSender(ExceptionSender exSender) {
        this.exSender = exSender;
    }

    public JmsSender getJmsSender() {
        return jmsSender;
    }

    public void setJmsSender(JmsSender jmsSender) {
        this.jmsSender = jmsSender;
    }
}
