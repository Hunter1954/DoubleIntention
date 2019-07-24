/**  
* <p>Title: DllUtil.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年4月23日上午10:34:41  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.util;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;
import com.yuzhi.doubleIntention.service.TextFormater;

/**  
* <p>Title: DllUtil</p>  
* <p>Description:Hownet工具包 </p>  
* @author Hunter  
* @date 2019年4月23日  
*/
public class DllUtil {
	
	
	//编写一个interface接口，名字随便起
	//让此接口继承com.sun.jna.win32.StdCallLibrary接口
	public interface ChineseParserHelper extends StdCallLibrary{
		//将c++提供的所有接口，用Java代码实现一下
				//HowNet初始化接口，对应c++接口列表里的 const bool initHowNet(char* ApPath);
				public boolean initHowNet(String Apath);
				//解析接口，对应c++接口列表里的 const bool parseText(char* ApPath);
				public boolean parseText(String input);
				//获取解析结果接口，对应c++接口列表里的 const char* getJsonRet();
				public String getJsonRet();
		
	}
	
	//预处理动态库
	public interface TextPreprocessor extends StdCallLibrary{
		
		public void format_callback(String text,int type,TextFormater textFormater,int a);
	}
	
	
	
//		public static class TextFormaterImpl implements TextFormater{
//
//			/* (non-Javadoc)
//			 * @see com.yuzhi.springboot.util.DllUtil.TextPreprocessor.TextFormater#TextFormater_callback(java.lang.String)
//			 */
//			@Override
//			public void TextFormater_callback(String article,int a) {
//				System.out.println(article);
//				System.out.println(a);
//			}
//		}
	
	
	//声明一个解析器变量
	private static ChineseParserHelper chineseParserHelper;
	//声明一个预处理变量
	private static TextPreprocessor textPreprocessor;
	
	
	
	//加载预处理动态库
		public static TextPreprocessor getTextPreprocessor(String hownetPath){
			//加载本地dll接口
			if (textPreprocessor==null) {//如果变量不为空，说明已经加载，则直接返回
				System.setProperty("jna.encoding","UTF-8");
				//com.sun.jna.Native类有一个loadLibrary()方法，通过反射的方式来加载动态库
				textPreprocessor = Native.load(hownetPath+"TextPreprocessor",
						TextPreprocessor.class);
//				preprocessor = (Preprocessor) Native.loadLibrary(DllConstant.INITIAL_PATH+"TextPreprocessor",
//						Preprocessor.class);
			}
			//返回实例
			return textPreprocessor;
		};
		
		//创建接口实例的方法，相当于单例模式下的 getInstance()方法
		public static ChineseParserHelper getChineseParserHelper(String hownetPath) {
			//加载本地dll接口
			if (chineseParserHelper==null) {//如果变量不为空，说明已经加载，则直接返回
				System.setProperty("jna.encoding","UTF-8");
				//com.sun.jna.Native类有一个loadLibrary()方法，通过反射的方式来加载动态库
				chineseParserHelper = (ChineseParserHelper) Native.load(hownetPath+"HowNet_ChineseParser_Helper",
						ChineseParserHelper.class);
			}
			//返回实例
			return chineseParserHelper;
		}
	
	
}
