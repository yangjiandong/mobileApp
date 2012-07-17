package com.ek.mobileapp.nurse.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.UtilString;

public class VitalSign extends Activity {
    Map<String, Integer> btns = new HashMap<String, Integer>();
    Map<String, Integer> btnsStyle = new HashMap<String, Integer>();
    Map<Integer, String> moduels = new HashMap<Integer, String>();

    //OnClickListener handler;
    SharedPreferences sharedPreferences;

    EditText patientId;
    TextView name;
    TextView sex;
    TextView age;
    TextView bedNo;
    TextView doctor;
    EditText busDate;
    Spinner timePoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createBtns();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        patientId = (EditText) findViewById(R.id.vitalsign_patientId);
        name = (TextView) findViewById(R.id.vitalsign_name);
        sex = (TextView) findViewById(R.id.vitalsign_sex);
        age = (TextView) findViewById(R.id.vitalsign_age);
        bedNo = (TextView) findViewById(R.id.vitalsign_bedNo);
        doctor = (TextView) findViewById(R.id.vitalsign_doctor);
        busDate = (EditText) findViewById(R.id.vitalsign_busDate);
        timePoint = (Spinner) findViewById(R.id.vitalsign_timePoint);

        try {

            //一排四个按钮
            int count = 4;
            LinearLayout modules = (LinearLayout) findViewById(R.id.vitalsign_layout1);

            //
            UserDTO user = GlobalCache.getCache().getLoginuser();
            String alls = user.getMobmodules();
            List<String> allIds = UtilString.stringToArrayList(alls, ",");
            int i = 0;
            LinearLayout one = null;
            Button[] bns = new Button[count];
            for (String oneModule : allIds) {
                StringTokenizer filter = new StringTokenizer(oneModule, "|");
                String code = filter.nextToken();
                String module = filter.nextToken();

                if (i == count) {
                    //ViewUtils.putViewsInLine(bns, getScreenWidth(), 0.1);
                    i = 0;
                    bns = new Button[count];
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

                Button bn = new Button(this, null, R.style.MButton);
                bn.setId(btns.get(code));
                bn.setCompoundDrawablesWithIntrinsicBounds(0, this.btnsStyle.get(code), 0, 0);
                bn.setText(module);
                //TODO 硬编码尺寸
                bn.setPadding(20, 10, 20, 0);
                //后台日志显示用
                moduels.put(bn.getId(), module);

                //向Layout容器中添加按钮
                one.addView(bn);//, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

                //为按钮绑定一个事件监听器
                // bn.setOnClickListener(new ClickEvent());
                bns[i] = bn;

                i++;
            }

            if (i > 0) {
                //ViewUtils.putViewsInLine(bns, getScreenWidth(), 0.1);
            }
        } catch (Exception e) {
            Log.e("", e.getMessage());
            MobLogAction.mobLogError("构建生命体征界面", e.getMessage());
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
        btnsStyle.put("02", R.drawable.doctor_button);
        btnsStyle.put("03", R.drawable.doctor_button);
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

    //    class ClickEvent implements View.OnClickListener {
    //
    //        public void onClick(View v) {
    //            LogonAction.userLog(moduels.get(v.getId()), ip);
    //
    //            switch (v.getId()) {
    //            case R.id.m01: // doStuff
    //
    //                Intent intent = new Intent(VitalSign.this, QueryActivity.class);
    //                startActivity(intent);
    //                break;
    //            case R.id.m02: // doStuff
    //                Intent intent2 = new Intent(VitalSign.this, InputDemoActivtiy.class);
    //                startActivity(intent2);
    //                break;
    //            case R.id.m03: // doStuff
    //                actionBar.setTitle("03");
    //                break;
    //            case R.id.m04: // doStuff
    //                actionBar.setTitle("04");
    //                break;
    //            case R.id.m05: // doStuff
    //                actionBar.setTitle("05");
    //                break;
    //
    //            default:
    //                break;
    //            }
    //
    //        }
    //    }
}
