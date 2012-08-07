package com.ek.mobileapp.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ek.mobileapp.MainApplication;
import com.ek.mobileapp.R;
import com.ek.mobileapp.action.LogonAction;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.example.InOutTitlesTriangle;
import com.ek.mobileapp.example.SampleTitlesTriangle;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.nurse.activity.DrugCheck;
import com.ek.mobileapp.nurse.activity.VitalSign;
import com.ek.mobileapp.query.activity.QueryActivity;
import com.ek.mobileapp.service.BluetoothService;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.SettingsUtils;
import com.ek.mobileapp.utils.ToastUtils;
import com.ek.mobileapp.utils.UtilString;
import com.ek.mobileapp.utils.WebUtils;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

//主界面
public class MainActivity extends BaseActivity {
    Map<String, Integer> btns = new HashMap<String, Integer>();
    Map<String, Integer> btnsStyle = new HashMap<String, Integer>();
    Map<Integer, String> moduels = new HashMap<Integer, String>();

    ActionBar actionBar;
    SharedPreferences sharedPreferences;
    String ip = "";

    @SuppressWarnings("unchecked")
    @Override
    protected void createUi() {
        MainApplication.getInstance().addActivity(this);

        //统一取数
        Runnable runLog = new Runnable() {
            public void run() { //
                //
                VitalSignAction.getTimePoint();
                VitalSignAction.getItem("");
                VitalSignAction.getSkinTest();
                VitalSignAction.getMeasureType();
            }
        };
        new Thread(runLog).start();

        createBtns();
        setContentView(R.layout.main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ip = sharedPreferences.getString("setting_http_ip", WebUtils.HOST);

        actionBar = (ActionBar) findViewById(R.id.actionbar);
        try {
            //
            final Action otherAction = new IntentAction(this, new Intent(this, SettingActivity.class),
                    R.drawable.ic_title_export_default);
            actionBar.addAction(otherAction);
        } catch (Exception e) {
            Log.e("about", e.getMessage());
        }
        String version = "x.xx";
        String vendor = "鑫亿";

        try {
            PackageInfo pinfo = this.getPackageManager().getPackageInfo(SettingsUtils.TRACKER_PACKAGE_NAME, 0);
            version = pinfo.versionCode + "_" + pinfo.versionName;
            actionBar.setTitle(pinfo.applicationInfo.labelRes);

            //一排三个按钮
            int count = 3;
            LinearLayout modules = (LinearLayout) findViewById(R.id.modules);

            UserDTO user = GlobalCache.getCache().getLoginuser();
            String alls = user.getMobmodules();
            List<String> allIds = UtilString.stringToArrayList(alls, ",");
            int i = 0;
            LinearLayout one = null;
            for (String oneModule : allIds) {
                StringTokenizer filter = new StringTokenizer(oneModule, "|");
                String code = filter.nextToken();
                String module = filter.nextToken();

                if (i == count) {
                    i = 0;
                }

                if (i == 0) {
                    one = new LinearLayout(this);
                    //one.set
                    one.setOrientation(LinearLayout.HORIZONTAL);
                    one.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    //buttnon 居中
                    one.setGravity(Gravity.CENTER);
                    modules.addView(one);
                }

                Button bn = new Button(this, null, R.style.MButton);//android.R.attr.buttonStyleSmall);
                bn.setId(btns.get(code));
                bn.setCompoundDrawablesWithIntrinsicBounds(0, this.btnsStyle.get(code), 0, 0);
                bn.setText(module);
                bn.setPadding(20, 20, 20, 20);
                bn.setTextAppearance(this, R.style.MButton);//Typeface.BOLD_ITALIC);//);
                //bn.setBackgroundResource(R.color.white);
                bn.setGravity(Gravity.CENTER_HORIZONTAL);//R.style.MButton);
                //后台日志显示用
                moduels.put(bn.getId(), module);

                //向Layout容器中添加按钮
                one.addView(bn);//, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

                //为按钮绑定一个事件监听器
                bn.setOnClickListener(new ClickEvent());

                i++;
            }

        } catch (Exception e) {
            MobLogAction.getMobLogAction().mobLogError("main", e.getMessage());
        }

    }

    private void createBtns() {
        btns.put("01", R.id.m01);
        btns.put("02", R.id.m02);
        btns.put("03", R.id.m03);
        btns.put("04", R.id.m04);
        btns.put("05", R.id.m05);
        btns.put("06", R.id.m06);
        //
        btnsStyle.put("01", R.drawable.hospital_button);
        btnsStyle.put("02", R.drawable.heart_button);
        btnsStyle.put("03", R.drawable.nurse_button);
        btnsStyle.put("04", R.drawable.doctor_button);
        btnsStyle.put("05", R.drawable.doctor_button);
        btnsStyle.put("06", R.drawable.doctor_button);
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
                MainApplication.getInstance().exit();
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

    class ClickEvent implements View.OnClickListener {

        public void onClick(View v) {
            final View v2 = v;
            Runnable runLog = new Runnable() {
                public void run() { //
                    LogonAction.userLog(moduels.get(v2.getId()), ip);
                }
            };
            new Thread(runLog).start();
            Intent intent;
            switch (v.getId()) {
            case R.id.m01: // doStuff

                intent = new Intent(MainActivity.this, QueryActivity.class);
                startActivity(intent);
                break;
            case R.id.m02: // doStuff
                intent = new Intent(MainActivity.this, VitalSign.class);
                startActivity(intent);
                break;
            case R.id.m03: // doStuff
                intent = new Intent(MainActivity.this, DrugCheck.class);
                startActivity(intent);
                break;
            case R.id.m04: // doStuff
                //actionBar.setTitle("04");
                intent = new Intent(MainActivity.this, SampleTitlesTriangle.class);
                startActivity(intent);
                break;
            case R.id.m05: // doStuff
                //actionBar.setTitle("05");
                intent = new Intent(MainActivity.this, InOutTitlesTriangle.class);
                startActivity(intent);
                break;

            default:
                break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_select_bluetooth:
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, BluetoothService.REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.menu_update_password:
            Intent intent = new Intent(this, UserUpdatePwdActivity.class);
            startActivity(intent);
            return true;
        case R.id.menu_about:
            showAbout();
            return true;
        }
        return false;
    }

    private void showAbout() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_about, null);

        try {
            PackageInfo pinfo = this.getPackageManager().getPackageInfo(SettingsUtils.TRACKER_PACKAGE_NAME, 0);
            String versionName = pinfo.versionCode + "_" + pinfo.versionName;
            String version = String.format(getString(R.string.app_version), versionName);
            final TextView versionTextView = (TextView) layout.findViewById(R.id.dialogabout_appversion);
            versionTextView.setText(version);
        } catch (NameNotFoundException e) {
            // No version found, not critical..
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setTitle("关于...");
        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case BluetoothService.REQUEST_CONNECT_DEVICE:
            // 此处只完成设定蓝牙设备,连接功能由实际activity实现
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                String name = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);

                Editor edit = sharedPreferences.edit();
                edit.putString("setting_bluetooth_scanner", name);
                edit.putString("setting_bluetooth_scanner_address", address);
                edit.commit();

                ToastUtils.show(this, "当前蓝牙设备为" + name);
            }
            break;
        }

    }
}
