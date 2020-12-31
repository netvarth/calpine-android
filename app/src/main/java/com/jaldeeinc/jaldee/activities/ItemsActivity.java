package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.Interface.ICartInterface;
import com.jaldeeinc.jaldee.Interface.IDialogInterface;
import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.SelectedItemsDialog;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsActivity extends AppCompatActivity implements IItemInterface, IDialogInterface {

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
    BorderImageView ivCatalogImage;

    @BindView(R.id.shimmer)
    SkeletonLoadingView shimmer;

    @BindView(R.id.cv_arrow)
    CardView cvArrow;

    @BindView(R.id.cv_itemsCart)
    CardView cvItemsCart;

    @BindView(R.id.ll_viewcart)
    LinearLayout llViewCart;

    @BindView(R.id.tv_itemsCount)
    CustomTextViewSemiBold tvItemsCount;

    @BindView(R.id.tv_subTotal)
    CustomTextViewMedium tvSubTotal;

    @BindView(R.id.tv_discountedPrice)
    CustomTextViewSemiBold tvDisCountedPrice;

    private Catalog catalogInfo;
    private Context mContext;
    private int catalogId, accountId;
    private SearchViewDetail mBusinessDataList = new SearchViewDetail();
    private ItemsAdapter itemsAdapter;
    private IItemInterface iItemInterface;
    private IDialogInterface iDialogInterface;
    private GridLayoutManager gridLayoutManager;
    private DatabaseHandler db;
    private SelectedItemsDialog selectedItemsDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        ButterKnife.bind(ItemsActivity.this);
        mContext = ItemsActivity.this;
        iItemInterface = this;
        iDialogInterface = this;
        db = new DatabaseHandler(ItemsActivity.this);

        Intent i = getIntent();
        mBusinessDataList = (SearchViewDetail) i.getSerializableExtra("providerInfo");
        catalogInfo = (Catalog) i.getSerializableExtra("catalogInfo");
        accountId = i.getIntExtra("accountId", 0);

        gridLayoutManager = new GridLayoutManager(ItemsActivity.this, 2);
        rvItems.setLayoutManager(gridLayoutManager);
        itemsAdapter = new ItemsAdapter(catalogInfo.getCatalogItemsList(), this, true, iItemInterface, accountId);
        rvItems.setAdapter(itemsAdapter);

        if (accountId == db.getAccountId()) {
            cvItemsCart.setVisibility(View.VISIBLE);
            tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");
            tvSubTotal.setText("₹" + db.getCartPrice());

        } else {

            cvItemsCart.setVisibility(View.GONE);
        }

        // set businessName
        if (mBusinessDataList != null && mBusinessDataList.getBusinessName() != null) {
            tvSpName.setText(mBusinessDataList.getBusinessName());
        }

        if (catalogInfo != null) {
            try {
                if (catalogInfo.getCatalogImagesList() != null && catalogInfo.getCatalogImagesList().size() > 0) {
                    shimmer.setVisibility(View.VISIBLE);
                    PicassoTrustAll.getInstance(ItemsActivity.this).load(catalogInfo.getCatalogImagesList().get(0).getUrl()).into(ivCatalogImage, new Callback() {
                        @Override
                        public void onSuccess() {

                            shimmer.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                            shimmer.setVisibility(View.GONE);
                            ivCatalogImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                        }
                    });
                } else {

                    ivCatalogImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                }

                if (catalogInfo.getCatLogName() != null) {
                    tvCataLogName.setText(catalogInfo.getCatLogName());
                }

                if (catalogInfo.getCatalogDescription() != null) {

                    tvDescription.setText(catalogInfo.getCatalogDescription());
                }

                if (catalogInfo.getHomeDelivery() != null && catalogInfo.getHomeDelivery().isHomeDelivery()) {
                    llHomeDelivery.setVisibility(View.VISIBLE);
                } else {
                    llHomeDelivery.setVisibility(View.GONE);
                }

                if (catalogInfo.getPickUp() != null && catalogInfo.getPickUp().isOrderPickUp()) {
                    llStorePickup.setVisibility(View.VISIBLE);
                } else {
                    llStorePickup.setVisibility(View.GONE);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        cvArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedItemsDialog = new SelectedItemsDialog(mContext, iDialogInterface);
                selectedItemsDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
                selectedItemsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                selectedItemsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                selectedItemsDialog.show();
                selectedItemsDialog.setCancelable(false);
                DisplayMetrics metrics = ItemsActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                selectedItemsDialog.getWindow().setGravity(Gravity.BOTTOM);
                selectedItemsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        llViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ItemsActivity.this, CartActivity.class);
                intent.putExtra("accountId", accountId);
                intent.putExtra("providerInfo", mBusinessDataList);
                startActivity(intent);

            }
        });

    }

    public void refreshData() {

        ArrayList<CatalogItem> catalogItemsList = new ArrayList<>();
        catalogItemsList = updateCatalogItemsDiscount(catalogInfo.getCatalogItemsList());
        catalogItemsList = updateCatalogItemsQuantity(catalogItemsList);
        gridLayoutManager = new GridLayoutManager(ItemsActivity.this, 2);
        rvItems.setLayoutManager(gridLayoutManager);
        itemsAdapter = new ItemsAdapter(catalogItemsList, this, false, iItemInterface, accountId);
        rvItems.setAdapter(itemsAdapter);
        updateCartUI();
    }


    @Override
    protected void onResume() {
        super.onResume();

        refreshData();
    }


    private ArrayList<CatalogItem> updateCatalogItemsDiscount(ArrayList<CatalogItem> catalogItemsList) {

        catalogItemsList = catalogItemsList == null ? new ArrayList<>() : catalogItemsList;

        for (CatalogItem catalogItem : catalogItemsList) {

            if (catalogItem.getItems().isShowPromotionalPrice()) {

                catalogItem.getItems().setDiscountedPrice(catalogItem.getItems().getPromotionalPrice());
            } else {

                catalogItem.getItems().setDiscountedPrice(catalogItem.getItems().getPrice());

            }

            if (catalogItem.getItems().getItemImagesList() != null && catalogItem.getItems().getItemImagesList().size() > 0) {
                for (int i = 0; i < catalogItem.getItems().getItemImagesList().size(); i++) {
                    if (catalogItem.getItems().getItemImagesList().get(i).isDisplayImage()) {
                        catalogItem.getItems().setDisplayImage(catalogItem.getItems().getItemImagesList().get(i).getUrl());
                    }
                }
            }


        }

        return catalogItemsList;
    }

    private ArrayList<CatalogItem> updateCatalogItemsQuantity(ArrayList<CatalogItem> catalogItemsList) {

        ArrayList<CartItemModel> cartItemsList = new ArrayList<>();
        catalogItemsList = catalogItemsList == null ? new ArrayList<>() : catalogItemsList;
        cartItemsList = db.getCartItems();
        db.markItemsAsExpired();

        for (CartItemModel cartItem : cartItemsList) {

            for (CatalogItem catalogItem : catalogItemsList) {

                if (cartItem.getItemId() == catalogItem.getItems().getItemId()) {

                    catalogItem.getItems().setItemQuantity(cartItem.getQuantity());
                    CartItemModel item = new CartItemModel(catalogItem.getItems().getItemId(), catalogItem.getItems().getPrice(), catalogItem.getMaxQuantity(), catalogItem.getItems().getDiscountedPrice());
                    item.setQuantity(cartItem.getQuantity());
                    if (catalogItem.getItems().isShowPromotionalPrice()) {
                        item.setIsPromotional(1);
                    }
                    db.updateCartItem(item);
                }
            }
        }

        if (cartItemsList.size() == 0) {

            for (CatalogItem catalogItem : catalogItemsList) {

                catalogItem.getItems().setItemQuantity(0);

            }

        }

        return catalogItemsList;
    }


    @Override
    public void onItemClick(CatalogItem catalogItem) {

        Intent intent = new Intent(ItemsActivity.this, ItemDetailAcitvity.class);
        intent.putExtra("itemInfo", catalogItem);
        intent.putExtra("accountId", accountId);
        intent.putExtra("providerInfo", mBusinessDataList);
        startActivity(intent);

    }

    @Override
    public void checkItemQuantity() {

        updateCartUI();

    }

    @Override
    public void onContinueClick() {

        refreshData();
        updateCartUI();
    }

    @Override
    public void onClearClick() {

        removeQuantity();
    }


    private void updateCartUI() {

        if (db.getCartCount() > 0) {

            cvItemsCart.setVisibility(View.VISIBLE);

            tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");

            if (db.getCartPrice() == db.getCartDiscountedPrice()) {

                tvSubTotal.setVisibility(View.GONE);
                tvDisCountedPrice.setVisibility(View.VISIBLE);
                tvDisCountedPrice.setText("₹" + db.getCartPrice());

            } else {

                tvSubTotal.setVisibility(View.VISIBLE);
                tvSubTotal.setText("₹" + db.getCartPrice());
                tvSubTotal.setPaintFlags(tvSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvDisCountedPrice.setVisibility(View.VISIBLE);
                tvDisCountedPrice.setText("₹" + db.getCartDiscountedPrice());
            }
        } else {

            cvItemsCart.setVisibility(View.GONE);
        }
    }


    private void removeQuantity() {

        for (int i = 0; i < catalogInfo.getCatalogItemsList().size(); i++) {

            catalogInfo.getCatalogItemsList().get(i).getItems().setItemQuantity(0);
        }
        refreshData();
    }


}