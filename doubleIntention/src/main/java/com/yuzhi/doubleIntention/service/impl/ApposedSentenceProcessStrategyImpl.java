/**  
* <p>Title: ApposedSentenceProcessStrategyImpl.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年5月30日上午11:16:17  
* @version 1.0  
*/  
package com.yuzhi.doubleIntention.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.yuzhi.doubleIntention.dto.ResponseJsonDto;
import com.yuzhi.doubleIntention.dto.constantdto.SentenceStrategyConstant;
import com.yuzhi.doubleIntention.dto.parser.KeenageDataDTO;
import com.yuzhi.doubleIntention.service.ApposedSentenceProcessStrategy;

/**  
* <p>Title: ApposedSentenceProcessStrategyImpl</p>  
* <p>Description: </p>  
* @author Hunter  
* @date 2019年5月30日  
*/
@Service
public class ApposedSentenceProcessStrategyImpl implements ApposedSentenceProcessStrategy,CommandLineRunner {
	
	
	//hownet动态库路径
		@Value("${hownetPathName}")
		private String hownetPath;
	
		 //标准化对照表的缓冲字符流
	    private static BufferedReader normalizationBufferedReader = null;
	    //标准化对照表的map格式缓存
	    private static Map<String, String> normalizationMap = null;
		
	
	/**
	 * 处理策略分发总控
	 */
	@Override
	public ResponseJsonDto processStartegySwitch(String sentenceStrategy,String frstSentenceType, String scndSentenceType,
			List<List<KeenageDataDTO>> splitSentencePreprocess){
		//第一句的标签词位置
		List<Integer> frstSentenceLabelLocation = this.getSentenceLabelLocation(false,frstSentenceType,splitSentencePreprocess.get(0));
		//第二句标签词位置
		List<Integer> scndSentenceLabelLocation = this.getSentenceLabelLocation(true,scndSentenceType,splitSentencePreprocess.get(1));
		if(frstSentenceLabelLocation.isEmpty()||scndSentenceLabelLocation.isEmpty()) {
			return new ResponseJsonDto(505,null,"并列句：没有找到标签词位置！");
		}
		if(frstSentenceLabelLocation.size()>2||scndSentenceLabelLocation.size()>2) {
			return new ResponseJsonDto(505,null,"并列句：单句中标签词超过两个,暂无法处理！");
		}
		if((frstSentenceLabelLocation.size()==1&&!frstSentenceType.equals("疑问_单要素+呢"))||(scndSentenceLabelLocation.size()==1&&!scndSentenceType.equals("疑问_单要素+呢"))) {
			return new ResponseJsonDto(505,null,"并列句：单句中只有一个标签，并且不是单要素标签，暂无法处理！");
		}
		
		//根据策略执行对应函数
		switch (sentenceStrategy) {
			case SentenceStrategyConstant.REPLACE_WHOLE:
				return this.replaceWhole(frstSentenceLabelLocation,scndSentenceLabelLocation,splitSentencePreprocess);
			case SentenceStrategyConstant.REPLACE_ELEMENT:
				return this.replaceElement(scndSentenceLabelLocation,splitSentencePreprocess);
			case SentenceStrategyConstant.APPEND_DELETEMARKER:
				return this.appendDeleteMarker(frstSentenceLabelLocation,scndSentenceLabelLocation,splitSentencePreprocess);
			case SentenceStrategyConstant.APPEND_NORMALIZATION:
				return this.appendNormalization(frstSentenceType,frstSentenceLabelLocation,scndSentenceLabelLocation,splitSentencePreprocess);
			case SentenceStrategyConstant.APPEND_NORM_DM:
				return this.appendNormDM(scndSentenceType,frstSentenceLabelLocation,scndSentenceLabelLocation,splitSentencePreprocess);
			case SentenceStrategyConstant.APPEND_ADDCOMMA:
				return this.appendAddcomma(frstSentenceLabelLocation,scndSentenceLabelLocation,splitSentencePreprocess);
			case SentenceStrategyConstant.REPLACE_SPAREMARKER:
				return this.replaceSpareMarker(frstSentenceLabelLocation,scndSentenceLabelLocation,splitSentencePreprocess);
			default:
				return new ResponseJsonDto(505,null,"并列句：当前系统版本不支持该策略！");
		}
	}
	/**  
	 * <p>Title: replaceSpareMarker</p>  
	 * <p>Description: 把前项带有Marker标签的词塞回替换部分的前面，最后删掉句中问号</p>  
	 * @param frstSentenceLabelLocation
	 * @param scndSentenceLabelLocation
	 * @param splitSentencePreprocess
	 * @return  
	 */
	private ResponseJsonDto replaceSpareMarker(List<Integer> frstSentenceLabelLocation,
			List<Integer> scndSentenceLabelLocation, List<List<KeenageDataDTO>> splitSentencePreprocess) {
		//结果句子返回列表
				List<String> resultSentenceList=new ArrayList<String>();
				//获取原始句子的两句分词对象
				List<KeenageDataDTO> frstSentenceList = splitSentencePreprocess.get(0);
				List<KeenageDataDTO> scndSentenceList = splitSentencePreprocess.get(1);
				//创建两个buffer用来存储拼接句子结果
				StringBuffer resultSentenceOne=new StringBuffer();//返回的第一个句子字符串
				StringBuffer resultSentenceTwo=new StringBuffer();//返回的第二个句子字符串
				/*获取第二句的主要成分*/
				StringBuffer scndSentenceBuffer=new StringBuffer();//第二句主要成分
				for (int i = scndSentenceLabelLocation.get(0); i <= scndSentenceLabelLocation.get(scndSentenceLabelLocation.size()-1); i++) {
					scndSentenceBuffer.append(scndSentenceList.get(i).getExpression());
				}
				boolean notCombineYet=true;//是否还没有拼接了第二句
				/*形成两句返回结果*/
				for (int i = 0; i < frstSentenceList.size(); i++) {
					//第一句正常拼接就ok
					resultSentenceOne.append(frstSentenceList.get(i).getExpression());
					//第二句拼接时，需要判断是否是第一句的标签词部分，如果是，则跳过
					if (i>frstSentenceLabelLocation.get(0)&&i<=frstSentenceLabelLocation.get(frstSentenceLabelLocation.size()-1)) {
						//如果还没有拼接第二句主要成分，就拼接
						if(notCombineYet) {
							//拼接一下
							resultSentenceTwo.append(scndSentenceBuffer.indexOf("？")==-1?scndSentenceBuffer.toString():scndSentenceBuffer.deleteCharAt(scndSentenceBuffer.indexOf("？")).toString());
							//并赋值false,表示已经拼接了，之后再进入这里就不用继续拼接
							notCombineYet=false;
						}
						
					}else {
						//如果当前分词不在第一句标签词范围内，则将其与拼接入第二句的成分
						resultSentenceTwo.append(frstSentenceList.get(i).getExpression());
					}
				}
				String resultSentenceOneString = this.resultSentenceFullstopStandalized(resultSentenceOne.toString());
				String resultSentenceTwoString = this.resultSentenceFullstopStandalized(resultSentenceTwo.toString());
				//存入返回结果列表并返回
				resultSentenceList.add(resultSentenceOneString);
				resultSentenceList.add(resultSentenceTwoString);
				return new ResponseJsonDto(200,resultSentenceList,"success:replace_spareMarker");
	}
	/**  
	 * <p>Title: appendNormalization</p>  
	 * <p>Description: 标准化追加，将前项截取部分按照标准化策略进行归一，然后将后项追加其后</p>  
	 * @param frstSentenceType 
	 * @param frstSentenceLabelLocation
	 * @param scndSentenceLabelLocation
	 * @param splitSentencePreprocess
	 * @return  
	 */
	private ResponseJsonDto appendNormalization(String frstSentenceType, List<Integer> frstSentenceLabelLocation,
			List<Integer> scndSentenceLabelLocation, List<List<KeenageDataDTO>> splitSentencePreprocess) {
		//结果句子返回列表
		List<String> resultSentenceList=new ArrayList<String>();
		//获取原始句子的两句分词对象
		List<KeenageDataDTO> frstSentenceList = splitSentencePreprocess.get(0);
		List<KeenageDataDTO> scndSentenceList = splitSentencePreprocess.get(1);
		//创建两个buffer用来存储拼接句子结果
		StringBuffer resultSentenceOne=new StringBuffer();//返回的第一个句子字符串
		StringBuffer resultSentenceTwo=new StringBuffer();//返回的第二个句子字符串
		/*获取第二句的主要成分*/
		StringBuffer scndSentenceBuffer=new StringBuffer();//第二句主要成分
		for (int i = scndSentenceLabelLocation.get(0); i <= scndSentenceLabelLocation.get(scndSentenceLabelLocation.size()-1); i++) {
			scndSentenceBuffer.append(scndSentenceList.get(i).getExpression());
		}
		boolean notCombineYet=true;//是否还没有拼接了第二句
		/*形成两句返回结果*/
		for (int i = 0; i < frstSentenceList.size(); i++) {
			//第一句正常拼接就ok
			resultSentenceOne.append(frstSentenceList.get(i).getExpression());
			//第二句拼接时，需要判断是否是第一句的标签词部分，如果是，则跳过
			if (i>=frstSentenceLabelLocation.get(0)&&i<=frstSentenceLabelLocation.get(frstSentenceLabelLocation.size()-1)) {
				//如果还没有拼接第二句主要成分，就拼接
				if(notCombineYet) {
					//去标准化对照表中超找标准化映射
					String normalizedString = normalizationMap.get(frstSentenceType);
					if(normalizedString==null||"".endsWith(normalizedString)) {
						return new ResponseJsonDto(505,resultSentenceList,"append_normalization:没有在标准化对照表中找到当前标签");
					}
					//先把标准化词（组）追加到句子后
					resultSentenceTwo.append(normalizedString);
					//然后拼接第二句的内容
					resultSentenceTwo.append(scndSentenceBuffer.indexOf("？")==-1?scndSentenceBuffer.toString():scndSentenceBuffer.deleteCharAt(scndSentenceBuffer.indexOf("？")).toString());
					//并赋值false,表示已经拼接了，之后再进入这里就不用继续拼接
					notCombineYet=false;
				}
			}else {
				//如果当前分词不在第一句标签词范围内，则将其与拼接入第二句的成分
				resultSentenceTwo.append(frstSentenceList.get(i).getExpression());
			}
		}
		String resultSentenceOneString = this.resultSentenceFullstopStandalized(resultSentenceOne.toString());
		String resultSentenceTwoString = this.resultSentenceFullstopStandalized(resultSentenceTwo.toString());
		//存入返回结果列表并返回
		resultSentenceList.add(resultSentenceOneString);
		resultSentenceList.add(resultSentenceTwoString);
		return new ResponseJsonDto(200,resultSentenceList,"success:append_normalization");
	}
	/**
	 * 
	 * <p>Title: resultSentenceFullstopStandalized</p>  
	 * <p>Description:返回句子标点标准化，以防止拼接完的句子中含有多余的分句符号 </p>  
	 * @param resultSentenceString
	 * @return
	 */
	private String resultSentenceFullstopStandalized(String resultSentenceString) {
		return resultSentenceString.replaceAll("[？。！；]", "")+"？";
	}
	/**  
	 * <p>Title: getSentenceLabelLocation</p>  
	 * <p>Description:获取两句的标签个数及位置 </p>  
	 * @param frstSentenceType
	 * @param scndSentenceType	
	 * @param splitSentencePreprocess  
	 */
	private List<Integer> getSentenceLabelLocation(boolean isScndSentenceOrNot ,String sentenceType, List<KeenageDataDTO> splitSentence) {
		//标签位置列表
		List<Integer> sentenceLabelLocations=new ArrayList<Integer>();
		String sentenceKey_1String=null;//key_1列的值
		for (int i = 0; i < splitSentence.size(); i++) {
			if(isScndSentenceOrNot) {//如果是第二句，则去key_2列截取
				sentenceKey_1String=splitSentence.get(i).getKey_2();
			}else {//如果是第一句，则去key_1列截取
				sentenceKey_1String=splitSentence.get(i).getKey_1();
			}
			//只要不为空，并且包含相关标签
			if(sentenceKey_1String!=null&&sentenceKey_1String.contains(sentenceType)) {
				sentenceLabelLocations.add(i);
			}
		}
		return sentenceLabelLocations;
	}
	
