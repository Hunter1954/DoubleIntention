/**  
* <p>Title: SimulateService.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年7月2日下午4:38:41  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.thread.entity;

import org.springframework.stereotype.Service;

import com.yuzhi.doubleIntention.dto.ResponseJsonDto;

/**  
* <p>Title: SimulateService</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年7月2日  
*/
@Service
public interface SimulateService {
	public ResponseJsonDto doubleIntentionProcess(String requestQuestion,int i);
}
