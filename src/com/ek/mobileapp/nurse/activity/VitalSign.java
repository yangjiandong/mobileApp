package com.ek.mobileapp.nurse.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.MobConstants;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.TimePoint;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.model.VitalSignData;
import com.ek.mobileapp.model.VitalSignItem;
import com.ek.mobileapp.nurse.action.VitalSignAction;
import com.ek.mobileapp.nurse.adapter.VitalSignDataGridViewAdapter;
import com.ek.mobileapp.utils.BlueToothConnector;
import com.ek.mobileapp.utils.BlueToothReceive;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.TimeTool;
import com.ek.mobileapp.utils.ToastUtils;
import com.ek.mobileapp.utils.UtilString;

public class VitalSign extends Activity implements BlueToothReceive {

    SharedPreferences sharedPreferences;

    TextView t_user_by;
    Button get_patient;
    EditText e_patientId;
    TextView t_name;
    TextView t_sex;
    TextView t_age;
    TextView t_bedNo;
    TextView t_doctor;
    EditText e_busDate;
    Spinner s_timePoint;

    protected PopupWindow selectDateView;
    protected LayoutInflater mLayoutInflater;
    protected DatePicker date_picker;
    private boolean date_changed = false;
    private ArrayAdapter<String> timePointAdapter;

    private String[] timeStr;

    GridView gridView1;
    GridView gridView2;
    //
    private int state = 0;
    private String busDate = "";
    private static final int scanPatient = 0;
    private Patient currentPatient = null;

