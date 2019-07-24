/**  
* <p>Title: SentenceSplitByHownetImpl.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>  
* <p>Company: www.yuzhi.com</p>  
* @author Hunter  
* @date 2019年4月23日上午10:20:26  
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yuzhi.doubleIntention.dto.ResponseJsonDto;
import com.yuzhi.doubleIntention.dto.constantdto.SentenceStrategyConstant;
import com.yuzhi.doubleIntention.dto.formater.FormaterReusltDto;
import com.yuzhi.doubleIntention.dto.parser.AnalyzeResultDTO;
import com.yuzhi.doubleIntention.dto.parser.KeenageDataDTO;
import com.yuzhi.doubleIntention.dto.parser.SentenceDTO;
import com.yuzhi.doubleIntention.dto.parser.WordLocationAndExpressionDTO;
import com.yuzhi.doubleIntention.service.ApposedSentenceProcessStrategy;
import com.yuzhi.doubleIntention.service.SentenceSplitByHownet;
import com.yuzhi.doubleIntention.service.TextFormater;
import com.yuzhi.doubleIntention.util.DllUtil;
import com.yuzhi.doubleIntention.util.DllUtil.ChineseParserHelper;
import com.yuzhi.doubleIntention.util.DllUtil.TextPreprocessor;

/**  
* <p>Title: SentenceSplitByHownetImpl</p>  
* <p>Description:业务层，双意图分句接口实现类 </p>  
* @author Hunter  
* @date 2019年4月23日  
*/
@Service
public class SentenceSplitByHownetImpl implements SentenceSplitByHownet,CommandLineRunner,TextFormater{
	
	
	//hownet动态库路径
	@Value("${hownetPathName}")
	private String hownetPath;
	//文本解析器动态库对象
	private static ChineseParserHelper chineseParserHelper;
	//文本预处理动态库对象
	private static TextPreprocessor textPreprocessor;
	
	//	//句型策略关系表的缓冲字符流
    private static BufferedReader sentenceTypeAndSplitStrategyReader = null;
    //	//句型策略关系表的map格式缓存
    private static Map<String, String> sentenceTypeAndSplitStrategyMap = null;
   
	//c++回调函数返回结果
	private String textFormaterResult;
	
	@Autowired
	private ApposedSentenceProcessStrategy apposedSentenceProcessStrategy;
	
	Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * @return the textFormaterResult
	 */
	public String getTextFormaterResult() {
		return textFormaterResult;
	}

	/**
	 * @param textFormaterResult the textFormaterResult to set
	 */
	public void setTextFormaterResult(String textFormaterResult) {
		this.textFormaterResult = textFormaterResult;
	}

	/* (non-Javadoc)  
	 * <p>Title: getSentenceSplited</p>  
	 * <p>Description: 双意图分句接口方法实现,双意图分句业务层总入口</p>  
	 * @param requestQuestion
	 * @return  
	 * @see com.yuzhi.doubleIntention.service.SentenceSplitByHownet#getSentenceSplited(java.lang.String)  
	 */
	@Override
	public ResponseJsonDto doubleIntentionProcess(String questionID,String requestQuestion) {
		ResponseJsonDto responseJsonDto=null;
		if("".equals(requestQuestion)) {
			return new ResponseJsonDto(questionID,requestQuestion,505,null,"请求问句为空！");
			
		}
		
		//调用预处理
		List<String> preprocessResultStringList = this.originalTextProprocess(requestQuestion);
		//判断是否为单句（潜在并列词、并列词句型）
		if(preprocessResultStringList.isEmpty()) {//如果处理完之后为空，直接返回
			return new ResponseJsonDto(questionID,requestQuestion,505,null,"预处理后为空！");
		}
		if(preprocessResultStringList.size()==1) {
			//如果只有一句
			//那将其作为并列词、词组处理
			responseJsonDto = this.apposedWordsSplit(this.splitSentencePreprocess(preprocessResultStringList).get(0));
			responseJsonDto.setQuestionID(questionID);
			responseJsonDto.setOrignalQuestion(requestQuestion);
			return	responseJsonDto;
		}else if(preprocessResultStringList.size()==2) {
			//否则作为并列句处理
			responseJsonDto = this.apposedSentenceSplit(this.splitSentencePreprocess(preprocessResultStringList));
			responseJsonDto.setQuestionID(questionID);
			responseJsonDto.setOrignalQuestion(requestQuestion);
			return	responseJsonDto;
		}else {
			return new ResponseJsonDto(questionID,requestQuestion,505,null,"非双句并列或单句，暂不处理！");
		}
			
	}

