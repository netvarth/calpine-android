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

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;

public class UserAppServicesDialog extends Dialog {

    private SearchService searchService;
    Context context;
    TextView tv_price, tv_service, tv_duration, tv_maxvalue, tv_minvalue, tv_multiples;
    String name, duration, price, desc = "", multiples;
    Toolbar toolbar;
    ArrayList<SearchService> mGallery;
    ArrayList<SearchDonation> dGallery;
    ArrayList<SearchAppointmentDepartmentServices> aGallery;
    ImageView i_servicegallery;
    String title, from;
    TextView tv_toolbartitle, tv_descVal;
    ImageView i_backpress,ivClose;
    boolean isTaxable, isPrepayment;
    LinearLayout Lprepayment, LserviceLayout, LminAmountlayout, LmaxAmountlayout, Lmultilayout, Ldurationlayout;
    TextView txtpreVal;
    String MinPrePaymentAmount, maxDonationAmount, minDonationAmount;


    public UserAppServicesDialog(Context context, SearchService searchService) {
        super(context);
        this.context = context;
        this.searchService = searchService;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_new);

        initializations();

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        if (searchService.getName() != null) {
            tv_toolbartitle.setVisibility(View.VISIBLE);
            tv_toolbartitle.setText(searchService.getName());
        } else {
            tv_toolbartitle.setVisibility(View.GONE);
        }

        if (searchService.getTotalAmount() != null) {
            tv_price.setVisibility(View.VISIBLE);
            LserviceLayout.setVisibility(View.VISIBLE);
            if (searchService.isTaxable()) {
                tv_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(searchService.getTotalAmount())) + " (Tax Applicable)");
            } else {
                tv_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(searchService.getTotalAmount())));
            }

        } else {
            tv_price.setVisibility(View.GONE);
            LserviceLayout.setVisibility(View.GONE);
        }

        if (searchService.getServiceDuration() != null) {
            tv_duration.setVisibility(View.VISIBLE);
            tv_duration.setText(searchService.getServiceDuration() + " mins");
            Ldurationlayout.setVisibility(View.VISIBLE);
        } else {
            tv_duration.setVisibility(View.GONE);
            Ldurationlayout.setVisibility(View.GONE);

        }

        if (minDonationAmount != null) {
            tv_minvalue.setVisibility(View.VISIBLE);
            tv_minvalue.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(minDonationAmount)));
            LminAmountlayout.setVisibility(View.VISIBLE);
        } else {
            tv_minvalue.setVisibility(View.GONE);
            LminAmountlayout.setVisibility(View.GONE);
        }

        if (maxDonationAmount != null) {
            tv_maxvalue.setVisibility(View.VISIBLE);
            tv_maxvalue.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(maxDonationAmount)));
            LmaxAmountlayout.setVisibility(View.VISIBLE);
        } else {
            tv_maxvalue.setVisibility(View.GONE);
            LmaxAmountlayout.setVisibility(View.GONE);
        }

        if (multiples != null) {
            tv_multiples.setVisibility(View.VISIBLE);
            tv_multiples.setText(multiples);
            Lmultilayout.setVisibility(View.VISIBLE);
        } else {
            tv_multiples.setVisibility(View.GONE);
            Lmultilayout.setVisibility(View.GONE);
        }

        if (searchService.isPrePayment()) {
            Lprepayment.setVisibility(View.VISIBLE);
            txtpreVal.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(searchService.getMinPrePaymentAmount())));
        } else {
            Lprepayment.setVisibility(View.GONE);
        }

        if (searchService.getDescription() != null && searchService.getDescription().length() > 0 && !searchService.getDescription().equalsIgnoreCase("")) {
            tv_descVal.setVisibility(View.VISIBLE);
//            Typeface tyfacedesc = Typeface.createFromAsset(getAssets(),
//                    "fonts/Montserrat_Bold.otf");
//            tv_descVal.setTypeface(tyfacedesc);
            tv_descVal.setText(searchService.getDescription());
        } else {
            tv_descVal.setVisibility(View.GONE);
        }

        if (searchService.getServicegallery() != null) {
            if (searchService.getServicegallery().size() > 0) {
                i_servicegallery.setVisibility(View.VISIBLE);
                try {
                    Glide.with(context).load(searchService.getServicegallery().get(0).getUrl()).fitCenter().placeholder(R.drawable.icon_noimage).into(i_servicegallery);

//                    PicassoTrustAll.getInstance(context).setLoggingEnabled(true);
//                    PicassoTrustAll.getInstance(context).load(searchService.getServicegallery().get(0).getUrl()).fit().placeholder(R.drawable.icon_noimage).into(i_servicegallery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        i_servicegallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchService.getServicegallery() != null) {
                    if (searchService.getServicegallery().size() > 0) {
                        ArrayList<String> mGalleryList = new ArrayList<>();
                        for (int i = 0; i < searchService.getServicegallery().size(); i++) {
                            mGalleryList.add(searchService.getServicegallery().get(i).getUrl());
                        }


                        boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                        if (mValue) {

                            Intent intent = new Intent(v.getContext(), SwipeGalleryImage.class);
                            context.startActivity(intent);
                        }
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
        ivClose = findViewById(R.id.iv_close);

        Typeface tyface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_service.setTypeface(tyface);
        tv_duration.setTypeface(tyface);
        tv_price.setTypeface(tyface);
    }
}


