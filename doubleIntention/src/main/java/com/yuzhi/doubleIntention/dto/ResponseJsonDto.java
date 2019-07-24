/**  
* <p>Title: ResponseJsonDto.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年4月23日上午10:10:11  
* @version 1.0  
*/
package com.yuzhi.doubleIntention.dto;

import java.util.List;

/**
 * <p>
 * Title: ResponseJsonDto
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author Hunter
 * @date 2019年4月23日
 */
public class ResponseJsonDto {
	private String questionID;
	private String orignalQuestion;
	private int status;
	private List<String> data;
	private String msg;
	
	

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
	 * @return the orignalQuestion
	 */
	public String getOrignalQuestion() {
		return orignalQuestion;
	}
	/**
	 * @param orignalQuestion the orignalQuestion to set
	 */
	public void setOrignalQuestion(String orignalQuestion) {
		this.orignalQuestion = orignalQuestion;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the data
	 */
	public List<String> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(List<String> data) {
		this.data = data;
	}
	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}
	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**  
	* <p>Title: </p>  
	* <p>Description: </p>  
	* @param status
	* @param data
	* @param msg  
	*/ 
	public ResponseJsonDto(String questionID,String orignalQuestion,int status, List<String> data, String msg) {
		super();
		this.questionID = questionID;
		this.orignalQuestion = orignalQuestion;
		this.status = status;
		this.data = data;
		this.msg = msg;
	}
	public ResponseJsonDto(String orignalSentence,int status, List<String> data, String msg) {
		super();
		this.orignalQuestion = orignalSentence;
		this.status = status;
		this.data = data;
		this.msg = msg;
	}
	public ResponseJsonDto(int status, List<String> data, String msg) {
		super();
		this.status = status;
		this.data = data;
		this.msg = msg;
	}
	/**  
	* <p>Title: </p>  
	* <p>Description: </p>    
	*/ 
	public ResponseJsonDto() {
		super();
	}
	
	


}