	/**  
	 * <p>Title: apposedSentenceSplit</p>  
	 * <p>Description: 一般并列句分句处理（两句）</p>  
	 * @param splitSentencePreprocess
	 * @return  
	 */
	private ResponseJsonDto apposedSentenceSplit(List<List<KeenageDataDTO>> splitSentencePreprocess) {
		return this.getSentenceStrategy(splitSentencePreprocess);
	}
	
	/**
	 * 
	 * <p>Title: getScndSentenceTypeAndString</p>  
	 * <p>Description: 获取第二句的句型及句子核心</p>  
	 * @param scndSentence
	 * @return
	 */
	private ResponseJsonDto getSentenceStrategy(List<List<KeenageDataDTO>> splitSentencePreprocess) {
		//获取到第一句和第二句的最有一个分词节点
		KeenageDataDTO lastKeenageDataInFrstSentence = splitSentencePreprocess.get(0).get(splitSentencePreprocess.get(0).size()-1);
		KeenageDataDTO lastKeenageDataInScndSentence = splitSentencePreprocess.get(1).get(splitSentencePreprocess.get(1).size()-1);
		//获取两句最后一个分词节点的key_1列，判断是否是并列词句型
		boolean frstIsApposedWordsOrNot=false;
		boolean scndIsApposedWordsOrNot=false;
		//先进行句内双意图判断
		if(lastKeenageDataInFrstSentence.getKey_1()!=null&&lastKeenageDataInFrstSentence.getKey_1().contains("结构_句内双意图")) {
			frstIsApposedWordsOrNot=true;
		}
		if(lastKeenageDataInScndSentence.getKey_1()!=null&&lastKeenageDataInScndSentence.getKey_1().contains("结构_句内双意图")) {
				scndIsApposedWordsOrNot=true;
		}
		//双意图拒识的判断（这里判断的是并列句的拒识）
		if (lastKeenageDataInScndSentence.getKey_1().contains("标记_跨句双意图拒识")) {
			//如果并列句拒识，那么要各自判断是否为句内双意图
			if(frstIsApposedWordsOrNot&&!scndIsApposedWordsOrNot) {//第一句有句内双意图
				return this.apposedWordsSplit(splitSentencePreprocess.get(0));
			}else {//第二句有句内双意图，其他情况（两者都是句内双意图，或都不是）均不考虑
				return new ResponseJsonDto(505,null,"并列句：标记_跨句双意图拒识，暂不处理！");
			}
		}
		
		//分别获取第一句和第二句的类型
		String frstSentenceType = lastKeenageDataInFrstSentence.getKey_IR();
		String scndSentenceType = lastKeenageDataInScndSentence.getKey_IR();
		if (frstSentenceType==null||scndSentenceType==null||!frstSentenceType.contains("疑问_")||!scndSentenceType.contains("疑问_")) {
			return new ResponseJsonDto(505,null,"并列句：key_IR列没有句型标签，暂不处理！");
		}
		if((frstSentenceType.startsWith("疑问_")&&frstSentenceType.split("疑问_").length>=2)||frstSentenceType.split("疑问_").length>=3) {
			return new ResponseJsonDto(505,null,"并列句：第一句key_IR列句型标签不止一个，暂不处理！");
		}
		if((scndSentenceType.startsWith("疑问_")&&scndSentenceType.split("疑问_").length>=2)||scndSentenceType.split("疑问_").length>=3) {
			return new ResponseJsonDto(505,null,"并列句：第二句key_IR列句型标签不止一个，暂不处理！");
		}
		if(sentenceTypeAndSplitStrategyMap==null) {
			return new ResponseJsonDto(505,null,"并列句：映射表没有加载成功！");
		}
		frstSentenceType=frstSentenceType.substring(frstSentenceType.indexOf("疑问_"));
		scndSentenceType=scndSentenceType.substring(scndSentenceType.indexOf("疑问_"));
		if (frstSentenceType.contains(",")) {
			frstSentenceType=frstSentenceType.substring(0,frstSentenceType.indexOf(","));
		}
		if (frstSentenceType.contains(",")) {
			frstSentenceType=frstSentenceType.substring(0,frstSentenceType.indexOf(","));
		}
		String sentenceStrategy = sentenceTypeAndSplitStrategyMap.get(scndSentenceType+frstSentenceType);
		if (sentenceStrategy==null||"".equals(sentenceStrategy)) {
			return new ResponseJsonDto(505,null,"并列句：没有在映射表里面找到对应策略！");
		}
		 ResponseJsonDto processStartegySwitch = apposedSentenceProcessStrategy.processStartegySwitch(sentenceStrategy, frstSentenceType, scndSentenceType, splitSentencePreprocess);
		 List<String> processResultList =processStartegySwitch.getData();
		if (processResultList==null) {//如果返回的结果为null直接返回
			return processStartegySwitch;
		}
		
		
		if(scndIsApposedWordsOrNot) {//只要第二句是句内双意图，直接拒识
			return new ResponseJsonDto(505,null,"并列句：原句中第二句存在句内双意图，暂不处理！");
		}else if (frstIsApposedWordsOrNot) {
			List<String> apposedWordsSplitFrst = this.apposedWordsSplit(this.splitSentencePreprocess(this.originalTextProprocess(processResultList.get(0))).get(0)).getData();
			List<String> apposedWordsSplitScnd = this.apposedWordsSplit(this.splitSentencePreprocess(this.originalTextProprocess(processResultList.get(1))).get(0)).getData();
			if (apposedWordsSplitFrst!=null) {
				processResultList.clear();
				processResultList.addAll(apposedWordsSplitFrst);
				if (apposedWordsSplitScnd!=null) {
					processResultList.addAll(apposedWordsSplitScnd);
				}else {
					System.out.println("并列句处理后的第二句并不是句内双意图！");
				}
			}else {
				System.out.println("第一句被标记为句内双意图，但是分析完之后为空！");
			}
		}
		return new ResponseJsonDto(200,processResultList,"success:并列句（或句内双意图）");
	}
	
	
	
	
	/**
	    * 
	 * <p>Title: splitSentencePreprocess</p>  
	 * <p>Description: 解析器解析及转换对象方法</p>  
	 * @param preprocessResultString
	 * @return
	 */
	private List<List<KeenageDataDTO>> splitSentencePreprocess(List<String> preprocessResultString){
		//调用解析器进行解析
		String parserResult=this.getParserResult(preprocessResultString);
		//解析结果提取并转接点对象
		return this.parserStringToDtoList(parserResult);
	}


