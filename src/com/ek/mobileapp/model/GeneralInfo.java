package com.ek.mobileapp.model;

public class GeneralInfo {
	public static final int INTTYPE = 1;
	public static final int FLOATTYPE = 2;
	public static final int CZYQ = 1;
	public static final int YHYQ = 2;

	private int level1;
	private int level2;
	private String level3;
	private String level4;
	private float value;
	
	public GeneralInfo clone() {
		GeneralInfo info = new GeneralInfo();
		info.setLevel1(this.level1);
		info.setLevel2(this.level2);
		info.setLevel3(this.getLevel3());
		info.setLevel4(this.level4);
		info.setValue(this.value);
		return info;
	}
	
	public static String valueToString(float value) {
		if(value - (int)value >0) {
			return ((Float)value).toString();
		}
		else return (int)value + "";
	}
	
	public GeneralInfo(int level1, int level2, String level3,
			String level4, float value, int valueType) {
		this.level1 = level1;
		this.level2 = level2;
		this.level3 = level3;
		this.level4 = level4;
		this.value = value;
	}
	
	public GeneralInfo() {
		
	}
	
	public static String addValue(GeneralInfo info1, GeneralInfo info2) {
		return ((Float)(info1.getValue() + info2.getValue())).toString(); 
	}

	public void setLevel1(int level1) {
		this.level1 = level1;
	}

	public int getLevel1() {
		return level1;
	}

	public void setLevel2(int level2) {
		this.level2 = level2;
	}

	public int getLevel2() {
		return level2;
	}

	public void setLevel3(String level3) {
		this.level3 = level3;
	}

	public String getLevel3() {
		return level3;
	}

	public void setLevel4(String level4) {
		this.level4 = level4;
	}

	public String getLevel4() {
		return level4;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getValue() {
		return value;
	}
}
