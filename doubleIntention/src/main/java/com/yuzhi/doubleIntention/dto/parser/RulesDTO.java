package com.yuzhi.doubleIntention.dto.parser;
/**
 * 
 * @author Administrator
 *
 */
public class RulesDTO {
	private String CN;//分词ID
	private String rule;//分词当前走的规则

	public RulesDTO(String cN, String rule) {
		super();
		CN = cN;
		this.rule = rule;
	}

	public String getCN() {
		return CN;
	}

	public void setCN(String cN) {
		CN = cN;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public RulesDTO() {
		super();
	}

	@Override
	public String toString() {
		return "RulesEntity [CN=" + CN + ", rule=" + rule + "]";
	}
	
	
}
