package com.singlee.webpageserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.singlee.priceengine.common.RunFlag;
import com.singlee.priceengine.heart.Heart;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class MarketPriceGUI extends javax.swing.JFrame {

    {
        //Set Look & Feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Logger logger = Logger.getLogger(this.getClass());// 日志
    private JButton stop;
    public static JTextArea exMsg;
    private JScrollPane jScrollPane2;
    public static JTextArea txtBaoJiaMsg;
    private JScrollPane jScrollPane1;
    private JButton jButton1;
    private JButton clearMsg;
    private JLabel jLabel1;
    private JButton start;
    private JLabel jLabel2;
    private Thread startThread;  //  @jve:decl-index=0:
    public static AbstractApplicationContext atx = new ClassPathXmlApplicationContext(new String[]{"config/server.xml"});//获取配置文件信息  //  @jve:decl-index=0:
    public Heart heart = (Heart) atx.getBean("heart");
    public MarketPriceServer webPageServer;

    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args) {
        try {
            Locale.setDefault(new Locale("zh", "CN"));
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    MarketPriceGUI inst = new MarketPriceGUI();
                    inst.setLocationRelativeTo(null);
                    inst.setVisible(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MarketPriceGUI() {
        super();
        initGUI();
    }

    private void initGUI() {
        try {
            webPageServer = (MarketPriceServer) atx.getBean("webPageServer");
            if (RunFlag.getRunFlag(webPageServer.getXmlPathName())) {
                JOptionPane.showMessageDialog(null, "对不起,您已经启动了" + webPageServer.serverName + "或请手动将启动状态改为false再启动!");
                System.exit(0);
            } else {
                try {
                    RunFlag.setRunFlag(webPageServer.getXmlPathName(), true);
                } catch (Exception e) {
                    logger.error(e);
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "对不起,设置程序为已启动时出错，不能再限制程序启动的个数！");
                }
            }
            heart.start();
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            getContentPane().setLayout(null);
            this.setFocusTraversalKeysEnabled(false);
            this.setResizable(false);
            this.setTitle(webPageServer.getServerName());
            this.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent evt) {
                    SwingUtilities.invokeLater(new Thread() {
                        public void run() {
                            if (startThread != null) {
                                startThread.stop();
                            }
                            webPageServer.ServerStop();
                            if (atx != null) {
                                heart.stop();
                                atx.destroy();
                            }
                        }
                    });
                    try {
                        RunFlag.setRunFlag(webPageServer.getXmlPathName(), false);
                    } catch (Exception e) {
                        logger.error(e);
                        JOptionPane.showMessageDialog(null, "对不起,设置程序为未启动时出错,请手动修改启动状态为false,否则程序不能再启动!");
                    }
                }
            });
            {
                stop = new JButton();
                getContentPane().add(stop);
                stop.setText("\u505c\u6b62");
                stop.setBounds(643, 10, 61, 22);
                stop.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(new Thread() {
                            public void run() {
                                if (startThread != null) {
                                    startThread.stop();
                                }
                                webPageServer.ServerStop();
                            }
                        });
                        if (webPageServer.isServerStop()) {
                            start.setEnabled(true);
                            stop.setEnabled(false);
                            exMsg.requestFocus();
                            exMsg.setCaretPosition(exMsg.getDocument().getLength());
                            exMsg.append("服务器已关闭!\r\n");
                        }
                    }
                });
            }

            {
                start = new JButton();
                getContentPane().add(start);
                start.setText("\u542f\u52a8\u7f51\u9875\u6e90");
                start.setBounds(535, 10, 102, 22);
                start.setEnabled(false);
                start.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        startThread = new Thread() {
                            public void run() {
                                webPageServer = (MarketPriceServer) atx.getBean("webPageServer");
                                webPageServer.startServer();
                            }
                        };
                        SwingUtilities.invokeLater(startThread);
                        if (webPageServer.isAlive_Server()) {
                            start.setEnabled(false);
                            stop.setEnabled(true);
                            exMsg.requestFocus();
                            exMsg.setCaretPosition(exMsg.getDocument().getLength());
                            exMsg.append("服务器已启动!\r\n");
                        }
                    }
                });
            }
            {
                jLabel1 = new JLabel();
                getContentPane().add(jLabel1);
                jLabel1.setText("\u53d1\u51fa\u7684\u62a5\u4ef7\u4fe1\u606f");
                jLabel1.setBounds(7, 32, 84, 15);
            }
            {
                clearMsg = new JButton();
                getContentPane().add(clearMsg);
                clearMsg.setText("\u6e05\u7a7a");
                clearMsg.setBounds(643, 355, 61, 22);
                clearMsg.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        txtBaoJiaMsg.setText("");
                    }
                });
            }
            {
                jButton1 = new JButton();
                getContentPane().add(jButton1);
                jButton1.setText("\u6e05\u7a7a");
                jButton1.setBounds(637, 570, 61, 22);
                jButton1.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        exMsg.setText("");
                    }
                });
            }
            {
                jScrollPane1 = new JScrollPane();
                getContentPane().add(jScrollPane1);
                jScrollPane1.setBounds(7, 53, 697, 296);
                {
                    txtBaoJiaMsg = new JTextArea();
                    jScrollPane1.setViewportView(txtBaoJiaMsg);
                    txtBaoJiaMsg.setBounds(7, 53, 478, 180);
                    txtBaoJiaMsg.setEditable(false);
                    //txtBaoJiaMsg.setPreferredSize(new java.awt.Dimension(691, 178));
                }
            }
            {
                jScrollPane2 = new JScrollPane();
                getContentPane().add(jScrollPane2);
                jScrollPane2.setBounds(0, 384, 702, 180);
                {
                    exMsg = new JTextArea();
                    jScrollPane2.setViewportView(exMsg);
                    exMsg.setBounds(7, 267, 478, 177);
                    exMsg.setWrapStyleWord(true);
                    exMsg.setEditable(false);
                }
            }
            {
                jLabel2 = new JLabel();
                getContentPane().add(jLabel2);
                jLabel2.setText("\u670d\u52a1\u5668\u8fd0\u884c\u4fe1\u606f");
                jLabel2.setBounds(10, 359, 84, 15);
            }
            pack();
            this.setSize(722, 636);
            startThread = new Thread() {
                public void run() {
                    webPageServer = (MarketPriceServer) atx.getBean("webPageServer");
                    webPageServer.startServer();
                }
            };
            startThread.start();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }
}
