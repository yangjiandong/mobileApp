package com.ek.mobileapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ek.mobileapp.utils.BlueToothReceive;

public class MyService extends Service {

    public boolean threadFlag = true;
    MyThread myThread;
    CommandReceiver cmdReceiver;//继承自BroadcastReceiver对象，用于得到Activity发送过来的命令

    /**************service 命令*********/
    static final int CMD_STOP_SERVICE = 0x01;
    static final int CMD_SEND_DATA = 0x02;
    static final int CMD_SYSTEM_EXIT = 0x03;
    static final int CMD_SHOW_TOAST = 0x04;

    BlueToothReceive blueToothReceive;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    public boolean bluetoothFlag = true;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address = "00:19:5D:EE:9B:8F"; // <==要连接的蓝牙设备MAC地址

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

    }

    //前台Activity调用startService时，该方法自动执行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        cmdReceiver = new CommandReceiver();
        IntentFilter filter = new IntentFilter();//创建IntentFilter对象
        //注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
        filter.addAction("android.intent.action.cmd");
        //注册Broadcast Receiver
        registerReceiver(cmdReceiver, filter);
        doJob();//调用方法启动线程
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.unregisterReceiver(cmdReceiver);//取消注册的CommandReceiver
        threadFlag = false;
        boolean retry = true;
        while (retry) {
            try {
                myThread.join();
                retry = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public class MyThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            connectDevice();//连接蓝牙设备
            while (threadFlag) {
                int value = readByte();
                if (value != -1) {
                    DisplayToast(value + "");
                }

                try {
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void doJob() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            DisplayToast("蓝牙设备不可用，请打开蓝牙！");
            bluetoothFlag = false;
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            DisplayToast("请打开蓝牙并重新运行程序！");
            bluetoothFlag = false;
            stopService();
            showToast("请打开蓝牙并重新运行程序！");
            return;
        }
        showToast("搜索到蓝牙设备!");
        threadFlag = true;
        myThread = new MyThread();
        myThread.start();

    }

    public MyService(BlueToothReceive blueToothReceive) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.blueToothReceive = blueToothReceive;
    }

    public void connectDevice() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(blueToothReceive
                .getContext());
        String deviceName = sharedPreferences.getString("setting_bluetooth_scanner", "SUMLUNG Device");

        DisplayToast("正在尝试连接蓝牙设备，请稍后····");
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices == null) {
            return;
        }
        BluetoothDevice device = null;
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device1 : pairedDevices) {
                if (device1.getName().equals(deviceName)) {
                    device = device1;
                }
            }
        }
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            DisplayToast("套接字创建失败！");
            bluetoothFlag = false;
        }
        DisplayToast("成功连接蓝牙设备！");
        mBluetoothAdapter.cancelDiscovery();
        try {
            btSocket.connect();
            DisplayToast("连接成功建立，可以开始操控了!");
            showToast("连接成功建立，可以开始操控了!");
            bluetoothFlag = true;
        } catch (IOException e) {
            try {
                btSocket.close();
                bluetoothFlag = false;
            } catch (IOException e2) {
                DisplayToast("连接没有建立，无法关闭套接字！");
            }
        }

        if (bluetoothFlag) {
            try {
                inStream = btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            } //绑定读接口

            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            } //绑定写接口

        }
    }

    public void sendCmd(byte cmd, int value)//串口发送数据
    {
        if (!bluetoothFlag) {
            return;
        }
        byte[] msgBuffer = new byte[5];
        msgBuffer[0] = cmd;
        msgBuffer[1] = (byte) (value >> 0 & 0xff);
        msgBuffer[2] = (byte) (value >> 8 & 0xff);
        msgBuffer[3] = (byte) (value >> 16 & 0xff);
        msgBuffer[4] = (byte) (value >> 24 & 0xff);

        try {
            outStream.write(msgBuffer, 0, 5);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readByte() {//return -1 if no data
        int ret = -1;
        if (!bluetoothFlag) {
            return ret;
        }
        try {
            ret = inStream.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void stopService() {//停止服务
        threadFlag = false;//停止线程
        stopSelf();//停止服务
    }

    public void showToast(String str) {//显示提示信息
        Intent intent = new Intent();
        intent.putExtra("cmd", CMD_SHOW_TOAST);
        intent.putExtra("str", str);
        intent.setAction("android.intent.action.lxx");
        sendBroadcast(intent);
    }

    public void DisplayToast(String str) {
        Log.d("Season", str);
    }

    //接收Activity传送过来的命令
    private class CommandReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.cmd")) {
                int cmd = intent.getIntExtra("cmd", -1);//获取Extra信息
                if (cmd == CMD_STOP_SERVICE) {
                    stopService();
                }

                if (cmd == CMD_SEND_DATA) {
                    byte command = intent.getByteExtra("command", (byte) 0);
                    int value = intent.getIntExtra("value", 0);
                    sendCmd(command, value);
                }

            }
        }
    }

}
