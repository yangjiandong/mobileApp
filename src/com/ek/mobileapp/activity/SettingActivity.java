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

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.utils.WebUtils;

//登录时设置界面
public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener

{
    EditTextPreference settingIp;
    CheckBoxPreference settingUseVociePref;
    CheckBoxPreference settingUpdate;
    EditTextPreference settingBlueToothScanner;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从xml文件中添加Preference项
        addPreferencesFromResource(R.xml.setting);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //
        settingUseVociePref = (CheckBoxPreference) findPreference("setting_use_vocie");
        settingUpdate = (CheckBoxPreference) findPreference("setting_update");
        settingIp = (EditTextPreference) findPreference("setting_http_ip");
        settingBlueToothScanner = (EditTextPreference) findPreference("setting_bluetooth_scanner");

        //TODO
        //这里有点多余,现有的参数都会影响SharedPreferences
        settingUseVociePref.setOnPreferenceChangeListener(this);
        settingUpdate.setOnPreferenceChangeListener(this);
        settingIp.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //Log.v("SystemSetting", "preference is changed");
        //Log.v("Key_SystemSetting", preference.getKey());
        String ip = sharedPreferences.getString("setting_http_ip", WebUtils.HOST);
        try {
            //用户没有登录,异常
            MobLogAction.mobLogInfo("SystemSetting", preference.getKey() + ":" + newValue, ip);
        } catch (Exception e) {
            Log.e("Key_SystemSetting", e.getMessage());
        }
        //判断是哪个Preference改变了
        if (preference.getKey().equals("setting_use_vocie")) {
            Log.v("SystemSetting", "checkbox preference is changed " + newValue);
        } else if (preference.getKey().equals("setting_update")) {

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
