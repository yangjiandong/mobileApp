package com.ek.mobileapp.nurse.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.InputType;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

//维护生命体征,按时间点
public class VitalSignEdit extends VitalSignBase {
    List<MeasureType> list = new ArrayList<MeasureType>();
    Map<Integer, Integer> btns = new HashMap<Integer, Integer>();
    Button B1, B2, B3, B4, B5, B6, B7, B8, B9, B0, BClear, BDot, BEqual, BClose;

    RadioGroup measures;
    RadioGroup measures2;

    TextView t_label1, t_label2, t_label3, t_label4;
    EditText e_text1, e_text2, e_text3, e_text4;
    boolean textCursor1 = false;
    boolean textCursor2 = false;
    boolean textCursor3 = false;
    boolean textCursor4 = false;
    boolean dotPressed1 = false;
    boolean dotPressed2 = false;
    boolean dotPressed3 = false;
    boolean dotPressed4 = false;
    boolean syntaxError = false;
    private static Long measureTypeCode = 0L;

    VitalSignData data1, data2, data3, data4;

    private int touchNo = 1;

    private String itemCode1 = "", itemCode2 = "", itemCode3 = "", itemCode4 = "";

    @Override
    public void initBase() {
        setContentView(R.layout.vitalsignedit);

    }

    @Override
    protected String getItemCode() {
        return itemCode1;
    }

    @Override
    public void refreshOther() {
        measures.clearCheck();
        measures2.clearCheck();

        getVitalSingData();

        if (!UtilString.isBlank(data1.getMeasureTypeCode())) {

            if (Integer.valueOf(data1.getMeasureTypeCode()) < 5) {
                measures.check(Integer.valueOf(data1.getMeasureTypeCode()));
            } else {
                measures2.check(Integer.valueOf(data1.getMeasureTypeCode()));
            }
        }

    }

    private void getVitalSingData() {
        data1 = getData(itemCode1);
        data2 = getData(itemCode2);
        data3 = getData(itemCode3);
        data4 = getData(itemCode4);

        if (data1 != null) {
            if (data1.getValue1() != null && data1.getValue1().length() > 0)
                e_text1.setText(data1.getValue1());
        } else {
            data1 = new VitalSignData();
            data1.setAddDate(GlobalCache.getCache().getBusDate());
            data1.setPatientId(GlobalCache.getCache().getCurrentPatient().getPatientId());
            data1.setTimePoint(GlobalCache.getCache().getTimePoint());
            data1.setItemCode(itemCode1);
            data1.setItemName(getItemName(itemCode1));
            data1.setUserId(GlobalCache.getCache().getLoginuser().getId());
            data1.setValue1("");
        }
        if (data2 != null) {
            if (data2.getValue1() != null && data2.getValue1().length() > 0)
                e_text2.setText(data2.getValue1());
        } else {
            data2 = new VitalSignData();
            data2.setAddDate(GlobalCache.getCache().getBusDate());
            data2.setPatientId(GlobalCache.getCache().getCurrentPatient().getPatientId());
            data2.setTimePoint(GlobalCache.getCache().getTimePoint());
            data2.setItemCode(itemCode2);
            data2.setItemName(getItemName(itemCode2));
            data2.setUserId(GlobalCache.getCache().getLoginuser().getId());
            data2.setValue1("");
        }
        if (data3 != null) {
            if (data3.getValue1() != null && data3.getValue1().length() > 0)
                e_text3.setText(data3.getValue1());
        } else {
            data3 = new VitalSignData();
            data3.setAddDate(GlobalCache.getCache().getBusDate());
            data3.setPatientId(GlobalCache.getCache().getCurrentPatient().getPatientId());
            data3.setTimePoint(GlobalCache.getCache().getTimePoint());
            data3.setItemCode(itemCode3);
            data3.setItemName(getItemName(itemCode3));
            data3.setUserId(GlobalCache.getCache().getLoginuser().getId());
            data3.setValue1("");
        }
        if (data4 != null) {
            if (data4.getValue1() != null && data4.getValue1().length() > 0)
                e_text4.setText(data4.getValue1());
        } else {
            data4 = new VitalSignData();
            data4.setAddDate(GlobalCache.getCache().getBusDate());
            data4.setPatientId(GlobalCache.getCache().getCurrentPatient().getPatientId());
            data4.setTimePoint(GlobalCache.getCache().getTimePoint());
            data4.setItemCode(itemCode4);
            data4.setItemName(getItemName(itemCode4));
            data4.setUserId(GlobalCache.getCache().getLoginuser().getId());
            data4.setValue1("");
        }

    }

