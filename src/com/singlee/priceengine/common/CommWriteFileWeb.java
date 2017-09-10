package com.singlee.priceengine.common;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.singlee.webpageserver.MarketPriceServer;
/**
 * 
 * 功能:将数据写入文件
 *  
 * 杭州新利软件有限公司 2010 版权所有.
 * @author 黄正良  2010-9-7 上午10:27:29	
 * @version 1.0
 */
public class CommWriteFileWeb {
	private static File file;
	private static List<String> dataList=new ArrayList<String>();//数据缓冲List,防止写文件出现异常时未将所有数据写入文件中
	
	private static FileWriter write;
	private String filepath;//文件路径
	public CommWriteFileWeb(){}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public CommWriteFileWeb(String filepath)throws Exception{
			
	}
	/**
	 * 功能:创建文件 如果当天文件存在则直接使用否则以当天日期重新创建一个文件
	 * 
	 * 创建人 黄正良  日期 2010-9-7 上午10:28:59
	 * 修改人        日期
	 * 修改摘要
	 * @throws Exception
	 */
	
	public void makeFile()throws Exception{
		try{
			String currentDate=DateFormat.getDateInstance().format(new Date());
			this.file=new File(filepath);
			boolean b=file.mkdirs();
			this.file=new File(filepath+currentDate+"-web-"+(MarketPriceServer.serverCode)+".txt");
			if(!file.exists()){
				file.createNewFile();
				if(this.write!=null){
					this.write.close();
					this.write=null;
				}
				 this.write=new FileWriter(this.file,true);
			}else{
				if(this.write==null){
					 this.write=new FileWriter(this.file,true);
				}
			}
			
			}catch(Exception e){
				e.printStackTrace();
				this.write.close();
				this.write=null;
				this.file=null;
			 throw new Exception("生成文件对象失败！"+e.getMessage()+"\r\n",e);	
			}
	}
	
	/**
	 * 功能:写文件的方法
	 * 
	 * 创建人 黄正良  日期 2010-9-7 上午10:30:45
	 * 修改人        日期
	 * 修改摘要
	 * @param qsinfo
	 * @throws Exception
	 */
	public synchronized void writeFile(String qsinfo)throws Exception{
		makeFile();
		dataList.add(qsinfo);
		if(dataList==null || dataList.size()<0){
			return;
		}
		StringBuilder s=new StringBuilder();
		try{
		for(int i=0;i<dataList.size();i++){
			s.append(dataList.get(i));
			dataList.remove(i);
		}
		
		write.append(s);
		write.flush();
		s=null;
		
		}catch(IOException e){
			write.close();
			write=null;
			file=null;
			throw new IOException("文件写入流出现异常"+e.getMessage()+"\r\n"+"数据写入日志失败！\r\n"+s+"\r\n"+e.getMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("发生意想不到的异常"+e.getMessage()+"\r\n无法预知以下数据：\r\n"+s+"\r\n是否写入到日志文件!");
		}
		
	}
	
	
}