	/**  
	 * <p>Title: apposedWordsSplit</p>  
	 * <p>Description: 并列词、词组句型的句子拆分</p>  
	 * @param parserStringToDtoList
	 * @return  
	 */
	private ResponseJsonDto apposedWordsSplit(List<KeenageDataDTO> parserStringToDtoList) {
		String key_1Value="";//key_1列变量
		String expressionWords="";//当前分词变量
		int nonlableWordCount=0;//非标签词计数，作为后续标签词插入位置索引
		List<String> normalWords=new LinkedList<String>();//非标签 词序列
		List<Integer> conjMarkIndex=new ArrayList<Integer>();//并列标记索引列表
		List<Integer> labelIndex=new ArrayList<Integer>();//标签词索引列表
		Map<String, WordLocationAndExpressionDTO> keyWordsMap=new LinkedHashMap<String,WordLocationAndExpressionDTO>();//label词及其插入位置索引
		List<String> labelExpression=null;//临时变量，引用map中已有的label词部分，当label是个词组的时候，便于追加
		WordLocationAndExpressionDTO labelAndExpressionDTO=null;//临时变量，引用label词map中已经存在的对象
		//遍历已经转换好的解析器结果，获取每个节点（node）内容
		for (KeenageDataDTO keenageDataDTO : parserStringToDtoList) {
			//获取key_1列的内容
			key_1Value=keenageDataDTO.getKey_1();
			//获取当前节点分词结果
			expressionWords=keenageDataDTO.getExpression();
			if(key_1Value.contains("XZ_conj_mark")) {
				conjMarkIndex.add(nonlableWordCount);
				continue;
			}else if (key_1Value.contains("XZ_Label")) {
				//根据逗号分割，以防key_1一列打了其他标签
				String[] splitKey_1s=key_1Value.split(",");
				for (String splitKey_1 : splitKey_1s) {
					if(splitKey_1.startsWith("XZ_Label")) {
						if(keyWordsMap.containsKey(splitKey_1)) {
							//如果label词map中已经有了当前标签，那么先取出词
							labelAndExpressionDTO=keyWordsMap.get(splitKey_1);
							//得到已有的ArrayList
							labelExpression=labelAndExpressionDTO.getWordExpression();
							//然后追加
							labelExpression.add(expressionWords);
							//然后再放回去
							labelAndExpressionDTO.setWordExpression(labelExpression);
							keyWordsMap.put(splitKey_1, labelAndExpressionDTO);
						}else {
							//新建一个ArrayList
							labelExpression=new ArrayList<String>();
							labelExpression.add(expressionWords);
							//否则直接放入即可
							keyWordsMap.put(splitKey_1, new WordLocationAndExpressionDTO(nonlableWordCount,labelExpression));
							labelIndex.add(nonlableWordCount);
						}
					}				
				}
			}else {//key_1的值不为空，但不是上述两种词，则列为正常词汇
				//将费标签词存入序列
				normalWords.add(expressionWords);
				//非标签词自增1
				nonlableWordCount++;
			}
		}
		
		if(keyWordsMap.isEmpty()) {//如果为空直接返回
			return new ResponseJsonDto(505,null,"并列词、词组：没有找到句中的Label标签！");
		}else {
			if(conjMarkIndex.size()==2&&labelIndex.size()==4&&conjMarkIndex.get(0)==labelIndex.get(0)&&conjMarkIndex.get(0)==labelIndex.get(1)&&conjMarkIndex.get(1)==labelIndex.get(2)&&conjMarkIndex.get(1)==labelIndex.get(3)) {
				log.info("并列词、词组：出现2乘2");
				return this.apposedWordsMuti(keyWordsMap,normalWords);
			}else if(conjMarkIndex.size()==(labelIndex.size()-1)) {
				//对并列词、词组句型进行细分并根据类型重组返回
				return this.apposedWordsSentenceTypeDistinguish(keyWordsMap,normalWords);
			}else {
				return new ResponseJsonDto(505,null,"并列词、词组：该句既不是2乘2特例，也不是普通并列词、词组");
			}
		}
	}