	/* (non-Javadoc)  
	 * <p>Title: replaceWholeSentence</p>  
	 * <p>Description: 整体替换策略，用后项通过句式标签截取的部分整体替换前项中标签截取出来的部分</p>  
	 * @param frstSentenceType
	 * @param scndSentenceType
	 * @param splitSentencePreprocess
	 * @return  
	 * @see com.yuzhi.doubleIntention.service.ApposedSentenceProcessStrategy#replaceWholeSentence(java.lang.String, java.lang.String, java.util.List)  
	 */
	private ResponseJsonDto replaceWhole(List<Integer> frstSentenceLabelLocation, List<Integer> scndSentenceLabelLocation,
			List<List<KeenageDataDTO>> splitSentencePreprocess) {
		//结果句子返回列表
		List<String> resultSentenceList=new ArrayList<String>();
		//获取原始句子的两句分词对象
		List<KeenageDataDTO> frstSentenceList = splitSentencePreprocess.get(0);
		List<KeenageDataDTO> scndSentenceList = splitSentencePreprocess.get(1);
		//创建两个buffer用来存储拼接句子结果
		StringBuffer resultSentenceOne=new StringBuffer();//返回的第一个句子字符串
		StringBuffer resultSentenceTwo=new StringBuffer();//返回的第二个句子字符串
		/*获取第二句的主要成分*/
		StringBuffer scndSentenceBuffer=new StringBuffer();//第二句主要成分
		for (int i = scndSentenceLabelLocation.get(0); i <= scndSentenceLabelLocation.get(scndSentenceLabelLocation.size()-1); i++) {
			scndSentenceBuffer.append(scndSentenceList.get(i).getExpression());
		}
		boolean notCombineYet=true;//是否还没有拼接了第二句
		/*形成两句返回结果*/
		for (int i = 0; i < frstSentenceList.size(); i++) {
			//第一句正常拼接就ok
			resultSentenceOne.append(frstSentenceList.get(i).getExpression());
			//第二句拼接时，需要判断是否是第一句的标签词部分，如果是，则跳过
			if (i>=frstSentenceLabelLocation.get(0)&&i<=frstSentenceLabelLocation.get(frstSentenceLabelLocation.size()-1)) {
				//如果还没有拼接第二句主要成分，就拼接
				if(notCombineYet) {
					//拼接一下
					resultSentenceTwo.append(scndSentenceBuffer.indexOf("？")==-1?scndSentenceBuffer.toString():scndSentenceBuffer.deleteCharAt(scndSentenceBuffer.indexOf("？")).toString());
					//并赋值false,表示已经拼接了，之后再进入这里就不用继续拼接
					notCombineYet=false;
				}
			}else {
				//如果当前分词不在第一句标签词范围内，则将其与拼接入第二句的成分
				resultSentenceTwo.append(frstSentenceList.get(i).getExpression());
			}
		}
		String resultSentenceOneString = this.resultSentenceFullstopStandalized(resultSentenceOne.toString());
		String resultSentenceTwoString = this.resultSentenceFullstopStandalized(resultSentenceTwo.toString());
		//存入返回结果列表并返回
		resultSentenceList.add(resultSentenceOneString);
		resultSentenceList.add(resultSentenceTwoString);
		return new ResponseJsonDto(200,resultSentenceList,"success:replace_whole");
	}

