package com.jaldeeinc.jaldee.adapter;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;

public class Parent extends RecyclerView.ViewHolder {

    CustomTextViewBold departmentName;

    public Parent(View itemView) {
        super(itemView);
        departmentName = (CustomTextViewBold) itemView.findViewById(R.id.tv_depName);
    }
}

