package com.singlee.priceengine.dao;

import java.io.OutputStream;
/**
 * 
 * 功能:网页数据发送
 *  
 * 杭州新利软件有限公司 2010 版权所有.
 * @author 黄正良  Apr 6, 2010 11:13:17 AM	
 * @version 1.0
 */
public abstract class AbstractWebPageCommonDAO implements WebPageCommonDAOInterface {
	/**
	 * 启动发送网页数据
	 */
	public  abstract  void      serverStart();
	
	/**
	 * 停止发送网页数据
	 */
	public  abstract  void      serverStop();
	
	/**
	 * 判断是否已启动
	 */
	public  abstract  boolean   isServerStart();
	
	/**
	 * 判断是否已停止
	 */
	public  abstract  boolean   isServerStop();
	public  void sendMessage(String msg)throws Exception{}  //JMS发送消息的方法
}