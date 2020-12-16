package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.response.Catalog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsActivity extends AppCompatActivity {

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    private Catalog catalogInfo;
    private int catalogId;
    private String businessName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        ButterKnife.bind(ItemsActivity.this);

        Intent i = getIntent();
        businessName = i.getStringExtra("businessName");
        catalogInfo = (Catalog) i.getSerializableExtra("catalogInfo");

        // set businessName
        if (businessName != null) {
            tvSpName.setText(businessName);
        }

        if (catalogInfo != null) {

            catalogId = catalogInfo.getCatLogId();

        }

    }


}