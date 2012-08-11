package com.ek.mobileapp.action;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.ek.mobileapp.activity.LogonActivity;
import com.ek.mobileapp.utils.GlobalCache;

public class SMSReceiver extends BroadcastReceiver {
    static final String RECEIVER_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(RECEIVER_ACTION)) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] message = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                message[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                if (message[i].getDisplayMessageBody().toString().startsWith("ekingsoft")) {
                    //ekingsoft(04),括号内为模块代码
                    String msg = message[i].getDisplayMessageBody().toString();
                    String code = msg.substring(10, 12);
                    Intent it = new Intent(context, LogonActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("message", code);
                    it.putExtras(bundle2);
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                }
            }
        }
    }
}
