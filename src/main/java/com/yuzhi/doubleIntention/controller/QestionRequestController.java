/**  
* <p>Title: QestionRequestController.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年4月11日上午8:35:45  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yuzhi.doubleIntention.dto.RequestJsonDto;
import com.yuzhi.doubleIntention.dto.ResponseJsonDto;
import com.yuzhi.doubleIntention.service.SentenceSplitByHownet;

/**  
* <p>Title: QestionRequestController</p>  
* <p>Description: 双意图转发控制层，接收并转发处理问题请求</p>  
* @author Hunter  
* @date 2019年4月11日  
*/


@RestController
@EnableAutoConfiguration
public class QestionRequestController {
	
	@Autowired
	private SentenceSplitByHownet sentenceSplitByHownet;
	
	@PostMapping
	public Object qestionIntention(@RequestBody(required=true)RequestJsonDto requestJsonDto) {
		if(requestJsonDto==null||"".equals(requestJsonDto.getRequestID())||"".equals(requestJsonDto.getRequestQuestion())) {
			return new ResponseJsonDto(500,null,"params shouldn't contains null or empty string");
		}
		//调用service层，解析句子并拆分
		List<String> sentenceSplited= sentenceSplitByHownet.doubleIntentionProcess(requestJsonDto.getRequestQuestion());
		return new ResponseJsonDto(200,sentenceSplited,"success");
	}
}
