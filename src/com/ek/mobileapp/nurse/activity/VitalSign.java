package com.ek.mobileapp.nurse.activity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ek.mobileapp.R;
import com.ek.mobileapp.model.MobConstants;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.TimePoint;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.model.VitalSignItem;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.nurse.adapter.VitalSignDataGridViewAdapter;
import com.ek.mobileapp.utils.BarCodeUtils;
import com.ek.mobileapp.utils.BlueToothConnector;
import com.ek.mobileapp.utils.BlueToothReceive;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.TimeTool;

public class VitalSign extends Activity implements BlueToothReceive {

    SharedPreferences sharedPreferences;

    TextView t_user_by;
    Button get_patient;
    EditText e_patientId;
    TextView t_name;
    TextView t_sex;
    TextView t_age;
    TextView t_bedNo;
    TextView t_doctor;
    EditText e_busDate;
    Spinner s_timePoint;

    protected PopupWindow selectDateView;
    protected LayoutInflater mLayoutInflater;
    protected DatePicker date_picker;
    private boolean date_changed = false;
    private ArrayAdapter<String> adapter;

    private String[] timeStr;

    GridView gridView1;
    GridView gridView2;
    //
    private int state = 0;
    private String busDate = "";
    private String barcode = "";
    private static final int scanPatient = 0;
    private Patient currentPatient = null;

