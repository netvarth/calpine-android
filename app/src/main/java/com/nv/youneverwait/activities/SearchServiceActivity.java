package com.nv.youneverwait.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.SearchService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sharmila on 31/7/18.
 */

public class SearchServiceActivity extends AppCompatActivity {
    TextView tv_price, tv_duration, tv_service, tv_desc;

    String name, duration, price, desc="";
    Toolbar toolbar;
    ArrayList<SearchService> mGallery;
    ImageView i_servicegallery;
    String title;
    TextView tv_toolbartitle,tv_descVal;
    ImageView i_backpress;
    boolean isTaxable,isPrepayment;
    LinearLayout Lprepayment;
    TextView txtpreVal;
String MinPrePaymentAmount;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service);
        tv_duration = (TextView) findViewById(R.id.txtduration);
        tv_price = (TextView) findViewById(R.id.txtprice);
        tv_service = (TextView) findViewById(R.id.txtservice);
        tv_desc = (TextView) findViewById(R.id.txtdesc);
        tv_descVal=(TextView) findViewById(R.id.txtdescVal);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        i_servicegallery = (ImageView) findViewById(R.id.img_service);
        i_backpress= (ImageView) findViewById(R.id.backpress);
        Lprepayment=(LinearLayout) findViewById(R.id.Lprepayment);
        txtpreVal=(TextView) findViewById(R.id.txtpreVal);


        i_backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
      //  i_servicethumb=(ImageView) findViewById(R.id.img_servicesmall);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            duration = extras.getString("duration");
            price = extras.getString("price");
            desc = extras.getString("desc");
            title=extras.getString("title");
            isTaxable=extras.getBoolean("taxable");
            isPrepayment=extras.getBoolean("isPrePayment");

            MinPrePaymentAmount=extras.getString("MinPrePaymentAmount");
            mGallery = (ArrayList<SearchService>) getIntent().getSerializableExtra("servicegallery");
        }

        tv_toolbartitle=(TextView)findViewById(R.id.txt_toolbartitle);
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
        } else {
            tv_service.setVisibility(View.GONE);
        }

        if (price != null) {
            tv_price.setVisibility(View.VISIBLE);
            if(isTaxable) {

                tv_price.setText("₹"+price+ " (Tax Applicable)");
            }else {
                tv_price.setText("₹"+price);
            }
        } else {
            tv_price.setVisibility(View.GONE);
        }
        if (duration != null) {
            tv_duration.setVisibility(View.VISIBLE);
            tv_duration.setText(duration +" Mins");
        } else {
            tv_duration.setVisibility(View.GONE);

        }
        if(isPrepayment){
            Lprepayment.setVisibility(View.VISIBLE);
            txtpreVal.setText("₹"+MinPrePaymentAmount);
        }else{
            Lprepayment.setVisibility(View.GONE);
        }
        if (desc != null&&desc.length()>0&&!desc.equalsIgnoreCase("")) {
            tv_desc.setVisibility(View.VISIBLE);
            tv_descVal.setVisibility(View.VISIBLE);
            Typeface tyfacedesc = Typeface.createFromAsset(getAssets(),
                    "fonts/Montserrat_Bold.otf");
            tv_descVal.setTypeface(tyfacedesc);
            tv_descVal.setText(desc);
        } else {
            tv_desc.setVisibility(View.GONE);
            tv_descVal.setVisibility(View.GONE);
        }

        if(mGallery!=null){

            if(mGallery.size()>0) {

                Config.logV("SERVICE GALLERY"+mGallery.get(0).getUrl());
                i_servicegallery.setVisibility(View.VISIBLE);
                try {
                    Picasso.with(this).setLoggingEnabled(true);
                    Picasso.with(this). load(mGallery.get(0).getUrl()).fit().placeholder(R.drawable.icon_noimage).into(i_servicegallery);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /*if(mGallery.size()>1) {
                if (mGallery.get(1).getUrl() != null) {

                    Picasso.with(this).load(mGallery.get(1).getUrl()).fit().into(i_servicethumb);
                }
            }*/


            i_servicegallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> mGalleryList = new ArrayList<>();
                    for (int i = 0; i < mGallery.size(); i++) {
                        mGalleryList.add(mGallery.get(i).getUrl());
                    }


                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryList, v.getContext());
                    if (mValue) {

                        Intent intent = new Intent(v.getContext(), SwipeGalleryImage.class);
                        startActivity(intent);
                    }
                }
            });
        }else{
            i_servicegallery.setVisibility(View.GONE);
           // i_servicethumb.setVisibility(View.GONE);
        }
    }
}
