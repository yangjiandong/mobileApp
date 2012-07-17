package com.ek.mobileapp.nurse.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.utils.BarCodeUtils;
import com.ek.mobileapp.utils.BlueToothConnector;
import com.ek.mobileapp.utils.BlueToothReceive;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.UtilString;

public class VitalSign extends Activity implements BlueToothReceive {
    Map<String, Integer> btns = new HashMap<String, Integer>();
    Map<String, Integer> btnsStyle = new HashMap<String, Integer>();
    Map<Integer, String> moduels = new HashMap<Integer, String>();

    SharedPreferences sharedPreferences;

    EditText patientId;
    TextView name;
    TextView sex;
    TextView age;
    TextView bedNo;
    TextView doctor;
    EditText busDate;
    Spinner timePoint;
    //
    private int state = 0;
    private String barcode = "";
    private static final int scanPatient = 0;
    private Patient currentPatient = null;

    //
    protected ProgressDialog proDialog;
    protected String submitString = "正在传输数据...";

    protected BlueToothConnector connector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createBtns();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vitalsign);

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
                showMessage(msg.getData().getString("msg"));
                break;
            case BlueToothConnector.READ:
                showMessage(msg.getData().getString("msg"));
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
