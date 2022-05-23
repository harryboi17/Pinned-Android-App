package com.example.sms;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sms.params.Params;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    public DBHandler(Context context) {
        super(context, Params.DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + Params.TABLE_NAME + "("
                + Params.KEY_ID + " INTEGER PRIMARY KEY, "
                + Params.KEY_NAME + " TEXT, "
                + Params.KEY_PHONE + " TEXT, " + Params.KEY_BODY + " TEXT" + ")";
        Log.d("DB_Harshit", "Query being run is :\n" + create);
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void AddContact(Message contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Params.KEY_NAME, contact.getName());
        values.put(Params.KEY_PHONE, contact.getPhoneNo());
        values.put(Params.KEY_BODY, contact.getBody());

        db.insert(Params.TABLE_NAME, null, values);
        Log.d("DB_Harshit", "Successfully inserted " + contact.getId() + " " + contact.getName());
        db.close();
    }

    public List<Message> getAllContacts(){
        List<Message> contactsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Generating query to read from DataBase
        String select = "SELECT * FROM " + Params.TABLE_NAME;
        Cursor cursor  = db.rawQuery(select, null);

        //Loop through now
        if(cursor.moveToFirst()){
            do{
                Message contact = new Message();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNo(cursor.getString(2));
                contact.setBody(cursor.getString(3));
                contactsList.add(contact);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return contactsList;
    }

    public Message getLastMessage(){
        Message contact = new Message();
        SQLiteDatabase db = this.getReadableDatabase();

        //Generating query to read from DataBase
        String select = "SELECT * FROM " + Params.TABLE_NAME;
        Cursor cursor  = db.rawQuery(select, null);

        //Loop through now
        if(cursor.moveToLast()){
            contact.setId(Integer.parseInt(cursor.getString(0)));
            contact.setName(cursor.getString(1));
            contact.setPhoneNo(cursor.getString(2));
            contact.setBody(cursor.getString(3));
        }
        cursor.close();
        return contact;
    }

    public Message getContactById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Params.TABLE_NAME + " WHERE " + Params.KEY_ID + " = ?", new String[]{String.valueOf(id)}, null);
        Message contact = new Message();
        if(cursor != null){
            if(cursor.moveToFirst()) {
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNo(cursor.getString(2));
                contact.setBody(cursor.getString(3));
            }
            cursor.close();
        }
        return contact;
    }

    public Message getContactByPosition(int pos){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Params.TABLE_NAME, null);
        Message contact = new Message();
        if(cursor.moveToPosition(pos)){
            contact.setId(Integer.parseInt(cursor.getString(0)));
            contact.setName(cursor.getString(1));
            contact.setPhoneNo(cursor.getString(2));
            contact.setBody(cursor.getString(3));
        }
        cursor.close();
        return contact;
    }

    public int updateContact(Message contact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Params.KEY_NAME, contact.getName());
        values.put(Params.KEY_PHONE, contact.getPhoneNo());
        values.put(Params.KEY_BODY, contact.getBody());

        //Updating
        return db.update(Params.TABLE_NAME, values, Params.KEY_ID + "=?", new String[]{String.valueOf(contact.getId())});
    }

    public void deleteContactbyID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Params.TABLE_NAME, Params.KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void deleteContact(Message contact){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Params.TABLE_NAME, Params.KEY_ID + "=?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    public int getCount(){
        String query = "SELECT * FROM " + Params.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        return cursor.getCount();
    }
}

