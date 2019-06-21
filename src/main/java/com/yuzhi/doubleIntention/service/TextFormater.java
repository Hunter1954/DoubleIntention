/**  
* <p>Title: TextFormater.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年5月17日上午10:34:32  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.service;

import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

/**  
* <p>Title: TextFormater</p>  
* <p>Description:实现函数回调的接口 </p>  
* @author Hunter  
* @date 2019年5月17日  
*/
public interface TextFormater extends StdCallCallback{
	public void TextFormater_callback(String article,int a);
}
