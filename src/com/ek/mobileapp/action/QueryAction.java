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
    public static int getTotalData(String startDate, String endDate) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("startDate", startDate));
        params.add(new BasicNameValuePair("endDate", endDate));
        JSONObject res = null;
        //res = HttpTool.getTool().post(WebUtils.HOST + WebUtils.ACTION_QUERYTOTAL, params);
        if (res == null)
            return WebUtils.WEBERROR;
        try {

            List<QueryTotalInfo> totals = new ArrayList<QueryTotalInfo>();
            JSONArray arrays = res.getJSONArray("data");
            for (int i = 0; i < arrays.length(); i++) {
                totals.add(new QueryTotalInfo(arrays.getJSONObject(i)));
            }
            GlobalCache.getCache().setQueryList(totals);

            return WebUtils.SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            return WebUtils.WEBERROR;
        }
    }
}
