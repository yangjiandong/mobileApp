package com.ek.mobileapp.nurse.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.activity.InputDemoActivtiy;
import com.ek.mobileapp.model.MeasureType;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.utils.GlobalCache;

//大便
//一次录入2个指标
public class VitalSignEdit5 extends VitalSignBase {
    List<MeasureType> list = new ArrayList<MeasureType>();
    Map<Integer, String> btns = new HashMap<Integer, String>();
    Button B1, B2, B3, B4, B5, B6, B7, B8, B9, B0, BClear, BSave, BClose, BBack;
    Button BLeft, BRight, BStar, BSlash, BE;

    TextView t_label;
    EditText e_text;
    boolean textCursor = false;
    boolean syntaxError = false;
    SharedPreferences sharedPreferences;

    private String itemCode = "";

    @Override
    public void initBase() {
        setContentView(R.layout.vitalsignedit_5);

    }

    @Override
    protected String getItemCode() {
        return itemCode;
    }

    private void getVitalSignData() {
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

        if (data != null) {
            if (data.getValue2() != null && data.getValue2().length() > 0 && data.getItemCode().equals(itemCode)) {
                e_text.setText(data.getValue2());
            } else {
                e_text.setText("");
            }
        }
        GlobalCache.getCache().setVitalSignData(data);

        e_text.requestFocus(1);
        Selection.setSelection(e_text.getText(), e_text.getText().toString().trim().length());
    }

    @Override
    public void refreshOther() {
        getVitalSignData();
    }

