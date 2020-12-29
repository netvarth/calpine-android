package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.jaldeeinc.jaldee.Interface.IDialogInterface;
import com.jaldeeinc.jaldee.Interface.IImageInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.ItemImagesAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.ElegantNumberButton;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.SelectedItemsDialog;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.Item;
import com.jaldeeinc.jaldee.response.ItemImages;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemDetailAcitvity extends AppCompatActivity implements IImageInterface, IDialogInterface {

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.iv_displayImage)
    ImageView ivDisplayImage;

    @BindView(R.id.rv_images)
    RecyclerView rvImages;

    @BindView(R.id.tv_itemName)
    CustomTextViewBold tvItemName;

    @BindView(R.id.tv_price)
    CustomTextViewItalicSemiBold tvPrice;

    @BindView(R.id.tv_discountedPrice)
    CustomTextViewItalicSemiBold tvDiscountedPrice;

    @BindView(R.id.fl_add)
    FrameLayout flAdd;

    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;

    @BindView(R.id.tv_itemDescription)
    CustomTextViewMedium tvItemDescription;

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

    @BindView(R.id.tv_totalDiscount)
    CustomTextViewSemiBold tvTotalDiscount;

    private int accountId;
    private CardView cvPlus;
    private Context mContext;
    private CatalogItem itemDetails;
    private DatabaseHandler db;
    private LinearLayoutManager linearLayoutManager;
    private ItemImagesAdapter itemImagesAdapter;
    private IImageInterface iImageInterface;
    private ArrayList<ItemImages> imagesList = new ArrayList<>();
    Vibrator vibe;
    ProgressBar progressBar;
    private IDialogInterface iDialogInterface;
    private SelectedItemsDialog selectedItemsDialog;
    private SearchViewDetail mBusinessDataList = new SearchViewDetail();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_acitvity);
        ButterKnife.bind(ItemDetailAcitvity.this);
        iImageInterface = ItemDetailAcitvity.this;
        iDialogInterface = ItemDetailAcitvity.this;
        mContext = ItemDetailAcitvity.this;
        db = new DatabaseHandler(mContext);
        vibe = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        Intent intent = getIntent();
        itemDetails = (CatalogItem) intent.getSerializableExtra("itemInfo");
        accountId = intent.getIntExtra("accountId", 0);
        mBusinessDataList = (SearchViewDetail) intent.getSerializableExtra("providerInfo");

        // to update UI
        checkCart();


        if (itemDetails != null) {

            if (itemDetails.getItems() != null) {

                // to set item Name
                tvItemName.setText(itemDetails.getItems().getDisplayName());

                tvItemDescription.setText(itemDetails.getItems().getItemDescription());

                // to get all the images into list
                if (itemDetails.getItems().getItemImagesList() != null && itemDetails.getItems().getItemImagesList().size() > 0) {
                    imagesList = itemDetails.getItems().getItemImagesList();


                    shimmer.setVisibility(View.VISIBLE);
                    PicassoTrustAll.getInstance(mContext).load(imagesList.get(0).getUrl()).into(ivDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                            shimmer.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                            shimmer.setVisibility(View.GONE);
                            ivDisplayImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                        }
                    });

                    rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    itemImagesAdapter = new ItemImagesAdapter(imagesList, this, false, iImageInterface);
                    rvImages.setAdapter(itemImagesAdapter);
                }

                if (itemDetails.getItems().isShowPromotionalPrice()) {

                    tvPrice.setVisibility(View.VISIBLE);
                    tvDiscountedPrice.setVisibility(View.VISIBLE);
                    tvPrice.setText("₹" + itemDetails.getItems().getPrice());
                    tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    String price = String.valueOf(itemDetails.getItems().getPromotionalPrice());
                    tvDiscountedPrice.setText("₹" + price);

                } else {
                    tvPrice.setVisibility(View.VISIBLE);
                    tvPrice.setText("₹" + itemDetails.getItems().getPrice());
                    tvDiscountedPrice.setVisibility(View.GONE);

                }


            }

        }


        flAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (db.getAccountId() == 0 || db.getAccountId() == accountId) {


                    flAdd.setVisibility(View.GONE);
                    numberButton.setVisibility(View.VISIBLE);
                    numberButton.setNumber("1");

                    CartItemModel item = new CartItemModel();
                    item.setItemId(itemDetails.getItems().getItemId());
                    item.setAccountId(accountId);
                    item.setCatalogId(itemDetails.getCatalogId());
                    item.setItemName(itemDetails.getItems().getDisplayName());
                    if (itemDetails.getItems().getItemImagesList() != null && itemDetails.getItems().getItemImagesList().size() > 0) {
                        for (int i = 0; i < itemDetails.getItems().getItemImagesList().size(); i++) {
                            if (itemDetails.getItems().getItemImagesList().get(i).isDisplayImage()) {
                                item.setImageUrl(itemDetails.getItems().getItemImagesList().get(i).getUrl());
                            }
                        }
                    }
                    item.setItemPrice(itemDetails.getItems().getPrice());
                    item.setMaxQuantity(itemDetails.getMaxQuantity());
                    item.setQuantity(1);
                    item.setPromotionalType(itemDetails.getItems().getPromotionalPriceType());
                    item.setDiscount(itemDetails.getItems().getPromotionalPrice());
                    item.setDiscountedPrice(itemDetails.getItems().getDiscountedPrice());

                    db.insertItemToCart(item);

                    checkCartCount();
                } else {

                    showAlertDialog();

                }
            }
        });

        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                cvPlus = view.findViewById(R.id.cv_add);

                if (newValue < 1) {

                    db.addQuantity(itemDetails.getItems().getItemId(), 0);
                    numberButton.setVisibility(View.GONE);
                    flAdd.setVisibility(View.VISIBLE);
                    checkCartCount();

                } else if (newValue <= itemDetails.getMaxQuantity()) {

                    // fadein cardview here
                    cvPlus.setBackgroundResource(R.drawable.plus_background);
                    cvPlus.setClickable(true);
                    progressBar = view.findViewById(R.id.myProgress);
                    progressBar.setVisibility(View.VISIBLE);
                    vibe.vibrate(100);

                    if (newValue == itemDetails.getMaxQuantity()) {

                        // give fadeout color for plus
                        cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                        cvPlus.setClickable(false);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms

                            db.addQuantity(itemDetails.getItems().getItemId(), newValue);
                            checkCartCount();
                            progressBar.setVisibility(View.GONE);

                        }
                    }, 500);
                    // plus and minus icons are disabled based on minQuantity and new value

                } else {

                    // give fadeout color for plus
                    cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                    cvPlus.setClickable(false);
                    checkCartCount();

                }


            }
        });

        if (itemDetails.getMaxQuantity() == Integer.parseInt(numberButton.getNumber())) {
            cvPlus = numberButton.findViewById(R.id.cv_add);
            cvPlus.setBackgroundResource(R.drawable.disabled_plus);
            cvPlus.setClickable(false);
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
                selectedItemsDialog.setCancelable(false);
                selectedItemsDialog.show();
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                selectedItemsDialog.getWindow().setGravity(Gravity.BOTTOM);
                selectedItemsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        llViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ItemDetailAcitvity.this, CartActivity.class);
                intent.putExtra("accountId", accountId);
                intent.putExtra("providerInfo", mBusinessDataList);
                startActivity(intent);

            }
        });


    }

    private void showAlertDialog() {

        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.order_conflict);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        CardView cvYes = dialog.findViewById(R.id.cv_yes);
        CardView cvNo = dialog.findViewById(R.id.cv_no);

        cvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.DeleteCart();
                checkCartCount();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void checkCart() {

        // to get quantity from previous activity
        int itemQuantity = db.getItemQuantity(itemDetails.getItems().getItemId());
        itemDetails.getItems().setItemQuantity(itemQuantity);

        if (itemDetails.getItems().getItemQuantity() > 0) {

            flAdd.setVisibility(View.GONE);
            numberButton.setVisibility(View.VISIBLE);
            numberButton.setNumber(String.valueOf(itemQuantity));
        } else {

            numberButton.setVisibility(View.GONE);
            flAdd.setVisibility(View.VISIBLE);
        }


        if (accountId == db.getAccountId()) {
            cvItemsCart.setVisibility(View.VISIBLE);
            tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");
            tvSubTotal.setText("₹" + db.getCartPrice());

        } else {

            cvItemsCart.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkCart();
    }

    private void checkCartCount() {

        if (db.getCartCount() > 0) {

            cvItemsCart.setVisibility(View.VISIBLE);
            tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");
            if (db.getCartPrice() == db.getCartDiscountedPrice()) {

                tvSubTotal.setVisibility(View.GONE);
                tvTotalDiscount.setVisibility(View.VISIBLE);
                tvTotalDiscount.setText("₹" + db.getCartPrice());

            } else {

                tvSubTotal.setVisibility(View.VISIBLE);
                tvSubTotal.setText("₹" + db.getCartPrice());
                tvTotalDiscount.setVisibility(View.VISIBLE);
                tvTotalDiscount.setText("₹" + db.getCartDiscountedPrice());
            }
        } else {

            cvItemsCart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onImageClick(String url) {

        shimmer.setVisibility(View.VISIBLE);
        PicassoTrustAll.getInstance(mContext).load(url).into(ivDisplayImage, new Callback() {
            @Override
            public void onSuccess() {

                shimmer.setVisibility(View.GONE);
            }

            @Override
            public void onError() {

                shimmer.setVisibility(View.GONE);
                ivDisplayImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
            }
        });
    }

    @Override
    public void onContinueClick() {

        checkCart();

    }

    @Override
    public void onClearClick() {

        checkCart();

    }
}