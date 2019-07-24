package com.yuzhi.doubleIntention.dto.parser;

import java.util.List;

/**
 * 解析结果实体类
 * @author Administrator
 *
 */
public class AnalyzeResultDTO {
	private List<SentenceDTO> sentence;//分析结果对象

	public List<SentenceDTO> getSentence() {
		return sentence;
	}

	public void setSentence(List<SentenceDTO> sentence) {
		this.sentence = sentence;
	}

	public AnalyzeResultDTO(List<SentenceDTO> sentence) {
		super();
		this.sentence = sentence;
	}

	public AnalyzeResultDTO() {
		super();
	}

	@Override
	public String toString() {
		return "AnalyzeResultEntity [sentence=" + sentence + "]";
	}
	
	
	
	
	
}
