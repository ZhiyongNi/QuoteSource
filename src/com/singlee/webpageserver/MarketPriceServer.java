package com.singlee.webpageserver;

import com.singlee.priceengine.common.ExceptionSender;
import org.apache.log4j.Logger;
import com.singlee.pricesource.exception.CatchWebPageException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import org.springframework.jms.JmsException;

public class MarketPriceServer {

    public static String serverName;//网页源名称
    public static int serverCode;//网页源代码
    public static int reCatchPageTime;//重新抓取网页的频率
    public static int warnTimes; //连接解析网页出错的最大次数
    public static int cls;//超过些值清一次屏
    private ExceptionSender exSender;// jms异常发送类

    private Logger logger = Logger.getLogger(this.getClass());
    private String xmlPathName;

    public static boolean isAlive_ServerThread = false;// 判断线程是否启动
    public static boolean isAlive_Sender_ManageThread = false;// 判断线程是否启动
    public static boolean isAlive_Sender_WorkThread = false;// 判断线程是否启动

    public static Sender_ManageThread Sender_ManageThread; // 管理发送数据线程的线程
    public static Sender_WorkThread Sender_WorkThread ;// 发送数据线程

    /**
     * 黄正良 启动发送数据线程
     */
    private void startMainThread() {
        try {
            Sender_ManageThread = new Sender_ManageThread();
            Sender_ManageThread.start();
        } catch (Exception e) {
            isAlive_Sender_ManageThread = false;
            sendExMsg("000", "启运管理发送数据线程的线程出现异常!", e);
            showErrorMsg("启运管理发送数据线程的线程出现异常！");
        } finally {
            isAlive_Sender_ManageThread = true;
        }
        try {
            Sender_WorkThread = new Sender_WorkThread();
            Sender_WorkThread.start();
        } catch (Exception e) {
            isAlive_Sender_WorkThread = false;
            sendExMsg("000", "启动出现异常,请查看!", e);
            showErrorMsg("启运发送数据线程出现异常！");
        } finally {
            isAlive_Sender_WorkThread = true;
        }
        if (isAlive_Sender_WorkThread && isAlive_Sender_ManageThread) {
            isAlive_ServerThread = true;
        }
    }

    /**
     * 黄正良 发送数据的管理线程
     *
     */
    class Sender_ManageThread extends Thread {

        public void run() {
            logger.debug("管理线程开始起动……");
            while (!MarketPriceServer.isAlive_ServerThread) {
                logger.debug("管理线程循环开始……");

                logger.debug("管理线程开始判断主线程是否在运行……");
                if (!isRun(Sender_WorkThread, "发送数据的线程")) { // 判断发送数据的线程是否停止，是则重新启动发送数据的线程否则不做任何处理
                    logger.debug("管理线程检测到主线程已停止运行……");

                    Sender_WorkThread.run();// 启动主线程
                    logger.debug("管理线程重起主线程成功……");
                } else {
                    logger.debug("管理线程检测到主线程正常运行……");
                }
                logger.debug("管理线程开始休眠……");
                threadWait(new Object(), 30000);
            }
        }
    }

    /**
     * 黄正良 发送数据的本线程
     */
    class Sender_WorkThread extends Thread {

