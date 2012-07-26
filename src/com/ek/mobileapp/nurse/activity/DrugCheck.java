package com.ek.mobileapp.nurse.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.DrugCheckData;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.nurse.action.DrugCheckAction;
import com.ek.mobileapp.nurse.adapter.DrugCheckDataListAdapter;
import com.ek.mobileapp.utils.BarCodeUtils;
import com.ek.mobileapp.utils.BlueToothConnector;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.ToastUtils;
import com.ek.mobileapp.utils.UtilString;

//用药核对
public class DrugCheck extends NurseBaseActivity {
    List<DrugCheckData> list = new ArrayList<DrugCheckData>();

    EditText e_barCode;
    TextView t_patientId;
    TextView t_patientName;
    TextView t_age;
    TextView t_sex;
    TextView t_bedNo;
    TextView t_doctor;
    TextView t_user_by;
    TextView t_deptName;;
    ListView infolist;

    MediaPlayer mMediaPlayer;

    Button commitBtn;

    SharedPreferences sharedPreferences;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        HenorShu();
        showUi();
        refreshPatientInfo();
    }

   public void HenorShu() {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {

            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Toast.makeText(getApplicationContext(), "切换为横屏", Toast.LENGTH_SHORT).show();
                this.setContentView(R.layout.drugcheck_land);
                LinearLayout inputkey = (LinearLayout) inflater.inflate(R.layout.drugcheck_patient_info_land, null);
                LinearLayout layout = (LinearLayout) findViewById(R.id.drugcheck_pa_infos_land);
                layout.addView(inputkey);

            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Toast.makeText(getApplicationContext(), "切换为竖屏", Toast.LENGTH_SHORT).show();
                this.setContentView(R.layout.drugcheck);
                LinearLayout inputkey = (LinearLayout) inflater.inflate(R.layout.drugcheck_patient_info, null);
                LinearLayout layout = (LinearLayout) findViewById(R.id.drugcheck_pa_infos);
                layout.addView(inputkey);
            }
        } catch (Exception e) {
            MobLogAction.getMobLogAction().mobLogError("病人信息", e.getMessage());
        }
    }

    @Override
    protected void createUi() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        HenorShu();
        //setContentView(R.layout.drugcheck);

        showUi();

        //振动器
        final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final int vibrationDuration = 33;
        mVibrator.vibrate(vibrationDuration);
        mMediaPlayer = MediaPlayer.create(DrugCheck.this, R.raw.voice);

    }

    private void showUi() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        UserDTO user = GlobalCache.getCache().getLoginuser();
        t_user_by = (TextView) findViewById(R.id.drugcheck_user_by);
        t_user_by.setText("操作人: " + user.getName() + " - " + user.getDepartName());

        e_barCode = (EditText) findViewById(R.id.drugcheck_barcode);
        e_barCode.setInputType(InputType.TYPE_NULL);

        t_patientId = (TextView) findViewById(R.id.drugcheck_patientId);
        t_patientName = (TextView) findViewById(R.id.drugcheck_name);

        t_age = (TextView) findViewById(R.id.drugcheck_age);
        t_sex = (TextView) findViewById(R.id.drugcheck_sex);
        t_bedNo = (TextView) findViewById(R.id.drugcheck_bedNo);
        t_doctor = (TextView) findViewById(R.id.drugcheck_doctor);
        t_deptName = (TextView) findViewById(R.id.drugcheck_deptName);

        commitBtn = (Button) findViewById(R.id.drugcheck_commit);
        commitBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (t_patientId.getText().toString().trim().equals("")) {
                    return;
                }
                showProcessingImage(R.id.loadingImageView);

                processCommitData();

            }
        });

        clearData();

        infolist = (ListView) findViewById(R.id.drugcheck_list);
        infolist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void clearData() {
        t_patientId.setText("");
        t_patientName.setText("");
        t_sex.setText("");
        t_age.setText("");
        t_bedNo.setText("");
        t_doctor.setText("");
        t_deptName.setText("");
    }

    //只负责显示数据
    private void refreshPatientInfo() {

        Patient pa = GlobalCache.getCache().getCurrentPatient();
        if (pa != null) {
            t_patientId.setText(pa.getPatientId());
            t_patientName.setText(pa.getPatientName());
            t_sex.setText(pa.getSex());
            t_age.setText(pa.getAge());
            t_bedNo.setText(pa.getBedNo());
            t_doctor.setText(pa.getDoctorName());
            t_deptName.setText(pa.getDeptName());

            refreshList();
        } else {
            stopAnimation(R.id.loadingImageView);
            ToastUtils.show(this, "没有找到对应病人");
        }

    }

    private void refreshList() {
        list = GlobalCache.getCache().getDrugCheckDatas();
        DrugCheckDataListAdapter adapter = new DrugCheckDataListAdapter(DrugCheck.this);
        adapter.setList(list);
        infolist.setAdapter(adapter);

    }

    //开始处理提交
    private void processCommitData() {

        CommitData commit = new CommitData(commitDataHandler);

        Thread thread = new Thread(commit);
        thread.start();
    }

    //回调函数，显示结果
    Handler commitDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            String message = msg.getData().getString("msg");

            switch (type) {
            case 1: {
                GlobalCache.getCache().setDrugCheckDatas(new ArrayList<DrugCheckData>());
                refreshList();
                showMessage(message);
                break;
            }
            case 0: {
                showMessage(message);
            }
            default: {

            }
            }
            stopAnimation(R.id.loadingImageView);
        }
    };

    //真正的取数过程
    class CommitData implements Runnable {
        Handler handler;

        public CommitData(Handler h) {
            this.handler = h;
        }

        public void run() {
            Message message = Message.obtain();
            try {

                String ret = DrugCheckAction.commitHis();
                if (ret.equals("-1")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    bundle.putString("msg", "提交失败");
                    message.setData(bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 1);
                    bundle.putString("msg", "提交成功");
                    message.setData(bundle);
                }

            } catch (Exception e) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                bundle.putString("msg", e.getMessage());
                message.setData(bundle);
            }
            this.handler.sendMessage(message);
        }

    }

    //开始处理取数
    private void processGetData() {

        GetDataByBarCode saveData = new GetDataByBarCode(getDataHandler);

        Thread thread = new Thread(saveData);
        thread.start();
    }

    //回调函数，显示结果
    Handler getDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            String message = msg.getData().getString("msg");

            switch (type) {
            case 1: {
                refreshPatientInfo();
                showMessage(message);
                break;
            }
            case 2: {
                refreshList();
                showMessage(message);
                break;
            }
            case 3: {
                mMediaPlayer.start();
                showMessage(message);
                break;
            }
            case 0: {
                //error
            }
            default: {

            }
            }
            //super.handleMessage(msg);
            stopAnimation(R.id.loadingImageView);
        }
    };

    //真正的取数过程
    class GetDataByBarCode implements Runnable {
        Handler handler;

        public GetDataByBarCode(Handler h) {
            this.handler = h;
        }

        public void run() {
            Message message = Message.obtain();
            try {

                if (BarCodeUtils.isPatientTM(e_barCode.getText().toString().trim())) {
                    String ret = DrugCheckAction.getPatient(e_barCode.getText().toString().trim());
                    if (UtilString.isBlank(t_patientId.getText().toString()) && ret.equals("-1")) {
                        return;
                    } else if (!UtilString.isBlank(t_patientId.getText().toString()) && ret.equals("-1")) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 0);
                        bundle.putString("msg", "找不到住院号为" + t_patientId.getText().toString().trim() + "的病人");
                        message.setData(bundle);
                    } else {

                        DrugCheckAction.queryData(e_barCode.getText().toString().trim());
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 1);
                        bundle.putString("msg", "病人" + GlobalCache.getCache().getCurrentPatient().getPatientName());
                        message.setData(bundle);
                    }
                } else {
                    String ret = DrugCheckAction.getData(GlobalCache.getCache().getCurrentPatient().getPatientId(),
                            e_barCode.getText().toString().trim());
                    if (ret.equals("-1")) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 3);
                        bundle.putString("msg", "请检查用药");
                        message.setData(bundle);
                    } else if (ret.equals("1")) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 2);
                        bundle.putString("msg", "正确");
                        message.setData(bundle);
                    }
                }
            } catch (Exception e) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                bundle.putString("msg", e.getMessage());
                message.setData(bundle);
            }
            this.handler.sendMessage(message);
        }

    }

    //需实现的方法
    public void receiveBlueToothMessage(String msg, int type) {
        //
    }

    public Context getContext() {
        return this;
    }

    Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");
            switch (type) {
            case BlueToothConnector.UNCONNECTED:
                unConnectBLuetoothImage();
                break;
            case BlueToothConnector.CONNECTED:
                //有蓝牙设备
                connectBLuetoothImage();
                break;
            case BlueToothConnector.READ:

                String p = msg.getData().getString("msg");
                e_barCode.setText(p);

                //振动器
                final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                final int vibrationDuration = 33;
                mVibrator.vibrate(vibrationDuration);

                processGetData();

                break;

            default: {

            }
            }
        }
    };

    public Handler getUIHandler() {
        return UIHandler;
    }

    // 释放播放器资源
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        releaseMediaPlayer();
    }

}
