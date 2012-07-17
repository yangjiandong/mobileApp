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

    public static void getPatient(String patientId) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_PATIENT;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("patientId", patientId));
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return;

        try {
            if (!res.getBoolean("success")) {
                return;
            }
            Patient patient = JSON.parseObject(res.getJSONObject("patient").toString(), Patient.class);

            GlobalCache.getCache().setCurrentPatient(patient);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public static void getOne(String patientId, String busDate, String timePoint, String itemName) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_ONE;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("patientId", patientId));
        params.add(new BasicNameValuePair("busDate", busDate));
        params.add(new BasicNameValuePair("timePoint", timePoint));
        params.add(new BasicNameValuePair("itemName", itemName));
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
            GlobalCache.getCache().setVitalSignDatas(vitalSignDatas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveVitalSign(String patientId, String busDate, String itemName, String timePoint,
            String itemCode, String timeCode, String value1, String value2, String unit, String measureTypeCode) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.VITALSIGN_GET_ALL;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        params.add(new BasicNameValuePair("patientId", patientId));
        params.add(new BasicNameValuePair("busDate", busDate));
        params.add(new BasicNameValuePair("itemName", itemName));
        params.add(new BasicNameValuePair("timePoint", timePoint));
        params.add(new BasicNameValuePair("itemCode", itemCode));
        params.add(new BasicNameValuePair("timeCode", timeCode));
        params.add(new BasicNameValuePair("value1", value1));
        params.add(new BasicNameValuePair("value2", value2));
        params.add(new BasicNameValuePair("unit", unit));
        params.add(new BasicNameValuePair("measureTypeCode", measureTypeCode));
        HttpTool.getTool().post(url, params);

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
