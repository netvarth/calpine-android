package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.ItemsActivity;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.ElegantNumberButton;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
import com.squareup.picasso.Callback;

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
    private int accountId;
    Vibrator vibe;
    ProgressBar progressBar;
    private CardView cvPlus;


    public ItemsAdapter(ArrayList<CatalogItem> itemsList, ItemsActivity context, boolean isLoading, IItemInterface iItemInterface, int accountId) {
        this.itemsList = itemsList;
        this.context = context;
        this.isLoading = isLoading;
        this.iItemInterface = iItemInterface;
        db = new DatabaseHandler(context);
        this.accountId = accountId;
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

//            if (catalogItem.getItems().getStatus().equalsIgnoreCase("ACTIVE")) {
//
//                viewHolder.llLoader.setVisibility(View.GONE);
//                viewHolder.cvCard.setClickable(true);
//                viewHolder.flAdd.setClickable(true);
//            } else {
//
//                viewHolder.llLoader.setVisibility(View.VISIBLE);
//                viewHolder.cvCard.setClickable(false);
//                viewHolder.flAdd.setClickable(false);
//            }

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


            // to set item Image
            if (catalogItem.getItems().getItemImagesList() != null && catalogItem.getItems().getItemImagesList().size() > 0) {

                for (int i = 0; i < catalogItem.getItems().getItemImagesList().size(); i++) {

                    if (catalogItem.getItems().getItemImagesList().get(i).isDisplayImage()) {

                        viewHolder.bIvItemImage.setVisibility(View.GONE);
                        viewHolder.shimmer.setVisibility(View.VISIBLE);
                        PicassoTrustAll.getInstance(context).load(catalogItem.getItems().getItemImagesList().get(i).getUrl()).into(viewHolder.bIvItemImage, new Callback() {
                            @Override
                            public void onSuccess() {

                                viewHolder.shimmer.setVisibility(View.GONE);
                                viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {

                                viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
                                viewHolder.bIvItemImage.setImageResource(R.drawable.icon_noimage);
                            }
                        });

                    }
                }
            }

            // to set itemPrice

            if (catalogItem.getItems().isShowPromotionalPrice()) {

                viewHolder.tvPrice.setVisibility(View.VISIBLE);
                viewHolder.tvDiscountedPrice.setVisibility(View.VISIBLE);
                viewHolder.tvPrice.setText("₹" + catalogItem.getItems().getPrice());
                viewHolder.tvPrice.setPaintFlags(viewHolder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                String price = String.valueOf(catalogItem.getItems().getPromotionalPrice());
                viewHolder.tvDiscountedPrice.setText("₹" + price);

            } else {
                viewHolder.tvPrice.setVisibility(View.VISIBLE);
                viewHolder.tvPrice.setText("₹" + catalogItem.getItems().getPrice());
                viewHolder.tvDiscountedPrice.setVisibility(View.GONE);

            }

            viewHolder.flAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (db.getAccountId() == accountId) {

                        viewHolder.flAdd.setVisibility(View.GONE);
                        viewHolder.numberButton.setVisibility(View.VISIBLE);
                        viewHolder.numberButton.setNumber("1");

                        CartItemModel item = new CartItemModel();
                        item.setItemId(catalogItem.getItems().getItemId());
                        item.setAccountId(accountId);
                        item.setCatalogId(catalogItem.getCatalogId());
                        item.setItemName(catalogItem.getItems().getItemName());
                        item.setImageUrl(catalogItem.getItems().getDisplayImage());
                        item.setItemPrice(catalogItem.getItems().getPrice());
                        item.setMaxQuantity(catalogItem.getMaxQuantity());
                        item.setQuantity(1);
                        item.setPromotionalType(catalogItem.getItems().getPromotionalPriceType());
                        item.setDiscount(catalogItem.getItems().getPromotionalPrice());
                        item.setDiscountedPrice(catalogItem.getItems().getDiscountedPrice());
                        if (catalogItem.getItems().isShowPromotionalPrice()) {
                            item.setIsPromotional(1);
                        } else {
                            item.setIsPromotional(0);
                        }

                        db.insertItemToCart(item);

                        iItemInterface.checkItemQuantity();
                    } else {

                        Toast.makeText(context, "Clear cart to add new items", Toast.LENGTH_SHORT).show();
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

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms

                                db.addQuantity(catalogItem.getItems().getItemId(), newValue);
                                iItemInterface.checkItemQuantity();

                                progressBar.setVisibility(View.GONE);

                            }
                        }, 500);
                        // plus and minus icons are disabled based on minQuantity and new value

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


        } else {

            ItemsAdapter.ViewHolder skeletonViewHolder = (ItemsAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : itemsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextViewBold tvItemName;
        private CustomTextViewSemiBold tvPrice, tvDiscountedPrice;
        private BorderImageView bIvItemImage;
        private ElegantNumberButton numberButton;
        private CardView cvCard;
        private FrameLayout flAdd;
        private SkeletonLoadingView shimmer;
        private LinearLayout llLoader;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {

                cvCard = itemView.findViewById(R.id.cv_card);
                bIvItemImage = itemView.findViewById(R.id.iv_itemImage);
                tvItemName = itemView.findViewById(R.id.tv_itemName);
                tvPrice = itemView.findViewById(R.id.tv_price);
                numberButton = itemView.findViewById(R.id.number_button);
                flAdd = itemView.findViewById(R.id.fl_add);
                tvDiscountedPrice = itemView.findViewById(R.id.tv_discountedPrice);
                shimmer = itemView.findViewById(R.id.shimmer);
                llLoader = itemView.findViewById(R.id.ll_loader);

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
