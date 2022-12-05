package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.util.ArrayList;

public class EmailsAdapter extends RecyclerView.Adapter<EmailsAdapter.ViewHolder> {

    ArrayList<SearchViewDetail> numbersList;
    private boolean isLoading = true;
    public Context context;
    private int lastPosition = -1;


    public EmailsAdapter(ArrayList<SearchViewDetail> itemsList, Context context) {
        this.context = context;
        this.numbersList = itemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.email_item, viewGroup, false);
        return new ViewHolder(v, false);


    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        SearchViewDetail number = numbersList.get(position);

        viewHolder.tvLabel.setText(number.getLabel()+" -");
        viewHolder.tvNumber.setText(number.getInstance());

    }


    @Override
    public int getItemCount() {

        return numbersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private CustomTextViewMedium tvNumber;
        private CustomTextViewLight tvLabel;


        public ViewHolder(@NonNull View itemView, boolean isLoading) {
            super(itemView);

            tvLabel = itemView.findViewById(R.id.tv_label);
            tvNumber = itemView.findViewById(R.id.tv_number);
        }
    }


}