	/**  
	 * <p>Title: replaceElementSentence</p>  
	 * <p>Description: 单要素替换，将后项中的单要素根据标签替换掉前项中有相同标签的节点</p>  
	 * @param frstSentenceType
	 * @param scndSentenceType
	 * @param splitSentencePreprocess  
	 * @return 
	 */
	private ResponseJsonDto replaceElement(List<Integer> scndSentenceLabelLocation,
			List<List<KeenageDataDTO>> splitSentencePreprocess) {
		//结果句子返回列表
		List<String> resultSentenceList=new ArrayList<String>();
		//获取原始句子的两句分词对象
		List<KeenageDataDTO> frstSentenceList = splitSentencePreprocess.get(0);
		List<KeenageDataDTO> scndSentenceList = splitSentencePreprocess.get(1);
		
		//创建两个buffer用来存储拼接句子结果
		StringBuffer resultSentenceOne=new StringBuffer();//返回的第一个句子字符串
		StringBuffer resultSentenceTwo=new StringBuffer();//返回的第二个句子字符串
		/*获取第二句的主要成分*/
		List<String> scndSentenceMainPartList =new ArrayList<String>();//第二句主要成分
		List<String> keyStringList=new ArrayList<String>();//及其对应的key列的值
		for (int i = scndSentenceLabelLocation.get(0); i <= scndSentenceLabelLocation.get(scndSentenceLabelLocation.size()-1); i++) {
			scndSentenceMainPartList.add(scndSentenceList.get(i).getExpression());
			//并获取其位置上的key列的值
			if(scndSentenceList.get(i).getKey()==null||"".equals(scndSentenceList.get(i).getKey())) {
				return new ResponseJsonDto(505,null,"replace_element:key_1列标签对应的key列标签为空，无法处理！");
			}
			keyStringList.add(scndSentenceList.get(i).getKey());
		}
		//第一句话插入的位置集合
		if (keyStringList.size()>2) {
			return new ResponseJsonDto(505,null,"replace_element:在第二句中，单要素分词节点超过两个的暂无法处理！");
		}
		for (String string : keyStringList) {
			if(string.contains(",")) {
				return new ResponseJsonDto(505,null,"replace_element:在第二句中，单要素分词节点的key列有多个标签，暂无法处理！");
			}
		}
		List<Integer> frstSentenceDelPartList=null;//要删除的节点位置
		frstSentenceDelPartList=new ArrayList<Integer>();
		for (int j = 0; j < frstSentenceList.size(); j++) {
			//获取key列值
			String key = frstSentenceList.get(j).getKey();
			//如果key列等于null或为空串,那就不参与匹配，直接将该分词作为句子一部分，并跳过当前循环
			if(key==null||"".equals(key)) {
				if(j==frstSentenceList.size()-1) {
					return new ResponseJsonDto(505,null,"replace_element:第一句中没有匹配到第二句中单要素相对应的key列值！");
				}else {
					continue;
				}
			}
			//如果key中包含第二句单要素对应的key的值
			if(key.contains(keyStringList.get(0))) {
				frstSentenceDelPartList.add(j);
				if(keyStringList.size()==2) {//如果单要素有两个节点
					if(j<frstSentenceList.size()-1) {//如果不是倒数第一个节点
						if (frstSentenceList.get(j+1).getKey().contains(keyStringList.get(1))) {
							frstSentenceDelPartList.add(j+1);
							break;
						}else if(j<frstSentenceList.size()-2&&frstSentenceList.get(j+2).getKey().contains(keyStringList.get(1))){
							frstSentenceDelPartList.add(j+2);
							break;
						}else if(j<frstSentenceList.size()-3&&frstSentenceList.get(j+3).getKey().contains(keyStringList.get(1))) {
							frstSentenceDelPartList.add(j+3);
							break;
						}else{//如果不包含，但没遍历完，那就继续遍历
							frstSentenceDelPartList.clear();
							continue;
						}
					}else {//如果是倒数第一个节点
						return new ResponseJsonDto(505,null,"replace_element:第一句即将遍历完，而第二句中还有未匹配到的节点！");
					}
				}
			}else if(j<frstSentenceList.size()-keyStringList.size()) {//如果不包含，但没遍历完，那就继续遍历
				continue;
			}else {//遍历完了,没找到，直接return
				return new ResponseJsonDto(505,null,"replace_element:第一句中没有匹配到第二句中单要素相对应的key列值！");
			}
		}
		
		//拼接句子
		int j = 0;
		for (int i = 0; i < frstSentenceList.size(); i++) {
			//第一句正常拼接就ok
			resultSentenceOne.append(frstSentenceList.get(i).getExpression());
			//第二句拼接
			if (scndSentenceMainPartList.size()==2) {//两个要素标签
				for (; j < frstSentenceDelPartList.size(); ) {
					//如果遍历到了上边记录的位置
					if(j<2&&i==frstSentenceDelPartList.get(j)) {
						//则将第二句的元素插入
						resultSentenceTwo.append(scndSentenceMainPartList.get(j));
						j++;
						break;
					}else {
						resultSentenceTwo.append(frstSentenceList.get(i).getExpression());
						break;
					}
				}
			}else {//一个要素标签
				if(i==frstSentenceDelPartList.get(j)) {
					resultSentenceTwo.append(scndSentenceMainPartList.get(j));
				}else if (frstSentenceDelPartList.size()>1&&i>frstSentenceDelPartList.get(j)&&i<=frstSentenceDelPartList.get(frstSentenceDelPartList.size()-1)) {
					
				}else {
					resultSentenceTwo.append(frstSentenceList.get(i).getExpression());
				}
			}
		}
		String resultSentenceOneString = this.resultSentenceFullstopStandalized(resultSentenceOne.toString());
		String resultSentenceTwoString = this.resultSentenceFullstopStandalized(resultSentenceTwo.toString());
		//存入返回结果列表并返回
		resultSentenceList.add(resultSentenceOneString);
		resultSentenceList.add(resultSentenceTwoString);
		return new ResponseJsonDto(200,resultSentenceList,"success:replace_element");
	}
	