	/**  
	 * <p>Title: apposedWordsMuti</p>  
	 * <p>Description: 并列词2乘2特例</p>  
	 * @param keyWordsMap
	 * @param normalWords
	 * @return  
	 */
	private ResponseJsonDto apposedWordsMuti(Map<String, WordLocationAndExpressionDTO> keyWordsMap,
			List<String> normalWords) {
		List<WordLocationAndExpressionDTO> wordLocationAndExpressionList=new ArrayList<WordLocationAndExpressionDTO>();
		boolean isSingleWordOrNot=true;
		for (String key : keyWordsMap.keySet()) {
			if (keyWordsMap.get(key).getWordExpression().size()>1) {
				isSingleWordOrNot=false;
			}
			//获取到map中所有关键词位置及内容的对象
			wordLocationAndExpressionList.add(keyWordsMap.get(key));
		}
		if (!isSingleWordOrNot) {//如果不是单词
			if (wordLocationAndExpressionList.get(0).getWordExpression().size()>1&&wordLocationAndExpressionList.get(1).getWordExpression().size()>1&&wordLocationAndExpressionList.get(2).getWordExpression().size()>1&&wordLocationAndExpressionList.get(3).getWordExpression().size()>1) {
			//前后都是词组
				//前两词的共同成分判断
				boolean containsSameWordOrNot1=this.containsSameWordOrNot(wordLocationAndExpressionList.get(0),wordLocationAndExpressionList.get(1));
				//后两词的共同成分判断
				boolean containsSameWordOrNot2=this.containsSameWordOrNot(wordLocationAndExpressionList.get(2),wordLocationAndExpressionList.get(3));
				if (!containsSameWordOrNot1||!containsSameWordOrNot2) {//如果两个中任何一个没有共同成分，直接拒识
					return new ResponseJsonDto(505,null,"并列词、词组_2乘2：该句中都是词组，但是词组没有共同词，暂不处理！");
				}
			}else if (wordLocationAndExpressionList.get(0).getWordExpression().size()>1&&wordLocationAndExpressionList.get(1).getWordExpression().size()>1) {
				//前两词的共同成分判断
				boolean containsSameWordOrNot1=this.containsSameWordOrNot(wordLocationAndExpressionList.get(0), wordLocationAndExpressionList.get(1));
				if (!containsSameWordOrNot1) {
					return new ResponseJsonDto(505,null,"并列词、词组_2乘2：该句中前半部分是词组，但是词组没有共同词，暂不处理！");
				}
			}else if (wordLocationAndExpressionList.get(2).getWordExpression().size()>1&&wordLocationAndExpressionList.get(3).getWordExpression().size()>1) {
				boolean containsSameWordOrNot2=this.containsSameWordOrNot(wordLocationAndExpressionList.get(2),wordLocationAndExpressionList.get(3));
				if (!containsSameWordOrNot2) {
					return new ResponseJsonDto(505,null,"并列词、词组_2乘2：该句中后半部分是词组，但是词组没有共同词，暂不处理！");
				}
			}else {
				return new ResponseJsonDto(505,null,"并列词、词组_2乘2：该句中既有单词又有词组，暂不处理！");
			}
		}//这里不需要else，因为处理策略都一样，只是词组需要过滤
		return this.apposedWordsCombingMuti(wordLocationAndExpressionList,normalWords);
	}	

