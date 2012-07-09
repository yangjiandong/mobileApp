package com.ek.mobileapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ek.mobileapp.R;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.SettingsUtils;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
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
            version = pinfo.versionCode + "." + pinfo.versionName;
            actionBar.setTitle(pinfo.applicationInfo.labelRes);

            //
            UserDTO user = GlobalCache.getCache().getLoginuser();
            //String roles = user.getRoles();
            //List<String> allIds = UtilString.stringToArrayList(roles, ",");

            LinearLayout modules = (LinearLayout) findViewById(R.id.modules);
            LinearLayout one = new LinearLayout(this);

            one.setOrientation(LinearLayout.HORIZONTAL);
            one.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            modules.addView(one);

            Button bn = new Button(this);//, null, R.style.H01Button
            bn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.speaker_button, 0, 0);
            bn.setText("全院概括");

            //向Layout容器中添加按钮
            one.addView(bn);
            //为按钮绑定一个事件监听器
            bn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, QueryActivity.class);
                    startActivity(intent);
                }
            });

            Button bn2 = new Button(this, null, R.style.H01Button);
            bn2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.speaker_button, 0, 0);

            bn2.setText("全院概括2");

            //向Layout容器中添加按钮
            one.addView(bn2);
            //为按钮绑定一个事件监听器
            bn2.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {

                }
            });

            //
            LinearLayout one2 = new LinearLayout(this);

            one2.setOrientation(LinearLayout.HORIZONTAL);
            one2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            modules.addView(one2);

            Button bn3 = new Button(this);//, null, R.style.H01Button);
            bn3.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.speaker_button, 0, 0);
            bn3.setText("全院概括");

            //向Layout容器中添加按钮
            one2.addView(bn3);
            //为按钮绑定一个事件监听器
            bn3.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    //Intent intent = new Intent(MainActivity.this, QueryActivity.class);
                    //startActivity(intent);
                }
            });

            Button bn4 = new Button(this, null, R.style.H01Button);
            bn4.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.speaker_button, 0, 0);

            bn4.setText("全院概括4");

            //向Layout容器中添加按钮
            one2.addView(bn4);
            //为按钮绑定一个事件监听器
            bn4.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {

                }
            });
        } catch (NameNotFoundException e) {
            Log.e("", e.getMessage());
            //e.printStackTrace();
        }
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
}
