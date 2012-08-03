package com.ek.mobileapp.nurse.activity;

import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.ek.mobileapp.MainApplication;
import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.utils.BlueToothReceive;
import com.ek.mobileapp.utils.ToastUtils;
import com.shoushuo.android.tts.ITts;
import com.shoushuo.android.tts.ITtsCallback;

//实现蓝牙功能
public abstract class NurseBaseActivity extends Activity implements BlueToothReceive {

    protected SharedPreferences sharedPreferences;
    protected LayoutInflater mLayoutInflater;

    //TTS
    protected boolean useVoice = false;
    protected ITts ttsService;
    protected boolean ttsBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainApplication.getInstance().addActivity(this);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        useVoice = sharedPreferences.getBoolean("setting_use_vocie", false);

        createUi();
    }

    protected abstract void createUi();

    protected void showMessage(String msg) {
        if (useVoice) {
            ToastUtils.show(this, msg);
            try {
                ttsService.speak(msg, TextToSpeech.QUEUE_ADD);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.show(this, msg);
        }
    }

    protected void showOnlyMessage(String msg) {
        ToastUtils.show(this, msg);
    }

    protected void showMessageByVoice(String msg) {
        if (useVoice) {
            try {
                ttsService.speak(msg, TextToSpeech.QUEUE_ADD);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    protected void showProcessingImage(int imageViewId) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.setImageResource(R.drawable.loading2);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, 13, 13);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(rotateAnimation);
    }

    protected void stopAnimation(int imageViewId) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.clearAnimation();
        imageView.setImageResource(0);
    }

    protected void connectBLuetoothImage() {
        ImageView imageView = (ImageView) findViewById(R.id.showBluetooth);
        imageView.setImageResource(R.drawable.bluetooth);//scanner);
    }

    protected void unConnectBLuetoothImage() {
        ImageView imageView = (ImageView) findViewById(R.id.showBluetooth);
        imageView.clearAnimation();
        imageView.setImageResource(0);
    }

    protected void onStart() {
        //ToastUtils.show(this, "onStart");
        super.onStart();
        if (!ttsBound) {
            String actionName = "com.shoushuo.android.tts.intent.action.InvokeTts";
            Intent intent = new Intent(actionName);
            // 绑定tts服务
            this.bindService(intent, ttsConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        //蓝牙设备不进行释放,需单独处理
        try {
            if (ttsBound) {
                ttsBound = false;
                // 撤销tts服务
                this.unbindService(ttsConnection);
            }
        } catch (Exception e) {
            MobLogAction.getMobLogAction().mobLogError("关闭手说", e.getMessage());
        }
        super.onDestroy();
    }

    // 处理蓝牙接收方
    protected void getCurrentBlueToothConnector() {
        VitalSign.getBlueToothConnector().setBlueToothReceive(new AtomicReference<BlueToothReceive>(this));
    }

    @Override
    protected void onResume() {
        getCurrentBlueToothConnector();
        super.onResume();
    }

    protected abstract void resumeOther();

    //锁定home 键
    //@Override
    //public void onAttachedToWindow() {
    //    this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
    //    super.onAttachedToWindow();
    //}

    //

    /**
     * 回调参数.
     */
    private final ITtsCallback ttsCallback = new ITtsCallback.Stub() {
        // 朗读完毕.
        public void speakCompleted() throws RemoteException {
            // handler.sendEmptyMessage(0);
        }
    };

    /**
     * tts服务连接.
     */
    private final ServiceConnection ttsConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName arg0) {
            try {
                // 撤销回调参数.
                ttsService.unregisterCallback(ttsCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            ttsService = null;
            ttsBound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            ttsService = ITts.Stub.asInterface(service);
            ttsBound = true;
            try {
                // 注册回调参数
                ttsService.registerCallback(ttsCallback);
            } catch (RemoteException e) {
            }
        }
    };
}