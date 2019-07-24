/**  
* <p>Title: SimulateServiceImpl.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年7月2日下午4:40:57  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.thread.service.impl;

import java.util.List;

import com.yuzhi.doubleIntention.dto.ResponseJsonDto;
import com.yuzhi.doubleIntention.thread.entity.SimulateService;

/**  
* <p>Title: SimulateServiceImpl</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年7月2日  
*/
public class SimulateServiceImpl implements SimulateService {

	/* (non-Javadoc)  
	 * <p>Title: doubleIntentionProcess</p>  
	 * <p>Description: </p>  
	 * @param requestQuestion
	 * @return  
	 * @see com.yuzhi.doubleIntention.thread.entity.SimulateService#doubleIntentionProcess(java.lang.String)  
	 */
	@Override
	public ResponseJsonDto doubleIntentionProcess(String requestQuestion,int i) {
		ResponseJsonDto responseJsonDto=null;
		if(i==1) {
			//如果只有一句
			//那将其作为并列词、词组处理
			responseJsonDto = new ResponseJsonDto(200,null,"success");
			responseJsonDto.setOrignalQuestion(requestQuestion);
			return responseJsonDto;
//				return	null;
		}else if(i==2) {
			//否则作为并列句处理
			responseJsonDto = new ResponseJsonDto(200,null,"success");
			responseJsonDto.setOrignalQuestion(requestQuestion);
			return responseJsonDto;
		
		}else {
			responseJsonDto = new ResponseJsonDto(requestQuestion,505,null,"非双句并列或单句，暂不处理！");
			responseJsonDto.setOrignalQuestion(requestQuestion);
			return responseJsonDto;
		}
		
	}

}
