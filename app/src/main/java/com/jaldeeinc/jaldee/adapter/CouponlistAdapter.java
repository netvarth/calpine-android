package com.jaldeeinc.jaldee.adapter;

import android.app.Dialog;
import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.Interface.ICpn;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.CheckIn;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.response.AdvancePaymentDetails;
import com.jaldeeinc.jaldee.response.AdvancePaymentDetailsOrder;
import com.jaldeeinc.jaldee.response.CoupnResponse;
import com.jaldeeinc.jaldee.enums.*;
import com.jaldeeinc.jaldee.response.CouponSystemNote;

import java.util.ArrayList;


public class CouponlistAdapter extends RecyclerView.Adapter<CouponlistAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<CoupnResponse> m3couponList;
    String mcouponEntered;
    ArrayList<String> mcouponArraylist;
    AdvancePaymentDetails couponApliedOrNotDetails = new AdvancePaymentDetails();
    private ICpn iCpn;
    JsonObject jcl, pcl;
    CouponSystemNote notes;
    Gson gson = new Gson();
    private CouponSystemNoteAdapter mAdapter;


    public CouponlistAdapter(Context Context, ArrayList<CoupnResponse> s3couponList, String couponEntered, ArrayList<String> couponArraylist) {

        this.mContext = Context;
        this.m3couponList = s3couponList;
        this.mcouponEntered = couponEntered;
        this.mcouponArraylist = couponArraylist;


    }
    public CouponlistAdapter(Context Context, ArrayList<CoupnResponse> s3couponList, String couponEntered, ArrayList<String> couponArraylist, ICpn iCpn) {

        this.mContext = Context;
        this.m3couponList = s3couponList;
        this.mcouponEntered = couponEntered;
        this.mcouponArraylist = couponArraylist;
        this.iCpn = iCpn;


    }

    public CouponlistAdapter(Context Context, ArrayList<CoupnResponse> s3couponList, String couponEntered, ArrayList<String> couponArraylist, AdvancePaymentDetails couponApliedOrNotDetails, ICpn iCpn) {

        this.mContext = Context;
        this.m3couponList = s3couponList;
        this.mcouponEntered = couponEntered;
        this.mcouponArraylist = couponArraylist;
        this.couponApliedOrNotDetails = couponApliedOrNotDetails;
        this.iCpn = iCpn;


    }
    public CouponlistAdapter(Context Context, ArrayList<CoupnResponse> s3couponList, String couponEntered, ArrayList<String> couponArraylist, AdvancePaymentDetailsOrder couponApliedOrNotDetailsOrder, ICpn iCpn) {

        this.mContext = Context;
        this.m3couponList = s3couponList;
        this.mcouponEntered = couponEntered;
        this.mcouponArraylist = couponArraylist;
        if(couponApliedOrNotDetailsOrder.getjCouponList() != null) {
            this.couponApliedOrNotDetails.setjCouponList(couponApliedOrNotDetailsOrder.getjCouponList());
        }
        if(couponApliedOrNotDetailsOrder.getProCouponList() != null) {
            this.couponApliedOrNotDetails.setProCouponList(couponApliedOrNotDetailsOrder.getProCouponList());
        }
        this.iCpn = iCpn;


    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mcouponCode, readTerms, tvAmount, tvCpnApplied;
        ImageView delete, info;
        LinearLayout llCpnApplied;

        public MyViewHolder(View view) {
            super(view);
            mcouponCode = (TextView) view.findViewById(R.id.name);
            delete = (ImageView) view.findViewById(R.id.deletecoupon);
            readTerms = (TextView) view.findViewById(R.id.ReadTC);
            tvAmount = (TextView) view.findViewById(R.id.tv_amount);
            tvCpnApplied = (TextView) view.findViewById(R.id.tv_cpnApplied);
            llCpnApplied = (LinearLayout) view.findViewById(R.id.ll_cpnApplied);
            info = (ImageView) view.findViewById(R.id.iv_info);

        }
    }

    @Override
    public CouponlistAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coupon_list_row, parent, false);

        return new CouponlistAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CouponlistAdapter.MyViewHolder myViewHolder, final int position) {
        final String coupan = mcouponArraylist.get(position);

        Config.logV("Coupan NAme-------------------" + mcouponArraylist.get(position));
        myViewHolder.mcouponCode.setText(coupan);
        myViewHolder.readTerms.setText("Read T & C");
        try {
            if (couponApliedOrNotDetails.getjCouponList() != null || couponApliedOrNotDetails.getProCouponList() != null) {
                if (couponApliedOrNotDetails.getjCouponList() != null) {
                    jcl = couponApliedOrNotDetails.getjCouponList().getAsJsonObject(coupan);
                    if (jcl != null) {
                        notes = gson.fromJson(jcl, CouponSystemNote.class);
                        if (!notes.getSystemNote().contains(CouponSystemNotes.COUPON_APPLIED.toString())) {
                            myViewHolder.info.setVisibility(View.VISIBLE);
                            myViewHolder.llCpnApplied.setVisibility(View.GONE);
                        } else {
                            myViewHolder.info.setVisibility(View.GONE);
                            myViewHolder.llCpnApplied.setVisibility(View.VISIBLE);
                        }
                        myViewHolder.tvAmount.setText("You saved ₹" + Config.getAmountinTwoDecimalPoints(notes.getValue()));
                    }
                }
                if (couponApliedOrNotDetails.getProCouponList() != null) {
                    pcl = couponApliedOrNotDetails.getProCouponList().getAsJsonObject(coupan);
                    if (pcl != null) {
                        notes = gson.fromJson(pcl, CouponSystemNote.class);
                        if (!notes.getSystemNote().contains(CouponSystemNotes.COUPON_APPLIED.toString())) {
                            myViewHolder.info.setVisibility(View.VISIBLE);
                            myViewHolder.llCpnApplied.setVisibility(View.GONE);
                        } else {
                            myViewHolder.info.setVisibility(View.GONE);
                            myViewHolder.llCpnApplied.setVisibility(View.VISIBLE);
                        }
                        myViewHolder.tvAmount.setText("You saved ₹" + Config.getAmountinTwoDecimalPoints(notes.getValue()));
                    }
                }
            }
            myViewHolder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cpn = mcouponArraylist.get(position);
                    if (couponApliedOrNotDetails.getjCouponList() != null) {
                        jcl = couponApliedOrNotDetails.getjCouponList().getAsJsonObject(cpn);
                        if (jcl != null) {
                            notes = gson.fromJson(jcl, CouponSystemNote.class);
                        }
                    }
                    if (couponApliedOrNotDetails.getProCouponList() != null) {
                        pcl = couponApliedOrNotDetails.getProCouponList().getAsJsonObject(cpn);
                        if (pcl != null) {
                            notes = gson.fromJson(pcl, CouponSystemNote.class);
                        }
                    }
                    Dialog settingsDialog = new Dialog(mContext);
                    settingsDialog.setContentView(R.layout.couponnotes_dialog);
                    CustomTextViewBold tvCpnName = settingsDialog.findViewById(R.id.tv_cpn_name);
                    ImageView close = settingsDialog.findViewById(R.id.iv_close);
                    RecyclerView list = settingsDialog.findViewById(R.id.list);
                    tvCpnName.setText("Coupon - " + coupan);
                    Config.logV("couponSystemNotes--code-------------------------" + mcouponArraylist);
                    list.setVisibility(View.VISIBLE);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    list.setLayoutManager(mLayoutManager);
                    mAdapter = new CouponSystemNoteAdapter(mContext, notes.getSystemNote());
                    list.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    settingsDialog.show();
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settingsDialog.cancel();
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        myViewHolder.readTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < m3couponList.size(); i++) {
                    if (coupan.equalsIgnoreCase(m3couponList.get(i).getJaldeeCouponCode())) {
                        Toast.makeText(mContext, m3couponList.get(i).getConsumerTermsAndconditions(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcouponArraylist.remove(position);
                notifyDataSetChanged();

                CheckIn checkIn = new CheckIn();
                checkIn.setCouponList(mcouponArraylist);

                iCpn.cpns(mcouponArraylist);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mcouponArraylist.size();
    }

}
