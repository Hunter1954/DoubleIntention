/**  
* <p>Title: SentetnceSplitByHownet.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年4月23日上午10:14:52  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.service;

import java.util.List;

import com.yuzhi.doubleIntention.dto.ResponseJsonDto;

/**  
* <p>Title: SentetnceSplitByHownet</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年4月23日  
*/
public interface SentenceSplitByHownet {
	public ResponseJsonDto doubleIntentionProcess(String questionID, String requestQuestion);
}
