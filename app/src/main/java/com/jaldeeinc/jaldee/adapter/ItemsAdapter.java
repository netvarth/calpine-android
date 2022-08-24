package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jaldeeinc.jaldee.Interface.IDataGrid;
import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.AutofitTextView;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.ElegantNumberButton;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private ArrayList<CatalogItem> itemsList;
    public Context context;
    private boolean isLoading = true;
    private int lastPosition = -1;
    private IItemInterface iItemInterface;
    private DatabaseHandler db;
    private int accountId, uniqueId;
    Vibrator vibe;
    ProgressBar progressBar;
    private CardView cvPlus;
    private Catalog catalogInfo;
    public IDataGrid iDataGrid;


    public ItemsAdapter(ArrayList<CatalogItem> itemsList, Context context, boolean isLoading, IItemInterface iItemInterface, int accountId, int uniqueId, Catalog catalogInfo, IDataGrid iDataGrid) {
        this.itemsList = itemsList;
        this.context = context;
        this.isLoading = isLoading;
        this.iItemInterface = iItemInterface;
        db = new DatabaseHandler(context);
        this.accountId = accountId;
        this.uniqueId = uniqueId;
        this.catalogInfo = catalogInfo;
        this.iDataGrid = iDataGrid;

        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shimmer, parent, false);
            return new ItemsAdapter.ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
            return new ItemsAdapter.ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder viewHolder, int position) {

        if (!isLoading) {
            final CatalogItem catalogItem = itemsList.get(position);

            setAnimation(viewHolder.cvCard, position);

            if (catalogItem.getItems().isStockAvailable()) {
                viewHolder.llLoader.setVisibility(View.GONE);
            } else {
                viewHolder.llLoader.setVisibility(View.VISIBLE);
            }

            if (catalogItem.getItems().isShowPromotionalPrice()) {

                viewHolder.rlTag.setVisibility(View.VISIBLE);
                viewHolder.tvTag.setText(catalogItem.getItems().getPromotionLabel());
            } else {

                viewHolder.rlTag.setVisibility(View.GONE);
            }

            if (catalogItem.getItems().getItemQuantity() == 0) {
                viewHolder.numberButton.setVisibility(View.GONE);
                viewHolder.flAdd.setVisibility(View.VISIBLE);
            } else {
                viewHolder.flAdd.setVisibility(View.GONE);
                viewHolder.numberButton.setVisibility(View.VISIBLE);
                viewHolder.numberButton.setNumber(String.valueOf(catalogItem.getItems().getItemQuantity()));
            }

            // to set itemName
            if (catalogItem.getItems().getDisplayName() != null) {

                viewHolder.tvItemName.setText(catalogItem.getItems().getDisplayName());
            }

            // to set shortDescription of item
            if (catalogItem.getItems().getShortDescription() != null) {
                viewHolder.tvShortDescription.setText(catalogItem.getItems().getShortDescription());
            }

            // to set item Image
            if (catalogItem.getItems().getItemImagesList() != null && catalogItem.getItems().getItemImagesList().size() > 0) {

                for (int i = 0; i < catalogItem.getItems().getItemImagesList().size(); i++) {

                    if (catalogItem.getItems().getItemImagesList().get(i).isDisplayImage()) {

                        if (!((Activity) context).isFinishing()) {

                            viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
                            viewHolder.shimmer.setVisibility(View.VISIBLE);

                            Glide.with(context)
                                    .load(catalogItem.getItems().getItemImagesList().get(i).getUrl())
                                    .apply(new RequestOptions().error(R.drawable.icon_noimage).centerCrop())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            //on load failed
                                            viewHolder.shimmer.setVisibility(View.GONE);
                                            viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
                                            viewHolder.bIvItemImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_noimage));
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            //on load success
                                            viewHolder.shimmer.setVisibility(View.GONE);
                                            viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
                                            return false;
                                        }
                                    })
                                    .into(viewHolder.bIvItemImage);
                        }
