package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.response.StoreDetails;

public class StoreDetailsDialog extends Dialog {
    StoreDetails storeDetails;
    Context context;
    CustomTextViewSemiBold tv_provider;
    CustomTextViewMedium tv_address, tv_phone, tv_alternate_phone, tv_email, tv_alternate_email, tv_whatsAppNo;
    ImageView iv_close;
    LinearLayout ll_address, ll_phone, ll_alternatePhone, ll_email, ll_alternateEmail, ll_whatsAppNo;

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
        tv_alternate_phone = findViewById(R.id.alternatePhoneNumber);
        tv_email = findViewById(R.id.emailId);
        tv_alternate_email = findViewById(R.id.alternateEmailId);
        tv_whatsAppNo = findViewById(R.id.whatsAppNo);
        iv_close = findViewById(R.id.iv_close);

        ll_address = findViewById(R.id.ll_address);
        ll_phone = findViewById(R.id.ll_phoneNumber);
        ll_alternatePhone = findViewById(R.id.ll_alternatePhoneNumber);
        ll_email = findViewById(R.id.ll_emailId);
        ll_alternateEmail = findViewById(R.id.ll_alternateEmailId);
        ll_whatsAppNo = findViewById(R.id.ll_whatsAppNo);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if (storeDetails != null) {
            if (storeDetails.getFirstName() != null && storeDetails.getLastName() != null) {

                tv_provider.setText(storeDetails.getFirstName() + " " + storeDetails.getLastName());
            }

            if (storeDetails.getAddress() != null && !storeDetails.getAddress().trim().equalsIgnoreCase("")) {

                ll_address.setVisibility(View.VISIBLE);
                tv_address.setText(storeDetails.getAddress());
            } else {

                ll_address.setVisibility(View.GONE);
            }

            if (storeDetails.getPhone() != null && !storeDetails.getPhone().isEmpty()) {

                if (storeDetails.getPrimCountryCode() != null) {

                    tv_phone.setText(storeDetails.getPrimCountryCode() + " " + storeDetails.getPhone());
                } else {

                    tv_phone.setText(storeDetails.getPhone());
                }
                ll_phone.setVisibility(View.VISIBLE);
            }

            if (storeDetails.getAlternatePhone() != null && !storeDetails.getAlternatePhone().isEmpty()) {

                if (storeDetails.getSecCountryCode() != null) {

                    tv_alternate_phone.setText(storeDetails.getSecCountryCode() + " " + storeDetails.getAlternatePhone());
                } else {

                    tv_alternate_phone.setText(storeDetails.getAlternatePhone());
                }
                ll_alternatePhone.setVisibility(View.VISIBLE);
            }

            if (storeDetails.getEmail() != null && !storeDetails.getEmail().isEmpty()) {

                tv_email.setText(storeDetails.getEmail());
                ll_email.setVisibility(View.VISIBLE);
            }

            if (storeDetails.getAlternateEmail() != null && !storeDetails.getAlternateEmail().isEmpty()) {

                tv_alternate_email.setText(storeDetails.getAlternateEmail());
                ll_alternateEmail.setVisibility(View.VISIBLE);
            }

            if (storeDetails.getWhatsappNo() != null && !storeDetails.getWhatsappNo().isEmpty()) {
                if (storeDetails.getWhatsAppCountryCode() != null) {

                    tv_whatsAppNo.setText(storeDetails.getWhatsAppCountryCode() + " " + storeDetails.getWhatsappNo());
                } else {

                    tv_whatsAppNo.setText(storeDetails.getWhatsappNo());
                }
                ll_whatsAppNo.setVisibility(View.VISIBLE);
            }
        }
    }
}
