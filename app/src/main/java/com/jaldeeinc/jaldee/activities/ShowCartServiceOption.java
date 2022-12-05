package com.jaldeeinc.jaldee.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.ShowCartServiceOptionItemsAdapter;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.response.ActiveOrders;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowCartServiceOption extends AppCompatActivity {

    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    @BindView(R.id.tv_subTotal)
    CustomTextViewMedium tvSubTotal;

    @BindView(R.id.tv_discountedPrice)
    CustomTextViewSemiBold tvDiscountedPrice;

    @BindView(R.id.cv_checkOut)
    CardView cvCheckOut;

    @BindView(R.id.ll_noItems)
    LinearLayout llNoItems;

    @BindView(R.id.cv_itemsCart)
    CardView cvItemsCart;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.tv_spName)
    CustomTextViewBold tv_spName;

    private Context mContext;
    private LinearLayoutManager linearLayoutManager;
    ActiveOrders orderInfo = new ActiveOrders();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(ShowCartServiceOption.this);
        mContext = ShowCartServiceOption.this;
        cvItemsCart.setVisibility(View.GONE);
        tv_spName.setVisibility(View.GONE);
        Intent intent = getIntent();
        String ext = intent.getStringExtra("orderInfo");
        Gson gson = new Gson();
        orderInfo = gson.fromJson(ext, ActiveOrders.class);
        updateUI();
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    ShowCartServiceOptionItemsAdapter showCartServiceOptionItemsAdapter;
    private void updateUI() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        rvItems.setLayoutManager(linearLayoutManager);
        showCartServiceOptionItemsAdapter = new ShowCartServiceOptionItemsAdapter(mContext, orderInfo);
        rvItems.setAdapter(showCartServiceOptionItemsAdapter);

    }
}
