package com.example.sms;

import static com.example.sms.ExampleAppWidgetProvider.ACTION_TOAST;
import static com.example.sms.MainActivity.MY_NAME;
import static com.example.sms.MainActivity.MY_NUMBER;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ExampleAppWidgetConfig extends AppCompatActivity {
    public static final String SHARED_PREF = "pref";
    public static final String KEY_BUTTON_TEXT = "keyButtonText";

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText editTextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_app_widget_config);

        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if(extras != null){
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        if(appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            finish();
        }

        editTextButton = findViewById(R.id.edit_text_button);

//        SharedPreferences pref = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
//        String nameText = pref.getString(MY_NAME, "Unknown Number");
//        String phoneText = pref.getString(MY_NUMBER, "");
//        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.example_appwidget_preview);
//        views.setCharSequence(R.id.Name_widget, "setText", nameText);
//        views.setCharSequence(R.id.Phone_widget, "setText", phoneText);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void confirmConfiguration(View v){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        Intent buttonIntent = new Intent(this, MainActivity.class);
        PendingIntent buttonPendingIntent = PendingIntent.getActivity(this, 0, buttonIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String buttonText = editTextButton.getText().toString();
        SharedPreferences pref = getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        String nameText = pref.getString(MY_NAME, "Unknown Number");
        String phoneText = pref.getString(MY_NUMBER, "");

        Intent serviceIntent = new Intent(this, ExampleWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        Intent clickIntent = new Intent(this, ExampleAppWidgetProvider.class);
        clickIntent.setAction(ACTION_TOAST);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this, 0, clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.example_appwidget_preview);
        views.setOnClickPendingIntent(R.id.button, buttonPendingIntent);
        views.setCharSequence(R.id.button, "setText", buttonText);
        views.setCharSequence(R.id.Name_widget, "setText", nameText);
        views.setCharSequence(R.id.Phone_widget, "setText", phoneText);
        views.setRemoteAdapter(R.id.ListId, serviceIntent);
        views.setEmptyView(R.id.ListId, R.id.example_widget_empty_view);
        views.setPendingIntentTemplate(R.id.ListId, clickPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_BUTTON_TEXT + appWidgetId, buttonText);
        editor.apply();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}