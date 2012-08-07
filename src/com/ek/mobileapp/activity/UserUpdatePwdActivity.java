package com.ek.mobileapp.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.LogonAction;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.ToastUtils;
import com.ek.mobileapp.utils.WebUtils;

//用户更改密码
public class UserUpdatePwdActivity extends Activity {
    // Debugging
    private static final String TAG = "UserUpdatePwdActivity";
    private static final boolean D = true;
    EditText oldPwd;
    EditText newPwd;
    EditText new2Pwd;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_update_pwd);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set result CANCELED incase the user backs out
        //setResult(Activity.RESULT_CANCELED);
        oldPwd = (EditText) findViewById(R.id.old_password);
        newPwd = (EditText) findViewById(R.id.new_password);
        new2Pwd = (EditText) findViewById(R.id.new2_password);

        //
        oldPwd.setText("");
        newPwd.setText("");
        new2Pwd.setText("");

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_ok);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                setProgressBarIndeterminateVisibility(true);
                updatePwd();
                //v.setVisibility(View.GONE);

                //finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void updatePwd() {
        boolean hasError = false;
        String old_password = sharedPreferences.getString("old_password", "");
        if (!oldPwd.getEditableText().toString().trim().equals(old_password)) {
            oldPwd.setError("原有密码不匹配");
            hasError = true;
        }
        if (newPwd.getEditableText().toString().trim().equals("")) {
            newPwd.setError("设置密码不能为空");
            hasError = true;
        }
        if (!newPwd.getEditableText().toString().trim().equals(new2Pwd.getEditableText().toString().trim())) {
            new2Pwd.setError("新密码确认不匹配");
            hasError = true;
        }
        if (hasError) {
            setProgressBarIndeterminateVisibility(false);
            return;
        }

        Thread login = new UpdatePwdHandler(newPwd.getEditableText().toString().trim());
        login.start();
    }

    private class UpdatePwdHandler extends Thread {
        private String password;

        private void unSuccess(String msg) {
            setProgressBarIndeterminateVisibility(false);
            ToastUtils.show(UserUpdatePwdActivity.this, msg);
        }

        public UpdatePwdHandler(String pwd) {
            this.password = pwd;
        }

        public void run() {

            int res = LogonAction.updatePwd(password);
            if (res == WebUtils.SUCCESS) {
                //重新更新密码
                Editor edit = sharedPreferences.edit();
                edit.putString("old_password", password);

                boolean issave = sharedPreferences.getBoolean("issave", false);
                if (issave) {
                    edit.putString("password", password);
                }
                edit.commit();

                UserDTO user = GlobalCache.getCache().getLoginuser();
                user.setPassword(password);

                ToastUtils.show(UserUpdatePwdActivity.this, "更改密码成功");
                //
                finish();
            } else {
                unSuccess("更新失败");
            }
        }
    }

}