	/**  
	 * <p>Title: appendAddcomma</p>  
	 * <p>Description:加逗追加，后项前面加一个逗号，然后追加到前项的后面 </p>  
	 * @param frstSentenceType
	 * @param scndSentenceType
	 * @param splitSentencePreprocess
	 * @return  
	 */
	private ResponseJsonDto appendAddcomma(List<Integer> frstSentenceLabelLocation, List<Integer> scndSentenceLabelLocation,
			List<List<KeenageDataDTO>> splitSentencePreprocess) {
		//结果句子返回列表
		List<String> resultSentenceList=new ArrayList<String>();
		//获取原始句子的两句分词对象
		List<KeenageDataDTO> frstSentenceList = splitSentencePreprocess.get(0);
		List<KeenageDataDTO> scndSentenceList = splitSentencePreprocess.get(1);
		//创建两个buffer用来存储拼接句子结果
		StringBuffer resultSentenceOne=new StringBuffer();//返回的第一个句子字符串
		StringBuffer resultSentenceTwo=new StringBuffer();//返回的第二个句子字符串
		
		/*获取第二句的主要成分*/
		StringBuffer scndSentenceBuffer=new StringBuffer();//第二句主要成分
		for (int i = scndSentenceLabelLocation.get(0); i <= scndSentenceLabelLocation.get(scndSentenceLabelLocation.size()-1); i++) {
			scndSentenceBuffer.append(scndSentenceList.get(i).getExpression());
		}
		
		/*形成两句返回结果*/
		for (int i = 0; i < frstSentenceList.size(); i++) {
			//第一句正常拼接就ok
			resultSentenceOne.append(frstSentenceList.get(i).getExpression());
			resultSentenceTwo.append(frstSentenceList.get(i).getExpression());
			if (i==frstSentenceLabelLocation.get(frstSentenceLabelLocation.size()-1)) {
				//保证第二句中不带问号过来
				resultSentenceTwo.append("，");
				resultSentenceTwo.append(scndSentenceBuffer.indexOf("？")==-1?scndSentenceBuffer.toString():scndSentenceBuffer.deleteCharAt(scndSentenceBuffer.indexOf("？")).toString());
			}
		}
		String resultSentenceOneString = this.resultSentenceFullstopStandalized(resultSentenceOne.toString());
		String resultSentenceTwoString = this.resultSentenceFullstopStandalized(resultSentenceTwo.toString());
		//存入返回结果列表并返回
		resultSentenceList.add(resultSentenceOneString);
		resultSentenceList.add(resultSentenceTwoString);
		return new ResponseJsonDto(200,resultSentenceList,"success:append_addComma");
	}

