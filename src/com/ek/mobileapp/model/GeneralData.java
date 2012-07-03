package com.ek.mobileapp.model;

import org.json.JSONException;
import org.json.JSONObject;

public class GeneralData {
	
	/*public GeneralData(JSONObject res) {
		try {
			FSubClass = res.getInt("FSubClass");
			FSubName = res.getString("FSubName");
			FDeptName = res.getString("FDeptName");
			fQtyLabel1 = res.getString("fQtyLabel1");
			fQty1 = res.getString("fQty1");
			fQtyLabel2 = res.getString("fQtyLabel2");
			fQty2 = res.getString("fQty2");
			fQtyLabel3 = res.getString("fQtyLabel3");
			fQty3 = res.getString("fQty3");
			fQtyLabel4 = res.getString("fQtyLabel4");
			fQty4 = res.getString("fQty4");
			fQtyLabel5 = res.getString("fQtyLabel5");
			fQty5 = res.getString("fQty5");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	private int FSubClass;
	private String FSubName;
	private String FDeptName;
	private String fQtyLabel1;
	private float fQty1;
	private String fQtyLabel2;
	private float fQty2;
	private String fQtyLabel3;
	private float fQty3;
	private String fQtyLabel4;
	private float fQty4;
	private String fQtyLabel5;
	private float fQty5;
	public void setFSubClass(int fSubClass) {
		FSubClass = fSubClass;
	}
	public int getFSubClass() {
		return FSubClass;
	}
	public void setFSubName(String fSubName) {
		FSubName = fSubName;
	}
	public String getFSubName() {
		return FSubName;
	}
	public void setFDeptName(String fDeptName) {
		FDeptName = fDeptName;
	}
	public String getFDeptName() {
		return FDeptName;
	}
	public void setfQtyLabel1(String fQtyLabel1) {
		this.fQtyLabel1 = fQtyLabel1;
	}
	public String getfQtyLabel1() {
		return fQtyLabel1;
	}
	public void setfQty1(float fQty1) {
		this.fQty1 = fQty1;
	}
	public float getfQty1() {
		return fQty1;
	}
	public void setfQtyLabel2(String fQtyLabel2) {
		this.fQtyLabel2 = fQtyLabel2;
	}
	public String getfQtyLabel2() {
		return fQtyLabel2;
	}
	public void setfQty2(float fQty2) {
		this.fQty2 = fQty2;
	}
	public float getfQty2() {
		return fQty2;
	}
	public void setfQtyLabel3(String fQtyLabel3) {
		this.fQtyLabel3 = fQtyLabel3;
	}
	public String getfQtyLabel3() {
		return fQtyLabel3;
	}
	public void setfQty3(float fQty3) {
		this.fQty3 = fQty3;
	}
	public float getfQty3() {
		return fQty3;
	}
	public void setfQtyLabel4(String fQtyLabel4) {
		this.fQtyLabel4 = fQtyLabel4;
	}
	public String getfQtyLabel4() {
		return fQtyLabel4;
	}
	public void setfQty4(float fQty4) {
		this.fQty4 = fQty4;
	}
	public float getfQty4() {
		return fQty4;
	}
	public void setfQtyLabel5(String fQtyLabel5) {
		this.fQtyLabel5 = fQtyLabel5;
	}
	public String getfQtyLabel5() {
		return fQtyLabel5;
	}
	public void setfQty5(float fQty5) {
		this.fQty5 = fQty5;
	}
	public float getfQty5() {
		return fQty5;
	}
}