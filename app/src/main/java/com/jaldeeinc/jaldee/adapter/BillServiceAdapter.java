package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Typeface;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.BillModel;


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

    BIllDiscountAdapter billDiscountAdapter;

    @Override
    public void onBindViewHolder(final BillServiceAdapter.BillAdapterViewHolder holder, int position) {


//        DecimalFormat format = new DecimalFormat("0.00");
        if (billServiceData.get(position).getServiceName() != null) {
            Config.logV("ServiceNAme" + billServiceData.get(position).getServiceName());
            holder.txtservicenme.setText(billServiceData.get(position).getServiceName() + " ₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(billServiceData.get(position).getPrice()));
        } else {
            holder.txtservicenme.setText(billServiceData.get(position).getItemName() + " ₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(billServiceData.get(position).getPrice()));
        }
        //holder.txt_amount.setText("₹ " + String.valueOf(billServiceData.get(position).getPrice()));

        if (billServiceData.get(position).getGSTpercentage() == 0) {

            holder.txttax.setVisibility(View.GONE);
        } else {
            holder.txttax.setVisibility(View.VISIBLE);

        }


        /*if (billServiceData.get(position).getDiscountValue() == 0.0) {
            holder.discountlayout.setVisibility(View.GONE);
        } else {
            holder.discountlayout.setVisibility(View.VISIBLE);
            if (billServiceData.get(position).getDiscountName() != null)
                holder.txtdiscount.setText(billServiceData.get(position).getDiscountName());
            holder.txtdiscountval.setText("₹ " + String.valueOf(billServiceData.get(position).getDiscountValue()));
        }

        if (billServiceData.get(position).getCouponValue() == 0.0) {
            holder.coupanlayout.setVisibility(View.GONE);
        } else {
            holder.coupanlayout.setVisibility(View.VISIBLE);
            if (billServiceData.get(position).getCouponName() != null)
                holder.txtcoupan.setText(billServiceData.get(position).getCouponName());
            holder.txtcoupanval.setText("₹ " + String.valueOf(billServiceData.get(position).getCouponValue()));
        }*/
        if (billServiceData.get(position).getServiceOptions() != null && billServiceData.get(position).getServiceOptions().size() > 0) {
            RecyclerView.LayoutManager mLayoutMngr = new LinearLayoutManager(context);
            holder.recycleView_item_ServiceOption.setLayoutManager(mLayoutMngr);
            BillServiceOptionAdapter billServiceOptionAdapter = new BillServiceOptionAdapter(billServiceData.get(position).getServiceOptions(), context);
            holder.recycleView_item_ServiceOption.setAdapter(billServiceOptionAdapter);
            billServiceOptionAdapter.notifyDataSetChanged();
        }

        holder.qtyval.setText("₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(billServiceData.get(position).getPrice() * billServiceData.get(position).getQuantity()));

        holder.qty.setText("Qty " + String.valueOf(billServiceData.get(position).getQuantity()));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        holder.recyclerView_discount.setLayoutManager(mLayoutManager);
        if (billServiceData.get(position).getDiscount() != null) {

            if (billServiceData.get(position).getDiscount().size() > 0) {
                holder.Lsubtotal.setVisibility(View.VISIBLE);
                holder.txtsubtotalval.setText("₹\u00a0" + Config.getAmountNoOrTwoDecimalPoints(billServiceData.get(position).getNetRate()));
                billDiscountAdapter = new BIllDiscountAdapter("service", billServiceData.get(position).getDiscount(), context);
                holder.recyclerView_discount.setAdapter(billDiscountAdapter);
                billDiscountAdapter.notifyDataSetChanged();
            } else {
                holder.Lsubtotal.setVisibility(View.GONE);
            }

        } else {
            holder.Lsubtotal.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return billServiceData.size();
    }

    public class BillAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView txtservicenme, qtyval, txtsubtotal, txtsubtotalval, qty, txtcoupanval, txtdiscount, txttax;
        RecyclerView recyclerView_discount, recycleView_item_ServiceOption;
        LinearLayout Lsubtotal;

        public BillAdapterViewHolder(View view) {
            super(view);
            txtsubtotal = view.findViewById(R.id.txtsubtotal);
            //   txtcoupanval = view.findViewById(R.id.txtcoupanval);
            //txtaxval = view.findViewById(R.id.txtaxval);
            txtservicenme = view.findViewById(R.id.txtservicenme);
            txtservicenme = view.findViewById(R.id.txtservicenme);
            //txt_amount = view.findViewById(R.id.txt_amount);
            // txtdiscountval = view.findViewById(R.id.txtdiscountval);
            txtsubtotalval = view.findViewById(R.id.txtsubtotalval);
            qtyval = view.findViewById(R.id.qtyval);
            // discountlayout = view.findViewById(R.id.discountlayout);
            //  coupanlayout = view.findViewById(R.id.coupanlayout);
            Lsubtotal = view.findViewById(R.id.Lsubtotal);
            //  txtdiscount = view.findViewById(R.id.txtdiscount);
            txttax = view.findViewById(R.id.txttax);
            qty = view.findViewById(R.id.qty);
            recyclerView_discount = view.findViewById(R.id.recycle_item_discount);
            recycleView_item_ServiceOption = view.findViewById(R.id.recycle_view_item_ServiceOption);

            Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/Montserrat_Bold.otf");
            txtservicenme.setTypeface(tyface);


        }
    }
}
