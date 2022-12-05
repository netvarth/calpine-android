package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.BillModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 12/12/18.
 */


public class BIllDiscountAdapter extends RecyclerView.Adapter<BIllDiscountAdapter.BillAdapterViewHolder> {
    private List<BillModel> billServiceData;
    Context context;
    String from;

    public BIllDiscountAdapter(String from, ArrayList<BillModel> billServiceData, Context context) {
        this.billServiceData = billServiceData;
        this.context = context;
        this.from=from;
        Config.logV("BIll SERVICE---------------" + billServiceData.size());
    }

    @Override
    public BillAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView =null;
        if(from.equalsIgnoreCase("totalbill")){
            queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_discount_listrow_total, parent, false);
        }else {
             queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_discount_listrow, parent, false);
        }
        BillAdapterViewHolder gvh = new BillAdapterViewHolder(queueView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(final BillAdapterViewHolder holder, int position) {
//        DecimalFormat format = new DecimalFormat("0.00");
        holder.txtdiscountName.setText(String.valueOf(billServiceData.get(position).getName()));
        if(billServiceData.get(position).getDiscValue()!=0.0) {
            Config.logV("VALUE @@@@@@"+String.valueOf(billServiceData.get(position).getDiscValue()));
            holder.txtdiscountVal.setText("(-)₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(billServiceData.get(position).getDiscValue()));
        }

        if(billServiceData.get(position).getCouponValue()!=0.0) {

            Config.logV("VALUE @@@@@@"+String.valueOf(billServiceData.get(position).getCouponValue()));
            holder.txtdiscountVal.setText("(-)₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(billServiceData.get(position).getCouponValue()));
        }

        if(billServiceData.get(position).getDiscountValue()!=0.0) {

            Config.logV("VALUE @@@@@@"+String.valueOf(billServiceData.get(position).getDiscountValue()));
            holder.txtdiscountVal.setText("(-)₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(billServiceData.get(position).getDiscountValue()));
        }



    }


    @Override
    public int getItemCount() {
        return billServiceData.size();
    }

    public class BillAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView txtdiscountName,txtdiscountVal;

        public BillAdapterViewHolder(View view) {
            super(view);
            txtdiscountName = view.findViewById(R.id.txtdiscount);
            txtdiscountVal = view.findViewById(R.id.txtdiscountval);



        }
    }
}