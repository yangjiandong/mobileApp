package com.ek.mobileapp.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.ek.mobileapp.model.MobLogDTO;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.WebUtils;

public class MobLogAction {
    public static int mobLog(String infos, String type, String ip) {
        UserDTO u = GlobalCache.getCache().getLoginuser();
        String lastIp = GlobalCache.getCache().getLastIp();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        MobLogDTO mlog = new MobLogDTO();
        mlog.setUserId(u.getId());
        mlog.setEvent(infos);
        mlog.setType(type);
        mlog.setNetIp(lastIp);
        //mobile info
        mlog.setFrom("");
        mlog.setNote(infos);
        params.add(new BasicNameValuePair("infos", JSON.toJSONString(mlog)));

        String url = "http://" + ip + WebUtils.MOBLOG;
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

    public static int mobLogInfo(String infos, String ip) {
        return mobLog(infos, "info", ip);
    }

    public static int mobLogError(String infos, String ip) {
        return mobLog(infos, "error", ip);
    }
}
