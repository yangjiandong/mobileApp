package com.ek.mobileapp.utils.download;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class DownLoad 
{
    private String strUrl = "";
    private DownLoadCallBack mCallback = null;
    private MHandler mHandler = null;
    private Thread thread = null;
    private String fileName = "";
    private int len = 0;    // 下载文件总长度
    private int total = 0;    // 当前下载的长度
    
    /**
     * 构造函数
     * @param url URL
     */
    public DownLoad(String url,String fileName,DownLoadCallBack callBack)
    {
        this.strUrl = url;
        this.mCallback = callBack;
        this.fileName = fileName;
    }
    
    /**
     * 下载文件
     */
    public void DownLoadFile()
    {
        mHandler = new MHandler();
        thread = new Thread(runnable);
        thread.start();
    }
    
    Runnable runnable = new Runnable()
    {
        @Override
        public void run() 
        {
            try 
            {
                URL url = new URL(strUrl);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                len = con.getContentLength();
                SendMsg("maxLen", len);
                InputStream inStream = new BufferedInputStream(con.getInputStream());
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                OutputStream outStream = new FileOutputStream(path + "/mMusic/"+fileName);
                byte[] buffer = new byte[10240];
                int count = 0;
                total = 0;
                DecimalFormat format = new DecimalFormat("#0.00");
                while((count = inStream.read(buffer)) != -1)
                {
                    total += count;
                    outStream.write(buffer,0,count);
                    SendMsg("currentVal", total);
                    double tLen = Double.valueOf(String.valueOf(len));
                    double tDownTotal = Double.valueOf(String.valueOf(total));
                    double tPercent = tDownTotal / tLen * 100;
                    String tStrPercent = format.format(tPercent);
                    SendMsg("percent", tStrPercent);
                }
                outStream.flush();
                outStream.close();
                inStream.close();
                if(len == total)
                {
                    SendMsg("success", 0);
                }
                else
                {
                    SendMsg("error", -1);
                }
            }
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    };
    
    final class MHandler extends Handler
    {
        public MHandler() 
        {
            
        }
        
        public MHandler(Looper l)
        {
            super(l);
        }
        
        @Override
        public void handleMessage(Message msg) 
        {
            try
            {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                if(data.containsKey("currentVal"))
                {
                    int val = data.getInt("currentVal");
                    mCallback.UpdateUIProgress(val);
                }
                if(data.containsKey("maxLen"))
                {
                    int val = data.getInt("maxLen");
                    mCallback.SetMaxProgress(val);
                    
                }
                if(data.containsKey("percent"))
                {
                    mCallback.ReturnPercent(data.getString("percent"));
                }
                if(data.containsKey("success"))
                {
                    mCallback.Success();
                    StopThread();
                }
                if(data.containsKey("error"))
                {
                    mCallback.Error();
                    StopThread();
                }
            }
            catch(Exception e)
            {
                StopThread();
            }
        }
    }
    
    /**
     * 向Handler发送消息
     * @param key 键
     * @param val 值
     */
    private void SendMsg(String key,int val)
    {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt(key, val);
        msg.setData(data);
        wl("-->"+key+val);
        mHandler.sendMessage(msg);
    }
    
    /**
     * 向Handler发送消息
     * @param key 键
     * @param val 值
     */
    private void SendMsg(String key,String val)
    {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString(key, val+"%");
        msg.setData(data);
        wl("-->"+key+val);
        mHandler.sendMessage(msg);
    }
    
    /**
     * 停止下载
     */
    public void StopThread()
    {
        if(thread != null)
        {
            Thread t = thread;
            thread = null;
            t.interrupt();
        }
        wl("StopThread");
    }
    
    /**
     * 写log
     * @param strLog
     */
    private void wl(String strLog)
    {
        Log.i("sjr",strLog);
    }
}