package com.ek.mobileapp.approval.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.ek.mobileapp.model.ApprovalNote;
import com.ek.mobileapp.model.DrugApprovalData;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.WebUtils;

public class OperationApprovalAction {

    public static String getAll() {
        String ip = GlobalCache.getCache().getHostIp();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.DRUGAPPROVAL_GET_ALL;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "-1";

        List<DrugApprovalData> drugApprovalDatas = new ArrayList<DrugApprovalData>();
        try {
            if (!res.getBoolean("success")) {
                return "-1";
            }
            JSONArray arrays = res.getJSONArray("drugApprovalDatas");
            if (arrays.length() == GlobalCache.getCache().getDrugApprovalDatas().size())
                return "-1";
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                drugApprovalDatas.add(JSON.parseObject(p.toString(), DrugApprovalData.class));
            }
            GlobalCache.getCache().setDrugApprovalDatas(drugApprovalDatas);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "1";
    }

    public static String getOne(String appNo) {
        String ip = GlobalCache.getCache().getHostIp();
        String url = "http://" + ip + WebUtils.DRUGCHECK_QUERY_DATA;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("appNo", appNo));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "-1";

        DrugApprovalData data = new DrugApprovalData();
        try {
            if (!res.getBoolean("success")) {
                return "-1";
            }

            data = JSON.parseObject(res.getJSONObject("drugApprovalData").toString(), DrugApprovalData.class);

            GlobalCache.getCache().setDrugApprovalData(data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "1";
    }

    public static String commitHis() {
        String ip = GlobalCache.getCache().getHostIp();
        String url = "http://" + ip + WebUtils.DRUGAPPROVAL_COMMIT_HIS;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

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

    public static String saveAppr() {
        String ip = GlobalCache.getCache().getHostIp();
        DrugApprovalData data = GlobalCache.getCache().getDrugApprovalData();
        Long userId = GlobalCache.getCache().getLoginuser().getId();
        String url = "http://" + ip + WebUtils.DRUGAPPROVAL_SAVE;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("userId", String.valueOf(userId)));
        params.add(new BasicNameValuePair("appNo", data.getAppNo().toString()));
        params.add(new BasicNameValuePair("result", data.getResult()));
        params.add(new BasicNameValuePair("note", data.getNote()));

        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "保存数据出错,请联系管理员";

        try {
            if (!res.getBoolean("success")) {
                return res.getString("保存数据出错,请联系管理员");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "1";
    }

    public static String getNote(String typeCode) {
        String ip = GlobalCache.getCache().getHostIp();
        String url = "http://" + ip + WebUtils.DRUGAPPROVAL_GET_NOTE;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("typeCode", typeCode));
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return "-1";

        List<ApprovalNote> approvalNotes = new ArrayList<ApprovalNote>();
        try {
            if (!res.getBoolean("success")) {
                return "-1";
            }
            JSONArray arrays = res.getJSONArray("approvalNotes");
            if (arrays.length() == GlobalCache.getCache().getApprovalNotes().size())
                return "-1";
            for (int i = 0; i < arrays.length(); i++) {
                JSONObject p = (JSONObject) arrays.get(i);
                approvalNotes.add(JSON.parseObject(p.toString(), ApprovalNote.class));
            }
            GlobalCache.getCache().setApprovalNotes(approvalNotes);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "1";
    }

}
