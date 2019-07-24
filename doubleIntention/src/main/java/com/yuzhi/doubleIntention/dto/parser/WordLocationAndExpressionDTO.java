/**  
* <p>Title: WordLocationAndExpressionDTO.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年4月23日下午12:05:46  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.dto.parser;

import java.util.List;

/**  
* <p>Title: WordLocationAndExpressionDTO</p>  
* <p>Description: 分词位置和内容实体类</p>  
* @author Hunter  
* @date 2019年4月23日  
*/
public class WordLocationAndExpressionDTO {
	private int wordLocation;
	private List<String> wordExpression;
	/**
	 * @return the wordLocation
	 */
	public int getWordLocation() {
		return wordLocation;
	}
	/**
	 * @param wordLocation the wordLocation to set
	 */
	public void setWordLocation(int wordLocation) {
		this.wordLocation = wordLocation;
	}
	
	/**
	 * @return the wordExpression
	 */
	public List<String> getWordExpression() {
		return wordExpression;
	}
	/**
	 * @param wordExpression the wordExpression to set
	 */
	public void setWordExpression(List<String> wordExpression) {
		this.wordExpression = wordExpression;
	}
	/**  
	* <p>Title: </p>  
	* <p>Description: </p>    
	*/ 
	public WordLocationAndExpressionDTO() {
		super();
	}
	/**  
	* <p>Title: </p>  
	* <p>Description: </p>  
	* @param wordLocation
	* @param wordExpression  
	*/ 
	public WordLocationAndExpressionDTO(int wordLocation, List<String> wordExpression) {
		super();
		this.wordLocation = wordLocation;
		this.wordExpression = wordExpression;
	}
	
	
}
