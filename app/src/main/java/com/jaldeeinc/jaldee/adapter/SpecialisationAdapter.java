package com.jaldeeinc.jaldee.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

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