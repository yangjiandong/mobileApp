package com.ek.mobileapp.utils;

import java.util.ArrayList;
import java.util.List;

import com.ek.mobileapp.model.DrugCheckData;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.SkinTest;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.model.VitalSignItem;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.model.MeasureType;
import com.ek.mobileapp.model.TimePoint;

public class GlobalCache {
    private static GlobalCache cache = null;
    private UserDTO loginuser;
    private String lastIp;//客户端ip
    private String hostIp;//服务端ip
    private String startDate = "";
    private String endDate = "";
    private int screenWidth;
    private int screenHeight;

    private String deviceId;

    private String busDate = "";
    private String timePoint = "";

    private List<Patient> patients = new ArrayList<Patient>();
    private List<VitalSignItem> vitalSignItems = new ArrayList<VitalSignItem>();
    private List<VitalSignData> vitalSignDatas_all = new ArrayList<VitalSignData>();
    private List<VitalSignData> vitalSignDatas = new ArrayList<VitalSignData>();
    private List<MeasureType> measureTypes = new ArrayList<MeasureType>();
    private List<TimePoint> timePoints = new ArrayList<TimePoint>();
    private List<SkinTest> skinTests = new ArrayList<SkinTest>();

    private Patient currentPatient;

    private VitalSignData vitalSignData;
    private VitalSignData vitalSignData2;

    private List<DrugCheckData> drugCheckDatas = new ArrayList<DrugCheckData>();

    private GlobalCache() {
    }

    public static GlobalCache getCache() {
        if (cache == null)
            cache = new GlobalCache();
        return cache;
    }

    public UserDTO getLoginuser() {
        return loginuser;
    }

    public void setLoginuser(UserDTO loginuser) {
        this.loginuser = loginuser;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public Patient getCurrentPatient() {
        return currentPatient;
    }

    public void setCurrentPatient(Patient currentPatient) {
        this.currentPatient = currentPatient;
    }

    public List<VitalSignItem> getVitalSignItems() {
        return vitalSignItems;
    }

    public void setVitalSignItems(List<VitalSignItem> vitalSignItems) {
        this.vitalSignItems = vitalSignItems;
    }

    public List<VitalSignData> getVitalSignDatas() {
        return vitalSignDatas;
    }

    public void setVitalSignDatas(List<VitalSignData> vitalSignDatas) {
        this.vitalSignDatas = vitalSignDatas;
    }

    public List<MeasureType> getMeasureTypes() {
        return measureTypes;
    }

    public void setMeasureTypes(List<MeasureType> measureTypes) {
        this.measureTypes = measureTypes;
    }

    public List<TimePoint> getTimePoints() {
        return timePoints;
    }

    public void setTimePoints(List<TimePoint> timePoints) {
        this.timePoints = timePoints;
    }

    public VitalSignData getVitalSignData() {
        return vitalSignData;
    }

    public void setVitalSignData(VitalSignData vitalSignData) {
        this.vitalSignData = vitalSignData;
    }

    public String getBusDate() {
        return busDate;
    }

    public void setBusDate(String busDate) {
        this.busDate = busDate;
    }

    public String getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    public List<VitalSignData> getVitalSignDatas_all() {
        return vitalSignDatas_all;
    }

    public void setVitalSignDatas_all(List<VitalSignData> vitalSignDatas_all) {
        this.vitalSignDatas_all = vitalSignDatas_all;
    }

    public List<SkinTest> getSkinTests() {
        return skinTests;
    }

    public void setSkinTests(List<SkinTest> skinTests) {
        this.skinTests = skinTests;
    }

    public VitalSignData getVitalSignData2() {
        return vitalSignData2;
    }

    public void setVitalSignData2(VitalSignData vitalSignData2) {
        this.vitalSignData2 = vitalSignData2;
    }

    public List<DrugCheckData> getDrugCheckDatas() {
        return drugCheckDatas;
    }

    public void setDrugCheckDatas(List<DrugCheckData> drugCheckDatas) {
        this.drugCheckDatas = drugCheckDatas;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
