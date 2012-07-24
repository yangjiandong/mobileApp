package com.ek.mobileapp.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.ek.mobileapp.model.MobLogDTO;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.WebUtils;

public class MobLogAction {
    private static MobLogAction me = null;

    public static MobLogAction getMobLogAction() {
        if (me == null)
            me = new MobLogAction();
        return me;
    }

    //
    private int mobLog(String event, String infos, String type) {
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

    private class MobLogTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... params) {

            String event = params[0];
            String infos = params[0];
            String type = params[0];
            mobLog(event, infos, type);

            return 1;
        }

        protected void onPostExecute(String result) {
            //textView.setText(result);
        }
    }

    public void mobLogInfo(String event, String infos) {

        Log.i(event, infos);

        MobLogTask log = new MobLogTask();
        log.execute(new String[] { event, infos, "info" });

        //Runnable runLog = new Runnable() {
        //    public void run() {                    //
        //        mobLog(events, infoss, "info");
        //    }
        //};
        //new Thread(runLog).start();
        //return mobLog(event, infos, "info");
    }

    public void mobLogError(String event, String infos) {

        Log.e(event, infos);

        MobLogTask log = new MobLogTask();
        log.execute(new String[] { event, infos, "error" });

        //        Runnable runLog = new Runnable() {
        //            public void run() {                    //
        //                mobLog(events, infoss, "error");
        //            }
        //        };
        // new Thread(runLog).start();
        //return mobLog(event, infos, "error");
    }
}
