package com.nv.youneverwait.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.model.BillModel;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharmila on 3/10/18.
 */

public class BillServiceAdapter extends RecyclerView.Adapter<BillServiceAdapter.BillAdapterViewHolder> {
    private List<BillModel> billServiceData;
    Context context;


    public BillServiceAdapter(ArrayList<BillModel> billServiceData, Context context) {
        this.billServiceData = billServiceData;
        this.context = context;
        Config.logV("BIll SERVICE---------------" + billServiceData.size());
    }

    @Override
    public BillServiceAdapter.BillAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View queueView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_items_listrow, parent, false);
        BillServiceAdapter.BillAdapterViewHolder gvh = new BillServiceAdapter.BillAdapterViewHolder(queueView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(final BillServiceAdapter.BillAdapterViewHolder holder, int position) {


        if(billServiceData.get(position).getServiceName()!=null) {
            Config.logV("ServiceNAme" + billServiceData.get(position).getServiceName());
            holder.txtservicenme.setText(billServiceData.get(position).getServiceName());
        }else{
            holder.txtservicenme.setText(billServiceData.get(position).getItemName());
        }
        holder.txt_amount.setText("₹ " + String.valueOf(billServiceData.get(position).getPrice()));

        if(billServiceData.get(position).getGSTpercentage()==0){

            holder.taxlayout.setVisibility(View.GONE);
        }else{
            holder.taxlayout.setVisibility(View.VISIBLE);
            holder.txtaxval.setText( String.valueOf(billServiceData.get(position).getGSTpercentage())+"%");
            holder.txttax.setText("Tax(CGST: "+String.valueOf(billServiceData.get(position).getGSTpercentage()/2)+" %"+", SGST: "+String.valueOf(billServiceData.get(position).getGSTpercentage()/2)+" %)");
        }


        if(billServiceData.get(position).getDiscountValue()==0.0){
            holder.discountlayout.setVisibility(View.GONE);
        }else{
            holder.discountlayout.setVisibility(View.VISIBLE);
            holder.txtdiscountval.setText("₹ " + String.valueOf(billServiceData.get(position).getDiscountValue()));
        }

        if(billServiceData.get(position).getCouponValue()==0.0){
            holder.coupanlayout.setVisibility(View.GONE);
        }else{
            holder.coupanlayout.setVisibility(View.VISIBLE);
            holder.txtcoupanval.setText("₹ " + String.valueOf(billServiceData.get(position).getCouponValue()));
        }

        holder.qtyval.setText("₹ " + String.valueOf(billServiceData.get(position).getPrice()*billServiceData.get(position).getQuantity()));
        holder.txtsubtotalval.setText("₹ " + String.valueOf(billServiceData.get(position).getNetRate()));
        holder.qty.setText("Qty " + String.valueOf(billServiceData.get(position).getQuantity()));



    }


    @Override
    public int getItemCount() {
        return billServiceData.size();
    }

    public class BillAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView txtservicenme, qtyval, txt_amount, txtdiscountval, txtsubtotalval, qty,txtcoupanval,txtaxval,txttax;
        LinearLayout coupanlayout,taxlayout,discountlayout;

        public BillAdapterViewHolder(View view) {
            super(view);
            txttax=view.findViewById(R.id.txttax);
            txtcoupanval = view.findViewById(R.id.txtcoupanval);
            txtaxval= view.findViewById(R.id.txtaxval);
            txtservicenme = view.findViewById(R.id.txtservicenme);
            txtservicenme = view.findViewById(R.id.txtservicenme);
            txt_amount = view.findViewById(R.id.txt_amount);
            txtdiscountval = view.findViewById(R.id.txtdiscountval);
            txtsubtotalval = view.findViewById(R.id.txtsubtotalval);
            qtyval = view.findViewById(R.id.qtyval);
            discountlayout = view.findViewById(R.id.discountlayout);
            coupanlayout = view.findViewById(R.id.coupanlayout);
            taxlayout = view.findViewById(R.id.taxlayout);
            qty = view.findViewById(R.id.qty);
            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            txtservicenme.setTypeface(tyface);


        }
    }
}
