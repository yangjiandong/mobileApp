package com.ek.mobileapp.activity;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.ek.mobileapp.R;

//登录时设置界面
public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener,
        OnPreferenceClickListener {
    CheckBoxPreference settingUseVociePref;
    String settingIp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //从xml文件中添加Preference项
        addPreferencesFromResource(R.xml.setting);


    }

    private void doSomething(int i) {
        // TODO Auto-generated method stub
        if (i == 1) {
            Toast.makeText(this, "checked!", Toast.LENGTH_SHORT).show();
        } else if (i == 0) {
            Toast.makeText(this, "unchecked!", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean onPreferenceClick(Preference arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean onPreferenceChange(Preference arg0, Object arg1) {
        // TODO Auto-generated method stub
        return false;
    }
}
