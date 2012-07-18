package com.ek.mobileapp.nurse.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ek.mobileapp.R;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.nurse.adapter.ImageAdapter;
import com.ek.mobileapp.utils.BarCodeUtils;
import com.ek.mobileapp.utils.BlueToothConnector;
import com.ek.mobileapp.utils.BlueToothReceive;
import com.ek.mobileapp.utils.GlobalCache;

public class VitalSign extends Activity implements BlueToothReceive {
    Map<String, Integer> btns = new HashMap<String, Integer>();
    Map<String, Integer> btnsStyle = new HashMap<String, Integer>();
    Map<Integer, String> moduels = new HashMap<Integer, String>();

    SharedPreferences sharedPreferences;

    TextView user_by;
    Button get_patient;
    EditText patientId;
    TextView name;
    TextView sex;
    TextView age;
    TextView bedNo;
    TextView doctor;
    EditText busDate;
    Spinner timePoint;

    GridView gridView1;
    GridView gridView2;
    //
    private int state = 0;
    private String barcode = "";
    private static final int scanPatient = 0;
    private Patient currentPatient = null;

    protected ProgressDialog proDialog;
    protected String submitString = "正在传输数据...";
    protected BlueToothConnector connector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createBtns();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vitalsign);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        UserDTO user = GlobalCache.getCache().getLoginuser();
        user_by = (TextView) findViewById(R.id.user_by);
        user_by.setText(user.getName() + " - " + user.getDepartName());

        get_patient = (Button) findViewById(R.id.get_patient);
        get_patient.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (patientId.getEditableText().toString().trim().equals("")) {
                    return;
                }
            }
        });

        patientId = (EditText) findViewById(R.id.vitalsign_patientId);
        name = (TextView) findViewById(R.id.vitalsign_name);
        sex = (TextView) findViewById(R.id.vitalsign_sex);
        age = (TextView) findViewById(R.id.vitalsign_age);
        bedNo = (TextView) findViewById(R.id.vitalsign_bedNo);
        doctor = (TextView) findViewById(R.id.vitalsign_doctor);
        busDate = (EditText) findViewById(R.id.vitalsign_busDate);
        timePoint = (Spinner) findViewById(R.id.vitalsign_timePoint);

        //
        clearData();
        gridView1 = (GridView) findViewById(R.id.gridView1);
        final List<String> numsList = new ArrayList<String>();
        numsList.add("tweetnum");
        numsList.add("fansnum");
        numsList.add("idolnum");
        gridView1.setAdapter(new ImageAdapter(this, numsList));

        gridView1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.grid_item_label)).getText(),
                        Toast.LENGTH_SHORT).show();

            }
        });

        gridView2 = (GridView) findViewById(R.id.gridView2);
        gridView2.setAdapter(new ImageAdapter(this, numsList));

        gridView2.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.grid_item_label)).getText(),
                        Toast.LENGTH_SHORT).show();

            }
        });
        connector = new BlueToothConnector(this);
        connector.setDaemon(true);
        connector.start();
    }

    private void clearData() {
        patientId.setText("");
        name.setText("");
        sex.setText("");
        age.setText("");
        bedNo.setText("");
        doctor.setText("");
    }

    private void createBtns() {
        btns.put("01", R.id.v01);
        btns.put("02", R.id.v02);
        btns.put("03", R.id.v03);
        btns.put("04", R.id.v04);
        btns.put("05", R.id.v05);
        btns.put("06", R.id.v06);
        btns.put("07", R.id.v07);
        btns.put("08", R.id.v08);
        btns.put("09", R.id.v09);
        btns.put("10", R.id.v10);
        btns.put("11", R.id.v11);
        btns.put("12", R.id.v12);
        btns.put("13", R.id.v13);
        btns.put("14", R.id.v14);
        btns.put("15", R.id.v15);
        //
        btnsStyle.put("01", R.drawable.hospital_button);
        btnsStyle.put("02", R.drawable.doctor_button);
        btnsStyle.put("03", R.drawable.doctor_button);
        btnsStyle.put("04", R.drawable.doctor_button);
        btnsStyle.put("05", R.drawable.doctor_button);
        btnsStyle.put("06", R.drawable.doctor_button);
        btnsStyle.put("07", R.drawable.doctor_button);
        btnsStyle.put("08", R.drawable.doctor_button);
        btnsStyle.put("09", R.drawable.doctor_button);
        btnsStyle.put("10", R.drawable.doctor_button);
        btnsStyle.put("11", R.drawable.doctor_button);
        btnsStyle.put("12", R.drawable.doctor_button);
        btnsStyle.put("13", R.drawable.doctor_button);
        btnsStyle.put("14", R.drawable.doctor_button);
        btnsStyle.put("15", R.drawable.doctor_button);
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
                patientId.setText(p);

                VitalSignAction.getPatient(p);
                Patient pa = GlobalCache.getCache().getCurrentPatient();

                if (pa != null) {
                    //patientId.setText("");
                    name.setText(pa.getPatientName());
                    sex.setText(pa.getSex());
                    age.setText(pa.getAge());
                    bedNo.setText(pa.getBedNo());
                    doctor.setText(pa.getDoctorName());
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
}
