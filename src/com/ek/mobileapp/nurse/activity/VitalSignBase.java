package com.ek.mobileapp.nurse.activity;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.model.VitalSignItem;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.utils.BlueToothConnector;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.UtilString;

//生命体征基类
public abstract class VitalSignBase extends NurseBaseActivity {

    TextView t_user_by;
    Button get_patient;
    EditText e_patientId;
    TextView t_name;
    TextView t_sex;
    TextView t_age;
    TextView t_bedNo;
    TextView t_doctor;

    //是否实时显示
    protected boolean realTimeShowData = false;

    protected boolean hasRefreshData = false;

    protected String getItemCode() {
        return "";
    }

    protected String getItemName(String code) {
        String itemName = "";

        List<VitalSignItem> items = GlobalCache.getCache().getVitalSignItems();
        for (VitalSignItem a : items) {
            if (a.getCode().equals(code)) {
                itemName = a.getName();
            }
        }

        return itemName;
    }

    protected LayoutInflater mLayoutInflater;

    protected void clearData() {
        e_patientId.setText("");
        t_name.setText("");
        t_sex.setText("");
        t_age.setText("");
        t_bedNo.setText("");
        t_doctor.setText("");
    }

    /*    @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            HenorShu();
            initUi();//重新刷新ui
            refreshData();//刷新数据
        }*/

    //横竖布局变化
    //protected abstract void HenorShu();

    @Override
    public void onPause() {
        //ToastUtils.show(this, "onPause");
        super.onPause();
    }

    @Override
    public void resumeOther() {

        get_patient.setVisibility(View.VISIBLE);
        if (realTimeShowData) {
            processGetData();
        } else {
            refreshData();
        }
    }

    protected void onStop() {
        //ToastUtils.show(this, "onStop");
        super.onStop();
    }

