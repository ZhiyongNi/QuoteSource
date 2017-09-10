package com.singlee.priceengine.common;

import java.io.FileWriter;
import java.util.Iterator;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 
 * 功能:启动状态管理类
 *  
 * 杭州新利软件有限公司 2010 版权所有.
 * @author 黄正良  2010-9-19 上午09:03:30	
 * @version 1.0
 */
public class RunFlag{
	/**
	 * 功能:设置程序启动的状态
	 * 
	 * 创建人 黄正良  日期 2010-9-19 上午09:04:13
	 * 修改人        日期
	 * 修改摘要
	 * @param fileName
	 * @param flag
	 * @throws Exception
	 */
	public static void setRunFlag(String fileName,boolean flag)throws Exception{
		/*
			SAXReader reader = new SAXReader();
			Document document = reader.read(fileName);
			Element root = document.getRootElement();
			for (Iterator i = root.elementIterator("bean"); i.hasNext();)
			{
				Element bean = (Element) i.next();
				Attribute attr = bean.attribute("id");
				if (attr != null && attr.getValue().equals("webPageServer"))
				{
					for (Iterator property = bean.elementIterator("property"); property
							.hasNext();)
					{
						Element pro = (Element) property.next();
						Attribute name = pro.attribute("name");
						if (name.getValue().equals("isRuning"))
						{
							Attribute value = pro.attribute("value");
							value.setValue(String.valueOf(flag));
						}
					}
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");
			XMLWriter xmlWriter = new XMLWriter(
					new FileWriter(fileName), format);
			xmlWriter.write(document);
			xmlWriter.close();
			*/
	}
	
	public static boolean getRunFlag(String fileName)throws Exception{
//		SAXReader reader = new SAXReader();
//		Document document = reader.read(fileName);
//		Element root = document.getRootElement();
//		for (Iterator i = root.elementIterator("bean"); i.hasNext();)
//		{
//			Element bean = (Element) i.next();
//			Attribute attr = bean.attribute("id");
//			if (attr != null && attr.getValue().equals("webPageServer"))
//			{
//				for (Iterator property = bean.elementIterator("property"); property
//						.hasNext();)
//				{
//					Element pro = (Element) property.next();
//					Attribute name = pro.attribute("name");
//					if (name.getValue().equals("isRuning"))
//					{
//						Attribute value = pro.attribute("value");
//						return Boolean.parseBoolean(value.getValue().trim());
//					}
//				}
//			}
//		}
		return false;
}
}
