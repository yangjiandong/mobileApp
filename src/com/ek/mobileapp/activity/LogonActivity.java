package com.ek.mobileapp.activity;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.LogonAction;
import com.ek.mobileapp.utils.SettingsUtils;
import com.ek.mobileapp.utils.WebUtils;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class LogonActivity extends Activity {

    EditText username;
    EditText password;
    Button logonBtn;
    CheckBox savepassword;
    SharedPreferences sharedPreferences;

    public static final int LOGINACTION = 12;
    private ProgressDialog proDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logon);

        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);

        //设置
        final Action otherAction = new IntentAction(this, new Intent(this, SettingActivity.class),
                R.drawable.ic_title_export_default);
        actionBar.addAction(otherAction);

        String version = "x.xx";
        String vendor = "鑫亿";
        try {
            PackageInfo pinfo = this.getPackageManager().getPackageInfo(SettingsUtils.TRACKER_PACKAGE_NAME, 0);
            version = pinfo.versionCode + "." + pinfo.versionName;
            //vendor = pinfo.applicationInfo.sharedUserLabel;
            actionBar.setTitle(pinfo.applicationInfo.labelRes);

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        //手工设置版本显示
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.repeat_bg);
        Bitmap other = textAsBitmap(bitmap, "ver:" + version, vendor, 15, Color.BLACK);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.footer);
        BitmapDrawable background = new BitmapDrawable(other);
        linearLayout.setBackgroundDrawable(background);

        username = (EditText) findViewById(R.id.logon_username);

        password = (EditText) findViewById(R.id.logon_password);
        savepassword = (CheckBox) findViewById(R.id.logon_save_password);
        logonBtn = (Button) findViewById(R.id.logon_ok);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //getSharedPreferences(SettingsUtils.PreferencesString, MODE_PRIVATE);
        String share_username = sharedPreferences.getString("username", "");
        String share_password = sharedPreferences.getString("password", "");

        // 记住密码
        boolean issave = sharedPreferences.getBoolean("issave", false);
        if (issave) {
            username.setText(share_username);
            password.setText(share_password);
            savepassword.setChecked(issave);
        }

        logonBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (username.getEditableText().toString().trim().equals("")) {
                    username.setError("用户名不能为空！");
                    return;
                }
                String ip = sharedPreferences.getString("setting_http_ip", WebUtils.HOST);
                login(username.getEditableText().toString().trim(), password.getEditableText().toString().trim(), ip);

            }
        });

    }

    private void login(String loginname, String psd, String ip) {
        proDialog = ProgressDialog.show(LogonActivity.this, "", "登录中...", true, true);
        Thread login = new LoginHandler(loginname, psd, ip);
        login.start();
    }

    private class LoginHandler extends Thread {
        private String loginname;
        private String psd;
        private String ip;

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

        public LoginHandler(String loginname, String psd, String ip) {
            this.loginname = loginname;
            this.psd = psd;
            this.ip = ip;
        }

        public void run() {

            int res = LogonAction.login(loginname, psd, ip);
            if (res == WebUtils.SUCCESS) {
                if (savepassword.isChecked()) {
                    setPreferences(loginname, psd);
                }
                Intent intent = new Intent(LogonActivity.this, MainActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, LOGINACTION);
                proDialog.dismiss();
                finish();

            } else if (res == WebUtils.APPLICATIONERROR) {
                loginUnSuccess("用户名密码不正确");
            } else if (res == com.ek.mobileapp.utils.WebUtils.WEBERROR) {
                loginUnSuccess("请检查网络,ip地址:" + ip);
            } else {
                loginUnSuccess("登录失败");
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
        //SharedPreferences sharedPreferences = getSharedPreferences(
        //        com.ek.mobileapp.utils.SettingsUtils.PreferencesString, 0);
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
        Editor edit = sharedPreferences.edit();
        edit.putString("username", "");
        edit.putString("password", "");
        edit.putBoolean("issave", false);
        edit.putBoolean("isauto", false);
        edit.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit(RESULT_OK, "确认退出程序");
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void exit(final int result, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认退出");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                setResult(result);
                System.exit(0);
            }
        });

        // 设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                // do nothing

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Bitmap textAsBitmap(Bitmap image, String version, String vendor, float textSize, int textColor) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setAntiAlias(true);

        Bitmap newMapBitmap = image.copy(Bitmap.Config.ARGB_8888, true);

        try {
            Canvas canvas = new Canvas(newMapBitmap);
            canvas.drawText(vendor, 10, 20, paint);
            canvas.drawText(version, 10, 35, paint);
        } catch (Exception e) {
            Log.e("textAsBitmap", e.getMessage());
        }

        String filename = "version.jpg";
        File sd = Environment.getExternalStorageDirectory();
        File dest = new File(sd, filename);

        try {
            FileOutputStream out = new FileOutputStream(dest);
            newMapBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newMapBitmap;
    }
}