	/**  
	 * <p>Title: apposedWordsCombingMuti</p>  
	 * <p>Description: 2*2句子拼接并返回</p>  
	 * @param wordLocationAndExpressionList
	 * @param normalWords
	 * @return  
	 */
	private ResponseJsonDto apposedWordsCombingMuti(List<WordLocationAndExpressionDTO> wordLocationAndExpressionList,
			List<String> normalWords) {
		List<String> returnSentenceResultList=new ArrayList<String>();
		returnSentenceResultList.add(this.apposedWordsMutiGetSentence(wordLocationAndExpressionList.get(0),wordLocationAndExpressionList.get(2),normalWords));
		returnSentenceResultList.add(this.apposedWordsMutiGetSentence(wordLocationAndExpressionList.get(0),wordLocationAndExpressionList.get(3),normalWords));
		returnSentenceResultList.add(this.apposedWordsMutiGetSentence(wordLocationAndExpressionList.get(1),wordLocationAndExpressionList.get(2),normalWords));
		returnSentenceResultList.add(this.apposedWordsMutiGetSentence(wordLocationAndExpressionList.get(1),wordLocationAndExpressionList.get(3),normalWords));
		
		return new ResponseJsonDto(200,returnSentenceResultList,"success:并列词、词组_2乘2");
	}

	/**  
	 * <p>Title: apposedWordsMutiGetSentence</p>  
	 * <p>Description: 2*2句子拼接</p>  
	 * @param wordLocationAndExpressionDTO
	 * @param wordLocationAndExpressionDTO2
	 * @param normalWords 
	 * @return  
	 */
	private String apposedWordsMutiGetSentence(WordLocationAndExpressionDTO wordLocationAndExpressionDTO1,
			WordLocationAndExpressionDTO wordLocationAndExpressionDTO2, List<String> normalWords) {
		StringBuffer labelWord1=new StringBuffer();
		StringBuffer labelWord2=new StringBuffer();
		List<String> orignalNormaWords=new LinkedList<String>();
		//先将句子的其他成分排列好
		for (String string : normalWords) {
			orignalNormaWords.add(string);
		}
		//将label词拼接
		for (String labelWordString : wordLocationAndExpressionDTO1.getWordExpression()) {
			labelWord1.append(labelWordString);
		}
		for (String labelWordString : wordLocationAndExpressionDTO2.getWordExpression()) {
			labelWord2.append(labelWordString);
		}
		//将label词分别插入
		orignalNormaWords.add(wordLocationAndExpressionDTO1.getWordLocation(),labelWord1.toString());
		orignalNormaWords.add(wordLocationAndExpressionDTO2.getWordLocation()+1,labelWord2.toString());
		//创建组句对象
		StringBuffer sentenceCombined=new StringBuffer();
		for (String orignalNormaWord : orignalNormaWords) {
			sentenceCombined.append(orignalNormaWord);
		}
		return sentenceCombined.toString();
	}

	/**  
	 * <p>Title: containsSameWordOrNot</p>  
	 * <p>Description:2*2特例判断词组是否有共同成分 </p>  
	 * @param wordLocationAndExpressionDTO
	 * @param wordLocationAndExpressionDTO2
	 * @return  
	 */
	private boolean containsSameWordOrNot(WordLocationAndExpressionDTO wordLocationAndExpressionDTO,
			WordLocationAndExpressionDTO wordLocationAndExpressionDTO2) {
		for (String labelOne : wordLocationAndExpressionDTO.getWordExpression()) {
			for (String labelTwo : wordLocationAndExpressionDTO2.getWordExpression()) {
				if (labelOne.equals(labelTwo)) {
					return true;
				}
			}
			
		}
		return false;
	}

