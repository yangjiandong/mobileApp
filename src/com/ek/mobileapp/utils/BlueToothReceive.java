package com.ek.mobileapp.utils;

import android.content.Context;

public interface BlueToothReceive {
	public void receiveBlueToothMessage(String msg, int type);
	public Context getContext();
}
