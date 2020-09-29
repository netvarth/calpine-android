package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectLocation;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CheckInServiceAdapter;
import com.jaldeeinc.jaldee.adapter.LocationsAdapter;
import com.jaldeeinc.jaldee.adapter.ParkingModel;
import com.jaldeeinc.jaldee.adapter.LAmenitiesListingAdapter;
import com.jaldeeinc.jaldee.response.SearchLocation;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;
import java.util.List;

public class LocationsDialog extends Dialog implements ISelectLocation {

    Context context;
    RecyclerView rvList;
    CardView cvCancel,cvSave;
    CustomTextViewBold tvTitle;
    ArrayList<SearchLocation> locationList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter locationAdapter;
    private ISelectLocation iSelectLocation;
    private IGetSelectedLocation iGetSelectedLocation;


    public LocationsDialog(@NonNull Context context, ArrayList<SearchLocation> locationList, IGetSelectedLocation iGetSelectedLocation) {
        super(context);
        this.context = context;
        this.locationList = locationList;
        this.iGetSelectedLocation = iGetSelectedLocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locations_list);


        tvTitle = findViewById(R.id.tv_title);
        cvCancel = findViewById(R.id.cv_cancel);
        cvSave = findViewById(R.id.cv_save);
        this.iSelectLocation = this;

        rvList = findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        locationAdapter = new LocationsAdapter(locationList,context,iSelectLocation);
        rvList.setAdapter(locationAdapter);

        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

    }

    @Override
    public void sendSelectedAddress(String address, int id) {

        cvSave.setClickable(true);
        cvSave.setBackgroundResource(R.drawable.curved_save);
        cvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iGetSelectedLocation.sendAddress(address,id);
                dismiss();
            }
        });

    }
}