	/**  
	 * <p>Title: sentenceCombining</p>  
	 * <p>Description: 确定并列词、词组句子是否都是词组、或者单词、还是有单词、词组混合。</p>  
	 * @param keyWordsMap
	 * @param normalWords
	 * @return  
	 */
	private ResponseJsonDto apposedWordsSentenceTypeDistinguish(Map<String, WordLocationAndExpressionDTO> keyWordsMap,
			List<String> normalWords) {
		/**
		 * 先确定是否都是词组、或者单词、还是有单词、词组混合
		 */
		//创建一个有序的word标签词列表
		List<List<String>> wordExpressionList =new ArrayList<List<String>>();
		String commonLabelString=null;
		for (String key : keyWordsMap.keySet()) {
			//获取map内label词对象中的词
			List<String> wordExpression = keyWordsMap.get(key).getWordExpression();
			if(wordExpressionList.size()>0) {//如果word标签词列表中已经有对象，那就拿当前的，跟之前的进行比较
				//获取到wordExpressionList最后一个对象
				List<String> lastWordExpression = wordExpressionList.get(wordExpressionList.size()-1);
				if(wordExpression.size()>1&&lastWordExpression.size()>1) {//两个都是词组
					/**
					 * 判断词组是否有共同标签模块儿
					 */
					//判断commonLabelString是否不为空
					if(commonLabelString!=null) {//不为空
						//直接判断
						for (int j = 0; j < wordExpression.size(); j++) {
							if(commonLabelString.equals(wordExpression.get(j))){
								//然后跳出循环
								break;
							}else if(j < wordExpression.size()-1) {
								continue;
							}else {//没有共同标签词,并且已经遍历完毕,直接拒识
								return new ResponseJsonDto(505,null,"并列词、词组：该句中都是词组，数量超过两个，但是词组没有共同词！");
							}
						}//for循环结束
						
					}else {//为空
						//寻找他们的共同label词
						String string1 =null;
						String string2 =null;
						flag:for (int j = 0; j < wordExpression.size(); j++) {
							string1 = wordExpression.get(j);
							for (int k = 0; k < lastWordExpression.size(); k++) {
								string2 = lastWordExpression.get(k);
								if(string1.equals(string2)) {//有共同label标签词
									//先存入标签词
									commonLabelString=string1;
									//然后跳出双层循环
									break flag;
								}else if(j<wordExpression.size()-1||k < lastWordExpression.size()-1) {//没有共同标签，但是遍历还没有结束
									continue;
								}else{//没有共同标签词,并且已经遍历完毕,直接拒识
									return new ResponseJsonDto(505,null,"并列词、词组：该句中都是词组，但是词组没有共同词！");
								}
							}//内层for循环结束
						}//外层for循环结束
					}//判断词组是否有共同标签模块儿结束
					
				}else if(wordExpression.size()==1&&wordExpressionList.get(wordExpressionList.size()-1).size()==1) {//都是单词
					/*此种类型无需任何处理*/
				}else {//既有词组、也有单词，直接拒识
					return new ResponseJsonDto(505,null,"并列词、词组：该句中既有单词、又有词组,暂不处理！");
				}
			}//wordExpressionList中暂时还没有对象
			//存入label词列表中
			wordExpressionList.add(wordExpression);
		}//label词map循环结束
		
		//执行到这一步，只能有两种情况：全是词组或全是单词，那么接下来进行句子重组并返回
		return this.apposedWordsSentenceCombing(keyWordsMap,normalWords);
		
	}
	
