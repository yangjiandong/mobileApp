package com.ek.mobileapp.nurse.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.ek.mobileapp.model.DrugCheckData;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.WebUtils;

public class DrugCheckAction {

    //取一个病人
    public static String getPatient(String patientId) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.DRUGCHECK_GET_PATIENT;
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

    public static String getData(String patientId, String barCode) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.DRUGCHECK_GET_DATA;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("patientId", patientId));
        params.add(new BasicNameValuePair("barCode", barCode));
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "-1";

        List<DrugCheckData> drugCheckDatas = new ArrayList<DrugCheckData>();
        try {
            if (!res.getBoolean("success")) {
                return "-1";
            }
            JSONArray arrays = res.getJSONArray("drugCheckData");
            if (arrays.length() == GlobalCache.getCache().getDrugCheckDatas().size())
                return "-1";
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                drugCheckDatas.add(JSON.parseObject(p.toString(), DrugCheckData.class));
            }
            GlobalCache.getCache().setDrugCheckDatas(drugCheckDatas);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "1";
    }

    public static String queryData(String patientId) {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.DRUGCHECK_QUERY_DATA;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("patientId", patientId));
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "-1";

        List<DrugCheckData> drugCheckDatas = new ArrayList<DrugCheckData>();
        try {
            if (!res.getBoolean("success")) {
                return "-1";
            }
            JSONArray arrays = res.getJSONArray("drugCheckData");
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                drugCheckDatas.add(JSON.parseObject(p.toString(), DrugCheckData.class));
            }
            GlobalCache.getCache().setDrugCheckDatas(drugCheckDatas);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "1";
    }

    public static String commitHis() {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.DRUGCHECK_COMMIT_HIS;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        params.add(new BasicNameValuePair("deviceId", GlobalCache.getCache().getDeviceId().trim()));

        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "-1";

        try {
            if (!res.getBoolean("success")) {
                return res.getString("message");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "1";
    }
}