	/**  
	 * <p>Title: appendNormDM</p>  
	 * <p>Description: 标准化+删标追加，后项先进行标准化，然后和前项进行删标追加</p>  
	 * @param scndSentenceType 
	 * @param frstSentenceType
	 * @param scndSentenceType
	 * @param splitSentencePreprocess
	 * @return  
	 */
	private ResponseJsonDto appendNormDM(String scndSentenceType, List<Integer> frstSentenceLabelLocation, List<Integer> scndSentenceLabelLocation,
			List<List<KeenageDataDTO>> splitSentencePreprocess) {
		//结果句子返回列表
		List<String> resultSentenceList=new ArrayList<String>();
		//获取原始句子的两句分词对象
		List<KeenageDataDTO> frstSentenceList = splitSentencePreprocess.get(0);
		//创建两个buffer用来存储拼接句子结果
		StringBuffer resultSentenceOne=new StringBuffer();//返回的第一个句子字符串
		StringBuffer resultSentenceTwo=new StringBuffer();//返回的第二个句子字符串
		
		/*这里不用获取第二句的主要成分了，直接根据标签去找标准化词汇就好*/
		
		/*形成两句返回结果*/
		for (int i = 0; i < frstSentenceList.size(); i++) {
			//第一句正常拼接就ok
			resultSentenceOne.append(frstSentenceList.get(i).getExpression());
			if(i==frstSentenceLabelLocation.get(0)) {
				continue;
			}else if (i==frstSentenceLabelLocation.get(frstSentenceLabelLocation.size()-1)) {
				//去标准化对照表中超找标准化映射
				String normalizedString = normalizationMap.get(scndSentenceType);
				//保证第二句中不带问号过来
				resultSentenceTwo.append(normalizedString);
			}else {
				resultSentenceTwo.append(frstSentenceList.get(i).getExpression());
			}
		}
		String resultSentenceOneString = this.resultSentenceFullstopStandalized(resultSentenceOne.toString());
		String resultSentenceTwoString = this.resultSentenceFullstopStandalized(resultSentenceTwo.toString());
		//存入返回结果列表并返回
		resultSentenceList.add(resultSentenceOneString);
		resultSentenceList.add(resultSentenceTwoString);
		return new ResponseJsonDto(200,resultSentenceList,"success:append_norm_dM");
	}


