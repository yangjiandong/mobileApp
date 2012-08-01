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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.activity.InputDemoActivtiy;
import com.ek.mobileapp.model.MeasureType;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.UtilString;

//入量,尿量,身高,体重指标
//一次录入一个指标
public class VitalSignEdit2 extends VitalSignBase {
    List<MeasureType> list = new ArrayList<MeasureType>();
    Map<Integer, Integer> btns = new HashMap<Integer, Integer>();
    Button B1, B2, B3, B4, B5, B6, B7, B8, B9, B0, BClear, BDot, BEqual, BClose;

    TextView t_label;
    EditText e_text;
    boolean textCursor = false;
    boolean dotPressed = false;
    boolean syntaxError = false;
    SharedPreferences sharedPreferences;

    private String itemCode = "";

    @Override
    public void initBase() {
        setContentView(R.layout.vitalsignedit_2);

    }

    @Override
    protected String getItemCode() {
        return itemCode;
    }

    @Override
    public void refreshOther() {
        VitalSignData data = GlobalCache.getCache().getVitalSignData();

        if (data != null) {
            if (!UtilString.isBlank(data.getValue2()) && data.getItemCode().equals(itemCode)) {
                e_text.setText(data.getValue2());
            } else {
                e_text.setText("");
            }

        }

    }

    @Override
    protected void showUi() {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Intent intent = getIntent();
        itemCode = intent.getStringExtra("code");
        String itemName = intent.getStringExtra("name");

        try {
            TableLayout inputkey = (TableLayout) inflater.inflate(R.layout.inputkey, null);
            LinearLayout layout = (LinearLayout) findViewById(R.id.vitalsign_edit_2_inputkey);
            layout.addView(inputkey);
            initLayout();
        } catch (Exception e) {
            MobLogAction.getMobLogAction().mobLogError("inputkey", e.getMessage());
        }

        //振动器
        final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final int vibrationDuration = 33;

        OnClickListener myListenerNum = new OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!syntaxError) {
                        mVibrator.vibrate(vibrationDuration);
                        Integer n = btns.get(v.getId());
                        NumPressed(n.toString());
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
        OnClickListener myListenerBDot = new OnClickListener() {
            public void onClick(View v) {
                if (dotPressed)
                    return;

                if (!syntaxError) {

                    NumPressed(".");
                    dotPressed = true;
                }
            }
        };
        OnClickListener myListenerBSave = new OnClickListener() {
            public void onClick(View v) {
                GlobalCache.getCache().getVitalSignData().setValue2(e_text.getText().toString().trim());
                processSaveData();
            }
        };
        OnClickListener myListenerBClose = new OnClickListener() {
            public void onClick(View v) {
                finish();
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
        BDot.setOnClickListener(myListenerBDot);
        BClear.setOnClickListener(myListenerBClear);
        BEqual.setOnClickListener(myListenerBSave);
        BClose.setOnClickListener(myListenerBClose);

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

            if (data != null) {
                if (data.getValue2() != null && data.getValue2().length() > 0 && data.getItemCode().equals(itemCode))
                    e_text.setText(data.getValue2());
            }

            GlobalCache.getCache().setVitalSignData(data);

        } catch (Exception e) {
            Log.e("", e.getMessage());
            MobLogAction.getMobLogAction().mobLogError("构建生命体征界面" + itemName, e.getMessage());
        }

    }

    private void ClearAll() {
        dotPressed = false;
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
            //设置光标的位置保持不变
            Selection.setSelection(e_text.getText(), index + 1);
        } else {
            e_text.setText(e_text.getText().toString().trim() + key);
        }

    }

    private void initLayout() {

        btns.put(R.id.button1, 1);
        btns.put(R.id.button2, 2);
        btns.put(R.id.button3, 3);
        btns.put(R.id.button4, 4);
        btns.put(R.id.button5, 5);
        btns.put(R.id.button6, 6);
        btns.put(R.id.button7, 7);
        btns.put(R.id.button8, 8);
        btns.put(R.id.button9, 9);
        btns.put(R.id.button0, 0);

        B1 = (Button) findViewById(R.id.button1);
        B2 = (Button) findViewById(R.id.button2);
        B3 = (Button) findViewById(R.id.button3);
        B4 = (Button) findViewById(R.id.button4);
        B5 = (Button) findViewById(R.id.button5);
        B6 = (Button) findViewById(R.id.button6);
        B7 = (Button) findViewById(R.id.button7);
        B8 = (Button) findViewById(R.id.button8);
        B9 = (Button) findViewById(R.id.button9);
        B0 = (Button) findViewById(R.id.button0);
        BClear = (Button) findViewById(R.id.buttonClear);
        BDot = (Button) findViewById(R.id.buttonDot);
        BEqual = (Button) findViewById(R.id.buttonEq);
        BClose = (Button) findViewById(R.id.buttonClose);

        e_text = (EditText) findViewById(R.id.vitalsign_edit_2_text);
        e_text.setInputType(InputType.TYPE_NULL); // 关闭软键盘
        t_label = (TextView) findViewById(R.id.vitalsign_edit_2_label);

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
