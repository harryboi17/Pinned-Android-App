package com.example.sms;

import static android.content.Intent.getIntentOld;
import static com.example.sms.ExampleAppWidgetConfig.KEY_BUTTON_TEXT;
import static com.example.sms.ExampleAppWidgetConfig.SHARED_PREF;
import static com.example.sms.MainActivity.MY_NAME;
import static com.example.sms.MainActivity.MY_NUMBER;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class ExampleAppWidgetProvider extends AppWidgetProvider {

    public static ExampleAppWidgetProvider instance;
    public static ExampleAppWidgetProvider Instance(){
        return instance;
    }
    public static final String ACTION_TOAST = "actionToast";
    public static final String EXTRA_ITEM_POSITION = "extraItemPosition";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            instance = this;
            // Create an Intent to launch ExampleActivity
            Intent buttonIntent = new Intent(context, MainActivity.class);
            PendingIntent buttonPendingIntent = PendingIntent.getActivity(context, 0, buttonIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
            String buttonText = prefs.getString(KEY_BUTTON_TEXT+appWidgetId, "Open App");
            String nameText = prefs.getString(MY_NAME, "Unknown Number");
            String phoneText = prefs.getString(MY_NUMBER, "");

            Intent serviceIntent = new Intent(context, ExampleWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            Intent clickIntent = new Intent(context, ExampleAppWidgetProvider.class);
            clickIntent.setAction(ACTION_TOAST);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_appwidget_preview);
            views.setOnClickPendingIntent(R.id.button, buttonPendingIntent);
            views.setCharSequence(R.id.button, "setText", buttonText);
            views.setCharSequence(R.id.Name_widget, "setText", nameText);
            views.setCharSequence(R.id.Phone_widget, "setText", phoneText);
            views.setRemoteAdapter(R.id.ListId, serviceIntent);
            views.setEmptyView(R.id.ListId, R.id.example_widget_empty_view);
            views.setPendingIntentTemplate(R.id.ListId, clickPendingIntent);


            Bundle appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
            ResizeWeight(appWidgetOptions, views);
            // Tell the AppWidgetManager to perform an update on the current app widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_appwidget_preview);

        ResizeWeight(newOptions, views);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void ResizeWeight(Bundle appWidgetOptions, RemoteViews views){
        int maxWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int minWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int minHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);

        if(maxHeight > 100){
            views.setViewVisibility(R.id.widgetText, View.VISIBLE);
            views.setViewVisibility(R.id.button, View.VISIBLE);
        }else{
            views.setViewVisibility(R.id.widgetText, View.GONE);
            views.setViewVisibility(R.id.button, View.GONE);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ACTION_TOAST.equals(intent.getAction())){
            int clickedPosition = intent.getIntExtra(EXTRA_ITEM_POSITION, 100);
            Toast.makeText(context, "Clicked Position : " + clickedPosition, Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }
}