	/**  
	 * <p>Title: appendDeleteMarker</p>  
	 * <p>Description:删标追加，将前项中带有句式标签的节点删掉，然后将后项追加其后 </p>  
	 * @param frstSentenceType
	 * @param scndSentenceType
	 * @param splitSentencePreprocess
	 * @return  
	 */
	private ResponseJsonDto appendDeleteMarker(List<Integer> frstSentenceLabelLocation, List<Integer> scndSentenceLabelLocation,
			List<List<KeenageDataDTO>> splitSentencePreprocess) {
		//结果句子返回列表
		List<String> resultSentenceList=new ArrayList<String>();
		//获取原始句子的两句分词对象
		List<KeenageDataDTO> frstSentenceList = splitSentencePreprocess.get(0);
		List<KeenageDataDTO> scndSentenceList = splitSentencePreprocess.get(1);
		//创建两个buffer用来存储拼接句子结果
		StringBuffer resultSentenceOne=new StringBuffer();//返回的第一个句子字符串
		StringBuffer resultSentenceTwo=new StringBuffer();//返回的第二个句子字符串
		
		/*获取第二句的主要成分*/
		StringBuffer scndSentenceBuffer=new StringBuffer();//第二句主要成分
		for (int i = scndSentenceLabelLocation.get(0); i <= scndSentenceLabelLocation.get(scndSentenceLabelLocation.size()-1); i++) {
			scndSentenceBuffer.append(scndSentenceList.get(i).getExpression());
		}
		
		/*形成两句返回结果*/
		for (int i = 0; i < frstSentenceList.size(); i++) {
			//第一句正常拼接就ok
			resultSentenceOne.append(frstSentenceList.get(i).getExpression());
			if(i==frstSentenceLabelLocation.get(0)) {
				continue;
			}else if (i==frstSentenceLabelLocation.get(frstSentenceLabelLocation.size()-1)) {
				//保证第二句中不带问号过来
				resultSentenceTwo.append(scndSentenceBuffer.indexOf("？")==-1?scndSentenceBuffer.toString():scndSentenceBuffer.deleteCharAt(scndSentenceBuffer.indexOf("？")).toString());
			}else {
				resultSentenceTwo.append(frstSentenceList.get(i).getExpression());
			}
		}
		String resultSentenceOneString = this.resultSentenceFullstopStandalized(resultSentenceOne.toString());
		String resultSentenceTwoString = this.resultSentenceFullstopStandalized(resultSentenceTwo.toString());
		//存入返回结果列表并返回
		resultSentenceList.add(resultSentenceOneString);
		resultSentenceList.add(resultSentenceTwoString);
		return new ResponseJsonDto(200,resultSentenceList,"success:append_deleteMarker");
	}
	/* (non-Javadoc)  
	 * <p>Title: run</p>  
	 * <p>Description: </p>  
	 * @param args
	 * @throws Exception  
	 * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])  
	 */
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		normalizationMap=new HashMap<String, String>();
		//读取标准化对照表字符流
        try {
            normalizationBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(hownetPath + "sentenceNormalization.txt")), "UTF-8"));
            //当前行,因为目前就一行，所以只获取一次就行了
            String currentLine = null;
            while ((currentLine = normalizationBufferedReader.readLine()) != null) {
                if("".equals(currentLine.trim())) {
                	continue;
                }
                String[] split = currentLine.trim().split("	");
                if(split.length!=2) {
                	System.out.println("当前行格式不对");
                	continue;
                }
                normalizationMap.put(split[0], split[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                normalizationBufferedReader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	}

	
	
	
	
}
