package com.ek.mobileapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;

import com.ek.mobileapp.action.MobLogAction;

//蓝牙线程服务
public class BlueToothConnector extends Thread {
    public static final int CONNECTED = 1;
    public static final int UNCONNECTED = 0;
    public static final int READ = 2;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //获取蓝牙本地对象
    private BluetoothAdapter mBluetoothAdapter;
    AtomicBoolean isActive = new AtomicBoolean(true);

    //动态设置接收
    AtomicReference<BlueToothReceive> blueToothReceive;

    public AtomicReference<BlueToothReceive> getBlueToothReceive() {
        return blueToothReceive;
    }

    public void setBlueToothReceive(AtomicReference<BlueToothReceive> blueToothReceive) {
        this.blueToothReceive = blueToothReceive;
    }

    boolean hasBlueToothDevice = false;
    InputThread inputThread;

    public InputThread getmConnectThread() {
        return inputThread;
    }

    public void setmConnectThread(InputThread mConnectThread) {
        this.inputThread = mConnectThread;
    }

    public BlueToothConnector() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void mystop() {
        if (inputThread != null) {
            inputThread.cancel();
            inputThread.stop();
            inputThread = null;
        }

    }

    public void run() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断BluetoothAdapter是否为空，如果为空，表明本机没有蓝牙设备
        if (mBluetoothAdapter == null) {
            setHasBlueToothDevice(false);
            return;
        }

        setHasBlueToothDevice(true);
        //判断蓝牙是否开启
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ////设置蓝牙可见性的时间，方法本身规定最多可见300秒
            enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            getBlueToothReceive().get().getContext().startActivity(enableBtIntent);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBlueToothReceive().get()
                .getContext());
        String deviceName = sharedPreferences.getString("setting_bluetooth_scanner", "");
        connectToDevice(deviceName);
    }

    public void connectToDevice(String name) {
        //获取已经配对的蓝牙设备
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices == null) {
            setHasBlueToothDevice(false);
            return;
        }

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(name)) {
                    inputThread = new InputThread(device);
                    inputThread.start();
                }
            }
        } else {
            setHasBlueToothDevice(false);
        }
    }

    //扫描
    private class InputThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public InputThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            this.mmDevice = device;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                MobLogAction.getMobLogAction().mobLogError("create蓝牙设备", e.getMessage());
            }
            mmSocket = tmp;
        }

        public void run() {
            BufferedReader reader = null;
            //Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();

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
                }
                //reader.close();
            } catch (IOException e) {
                MobLogAction.getMobLogAction().mobLogError("蓝牙设备", "蓝牙未连接," + e.getMessage());
                sendResult("蓝牙未连接", UNCONNECTED);
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

            getBlueToothReceive().get().getUIHandler().sendMessage(message);
        }
    }

    public boolean isHasBlueToothDevice() {
        return hasBlueToothDevice;
    }

    public void setHasBlueToothDevice(boolean hasBlueToothDevice) {
        this.hasBlueToothDevice = hasBlueToothDevice;
    }

    public AtomicBoolean getIsActive() {
        return isActive;
    }

    public void setIsActive(AtomicBoolean isActive) {
        this.isActive = isActive;
    }

}
