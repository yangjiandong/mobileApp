package com.ek.mobileapp.utils;

import android.content.Context;
import android.os.Handler;

public interface BlueToothReceive {
    public void receiveBlueToothMessage(String msg, int type);
    public Context getContext();

    public Handler getUIHandler();
}