    protected ProgressDialog proDialog;
    protected String submitString = "正在传输数据...";
    protected BlueToothConnector connector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.vitalsign);

        busDate = TimeTool.getDateFormated(TimeTool.getCurrentTime());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLayoutInflater = (LayoutInflater) VitalSign.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        UserDTO user = GlobalCache.getCache().getLoginuser();
        t_user_by = (TextView) findViewById(R.id.user_by);
        t_user_by.setText("操作人: " + user.getName() + " - " + user.getDepartName());

        get_patient = (Button) findViewById(R.id.get_patient);
        get_patient.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (e_patientId.getEditableText().toString().trim().equals("")) {
                    return;
                }
                VitalSignAction.getPatient(e_patientId.getEditableText().toString().trim());
                Patient pa = GlobalCache.getCache().getCurrentPatient();
                if (pa != null) {
                    t_name.setText(pa.getPatientName());
                    t_sex.setText(pa.getSex());
                    t_age.setText(pa.getAge());
                    t_bedNo.setText(pa.getBedNo());
                    t_doctor.setText(pa.getDoctorName());
                }
            }
        });

        e_patientId = (EditText) findViewById(R.id.vitalsign_patientId);
        t_name = (TextView) findViewById(R.id.vitalsign_name);
        t_sex = (TextView) findViewById(R.id.vitalsign_sex);
        t_age = (TextView) findViewById(R.id.vitalsign_age);
        t_bedNo = (TextView) findViewById(R.id.vitalsign_bedNo);
        t_doctor = (TextView) findViewById(R.id.vitalsign_doctor);
        e_busDate = (EditText) findViewById(R.id.vitalsign_busDate);
        e_busDate.setTextSize(12);
        s_timePoint = (Spinner) findViewById(R.id.vitalsign_timePoint);

        VitalSignAction.getTimePoint();
        List<TimePoint> times = GlobalCache.getCache().getTimePoints();
        timeStr = new String[times.size()];
        int i = 0;
        for (TimePoint a : times) {
            timeStr[i] = a.getName();
            i++;
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeStr);

        s_timePoint.setAdapter(adapter);
        s_timePoint.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapter, View view, int selected, long arg3) {
                GlobalCache.getCache().setTimePoint(timeStr[selected]);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        e_busDate.setInputType(InputType.TYPE_NULL);
        e_busDate.setText(busDate);

        e_busDate.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (selectDateView == null) {
                    initSelectDate();
                }
                if (!selectDateView.isShowing()) {
                    View view = mLayoutInflater.inflate(R.layout.activity_query, null);
                    selectDateView.showAtLocation(view, BIND_AUTO_CREATE, 0, 0);
                } else {
                    selectDateView.dismiss();
                }
            }

        });

        //
        clearData();

        gridView1 = (GridView) findViewById(R.id.gridView1);
        final List<String> numsList = new ArrayList<String>();
        final List<String> numsList2 = new ArrayList<String>();
        VitalSignAction.getItem("");
        List<VitalSignItem> vas = GlobalCache.getCache().getVitalSignItems();
        for (VitalSignItem vitalSignItem : vas) {
            if (vitalSignItem.getTypeCode().equals(MobConstants.MOB_VITALSIGN_MORE)) {
                numsList.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + vitalSignItem.getUnit() + "|000");
            } else {
                numsList2.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + vitalSignItem.getUnit()
                        + "|000");
            }

        }

        gridView1.setAdapter(new VitalSignDataGridViewAdapter(this, numsList));
        gridView1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String code = ((TextView) v.findViewById(R.id.grid_item_code)).getText().toString();
                String name = ((TextView) v.findViewById(R.id.grid_item_label)).getText().toString();

                if (code.equals("01") || code.equals("02") || code.equals("03") || code.equals("04")) {
                    Intent intent = new Intent(VitalSign.this, VitalSignEdit.class);
                    intent.putExtra("code", code);
                    intent.putExtra("name", name);
                    startActivity(intent);

                } else if (code.equals("99")) {

                } else {
                    Toast.makeText(getApplicationContext(), code, Toast.LENGTH_SHORT).show();
                }

            }
        });

        gridView2 = (GridView) findViewById(R.id.gridView2);
        gridView2.setAdapter(new VitalSignDataGridViewAdapter(this, numsList2));

        gridView2.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.grid_item_code)).getText(),
                        Toast.LENGTH_SHORT).show();

            }
        });
        connector = new BlueToothConnector(this);
        connector.setDaemon(true);
        connector.start();
    }

    private void clearData() {
        e_patientId.setText("");
        t_name.setText("");
        t_sex.setText("");
        t_age.setText("");
        t_bedNo.setText("");
        t_doctor.setText("");
    }

    private void sendMessage(String msg, int type) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putString("msg", msg);
        message.setData(bundle);
        getUIHandler().sendMessage(message);
    }

    private void showMessage(String msg) {
        //if (useVoice) {
        //    Toast.makeText(CheckMedicine.this, msg, Toast.LENGTH_SHORT).show();
        //    try {
        //        ttsService.speak(msg, TextToSpeech.QUEUE_ADD);
        //    } catch (RemoteException e) {
        // TODO Auto-generated catch block
        //        e.printStackTrace();
        //    }
        //} else {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        //}
    }

    protected void submitDate() {
        if (state == scanPatient) {
            //medicines = null;
            //currentPatient = CheckMedicineAction.GetPatient(barcode);
        }
        //else if (state == scanMedicine) {
        //    medicines = null;
        //    if (currentPatient != null)
        //        medicines = CheckMedicineAction.GetMedicine(barcode,
        //                currentPatient.getPatientid());
        //}
    }

    private class SubmitHandler extends Thread {
        @Override
        public void run() {
            submitDate();
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("type", -2);
            message.setData(bundle);
            getUIHandler().sendMessage(message);
            proDialog.dismiss();
        }
    }

    protected void submitWithProgressDialog() {
        proDialog = ProgressDialog.show(this, "", submitString, true, true);
        SubmitHandler t = new SubmitHandler();
        t.start();
    }

    private void setMessage() {
        if (currentPatient == null) {
            //清除病人信息
            clearData();
            return;
        }
        //from currentPatient 装载病人信息
        //patientInfo.setText(res);
    }

    protected void afterSubmit() {
        if (state == scanPatient) {
            if (currentPatient == null) {
                showMessage("不能识别当前病人");
                setMessage();
            } else {
                showMessage("当前病人" + currentPatient.getPatientName());
                setMessage();
            }

        }
    }

    private void showProcessingImage(int imageViewId) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.setImageResource(R.drawable.loading);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, 13, 13);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(rotateAnimation);
    }

    private void stopAnimation(int imageViewId) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.clearAnimation();
        imageView.setImageResource(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        //isActive.set(false);
        //AtomicBoolean isActive=new AtomicBoolean(false);
        //this.connector.setIsActive(isActive);
    }

    public void onResume() {
        super.onResume();

        //AtomicBoolean isActive=new AtomicBoolean(true);
        //this.connector.setIsActive(isActive);
    }

    @Override
    public void onDestroy() {
        AtomicBoolean isActive = new AtomicBoolean(false);
        this.connector.setIsActive(isActive);

        connector.mystop();
        connector.stop();
        connector = null;

        super.onDestroy();
    }

    public void receiveBlueToothMessage(String msg, int type) {
        if (type == BlueToothConnector.CONNECTED) {
            sendMessage(msg, type);
        } else {
            if (BarCodeUtils.isPatientTM(msg)) {
                state = scanPatient;
                barcode = msg;
                sendMessage("", 3);
                submitWithProgressDialog();
            } else if (BarCodeUtils.isMedicineTM(msg)) {
                if (currentPatient == null) {
                    sendMessage("请先扫描病人条码", type);
                } else {
                    //state = scanMedicine;
                    barcode = msg;
                    sendMessage("", 4);
                    // submitWithProgressDialog();
                }
            } else {
                sendMessage("请检查条码重新扫描", type);
            }
        }
    }

    public Context getContext() {
        return this;
    }

    Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            switch (type) {
            case BlueToothConnector.CONNECTED:
                //有蓝牙设备
                showMessage(msg.getData().getString("msg"));
                break;
            case BlueToothConnector.READ:
                //showMessage(msg.getData().getString("msg"));
                String p = msg.getData().getString("msg");
                e_patientId.setText(p);

                VitalSignAction.getPatient(p);
                Patient pa = GlobalCache.getCache().getCurrentPatient();

                if (pa != null) {
                    //patientId.setText("");
                    t_name.setText(pa.getPatientName());
                    t_sex.setText(pa.getSex());
                    t_age.setText(pa.getAge());
                    t_bedNo.setText(pa.getBedNo());
                    t_doctor.setText(pa.getDoctorName());
                }
                break;
            case 3:
                submitWithProgressDialog();
                break;
            case 4:
                submitWithProgressDialog();
                break;
            case -2: {
                afterSubmit();
                break;
            }
            default: {

            }
            }
            super.handleMessage(msg);
        }
    };

    public Handler getUIHandler() {
        return UIHandler;
    }

    private void initSelectDate() {
        View view = mLayoutInflater.inflate(R.layout.activity_date, null);
        date_picker = (DatePicker) view.findViewById(R.id.seldate);
        date_picker.init(TimeTool.getYear(), TimeTool.getMonth(), TimeTool.getDay(), new OnDateChangedListener() {

            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date_changed = true;
                busDate = TimeTool.getDateFormatedFromDataPicker(year, monthOfYear, dayOfMonth);

            }

        });

        selectDateView = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        selectDateView.setFocusable(true);
        selectDateView.setAnimationStyle(-1);
        selectDateView.update();
        Button okbtn = (Button) view.findViewById(R.id.date_ok);
        okbtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (selectDateView != null && selectDateView.isShowing()) {
                    selectDateView.dismiss();
                    e_busDate.setText(busDate);
                    GlobalCache.getCache().setBusDate(busDate);

                    if (GlobalCache.getCache().getTimePoint() != null) {
                        VitalSignAction.getOne(GlobalCache.getCache().getCurrentPatient().getPatientId(), busDate,
                                GlobalCache.getCache().getTimePoint(), "");
                    } else {
                        VitalSignAction.getAll(GlobalCache.getCache().getCurrentPatient().getPatientId(), busDate);
                    }

                    final List<String> dataList1 = new ArrayList<String>();
                    final List<String> dataList2 = new ArrayList<String>();
                    List<VitalSignData> datas = GlobalCache.getCache().getVitalSignDatas();
                    List<VitalSignItem> items = GlobalCache.getCache().getVitalSignItems();
                    for (VitalSignItem vitalSignItem : items) {
                        if (vitalSignItem.getTypeCode().equals(MobConstants.MOB_VITALSIGN_MORE)) {
                            if (GlobalCache.getCache().getTimePoint() != null) {
                                for (VitalSignData vsd : datas) {
                                    if (vsd.getItemName().equals(vitalSignItem.getName())) {
                                        dataList1.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                                                + vitalSignItem.getUnit() + ")" + "|" + vsd.getValue1());
                                        break;
                                    } else {
                                        dataList1.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                                                + vitalSignItem.getUnit() + ")" + "| ");
                                        break;
                                    }
                                }
                            } else {
                                dataList1.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                                        + vitalSignItem.getUnit() + ")" + "| ");
                            }

                        } else {

                            for (VitalSignData vsd : datas) {
                                if (vsd.getItemName().equals(vitalSignItem.getName())) {
                                    dataList2.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                                            + vitalSignItem.getUnit() + ")" + "|" + vsd.getValue2());
                                    break;
                                } else {
                                    dataList2.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                                            + vitalSignItem.getUnit() + ")" + "| ");
                                    break;
                                }
                            }

                        }

                    }
                    gridView1.setAdapter(new VitalSignDataGridViewAdapter(VitalSign.this, dataList1));
                    gridView2.setAdapter(new VitalSignDataGridViewAdapter(VitalSign.this, dataList2));

                }
            }
        });

        Button canelbtn = (Button) view.findViewById(R.id.date_cancle);
        canelbtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (selectDateView != null && selectDateView.isShowing()) {
                    selectDateView.dismiss();
                }
            }
        });
    }
}
