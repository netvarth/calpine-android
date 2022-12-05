package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchViewDetail;

import java.util.ArrayList;

public class ServiceInfoDialog extends Dialog {

    private SearchService searchService;
    private SearchViewDetail providerInfo;
    private CustomTextViewMedium tvCostHint,tvPrepaymentHint,tvDurationHint;
    Context context;
    String multiples, maxDonationAmount, minDonationAmount;
    Toolbar toolbar;
    ImageView i_servicegallery, ivClose, i_backpress;
    CustomTextViewBold tv_toolbartitle, tv_price, tv_duration,txtpreVal;
    CustomTextViewSemiBold tv_service, tv_maxvalue, tv_minvalue, tv_multiples;
    CustomTextViewMedium tv_descVal, tvisTax, txt_payment_desc, more_Imag_text;
    LinearLayout Lprepayment, LserviceLayout, LminAmountlayout, LmaxAmountlayout, Lmultilayout, Ldurationlayout;

    public ServiceInfoDialog(@NonNull Context context, SearchService searchService, SearchViewDetail providerInfo) {
        super(context);
        this.context = context;
        this.searchService = searchService;
        this.providerInfo = providerInfo;
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

        try {
            if (searchService.getName() != null) {
                tv_toolbartitle.setVisibility(View.VISIBLE);
                String name = searchService.getName();
                tv_toolbartitle.setText(name);
                try {
                    if (searchService.getServiceType().equalsIgnoreCase("virtualservice")) {
                        if (searchService.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                            tv_toolbartitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.zoomicon_sized, 0, 0, 0);
                            tv_toolbartitle.setCompoundDrawablePadding(15);
                        } else if (searchService.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                            tv_toolbartitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.googlemeet_sized, 0, 0, 0);
                            tv_toolbartitle.setCompoundDrawablePadding(15);
                        } else if (searchService.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                            if (searchService.getVirtualCallingModes().get(0).getVirtualServiceType() != null && searchService.getVirtualCallingModes().get(0).getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                tv_toolbartitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsapp_videoicon, 0, 0, 0);
                            } else {
                                tv_toolbartitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                            }
                            tv_toolbartitle.setCompoundDrawablePadding(15);
                        } else if (searchService.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                            tv_toolbartitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneicon_sized, 0, 0, 0);
                            tv_toolbartitle.setCompoundDrawablePadding(15);
                        } else if (searchService.getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                            tv_toolbartitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_jaldeevideo, 0, 0, 0);
                            tv_toolbartitle.setCompoundDrawablePadding(15);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                tv_toolbartitle.setVisibility(View.GONE);
            }
            if(searchService.getServicegallery() != null && searchService.getServicegallery().size() > 1){
                int gallerySize = searchService.getServicegallery().size() - 1;
                more_Imag_text.setText("+" + gallerySize);
                more_Imag_text.setVisibility(View.VISIBLE);
                i_servicegallery.animate().alpha(0.4f);
            } else {
                more_Imag_text.setVisibility(View.GONE);
            }
            if (providerInfo != null &&  providerInfo.getServiceSector() != null){
                if (providerInfo.getServiceSector().getDisplayName().equalsIgnoreCase("Healthcare")){
                    tvCostHint.setText("Consultation Fee");
                    tvDurationHint.setText("Consultation Duration");
                }  else {
                    tvCostHint.setText("Service Fee");
                    tvDurationHint.setText("Service Duration");
                }
            }
            if (searchService.getTotalAmount() != null) {
                if (!searchService.getTotalAmount().equals("0.0")) {
                    tv_price.setVisibility(View.VISIBLE);
                    LserviceLayout.setVisibility(View.VISIBLE);
                    if (searchService.isTaxable()) {
                        tv_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(searchService.getTotalAmount())));
                        tvisTax.setVisibility(View.VISIBLE);
                    } else {
                        tv_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(searchService.getTotalAmount())));
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

            if (searchService.isServiceDurationEnabled() && searchService.getServiceDuration() != null) {
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
                if (searchService.getMinPrePaymentAmount() != null) {
                    Lprepayment.setVisibility(View.VISIBLE);
                    txtpreVal.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(searchService.getMinPrePaymentAmount())));
                }
            } else {
                Lprepayment.setVisibility(View.GONE);
            }
            if (searchService.getDescription() != null && searchService.getDescription().length() > 0 && !searchService.getDescription().equalsIgnoreCase("")) {
                tv_descVal.setVisibility(View.VISIBLE);
                tv_descVal.setText(searchService.getDescription());
            } else {
                tv_descVal.setVisibility(View.GONE);
            }
            if (searchService.getPaymentDescription() != null && searchService.getPaymentDescription().length() > 0 && !searchService.getPaymentDescription().equalsIgnoreCase("")) {
                txt_payment_desc.setVisibility(View.VISIBLE);
                txt_payment_desc.setText(searchService.getPaymentDescription());
            } else {
                txt_payment_desc.setVisibility(View.GONE);
            }
            if (txtpreVal.getText().toString().equalsIgnoreCase(tv_price.getText().toString())){
                tvPrepaymentHint.setText("Full fees should be paid in advance");
                txtpreVal.setVisibility(View.GONE);
            } else {
                tvPrepaymentHint.setText("Advance payment");
                txtpreVal.setVisibility(View.VISIBLE);
            }
            if (searchService.getServicegallery() != null) {
                if (searchService.getServicegallery().size() > 0) {
                    i_servicegallery.setVisibility(View.VISIBLE);
                    try {
                        Glide.with(context).load(searchService.getServicegallery().get(0).getUrl()).fitCenter().placeholder(R.drawable.icon_noimage).into(i_servicegallery);

//                        PicassoTrustAll.getInstance(context).setLoggingEnabled(true);
//                        PicassoTrustAll.getInstance(context).load(searchService.getServicegallery().get(0).getUrl()).fit().placeholder(R.drawable.icon_noimage).into(i_servicegallery);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    i_servicegallery.setVisibility(View.GONE);
                }
            } else {
                i_servicegallery.setVisibility(View.GONE);
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
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void initializations() {
        tv_toolbartitle = findViewById(R.id.txt_toolbartitle);
        tv_duration = findViewById(R.id.txtduration);
        tv_price = findViewById(R.id.txtprice);
        tv_service = findViewById(R.id.txtservice);
        tvisTax = findViewById(R.id.tv_isTax);
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
        tvCostHint = findViewById(R.id.tv_costHint);
        tvPrepaymentHint = findViewById(R.id.tv_prepaymentHint);
        tvDurationHint = findViewById(R.id.tv_durationHint);
        txt_payment_desc = findViewById(R.id.txt_payment_desc);
        more_Imag_text =  findViewById(R.id.more_Imag_text);
    }
}
