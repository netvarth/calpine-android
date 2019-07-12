package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;

/**
 * Created by sharmila on 6/8/18.
 */

public class CustomSpinnerAdapter extends ArrayAdapter {
    ArrayList<SearchService> mService;
    Context mContext;

    public CustomSpinnerAdapter(Context context, int textViewResourceId,
                                ArrayList<SearchService> objects) {
        super(context, textViewResourceId, objects);
        mContext=context;
        mService=objects;

    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spinner_layout, parent, false);

// Declaring and Typecasting the textview in the inflated layout
        TextView tvService = (TextView) itemView
                .findViewById(R.id.tvservice);
        SearchService mSericeList = mService.get(position);
// Setting the text using the array

        tvService.setText(mSericeList.getmAllService().get(position).getName());


        return itemView;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}