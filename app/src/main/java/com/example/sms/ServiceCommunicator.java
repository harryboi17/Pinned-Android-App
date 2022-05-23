package com.example.sms;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class ServiceCommunicator extends Service
{
    private MyReceiver mSMSreceiver;
    private IntentFilter mIntentFilter;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSMSreceiver = new MyReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate()
    {
        //SMS event receiver
        mSMSreceiver = new MyReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
//        super.onDestroy();

        // Unregister the SMS receiver
//        unregisterReceiver(mSMSreceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
