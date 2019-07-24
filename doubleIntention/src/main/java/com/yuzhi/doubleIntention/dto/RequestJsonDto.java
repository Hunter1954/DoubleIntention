/**  
* <p>Title: RequestJsonDto.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年4月23日上午10:04:59  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.dto;

/**  
* <p>Title: RequestJsonDto</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年4月23日  
*/
public class RequestJsonDto {
	private String questionID;
	private String requestQuestion;
	
	/**
	 * @return the questionID
	 */
	public String getQuestionID() {
		return questionID;
	}
	/**
	 * @param questionID the questionID to set
	 */
	public void setQuestionID(String questionID) {
		this.questionID = questionID;
	}
	/**
	 * @return the requestQuestion
	 */
	public String getRequestQuestion() {
		return requestQuestion;
	}
	/**
	 * @param requestQuestion the requestQuestion to set
	 */
	public void setRequestQuestion(String requestQuestion) {
		this.requestQuestion = requestQuestion;
	}
	
	
	
}
