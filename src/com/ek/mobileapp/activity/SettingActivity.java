package com.ek.mobileapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ek.mobileapp.MainApplication;
import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;

//登录时设置界面
public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener

{
    EditTextPreference settingIp;
    CheckBoxPreference settingUseVociePref;
    CheckBoxPreference settingUpdate;
    //EditTextPreference settingBlueToothScanner;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MainApplication.getInstance().addActivity(this);

        super.onCreate(savedInstanceState);
        //从xml文件中添加Preference项
        addPreferencesFromResource(R.xml.setting);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //
        settingUseVociePref = (CheckBoxPreference) findPreference("setting_use_vocie");
        settingUpdate = (CheckBoxPreference) findPreference("setting_update");
        settingIp = (EditTextPreference) findPreference("setting_http_ip");
        //settingBlueToothScanner = (EditTextPreference) findPreference("setting_bluetooth_scanner");

        //TODO
        //这里有点多余,现有的参数都会影响SharedPreferences
        settingUseVociePref.setOnPreferenceChangeListener(this);
        settingUpdate.setOnPreferenceChangeListener(this);
        settingIp.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {

        try {
            //用户没有登录,异常
            MobLogAction.getMobLogAction().mobLogInfo("SystemSetting", preference.getKey() + ":" + newValue);
        } catch (Exception e) {
            Log.e("Key_SystemSetting", e.getMessage());
        }
        //判断是哪个Preference改变了
        if (preference.getKey().equals("setting_use_vocie")) {
            Log.v("SystemSetting", "checkbox preference is changed " + newValue);
        } else if (preference.getKey().equals("setting_update")) {

        } else if (preference.getKey().equals("setting_http_ip")) {
            Log.v("SystemSetting", "ip is changed " + newValue);
            //Editor edit = sharedPreferences.edit();
            //edit.putString("setting_http_ip", newValue.toString());
            //edit.commit();

        } else {
            //如果返回false表示不允许被改变
            return false;
        }
        //返回true表示允许改变
        return true;
    }
}
