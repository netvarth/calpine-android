package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;

/**
 * Created by sharmila on 6/8/18.
 */

public class CustomSpinnerAdapterAppointment extends ArrayAdapter {
    ArrayList<SearchAppoinment> mService;
    Context mContext;

    public CustomSpinnerAdapterAppointment(Context context, int textViewResourceId,
                                ArrayList<SearchAppoinment> objects) {
        super(context, textViewResourceId, objects);
        mContext=context;
        mService=objects;

    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);

// Declaring and Typecasting the textview in the inflated layout
        TextView tvService = (TextView) itemView
                .findViewById(android.R.id.text1);



        if(mService.get(position).getVirtualCallingModes()!=null){
            if(mService.get(position).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")){
                tvService.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_phone_iphone_black_24dps,0,0,0);
            }
            else if(mService.get(position).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")){
                tvService.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsapp,0,0,0);
            } else if(mService.get(position).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")){
                tvService.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet,0,0,0);
            }
            else if(mService.get(position).getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")){
                tvService.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoom,0, 0,0);
            }


        }
        SearchAppoinment mSericeList = mService.get(position);
// Setting the text using the array

        tvService.setText(mSericeList.getName());


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
