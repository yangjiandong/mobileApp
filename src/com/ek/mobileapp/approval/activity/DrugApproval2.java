package com.ek.mobileapp.approval.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.approval.action.DrugApprovalAction;
import com.ek.mobileapp.model.ApprovalNote;
import com.ek.mobileapp.model.DrugApprovalData;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.ToastUtils;

//合理用药审批
public class DrugApproval2 extends Activity {
    List<DrugApprovalData> list = new ArrayList<DrugApprovalData>();

    ListView infolist;

    SharedPreferences sharedPreferences;
    TextView t_patientId;
    TextView t_patientName;
    TextView t_sex;
    TextView t_age;
    TextView t_deptName;
    TextView t_appWho;
    TextView t_appDate;
    TextView t_diagnosis;
    TextView t_useReason;
    TextView t_drugName;
    TextView t_drugSpec;
    TextView t_mettleType;

    RadioButton radio_yes;//同意
    RadioButton radio_no;//不同意

    Button button_save;
    Button button_close;

    Spinner s_note;
    String note;
    String[] noteStr;
    String[] noteCodeStr;
    private ArrayAdapter<String> noteAdapter;
    String position = "0";

    public void onCreate(Bundle fromMainBundle) {
        super.onCreate(fromMainBundle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.drugapproval_appr);

        Intent intent = getIntent();
        position = intent.getStringExtra("position");
        GlobalCache.getCache().setDrugApprovalData(
                GlobalCache.getCache().getDrugApprovalDatas().get(Integer.valueOf(position)));

        t_patientId = (TextView) findViewById(R.id.drugappr_patientId);
        t_patientName = (TextView) findViewById(R.id.drugappr_patientName);
        t_sex = (TextView) findViewById(R.id.drugappr_sex);
        t_age = (TextView) findViewById(R.id.drugappr_age);
        t_deptName = (TextView) findViewById(R.id.drugappr_deptName);
        t_appWho = (TextView) findViewById(R.id.drugappr_appWho);
        t_appDate = (TextView) findViewById(R.id.drugappr_appDate);
        t_diagnosis = (TextView) findViewById(R.id.drugappr_diagnosis);
        t_useReason = (TextView) findViewById(R.id.drugappr_useReason);
        t_drugName = (TextView) findViewById(R.id.drugappr_drugName);
        t_drugSpec = (TextView) findViewById(R.id.drugappr_drugSpec);
        t_mettleType = (TextView) findViewById(R.id.drugappr_meddleType);

        radio_yes = (RadioButton) findViewById(R.id.drugappr_yes);
        radio_no = (RadioButton) findViewById(R.id.drugappr_no);

        button_save = (Button) findViewById(R.id.drugappr_save);
        button_close = (Button) findViewById(R.id.drugappr_close);

        //振动器
        final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final int vibrationDuration = 33;

        button_save.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
                String result = "Y";
                if (radio_yes.isChecked()) {
                    result = "Y";
                }
                if (radio_no.isChecked()) {
                    result = "N";
                }

                GlobalCache.getCache().getDrugApprovalData().setResult(result);
                GlobalCache.getCache().getDrugApprovalData().setNote(note);
                GlobalCache.getCache().getDrugApprovalDatas().get(Integer.valueOf(position)).setResult(result);
                GlobalCache.getCache().getDrugApprovalDatas().get(Integer.valueOf(position)).setNote(note);

                processSaveData();
            }
        });
        button_close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
                finish();
            }
        });

        s_note = (Spinner) findViewById(R.id.drugappr_note);

        DrugApprovalAction.getNote("1");
        List<ApprovalNote> notes = GlobalCache.getCache().getApprovalNotes();
        noteStr = new String[notes.size()];
        noteCodeStr = new String[notes.size()];
        int i = 0;
        note = "";
        for (ApprovalNote a : notes) {
            noteStr[i] = a.getName();
            noteCodeStr[i] = a.getCode();
            i++;
        }

        noteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, noteStr);
        noteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_note.setAdapter(noteAdapter);
        s_note.setPrompt("选择备注信息:");
        s_note.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapter, View view, int selected, long arg3) {
                note = noteCodeStr[selected];

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        List<DrugApprovalData> list = GlobalCache.getCache().getDrugApprovalDatas();
        DrugApprovalData data = list.get(Integer.valueOf(position));

        if (data.getPatientId() == null) {
            t_patientId.setText("");
        } else {
            t_patientId.setText(data.getPatientId().toString());
        }
        if (data.getPatientName() == null) {
            t_patientName.setText("");
        } else {
            t_patientName.setText(data.getPatientName().toString());
        }
        if (data.getSex() == null) {
            t_sex.setText("");
        } else {
            t_sex.setText(data.getSex().toString());
        }
        if (data.getAge() == null) {
            t_age.setText("");
        } else {
            t_age.setText(data.getAge().toString());
        }
        if (data.getDeptName() == null) {
            t_deptName.setText("");
        } else {
            t_deptName.setText(data.getDeptName().toString());
        }
        if (data.getAppWho() == null) {
            t_appWho.setText("");
        } else {
            t_appWho.setText(data.getAppWho().toString());
        }
        if (data.getAppDate() == null) {
            t_appDate.setText("");
        } else {
            t_appDate.setText(data.getAppDate().toString());
        }
        if (data.getDiagnosis() == null) {
            t_diagnosis.setText("");
        } else {
            t_diagnosis.setText(data.getDiagnosis().toString());
        }
        if (data.getUseReasion() == null) {
            t_useReason.setText("");
        } else {
            t_useReason.setText(data.getUseReasion().toString());
        }
        if (data.getDrugName() == null) {
            t_drugName.setText("");
        } else {
            t_drugName.setText(data.getDrugName().toString());
        }
        if (data.getDrugSpec() == null) {
            t_drugSpec.setText("");
        } else {
            t_drugSpec.setText(data.getDrugSpec().toString());
        }
        if (data.getMeddleType() == null) {
            t_mettleType.setText("");
        } else {
            t_mettleType.setText(data.getMeddleType().toString());
        }

    }

    //开始处理取数
    private void processSaveData() {

        SaveDrugApprData saveData = new SaveDrugApprData(saveDataHandler);
        Thread thread = new Thread(saveData);
        thread.start();
    }

    //回调函数，显示结果
    Handler saveDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            String message = msg.getData().getString("msg");
            int type = msg.getData().getInt("type");
            switch (type) {
            case 1: {
                ToastUtils.show(DrugApproval2.this, message);
                break;
            }
            case 0: {
                ToastUtils.show(DrugApproval2.this, message);
                break;
            }
            default: {

            }
            }
            //super.handleMessage(msg);
        }
    };

    //真正的取数过程
    class SaveDrugApprData implements Runnable {
        Handler handler;

        public SaveDrugApprData(Handler h) {
            this.handler = h;
        }

        public void run() {
            Message message = Message.obtain();
            try {

                //保存用药审批
                String ret = DrugApprovalAction.saveAppr();
                if (ret.equals("1")) {

                    String ret2 = DrugApprovalAction.commitHis();
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
