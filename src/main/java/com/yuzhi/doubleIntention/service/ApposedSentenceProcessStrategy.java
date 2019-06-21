/**  
* <p>Title: ApposedSentenceProcessStrategy.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年5月30日上午10:50:59  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.service;

import java.util.List;

import com.yuzhi.doubleIntention.dto.parser.KeenageDataDTO;

/**  
* <p>Title: ApposedSentenceProcessStrategy</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年5月30日  
*/
public interface ApposedSentenceProcessStrategy {
	public List<String> processStartegySwitch(String sentenceStrategy,String frstSentenceType, String secndSentenceType,
			List<List<KeenageDataDTO>> splitSentencePreprocess);
}
