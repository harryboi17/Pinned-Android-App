package com.example.sms;

import static com.example.sms.ExampleAppWidgetConfig.SHARED_PREF;
import static com.example.sms.MyReceiver.SHRD_PREF;
import static com.example.sms.MyReceiver.SMS_RECEIVED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 0;
    ListView listView;
    Button delAll, receiveBtn;
    TextView nameTV, phoneTV;
    public static final String MY_NUMBER = "numbers";
    public static final String MY_NAME = "names";
    public static String give_number;
//    ArrayAdapter arrayAdapter;
//    ArrayList<String> ContData = new ArrayList<>();
    ArrayList<Message> ContData = new ArrayList<>();
    ListAdaptor arrayAdapter;
    public static MainActivity instance;
    DBHandler db;

    public static MainActivity Instance(){
        return instance;
    }

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    ExampleAppWidgetProvider inst = ExampleAppWidgetProvider.Instance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        String[] permission = {Manifest.permission.RECEIVE_SMS,
                               Manifest.permission.READ_CONTACTS};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, MY_PERMISSION_REQUEST);
        }

        nameTV = findViewById(R.id.textViewName);
        phoneTV = findViewById(R.id.textViewPhone);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        nameTV.setText(prefs.getString(MY_NAME, "Unknown"));
        phoneTV.setText(prefs.getString(MY_NUMBER, ""));


//        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ContData);
        db = new DBHandler(MainActivity.this);
        List<Message> AllContacts = db.getAllContacts();
        ContData.addAll(AllContacts);
        arrayAdapter = new ListAdaptor(ContData, MainActivity.this);
        listView = findViewById(R.id.listviewid);
        listView.setAdapter(arrayAdapter);

        delAll = findViewById(R.id.delAll);
        delAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setIcon(android.R.drawable.ic_dialog_alert);
                builder1.setTitle("Are you sure you want to delete?");
                builder1.setMessage("deleting all will delete every single message from your app");
                builder1.setCancelable(true);

                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "Deleted All Contacts", Toast.LENGTH_SHORT).show();
                        List<Message> AllContacts = db.getAllContacts();
                        for(Message a : AllContacts){
                            db.deleteContact(a);
                        }

                        ContData.clear();
                        arrayAdapter.notifyDataSetChanged();

                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.Instance());
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                                new ComponentName(MainActivity.Instance(), ExampleAppWidgetProvider.class));
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ListId);
                        dialog.cancel();
                    }
                });
                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        receiveBtn = findViewById(R.id.receive_id);
        receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReceiverDialogBox();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.getDragToOpenListener();

                TextView mytext = view.findViewById(R.id.Body_id);
                String value = mytext.getText().toString();

                int k = 0;
                int idx = 0;
                while(value.charAt(k) != '.'){
                    idx = idx*10;
                    idx += (value.charAt(k) - '0');
                    k++;
                }
                final int index = idx;

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        Toast.makeText(MainActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        switch (menuItem.getItemId()){
                            case R.id.delete:
                                db.deleteContactbyID(index);
                                ContData.remove(i);
                                arrayAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.Instance());
                                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                                        new ComponentName(MainActivity.Instance(), ExampleAppWidgetProvider.class));
                                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ListId);
                                return true;
                            case R.id.reset:
                                createNewAlertDialog(index, i);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                return true;
            }
        });



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission has been Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied!\n Granting Permission is necessary for this app to work correctly", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Request Code Failed, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void msgUpdate(){
//        ContData.add(new Message(name, phoneNo, body));
//        ContData.clear();
//        List<Message> AllContacts = db.getAllContacts();
//        ContData.addAll(AllContacts);
        ContData.add(db.getLastMessage());
        arrayAdapter.notifyDataSetChanged();
    }

    public void createNewAlertDialog(int idx, int i){
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final View myView = getLayoutInflater().inflate(R.layout.popup, null);
        EditText new_body = myView.findViewById(R.id.newBody);
        Button set = myView.findViewById(R.id.btnset);

        dialogBuilder.setView(myView);
        dialog = dialogBuilder.create();
        dialog.show();

        db = new DBHandler(MainActivity.this);
        Message new_Msg = db.getContactById(idx);
        new_body.setText(new_Msg.getBody());

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newMsgBody = new_body.getText().toString();
                new_Msg.setBody(newMsgBody);
                db.updateContact(new_Msg);
                ContData.get(i).setBody(newMsgBody);
                arrayAdapter.notifyDataSetChanged();

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.Instance());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                        new ComponentName(MainActivity.Instance(), ExampleAppWidgetProvider.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ListId);

                Toast.makeText(MainActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void ReceiverDialogBox(){
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final View myView = getLayoutInflater().inflate(R.layout.set_phone_popup, null);
        EditText receive_name = myView.findViewById(R.id.receive_name);
        Button set = myView.findViewById(R.id.receivebtn);

        dialogBuilder.setView(myView);
        dialog = dialogBuilder.create();
        dialog.show();

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = receive_name.getText().toString();

//                Intent intent = new Intent();
//                intent.putExtra("extra", number);
//                sendBroadcast(intent);

                give_number = number;
                String display_number = "+91" + number;
                String name = getContactName(display_number, MainActivity.this);

                SharedPreferences prefs = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(MY_NUMBER, display_number);
                editor.putString(MY_NAME, name);
                editor.apply();

                nameTV.setText(name);
                phoneTV.setText(display_number);

                RemoteViews views = new RemoteViews(MainActivity.this.getPackageName(), R.layout.example_appwidget_preview);
                views.setCharSequence(R.id.Name_widget, "setText", name);
                views.setCharSequence(R.id.Phone_widget, "setText", display_number);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.Instance());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                        new ComponentName(MainActivity.Instance(), ExampleAppWidgetProvider.class));
                appWidgetManager.partiallyUpdateAppWidget(appWidgetIds, views);

                Toast.makeText(MainActivity.this, "APPLIED", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
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

        return (Objects.equals(contactName, "") ? "Unknown" : contactName);
    }

    public String returnNumber(){
        return give_number;
    }
}