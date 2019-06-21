package com.yuzhi.doubleIntention.dto.parser;

import com.alibaba.fastjson.annotation.JSONField;

/**   
 * @Title: Entity
 * @Description: 知网Data
 * @author xiangmeng
 * @date 2016-11-21 12:01:30
 * @version V1.0   
 *
 */
public class KeenageDataDTO implements java.io.Serializable {
	/**
	 *  "idNo_1": "000",//输入文本经过词法处理后各定形词语在文本中排列的序号
        "MaskID": "",//加工过程中，为了简化句子复杂度暂时屏蔽的标记
        "idNo_2": "000",//输入文本经过命名体识别和语义判定后在文本中排列的序号
        "expression": "ROOT",//各个定形后的词语
        "FH": "",//词语的父节点的序号
        "Son": "",//词语的子节点的序号
        "ES": "",//词语的姐姐节点的序号
        "YS": "",//词语的妹妹节点的序号
        "DP": "",//词语的深层父节点的序号
        "DeepSon": "",//词语的深层子节点的序号
        "log": "",//词语的逻辑语义关系
        "DeepLog": "",//词语的深层逻辑语义关系
        "POS": "",
        "UnitID": ""//词语在知网词典中的义项ID号
	 */
	private static final long serialVersionUID = -7384729186497127711L;
	@JSONField(ordinal = 1)
	private String id;
	@JSONField(ordinal = 2)
	private String expression;
	@JSONField(ordinal = 3)
	private String FH;
	@JSONField(ordinal = 4)
	private String Son;
	@JSONField(ordinal = 5)
	private String ES;
	@JSONField(ordinal = 6)
	private String YS;
	@JSONField(ordinal = 7)
	private String DP;
	@JSONField(ordinal = 8)
	private String DeepSon;
	@JSONField(ordinal = 9)
	private String log;
	@JSONField(ordinal = 10)
	private String DeepLog;
	@JSONField(ordinal = 11)
	private String Key;
	@JSONField(ordinal = 12)
	private String Key_1;
	@JSONField(ordinal = 13)
	private String Key_2;
	@JSONField(ordinal = 14)
	private String Key_IR;
	@JSONField(ordinal = 15)
	private String NER;
	@JSONField(ordinal = 16)
	private String POS;
	@JSONField(ordinal = 17)
	private String UnitID;
	@JSONField(ordinal = 18)
	private String GramInfo;
	@JSONField(ordinal = 19)
	private String TempInfo;
	@JSONField(ordinal = 20)
	private String Aspect;
	@JSONField(ordinal = 21)
	private String pos_begin;
	@JSONField(ordinal = 22)
	private String pos_end;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getFH() {
		return FH;
	}
	public void setFH(String fH) {
		FH = fH;
	}
	public String getSon() {
		return Son;
	}
	public void setSon(String son) {
		Son = son;
	}
	public String getES() {
		return ES;
	}
	public void setES(String eS) {
		ES = eS;
	}
	public String getYS() {
		return YS;
	}
	public void setYS(String yS) {
		YS = yS;
	}
	public String getDP() {
		return DP;
	}
	public void setDP(String dP) {
		DP = dP;
	}
	public String getDeepSon() {
		return DeepSon;
	}
	public void setDeepSon(String deepSon) {
		DeepSon = deepSon;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public String getDeepLog() {
		return DeepLog;
	}
	public void setDeepLog(String deepLog) {
		DeepLog = deepLog;
	}
	public String getKey() {
		return Key;
	}
	public void setKey(String key) {
		Key = key;
	}
	public String getKey_1() {
		return Key_1;
	}
	public void setKey_1(String key_1) {
		Key_1 = key_1;
	}
	public String getKey_2() {
		return Key_2;
	}
	public void setKey_2(String key_2) {
		Key_2 = key_2;
	}
	public String getKey_IR() {
		return Key_IR;
	}
	public void setKey_IR(String key_IR) {
		Key_IR = key_IR;
	}
	public String getNER() {
		return NER;
	}
	public void setNER(String nER) {
		NER = nER;
	}
	public String getPOS() {
		return POS;
	}
	public void setPOS(String pOS) {
		POS = pOS;
	}
	public String getUnitID() {
		return UnitID;
	}
	public void setUnitID(String unitID) {
		UnitID = unitID;
	}
	public String getGramInfo() {
		return GramInfo;
	}
	public void setGramInfo(String gramInfo) {
		GramInfo = gramInfo;
	}
	public String getTempInfo() {
		return TempInfo;
	}
	public void setTempInfo(String tempInfo) {
		TempInfo = tempInfo;
	}
	public String getAspect() {
		return Aspect;
	}
	public void setAspect(String aspect) {
		Aspect = aspect;
	}
	public String getPos_begin() {
		return pos_begin;
	}
	public void setPos_begin(String pos_begin) {
		this.pos_begin = pos_begin;
	}
	public String getPos_end() {
		return pos_end;
	}
	public void setPos_end(String pos_end) {
		this.pos_end = pos_end;
	}
	public KeenageDataDTO() {
		super();
	}
	
	public KeenageDataDTO(String id, String expression, String fH, String son, String eS, String yS, String dP,
			String deepSon, String log, String deepLog, String key, String key_1, String key_2, String key_IR,
			String nER, String pOS, String unitID, String gramInfo, String tempInfo, String aspect, String pos_begin,
			String pos_end) {
		super();
		this.id = id;
		this.expression = expression;
		FH = fH;
		Son = son;
		ES = eS;
		YS = yS;
		DP = dP;
		DeepSon = deepSon;
		this.log = log;
		DeepLog = deepLog;
		Key = key;
		Key_1 = key_1;
		Key_2 = key_2;
		Key_IR = key_IR;
		NER = nER;
		POS = pOS;
		UnitID = unitID;
		GramInfo = gramInfo;
		TempInfo = tempInfo;
		Aspect = aspect;
		this.pos_begin = pos_begin;
		this.pos_end = pos_end;
	}
	@Override
	public String toString() {
		return "KeenageData [id=" + id + ", expression=" + expression + ", FH=" + FH + ", Son=" + Son + ", ES=" + ES
				+ ", YS=" + YS + ", DP=" + DP + ", DeepSon=" + DeepSon + ", log=" + log + ", DeepLog=" + DeepLog
				+ ", Key=" + Key + ", Key_1=" + Key_1 + ", Key_2=" + Key_2 + ", Key_IR=" + Key_IR + ", NER=" + NER
				+ ", POS=" + POS + ", UnitID=" + UnitID + ", GramInfo=" + GramInfo + ", TempInfo=" + TempInfo
				+ ", Aspect=" + Aspect + ", pos_begin=" + pos_begin + ", pos_end=" + pos_end + "]";
	}
	
	
	
	
	
	
}
