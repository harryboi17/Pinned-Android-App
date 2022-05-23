package com.example.sms;

import static com.example.sms.MyReceiver.MSG_KEY;
import static com.example.sms.MyReceiver.PHONE_KEY;
import static com.example.sms.MyReceiver.SHRD_PREF;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ExampleAppWidgetItem extends AppCompatActivity {
    TextView Name;
    TextView PhoneNo;
    TextView Body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_app_widget_item);

        Body = findViewById(R.id.Body_id);

    }
}