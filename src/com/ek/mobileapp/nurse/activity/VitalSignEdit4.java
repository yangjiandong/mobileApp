package com.ek.mobileapp.nurse.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.MeasureType;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.SkinTest;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.UtilString;

//皮试1,皮试2
//一次录入一个指标
public class VitalSignEdit4 extends Activity {
    List<MeasureType> list = new ArrayList<MeasureType>();
    Map<Integer, Integer> btns = new HashMap<Integer, Integer>();
    Button B1, B2, B3, B4, B5, B6, B7, B8, B9, B0, BClear, BDot, BEqual;

    EditText e_patientId;
    TextView t_patientName;
    TextView t_age;
    TextView t_sex;
    TextView t_bedNo;
    TextView t_doctor;
    TextView t_user_by;
    Button get_patient;
    Spinner s_skintest;
    RadioButton button1;//阴性
    RadioButton button2;//阳性

    private String[] skinTestStr;
    private ArrayAdapter<String> skinTestAdapter;
    String itemCode = "";
    TextView t_label;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vitalsignedit_2);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            LinearLayout inputkey = (LinearLayout) inflater.inflate(R.layout.vitalsign_patient_info, null);

            LinearLayout layout = (LinearLayout) findViewById(R.id.pa_infos);
            layout.addView(inputkey);

        } catch (Exception e) {
            MobLogAction.getMobLogAction().mobLogError("病人信息", e.getMessage());
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        UserDTO user = GlobalCache.getCache().getLoginuser();
        t_user_by = (TextView) findViewById(R.id.user_by);
        t_user_by.setText("操作人: " + user.getName() + " - " + user.getDepartName());

        Intent intent = getIntent();
        itemCode = intent.getStringExtra("code");
        String itemName = intent.getStringExtra("name");
        Patient pa = GlobalCache.getCache().getCurrentPatient();

        try {

            processGetData();

        } catch (Exception e) {
            MobLogAction.getMobLogAction().mobLogError("病人信息", e.getMessage());
        }

        e_patientId = (EditText) findViewById(R.id.vitalsign_patientId);
        e_patientId.setInputType(InputType.TYPE_NULL);
        t_patientName = (TextView) findViewById(R.id.vitalsign_name);

        get_patient = (Button) findViewById(R.id.get_patient);
        get_patient.setVisibility(View.INVISIBLE);

        t_age = (TextView) findViewById(R.id.vitalsign_age);
        t_sex = (TextView) findViewById(R.id.vitalsign_sex);
        t_bedNo = (TextView) findViewById(R.id.vitalsign_bedNo);
        t_doctor = (TextView) findViewById(R.id.vitalsign_doctor);
        t_label = (TextView) findViewById(R.id.vitalsign_edit_4_label);
        button1 = (RadioButton) findViewById(R.id.vitalsign_edit_4_radion1);
        button2 = (RadioButton) findViewById(R.id.vitalsign_edit_4_radion1);

        s_skintest = (Spinner) findViewById(R.id.vitalsign_edit_4_spinner);

        try {
            TableLayout inputkey = (TableLayout) inflater.inflate(R.layout.inputkey, null);
            LinearLayout layout = (LinearLayout) findViewById(R.id.vitalsign_edit_2_inputkey);
            layout.addView(inputkey);

        } catch (Exception e) {
            MobLogAction.getMobLogAction().mobLogError("inputkey", e.getMessage());
        }

        if (pa != null) {
            e_patientId.setText(pa.getPatientId());
            t_patientName.setText(pa.getPatientName());
            t_age.setText(pa.getAge());
            t_sex.setText(pa.getSex());
            t_bedNo.setText(pa.getBedNo());
            t_doctor.setText(pa.getDoctorName());
        }

        t_label.setText(itemName);

        try {

            VitalSignData data = GlobalCache.getCache().getVitalSignData();
            if (data.getValue2().contentEquals("阴性")) {
                button1.setChecked(true);
            }
            if (data.getValue2().contentEquals("阳性")) {
                button2.setChecked(true);
            }

            List<SkinTest> st = GlobalCache.getCache().getSkinTests();
            skinTestStr = new String[st.size()];
            int i = 0;
            int t = 0;
            String str = "";
            for (SkinTest a : st) {
                if (data != null && data.getValue2().contentEquals(a.getName())) {
                    t = i;
                    str = GlobalCache.getCache().getTimePoint();
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

        } catch (Exception e) {
            Log.e("", e.getMessage());
            MobLogAction.getMobLogAction().mobLogError("构建生命体征界面" + itemName, e.getMessage());
        }

    }

    //开始处理取数
    private void processGetData() {

        GetVitalSignData getData = new GetVitalSignData(getDataHandler);

        Thread thread = new Thread(getData);
        thread.start();
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
