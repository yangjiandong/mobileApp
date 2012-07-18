package com.ek.mobileapp.nurse.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.MeasureType;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.utils.BlueToothConnector;
import com.ek.mobileapp.utils.GlobalCache;

public class VitalSignEdit extends Activity {
    List<MeasureType> list = new ArrayList<MeasureType>();

    RadioGroup measures;
    RadioGroup measures2;
    EditText patientId;
    private static Long measureTypeCode = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vitalsignedit);
        patientId = (EditText) findViewById(R.id.vitalsign_text);

        measures = (RadioGroup) findViewById(R.id.vitalsign_measures);
        measures2 = (RadioGroup) findViewById(R.id.vitalsign_measures2);
        int count = 4;
        int i = 0;
        try {

            VitalSignAction.getMeasureType();
            list = GlobalCache.getCache().getMeasureTypes();
            for (MeasureType a : list) {
                if (i < count) {
                    RadioButton rb = new RadioButton(this, null);
                    rb.setId(a.getId().intValue());
                    rb.setText(a.getName());
                    rb.setOnClickListener(new ClickEvent());
                    measures.addView(rb);
                } else {
                    RadioButton rb = new RadioButton(this, null);
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
            patientId.setText(measureTypeCode.toString());
            if (v.getId() < 4) {
                measures2.clearCheck();
            } else {
                measures.clearCheck();
            }

        }
    }

}
