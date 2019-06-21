package com.yuzhi.doubleIntention.dto.parser;

import java.util.List;

/**   
 * @Title: Entity
 * @Description: 知网Data
 * @author xiangmeng
 * @date 2016-11-21 12:01:30
 * @version V1.0   
 *
 */
public class SentenceDTO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5580753712702878546L;
	private List<RulesDTO> rules;//所跑规则列表
	private List<KeenageDataDTO> nodes;//分词内容节点
	public List<KeenageDataDTO> getNodes() {
		return nodes;
	}
	public void setNodes(List<KeenageDataDTO> nodes) {
		this.nodes = nodes;
	}
	public List<RulesDTO> getRules() {
		return rules;
	}
	public void setRules(List<RulesDTO> rules) {
		this.rules = rules;
	}
	public SentenceDTO(List<RulesDTO> rules, List<KeenageDataDTO> nodes) {
		super();
		this.rules = rules;
		this.nodes = nodes;
	}
	public SentenceDTO() {
		super();
	}
	@Override
	public String toString() {
		return "Sentence [rules=" + rules + ", nodes=" + nodes + "]";
	}
	
	
	
	
	
	
}
