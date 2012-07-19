package com.ek.mobileapp.nurse.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.MeasureType;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.utils.GlobalCache;

public class VitalSignEdit extends Activity {
    List<MeasureType> list = new ArrayList<MeasureType>();

    RadioGroup measures;
    RadioGroup measures2;
    EditText e_patientId;
    TextView t_patientName;
    TextView t_age;
    TextView t_sex;
    TextView t_bedNo;
    TextView t_doctor;

    TextView t_label;
    EditText e_text;

    private static Long measureTypeCode = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vitalsignedit);

        Intent intent = getIntent();
        String itemCode = intent.getStringExtra("code");
        String itemName = intent.getStringExtra("name");

        Patient pa = GlobalCache.getCache().getCurrentPatient();
        String busDate = GlobalCache.getCache().getBusDate();
        String timePoint = GlobalCache.getCache().getTimePoint();

        e_patientId = (EditText) findViewById(R.id.vitalsign_edit_patientId);
        e_patientId.setInputType(InputType.TYPE_NULL);
        t_patientName = (TextView) findViewById(R.id.vitalsign_edit_patientName);

        t_age = (TextView) findViewById(R.id.vitalsign_edit_age);
        t_sex = (TextView) findViewById(R.id.vitalsign_edit_sex);
        t_bedNo = (TextView) findViewById(R.id.vitalsign_edit_bedNo);
        t_doctor = (TextView) findViewById(R.id.vitalsign_edit_doctor);

        e_text = (EditText) findViewById(R.id.vitalsign_edit_text);
        t_label = (TextView) findViewById(R.id.vitalsign_edit_label);

        if (pa != null) {
            e_patientId.setText(pa.getPatientId());
            t_patientName.setText(pa.getPatientName());
            t_age.setText(pa.getAge());
            t_sex.setText(pa.getSex());
            t_bedNo.setText(pa.getBedNo());
            t_doctor.setText(pa.getDoctorName());
        }

        t_label.setText(itemName);

        measures = (RadioGroup) findViewById(R.id.vitalsign_edit_measures);
        measures2 = (RadioGroup) findViewById(R.id.vitalsign_edit_measures2);
        int count = 4;
        int i = 0;
        try {

            if (pa != null && busDate != null && timePoint != null && timePoint.length() > 0 && busDate.length() > 0)
                VitalSignAction.getOne(pa.getPatientId(), busDate, timePoint, itemName);

            VitalSignData data = GlobalCache.getCache().getVitalSignData();

            if (data != null) {
                if (data.getValue1() != null && data.getValue1().length() > 0)
                    e_text.setText(data.getValue1());
            }

            VitalSignAction.getMeasureType();
            list = GlobalCache.getCache().getMeasureTypes();
            for (MeasureType a : list) {
                RadioButton rb = new RadioButton(this, null);
                if (data != null) {
                    if (a.getName().equals(data.getMeasureTypeCode()))
                        rb.setChecked(true);
                }

                if (i < count) {
                    rb.setId(a.getId().intValue());
                    rb.setText(a.getName());
                    rb.setOnClickListener(new ClickEvent());
                    measures.addView(rb);
                } else {
                    rb.setId(a.getId().intValue());
                    rb.setText(a.getName());
                    rb.setOnClickListener(new ClickEvent());
                    measures2.addView(rb);
                }

                i++;
            }

        } catch (Exception e) {
            Log.e("", e.getMessage());
            MobLogAction.mobLogError("构建生命体征界面", e.getMessage());
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

}
