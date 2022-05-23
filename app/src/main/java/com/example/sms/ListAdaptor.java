package com.example.sms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdaptor extends BaseAdapter {
    ArrayList<Message> SmSMsg;
    Context c;

    public ListAdaptor(ArrayList<Message> smSMsg, Context c) {
        SmSMsg = smSMsg;
        this.c = c;
    }

    @Override
    public int getCount() {
        return SmSMsg.size();
    }

    @Override
    public Object getItem(int i) {
        return SmSMsg.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = null;
        LayoutInflater inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            row = inflater.inflate(R.layout.example_app_widget_item, viewGroup,
                    false);
        } else {
            row = view;
        }

        Message detail = SmSMsg.get(i);
//        TextView name = (TextView) row.findViewById(R.id.Name_id);
//        name.setText(detail.getName());
//        TextView phoneNo = (TextView) row.findViewById(R.id.Phone_id);
//        phoneNo.setText(detail.getPhoneNo());
        TextView body = (TextView) row.findViewById(R.id.Body_id);
        body.setText(detail.getId() + ". "  + detail.getBody());
        return row;
    }
}
