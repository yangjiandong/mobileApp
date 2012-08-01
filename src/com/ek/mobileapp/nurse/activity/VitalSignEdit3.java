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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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

//血压,血压2
//一次录入2个指标
public class VitalSignEdit3 extends VitalSignBase {
    List<MeasureType> list = new ArrayList<MeasureType>();
    Map<Integer, Integer> btns = new HashMap<Integer, Integer>();
    Button B1, B2, B3, B4, B5, B6, B7, B8, B9, B0, BClear, BDot, BEqual, BClose;

    TextView t_label;
    EditText e_text;
    TextView t_label2;
    EditText e_text2;
    boolean textCursor = false;
    boolean textCursor2 = false;
    boolean dotPressed = false;
    boolean dotPressed2 = false;
    boolean syntaxError = false;
    SharedPreferences sharedPreferences;

    private int touchNo = 1;
    private boolean flag = false;

    private String itemCode = "";
    private String itemCode2 = "";

    @Override
    public void initBase() {
        setContentView(R.layout.vitalsignedit_3);

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

        VitalSignData data2 = new VitalSignData();
        data2.setAddDate(GlobalCache.getCache().getBusDate());
        data2.setPatientId(GlobalCache.getCache().getCurrentPatient().getPatientId());
        data2.setTimePoint(GlobalCache.getCache().getTimePoint());
        data2.setItemCode(itemCode2);
        data2.setItemName(getItemName(itemCode2));
        data2.setUserId(GlobalCache.getCache().getLoginuser().getId());
        data2.setValue2("");

        List<VitalSignData> lists = GlobalCache.getCache().getVitalSignDatas_all();
        for (VitalSignData a : lists) {
            if (a.getItemCode().equals(itemCode)) {
                data = a;
            }
            if (a.getItemCode().equals(itemCode2)) {
                data2 = a;
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

        if (data2 != null) {
            if (data2.getValue2() != null && data2.getValue2().length() > 0 && data2.getItemCode().equals(itemCode2)) {
                e_text2.setText(data2.getValue2());
            } else {
                e_text2.setText("");
            }
        }
        GlobalCache.getCache().setVitalSignData2(data2);
        e_text.requestFocus(1);
    }

    @Override
    public void refreshOther() {
        getVitalSignData();
    }

    @Override
    protected void showUi() {

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Intent intent = getIntent();
        itemCode = intent.getStringExtra("code");
        String itemName = intent.getStringExtra("name");

        String itemName2 = "";
        if (itemCode.equals("08") || itemCode.equals("09")) {
            itemCode = "08";
            itemName = "血压Low(mmHg)";
            itemCode2 = "09";
            itemName2 = "血压high(mmHg)";
        }

        if (itemCode.equals("10") || itemCode.equals("11")) {
            itemCode = "10";
            itemName = "血压Low(2)(mmHg)";
            itemCode2 = "11";
            itemName2 = "血压high(2)(mmHg)";
        }

        try {
            TableLayout inputkey = (TableLayout) inflater.inflate(R.layout.inputkey, null);
            LinearLayout layout = (LinearLayout) findViewById(R.id.vitalsign_edit_3_inputkey);
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
                if (touchNo == 1 && dotPressed)
                    return;
                if (touchNo == 2 && dotPressed2)
                    return;

                if (!syntaxError) {
                    if (touchNo == 1) {
                        NumPressed(".");
                        dotPressed = true;
                    } else {
                        NumPressed(".");
                        dotPressed2 = true;
                    }

                }
            }
        };
        OnClickListener myListenerBSave = new OnClickListener() {
            public void onClick(View v) {
                GlobalCache.getCache().getVitalSignData2().setValue2(e_text2.getText().toString().trim());
                GlobalCache.getCache().getVitalSignData().setValue2(e_text.getText().toString().trim());
                flag = false;
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
        t_label2.setText(itemName2);

        try {

            getVitalSignData();

        } catch (Exception e) {
            Log.e("", e.getMessage());
            MobLogAction.getMobLogAction().mobLogError("构建生命体征界面" + itemName, e.getMessage());
        }

    }

    private void ClearAll() {
        dotPressed = false;
        if (touchNo == 1) {
            e_text.setText("");
        } else {
            e_text2.setText("");
        }
    }

    private void NumPressed(String key) {

        if (touchNo == 1) {
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
        } else {
            //设置一个变量判断是否有光标
            if (textCursor2 == true) {
                //获得光标的位置
                int index = e_text2.getSelectionStart();
                //将字符串转换为StringBuffer
                StringBuffer sb = new StringBuffer(e_text2.getText().toString().trim());
                //将字符插入光标所在的位置
                sb = sb.insert(index, key);
                e_text2.setText(sb.toString());
                //设置光标的位置保持不变
                Selection.setSelection(e_text2.getText(), index + 1);
            } else {
                e_text2.setText(e_text2.getText().toString().trim() + key);
            }
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

        e_text = (EditText) findViewById(R.id.vitalsign_edit_3_text);
        e_text.setInputType(InputType.TYPE_NULL); // 关闭软键盘
        t_label = (TextView) findViewById(R.id.vitalsign_edit_3_label);

        e_text.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchNo = 1;
                }
                return false;
            }
        });
        e_text.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (e_text.getText().toString().trim().length() > 0) {
                    //设置光标为可见状态
                    e_text.setCursorVisible(true);
                    textCursor = true;
                }
            }
        });

        e_text2 = (EditText) findViewById(R.id.vitalsign_edit_3_text2);
        e_text2.setInputType(InputType.TYPE_NULL); // 关闭软键盘
        t_label2 = (TextView) findViewById(R.id.vitalsign_edit_3_label2);

        e_text2.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchNo = 2;
                }
                return false;
            }
        });
        e_text2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (e_text2.getText().toString().trim().length() > 0) {
                    //设置光标为可见状态
                    e_text2.setCursorVisible(true);
                    textCursor2 = true;
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
                if (!flag) {
                    GlobalCache.getCache().setVitalSignData(GlobalCache.getCache().getVitalSignData2());
                    flag = true;
                    processSaveData();
                } else {
                    showMessage(message);
                }
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
