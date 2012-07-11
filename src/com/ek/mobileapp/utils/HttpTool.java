package com.ek.mobileapp.utils;

import java.net.URI;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

public class HttpTool {
    protected static DefaultHttpClient httpclient;
    private String cookie = "";
    private static HttpTool tool = null;

    private HttpTool() {
        httpclient = new DefaultHttpClient();
    }

    public static HttpTool getTool() {
        if(tool == null) tool = new HttpTool();
        return tool;
    }

    public JSONObject login(String url) {
        if (url == null)
            return null;
        try {
            HttpGet get = new HttpGet();
            get.setURI(new URI(url));
            HttpResponse response = httpclient.execute(get);
            int result = response.getStatusLine().getStatusCode();
            if (result == 200) {
                String strResult = EntityUtils.toString(response.getEntity(),
                        HTTP.UTF_8);
                JSONObject json = new JSONObject(strResult);
                Header[] headers = response.getHeaders("Set-Cookie");
                if (headers.length != 0)
                    cookie = headers[0].toString();

                return json;
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject post(String url, List<NameValuePair> parameters) {
        if (url == null)
            return null;
        try {
            HttpPost post = new HttpPost(url);
            post.setHeader("Cookie", cookie);
            post.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(post);
            int result = response.getStatusLine().getStatusCode();
            if (result == 200) {
                String strResult = EntityUtils.toString(response.getEntity(),
                        HTTP.UTF_8);
                JSONObject json = new JSONObject(strResult);
                return json;
            } else
                return null;
        }
        catch(Exception e) {
            Log.e("http post", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject get(String url) {
        if (url == null)
            return null;
        try {
            HttpGet get = new HttpGet();
            get.setURI(new URI(url));
            get.setHeader("Cookie", cookie);
            HttpResponse response = httpclient.execute(get);
            int result = response.getStatusLine().getStatusCode();
            if (result == 200) {
                String strResult = EntityUtils.toString(response.getEntity(),
                        HTTP.UTF_8);
                //Log.e("web", strResult);
                JSONObject json = new JSONObject(strResult);
                return json;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
