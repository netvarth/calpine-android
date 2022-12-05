package com.jaldeeinc.jaldee.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.SearchAppointmentDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchService;

import java.util.ArrayList;

/**
 * Created by sharmila on 31/7/18.
 */

public class SearchServiceActivity extends AppCompatActivity {
    TextView tv_price, tv_service, tv_duration, tv_maxvalue, tv_minvalue, tv_multiples;

    String name, duration, price, desc = "", multiples;
    ArrayList<SearchService> mGallery;
    ArrayList<SearchDonation> dGallery;
    ArrayList<SearchAppointmentDepartmentServices> aGallery;
    ImageView i_servicegallery;
    TextView tv_toolbartitle, tv_descVal, tvisTax;
    String title, from, callingMode, serviceType;
    ImageView i_backpress;
    boolean isTaxable, isPrepayment;
    LinearLayout Lprepayment, LserviceLayout, LminAmountlayout, LmaxAmountlayout, Lmultilayout, Ldurationlayout;
    TextView txtpreVal;
    String MinPrePaymentAmount, maxDonationAmount, minDonationAmount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service);
        tv_duration = findViewById(R.id.txtduration);
        tv_price = findViewById(R.id.txtprice);
        tv_service = findViewById(R.id.txtservice);
        tv_descVal = findViewById(R.id.txtdescVal);
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
        tvisTax = findViewById(R.id.tv_isTax);

        i_backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            duration = extras.getString("duration");
            price = extras.getString("price");
            desc = extras.getString("desc");
            title = extras.getString("title");
            isTaxable = extras.getBoolean("taxable");
            isPrepayment = extras.getBoolean("isPrePayment");
            from = extras.getString("from");
            callingMode = extras.getString("callingMode");
            serviceType = extras.getString("serviceType");

            MinPrePaymentAmount = extras.getString("MinPrePaymentAmount");
            if (from != null) {
                if (from.equalsIgnoreCase("dnt")) {
                    dGallery = (ArrayList<SearchDonation>) getIntent().getSerializableExtra("servicegallery");
                } else if (from.equalsIgnoreCase("appt")) {
                    aGallery = (ArrayList<SearchAppointmentDepartmentServices>) getIntent().getSerializableExtra("servicegallery");
                } else {
                    mGallery = (ArrayList<SearchService>) getIntent().getSerializableExtra("servicegallery");
                }

            }

            minDonationAmount = extras.getString("minamount");
            maxDonationAmount = extras.getString("maxamount");
            multiples = extras.getString("multiples");

        }


        tv_toolbartitle = (TextView) findViewById(R.id.txt_toolbartitle);
        tv_toolbartitle.setText(title);

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_toolbartitle.setTypeface(tyface);
        tv_service.setTypeface(tyface);
        tv_duration.setTypeface(tyface);
        tv_price.setTypeface(tyface);


        if (name != null) {
            tv_service.setVisibility(View.VISIBLE);
            tv_service.setText(name);
            try {
                if (serviceType != null && serviceType.equalsIgnoreCase("virtualService")) {
                    if (callingMode.equalsIgnoreCase("Zoom")) {
                        tv_service.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                        tv_service.setCompoundDrawablePadding(10);
                    } else if (callingMode.equalsIgnoreCase("GoogleMeet")) {
                        tv_service.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                        tv_service.setCompoundDrawablePadding(10);
                    } else if (callingMode.equalsIgnoreCase("WhatsApp")) {
                        tv_service.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                        tv_service.setCompoundDrawablePadding(10);
                    } else if (callingMode.equalsIgnoreCase("phone")) {
                        tv_service.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneicon_sized, 0, 0, 0);
                        tv_service.setCompoundDrawablePadding(10);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tv_service.setVisibility(View.GONE);
        }


        if (price != null) {
            if (!price.equals("0.0")) {
                tv_price.setVisibility(View.VISIBLE);
                LserviceLayout.setVisibility(View.VISIBLE);
                if (isTaxable) {
                    tv_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(price)));
                    tvisTax.setVisibility(View.VISIBLE);
                } else {
                    tv_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(price)));
                    tvisTax.setVisibility(View.GONE);
                }
            } else {
                tv_price.setVisibility(View.GONE);
                tvisTax.setVisibility(View.GONE);
                LserviceLayout.setVisibility(View.GONE);
            }
        } else {
            tv_price.setVisibility(View.GONE);
            tvisTax.setVisibility(View.GONE);
            LserviceLayout.setVisibility(View.GONE);
        }


        if (duration != null) {
            tv_duration.setVisibility(View.VISIBLE);
            tv_duration.setText(duration + " mins");
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

        if (isPrepayment) {
            Lprepayment.setVisibility(View.VISIBLE);
            txtpreVal.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(MinPrePaymentAmount)));
        } else {
            Lprepayment.setVisibility(View.GONE);
        }
        if (desc != null && desc.length() > 0 && !desc.equalsIgnoreCase("")) {
            tv_descVal.setVisibility(View.VISIBLE);
//            Typeface tyfacedesc = Typeface.createFromAsset(getAssets(),
//                    "fonts/Montserrat_Bold.otf");
//            tv_descVal.setTypeface(tyfacedesc);
            tv_descVal.setText(desc);
        } else {
            tv_descVal.setVisibility(View.GONE);
        }

        if (from != null) {
            if (from.equalsIgnoreCase("dnt")) {
                if (dGallery != null) {
                    if (dGallery.size() > 0) {
                        i_servicegallery.setVisibility(View.VISIBLE);
                        try {
                            Glide.with(this).load(dGallery.get(0).getUrl()).fitCenter().placeholder(R.drawable.icon_noimage).error(R.drawable.icon_noimage).into(i_servicegallery);

//                            PicassoTrustAll.getInstance(this).setLoggingEnabled(true);
//                            PicassoTrustAll.getInstance(this).load(dGallery.get(0).getUrl()).fit().placeholder(R.drawable.icon_noimage).into(i_servicegallery);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (from.equalsIgnoreCase("appt")) {
                if (aGallery != null) {
                    if (aGallery.size() > 0) {
                        i_servicegallery.setVisibility(View.VISIBLE);
                        try {
                            Glide.with(this).load(aGallery.get(0).getUrl()).fitCenter().placeholder(R.drawable.icon_noimage).into(i_servicegallery);

//                            PicassoTrustAll.getInstance(this).setLoggingEnabled(true);
//                            PicassoTrustAll.getInstance(this).load(aGallery.get(0).getUrl()).fit().placeholder(R.drawable.icon_noimage).into(i_servicegallery);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (mGallery != null) {
                    if (mGallery.size() > 0) {
                        Config.logV("SERVICE GALLERY" + mGallery.get(0).getUrl());
                        i_servicegallery.setVisibility(View.VISIBLE);
                        try {
                            Glide.with(this).load(mGallery.get(0).getUrl()).placeholder(R.drawable.icon_noimage).into(i_servicegallery);

//                            PicassoTrustAll.getInstance(this).setLoggingEnabled(true);
//                            PicassoTrustAll.getInstance(this).load(mGallery.get(0).getUrl()).fit().placeholder(R.drawable.icon_noimage).into(i_servicegallery);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            i_servicegallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (from != null) {

                        if (from.equalsIgnoreCase("dnt")) {
                            ArrayList<String> mGalleryList = new ArrayList<>();
                            for (int i = 0; i < dGallery.size(); i++) {
                                mGalleryList.add(dGallery.get(i).getUrl());
                            }


                            boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                            if (mValue) {

                                Intent intent = new Intent(v.getContext(), SwipeGalleryImage.class);
                                startActivity(intent);
                            }
                        } else if (from.equalsIgnoreCase("appt")) {
                            ArrayList<String> mGalleryList = new ArrayList<>();
                            for (int i = 0; i < aGallery.size(); i++) {
                                mGalleryList.add(aGallery.get(i).getUrl());
                            }


                            boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                            if (mValue) {

                                Intent intent = new Intent(v.getContext(), SwipeGalleryImage.class);
                                startActivity(intent);
                            }
                        } else {
                            ArrayList<String> mGalleryList = new ArrayList<>();
                            if (mGallery != null) {
                                for (int i = 0; i < mGallery.size(); i++) {
                                    mGalleryList.add(mGallery.get(i).getUrl());
                                }


                                boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                                if (mValue) {

                                    Intent intent = new Intent(v.getContext(), SwipeGalleryImage.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                }
            });
        } else {
            i_servicegallery.setVisibility(View.VISIBLE);
        }
    }
}
