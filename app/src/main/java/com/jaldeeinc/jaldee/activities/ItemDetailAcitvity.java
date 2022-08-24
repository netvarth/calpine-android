package com.jaldeeinc.jaldee.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.CustomSwipe.DiscreteScrollView;
import com.jaldeeinc.jaldee.CustomSwipe.transform.ScaleTransformer;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.IDialogInterface;
import com.jaldeeinc.jaldee.Interface.IImageInterface;
import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.Interface.IServiceOption;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CartItemServceOptnAdapter;
import com.jaldeeinc.jaldee.adapter.DetailPageItemsAdapter;
import com.jaldeeinc.jaldee.adapter.ItemImagesAdapter;
import com.jaldeeinc.jaldee.adapter.ItemsAdapter;
import com.jaldeeinc.jaldee.adapter.SelectedItemsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AutofitTextView;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewBoldItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.ElegantNumberButton;
import com.jaldeeinc.jaldee.custom.KeyPairBoolData;
import com.jaldeeinc.jaldee.custom.OrderitemServiceoptionadditemDialog;
import com.jaldeeinc.jaldee.custom.SelectedItemsDialog;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.AnswerLine;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.model.DataGrid;
import com.jaldeeinc.jaldee.model.GridColumnAnswerLine;
import com.jaldeeinc.jaldee.model.QuestionnairInpt;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.ItemImages;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.SearchViewDetail;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class ItemDetailAcitvity extends AppCompatActivity implements IImageInterface, IDialogInterface, IItemInterface, IServiceOption, IDataGrid {

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
    CustomTextViewBoldItalic tvDiscountedPrice;

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
    private Catalog catalogInfo;
    /**
     * ServiceOption
     **/
    private Questionnaire questionnaire = new Questionnaire();
    IServiceOption iServiceOptionListOptionChange;
    IDataGrid iDataGrid;
    CartItemModel itemDetails1;
    QuestionnairInpt answerLine;

    /**
     * ServiceOption
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail_acitvity);
        ButterKnife.bind(ItemDetailAcitvity.this);
        iImageInterface = ItemDetailAcitvity.this;
        iDialogInterface = ItemDetailAcitvity.this;
        iItemInterface = ItemDetailAcitvity.this;
        iDataGrid = ItemDetailAcitvity.this;

        mContext = ItemDetailAcitvity.this;
        iServiceOptionListOptionChange = this;
        db = new DatabaseHandler(mContext);
        vibe = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        Intent intent = getIntent();
        itemDetails = (CatalogItem) intent.getSerializableExtra("itemInfo");
        accountId = intent.getIntExtra("accountId", 0);
        uniqueId = intent.getIntExtra("uniqueId", 0);
        catalogInfo = (Catalog) intent.getSerializableExtra("catalogInfo");
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
                    tvPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));
                    tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    String price = String.valueOf(itemDetails.getItems().getPromotionalPrice());
                    tvDiscountedPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(price)));

                } else {
                    tvDiscountedPrice.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(itemDetails.getItems().getPrice());
                    tvDiscountedPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));
                    tvPrice.setVisibility(View.GONE);

                }


            }

        }


        flAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (db.getAccountId() == 0 || db.getAccountId() == accountId) {
                    /*flAdd.setVisibility(View.GONE);
                    numberButton.setVisibility(View.VISIBLE);
                    numberButton.setNumber("1");*/

                    apiGetOrderItemServiceOptionQnr(itemDetails, accountId, false);

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
                    //checkCartCount();  //commented because of service option
                    updateCartUI();  // new line because of service option

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
                    boolean isAddedServiceOption = db.isAddedServiceOption(itemDetails.getItems().getItemId());
                    if (isAddedServiceOption) {

                        progressBar.setVisibility(View.GONE);
                        boolean isDecreaseQty = false;
                        if (newValue < oldValue) {
                            isDecreaseQty = true;
                        } else if (newValue > oldValue) {
                            isDecreaseQty = false;
                        }
                        onAddClick(itemDetails, (DetailPageItemsAdapter.ViewHolder) null, isDecreaseQty, newValue);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms

                                db.addQuantity(itemDetails.getItems().getItemId(), newValue);
                                //checkCartCount(); // commented becasuse of sericeoption
                                updateCartUI(); // newline because of service option
                                progressBar.setVisibility(View.GONE);

                            }
                        }, 300);
                        // plus and minus icons are disabled based on minQuantity and new value
                    }

                } else {

                    // give fadeout color for plus
                    cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                    cvPlus.setClickable(false);
                    //checkCartCount();   // commented because of service option
                    updateCartUI();  // new line because of service option

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

                selectedItemsDialog = new SelectedItemsDialog(mContext, iDialogInterface, accountId);
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
                intent.putExtra("providerAccountId", accountId);
                intent.putExtra("providerInfo", mBusinessDataList);
                intent.putExtra("catalogInfo", catalogInfo);
                intent.putExtra("serviceId", catalogInfo.getCatLogId());

                startActivity(intent);

            }
        });

    }


    public void refreshData() {

        ArrayList<CatalogItem> catalogItemsList = new ArrayList<>();
        catalogItemsList = updateCatalogItemsDiscount(remainingItemsList);
        catalogItemsList = updateCatalogItemsQuantity(catalogItemsList);
        if (catalogItemsList.size() > 0) {

            tvSimilarItems.setVisibility(View.VISIBLE);
        } else {
            tvSimilarItems.setVisibility(View.GONE);
        }
        detailPageItemsAdapter = new DetailPageItemsAdapter(catalogItemsList, mContext, false, iItemInterface, accountId, uniqueId, iDataGrid);
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
        try {

            if (db.getCartCount() > 0) {

                cvItemsCart.setVisibility(View.VISIBLE);

                tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");

                if (db.getCartPrice() == db.getCartDiscountedPrice()) {

                    tvSubTotal.setVisibility(View.GONE);
                    tvTotalDiscount.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(db.getCartPrice());
                    tvTotalDiscount.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));

                } else {

                    tvSubTotal.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(db.getCartPrice());
                    tvSubTotal.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));
                    tvSubTotal.setPaintFlags(tvSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTotalDiscount.setVisibility(View.VISIBLE);
                    String discountedPrice = String.valueOf(db.getCartDiscountedPrice());
                    tvTotalDiscount.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(discountedPrice)));
                }
            } else {

                cvItemsCart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                //checkCartCount(); // commented because of service option
                updateCartUI();  // newline because of service option
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
                    tvTotalDiscount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));

                } else {

                    tvSubTotal.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(db.getCartPrice());
                    tvSubTotal.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));
                    tvSubTotal.setPaintFlags(tvSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTotalDiscount.setVisibility(View.VISIBLE);
                    String discountedPrice = String.valueOf(db.getCartPrice());
                    tvTotalDiscount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(discountedPrice)));
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
/*   // commented because of serviceoption

    private void checkCartCount() {

        try {

            if (db.getCartCount() > 0) {

                cvItemsCart.setVisibility(View.VISIBLE);
                tvItemsCount.setText("Your Order " + "(" + db.getCartCount() + ")");
                if (db.getCartPrice() == db.getCartDiscountedPrice()) {

                    tvSubTotal.setVisibility(View.GONE);
                    tvTotalDiscount.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(db.getCartPrice());
                    tvTotalDiscount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));

                } else {

                    tvSubTotal.setVisibility(View.VISIBLE);
                    String amount = String.valueOf(db.getCartPrice());
                    tvSubTotal.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(amount)));
                    tvSubTotal.setPaintFlags(tvSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvTotalDiscount.setVisibility(View.VISIBLE);
                    String discountedPrice = String.valueOf(db.getCartDiscountedPrice());
                    tvTotalDiscount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(discountedPrice)));
                }
            } else {

                cvItemsCart.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

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

    @Override
    public void onItemClick(CatalogItem catalogItem) {

    }

    public void saveToDB(CatalogItem itemDetails, String serviceoption, String serviceOptionAttachedImages) {
        CartItemModel item = new CartItemModel();
        item.setItemId(itemDetails.getItems().getItemId());
        item.setAccountId(accountId);
        item.setCatalogId(itemDetails.getCatalogId());
        item.setItemName(itemDetails.getItems().getDisplayName());
        //item.setImageUrl(catalogItem.getItems().getDisplayImage());
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
        item.setItemType(itemDetails.getItems().getItemType());
        if (itemDetails.getItems().isTaxable()) {
            item.setIsTaxable(1);
        } else {
            item.setIsTaxable(0);
        }
        if (itemDetails.getItems().isTaxable()) {
            if (catalogInfo != null) {
                item.setTax(catalogInfo.getTaxPercentage());
            }
        }
        if (itemDetails.getItems().isShowPromotionalPrice()) {
            item.setIsPromotional(1);
        } else {
            item.setIsPromotional(0);
        }
        if (questionnaire.getQuestionsList() != null) {
            Gson gson = new Gson();
            String json = gson.toJson(questionnaire);
            item.setQuestionnaire(json);
        }
        if (serviceoption != null && !serviceoption.trim().isEmpty()) {
            item.setServiceOptioniput(serviceoption);
        }
        if (serviceOptionAttachedImages != null && !serviceOptionAttachedImages.trim().isEmpty()) {
            item.setServiceOptionAtachedImages(serviceOptionAttachedImages);
        }
        if (questionnaire.getQuestionsList() != null && serviceoption != null && !serviceoption.trim().isEmpty()) {
            Gson gson = new Gson();
            String qnr = gson.toJson(questionnaire);
            try {
                ItemsActivity itemsActivity = new ItemsActivity();
                float serviceOtpionPrice = itemsActivity.calculateTotalPrice(itemDetails.getItems().getItemId(), qnr, serviceoption);
                item.setServiceOptionPrice(serviceOtpionPrice);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        boolean isSaved = db.insertItemToCart(item);
        if (isSaved) {
            if (itemsAdapterVieHolder != null) {
                itemsAdapterVieHolder.flAdd.setVisibility(View.GONE);
                itemsAdapterVieHolder.numberButton.setVisibility(View.VISIBLE);
                itemsAdapterVieHolder.numberButton.setNumber("1");
            } else if (detailItemsAdapterVieHolder != null) {
                detailItemsAdapterVieHolder.flAdd.setVisibility(View.GONE);
                detailItemsAdapterVieHolder.numberButton.setVisibility(View.VISIBLE);
                detailItemsAdapterVieHolder.numberButton.setNumber("1");
            } else {
                flAdd.setVisibility(View.GONE);
                numberButton.setVisibility(View.VISIBLE);
                numberButton.setNumber("1");
            }
        }
        //checkCartCount(); // commented because of service option
        updateCartUI();  // newline because of service option
    }

    @Override
    public void checkItemQuantity() {
        updateCartUI();
    }

    ItemsAdapter.ViewHolder itemsAdapterVieHolder;

    @Override
    public void checkItemQuantity(CatalogItem itemDetails, ItemsAdapter.ViewHolder viewHolder) {
        itemsAdapterVieHolder = viewHolder;
        apiGetOrderItemServiceOptionQnr(itemDetails, accountId, false);

    }

    DetailPageItemsAdapter.ViewHolder detailItemsAdapterVieHolder;

    @Override
    public void checkItemQuantity(CatalogItem itemDetails, DetailPageItemsAdapter.ViewHolder viewHolder) {
        detailItemsAdapterVieHolder = viewHolder;
        apiGetOrderItemServiceOptionQnr(itemDetails, accountId, false);
    }

    private void removeQuantity() {

        for (int i = 0; i < remainingItemsList.size(); i++) {

            remainingItemsList.get(i).getItems().setItemQuantity(0);
        }
        refreshData();
    }

    private void apiGetOrderItemServiceOptionQnr(CatalogItem itemDetails, int providerId, boolean fromAddedAsNew) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        //final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        //mDialog.show();
        Call<Questionnaire> call = apiService.getOrderItemServiceOptionQnr(itemDetails.getItems().getItemId(), providerId);
        call.enqueue(new retrofit2.Callback<Questionnaire>() {
            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {
                Config.logV("URL------getQNR response---------" + response.raw().request().url().toString().trim());
                Config.logV("Response--code-------------------------" + response.code());
                if (response.code() == 200) {
                    questionnaire = response.body();
                    //                        Map<String, Object> retMap = new Gson().fromJson(
//                                questionnaire.getQuestionsList().get(0).getGetQuestion().getPriceGridList(), new TypeToken<HashMap<String, Object>>() {
//                                }.getType()
//                        );
                    if (questionnaire != null && questionnaire.getQuestionnaireId() != null && questionnaire.getQuestionsList() != null && questionnaire.getQuestionsList().size() > 0) {
                        OrderitemServiceoptionadditemDialog orderitemServiceoptionadditemDialog = OrderitemServiceoptionadditemDialog.newInstance(questionnaire, itemDetails, null, iServiceOptionListOptionChange, iItemInterface, fromAddedAsNew);
                        final FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, orderitemServiceoptionadditemDialog).addToBackStack("DataGrid")
                                .commit();
                    } else {
                        saveToDB(itemDetails, null, null);
                    }
                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {

            }
        });
    }

    @Override
    public void updateTotalPrice() {

    }

    @Override
    public void radioListItemSelected(String s, Float price) {

    }

    @Override
    public void savetoDataBase(CatalogItem itemDetails, String serviceOption, String serviceOptionAtachedImages) {
        saveToDB(itemDetails, serviceOption, serviceOptionAtachedImages);
    }

    @Override
    public KeyPairBoolData openImageOptions(KeyPairBoolData fileObject, String qLabelName, HashMap<String, View> viewsList) {
        return null;
    }

    @Override
    public void onEditClick(DataGrid gridObj, int position) {

    }

    @Override
    public void onEditClick(Questionnaire qnr, QuestionnairInpt answerGridObj, int position, CartItemModel itemDetails, boolean isEdit) {

    }

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onAddClick(int position) {

    }

    @Override
    public void onAddClick(CatalogItem catalogItem, ItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    @Override
    public void onAddClick(CartItemModel cartItemModel, SelectedItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {

    }

    @Override
    public void onAddClick(CatalogItem catalogItem, DetailPageItemsAdapter.ViewHolder viewHolder, boolean isDecreaseQty, int newValue) {
        DetailPageItemsAdapter.ViewHolder vHolder = viewHolder;
        if (isDecreaseQty) {
            Dialog removeItemSrvcoption;

            removeItemSrvcoption = new Dialog(mContext);
            removeItemSrvcoption.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            removeItemSrvcoption.requestWindowFeature(Window.FEATURE_NO_TITLE);
            removeItemSrvcoption.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            removeItemSrvcoption.setContentView(R.layout.remove_cart_item_serviceoption_dialog);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            removeItemSrvcoption.setCancelable(false);
            removeItemSrvcoption.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            removeItemSrvcoption.getWindow().setGravity(Gravity.BOTTOM);
            ImageView iv_close = removeItemSrvcoption.findViewById(R.id.iv_close);
            CardView cv_cancel = removeItemSrvcoption.findViewById(R.id.cv_cancel);
            CardView cv_done = removeItemSrvcoption.findViewById(R.id.cv_done);
            RecyclerView rv_itemDetails = removeItemSrvcoption.findViewById(R.id.rv_itemDetails);

            removeItemSrvcoption.show();
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItemSrvcoption.cancel();
                    int qty;
                    if (vHolder == null) {
                        qty = Integer.parseInt(numberButton.getNumber());
                        numberButton.setNumber(String.valueOf(qty + 1));   // for corecting quantity of items
                    } else {
                        qty = Integer.parseInt(vHolder.numberButton.getNumber());
                        vHolder.numberButton.setNumber(String.valueOf(qty + 1));   // for corecting quantity of items
                    }

                }
            });
            cv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItemSrvcoption.cancel();
                    if (vHolder == null) {
                        int qty = Integer.parseInt(numberButton.getNumber());
                        numberButton.setNumber(String.valueOf(qty + 1));   // for corecting quantity of items
                    } else {
                        int qty = Integer.parseInt(vHolder.numberButton.getNumber());
                        vHolder.numberButton.setNumber(String.valueOf(qty + 1));   // for corecting quantity of items
                    }
                }
            });
            cv_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeItemSrvcoption.cancel();
                    String qnr = db.getServiceOptionQnr(itemDetails1.getItemId());

                    if (itemDetails1 != null) {
                        String inputImages = db.getServiceOptioniputImages(itemDetails1.getItemId());
                        float serviceOtpionPrice = 0;
                        if (qnr.trim() != null && answerLine != null && !answerLine.getAnswerLines().isEmpty()) {
                            Gson gson = new Gson();
                            try {
                                ItemsActivity itemsActivity = new ItemsActivity();
                                serviceOtpionPrice = itemsActivity.calculateTotalPrice(itemDetails1.getItemId(), qnr, gson.toJson(answerLine));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        boolean result = db.updateServiceOptionInput(itemDetails1.getItemId(), new Gson().toJson(answerLine), inputImages, serviceOtpionPrice);
                        if (result) {
                            db.addQuantity(itemDetails1.getItemId(), newValue);

                        }
                    } else {
                        if (vHolder == null) {
                            int qty = Integer.parseInt(numberButton.getNumber());
                            numberButton.setNumber(String.valueOf(qty + 1));   // for corecting quantity of items
                        } else {
                            int qty = Integer.parseInt(vHolder.numberButton.getNumber());
                            vHolder.numberButton.setNumber(String.valueOf(qty + 1));   // for corecting quantity of items
                        }
                    }
                }
            });

            CartItemModel cartItemModel = new CartItemModel();
            cartItemModel.setItemId(catalogItem.getItems().getItemId());

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            rv_itemDetails.setLayoutManager(linearLayoutManager);
            CartItemServceOptnAdapter cartItemServceOptnAdapter = new CartItemServceOptnAdapter(cartItemModel, mContext, iDataGrid, false, true);
            rv_itemDetails.setAdapter(cartItemServceOptnAdapter);
            rv_itemDetails.setVisibility(View.VISIBLE);
        } else {
            float totalPrice = 0;

            answerLine = new QuestionnairInpt();
            String itemServcOptionName = "";
            StringJoiner joiner = new StringJoiner(",");

            String input = db.getServiceOptioniput(catalogItem.getItems().getItemId());
            String inputImages = db.getServiceOptioniputImages(catalogItem.getItems().getItemId());
//
//        if (qnr != null && !qnr.trim().isEmpty()) {
//            Gson gson = new Gson();
//            this.questionaire = gson.fromJson(qnr, Questionnaire.class);
//        }

            Dialog serviceOptionrepeatSameDialog;

            serviceOptionrepeatSameDialog = new Dialog(mContext);
            serviceOptionrepeatSameDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            serviceOptionrepeatSameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            serviceOptionrepeatSameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            serviceOptionrepeatSameDialog.setContentView(R.layout.service_option_repeat_same_dialog);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            serviceOptionrepeatSameDialog.setCancelable(false);
            serviceOptionrepeatSameDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            serviceOptionrepeatSameDialog.getWindow().setGravity(Gravity.BOTTOM);
            ImageView iv_close = serviceOptionrepeatSameDialog.findViewById(R.id.iv_close);
            LinearLayout ll_Add_new = serviceOptionrepeatSameDialog.findViewById(R.id.ll_Add_new);
            LinearLayout ll_repeat_same = serviceOptionrepeatSameDialog.findViewById(R.id.ll_repeat_same);
            TextView tv_itemName = serviceOptionrepeatSameDialog.findViewById(R.id.tv_itemName);
            TextView tv_item_Hint = serviceOptionrepeatSameDialog.findViewById(R.id.tv_item_Hint);
            TextView tv_item_price = serviceOptionrepeatSameDialog.findViewById(R.id.tv_item_price);
            TextView tv_total_price = serviceOptionrepeatSameDialog.findViewById(R.id.tv_total_price);
            LinearLayout llDivider = serviceOptionrepeatSameDialog.findViewById(R.id.ll_divider);
            serviceOptionrepeatSameDialog.show();

            if (input != null && !input.trim().isEmpty()) {
                Gson gson = new Gson();
                answerLine = gson.fromJson(input, QuestionnairInpt.class);
            }
            if (answerLine != null && !answerLine.getAnswerLines().isEmpty()) {
                ArrayList<AnswerLine> als = answerLine.getAnswerLines();
                for (AnswerLine al : als) {
                    DataGrid dataGrid = new DataGrid();
                    ArrayList<DataGrid> dataGridList = new ArrayList<>();
                    dataGridList = al.getDataGridListList();
                    dataGrid = dataGridList.get(dataGridList.size() - 1);
                    ArrayList<GridColumnAnswerLine> dataGridListColumn = dataGrid.dataGridListColumn;
                    for (int i = 0; i < dataGridListColumn.size(); i++) {
                        GridColumnAnswerLine gridColumnAnswerLine = dataGridListColumn.get(i);
                        JsonArray ja = gridColumnAnswerLine.getColumn().getAsJsonArray("list");
                        if (i == 0) {
                            if (ja.get(0).toString().trim() != null && !ja.get(0).toString().trim().isEmpty()) {
                                joiner.add(ja.get(0).getAsString());
                            }
                        }
                        totalPrice = totalPrice + gridColumnAnswerLine.getPrice();
                    }
                }

                if (catalogItem != null && catalogItem.getItems().getDisplayName().trim() != null) {
                    tv_itemName.setText(catalogItem.getItems().getDisplayName());
                } else {
                    tv_itemName.setVisibility(View.GONE);
                }
                if (totalPrice > 0) {
                    tv_item_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalPrice));
                    tv_total_price.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(totalPrice));
                } else {
                    tv_total_price.setText("₹ 0");
                }
                String hint = "";
                if (joiner.length() > 0) {
                    hint = joiner.toString();
                    tv_item_Hint.setText(hint);
                    tv_item_Hint.setVisibility(View.VISIBLE);
                } else {
                    tv_item_Hint.setVisibility(View.GONE);
                }
            } else {
                tv_itemName.setVisibility(View.GONE);
                tv_item_Hint.setVisibility(View.GONE);
                //tv_iteme_dit.setVisibility(View.GONE);
                tv_total_price.setVisibility(View.GONE);
            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    serviceOptionrepeatSameDialog.cancel();
                    if (vHolder == null) {
                        int qty = Integer.parseInt(numberButton.getNumber());
                        if (qty > 0) {
                            numberButton.setNumber(String.valueOf(qty - 1));   // for corecting quantity of items
                        }
                    } else {
                        int qty = Integer.parseInt(vHolder.numberButton.getNumber());
                        if (qty > 0) {
                            vHolder.numberButton.setNumber(String.valueOf(qty - 1));   // for corecting quantity of items
                        }
                    }
                }
            });
            ll_Add_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // llAdd.performClick();
                    serviceOptionrepeatSameDialog.cancel();
                    apiGetOrderItemServiceOptionQnr(catalogItem, accountId, true);

                }
            });
            ll_repeat_same.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    serviceOptionrepeatSameDialog.cancel();
                    Toast.makeText(mContext, "item added", Toast.LENGTH_LONG).show();
//                DynamicToast.make(context, "item added",
//                        ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                    if (answerLine != null && !answerLine.getAnswerLines().isEmpty()) {
                        ArrayList<AnswerLine> als = answerLine.getAnswerLines();
                        for (int k = 0; k < als.size(); k++) {

                            AnswerLine al = als.get(k);
                            DataGrid dataGrid = new DataGrid();
                            ArrayList<DataGrid> dataGridList = new ArrayList<>();
                            dataGridList = al.getDataGridListList();
                            dataGrid = dataGridList.get(dataGridList.size() - 1);
                            dataGridList.add(dataGrid);
                            JsonObject answer = new JsonObject();
                            Gson gson = new Gson();
                            JsonElement element = gson.toJsonTree(dataGridList);
                            answer.add("dataGridList", element);
                            answerLine.getAnswerLines().get(k).setAnswer(answer);
                        }
                        String qnr = db.getServiceOptionQnr(catalogItem.getItems().getItemId());

                        float serviceOtpionPrice = 0;
                        if (qnr.trim() != null && answerLine != null && !answerLine.getAnswerLines().isEmpty()) {
                            Gson gson = new Gson();
                            try {
                                ItemsActivity itemsActivity = new ItemsActivity();
                                serviceOtpionPrice = itemsActivity.calculateTotalPrice(catalogItem.getItems().getItemId(), qnr, gson.toJson(answerLine));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        db.addQuantity(catalogItem.getItems().getItemId(), newValue);
                        db.updateServiceOptionInput(catalogItem.getItems().getItemId(), new Gson().toJson(answerLine), inputImages, serviceOtpionPrice);
                        finish();
                        startActivity(getIntent());
                    }
                }
            });
        }
    }

    @Override
    public void onRemoveClick(int position, Questionnaire questionnaire, QuestionnairInpt answerLine, CartItemModel itemDetails) {
        this.answerLine = answerLine;
        this.itemDetails1 = itemDetails;
    }
}