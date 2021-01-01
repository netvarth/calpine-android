package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.StoreDetails;

public class StoreDetailsDialog extends Dialog {
    StoreDetails storeDetails;
    Context context;
    CustomTextViewSemiBold tv_provider;
    CustomTextViewMedium tv_address, tv_phone, tv_email;
    ImageView iv_close;
    public StoreDetailsDialog(@NonNull Context context, StoreDetails storeDetails) {
        super(context);
        this.context = context;
        this.storeDetails = storeDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storedetails);

        tv_provider = findViewById(R.id.ownerName);
        tv_address = findViewById(R.id.address);
        tv_phone = findViewById(R.id.phoneNumber);
        tv_email = findViewById(R.id.emailId);
        iv_close = findViewById(R.id.iv_close);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        if(storeDetails!=null){
            if(storeDetails.getFirstName()!=null && storeDetails.getLastName()!=null){
                tv_provider.setText(storeDetails.getFirstName() + " " + storeDetails.getLastName());
            }

            if(storeDetails.getAddress()!=null && !storeDetails.getAddress().trim().equalsIgnoreCase("")){
                tv_address.setText(storeDetails.getAddress());
                tv_address.setVisibility(View.VISIBLE);
            }
            else{
                tv_address.setVisibility(View.GONE);
            }

            if(storeDetails.getPhone()!=null){
                if(storeDetails.getPrimCountryCode()!=null) {
                    tv_phone.setText(storeDetails.getPrimCountryCode() + " " + storeDetails.getPhone());
                }
                else{
                    tv_phone.setText(storeDetails.getPhone());
                }
            }

            if(storeDetails.getEmail()!=null){
                tv_email.setText(storeDetails.getEmail());
            }
        }
    }
}
