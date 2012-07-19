package com.ek.mobileapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class ConnectServerWithProgressActivity extends Activity{
    protected ProgressDialog proDialog;
    protected String loadString = "正在获取数据...";
    protected String submitString = "正在传输数据...";
    protected String submitSuccess = "";

    protected void afterLoadData() {

    }

    protected void loadDate() {

    }

    protected void afterSubmit() {

    }

    protected void submitDate() {

    }

    protected void loadDataWithProgressDialog() {
        proDialog = ProgressDialog.show(getParent(), "", loadString, true,
                true);
        LoadHandler t = new LoadHandler();
        t.start();
    }

    protected void submitWithProgressDialog() {
        proDialog = ProgressDialog.show(getParent(), "", submitString, true,
                true);
        SubmitHandler t = new SubmitHandler();
        t.start();
    }

    private class LoadHandler extends Thread {
        @Override
        public void run() {
            loadDate();
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("type", -1);
            message.setData(bundle);
            UIHandler.sendMessage(message);
            proDialog.dismiss();
        }
    }

    private class SubmitHandler extends Thread {
        @Override
        public void run() {
            submitDate();
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("type", -2);
            message.setData(bundle);
            UIHandler.sendMessage(message);
            proDialog.dismiss();
        }
    }

    Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            switch (type) {
            case -1: {
                afterLoadData();
                break;
            }
            case -2: {
                afterSubmit();
                if(submitSuccess.equals("")) {

                }
                else {
                    Toast.makeText(getParent(),submitSuccess,Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default: {

            }
            }
            super.handleMessage(msg);
        }
    };

    public String getLoadString() {
        return loadString;
    }
    public void setLoadString(String loadString) {
        this.loadString = loadString;
    }
    public String getSubmitString() {
        return submitString;
    }
    public void setSubmitString(String submitString) {
        this.submitString = submitString;
    }

    public String getSubmitSuccess() {
        return submitSuccess;
    }

    public void setSubmitSuccess(String submitSuccess) {
        this.submitSuccess = submitSuccess;
    }
}