    protected ProgressDialog proDialog;
    protected String submitString = "正在传输数据...";
    protected BlueToothConnector blueTootheConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vitalsign);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            LinearLayout inputkey = (LinearLayout) inflater.inflate(R.layout.vitalsign_patient_info, null);

            LinearLayout layout = (LinearLayout) findViewById(R.id.pa_infos);
            layout.addView(inputkey);

        } catch (Exception e) {
            MobLogAction.mobLogError("病人信息", e.getMessage());
        }

        busDate = TimeTool.getDateFormated(TimeTool.getCurrentTime());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mLayoutInflater = (LayoutInflater) VitalSign.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        UserDTO user = GlobalCache.getCache().getLoginuser();
        t_user_by = (TextView) findViewById(R.id.user_by);
        t_user_by.setText("操作人: " + user.getName() + " - " + user.getDepartName());

        get_patient = (Button) findViewById(R.id.get_patient);
        get_patient.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (e_patientId.getText().toString().trim().equals("")) {
                    return;
                }
                showProcessingImage(R.id.loadingImageView);

                processGetData();

            }
        });

        e_patientId = (EditText) findViewById(R.id.vitalsign_patientId);
        t_name = (TextView) findViewById(R.id.vitalsign_name);
        t_sex = (TextView) findViewById(R.id.vitalsign_sex);
        t_age = (TextView) findViewById(R.id.vitalsign_age);
        t_bedNo = (TextView) findViewById(R.id.vitalsign_bedNo);
        t_doctor = (TextView) findViewById(R.id.vitalsign_doctor);
        e_busDate = (EditText) findViewById(R.id.vitalsign_busDate);
        e_busDate.setTextSize(16);
        s_timePoint = (Spinner) findViewById(R.id.vitalsign_timePoint);

        //时间点
        if (GlobalCache.getCache().getTimePoints() == null || GlobalCache.getCache().getTimePoints().size() == 0) {
            VitalSignAction.getTimePoint();
        }
        List<TimePoint> times = GlobalCache.getCache().getTimePoints();
        timeStr = new String[times.size()];
        int i = 0;
        int t = 0;
        String timePoint = "";
        for (TimePoint a : times) {
            if (GlobalCache.getCache().getTimePoint() != null
                    && GlobalCache.getCache().getTimePoint().equals(a.getName())) {
                t = i;
                timePoint = GlobalCache.getCache().getTimePoint();
            }
            timeStr[i] = a.getName();
            i++;
        }

        timePointAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeStr);
        timePointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_timePoint.setAdapter(timePointAdapter);
        s_timePoint.setPrompt("选择时间点:");
        s_timePoint.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapter, View view, int selected, long arg3) {
                GlobalCache.getCache().setTimePoint(timeStr[selected]);
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        if (timePoint != null && timePoint.length() > 0) {
            GlobalCache.getCache().setTimePoint(timePoint);
            s_timePoint.setSelection(t);
        }

        //日期
        e_busDate.setInputType(InputType.TYPE_NULL);
        if (GlobalCache.getCache().getBusDate() != null && GlobalCache.getCache().getBusDate().length() > 0) {
            e_busDate.setText(GlobalCache.getCache().getBusDate());
            busDate = GlobalCache.getCache().getBusDate();
        } else {
            e_busDate.setText(busDate);
            GlobalCache.getCache().setBusDate(busDate);
        }
        e_busDate.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (selectDateView == null) {
                    initSelectDate();
                }
                if (!selectDateView.isShowing()) {
                    View view = mLayoutInflater.inflate(R.layout.activity_query, null);
                    selectDateView.showAtLocation(view, BIND_AUTO_CREATE, 0, 0);
                } else {
                    selectDateView.dismiss();
                }
            }
        });

        //
        clearData();
        if (GlobalCache.getCache().getCurrentPatient() != null) {
            e_patientId.setText(GlobalCache.getCache().getCurrentPatient().getPatientId());
        }

        gridView1 = (GridView) findViewById(R.id.gridView1);
        final List<String> numsList = new ArrayList<String>();
        final List<String> numsList2 = new ArrayList<String>();
        VitalSignAction.getItem("");
        List<VitalSignItem> vas = GlobalCache.getCache().getVitalSignItems();
        for (VitalSignItem vitalSignItem : vas) {
            if (vitalSignItem.getTypeCode().equals(MobConstants.MOB_VITALSIGN_MORE)) {
                numsList.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "(" + vitalSignItem.getUnit()
                        + ")" + "| ");
            } else {
                numsList2.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "(" + vitalSignItem.getUnit()
                        + ")" + "| ");
            }

        }

        gridView1.setAdapter(new VitalSignDataGridViewAdapter(this, numsList));
        gridView1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String code = ((TextView) v.findViewById(R.id.grid_item_code)).getText().toString();
                String name = ((TextView) v.findViewById(R.id.grid_item_label)).getText().toString();

                if (code.equals("01") || code.equals("02") || code.equals("03") || code.equals("04")) {
                    if (UtilString.isBlank(e_patientId.getText().toString().trim())
                            || GlobalCache.getCache().getCurrentPatient() == null) {
                        ToastUtils.show(VitalSign.this, "请先选择一个病人");
                        return;
                    }
                    Intent intent = new Intent(VitalSign.this, VitalSignEdit.class);
                    intent.putExtra("code", code);
                    intent.putExtra("name", name);
                    startActivity(intent);

                } else if (code.equals("99")) {

                } else {
                    Toast.makeText(getApplicationContext(), code, Toast.LENGTH_SHORT).show();
                }

            }
        });

        gridView2 = (GridView) findViewById(R.id.gridView2);
        gridView2.setAdapter(new VitalSignDataGridViewAdapter(this, numsList2));
        gridView2.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.grid_item_code)).getText(),
                        Toast.LENGTH_SHORT).show();

            }
        });

        //蓝牙设备
        blueTootheConnector = new BlueToothConnector(this);
        blueTootheConnector.setDaemon(true);
        blueTootheConnector.start();

    }

    private void clearData() {
        e_patientId.setText("");
        t_name.setText("");
        t_sex.setText("");
        t_age.setText("");
        t_bedNo.setText("");
        t_doctor.setText("");
    }

    private void showMessage(String msg) {
        //if (useVoice) {
        //    Toast.makeText(CheckMedicine.this, msg, Toast.LENGTH_SHORT).show();
        //    try {
        //        ttsService.speak(msg, TextToSpeech.QUEUE_ADD);
        //    } catch (RemoteException e) {
        // TODO Auto-generated catch block
        //        e.printStackTrace();
        //    }
        //} else {
        ToastUtils.show(this, msg);
        //}
    }

    private void showProcessingImage(int imageViewId) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.setImageResource(R.drawable.loading);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, 13, 13);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(rotateAnimation);
    }

    private void stopAnimation(int imageViewId) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.clearAnimation();
        imageView.setImageResource(0);
    }

    private void connectBLuetoothImage() {
        ImageView imageView = (ImageView) findViewById(R.id.showBluetooth);
        imageView.setImageResource(R.drawable.bluetooth);//scanner);
    }

    private void unConnectBLuetoothImage() {
        ImageView imageView = (ImageView) findViewById(R.id.showBluetooth);
        imageView.clearAnimation();
        imageView.setImageResource(0);
    }

    @Override
    public void onPause() {
        //ToastUtils.show(this, "onPause");
        super.onPause();
        //isActive.set(false);
        //AtomicBoolean isActive=new AtomicBoolean(false);
        //this.connector.setIsActive(isActive);
    }

    public void onResume() {
        //ToastUtils.show(this, "onResume");
        super.onResume();

        processGetData();
    }

    protected void onStop() {
        //ToastUtils.show(this, "onStop");
        super.onStop();
    }

    protected void onStart() {
        //ToastUtils.show(this, "onStart");
        super.onStart();
    }

    @Override
    public void onDestroy() {
        AtomicBoolean isActive = new AtomicBoolean(false);
        this.blueTootheConnector.setIsActive(isActive);
        //ToastUtils.show(this, "onDestroy");
        try {
            //没有蓝牙设备时,
            if (blueTootheConnector.isHasBlueToothDevice()) {
                // Stop the Bluetooth chat services
                blueTootheConnector.mystop();
                blueTootheConnector.stop();
                blueTootheConnector = null;
            }
        } catch (Exception e) {
            MobLogAction.mobLogError("关闭蓝牙", e.getMessage());
        }
        super.onDestroy();
    }

    private void initSelectDate() {
        View view = mLayoutInflater.inflate(R.layout.activity_date, null);
        date_picker = (DatePicker) view.findViewById(R.id.seldate);
        date_picker.init(TimeTool.getYear(), TimeTool.getMonth(), TimeTool.getDay(), new OnDateChangedListener() {

            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date_changed = true;
                busDate = TimeTool.getDateFormatedFromDataPicker(year, monthOfYear, dayOfMonth);

            }

        });

        selectDateView = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        selectDateView.setFocusable(true);
        selectDateView.setAnimationStyle(-1);
        selectDateView.update();
        Button okbtn = (Button) view.findViewById(R.id.date_ok);
        okbtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (selectDateView != null && selectDateView.isShowing()) {
                    selectDateView.dismiss();
                    e_busDate.setText(busDate);
                    GlobalCache.getCache().setBusDate(busDate);

                }
            }
        });

        Button canelbtn = (Button) view.findViewById(R.id.date_cancle);
        canelbtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (selectDateView != null && selectDateView.isShowing()) {
                    selectDateView.dismiss();
                }
            }
        });
    }

    //回调函数，显示结果
    Handler getDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.getData().getInt("type");

            switch (type) {
            case 1: {
                refreshData();
                break;
            }
            case 0: {
                String mss = msg.getData().getString("msg");
                ToastUtils.show(VitalSign.this, mss);
                MobLogAction.mobLogError("提取数据出错,请联系管理员", mss);
                break;
            }
            default: {

            }
            }
            stopAnimation(R.id.loadingImageView);
            //super.handleMessage(msg);
        }
    };

    //真正的取数过程
    class GetVitalSignData implements Runnable {
        Handler handler;

        public GetVitalSignData(Handler h) {
            this.handler = h;
        }

        public void run() {
            Message message = Message.obtain();
            try {
                //提取病人基本信息
                String ret = VitalSignAction.getPatient(e_patientId.getText().toString().trim());
                if (ret.equals("-1")) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    bundle.putString("msg", "找不到住院号为" + e_patientId.getText().toString().trim() + "的病人");
                    message.setData(bundle);
                } else {
                    VitalSignAction.getAll(GlobalCache.getCache().getCurrentPatient().getPatientId(), busDate);

                    if (GlobalCache.getCache().getTimePoint() == null) {

                    } else {

                        VitalSignAction.getOne(GlobalCache.getCache().getCurrentPatient().getPatientId(), busDate,
                                GlobalCache.getCache().getTimePoint(), "");
                    }

                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 1);
                    bundle.putString("msg", "ok");
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
        GetVitalSignData getData = new GetVitalSignData(getDataHandler);
        Thread thread = new Thread(getData);
        thread.start();
    }

    //只负责显示数据
    private void refreshData() {

        Patient pa = GlobalCache.getCache().getCurrentPatient();
        if (pa != null) {
            t_name.setText(pa.getPatientName());
            t_sex.setText(pa.getSex());
            t_age.setText(pa.getAge());
            t_bedNo.setText(pa.getBedNo());
            t_doctor.setText(pa.getDoctorName());
            //取生命体征
            refreshGrid();
        } else {
            stopAnimation(R.id.loadingImageView);
            ToastUtils.show(this, "没有找到对应病人");
        }

    }

    //只负责显示数据
    private void refreshGrid() {

        List<String> dataList1 = new ArrayList<String>();
        List<String> dataList2 = new ArrayList<String>();

        List<VitalSignData> datas = GlobalCache.getCache().getVitalSignDatas_all();
        List<VitalSignItem> items = GlobalCache.getCache().getVitalSignItems();
        for (VitalSignItem vitalSignItem : items) {
            if (vitalSignItem.getTypeCode().equals(MobConstants.MOB_VITALSIGN_MORE)) {
                if (GlobalCache.getCache().getTimePoint() == null) {
                    dataList1.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                            + vitalSignItem.getUnit() + ")" + "| ");
                }
            } else {

                boolean flag = true;
                for (VitalSignData vsd : datas) {
                    if (vsd.getItemName().equals(vitalSignItem.getName())) {
                        dataList2.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                                + vitalSignItem.getUnit() + ")" + "|" + vsd.getValue2());
                        flag = false;
                        break;
                    } else {
                        continue;
                    }

                }
                if (flag) {
                    dataList2.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                            + vitalSignItem.getUnit() + ")" + "| ");
                }

            }

        }

        gridView2.setAdapter(new VitalSignDataGridViewAdapter(VitalSign.this, dataList2));

        if (GlobalCache.getCache().getTimePoint() == null) {
            gridView1.setAdapter(new VitalSignDataGridViewAdapter(VitalSign.this, dataList1));
        } else {
            dataList1 = new ArrayList<String>();
            datas = GlobalCache.getCache().getVitalSignDatas();
            items = GlobalCache.getCache().getVitalSignItems();

            for (VitalSignItem vitalSignItem : items) {
                boolean flag = true;
                if (vitalSignItem.getTypeCode().equals(MobConstants.MOB_VITALSIGN_MORE)) {

                    for (VitalSignData vsd : datas) {
                        if (vsd.getItemName().equals(vitalSignItem.getName())) {
                            dataList1.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                                    + vitalSignItem.getUnit() + ")" + "|" + vsd.getValue1());
                            flag = false;
                            break;
                        } else {
                            continue;
                        }
                    }
                    if (flag) {
                        dataList1.add(vitalSignItem.getCode() + "|" + vitalSignItem.getName() + "("
                                + vitalSignItem.getUnit() + ")" + "| ");
                    }
                }

            }

            gridView1.setAdapter(new VitalSignDataGridViewAdapter(VitalSign.this, dataList1));
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
                //showMessage(msg.getData().getString("msg"));
                unConnectBLuetoothImage();
                break;
            case BlueToothConnector.CONNECTED:
                //有蓝牙设备
                //showMessage(msg.getData().getString("msg"));
                connectBLuetoothImage();
                break;
            case BlueToothConnector.READ:

                String p = msg.getData().getString("msg");
                e_patientId.setText(p);

                //振动器
                final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                final int vibrationDuration = 33;
                mVibrator.vibrate(vibrationDuration);

                processGetData();

                break;

            default: {

            }
            }
            //super.handleMessage(msg);
        }
    };

    public Handler getUIHandler() {
        return UIHandler;
    }
}