        public void run() {
            int counter = 0;// 计数器记录是否连续解析网页源失败次数达到指定的次数
            int k = 1; // jms通信状态 1 正常 0 断开
            MarketPriceSender MarketPS = new MarketPriceSender();

            logger.debug("主线程开始运行……");
            while (MarketPriceServer.isAlive_ServerThread) {

                logger.debug("主线程循环开始运行……");
                try {
                    logger.debug("主线程开始判断管理线程是否在运行……");
                    // 判断发送数据的线程是否停止，是则重新启动发送数据的线程否则不做任何处理
                    if (!isRun(Sender_ManageThread, "管理发送数据线程的线程")) {
                        logger.debug("主线程检测到管理线程已停止运行……");
                        startMainThread();// 启动管理线程
                        logger.debug("主线程重起管理线程成功……");
                    }
                    logger.debug("主线程判断管理线程是否在运行结束……");
                } catch (final Exception e) {
                    logger.debug("主线程捕获最终的异常……");
                    // 将异常发送到jms中
                    sendExMsg("000", "出现未知异常最可能原因是JMS通信出现问题！", e);
                    // 将异常在界面显示
                    showErrorMsg("出现未知异常最可能原因是JMS通信出现问题！");

                    logger.debug("主线程处理捕获最终的异常完成……");
                }

                try {
                    logger.debug("主线程开始调用getJmsData()抓取网页……");
                    MarketPS.catchQuoteData();
                    //continue;
                    logger.debug("主线程调用getJmsData()抓取网页结束……");

                } catch (final CatchWebPageException e) {
                    logger.debug("主线程捕获到抓取网页数据出现异常……");
                    counter++;// 记录连录出现网页解析异常的次数

                    // 判断是异常次数是否满足界面显示的要求
                    if (counter >= MarketPriceServer.warnTimes) {
                        counter = 0;
                        sendExMsg("001", e.getMessage(), e);
                    }

                    showErrorMsg(e.getMessage());// 将异常信息在界面显示

                    logger.debug("主线程处理捕获到抓取网页数据异常完成……");
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(MarketPriceServer.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {

                    if (MarketPS.getQuoteData_List() == null) {
                        logger.debug("主线程开始休眠……");
                        //threadWait(new Object(), MarketPriceServer.reCatchPageTime);// 重新抓取数据的时间
                        logger.debug("主线程休眠完成……");
                    } else {
                        logger.debug("主线程开始发送组装的数据……");
                        MarketPS.sendQuoteData();
                        logger.debug("主线程发送组装的数据完成……");

                        logger.debug("主线程将jms通信状况在界面显示……");
                        logger.debug("主线程将jms通信状况在界面显示完成……");
                    }

                    logger.debug("主线程开始将组装抓取到的数据写入文件并显示……");
                    // 将组装好的数据记录在文件中

                    counter = 0;// 计数器清0
                    if (k == 0) {
                        k = 1;
                        showErrorMsg("JMS通信已恢复正常!");
                    }
                } catch (JmsException e) {
                    logger.debug("主线程捕获到jms通信的异常……");

                    k = 0; // 改变jms通信状态为停止
                    // 发送异常数据
                    sendExMsg("000", "JMS通信出现异常！", e);
                    // 将ms异常 在界面显示
                    showErrorMsg("JMS通信出现异常！");

                    logger.debug("主线程处理捕获到jms通信的异常完成……");
                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(MarketPriceServer.class.getName()).log(Level.SEVERE, null, ex);
                }
                logger.debug("主线程开始休眠……");
                threadWait(new Object(), MarketPriceServer.reCatchPageTime);// 重新抓取数据的时间
                logger.debug("主线程休眠完成……");
            }
        }
    }

    /**
     * 黄正良 调用此方法启动网页源
     */
    public void startServer() {
        //this.isAlive_ServerThread = true;// 改变线程启动状态
        startMainThread();// 启动主线程
    }

    /**
     * 黄正良 调用此方法停止网页源
     */
    public void stopServer() {
        try {
            this.isAlive_ServerThread = false;
            // 清除管理线程对象
            if (Sender_ManageThread != null) {
                Sender_ManageThread.interrupt();
                Sender_ManageThread = null;
            }
            // 清除发送线程对象
            if (Sender_WorkThread != null) {
                Sender_WorkThread.interrupt();
                Sender_WorkThread = null;
            }
        } catch (Exception e) {
            sendExMsg("000", "停止出现异常", e);
            isAlive_ServerThread = false;// 改变发送数据的线程的状为停止
        }
    }

    /**
     * 黄正良 判断服务器是否启动
     */
    public boolean isAlive_Server() {
        return isAlive_ServerThread;
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

    public boolean isRun(Thread thread, String threadName) {

        boolean flag = (thread == null);
        logger.debug(threadName + " 是否为空：" + (flag)
                + " 运行状态 " + (flag ? "" : thread.isAlive())
                + " 是否终断 " + (flag ? "" : thread.isInterrupted())
                + " 后台线程 " + (flag ? "" : thread.isDaemon())
                + " 运行状态 " + (flag ? "" : thread.getState()));
        if (thread == null || !thread.isAlive()
                || thread.isInterrupted() || thread.getState() == Thread.State.TERMINATED) {
            return false;
        } else {
            return true;
        }
    }

    public String getXmlPathName() {
        return xmlPathName;
    }

    public void setXmlPathName(String xmlPathName) {
        this.xmlPathName = xmlPathName;
    }

    public int getWarnTimes() {
        return warnTimes;
    }

    public void setWarnTimes(int warnTimes) {
        this.warnTimes = warnTimes;
    }

    public MarketPriceServer() throws Exception {
    }

    public int getServerCode() {
        return serverCode;
    }

    public void setServerCode(int serverCode) throws Exception {
        MarketPriceServer.serverCode = serverCode;
    }

    public int getReCatchPageTime() {
        return reCatchPageTime;
    }

    public void setReCatchPageTime(int reCatchPageTime) {
        this.reCatchPageTime = reCatchPageTime;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getCls() {
        return cls;
    }

    public void setCls(int cls) {
        MarketPriceServer.cls = cls;
    }
}
