package com.jaldeeinc.jaldee.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;

public class Child extends RecyclerView.ViewHolder {

    ImageView ivImage, ivTeleService,ivMore;
    CardView cvImage,cvCard;
    CustomTextViewBold tvName, tvEstWaitTime, tvDontnAmount, tvNextAvailableTime;
    LinearLayout llTime, llEstwaitTime, llDonationRange;
    CustomTextViewMedium  tvPeopleAhead,tvTimeHint;
    CustomTextViewSemiBold tvServiceType;

    Child(View itemView) {
        super(itemView);

        ivImage = itemView.findViewById(R.id.iv_image);
        ivTeleService = itemView.findViewById(R.id.iv_teleService);
        cvImage = itemView.findViewById(R.id.cv_image);
        tvName = itemView.findViewById(R.id.tv_serviceName);
        tvEstWaitTime = itemView.findViewById(R.id.tv_estWaitTime);
        tvDontnAmount = itemView.findViewById(R.id.tv_dontnAmount);
        ivMore = itemView.findViewById(R.id.iv_info);
        llTime = itemView.findViewById(R.id.ll_time);
        llEstwaitTime = itemView.findViewById(R.id.ll_estWaitTime);
        llDonationRange = itemView.findViewById(R.id.ll_donationRange);
        tvNextAvailableTime = itemView.findViewById(R.id.tv_nextAvailableTime);
        tvPeopleAhead = itemView.findViewById(R.id.tv_peopleAhead);
        tvServiceType = itemView.findViewById(R.id.tv_serviceType);
        tvTimeHint = itemView.findViewById(R.id.tv_timeHint);
        cvCard = itemView.findViewById(R.id.cv_card);

    }
}

