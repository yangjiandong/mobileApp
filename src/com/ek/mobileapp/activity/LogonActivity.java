package com.ek.mobileapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.LogonAction;
import com.ek.mobileapp.utils.SettingsUtils;
import com.ek.mobileapp.utils.WebUtils;

public class LogonActivity extends Activity {

    EditText username;
    EditText password;
    Button logonBtn;

    public static final int LOGINACTION = 12;
    private ProgressDialog proDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);
        username = (EditText) findViewById(R.id.logon_username);
        password = (EditText) findViewById(R.id.logon_password);
        logonBtn = (Button) findViewById(R.id.logon_ok);

        logonBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (username.getEditableText().toString().trim().equals("")) {
                    username.setError("用户名不能为空！");
                    return;
                }
                login(username.getEditableText().toString().trim(), password
                        .getEditableText().toString().trim());

            }
        });

    }

    private void login(String loginname, String psd) {
        proDialog = ProgressDialog.show(LogonActivity.this, "", "登录中...", true, true);
        Thread login = new LoginHandler(loginname, psd);
        login.start();
    }

    private class LoginHandler extends Thread {
        private String loginname;
        private String psd;

        private void loginUnSuccess(String msg) {
            proDialog.dismiss();
            clearPreferences();
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("type", 0);
            bundle.putString("msg", msg);
            message.setData(bundle);
            UIHandler.sendMessage(message);
        }

        public LoginHandler(String loginname, String psd) {
            this.loginname = loginname;
            this.psd = psd;
        }

        public void run() {
            int res = LogonAction.login(loginname, psd);
            if (res == com.ek.mobileapp.utils.WebUtils.WEBERROR) {
                loginUnSuccess("请检查网络");
            } else if (res == WebUtils.APPLICATIONERROR) {
                loginUnSuccess("用户名密码不正确");
            } else {
                //if(savepassword.isChecked()) {
                //    setPreferences(loginname,psd);
                //}
                Intent intent = new Intent(LogonActivity.this, MainActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, LOGINACTION);
                proDialog.dismiss();
                finish();
            }
        }
    }

    Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            switch (type) {
            case 0: {
                AlertDialog.Builder builder = new AlertDialog.Builder(LogonActivity.this);
                builder.setMessage(msg.getData().getString("msg")).setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            }
            default: {

            }
            }
        }
    };

    public void setPreferences(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                com.ek.mobileapp.utils.SettingsUtils.PreferencesString, 0);
        Editor edit = sharedPreferences.edit();
        edit.putString("username", username);
        edit.putString("password", password);
        edit.putBoolean("issave", true);
        //if (autologin.isChecked()) {
        //    edit.putBoolean("isauto", true);
        //} else {
        //    edit.putBoolean("isauto", false);
        //}
        edit.commit();
    }

    public void clearPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SettingsUtils.PreferencesString, 0);
        Editor edit = sharedPreferences.edit();
        edit.putString("username", "");
        edit.putString("password", "");
        edit.putBoolean("issave", false);
        edit.putBoolean("isauto", false);
        edit.commit();
    }
}