	/**
	 * 
	 * <p>Title: apposedWordsSentenceCombing</p>  
	 * <p>Description: 并列词、词组句子重组。根据拆分序列，将句子重组</p>  
	 * @param keyWordsMap
	 * @param normalWords
	 * @return
	 */
	private ResponseJsonDto apposedWordsSentenceCombing(Map<String, WordLocationAndExpressionDTO> keyWordsMap,List<String> normalWords) {

		StringBuffer labelWord=null;//当前label词
		int wordLocation=-1;//当前label词的插入位置
		int lastWordLocation=-1;//上一个label词的插入位置
		List<String> returnSentenceResult=new ArrayList<String>();//重组好的句子集合，用于返回结果
		List<String> originalNormalWords=null;//句子其他成分词列表
		StringBuffer sentenceCombined=null;//组句变量
		/**
		 * 遍历取值
		 */
		for (String key : keyWordsMap.keySet()) {
			originalNormalWords=new LinkedList<String>();//为避免变量被修改，需要重新赋值
			//先将句子的其他成分排列好
			for (String string : normalWords) {
				originalNormalWords.add(string);
			}
			//获取map内label词对象中的词
			List<String> wordExpression = keyWordsMap.get(key).getWordExpression();
			labelWord=new StringBuffer();
			for (String labelWordString : wordExpression) {
				labelWord.append(labelWordString);
			}
			//获取label词的插入位置
			wordLocation=keyWordsMap.get(key).getWordLocation();
			//判断是否为第一个label词
			if (lastWordLocation!=-1) {
				//如果不是第一个，判断是否跟上一个的插入位置一样
				if (wordLocation!=lastWordLocation) {
					//如果不一样，另外处理（处理策略待定）
					return new ResponseJsonDto(505,null,"并列词、词组：当前词插入位置和上一个不一样");
				}
			}
			//如果是第一个词，或者第二个词位置和第一个词一样，那就按位置插入即可
			originalNormalWords.add(wordLocation,labelWord.toString());
			//创建组句对象
			sentenceCombined=new StringBuffer();
			for (String originalNormalWord : originalNormalWords) {
				sentenceCombined.append(originalNormalWord);
			}
			//存入返回结果
			returnSentenceResult.add(sentenceCombined.toString());
		}
		return new ResponseJsonDto(200,returnSentenceResult,"success:并列词、词组");
	}


	/**  
	 * <p>Title: parserStringToDtoList</p>  
	 * <p>Description: 将解析器字符串结果转换成java对象</p>  
	 * @param parserResult
	 * @return  
	 */
	private List<List<KeenageDataDTO>> parserStringToDtoList(String parserResult) {
		//判断解析结果是否为空
		if (parserResult==null||"".equals(parserResult)) {
			return null;
		}
		
		AnalyzeResultDTO parseObject=null;
		try {
			//将json字符串转换成对象
			parseObject=JSON.parseObject(parserResult,AnalyzeResultDTO.class);
		} catch (JSONException e) {
			System.out.println("parser返回的json字符串有问题，转换对象失败");
		}
		//获取解析对象中的每个句子（目前parser只支持单句解析）
		List<SentenceDTO> sentenceDTOList=parseObject.getSentence();
		//创建一个节点对象列表
		List<List<KeenageDataDTO>> keenageDataDTOList=new ArrayList<List<KeenageDataDTO>>();
		//要遍历一下
		for (SentenceDTO sentenceDTO : sentenceDTOList) {
			//存入节点对象列表
			keenageDataDTOList.add(sentenceDTO.getNodes());
		}
		//返回节点对象列表
		return keenageDataDTOList;
	}



	/**  
	 * <p>Title: getParserResult</p>  
	 * <p>Description: </p>  
	 * @param preprocessResultString
	 * @return  
	 */
	private synchronized String getParserResult(String preprocessResultString) {
		//解析文本
		boolean parseText=chineseParserHelper.parseText(preprocessResultString);
		//如果解析成功，获取解析结果
		if (parseText) {
			return chineseParserHelper.getJsonRet();
		} else {
			System.out.println("解析器解析失败");
			return null;
		}
	}
	/**  
	 * <p>Title: getParserResult</p>  
	 * <p>Description: </p>  
	 * @param preprocessResultString
	 * @return  
	 */
	private synchronized String getParserResult(List<String> preprocessResultStringList) {
		StringBuffer preprocessResultString=new StringBuffer();
		for (String string : preprocessResultStringList) {
			preprocessResultString.append(string);
		}
		//解析文本
		boolean parseText=chineseParserHelper.parseText(preprocessResultString.toString());
		//如果解析成功，获取解析结果
		if (parseText) {
			return chineseParserHelper.getJsonRet();
		} else {
			System.out.println("解析器解析失败");
			return null;
		}
	}



