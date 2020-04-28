package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.jaldeeinc.jaldee.response.SearchVirtualFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sharmila on 22/11/18.
 */

public class SpecialisationAdapter extends RecyclerView.Adapter<SpecialisationAdapter.SpecialisationAdapterViewHolder> {
    SearchViewDetail searchViewDetail;


    public SpecialisationAdapter(SearchViewDetail searchViewDetail) {
        this.searchViewDetail = searchViewDetail;

    }

    @Override
    public SpecialisationAdapter.SpecialisationAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.specialisation, parent, false);
        SpecialisationAdapterViewHolder gvh = new SpecialisationAdapterViewHolder(queueView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(final SpecialisationAdapterViewHolder myViewHolder, int position) {
        //SearchVirtualFields virtualList = virtualFieldList.get(position);
        final String specialisation = searchViewDetail.getSpecialization().get(position).toString();
        myViewHolder.mSpecial.setText(specialisation);

    }


    @Override
    public int getItemCount() {
        return /*virtualFieldList.size();*/
                searchViewDetail.getSpecialization().size();
    }

    public class SpecialisationAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView mSpecial;


        public SpecialisationAdapterViewHolder(View view) {
            super(view);
            mSpecial = view.findViewById(R.id.special);



        }
    }
}