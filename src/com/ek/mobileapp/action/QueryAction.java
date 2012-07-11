package com.ek.mobileapp.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ek.mobileapp.model.QueryTotalInfo;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.WebUtils;

public class QueryAction {
    public static int getTotalData(String startDate, String endDate, String ip) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("startDate", startDate));
        params.add(new BasicNameValuePair("endDate", endDate));
        String url = "http://" + ip + WebUtils.ACTION_QUERYTOTAL;
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return WebUtils.WEBERROR;
        try {

            List<QueryTotalInfo> totals = new ArrayList<QueryTotalInfo>();
            if (!res.getBoolean("success")) {
                return WebUtils.LOGINERROR;
            }
            //JSONArray arrays = res.getJSONArray("data");
            //for (int i = 0; i < arrays.length(); i++) {
            //    totals.add(new QueryTotalInfo(arrays.getJSONObject(i)));
            //}
            //GlobalCache.getCache().setQueryList(totals);

            return WebUtils.SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            return WebUtils.WEBERROR;
        }
    }
}
