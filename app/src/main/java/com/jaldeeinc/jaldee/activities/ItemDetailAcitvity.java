package com.jaldeeinc.jaldee.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.jaldeeinc.jaldee.CustomSwipe.DiscreteScrollView;
import com.jaldeeinc.jaldee.CustomSwipe.transform.ScaleTransformer;
import com.jaldeeinc.jaldee.Interface.IDialogInterface;
import com.jaldeeinc.jaldee.Interface.IImageInterface;
import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailPageItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ItemImagesAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.custom.AutofitTextView;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.ElegantNumberButton;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.SelectedItemsDialog;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.Item;
import com.jaldeeinc.jaldee.response.ItemImages;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
import com.squareup.picasso.Callback;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemDetailAcitvity extends AppCompatActivity implements IImageInterface, IDialogInterface, IItemInterface {

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.iv_displayImage)
    ImageView ivDisplayImage;

    @BindView(R.id.rv_images)
    RecyclerView rvImages;

    @BindView(R.id.tv_itemName)
    CustomTextViewBold tvItemName;

    @BindView(R.id.tv_price)
    CustomTextViewLight tvPrice;

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

    @BindView(R.id.rv_items)
    DiscreteScrollView rvItems;

    @BindView(R.id.tv_subTotal)
    AutofitTextView tvSubTotal;

    @BindView(R.id.tv_totalDiscount)
    AutofitTextView tvTotalDiscount;

    @BindView(R.id.tv_stock)
    CustomTextViewSemiBold tvStock;

    @BindView(R.id.ll_actions)
    LinearLayout llActions;

    @BindView(R.id.tv_similarItems)
    CustomTextViewSemiBold tvSimilarItems;

    private int accountId, uniqueId;
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
    private IItemInterface iItemInterface;
    private DetailPageItemsAdapter detailPageItemsAdapter;
    ArrayList<CatalogItem> remainingItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_acitvity);
        ButterKnife.bind(ItemDetailAcitvity.this);
        iImageInterface = ItemDetailAcitvity.this;
        iDialogInterface = ItemDetailAcitvity.this;
        iItemInterface = ItemDetailAcitvity.this;
        mContext = ItemDetailAcitvity.this;
        db = new DatabaseHandler(mContext);
        vibe = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        Intent intent = getIntent();
        itemDetails = (CatalogItem) intent.getSerializableExtra("itemInfo");
        accountId = intent.getIntExtra("accountId", 0);
        uniqueId = intent.getIntExtra("uniqueId", 0);
        remainingItemsList = (ArrayList<CatalogItem>) intent.getSerializableExtra("catalogItems");
        mBusinessDataList = (SearchViewDetail) intent.getSerializableExtra("providerInfo");

        Typeface font_semiBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
        Typeface font_medium = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-Regular.ttf");

        tvSubTotal.setTypeface(font_medium);
        tvTotalDiscount.setTypeface(font_semiBold);

        // to update UI
        checkCart();


        if (itemDetails != null) {

            if (itemDetails.getItems() != null) {

                // to set item Name
                tvItemName.setText(itemDetails.getItems().getDisplayName());

                if (itemDetails.getItems().isStockAvailable()) {
                    tvStock.setVisibility(View.GONE);
                    llActions.setEnabled(true);
                } else {
                    tvStock.setVisibility(View.VISIBLE);
                    llActions.setEnabled(false);
                }

                if (itemDetails.getItems().getItemDescription() != null) {
                    tvItemDescription.setVisibility(View.VISIBLE);
                    tvItemDescription.setText(itemDetails.getItems().getItemDescription());
                } else {
                    tvItemDescription.setVisibility(View.GONE);
                }

                // to get all the images into list
                if (itemDetails.getItems().getItemImagesList() != null && itemDetails.getItems().getItemImagesList().size() > 0) {
                    imagesList = itemDetails.getItems().getItemImagesList();

                    if (!((Activity) mContext).isFinishing()) {

                        shimmer.setVisibility(View.VISIBLE);
                        Glide.with(mContext)
                                .load(imagesList.get(0).getUrl())
                                .apply(new RequestOptions().error(R.drawable.icon_noimage).centerCrop())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        //on load failed
                                        shimmer.setVisibility(View.GONE);
                                        ivDisplayImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        //on load success
                                        shimmer.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(ivDisplayImage);
                    }

                    if (imagesList.size() > 1) {
                        rvImages.setVisibility(View.VISIBLE);
                        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        itemImagesAdapter = new ItemImagesAdapter(imagesList, this, false, iImageInterface);
                        rvImages.setAdapter(itemImagesAdapter);
                    } else {

                        rvImages.setVisibility(View.GONE);
                    }
                } else {

                    shimmer.setVisibility(View.GONE);
                    ivDisplayImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                }

                if (itemDetails.getItems().isShowPromotionalPrice()) {

                    tvPrice.setVisibility(View.VISIBLE);
                    tvDiscountedPrice.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(itemDetails.getItems().getPrice());
                    tvPrice.setText("₹ " + convertAmountToDecimals(amount));
                    tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    String price = String.valueOf(itemDetails.getItems().getPromotionalPrice());
                    tvDiscountedPrice.setText("₹ " + convertAmountToDecimals(price));

                } else {
                    tvDiscountedPrice.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(itemDetails.getItems().getPrice());
                    tvDiscountedPrice.setText("₹ " + convertAmountToDecimals(amount));
                    tvPrice.setVisibility(View.GONE);

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
                    item.setUniqueId(uniqueId);
                    item.setPromotionalType(itemDetails.getItems().getPromotionalPriceType());
                    item.setDiscount(itemDetails.getItems().getPromotionalPrice());
                    item.setDiscountedPrice(itemDetails.getItems().getDiscountedPrice());
                    if (itemDetails.getItems().isShowPromotionalPrice()) {
                        item.setIsPromotional(1);
                    } else {
                        item.setIsPromotional(0);
                    }

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


    public void refreshData() {

        ArrayList<CatalogItem> catalogItemsList = new ArrayList<>();
        catalogItemsList = updateCatalogItemsDiscount(remainingItemsList);
        catalogItemsList = updateCatalogItemsQuantity(catalogItemsList);
        if (catalogItemsList != null && catalogItemsList.size() > 0) {

            tvSimilarItems.setVisibility(View.VISIBLE);
        } else {
            tvSimilarItems.setVisibility(View.GONE);
        }
        detailPageItemsAdapter = new DetailPageItemsAdapter(catalogItemsList, mContext, false, iItemInterface, accountId, uniqueId);
        rvItems.setAdapter(detailPageItemsAdapter);
        rvItems.setItemTransformer(new ScaleTransformer.Builder().setMinScale(0.8f).build());
        updateCartUI();
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

    private void updateCartUI() {

        if (db.getCartCount() > 0) {

            cvItemsCart.setVisibility(View.VISIBLE);

            tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");

            if (db.getCartPrice() == db.getCartDiscountedPrice()) {

                tvSubTotal.setVisibility(View.GONE);
                tvTotalDiscount.setVisibility(View.VISIBLE);
                String amount = String.valueOf(db.getCartPrice());
                tvTotalDiscount.setText("₹ " + convertAmountToDecimals(amount));

            } else {

                tvSubTotal.setVisibility(View.VISIBLE);
                String amount = String.valueOf(db.getCartPrice());
                tvSubTotal.setText("₹ " + convertAmountToDecimals(amount));
                tvSubTotal.setPaintFlags(tvSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTotalDiscount.setVisibility(View.VISIBLE);
                String discountedPrice = String.valueOf(db.getCartDiscountedPrice());
                tvTotalDiscount.setText("₹ " + convertAmountToDecimals(discountedPrice));
            }
        } else {

            cvItemsCart.setVisibility(View.GONE);
        }
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

        try {

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
                if (db.getCartPrice() == db.getCartDiscountedPrice()) {

                    tvSubTotal.setVisibility(View.GONE);
                    tvTotalDiscount.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(db.getCartPrice());
                    tvTotalDiscount.setText("₹" + convertAmountToDecimals(amount));

                } else {

                    tvSubTotal.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(db.getCartPrice());
                    tvSubTotal.setText("₹" + convertAmountToDecimals(amount));
                    tvSubTotal.setPaintFlags(tvSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTotalDiscount.setVisibility(View.VISIBLE);
                    String discountedPrice = String.valueOf(db.getCartPrice());
                    tvTotalDiscount.setText("₹" + convertAmountToDecimals(discountedPrice));
                }

            } else {

                cvItemsCart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        checkCart();

        refreshData();
    }

    private void checkCartCount() {

        try {

            if (db.getCartCount() > 0) {

                cvItemsCart.setVisibility(View.VISIBLE);
                tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");
                if (db.getCartPrice() == db.getCartDiscountedPrice()) {

                    tvSubTotal.setVisibility(View.GONE);
                    tvTotalDiscount.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(db.getCartPrice());
                    tvTotalDiscount.setText("₹" + convertAmountToDecimals(amount));

                } else {

                    tvSubTotal.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(db.getCartPrice());
                    tvSubTotal.setText("₹" + convertAmountToDecimals(amount));
                    tvSubTotal.setPaintFlags(tvSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTotalDiscount.setVisibility(View.VISIBLE);
                    String discountedPrice = String.valueOf(db.getCartDiscountedPrice());
                    tvTotalDiscount.setText("₹" + convertAmountToDecimals(discountedPrice));
                }
            } else {

                cvItemsCart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageClick(String url) {

        if (!((Activity) mContext).isFinishing()) {

            shimmer.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().error(R.drawable.icon_noimage).centerCrop())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //on load failed
                            shimmer.setVisibility(View.GONE);
                            ivDisplayImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //on load success
                            shimmer.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(ivDisplayImage);
        }

//        PicassoTrustAll.getInstance(mContext).load(url).into(ivDisplayImage, new Callback() {
//            @Override
//            public void onSuccess() {
//
//                shimmer.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onError() {
//
//                shimmer.setVisibility(View.GONE);
//                ivDisplayImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
//            }
//        });
    }

    @Override
    public void onContinueClick() {

        checkCart();
        refreshData();
        updateCartUI();

    }

    @Override
    public void onClearClick() {

        checkCart();
        removeQuantity();

    }

    public static String convertAmountToDecimals(String price) {

        double a = Double.parseDouble(price);
        DecimalFormat decim = new DecimalFormat("0.00");
        Double price2 = Double.parseDouble(decim.format(a));
        String amount = decim.format(price2);
        return amount;

    }

    @Override
    public void onItemClick(CatalogItem catalogItem) {

    }

    @Override
    public void checkItemQuantity() {

        updateCartUI();

    }

    private void removeQuantity() {

        for (int i = 0; i < remainingItemsList.size(); i++) {

            remainingItemsList.get(i).getItems().setItemQuantity(0);
        }
        refreshData();
    }


}