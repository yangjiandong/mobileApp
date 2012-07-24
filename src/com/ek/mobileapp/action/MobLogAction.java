package com.ek.mobileapp.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.ek.mobileapp.model.MobLogDTO;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.WebUtils;

public class MobLogAction {

    //
    public static int mobLog(String event, String infos, String type) {
        UserDTO u = GlobalCache.getCache().getLoginuser();
        if (u == null)
            return WebUtils.WEBERROR;
        String lastIp = GlobalCache.getCache().getLastIp();
        String hostIp = GlobalCache.getCache().getHostIp();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        MobLogDTO mlog = new MobLogDTO();
        mlog.setUserId(u.getId());
        mlog.setEvent(event);
        mlog.setType(type);
        mlog.setNetIp(lastIp);
        //mobile info
        mlog.setFrom("");
        mlog.setNote(infos);
        params.add(new BasicNameValuePair("infos", JSON.toJSONString(mlog)));

        String url = "http://" + hostIp + WebUtils.MOBLOG;
        JSONObject res = HttpTool.getTool().post(url, params);
        if (res == null)
            return WebUtils.WEBERROR;
        try {
            if (!res.getBoolean("success")) {
                return WebUtils.LOGINERROR;
            }
            return WebUtils.SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            return WebUtils.APPLICATIONERROR;
        }
    }

    public static void mobLogInfo(String event, String infos) {
        final String events = event;
        final String infoss = infos;
        Log.i(event, infos);
        Runnable runLog = new Runnable() {
            public void run() {                    //
                mobLog(events, infoss, "info");
            }
        };
        new Thread(runLog).start();
        //return mobLog(event, infos, "info");
    }

    public static void mobLogError(String event, String infos) {
        final String events = event;
        final String infoss = infos;
        Log.e(event, infos);
        Runnable runLog = new Runnable() {
            public void run() {                    //
                mobLog(events, infoss, "error");
            }
        };
        new Thread(runLog).start();
        //return mobLog(event, infos, "error");
    }
}