//                        PicassoTrustAll.getInstance(context).load(catalogItem.getItems().getItemImagesList().get(i).getUrl()).into(viewHolder.bIvItemImage, new Callback() {
//                            @Override
//                            public void onSuccess() {
//
//                                viewHolder.shimmer.setVisibility(View.GONE);
//                                viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onError() {
//
//                                viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
//                                viewHolder.bIvItemImage.setImageResource(R.drawable.icon_noimage);
//                            }
//                        });
                    } else {

                        viewHolder.shimmer.setVisibility(View.GONE);
                        viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
                        Glide.with(context).load(R.drawable.ic_item).into(viewHolder.bIvItemImage);
                    }
                }
            } else {

                viewHolder.shimmer.setVisibility(View.GONE);
                viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.ic_item).into(viewHolder.bIvItemImage);
            }

            // to set itemPrice

            if (catalogItem.getItems().isShowPromotionalPrice()) {

                viewHolder.tvPrice.setVisibility(View.VISIBLE);
                viewHolder.tvDiscountedPrice.setVisibility(View.VISIBLE);
                String amount = Config.getAmountNoOrTwoDecimalPoints(catalogItem.getItems().getPrice());
                viewHolder.tvPrice.setText("₹ " + amount);
                viewHolder.tvPrice.setPaintFlags(viewHolder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                String price = Config.getAmountNoOrTwoDecimalPoints(catalogItem.getItems().getPromotionalPrice());
                viewHolder.tvDiscountedPrice.setText("₹ " + price);

            } else {
                viewHolder.tvDiscountedPrice.setVisibility(View.VISIBLE);
                String amount = Config.getAmountNoOrTwoDecimalPoints(catalogItem.getItems().getPrice());
                viewHolder.tvDiscountedPrice.setText("₹ " + amount);
                viewHolder.tvPrice.setVisibility(View.GONE);

            }

            viewHolder.flAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (db.getAccountId() == 0 || db.getAccountId() == accountId) {

                       /* viewHolder.flAdd.setVisibility(View.GONE);
                        viewHolder.numberButton.setVisibility(View.VISIBLE);
                        viewHolder.numberButton.setNumber("1");*/

                        /*CartItemModel item = new CartItemModel();
                        item.setItemId(catalogItem.getItems().getItemId());
                        item.setAccountId(accountId);
                        item.setCatalogId(catalogItem.getCatalogId());
                        item.setItemName(catalogItem.getItems().getDisplayName());
                        item.setImageUrl(catalogItem.getItems().getDisplayImage());
                        item.setItemPrice(catalogItem.getItems().getPrice());
                        item.setMaxQuantity(catalogItem.getMaxQuantity());
                        item.setQuantity(1);
                        item.setUniqueId(uniqueId);
                        item.setPromotionalType(catalogItem.getItems().getPromotionalPriceType());
                        item.setDiscount(catalogItem.getItems().getPromotionalPrice());
                        item.setDiscountedPrice(catalogItem.getItems().getDiscountedPrice());
                        item.setItemType(catalogItem.getItems().getItemType());
                        if (catalogItem.getItems().isTaxable()) {
                            item.setIsTaxable(1);
                        } else {
                            item.setIsTaxable(0);
                        }
                        if (catalogItem.getItems().isTaxable()) {
                            if (catalogInfo != null) {
                                item.setTax(catalogInfo.getTaxPercentage());
                            }
                        }
                        if (catalogItem.getItems().isShowPromotionalPrice()) {
                            item.setIsPromotional(1);
                        } else {
                            item.setIsPromotional(0);
                        }

                        db.insertItemToCart(item);

                        iItemInterface.checkItemQuantity(); // commented because service option*/
                        iItemInterface.checkItemQuantity(catalogItem, viewHolder);  // newline because service option

                    } else {

                        showAlertDialog();

                    }
                }
            });

            viewHolder.numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                    cvPlus = view.findViewById(R.id.cv_add);

                    if (newValue < 1) {

                        db.addQuantity(catalogItem.getItems().getItemId(), 0);
                        viewHolder.numberButton.setVisibility(View.GONE);
                        viewHolder.flAdd.setVisibility(View.VISIBLE);
                        iItemInterface.checkItemQuantity();

                    } else if (newValue <= catalogItem.getMaxQuantity()) {

                        // fadein cardview here
                        cvPlus.setBackgroundResource(R.drawable.plus_background);
                        cvPlus.setClickable(true);
                        progressBar = view.findViewById(R.id.myProgress);
                        progressBar.setVisibility(View.VISIBLE);
                        vibe.vibrate(100);

                        if (newValue == catalogItem.getMaxQuantity()) {

                            // give fadeout color for plus
                            cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                            cvPlus.setClickable(false);
                        }
                        boolean isAddedServiceOption = db.isAddedServiceOption(catalogItem.getItems().getItemId());
                        if (isAddedServiceOption) {
                            progressBar.setVisibility(View.GONE);
                            boolean isDecreaseQty = false;
                            if(newValue < oldValue){
                                isDecreaseQty = true;
                            } else if(newValue > oldValue) {
                                isDecreaseQty = false;
                            }
                            iDataGrid.onAddClick(catalogItem, viewHolder, isDecreaseQty, newValue);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms

                                    db.addQuantity(catalogItem.getItems().getItemId(), newValue);
                                    iItemInterface.checkItemQuantity();
                                    progressBar.setVisibility(View.GONE);

                                }
                            }, 300);
                            // plus and minus icons are disabled based on minQuantity and new value
                        }

                    } else {

                        // give fadeout color for plus
                        cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                        cvPlus.setClickable(false);
                        iItemInterface.checkItemQuantity();

                    }

                }
            });

            if (catalogItem.getMaxQuantity() == Integer.parseInt(viewHolder.numberButton.getNumber())) {
                cvPlus = viewHolder.numberButton.findViewById(R.id.cv_add);
                cvPlus.setBackgroundResource(R.drawable.disabled_plus);
                cvPlus.setClickable(false);
            }

            viewHolder.cvCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (catalogItem.getItems() != null) {
                        iItemInterface.onItemClick(catalogItem);
                    }
                }
            });

            viewHolder.llActionPreventor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            viewHolder.llLoader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        } else {

            ItemsAdapter.ViewHolder skeletonViewHolder = (ItemsAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    private void showAlertDialog() {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.order_conflict);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
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
                iItemInterface.checkItemQuantity();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : itemsList.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.bIvItemImage);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextViewBold tvItemName;
        private AutofitTextView tvPrice, tvDiscountedPrice;
        private BorderImageView bIvItemImage;
        public ElegantNumberButton numberButton;
        private CardView cvCard;
        public FrameLayout flAdd;
        private SkeletonLoadingView shimmer;
        private LinearLayout llLoader, llActionPreventor;
        private RelativeLayout rlTag;
        private CustomTextViewMedium tvTag, tvShortDescription;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {

                Typeface tyface1 = Typeface.createFromAsset(context.getAssets(),
                        "fonts/JosefinSans-SemiBold.ttf");
                Typeface tyface2 = Typeface.createFromAsset(context.getAssets(),
                        "fonts/JosefinSans-Light.ttf");
                cvCard = itemView.findViewById(R.id.cv_card);
                bIvItemImage = itemView.findViewById(R.id.iv_itemImage);
                tvItemName = itemView.findViewById(R.id.tv_itemName);
                tvPrice = itemView.findViewById(R.id.tv_price);
                numberButton = itemView.findViewById(R.id.number_button);
                flAdd = itemView.findViewById(R.id.fl_add);
                tvDiscountedPrice = itemView.findViewById(R.id.tv_discountedPrice);
                shimmer = itemView.findViewById(R.id.shimmer);
                llLoader = itemView.findViewById(R.id.ll_loader);
                tvTag = itemView.findViewById(R.id.tv_tag);
                rlTag = itemView.findViewById(R.id.rl_tag);
                tvPrice.setTypeface(tyface2);
                tvDiscountedPrice.setTypeface(tyface1);
                llActionPreventor = itemView.findViewById(R.id.ll_actionPreventor);
                tvShortDescription = itemView.findViewById(R.id.tv_shortDescription);

            }
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static String getMoneyFormat(String number) {

        if (!number.isEmpty()) {
            double val = Double.parseDouble(number);
            return NumberFormat.getNumberInstance(Locale.US).format(val);
        } else {
            return "0";
        }
    }

}
