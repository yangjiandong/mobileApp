package com.ek.mobileapp.nurse.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.SkinTest;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.UtilString;

//皮试1,皮试2
//一次录入一个指标
public class VitalSignEdit4 extends VitalSignBase {

    Spinner s_skintest;
    RadioButton button1;//阴性
    RadioButton button2;//阳性

    Button button_save;
    Button button_close;

    private String[] skinTestStr;
    private ArrayAdapter<String> skinTestAdapter;
    TextView t_label;
    SharedPreferences sharedPreferences;

    private String itemCode = "";

    private String testName = "";

    @Override
    public void initBase() {
        setContentView(R.layout.vitalsignedit_4);

    }

    @Override
    protected String getItemCode() {
        return itemCode;
    }

    @Override
    public void refreshOther() {
        VitalSignData data = GlobalCache.getCache().getVitalSignData();

        if (data != null) {
            if (data.getValue2().contains("阴性")) {
                button1.setChecked(true);
            }
            if (data.getValue2().contains("阳性")) {
                button2.setChecked(true);
            }
        }
        List<SkinTest> st = GlobalCache.getCache().getSkinTests();
        int i = 0;
        int t = 0;
        for (SkinTest a : st) {
            if (data != null && data.getValue2().contains(a.getName())) {
                t = i;
                testName = a.getName();
            }
            i++;
        }

        if (!UtilString.isBlank(testName)) {
            s_skintest.setSelection(t);
        }

    }

    @Override
    protected void showUi() {

        Intent intent = getIntent();
        itemCode = intent.getStringExtra("code");
        String itemName = intent.getStringExtra("name");

        //振动器
        final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final int vibrationDuration = 33;

        button1 = (RadioButton) findViewById(R.id.vitalsign_edit_4_radion1);
        button2 = (RadioButton) findViewById(R.id.vitalsign_edit_4_radion2);

        button_save = (Button) findViewById(R.id.vitalsign_edit_4_button_save);
        button_close = (Button) findViewById(R.id.vitalsign_edit_4_button_close);

        button_save.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
                String skName = "";
                if (button1.isChecked()) {
                    skName = "阴性";
                }
                if (button2.isChecked()) {
                    skName = "阳性";
                }
                if (UtilString.isBlank(skName)) {
                    skName = testName;
                } else {
                    skName = testName + "(" + skName + ")";
                }
                GlobalCache.getCache().getVitalSignData().setValue2(skName);

                processSaveData();
            }
        });
        button_close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
                finish();
            }
        });

        s_skintest = (Spinner) findViewById(R.id.vitalsign_edit_4_spinner);

        t_label = (TextView) findViewById(R.id.vitalsign_edit_4_label);
        t_label.setText(itemName);

        try {

            VitalSignData data = new VitalSignData();
            data.setAddDate(GlobalCache.getCache().getBusDate());
            data.setPatientId(GlobalCache.getCache().getCurrentPatient().getPatientId());
            data.setTimePoint(GlobalCache.getCache().getTimePoint());
            data.setItemCode(itemCode);
            data.setItemName(getItemName(itemCode));
            data.setUserId(GlobalCache.getCache().getLoginuser().getId());
            data.setValue2("");

            List<VitalSignData> lists = GlobalCache.getCache().getVitalSignDatas_all();
            for (VitalSignData a : lists) {
                if (a.getItemCode().equals(itemCode)) {
                    data = a;
                }
            }

            if (data.getValue2().contains("阴性")) {
                button1.setChecked(true);
            }
            if (data.getValue2().contains("阳性")) {
                button2.setChecked(true);
            }

            List<SkinTest> st = GlobalCache.getCache().getSkinTests();
            skinTestStr = new String[st.size()];
            int i = 0;
            int t = 0;
            String str = "";
            for (SkinTest a : st) {
                if (data != null && data.getValue2().contains(a.getName())) {
                    t = i;
                    str = a.getName();
                }
                skinTestStr[i] = a.getName();
                i++;
            }

            skinTestAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, skinTestStr);
            skinTestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s_skintest.setAdapter(skinTestAdapter);
            s_skintest.setPrompt("选择皮试项目:");
            s_skintest.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapter, View view, int selected, long arg3) {
                    String skName = "";
                    if (button1.isChecked()) {
                        skName = "阴性";
                    }
                    if (button2.isChecked()) {
                        skName = "阳性";
                    }
                    testName = skinTestStr[selected];
                    if (UtilString.isBlank(skName)) {
                        skName = skinTestStr[selected];
                    } else {
                        skName = skinTestStr[selected] + "(" + skName + ")";
                    }
                    GlobalCache.getCache().getVitalSignData().setValue2(skName);
                }

                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
            if (!UtilString.isBlank(str)) {
                s_skintest.setSelection(t);
            }

            GlobalCache.getCache().setVitalSignData(data);
        } catch (Exception e) {
            Log.e("", e.getMessage());
            MobLogAction.getMobLogAction().mobLogError("构建生命体征界面" + itemName, e.getMessage());
        }

    }

    //开始处理取数
    private void processSaveData() {

        SaveVitalSignData saveData = new SaveVitalSignData(saveDataHandler);

        Thread thread = new Thread(saveData);
        thread.start();
    }

    //回调函数，显示结果
    Handler saveDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");

            switch (type) {
            case 1: {
                finish();
                break;
            }
            case 0: {
                //error
            }
            default: {

            }
            }
            //super.handleMessage(msg);
        }
    };

    //真正的取数过程
    class SaveVitalSignData implements Runnable {
        Handler handler;

        public SaveVitalSignData(Handler h) {
            this.handler = h;
        }

        public void run() {
            Message message = Message.obtain();
            try {

                //保存生命体征
                String ret = VitalSignAction.saveVitalSign();
                if (ret.equals("1")) {

                    String ret2 = VitalSignAction.commitHis();
                    if (ret2.equals("1")) {

                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 1);
                        bundle.putString("msg", "ok");
                        message.setData(bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 0);
                        bundle.putString("msg", ret2);
                        message.setData(bundle);
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    bundle.putString("msg", ret);
                    message.setData(bundle);
                }

            } catch (Exception e) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                bundle.putString("msg", e.getMessage());
                message.setData(bundle);
            }
            this.handler.sendMessage(message);
        }

    }

    //回调函数，显示结果
    Handler getDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");

            switch (type) {
            case 1: {
                break;
            }
            case 0: {
                //error
            }
            default: {

            }
            }
            //super.handleMessage(msg);
        }
    };

    //真正的取数过程
    class GetVitalSignData implements Runnable {
        Handler handler;

        public GetVitalSignData(Handler h) {
            this.handler = h;
        }

        public void run() {
            Message message = Message.obtain();
            try {

                String busDate = GlobalCache.getCache().getBusDate();
                String timePoint = GlobalCache.getCache().getTimePoint();
                Patient pa = GlobalCache.getCache().getCurrentPatient();
                if (pa != null && busDate != null && timePoint != null && timePoint.length() > 0
                        && busDate.length() > 0)
                    VitalSignAction.getOne(pa.getPatientId(), busDate, timePoint, itemCode);

                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putString("msg", "ok");
                message.setData(bundle);

            } catch (Exception e) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                bundle.putString("msg", e.getMessage());
                message.setData(bundle);
            }
            this.handler.sendMessage(message);
        }

    }
}
