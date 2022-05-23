package com.example.sms;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.example.sms.ExampleAppWidgetConfig.SHARED_PREF;
import static com.example.sms.MainActivity.MY_NUMBER;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    public static final String  SHRD_PREF = "sms_pref";
    public static final String MSG_KEY = "msg_key";
    public static final String PHONE_KEY = "phone_key";
    public static final String NAME_KEY = "name_key";
    public static final String COUNT = "msg_count";
    public static int CNT = 0;
    String msg = "", phoneNo = "", name = "";

    boolean bound;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "Intent Received : " + intent.getAction());
        if(intent.getAction().equals(SMS_RECEIVED)){
            Bundle dataBundle = intent.getExtras();
            if(dataBundle != null){
                Object[] mypdu = (Object[]) dataBundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[mypdu.length];

                for (int i = 0; i < mypdu.length; i++) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        String format = dataBundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i], format);

                    }else{
                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i]);
                    }

                    msg = message[i].getMessageBody();
                    phoneNo = message[i].getOriginatingAddress().trim();
                    name = getContactName(phoneNo, context);
                }

                SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
                String check_number = "+91";
                check_number = prefs.getString(MY_NUMBER, "00");
//                String action = intent.getAction();
//                if(action.equals(SMS_RECEIVED)){
//                    check_number += intent.getExtras().getString("extra");
//                }
//
                MainActivity inst = MainActivity.Instance();
//                check_number += inst.returnNumber();

                String smsMsg = "SMS from : " + name + "\nNumber : " + phoneNo + "\n" + msg;
                Toast.makeText(context, "Message : " + msg + "\nNumber : " + phoneNo, Toast.LENGTH_SHORT).show();
                if(check_number.equals("00")){
                    Toast.makeText(context, "please set a number to receive SMS from", Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(context, check_number, Toast.LENGTH_SHORT).show();

                boolean checkFlag = true;
                String check_string = "Pinned:";

                if(msg.length() >= 7) {
                    for (int i = 0; i < 7; i++) {
                        if (msg.charAt(i) != check_string.charAt(i)) {
                            checkFlag = false;
                            break;
                        }
                    }
                }
                else{
                    checkFlag = false;
                }

                if(check_number.equals(phoneNo) && checkFlag) {
                    DBHandler db = new DBHandler(context);
                    Message new_msg = new Message();
                    new_msg.setName(name);
                    new_msg.setPhoneNo(phoneNo);
                    msg = msg.substring(7);
                    new_msg.setBody(msg);
                    db.AddContact(new_msg);
                }

//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putString(MSG_KEY+CNT, msg);
//                editor.putString(PHONE_KEY+CNT, phoneNo);
//                editor.putString(NAME_KEY+CNT, name);
//                CNT++;
//                editor.putInt(COUNT, CNT);
//                editor.apply();

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                        new ComponentName(context, ExampleAppWidgetProvider.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ListId);
                try {
                    inst.msgUpdate();
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                    Log.e(TAG, "onReceive: ", e);
                }
            }
        }
    }

    public String getContactName(String phoneNumber, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }
        else{
            return "Unknown Number";
        }

        return contactName;
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        myContext.bindService(service, connection, BIND_AUTO_CREATE);
        return super.peekService(myContext, service);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };
}
