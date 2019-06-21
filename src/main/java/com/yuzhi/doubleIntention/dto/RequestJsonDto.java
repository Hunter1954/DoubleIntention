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
	private String requestID;
	private String requestQuestion;
	/**
	 * @return the requestID
	 */
	public String getRequestID() {
		return requestID;
	}
	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(String requestID) {
		this.requestID = requestID;
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
