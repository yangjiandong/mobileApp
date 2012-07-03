package com.ek.mobileapp.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.ek.mobileapp.model.GeneralInfo;
import com.ek.mobileapp.model.GeneralTotal;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.WebUtils;

public class GetGeneralInfoAction {
    public static void getGeneralTotals() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start", GlobalCache.getCache()
                .getStartDate()));
        params.add(new BasicNameValuePair("end", GlobalCache.getCache()
                .getEndDate()));
        params.add(new BasicNameValuePair("type", "total"));
        JSONObject res = HttpTool.getTool().post(
                WebUtils.HOST + WebUtils.GETGENERALINFOACTION, params);
        if (res == null)
            return;
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            List<GeneralTotal> totals = new ArrayList<GeneralTotal>();
            JSONArray arrays = res.getJSONArray("info");
            for (int i = 0; i < arrays.length(); i++) {
                totals.add(new GeneralTotal(arrays.getJSONObject(i)));
            }
            GlobalCache.getCache().setTotals(totals);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void getGeneralInfos(int sub) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("start", GlobalCache.getCache()
                .getStartDate()));
        params.add(new BasicNameValuePair("end", GlobalCache.getCache()
                .getEndDate()));
        params.add(new BasicNameValuePair("sub", sub+""));
        params.add(new BasicNameValuePair("type", "detail"));
        JSONObject res = HttpTool.getTool().post(
                WebUtils.HOST + WebUtils.GETGENERALINFOACTION, params);
        if (res == null)
            return;
        try {
            if (!res.getBoolean("success")) {
                return;
            }
            List<GeneralInfo> infos = new ArrayList<GeneralInfo>();
            JSONArray arrays = res.getJSONArray("info");
            for (int i = 0; i < arrays.length(); i++) {
                infos.add((GeneralInfo) (JSON.parseObject(
                        arrays.getJSONObject(i).toString(), GeneralInfo.class)));
            }
            GlobalCache.getCache().setInfos(infos);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
