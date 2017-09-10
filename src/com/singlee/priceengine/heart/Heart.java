package com.singlee.priceengine.heart;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * 功能:报价源接口心跳
 *  
 * 杭州新利软件有限公司 2010 版权所有.
 * @author 黄正良  Apr 6, 2010 11:25:37 AM	
 * @version 1.0
 */
public class Heart extends Thread{
	
	private int				serverPort;
	private ServerSocket	serverSocket;
	private boolean			isStop	= false;
	public int getServerPort()
	{
		return serverPort;
	}
	
	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}
	
	public void run()
	{
		serverStart();
	}
	
	public void serverStart()
	{
		try
		{
			serverSocket = new ServerSocket(serverPort);
			while (!isStop)
			{
				try{
				if(serverSocket==null || serverSocket.isClosed()){
					serverSocket = new ServerSocket(serverPort);
				}
				this.new SocketThread(serverSocket.accept()).start();
				}catch(Exception e){
					
				}
			}
		} catch (IOException e)
		{}
	}
	
	public void serverStop()
	{
		try
		{
			isStop = true;
			serverSocket.close();
		} catch (IOException e)
		{}
	}
	
	/**
	 * 
	 * 功能:心跳主线程
	 *  
	 * 杭州新利软件有限公司 2010 版权所有.
	 * @author 黄正良  Apr 6, 2010 11:26:17 AM	
	 * @version 1.0
	 */
	class SocketThread extends Thread{
		
		Socket	socket;
		
		public SocketThread(Socket socket){
			this.socket = socket;
		}
		
		public void run()
		{
			try
			{
				socket.setOOBInline(true);
				socket.setSoTimeout(60000);
				InputStream input = socket.getInputStream();
				byte[] b = new byte[28];
				int i = 1;
				while (!socket.isClosed() && !isStop
						&& (i = input.read(b, 0, 28)) != -1)
				{}
				if (socket != null)
					socket.close();
			} catch (Exception e)
			{
				try
				{
					if (socket != null)
						socket.close();
				} catch (IOException e1)
				{}
			}
		}
	}
}
