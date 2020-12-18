package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.adapter.UserServicesAdapter;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.Item;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsActivity extends AppCompatActivity implements IItemInterface {

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    @BindView(R.id.tv_catalogName)
    CustomTextViewSemiBold tvCataLogName;

    @BindView(R.id.tv_description)
    CustomTextViewMedium tvDescription;

    @BindView(R.id.ll_homeDelivery)
    LinearLayout llHomeDelivery;

    @BindView(R.id.ll_storePickup)
    LinearLayout llStorePickup;

    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    @BindView(R.id.ll_noItems)
    LinearLayout llNoItems;

    @BindView(R.id.iv_catalogImage)
    ImageView ivCatalogImage;

    private Catalog catalogInfo;
    private int catalogId;
    private String businessName;
    private ItemsAdapter itemsAdapter;
    private IItemInterface iItemInterface;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<CatalogItem> itemsList = new ArrayList<>();

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
            if (catalogInfo.getCatalogImagesList() != null && catalogInfo.getCatalogImagesList().size() > 0) {
                PicassoTrustAll.getInstance(ItemsActivity.this).load(catalogInfo.getCatalogImagesList().get(0).getUrl()).into(ivCatalogImage);
            }

            if (catalogInfo.getCatLogName() != null){
                tvCataLogName.setText(catalogInfo.getCatLogName());
            }

            if (catalogInfo.getCatalogDescription() != null){

                tvDescription.setText(catalogInfo.getCatalogDescription());
            }

            if (catalogInfo.getHomeDelivery().isHomeDelivery()){
                llHomeDelivery.setVisibility(View.VISIBLE);
            }

        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        gridLayoutManager = new GridLayoutManager(ItemsActivity.this, 2);
        rvItems.setLayoutManager(gridLayoutManager);
        itemsAdapter = new ItemsAdapter(catalogInfo.getCatalogItemsList(), this, false, iItemInterface);
        rvItems.setAdapter(itemsAdapter);


    }


    @Override
    public void onItemClick(CatalogItem catalogItem) {



    }
}