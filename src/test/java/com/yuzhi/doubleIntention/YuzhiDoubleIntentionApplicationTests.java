package com.yuzhi.doubleIntention;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class YuzhiDoubleIntentionApplicationTests {

//	@Test
//	public void contextLoads() {
//		
//		String string="dfs.dfd?。 。 。??? jdl\r\n"
//				+ "fjkslj?.。";
//		System.out.println(string);
//		string=string.replaceAll("[ ]{0,1}[?？.。！!;]+", "？");
//		System.out.println(string);
//		string=string.replaceAll("[?？.。！!;；]+", "？");
//		System.out.println(string);
//		String[] split = string.split("[?？.。！!;；\r\n]");
//		System.out.println(split.length);
//		for (String string2 : split) {
//			if("".equals(string2.trim())) {
//				System.out.println("有空行"+string2);
//			}else{
//				System.out.println("分词结果："+string2);
//			}
//		}
//	}
	
//	@Test
//	public void paramLifeCircle() {
//		SentenceSplitByHownetImpl sentenceSplitByHownetImpl=new SentenceSplitByHownetImpl();
//		List<String> doubleIntentionProcess = sentenceSplitByHownetImpl.doubleIntentionProcess("ATM转账只能转5万？还有手续费是吗？");
//	
//	}
	
//	@Test
//	public void forIfJudge() {
//		List<String> wordExpression=new ArrayList<String>(); 
//		String commonLabelString="123";
//		wordExpression.add("aaa");
//		wordExpression.add("bbb");
//		wordExpression.add("ccc");
//		wordExpression.add("ddd");
//		wordExpression.add("eee");
//		wordExpression.add("fff");
//		wordExpression.add("ggg");
//		wordExpression.add("hhh");
//		wordExpression.add("iii");
//		for (int j = 0; j < wordExpression.size(); j++) {
//			if(commonLabelString.equals(wordExpression.get(j))){
//				//然后跳出循环
//				break;
//			}else if(j < wordExpression.size()-1) {
//				System.out.println("else if:"+wordExpression.get(j));
//				continue;
//			}else {//没有共同标签词,并且已经遍历完毕,直接拒识
//				System.out.println("else:"+wordExpression.get(j));
//				System.out.println("该句中都是词组，数量超过两个，但是词组没有共同词");
//				return ;
//			}
//		}
//	}
	@Test
	public void configureTest() {
	
	}
	
}
