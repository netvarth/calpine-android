package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DonationServiceDialog extends Dialog {

    Context context;
    TextView tv_price, tv_service, tv_desc, tv_duration, tv_maxvalue, tv_minvalue, tv_multiples;
    String name, duration, price, desc = "";
    Toolbar toolbar;
    ArrayList<SearchService> mGallery;
    ArrayList<SearchDonation> dGallery;
    ArrayList<SearchAppointmentDepartmentServices> aGallery;
    ImageView i_servicegallery;
    String title, from;
    TextView tv_toolbartitle, tv_descVal;
    ImageView i_backpress;
    boolean isTaxable, isPrepayment;
    LinearLayout Lprepayment, LserviceLayout, LminAmountlayout, LmaxAmountlayout, Lmultilayout, Ldurationlayout;
    TextView txtpreVal;
    String MinPrePaymentAmount, maxDonationAmount, minDonationAmount;
    SearchDonation donationInfo;
    String minAmount,maxAmount,multiples;

    public DonationServiceDialog(Context context, SearchDonation donationInfo) {
        super(context);
        this.context = context;
        this.donationInfo = donationInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_new);

        initializations();

        minAmount = String.valueOf(donationInfo.getMinDonationAmount());
        maxAmount = String.valueOf(donationInfo.getMaxDonationAmount());
        multiples = String.valueOf(donationInfo.getMultiples());


        if (donationInfo.getName() != null) {
            tv_toolbartitle.setVisibility(View.VISIBLE);
            tv_toolbartitle.setText(donationInfo.getName());
        } else {
            tv_toolbartitle.setVisibility(View.GONE);
        }


        if(minAmount !=null){
            tv_minvalue.setVisibility(View.VISIBLE);
            tv_minvalue.setText("₹ " + minAmount);
            LminAmountlayout.setVisibility(View.VISIBLE);
        }else{
            tv_minvalue.setVisibility(View.GONE);
            LminAmountlayout.setVisibility(View.GONE);
        }

        if(maxAmount!=null){
            tv_maxvalue.setVisibility(View.VISIBLE);
            tv_maxvalue.setText("₹ " + maxAmount);
            LmaxAmountlayout.setVisibility(View.VISIBLE);
        }else{
            tv_maxvalue.setVisibility(View.GONE);
            LmaxAmountlayout.setVisibility(View.GONE);
        }

        if(multiples!=null){
            tv_multiples.setVisibility(View.VISIBLE);
            tv_multiples.setText(multiples);
            Lmultilayout.setVisibility(View.VISIBLE);
        }else{
            tv_multiples.setVisibility(View.GONE);
            Lmultilayout.setVisibility(View.GONE);
        }

        if (donationInfo.isPrePayment()) {
            Lprepayment.setVisibility(View.VISIBLE);
            txtpreVal.setText("₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(donationInfo.getMinPrePaymentAmount())));
        } else {
            Lprepayment.setVisibility(View.GONE);
        }

        if (donationInfo.getDescription() != null && donationInfo.getDescription().length() > 0 && !donationInfo.getDescription().equalsIgnoreCase("")) {
            tv_desc.setVisibility(View.VISIBLE);
            tv_descVal.setVisibility(View.VISIBLE);

            tv_descVal.setText(donationInfo.getDescription());
        } else {
            tv_desc.setVisibility(View.GONE);
            tv_descVal.setVisibility(View.GONE);
        }

        if(donationInfo.getServicegallery()!=null) {
            if (donationInfo.getServicegallery().size() > 0) {
                i_servicegallery.setVisibility(View.VISIBLE);
                try {
                    Picasso.with(context).setLoggingEnabled(true);
                    Picasso.with(context).load(donationInfo.getServicegallery().get(0).getUrl()).fit().placeholder(R.drawable.icon_noimage).into(i_servicegallery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        i_servicegallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (donationInfo != null){
                    ArrayList<String> mGalleryList = new ArrayList<>();
                    for (int i = 0; i < donationInfo.getServicegallery().size(); i++) {
                        mGalleryList.add(donationInfo.getServicegallery().get(i).getUrl());
                    }

                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                    if (mValue) {
                        Intent intent = new Intent(v.getContext(), SwipeGalleryImage.class);
                        context.startActivity(intent);
                    }
                }
            }
        });


    }

    private void initializations() {

        tv_toolbartitle = (TextView) findViewById(R.id.txt_toolbartitle);
        tv_duration = findViewById(R.id.txtduration);
        tv_price = findViewById(R.id.txtprice);
        tv_service = findViewById(R.id.txtservice);
        tv_desc = findViewById(R.id.txtdesc);
        tv_descVal = findViewById(R.id.txtdescVal);
        toolbar = findViewById(R.id.toolbar);
        i_servicegallery = findViewById(R.id.img_service);
        i_backpress = findViewById(R.id.backpress);
        Lprepayment = findViewById(R.id.Lprepayment);
        LserviceLayout = findViewById(R.id.Lprice);
        LmaxAmountlayout = findViewById(R.id.Lmaxvalue);
        LminAmountlayout = findViewById(R.id.Lminvalue);
        Lmultilayout = findViewById(R.id.Lmultiples);
        Ldurationlayout = findViewById(R.id.Lduration);
        txtpreVal = findViewById(R.id.txtpreVal);
        tv_minvalue = findViewById(R.id.txtminValue);
        tv_maxvalue = findViewById(R.id.txtmaxvalue);
        tv_multiples = findViewById(R.id.txtmultiples);

        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_service.setTypeface(tyface);
        tv_duration.setTypeface(tyface);
        tv_price.setTypeface(tyface);
    }

}
