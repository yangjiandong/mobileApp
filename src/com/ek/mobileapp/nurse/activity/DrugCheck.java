package com.ek.mobileapp.nurse.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ek.mobileapp.R;
import com.ek.mobileapp.action.MobLogAction;
import com.ek.mobileapp.model.DrugCheckData;
import com.ek.mobileapp.model.Patient;
import com.ek.mobileapp.model.UserDTO;
import com.ek.mobileapp.nurse.action.DrugCheckAction;
import com.ek.mobileapp.nurse.adapter.DrugCheckDataListAdapter;
import com.ek.mobileapp.utils.BarCodeUtils;
import com.ek.mobileapp.utils.BluetoothService;
import com.ek.mobileapp.utils.GlobalCache;
import com.ek.mobileapp.utils.ToastUtils;
import com.ek.mobileapp.utils.UtilString;

//用药核对
public class DrugCheck extends Activity {
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

    SharedPreferences sharedPreferences;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private com.ek.mobileapp.utils.BluetoothService bluetoothService = null;
    private Object lock = new Object();
    private String numcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.drugcheck);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            LinearLayout inputkey = (LinearLayout) inflater.inflate(R.layout.drugcheck_patient_info, null);

            LinearLayout layout = (LinearLayout) findViewById(R.id.pa_infos);
            layout.addView(inputkey);

        } catch (Exception e) {
            MobLogAction.mobLogError("病人信息", e.getMessage());
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        UserDTO user = GlobalCache.getCache().getLoginuser();
        t_user_by = (TextView) findViewById(R.id.user_by);
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

        clearData();
        //振动器
        final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final int vibrationDuration = 33;
        mVibrator.vibrate(vibrationDuration);

        infolist = (ListView) findViewById(R.id.drugcheck_list);
        infolist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        refreshList();

        //蓝牙设备
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            unConnectBLuetoothImage();
        } else {
            //TODO
            connectBLuetoothImage();
        }

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

    private void stopAnimation(int imageViewId) {
        ImageView imageView = (ImageView) findViewById(imageViewId);
        imageView.clearAnimation();
        imageView.setImageResource(0);
    }

    //只负责显示数据
    private void refreshPatientInfo() {

        Patient pa = GlobalCache.getCache().getCurrentPatient();
        if (pa != null) {
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

    private void playSound(String soundContent) {

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
                playSound(message);
                break;
            }
            case 2: {
                refreshList();
                playSound(message);
                break;
            }
            case 3: {
                playSound(message);
                break;
            }
            case 0: {
                //error
            }
            default: {

            }
            }
            //super.handleMessage(msg);
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

    private void connectBLuetoothImage() {
        ImageView imageView = (ImageView) findViewById(R.id.showBluetooth);
        imageView.setImageResource(R.drawable.bluetooth);//scanner);
    }

    private void unConnectBLuetoothImage() {
        ImageView imageView = (ImageView) findViewById(R.id.showBluetooth);
        imageView.clearAnimation();
        imageView.setImageResource(0);
    }

    private synchronized void setupConnect() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        bluetoothService = new BluetoothService(this, getUIHandler());

        // Initialize the buffer for outgoing messages
        // mOutStringBuffer = new StringBuffer("");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
        case BluetoothService.REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras().getString(BluetoothService.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                bluetoothService.connect(device);
            }
            break;
        case BluetoothService.REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupConnect();
            } else {
                // User did not enable Bluetooth or an error occured

                //Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }

    //蓝牙处理
    Handler UIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:

                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:

                    break;
                case BluetoothService.STATE_CONNECTING:
                    connectBLuetoothImage();

                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    unConnectBLuetoothImage();
                    break;
                }
                break;
            case BluetoothService.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);

                break;
            case BluetoothService.MESSAGE_READ:
                //              MessageRead messageRead = new MessageRead(mConversationArrayAdapter,msg);
                //              messageRead.start();
                synchronized (lock) {
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if (readMessage.indexOf("\r") != -1) {
                        readMessage = readMessage.replace("\r", "");
                        numcode = numcode + readMessage;
                        numcode = "";
                    } else if (readMessage.indexOf("\r") == -1) {
                        numcode = numcode + readMessage;
                    }
                }
                e_barCode.setText(numcode);

                //振动器
                final Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                final int vibrationDuration = 33;
                mVibrator.vibrate(vibrationDuration);

                processGetData();

                break;
            case BluetoothService.MESSAGE_DEVICE_NAME:
                mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
                ToastUtils.show(DrugCheck.this, "连接" + mConnectedDeviceName);

                break;
            case BluetoothService.MESSAGE_TOAST:
                ToastUtils.show(DrugCheck.this, msg.getData().getString(BluetoothService.TOAST));
                break;
            }

        }
    };

    public Handler getUIHandler() {
        return UIHandler;
    }
}
