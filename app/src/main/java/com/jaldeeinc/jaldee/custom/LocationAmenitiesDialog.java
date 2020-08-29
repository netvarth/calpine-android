package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.LAmenitiesListingAdapter;
import com.jaldeeinc.jaldee.adapter.ParkingModel;

import java.util.ArrayList;

public class LocationAmenitiesDialog extends Dialog {

    Context context;
    RecyclerView rvList;
    ArrayList<ParkingModel> listType = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter serviceListingAdapter;



    public LocationAmenitiesDialog(@NonNull Context context, ArrayList<ParkingModel> listType) {
        super(context);
        this.context = context;
        this.listType = listType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services_list);

        rvList = findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        serviceListingAdapter = new LAmenitiesListingAdapter(listType,context);
        rvList.setAdapter(serviceListingAdapter);

    }

}