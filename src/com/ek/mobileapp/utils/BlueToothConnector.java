package com.ek.mobileapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ek.mobileapp.action.MobLogAction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class BlueToothConnector extends Thread {
    //public static final int NOBLUETOOTH = -1;
    public static final int CONNECTED = 1;
    public static final int READ = 0;
    private static final int REQUEST_ENABLE_BT = 10;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //获取蓝牙本地对象
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BlueToothReceive blueToothReceive;
    AtomicBoolean isActive = new AtomicBoolean(true);

    //

    public AtomicBoolean getIsActive() {
        return isActive;
    }

    public void setIsActive(AtomicBoolean isActive) {
        this.isActive = isActive;
    }

    ConnectThread mConnectThread;

    public ConnectThread getmConnectThread() {
        return mConnectThread;
    }

    public void setmConnectThread(ConnectThread mConnectThread) {
        this.mConnectThread = mConnectThread;
    }

    public BlueToothConnector(BlueToothReceive blueToothReceive) {
        this.blueToothReceive = blueToothReceive;
    }

    public void mystop() {
        mConnectThread.cancel();
        mConnectThread.stop();
        mConnectThread = null;
    }

    public void run() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断BluetoothAdapter是否为空，如果为空，表明本机没有蓝牙设备
        if (mBluetoothAdapter == null)
            return;
        //判断蓝牙是否开启
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ////设置蓝牙可见性的时间，方法本身规定最多可见300秒
            //enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            blueToothReceive.getContext().startActivity(enableBtIntent);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(blueToothReceive
                .getContext());
        String o = sharedPreferences.getString("setting_bluetooth_scanner", "SUMLUNG Device");
        connectToDevice(o);
    }

    public void connectToDevice(String name) {
        //获取已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices == null)
            return;
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(name)) {
                    mConnectThread = new ConnectThread(device, this.blueToothReceive);
                    mConnectThread.start();
                }
            }
        } else {
            // System.out.println(TAG+"device is not bonded");
        }
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        BlueToothReceive blueToothReceive;

        public ConnectThread(BluetoothDevice device, BlueToothReceive blueToothReceive) {
            BluetoothSocket tmp = null;
            this.mmDevice = device;

            this.blueToothReceive = blueToothReceive;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                MobLogAction.mobLogError("create蓝牙设备", e.getMessage());
            }

            mmSocket = tmp;
        }

        public void run() {
            BufferedReader reader = null;
            try {
                mmSocket.connect();
                // Log.e(TAG, "connected");
                sendResult("蓝牙设备已连接", CONNECTED);
                InputStream in = mmSocket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                while (true) {
                    // line = reader.readLine();
                    char[] line = new char[1024];
                    reader.read(line);
                    if (line == null)
                        break;
                    String receive = new String(line).trim();
                    sendResult(receive, READ);
                    // Log.e(TAG + " content", receive);
                }
                reader.close();
            } catch (IOException e) {
                //if (isActive.get()) {
                    MobLogAction.mobLogError("蓝牙设备", e.getMessage());
                    sendResult("蓝牙未连接", CONNECTED);
                //}
                try {
                    mmSocket.close();
                    if (reader != null)
                        reader.close();
                } catch (IOException closeException) {
                }
                return;
            }

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }

        private void sendResult(String msg, int type) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt("type", type);
            bundle.putString("msg", msg);
            message.setData(bundle);

            this.blueToothReceive.getUIHandler().sendMessage(message);
        }
    }
}