    @Override
    protected void showUi() {

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        String itemName1 = "", itemName2 = "", itemName3 = "", itemName4 = "";
        itemCode1 = "01";
        itemCode2 = "02";
        itemCode3 = "03";
        itemCode4 = "04";
        itemName1 = "体温(℃)";
        itemName2 = "脉搏(次)";
        itemName3 = "呼吸(次)";
        itemName4 = "物理降温(次)";

        try {
            TableLayout inputkey = (TableLayout) inflater.inflate(R.layout.inputkey, null);
            LinearLayout layout = (LinearLayout) findViewById(R.id.vitalsign_edit_inputkey);
            layout.addView(inputkey);
            initLayout();
            t_label1.setText(itemName1);
            t_label2.setText(itemName2);
            t_label3.setText(itemName3);
            t_label4.setText(itemName4);
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
                mVibrator.vibrate(vibrationDuration);
                if (touchNo == 1 && dotPressed1)
                    return;
                if (touchNo == 1 && dotPressed1)
                    return;
                if (touchNo == 1 && dotPressed1)
                    return;
                if (touchNo == 1 && dotPressed1)
                    return;

                syntaxError = false;
                //有可能原有数据已有小数点
                if (touchNo == 1 && !syntaxError) {
                    StringBuffer sb = new StringBuffer(e_text1.getText().toString().trim());
                    sb.append(".");
                    try {
                        double d = Double.valueOf(sb.toString());
                    } catch (Exception e) {
                        syntaxError = true;
                    }
                }
                if (touchNo == 2 && !syntaxError) {
                    StringBuffer sb = new StringBuffer(e_text2.getText().toString().trim());
                    sb.append(".");
                    try {
                        double d = Double.valueOf(sb.toString());
                    } catch (Exception e) {
                        syntaxError = true;
                    }
                }
                if (touchNo == 3 && !syntaxError) {
                    StringBuffer sb = new StringBuffer(e_text3.getText().toString().trim());
                    sb.append(".");
                    try {
                        double d = Double.valueOf(sb.toString());
                    } catch (Exception e) {
                        syntaxError = true;
                    }
                }
                if (touchNo == 4 && !syntaxError) {
                    StringBuffer sb = new StringBuffer(e_text4.getText().toString().trim());
                    sb.append(".");
                    try {
                        double d = Double.valueOf(sb.toString());
                    } catch (Exception e) {
                        syntaxError = true;
                    }
                }
                if (!syntaxError) {
                    if (touchNo == 1) {
                        NumPressed(".");
                        dotPressed1 = true;
                    } else if (touchNo == 2) {
                        NumPressed(".");
                        dotPressed2 = true;
                    } else if (touchNo == 3) {
                        NumPressed(".");
                        dotPressed3 = true;
                    } else if (touchNo == 4) {
                        NumPressed(".");
                        dotPressed4 = true;
                    }
                }
            }
        };
        OnClickListener myListenerBSave = new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
                data1.setValue1(e_text1.getText().toString().trim());
                data1.setMeasureTypeCode(measureTypeCode.toString());
                data2.setValue1(e_text2.getText().toString().trim());
                data2.setMeasureTypeCode(measureTypeCode.toString());
                data3.setValue1(e_text3.getText().toString().trim());
                data3.setMeasureTypeCode(measureTypeCode.toString());
                data4.setValue1(e_text4.getText().toString().trim());
                data4.setMeasureTypeCode(measureTypeCode.toString());
                processSaveData();
            }
        };
        OnClickListener myListenerBClose = new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
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

        measures = (RadioGroup) findViewById(R.id.vitalsign_edit_measures);
        measures2 = (RadioGroup) findViewById(R.id.vitalsign_edit_measures2);

        try {

            getVitalSingData();

            list = GlobalCache.getCache().getMeasureTypes();
            int count = 4;
            int i = 0;

            for (MeasureType a : list) {
                RadioButton rb = new RadioButton(this, null);

                if (data1 != null) {
                    if (a.getCode().equals(data1.getMeasureTypeCode()))
                        rb.setChecked(true);
                }

                if (i < count) {
                    rb.setId(a.getId().intValue());
                    rb.setText(a.getName());
                    //rb.setTextColor(getResources().getColor(R.color.black));
                    rb.setOnClickListener(new ClickEvent());
                    measures.addView(rb);
                } else {
                    rb.setId(a.getId().intValue());
                    rb.setText(a.getName());
                    //rb.setTextColor(getResources().getColor(R.color.black));
                    rb.setOnClickListener(new ClickEvent());
                    measures2.addView(rb);
                }

                i++;
            }

        } catch (Exception e) {

            MobLogAction.getMobLogAction().mobLogError("构建生命体征界面", e.getMessage());
        }

    }

    class ClickEvent implements View.OnClickListener {

        public void onClick(View v) {
            measureTypeCode = Long.valueOf(v.getId());
            if (v.getId() < 5) {
                measures2.clearCheck();
            } else {
                measures.clearCheck();
            }

        }
    }

    private void ClearAll() {
        syntaxError = false;
        if (touchNo == 1) {
            dotPressed1 = false;
            e_text1.setText("");
        } else if (touchNo == 2) {
            dotPressed2 = false;
            e_text2.setText("");
        } else if (touchNo == 3) {
            dotPressed3 = false;
            e_text3.setText("");
        } else if (touchNo == 4) {
            dotPressed4 = false;
            e_text4.setText("");
        }

    }

    private void NumPressed(String key) {

        if (touchNo == 1) {
            //设置一个变量判断是否有光标
            if (textCursor1 == true) {
                //获得光标的位置
                int index = e_text1.getSelectionStart();
                //将字符串转换为StringBuffer
                StringBuffer sb = new StringBuffer(e_text1.getText().toString().trim());
                //将字符插入光标所在的位置
                sb = sb.insert(index, key);
                e_text1.setText(sb.toString());
                //设置光标的位置保持不变
                Selection.setSelection(e_text1.getText(), index + 1);
            } else {
                e_text1.setText(e_text1.getText().toString().trim() + key);
            }
        } else if (touchNo == 2) {
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
        } else if (touchNo == 3) {
            //设置一个变量判断是否有光标
            if (textCursor3 == true) {
                //获得光标的位置
                int index = e_text3.getSelectionStart();
                //将字符串转换为StringBuffer
                StringBuffer sb = new StringBuffer(e_text3.getText().toString().trim());
                //将字符插入光标所在的位置
                sb = sb.insert(index, key);
                e_text3.setText(sb.toString());
                //设置光标的位置保持不变
                Selection.setSelection(e_text3.getText(), index + 1);
            } else {
                e_text3.setText(e_text3.getText().toString().trim() + key);
            }
        } else if (touchNo == 4) {
            //设置一个变量判断是否有光标
            if (textCursor4 == true) {
                //获得光标的位置
                int index = e_text4.getSelectionStart();
                //将字符串转换为StringBuffer
                StringBuffer sb = new StringBuffer(e_text4.getText().toString().trim());
                //将字符插入光标所在的位置
                sb = sb.insert(index, key);
                e_text4.setText(sb.toString());
                //设置光标的位置保持不变
                Selection.setSelection(e_text4.getText(), index + 1);
            } else {
                e_text4.setText(e_text4.getText().toString().trim() + key);
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

        e_text1 = (EditText) findViewById(R.id.vitalsign_edit_text1);
        t_label1 = (TextView) findViewById(R.id.vitalsign_edit_label1);
        e_text2 = (EditText) findViewById(R.id.vitalsign_edit_text2);
        t_label2 = (TextView) findViewById(R.id.vitalsign_edit_label2);
        e_text3 = (EditText) findViewById(R.id.vitalsign_edit_text3);
        t_label3 = (TextView) findViewById(R.id.vitalsign_edit_label3);
        e_text4 = (EditText) findViewById(R.id.vitalsign_edit_text4);
        t_label4 = (TextView) findViewById(R.id.vitalsign_edit_label4);

        e_text1.setInputType(InputType.TYPE_NULL); // 关闭软键盘
        e_text2.setInputType(InputType.TYPE_NULL); // 关闭软键盘
        e_text3.setInputType(InputType.TYPE_NULL); // 关闭软键盘
        e_text4.setInputType(InputType.TYPE_NULL); // 关闭软键盘

        e_text1.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchNo = 1;
                }
                return false;
            }
        });
        e_text2.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchNo = 2;
                }
                return false;
            }
        });
        e_text3.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchNo = 3;
                }
                return false;
            }
        });
        e_text4.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchNo = 4;
                }
                return false;
            }
        });
        e_text1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (e_text1.getText().toString().trim().length() > 0) {
                    //设置光标为可见状态
                    e_text1.setCursorVisible(true);
                    textCursor1 = true;
                }
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
        e_text3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (e_text3.getText().toString().trim().length() > 0) {
                    //设置光标为可见状态
                    e_text3.setCursorVisible(true);
                    textCursor3 = true;
                }
            }
        });
        e_text4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (e_text4.getText().toString().trim().length() > 0) {
                    //设置光标为可见状态
                    e_text4.setCursorVisible(true);
                    textCursor4 = true;
                }
            }
        });
    }

    //开始处理取数
    private void processSaveData() {
        showProcessingImage(R.id.loadingImageView);

        SaveVitalSignData saveData = new SaveVitalSignData(saveDataHandler);
        Thread thread = new Thread(saveData);
        thread.start();
    }

    //回调函数，显示结果
    Handler saveDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            stopAnimation(R.id.loadingImageView);
            String message = msg.getData().getString("msg");
            int type = msg.getData().getInt("type");
            switch (type) {
            case 1: {
                showMessage(message);
                break;
            }
            case 0: {
                showMessage(message);
                break;
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
                GlobalCache.getCache().setVitalSignData(data1);
                String ret1 = VitalSignAction.saveVitalSign();
                if (ret1.equals("1")) {
                    GlobalCache.getCache().setVitalSignData(data2);
                    String ret2 = VitalSignAction.saveVitalSign();
                    if (ret2.equals("1")) {
                        GlobalCache.getCache().setVitalSignData(data3);
                        String ret3 = VitalSignAction.saveVitalSign();
                        if (ret3.equals("1")) {
                            GlobalCache.getCache().setVitalSignData(data4);
                            String ret4 = VitalSignAction.saveVitalSign();
                            if (ret4.equals("1")) {
                                String ret = VitalSignAction.commitHis();
                                if (ret.equals("1")) {

                                    Bundle bundle = new Bundle();
                                    bundle.putInt("type", 1);
                                    bundle.putString("msg", "保存成功");
                                    message.setData(bundle);
                                } else {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("type", 0);
                                    bundle.putString("msg", ret);
                                    message.setData(bundle);
                                }
                            } else {
                                Bundle bundle = new Bundle();
                                bundle.putInt("type", 0);
                                bundle.putString("msg", ret4);
                                message.setData(bundle);
                            }
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putInt("type", 0);
                            bundle.putString("msg", ret3);
                            message.setData(bundle);
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 0);
                        bundle.putString("msg", ret2);
                        message.setData(bundle);
                    }

                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    bundle.putString("msg", ret1);
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
