package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CheckInServiceAdapter;
import com.jaldeeinc.jaldee.adapter.ParkingModel;
import com.jaldeeinc.jaldee.adapter.LAmenitiesListingAdapter;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;
import java.util.List;

public class CheckInServicesDialog extends Dialog {

    Context context;
    RecyclerView rvList;
    TextView tvTitle;
    List<SearchService> mSearchServiceList;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter checkInServiceAdapter;

    public CheckInServicesDialog(@NonNull Context context, List<SearchService> mSearchServiceList) {
        super(context);
        this.context = context;
        this.mSearchServiceList = mSearchServiceList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.services_list);

        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "font/montserrat_bold.otf");

        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setTypeface(tyface);
        tvTitle.setText("Location Amenities");

        rvList = findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(context);
        rvList.setLayoutManager(linearLayoutManager);
        checkInServiceAdapter = new CheckInServiceAdapter(mSearchServiceList,context);
        rvList.setAdapter(checkInServiceAdapter);

    }
}
