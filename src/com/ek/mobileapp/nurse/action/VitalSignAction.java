package com.ek.mobileapp.nurse.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.ek.mobileapp.model.MeasureType;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.SkinTest;
import com.ek.mobileapp.model.TimePoint;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.model.VitalSignItem;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.WebUtils;

public class VitalSignAction {
    public static void getItem(String typeCode) {
        String ip = GlobalCache.getCache().getHostIp();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_ITEM;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("typeCode", typeCode));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return;

        List<VitalSignItem> vitalSignItems = new ArrayList<VitalSignItem>();
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            JSONArray arrays = res.getJSONArray("vitalSignItem");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                vitalSignItems.add(JSON.parseObject(p.toString(), VitalSignItem.class));
            }
            GlobalCache.getCache().setVitalSignItems(vitalSignItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //取一个病人
    public static String getPatient(String patientId) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_PATIENT;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("patientId", patientId));
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "-1";

        try {
            if (!res.getBoolean("success")) {
                return "-1";
            }
            Patient patient = JSON.parseObject(res.getJSONObject("patient").toString(), Patient.class);

            GlobalCache.getCache().setCurrentPatient(patient);
        } catch (JSONException e) {
            e.printStackTrace();
            return "-1";
        }
        return "1";
    }

    public static void getMeasureType() {
        String ip = GlobalCache.getCache().getHostIp();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_MEASURETYPE;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return;

        List<MeasureType> measureTypes = new ArrayList<MeasureType>();
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            JSONArray arrays = res.getJSONArray("measureType");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                measureTypes.add(JSON.parseObject(p.toString(), MeasureType.class));
            }
            GlobalCache.getCache().setMeasureTypes(measureTypes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getSkinTest() {
        String ip = GlobalCache.getCache().getHostIp();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_SKINTEST;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return;

        List<SkinTest> skinTests = new ArrayList<SkinTest>();
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            JSONArray arrays = res.getJSONArray("skinTest");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                skinTests.add(JSON.parseObject(p.toString(), SkinTest.class));
            }
            GlobalCache.getCache().setSkinTests(skinTests);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getTimePoint() {
        String ip = GlobalCache.getCache().getHostIp();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_TIMEPOINT;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return;

        List<TimePoint> timePoints = new ArrayList<TimePoint>();
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            JSONArray arrays = res.getJSONArray("timePoint");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                timePoints.add(JSON.parseObject(p.toString(), TimePoint.class));
            }
            GlobalCache.getCache().setTimePoints(timePoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getOne(String patientId, String busDate, String timePoint, String itemCode) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_ONE;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("patientId", patientId));
        params.add(new BasicNameValuePair("busDate", busDate));
        params.add(new BasicNameValuePair("timePoint", timePoint));
        params.add(new BasicNameValuePair("itemCode", itemCode));
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return;

        List<VitalSignData> vitalSignDatas = new ArrayList<VitalSignData>();
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            JSONArray arrays = res.getJSONArray("vitalSignData");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                vitalSignDatas.add(JSON.parseObject(p.toString(), VitalSignData.class));
            }
            GlobalCache.getCache().setVitalSignDatas(vitalSignDatas);

            if (vitalSignDatas.size() == 1) {
                GlobalCache.getCache().setVitalSignData(vitalSignDatas.get(0));
            } else {
                VitalSignData entity = new VitalSignData();
                entity.setAddDate(busDate);
                entity.setPatientId(patientId);
                entity.setTimePoint(timePoint);
                entity.setItemCode(itemCode);
                entity.setUserId(userId);
                GlobalCache.getCache().setVitalSignData(entity);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getAll(String patientId, String busDate) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_ALL;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("patientId", patientId));
        params.add(new BasicNameValuePair("busDate", busDate));
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return;

        List<VitalSignData> vitalSignDatas = new ArrayList<VitalSignData>();
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            JSONArray arrays = res.getJSONArray("vitalSignData");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                vitalSignDatas.add(JSON.parseObject(p.toString(), VitalSignData.class));
            }
            GlobalCache.getCache().setVitalSignDatas_all(vitalSignDatas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String saveVitalSign() {
        String ip = GlobalCache.getCache().getHostIp();
        VitalSignData data = GlobalCache.getCache().getVitalSignData();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.VITALSIGN_SAVE;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        params.add(new BasicNameValuePair("patientId", data.getPatientId()));
        params.add(new BasicNameValuePair("busDate", data.getAddDate()));
        params.add(new BasicNameValuePair("itemName", data.getItemName()));
        params.add(new BasicNameValuePair("timePoint", data.getTimePoint()));
        params.add(new BasicNameValuePair("itemCode", data.getItemCode()));
        params.add(new BasicNameValuePair("timeCode", data.getTimeCode()));
        params.add(new BasicNameValuePair("value1", data.getValue1()));
        params.add(new BasicNameValuePair("value2", data.getValue2()));
        params.add(new BasicNameValuePair("unit", data.getUnit()));
        params.add(new BasicNameValuePair("measureTypeCode", data.getMeasureTypeCode()));

        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "保存数据出错,请联系管理员";

        try {
            if (!res.getBoolean("success")) {
                return res.getString("保存数据出错,请联系管理员");
            }

            boolean flag = false;
            List<VitalSignData> lists = GlobalCache.getCache().getVitalSignDatas_all();
            for (VitalSignData a : lists) {
                if (a.getItemCode().equals(data.getItemCode())) {
                    flag = true;
                    a = data;
                }

            }
            if (!flag) {
                lists.add(data);
            }
            GlobalCache.getCache().setVitalSignDatas_all(lists);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "1";
    }

    public static String commitHis() {
        String ip = GlobalCache.getCache().getHostIp();
        VitalSignData data = GlobalCache.getCache().getVitalSignData();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.VITALSIGN_COMMIT_HIS;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        params.add(new BasicNameValuePair("patientId", data.getPatientId()));
        params.add(new BasicNameValuePair("busDate", data.getAddDate()));

        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "提取数据出错,请联系管理员";

        try {
            if (!res.getBoolean("success")) {
                return res.getString("保存数据出错,请联系管理员");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "1";
    }

    public static void getPatientAll(String deptCode) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_PATIENT_ALL;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("deptCode", deptCode));
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return;

        List<Patient> patients = new ArrayList<Patient>();
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            JSONArray arrays = res.getJSONArray("patients");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                patients.add(JSON.parseObject(p.toString(), Patient.class));
            }
            GlobalCache.getCache().setPatients(patients);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
