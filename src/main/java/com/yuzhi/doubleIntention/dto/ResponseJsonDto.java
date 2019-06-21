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
	private int status;
	private Object data;
	private String msg;
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
	public Object getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
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
	public ResponseJsonDto(int status, Object data, String msg) {
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
