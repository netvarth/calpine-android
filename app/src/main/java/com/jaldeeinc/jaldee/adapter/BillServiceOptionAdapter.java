package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.BillModel;

import java.util.ArrayList;

public class BillServiceOptionAdapter extends RecyclerView.Adapter<BillServiceOptionAdapter.BillServiceOptionAdapterViewHolder>{
    Context context;
    ArrayList<BillModel> items;
    public BillServiceOptionAdapter(ArrayList<BillModel> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public BillServiceOptionAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_discount_listrow_total, parent, false);
        BillServiceOptionAdapterViewHolder gvh = new BillServiceOptionAdapterViewHolder(queueView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(@NonNull BillServiceOptionAdapterViewHolder holder, int position) {
        holder.txtdiscountName.setText(String.valueOf(items.get(position).getServiceOptionName()) + " (Service Option)" );
        if(items.get(position).getTotalPrice() != 0.0) {

            Config.logV("VALUE @@@@@@"+String.valueOf(items.get(position).getTotalPrice()));
            holder.txtdiscountVal.setText("₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(items.get(position).getTotalPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public class BillServiceOptionAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView txtdiscountName,txtdiscountVal;

        public BillServiceOptionAdapterViewHolder(View view) {
            super(view);
            txtdiscountName = view.findViewById(R.id.txtdiscount);
            txtdiscountVal = view.findViewById(R.id.txtdiscountval);

        }
    }
}