    @Override
    protected void showUi() {

        Intent intent = getIntent();
        itemCode = intent.getStringExtra("code");
        String itemName = intent.getStringExtra("name");

        t_label = (TextView) findViewById(R.id.vitalsign_edit_5_label);

        initLayout();
        //振动器
        final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final int vibrationDuration = 33;

        OnClickListener myListenerNum = new OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!syntaxError) {
                        mVibrator.vibrate(vibrationDuration);
                        String n = btns.get(v.getId());
                        NumPressed(n);
                    }
                } catch (Exception e) {
                    MobLogAction.getMobLogAction().mobLogError(InputDemoActivtiy.class.getName(), e.getMessage());
                }
            }
        };
        OnClickListener myListenerBClear = new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
                ClearAll();
            }
        };
        OnClickListener myListenerBSave = new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
                GlobalCache.getCache().getVitalSignData().setValue2(e_text.getText().toString().trim());
                processSaveData();

            }
        };
        OnClickListener myListenerBClose = new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
                finish();
            }
        };

        OnClickListener myListenerBBack = new OnClickListener() {
            public void onClick(View v) {

                mVibrator.vibrate(vibrationDuration);

                int i = e_text.getText().toString().trim().length();
                if (i > 0)
                    e_text.setText(e_text.getText().toString().trim().substring(0, i - 1));

                Selection.setSelection(e_text.getText(), i - 1);

            }
        };

        B1.setOnClickListener(myListenerNum);
        B2.setOnClickListener(myListenerNum);
        B3.setOnClickListener(myListenerNum);
        B4.setOnClickListener(myListenerNum);
        B5.setOnClickListener(myListenerNum);
        B6.setOnClickListener(myListenerNum);
        B7.setOnClickListener(myListenerNum);
        B8.setOnClickListener(myListenerNum);
        B9.setOnClickListener(myListenerNum);
        B0.setOnClickListener(myListenerNum);
        BLeft.setOnClickListener(myListenerNum);
        BRight.setOnClickListener(myListenerNum);
        BSlash.setOnClickListener(myListenerNum);
        BStar.setOnClickListener(myListenerNum);
        BE.setOnClickListener(myListenerNum);

        BClear.setOnClickListener(myListenerBClear);
        BClose.setOnClickListener(myListenerBClose);
        BSave.setOnClickListener(myListenerBSave);
        BBack.setOnClickListener(myListenerBBack);

        t_label.setText(itemName);
        try {

            getVitalSignData();

        } catch (Exception e) {
            Log.e("", e.getMessage());
            MobLogAction.getMobLogAction().mobLogError("构建生命体征界面" + itemName, e.getMessage());
        }

    }

    private void ClearAll() {
        e_text.setText("");

    }

    private void NumPressed(String key) {

        //设置一个变量判断是否有光标
        if (textCursor == true) {
            //获得光标的位置
            int index = e_text.getSelectionStart();
            //将字符串转换为StringBuffer
            StringBuffer sb = new StringBuffer(e_text.getText().toString().trim());
            //将字符插入光标所在的位置
            sb = sb.insert(index, key);
            e_text.setText(sb.toString());
            //设置光标的位置
            Selection.setSelection(e_text.getText(), index + 1);
        } else {
            e_text.setText(e_text.getText().toString().trim() + key);
        }

    }

    private void initLayout() {

        btns.put(R.id.vitalsign_edit_5_button_1, "1");
        btns.put(R.id.vitalsign_edit_5_button_2, "2");
        btns.put(R.id.vitalsign_edit_5_button_3, "3");
        btns.put(R.id.vitalsign_edit_5_button_4, "4");
        btns.put(R.id.vitalsign_edit_5_button_5, "5");
        btns.put(R.id.vitalsign_edit_5_button_6, "6");
        btns.put(R.id.vitalsign_edit_5_button_7, "7");
        btns.put(R.id.vitalsign_edit_5_button_8, "8");
        btns.put(R.id.vitalsign_edit_5_button_9, "9");
        btns.put(R.id.vitalsign_edit_5_button_0, "0");
        btns.put(R.id.vitalsign_edit_5_button_left, "(");
        btns.put(R.id.vitalsign_edit_5_button_right, ")");
        btns.put(R.id.vitalsign_edit_5_button_slash, "/");
        btns.put(R.id.vitalsign_edit_5_button_star, "*");
        btns.put(R.id.vitalsign_edit_5_button_E, "E");

        B1 = (Button) findViewById(R.id.vitalsign_edit_5_button_1);
        B2 = (Button) findViewById(R.id.vitalsign_edit_5_button_2);
        B3 = (Button) findViewById(R.id.vitalsign_edit_5_button_3);
        B4 = (Button) findViewById(R.id.vitalsign_edit_5_button_4);
        B5 = (Button) findViewById(R.id.vitalsign_edit_5_button_5);
        B6 = (Button) findViewById(R.id.vitalsign_edit_5_button_6);
        B7 = (Button) findViewById(R.id.vitalsign_edit_5_button_7);
        B8 = (Button) findViewById(R.id.vitalsign_edit_5_button_8);
        B9 = (Button) findViewById(R.id.vitalsign_edit_5_button_9);
        B0 = (Button) findViewById(R.id.vitalsign_edit_5_button_0);
        BLeft = (Button) findViewById(R.id.vitalsign_edit_5_button_left);
        BRight = (Button) findViewById(R.id.vitalsign_edit_5_button_right);
        BSlash = (Button) findViewById(R.id.vitalsign_edit_5_button_slash);
        BStar = (Button) findViewById(R.id.vitalsign_edit_5_button_star);
        BE = (Button) findViewById(R.id.vitalsign_edit_5_button_E);
        BClear = (Button) findViewById(R.id.vitalsign_edit_5_button_clear);
        BClose = (Button) findViewById(R.id.vitalsign_edit_5_button_close);
        BSave = (Button) findViewById(R.id.vitalsign_edit_5_button_save);
        BBack = (Button) findViewById(R.id.vitalsign_edit_5_button_back);

        Button button = (Button) findViewById(R.id.vitalsign_edit_5_button_space);
        button.setVisibility(View.INVISIBLE);

        e_text = (EditText) findViewById(R.id.vitalsign_edit_5_text);
        e_text.setInputType(InputType.TYPE_NULL); // 关闭软键盘

        e_text.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (e_text.getText().toString().trim().length() > 0) {
                    //设置光标为可见状态
                    e_text.setCursorVisible(true);
                    textCursor = true;
                }
            }
        });
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
            String message = msg.getData().getString("msg");

            switch (type) {
            case 1: {
                showMessage(message);
                break;
            }
            case 0: {
                showMessage(message);
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
                        bundle.putString("msg", "保存成功");
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
}
