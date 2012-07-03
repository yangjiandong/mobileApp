package com.ek.mobileapp.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.WebUtils;

public class LogonAction {
    public static int login(String loginname, String password) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", loginname));
        params.add(new BasicNameValuePair("password", password));
        JSONObject res = HttpTool.getTool().post(WebUtils.HOST + WebUtils.LOGINACTION, params);
        if (res == null)
            return WebUtils.WEBERROR;
        try {
            if (!res.getBoolean("success")) {
                return WebUtils.LOGINERROR;
            }
            UserDTO user = JSON.parseObject(res.getJSONObject("user").toString(), UserDTO.class);
            GlobalCache.getCache().setLoginuser(user);
            return WebUtils.SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
            return WebUtils.APPLICATIONERROR;
        }
    }
}
