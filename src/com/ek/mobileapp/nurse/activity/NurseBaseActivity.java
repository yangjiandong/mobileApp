package com.ek.mobileapp.nurse.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.utils.BlueToothConnector;
import com.ek.mobileapp.utils.BlueToothReceive;
import com.ek.mobileapp.utils.ToastUtils;
import com.shoushuo.android.tts.ITts;
import com.shoushuo.android.tts.ITtsCallback;

public abstract class NurseBaseActivity extends Activity implements BlueToothReceive {

    protected SharedPreferences sharedPreferences;

    protected LayoutInflater mLayoutInflater;
    //bluetooth
    protected BlueToothConnector blueTootheConnector;

    //TTS
    protected boolean useVoice = false;
    protected ITts ttsService;
    protected boolean ttsBound;

    //
    private ToneGenerator mToneGenerator;
    private Object mToneGeneratorLock = new Object();//监视器对象锁
    private boolean mDTMFToneEnabled; //按键操作音
    private static final int TONE_LENGTH_MS = 150;//延迟时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//
//            //if (DEVELOPER_MODE) {
//                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()   // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build());
//                 StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                //.detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
//            //}

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        useVoice = sharedPreferences.getBoolean("setting_use_vocie", false);

        createUi();

        //蓝牙设备
        blueTootheConnector = new BlueToothConnector(this);
        blueTootheConnector.setDaemon(true);
        blueTootheConnector.start();

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
        imageView.setImageResource(R.drawable.loading);
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

        //
        mDTMFToneEnabled = Settings.System.getInt(getContentResolver(), Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;//获取系统参数“按键操作音”是否开启

        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                try {
                    mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);
                    setVolumeControlStream(AudioManager.STREAM_MUSIC);
                } catch (RuntimeException e) {
                    Log.w("", "Exception caught while creating local tone generator: " + e);
                    mToneGenerator = null;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //ToastUtils.show(this, "onDestroy");
        try {
            //没有蓝牙设备时,
            if (blueTootheConnector != null){
            //if (blueTootheConnector.isHasBlueToothDevice()) {
                // Stop the Bluetooth chat services
                blueTootheConnector.mystop();
                blueTootheConnector.stop();
                blueTootheConnector = null;
            }
        } catch (Exception e) {
            MobLogAction.mobLogError("关闭蓝牙", e.getMessage());
        }
        if (ttsBound) {
            ttsBound = false;
            // 撤销tts服务
            this.unbindService(ttsConnection);
        }

        super.onDestroy();
    }

    @Override
    public void onPause() {
        //ToastUtils.show(this, "onPause");
        super.onPause();

        try {
            //没有蓝牙设备时,
            if (blueTootheConnector != null){
            //if (blueTootheConnector.isHasBlueToothDevice()) {
                // Stop the Bluetooth chat services
                blueTootheConnector.mystop();
                blueTootheConnector.stop();
                blueTootheConnector = null;
            }
        } catch (Exception e) {
            MobLogAction.mobLogError("关闭蓝牙", e.getMessage());
        }
    }

    public void onResume() {
        super.onResume();

        if (blueTootheConnector == null) {
            //蓝牙设备
            blueTootheConnector = new BlueToothConnector(this);
            blueTootheConnector.setDaemon(true);
            blueTootheConnector.start();
        }
    }

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
                // 注册回调参数
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
                // tts服务初始化
                ttsService.initialize();
                // 撤销回调参数.
                ttsService.registerCallback(ttsCallback);
            } catch (RemoteException e) {
            }
        }
    };

    protected void playTone(int tone) {
        // TODO 播放按键声音
        if (!mDTMFToneEnabled) {
            return;
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT) || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {//静音或震动时不发出按键声音
            return;
        }

        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log.w("", "playTone: mToneGenerator == null, tone: " + tone);
                return;
            }
            mToneGenerator.startTone(tone, TONE_LENGTH_MS);//发声
        }
    }

    //    protected void onResume(){
    //        super.onResume();
    //        mDTMFToneEnabled = Settings.System.getInt(getContentResolver(),
    //                Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;//获取系统参数“按键操作音”是否开启
    //
    //        synchronized(mToneGeneratorLock) {
    //            if (mToneGenerator == null) {
    //                try {
    //                    mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);
    //                    setVolumeControlStream(AudioManager.STREAM_MUSIC);
    //                } catch (RuntimeException e) {
    //                    Log.w("", "Exception caught while creating local tone generator: " + e);
    //                    mToneGenerator = null;
    //                }
    //            }
    //        }
    //    }

}