package com.ek.mobileapp.utils;

import java.util.ArrayList;
import java.util.List;

import com.ek.mobileapp.model.ApprovalNote;
import com.ek.mobileapp.model.DrugApprovalData;
import com.ek.mobileapp.model.DrugCheckData;
import com.ek.mobileapp.model.MeasureType;
import com.ek.mobileapp.model.OperationApprovalData;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.SkinTest;
import com.ek.mobileapp.model.TimePoint;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.model.VitalSignItem;

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
    //一天所有生命体征
    private List<VitalSignData> vitalSignDatas_all = new ArrayList<VitalSignData>();
    private List<VitalSignData> vitalSignDatas = new ArrayList<VitalSignData>();
    private List<MeasureType> measureTypes = new ArrayList<MeasureType>();
    private List<TimePoint> timePoints = new ArrayList<TimePoint>();
    private List<SkinTest> skinTests = new ArrayList<SkinTest>();

    private Patient currentPatient;

    private VitalSignData vitalSignData;
    private VitalSignData vitalSignData2;

    private List<DrugCheckData> drugCheckDatas = new ArrayList<DrugCheckData>();

    private List<DrugApprovalData> drugApprovalDatas = new ArrayList<DrugApprovalData>();

    private DrugApprovalData drugApprovalData;
    private List<OperationApprovalData> operationApprovalDatas = new ArrayList<OperationApprovalData>();

    private OperationApprovalData operationApprovalData;
    private List<ApprovalNote> approvalNotes = new ArrayList<ApprovalNote>();

    private String moduleCode;//进入模块后设为null

    //
    private boolean webLog;

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

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public List<DrugApprovalData> getDrugApprovalDatas() {
        return drugApprovalDatas;
    }

    public void setDrugApprovalDatas(List<DrugApprovalData> drugApprovalDatas) {
        this.drugApprovalDatas = drugApprovalDatas;
    }

    public DrugApprovalData getDrugApprovalData() {
        return drugApprovalData;
    }

    public void setDrugApprovalData(DrugApprovalData drugApprovalData) {
        this.drugApprovalData = drugApprovalData;
    }

    public List<ApprovalNote> getApprovalNotes() {
        return approvalNotes;
    }

    public void setApprovalNotes(List<ApprovalNote> approvalNotes) {
        this.approvalNotes = approvalNotes;
    }

    public boolean isWebLog() {
        return webLog;
    }

    public void setWebLog(boolean webLog) {
        this.webLog = webLog;
    }

    public List<OperationApprovalData> getOperationApprovalDatas() {
        return operationApprovalDatas;
    }

    public void setOperationApprovalDatas(List<OperationApprovalData> operationApprovalDatas) {
        this.operationApprovalDatas = operationApprovalDatas;
    }

    public OperationApprovalData getOperationApprovalData() {
        return operationApprovalData;
    }

    public void setOperationApprovalData(OperationApprovalData operationApprovalData) {
        this.operationApprovalData = operationApprovalData;
    }

}