    protected void onStart() {
        //ToastUtils.show(this, "onStart");
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //回调函数，显示结果
    Handler getDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");

            switch (type) {
            case 1: {
                refreshData();
                break;
            }
            case 0: {
                String mss = msg.getData().getString("msg");
                showMessage(mss);
                MobLogAction.getMobLogAction().mobLogError("提取数据出错,请联系管理员", mss);
                break;
            }
            default: {

            }
            }
            stopAnimation(R.id.loadingImageView);
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

                if (GlobalCache.getCache().getCurrentPatient() != null
                        && GlobalCache.getCache().getCurrentPatient().getPatientId()
                                .equals(e_patientId.getText().toString().trim())) {

                    //取出全部生命体征数据
                    VitalSignAction.getAll(GlobalCache.getCache().getCurrentPatient().getPatientId(), busDate);

                    //if (GlobalCache.getCache().getTimePoint() == null) {

                    //} else {
                    //    //时间点
                    //    VitalSignAction.getOne(GlobalCache.getCache().getCurrentPatient().getPatientId(), busDate,
                    //            GlobalCache.getCache().getTimePoint(), getItemCode());
                    //}

                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 1);
                    bundle.putString("msg", "ok");
                    message.setData(bundle);
                } else {

                    //提取病人基本信息
                    String ret = VitalSignAction.getPatient(e_patientId.getText().toString().trim());
                    if (UtilString.isBlank(e_patientId.getText().toString()) && ret.equals("-1")) {
                        return;
                    } else if (!UtilString.isBlank(e_patientId.getText().toString()) && ret.equals("-1")) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 0);
                        bundle.putString("msg", "找不到住院号为" + e_patientId.getText().toString().trim() + "的病人");
                        message.setData(bundle);
                    } else {
                        hasRefreshData = true;
                        //取出全部生命体征数据
                        VitalSignAction.getAll(GlobalCache.getCache().getCurrentPatient().getPatientId(), busDate);

                        //if (GlobalCache.getCache().getTimePoint() == null) {

                        //} else {
                        //    //时间点
                        //    VitalSignAction.getOne(GlobalCache.getCache().getCurrentPatient().getPatientId(), busDate,
                        //            GlobalCache.getCache().getTimePoint(), getItemCode());
                        //}

                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 1);
                        bundle.putString("msg", "ok");
                        message.setData(bundle);
                    }
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

    //开始处理取数
    public void processGetData() {
        if (e_patientId.getText().toString().trim().equals("")) {
            return;
        }
        showProcessingImage(R.id.loadingImageView);
        GetVitalSignData getData = new GetVitalSignData(getDataHandler);
        Thread thread = new Thread(getData);
        thread.start();
    }

    //只负责显示数据
    private void refreshData() {

        Patient pa = GlobalCache.getCache().getCurrentPatient();
        if (pa != null) {
            e_patientId.setText(pa.getPatientId());
            t_name.setText(pa.getPatientName());
            t_sex.setText(pa.getSex());
            t_age.setText(pa.getAge());
            t_bedNo.setText(pa.getBedNo());
            t_doctor.setText(pa.getDoctorName());
            //取生命体征
            if (hasRefreshData) {
                showMessage("当前病人" + pa.getPatientName());
                hasRefreshData = false;
            }

            VitalSignData data = new VitalSignData();
            data.setAddDate(GlobalCache.getCache().getBusDate());
            data.setPatientId(GlobalCache.getCache().getCurrentPatient().getPatientId());
            data.setTimePoint(GlobalCache.getCache().getTimePoint());
            data.setItemCode(getItemCode());
            data.setItemName(getItemName(getItemCode()));
            data.setUserId(GlobalCache.getCache().getLoginuser().getId());
            data.setValue1("");
            data.setValue2("");

            List<VitalSignData> lists = GlobalCache.getCache().getVitalSignDatas_all();
            if (lists.size() == 0) {

            } else {
                for (VitalSignData a : lists) {
                    if (a.getItemCode().equals(getItemCode())) {
                        data = a;
                    }
                }
            }

            GlobalCache.getCache().setVitalSignData(data);
            refreshOther();
        } else {
            stopAnimation(R.id.loadingImageView);
            //showMessage("没有找到对应病人");
        }

    }

    //实现刷新其他控件
    protected abstract void refreshOther();

    //需实现的方法
    public void receiveBlueToothMessage(String msg, int type) {
        //
    }

    public Context getContext() {
        return this;
    }

    Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            switch (type) {
            case BlueToothConnector.UNCONNECTED:
                unConnectBLuetoothImage();
                break;
            case BlueToothConnector.CONNECTED:
                //有蓝牙设备
                connectBLuetoothImage();
                break;
            case BlueToothConnector.READ:

                String p = msg.getData().getString("msg");
                e_patientId.setText(p);

                //振动器
                final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                final int vibrationDuration = 33;
                mVibrator.vibrate(vibrationDuration);

                processGetData();

                break;

            default: {

            }
            }
        }
    };

    public Handler getUIHandler() {
        return UIHandler;
    }

    private void initPatientInfo() {
        try {
            LinearLayout inputkey = (LinearLayout) mLayoutInflater.inflate(R.layout.vitalsign_patient_info, null);
            LinearLayout layout = (LinearLayout) findViewById(R.id.pa_infos);
            layout.addView(inputkey);

        } catch (Exception e) {
            MobLogAction.getMobLogAction().mobLogError("病人信息", e.getMessage());
        }

        UserDTO user = GlobalCache.getCache().getLoginuser();
        t_user_by = (TextView) findViewById(R.id.user_by);
        t_user_by.setText("操作人: " + user.getName() + " - " + user.getDepartName());

        get_patient = (Button) findViewById(R.id.get_patient);
        get_patient.setVisibility(View.VISIBLE);
        get_patient.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (e_patientId.getText().toString().trim().equals("")) {
                    return;
                }
                showProcessingImage(R.id.loadingImageView);

                processGetData();

            }
        });

        e_patientId = (EditText) findViewById(R.id.vitalsign_patientId);
        t_name = (TextView) findViewById(R.id.vitalsign_name);
        t_sex = (TextView) findViewById(R.id.vitalsign_sex);
        t_age = (TextView) findViewById(R.id.vitalsign_age);
        t_bedNo = (TextView) findViewById(R.id.vitalsign_bedNo);
        t_doctor = (TextView) findViewById(R.id.vitalsign_doctor);
    }

    @Override
    protected void createUi() {
        initBase();

        initUi();
    }

    private void initUi() {
        mLayoutInflater = (LayoutInflater) VitalSignBase.this.getSystemService(LAYOUT_INFLATER_SERVICE);

        initPatientInfo();

        showUi();

        clearData();
        if (GlobalCache.getCache().getCurrentPatient() != null) {
            e_patientId.setText(GlobalCache.getCache().getCurrentPatient().getPatientId());
        }
    }

    //生成除了病人信息以外的控件
    protected abstract void showUi();

    //setContentView
    protected abstract void initBase();
}