	/**  
	 * <p>Title: proprocessText</p>  
	 * <p>Description:字符串预处理，替换掉可能影响返回结果的字符串 </p>  
	 * @param requestQuestion
	 * @return  
	 */
	private List<String> originalTextProprocess(String sentence) {
		
		//导致返回结果不正确的字符过滤
//		sentence=sentence.replace("\"", "“");
//		sentence=sentence.replace("\'", "’");
//		sentence=sentence.replace("\\r", "\r");
//		sentence=sentence.replace("\\n", "\n");
//		sentence=sentence.replace("\\u201C", "“");
//		sentence=sentence.replace("\\u201D", "”");
//		sentence=sentence.replace("\\", "");
		//经过hownet过滤
		textPreprocessor.format_callback(sentence, 2, this, 0);
		sentence=this.getTextFormaterResult();
//		System.out.println("回调完成后："+sentence);
		//以下处理非法使用标点符号，如“这个怎么操作？？？？急急急！！！”
//		sentence=sentence.replaceAll("[ ]{0,1}[？。！；]+", "？");
//		sentence=sentence.replaceAll("[？。！；]{2,}", "？");
//		sentence=sentence.replace("?", "?f_stp");
		sentence=sentence.replace("？", "？f_stp");
//		sentence=sentence.replace(".", ".f_stp");
		sentence=sentence.replace("。", "。f_stp");
		sentence=sentence.replace("！", "！f_stp");
//		sentence=sentence.replace("!", "!f_stp");
//		sentence=sentence.replace(";", ";f_stp");
		sentence=sentence.replace("；", "；f_stp");
		sentence=sentence.replace("\r", "\rf_stp");
		sentence=sentence.replace("\n", "\nf_stp");
		//根据HowNet标点符号去分句，剔除为空，或者没有实际意义的句子
		String[] splitByFullstop = sentence.split("f_stp");
		//创建返回结果的列表
		List<String> resultString=new LinkedList<String>();
		for (String string2 : splitByFullstop) {
			if(string2.trim().length()<2) {//如果分完的句子有空的，直接略过
				continue;
			}else{//如果正常，则将句子拼接并返回
				resultString.add(string2);
			}
		}
		//返回处理结果
		return resultString;
	}



	/* (non-Javadoc)  
	 * <p>Title: run</p>  
	 * <p>Description: 动态库预加载，在项目启动前加载完毕</p>  
	 * @param args
	 * @throws Exception  
	 * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])  
	 */
	@Override
	public void run(String... args) throws Exception {
		if(hownetPath==null||"".equals(hownetPath)) {
			System.out.println("动态库路径为空");
			return;
		}
		
		//加载解析器
		chineseParserHelper=DllUtil.getChineseParserHelper(hownetPath);
		//加载预处理
		textPreprocessor=DllUtil.getTextPreprocessor(hownetPath);
		//打印加载结果到控制台
		System.out.println("加载解析器Helper>>>>>>>>"+chineseParserHelper);
		System.out.println("加载预处理processor>>>>>"+textPreprocessor);
		//初始化解析器
		boolean initHownet=chineseParserHelper.initHowNet(hownetPath);
		System.out.println("解析器初始化结果>>>>>>>>>>"+initHownet);
		
		sentenceTypeAndSplitStrategyMap=new HashMap<String,String>();
		 //读取并列句句型映射关系表字符流
        try {
            sentenceTypeAndSplitStrategyReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(hownetPath + "SentenceTypeAndSplitStrategy.txt")), "UTF-8"));
            //当前行
            String currentLine = null;
            while ((currentLine = sentenceTypeAndSplitStrategyReader.readLine()) != null) {
                if("".equals(currentLine.trim())) {
                	continue;
                }
                String[] split = currentLine.trim().split("	");
                if(split.length!=3) {
                	System.out.println("当前行格式不对");
                	continue;
                }
                sentenceTypeAndSplitStrategyMap.put(split[0]+split[1], split[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                sentenceTypeAndSplitStrategyReader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
		
	}

	/* (non-Javadoc)  
	 * <p>Title: TextFormater_callback</p>  
	 * <p>Description: </p>  
	 * @param article
	 * @param a  
	 * @see com.yuzhi.doubleIntention.util.DllUtil.TextFormater#TextFormater_callback(java.lang.String, int)  
	 */
	@Override
	public void TextFormater_callback(String article, int a) {
		List<FormaterReusltDto> formaterReusltDto=null;
		try {
			//将json字符串转换成对象
			formaterReusltDto = JSON.parseArray(article,FormaterReusltDto.class);
		} catch (JSONException e) {
			System.out.println("parser返回的json字符串有问题，转换对象失败");
		}
		if(formaterReusltDto!=null&&formaterReusltDto.size()==1) {
			this.setTextFormaterResult(formaterReusltDto.get(0).getClean());
		}
		
	}
	
	
	
	
	
	
}
