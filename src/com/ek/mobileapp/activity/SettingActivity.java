package com.ek.mobileapp.activity;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

import com.ek.mobileapp.R;

//登录时设置界面
public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener
//,
//OnPreferenceClickListener
{
    EditTextPreference settingIp;
    CheckBoxPreference settingUseVociePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从xml文件中添加Preference项
        addPreferencesFromResource(R.xml.setting);

        //
        settingUseVociePref = (CheckBoxPreference) findPreference("setting_use_vocie");
        settingIp = (EditTextPreference) findPreference("setting_http_ip");

        //
        settingUseVociePref.setOnPreferenceChangeListener(this);
        //settingUseVociePref.setOnPreferenceClickListener(this);
        settingIp.setOnPreferenceChangeListener(this);
        //settingIp.setOnPreferenceClickListener(this);

    }

    //public boolean onPreferenceClick(Preference arg0) {
    //    // TODO Auto-generated method stub
    //    return false;
    //}

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.v("SystemSetting", "preference is changed");
        Log.v("Key_SystemSetting", preference.getKey());
        //判断是哪个Preference改变了
        if (preference.getKey().equals("setting_use_vocie")) {
            Log.v("SystemSetting", "checkbox preference is changed " + newValue);
        } else if (preference.getKey().equals("setting_http_ip")) {
            Log.v("SystemSetting", "ip is changed " + newValue);
        } else {
            //如果返回false表示不允许被改变
            return false;
        }
        //返回true表示允许改变
        return true;
    }
}
