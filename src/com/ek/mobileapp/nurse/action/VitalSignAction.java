package com.ek.mobileapp.nurse.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.ek.mobileapp.model.Patient;
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

        //TODO
        List<com.ek.mobileapp.model.Patient> patients = new ArrayList<Patient>();
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            JSONArray arrays = res.getJSONArray("VitalSignData");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                patients.add(JSON.parseObject(p.toString(), Patient.class));
            }
            GlobalCache.getCache().setPatients(patients);
            if (patients != null && patients.size() > 0)
                GlobalCache.getCache().setCurrentPatient(patients.get(0));
        } catch (JSONException e) {

        }
    }

    public static void getPatient(String infos) {
        String ip = GlobalCache.getCache().getHostIp();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("infos", infos));

        //TODO
        String url = "http://" + ip + WebUtils.USERLOG;
        JSONObject res = HttpTool.getTool().post(url, params);
        List<com.ek.mobileapp.model.Patient> patients = new ArrayList<Patient>();
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            JSONArray arrays = res.getJSONArray("VitalSignData");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                patients.add(JSON.parseObject(p.toString(), Patient.class));
            }
            GlobalCache.getCache().setPatients(patients);
            if (patients != null && patients.size() > 0)
                GlobalCache.getCache().setCurrentPatient(patients.get(0));
        } catch (JSONException e) {

        }
    }

}
