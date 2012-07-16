package com.ek.mobileapp.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.utils.WebUtils;

public class InputDemoActivtiy extends Activity {
    Map<Integer, Integer> btns = new HashMap<Integer, Integer>();
    Button B1, B2, B3, B4, B5, B6, B7, B8, B9, B0,BClear, BDot, BEqual;
    //BSum, BDif, BMul, BDiv,
    EditText Result;
    SharedPreferences sharedPreferences;
    boolean dotPressed = false;
    boolean syntaxError = false;

    boolean textCursor = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputdemo);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout inputkey = (RelativeLayout) inflater.inflate(R.layout.inputkey, null);

        LinearLayout layout = (LinearLayout) findViewById(R.id.main);
        layout.addView(inputkey);
        initLayout();

        ClearAll();

        //振动器
        final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final int vibrationDuration = 33;

        OnClickListener myListenerNum = new OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!syntaxError) {
                        mVibrator.vibrate(vibrationDuration);
                        Integer n = btns.get(v.getId());
                        NumPressed(n.toString());
                    }
                } catch (Exception e) {
                    String ip = sharedPreferences.getString("setting_http_ip", WebUtils.HOST);
                    MobLogAction.mobLogError(InputDemoActivtiy.class.getName(), e.getMessage(), ip);
                }
            }
        };
        OnClickListener myListenerBClear = new OnClickListener() {
            public void onClick(View v) {
                mVibrator.vibrate(vibrationDuration);
                ClearAll();
            }
        };
        OnClickListener myListenerBDot = new OnClickListener() {
            public void onClick(View v) {
                if (dotPressed) return;

                if (!syntaxError) {
                    //                mVibrator.vibrate(vibrationDuration);
                    //                if (num1 == -1) {
                    //                    num1 = 0;
                    //                } else if (num2 == -1 && op != ' ') {
                    //                    num2 = 0;
                    //                    Result.append("0");
                    //                }
                    //                if (syntaxError)
                    //                    Result.setText("0.");
                    //                else if (!dotPressed)
                    //                    Result.append(".");
                    //                if (num1 == 0 && num2 == -1 && op == ' ' && Float.parseFloat(Result.getText().toString()) != 0) {
                    //                    Result.setText("0");
                    //                }
                    //                syntaxError = false;
                    NumPressed(".");
                    dotPressed = true;
                }
            }
        };
        B1.setOnClickListener(myListenerNum);
        B2.setOnClickListener(myListenerNum);
        B3.setOnClickListener(myListenerNum);
        B4.setOnClickListener(myListenerNum);
        B5.setOnClickListener(myListenerNum);
        B6.setOnClickListener(myListenerNum);
        B7.setOnClickListener(myListenerNum);
        B8.setOnClickListener(myListenerNum);
        B9.setOnClickListener(myListenerNum);
        B0.setOnClickListener(myListenerNum);
        BDot.setOnClickListener(myListenerBDot);
        BClear.setOnClickListener(myListenerBClear);

    }

    private void ClearAll() {
        dotPressed = false;
        Result.setText("");
    }

    private void NumPressed(String key) {
        Log.v("Test click", "The button " + key + " has been pressed!");

        //设置一个变量判断是否有光标
        if (textCursor == true) {
            //获得光标的位置
            int index = Result.getSelectionStart();
            //将字符串转换为StringBuffer
            StringBuffer sb = new StringBuffer(Result.getText().toString().trim());
            //将字符插入光标所在的位置
            sb = sb.insert(index, key);
            Result.setText(sb.toString());
            //设置光标的位置保持不变
            Selection.setSelection(Result.getText(), index + 1);
        } else {
            Result.setText(Result.getText().toString().trim() + key);
        }
    }

    private void initLayout() {

        btns.put(R.id.button1, 1);
        btns.put(R.id.button2, 2);
        btns.put(R.id.button3, 3);
        btns.put(R.id.button4, 4);
        btns.put(R.id.button5, 5);
        btns.put(R.id.button6, 6);
        btns.put(R.id.button7, 7);
        btns.put(R.id.button8, 8);
        btns.put(R.id.button9, 9);
        btns.put(R.id.button0, 0);

        B1 = (Button) findViewById(R.id.button1);
        B2 = (Button) findViewById(R.id.button2);
        B3 = (Button) findViewById(R.id.button3);
        B4 = (Button) findViewById(R.id.button4);
        B5 = (Button) findViewById(R.id.button5);
        B6 = (Button) findViewById(R.id.button6);
        B7 = (Button) findViewById(R.id.button7);
        B8 = (Button) findViewById(R.id.button8);
        B9 = (Button) findViewById(R.id.button9);
        B0 = (Button) findViewById(R.id.button0);
        //BSum = (Button) findViewById(R.id.buttonSum);
        //BDif = (Button) findViewById(R.id.buttonDif);
        //BMul = (Button) findViewById(R.id.buttonMul);
        //BDiv = (Button) findViewById(R.id.buttonDiv);
        BClear = (Button) findViewById(R.id.buttonClear);
        BDot = (Button) findViewById(R.id.buttonDot);
        BEqual = (Button) findViewById(R.id.buttonEq);
        Result = (EditText) findViewById(R.id.textView1);
        //
        //Result.setSelection(0);//text.length())
        Result.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Result.setInputType(InputType.TYPE_NULL); // 关闭软键盘
                return false;
            }
        });
        Result.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (Result.getText().toString().trim().length() > 0) {
                    //设置光标为可见状态
                    Result.setCursorVisible(true);
                    textCursor = true;
                }
            }
        });
    }
}
