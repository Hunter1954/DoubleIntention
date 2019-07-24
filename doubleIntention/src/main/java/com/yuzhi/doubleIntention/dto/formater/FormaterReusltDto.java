/**  
* <p>Title: FormaterReusltDto.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年5月17日上午10:45:31  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.dto.formater;

/**  
* <p>Title: FormaterReusltDto</p>  
* <p>Description:Hownet的Formater返回结果实体类 </p>  
* @author Hunter  
* @date 2019年5月17日  
*/
public class FormaterReusltDto {
	private String src;//原句子
	private String clean;//清洗过的句子
	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}
	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	/**
	 * @return the clean
	 */
	public String getClean() {
		return clean;
	}
	/**
	 * @param clean the clean to set
	 */
	public void setClean(String clean) {
		this.clean = clean;
	}
	
	
	
}
