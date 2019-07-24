/**  
* <p>Title: SentenceStrategyEnum.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年5月29日下午8:25:21  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.dto.constantdto;

/**  
* <p>Title: SentenceStrategyEnum</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年5月29日  
*/
public class SentenceStrategyConstant {
	
	public static final String DEFAULT_INTENTION="default_intention";//默认意图，潜在多意图问句分句后的第一句作为一个意图进行处理，不必进行拼接处理
	
	public static final String REPLACE_WHOLE="replace_whole";//整体替换，用后项通过句式标签截取的部分整体替换前项中标签截取出来的部分
	public static final String REPLACE_ELEMENT="replace_element";//单要素替换，将后项中的单要素根据标签替换掉前项中有相同标签的节点
	public static final String APPEND_DELETEMARKER="append_deleteMarker";//删标追加，将前项中带有句式标签的节点删掉，然后将后项追加其后
	public static final String APPEND_NORMALIZATION="append_normalization";//标准化追加，将前项截取部分按照标准化策略进行归一，然后将后项追加其后
	public static final String APPEND_NORM_DM="append_norm_dM";//标准化+删标追加，后项先进行标准化，然后和前项进行删标追加
	public static final String APPEND_ADDCOMMA="append_addComma";//加逗追加，后项前面加一个逗号，然后追加到前项的后面
	public static final String REPLACE_SPAREMARKER="replace_spareMarker";//基本逻辑是先进行整体替换（replace_whole）操作，然后把前项带有Marker标签的词塞回替换部分的前面，最后删掉句中问号。
	

}
