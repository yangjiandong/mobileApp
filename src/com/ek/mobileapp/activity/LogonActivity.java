package com.ek.mobileapp.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.LogonAction;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.activity.SettingActivity;
import com.ek.mobileapp.approval.activity.DrugApproval;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.HttpTool;
import com.ek.mobileapp.utils.SettingsUtils;
import com.ek.mobileapp.utils.UtilString;
import com.ek.mobileapp.utils.WebUtils;

public class LogonActivity extends Activity implements OnSharedPreferenceChangeListener {
    WebView host_info;
    EditText username;
    EditText password;
    Button logonBtn;
    ImageButton parmBtn;
    CheckBox savepassword;
    SharedPreferences sharedPreferences;
    TelephonyManager tm;
    //
    String version = "1";

    public static final int LOGINACTION = 12;
    private ProgressDialog proDialog;

    String ip = "";

    //update
    private boolean isupdate = false;
    private String currentTempFilePath = "";
    private String strURL = "";

    private String moduleCode = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.logon);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //直接放在sharedPreferences,MobLogAction取不出值,暂时存放在cache
        boolean weblog = sharedPreferences.getBoolean("setting_weblog", false);
        GlobalCache.getCache().setWebLog(weblog);

        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        version = "1";
        String vendor = "鑫亿";
        try {
            PackageInfo pinfo = this.getPackageManager().getPackageInfo(SettingsUtils.TRACKER_PACKAGE_NAME, 0);
            version = pinfo.versionCode + "_" + pinfo.versionName;
            //vendor = pinfo.applicationInfo.sharedUserLabel;
            //actionBar.setTitle(pinfo.applicationInfo.labelRes);

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        //boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        //if (customTitleSupported) {

        //}

        final TextView myTitleText = (TextView) findViewById(R.id.myTitle);
        if (myTitleText != null) {
            //myTitleText.setText("NEW TITLE");
            // user can also set color using "Color" and then "Color value constant"
            // myTitleText.setBackgroundColor(Color.GREEN);
        }

        TextView app_info = (TextView) findViewById(R.id.app_info);
        app_info.setText("Copyright © " + vendor);
        TextView app_info2 = (TextView) findViewById(R.id.app_info2);
        app_info2.setText(" ver:" + version);

        host_info = (WebView) findViewById(R.id.host_info);
        username = (EditText) findViewById(R.id.logon_username);
        password = (EditText) findViewById(R.id.logon_password);
        savepassword = (CheckBox) findViewById(R.id.logon_save_password);
        logonBtn = (Button) findViewById(R.id.logon_ok);
        parmBtn = (ImageButton) findViewById(R.id.parm_set);

        //监听
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        //getSharedPreferences(SettingsUtils.PreferencesString, MODE_PRIVATE);
        String share_username = sharedPreferences.getString("username", "");
        String share_password = sharedPreferences.getString("password", "");
        ip = sharedPreferences.getString("setting_http_ip", WebUtils.HOST);

        host_info.loadUrl(WebUtils.HTTP + ip + WebUtils.NEWS);

        // 记住密码
        boolean issave = sharedPreferences.getBoolean("issave", false);
        if (issave) {
            username.setText(share_username);
            password.setText(share_password);
            savepassword.setChecked(issave);
        }

        isupdate = sharedPreferences.getBoolean("setting_update", false);
        //自动更新
        if (isupdate) {
            //检查后台
            getLastVersion();
        }

        parmBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LogonActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        logonBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (username.getEditableText().toString().trim().equals("")) {
                    //username.setHintTextColor(Color.RED);
                    username.setError("用户名不能为空！");
                    return;
                }

                ip = sharedPreferences.getString("setting_http_ip", WebUtils.HOST);
                login(username.getEditableText().toString().trim(), password.getEditableText().toString().trim(), ip);

            }
        });

    }

    @Override
    public void onStart() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            moduleCode = extras.getString("message");
        }
        super.onStart();
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
                //更新密码时用
                saveOldPwd(psd);
                if (savepassword.isChecked()) {
                    setPreferences(loginname, psd);
                }
                GlobalCache.getCache().setDeviceId(tm.getDeviceId());

                if (UtilString.isBlank(moduleCode)) {
                    Intent intent = new Intent(LogonActivity.this, MainActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent, LOGINACTION);
                } else {
                    if (moduleCode.equals("04")) {
                        //合理用药审批
                        Intent intent = new Intent(LogonActivity.this, DrugApproval.class);
                        //startActivity(intent);
                        startActivityForResult(intent, LOGINACTION);
                    } else if (moduleCode.equals("05")) {
                        //手术审批

                    } else if (moduleCode.equals("06")) {
                        //危机值

                    }
                }

                //startActivityForResult(intent, LOGINACTION);
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

    public void saveOldPwd(String password) {
        Editor edit = sharedPreferences.edit();
        edit.putString("old_password", password);
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
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        delFile(currentTempFilePath);
        super.onResume();

        host_info.loadUrl(WebUtils.HTTP + ip + WebUtils.NEWS);
    }

    private void update(final String filename) {
        new AlertDialog.Builder(LogonActivity.this).setTitle("更新提示").setMessage("发现新版本，是否更新")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        strURL = filename;
                        getFile(strURL);
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void getFile(final String strPath) {
        try {
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        getDataSource(strPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            new Thread(r).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataSource(String strPath) throws Exception {
        if (!URLUtil.isNetworkUrl(strPath)) {
            Toast.makeText(this, "下载地址错误", Toast.LENGTH_SHORT).show();
        } else {

            try {
                URL myURL = new URL(strPath);
                URLConnection conn = myURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                if (is == null) {
                    throw new RuntimeException("stream is null");
                }
                File myTempFile = File.createTempFile("sshapp-mobileapp", ".apk");
                currentTempFilePath = myTempFile.getAbsolutePath();
                FileOutputStream fos = new FileOutputStream(myTempFile);
                byte buf[] = new byte[128];
                do {
                    int numread = is.read(buf);
                    if (numread <= 0) {
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (true);
                openFile(myTempFile);
                this.finish();

                is.close();
            } catch (Exception ex) {
                MobLogAction.getMobLogAction().mobLogError("自动更新", ex.getMessage());
            }
        }
    }

    private void openFile(File f) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if (end.equals("apk")) {
            type = "application/vnd.android.package-archive";
        } else {
            type = "*";
        }
        if (end.equals("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }

    private void delFile(String strFileName) {
        File myFile = new File(strFileName);
        if (myFile.exists()) {
            myFile.delete();
        }
    }

    private void getLastVersion() {
        GetLastVersion getData = new GetLastVersion(getLastVersionHandler);
        Thread thread = new Thread(getData);
        thread.start();
    }

    //提取最新版本
    class GetLastVersion implements Runnable {
        Handler handler;

        public GetLastVersion(Handler h) {
            this.handler = h;
        }

        public void run() {
            Message message = Message.obtain();
            try {
                String url = "http://" + ip + "/common/get_last_deploy?type=mobile";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                JSONObject res = HttpTool.getTool().post(url, params);
                if (res == null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    bundle.putString("msg", "没取得版本信息");
                    message.setData(bundle);
                } else if (!res.getBoolean("success")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    bundle.putString("msg", "没取得版本信息");
                    message.setData(bundle);
                } else {
                    String lastVersion = res.getString("lastVersion");
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 1);
                    bundle.putString("msg", lastVersion);
                    message.setData(bundle);

                }
            } catch (Exception e) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                bundle.putString("msg", e.getMessage());
                message.setData(bundle);
            } finally {
                this.handler.sendMessage(message);
            }
        }
    }

    //回调函数，显示结果
    Handler getLastVersionHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");

            switch (type) {
            case 1: {
                String lastVersion = msg.getData().getString("msg");
                if (version.compareTo(lastVersion) < 0) {
                    processUpdate();
                }
                break;
            }
            case 0: {
                String mss = msg.getData().getString("msg");
                MobLogAction.getMobLogAction().mobLogError("自动更新", mss);
                break;
            }
            default: {

            }
            }
        }
    };

    private void processUpdate() {
        String ip = sharedPreferences.getString("setting_http_ip", WebUtils.HOST);
        String uriPath = "http://" + ip + "/common/downloadFile?type=mobile";
        update(uriPath);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("setting_http_ip")) {
            ip = sharedPreferences.getString(key, WebUtils.HOST);
            host_info.loadUrl(WebUtils.HTTP + ip + WebUtils.NEWS);
        }
    }

}
