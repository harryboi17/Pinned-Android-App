package com.example.sms;

import static com.example.sms.ExampleAppWidgetProvider.ACTION_TOAST;
import static com.example.sms.ExampleAppWidgetProvider.EXTRA_ITEM_POSITION;
import static com.example.sms.MyReceiver.CNT;
import static com.example.sms.MyReceiver.COUNT;
import static com.example.sms.MyReceiver.MSG_KEY;
import static com.example.sms.MyReceiver.NAME_KEY;
import static com.example.sms.MyReceiver.PHONE_KEY;
import static com.example.sms.MyReceiver.SHRD_PREF;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

public class ExampleWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ExampleWidgetItemFactory(getApplicationContext(), intent);
    }

    class ExampleWidgetItemFactory implements RemoteViewsFactory{
        private Context context;
        private int appwidgetId;
//        SharedPreferences prefs = getSharedPreferences(SHRD_PREF, MODE_PRIVATE);

        ExampleWidgetItemFactory(Context context, Intent intent){
            this.context = context;
            this.appwidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            //connect to a data Source
            SystemClock.sleep(1000);
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            //close data source
        }

        @Override
        public int getCount() {
//            return prefs.getInt(COUNT, 0);
            DBHandler db = new DBHandler(getApplicationContext());
            return db.getCount();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_app_widget_item);

            DBHandler db = new DBHandler(getApplicationContext());
            Message contact = db.getContactByPosition(i);

//            String phoneNo = prefs.getString(PHONE_KEY+i, "Unknown No");
//            String body = prefs.getString(MSG_KEY+i, "");
//            String name = prefs.getString(NAME_KEY+i, "Unknown Contact");
//
//            views.setTextViewText(R.id.Phone_id, phoneNo);
//            views.setTextViewText(R.id.Body_id, body);
//            views.setTextViewText(R.id.Name_id, name);


//            views.setTextViewText(R.id.Phone_id, contact.getPhoneNo());
            views.setTextViewText(R.id.Body_id, contact.getId() + ". "  + contact.getBody());
//            views.setTextViewText(R.id.Name_id, contact.getName());

            Intent fillIntent = new Intent();
            fillIntent.setAction(ACTION_TOAST);
            fillIntent.putExtra(EXTRA_ITEM_POSITION, i);
            context.sendBroadcast(fillIntent);
            views.setOnClickFillInIntent(R.id.item_linear_layout, fillIntent);

            SystemClock.sleep(100);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
