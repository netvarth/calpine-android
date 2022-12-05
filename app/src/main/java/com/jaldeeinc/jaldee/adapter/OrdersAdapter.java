package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewBoldItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.ViewNotesDialog;
import com.jaldeeinc.jaldee.response.ItemDetails;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    ArrayList<ItemDetails> itemsList;
    private boolean isLoading = true;
    public Context context;
    private int lastPosition = -1;
    private ViewNotesDialog viewNotesDialog;


    public OrdersAdapter(ArrayList<ItemDetails> itemsList, Context context, boolean isLoading) {
        this.context = context;
        this.itemsList = itemsList;
        this.isLoading = isLoading;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (isLoading) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shimmer, viewGroup, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item, viewGroup, false);
            return new ViewHolder(v, false);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        if (!isLoading) {
            final ItemDetails orderItem = itemsList.get(position);

            setAnimation(viewHolder.cvCard, position);

            viewHolder.tvItemName.setText(orderItem.getItemName());

            viewHolder.tvItemQuantity.setText(String.valueOf(orderItem.getQuantity()));

            viewHolder.tvItemPrice.setText("₹ " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(orderItem.getTotalPrice())));

            if (orderItem.getConsumerNotes() != null && !orderItem.getConsumerNotes().trim().equalsIgnoreCase("")) {

                viewHolder.tvNotes.setVisibility(View.VISIBLE);
                viewHolder.tvNotes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showViewNotesDialog(orderItem.getConsumerNotes(), orderItem.getItemName());

                    }
                });
            } else {

                viewHolder.tvNotes.setVisibility(View.GONE);
            }


            if (orderItem.getItemImagesList() != null && orderItem.getItemImagesList().size() > 0) {

                for (int i = 0; i < orderItem.getItemImagesList().size(); i++) {

                    if (orderItem.getItemImagesList().get(i).isDisplayImage()) {

                        viewHolder.bvImage.setVisibility(View.GONE);
                        viewHolder.shimmer.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(orderItem.getItemImagesList().get(i).getUrl())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        //on load failed
                                        viewHolder.bvImage.setVisibility(View.VISIBLE);
                                        Glide.with(context).load(R.drawable.icon_noimage).into(viewHolder.bvImage);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        //on load success

                                        viewHolder.shimmer.setVisibility(View.GONE);
                                        viewHolder.bvImage.setVisibility(View.VISIBLE);
                                        return false;
                                    }
                                })
                                .into(viewHolder.bvImage);
                        /*PicassoTrustAll.getInstance(context).load(orderItem.getItemImagesList().get(i).getUrl()).into(viewHolder.bvImage, new Callback() {
                            @Override
                            public void onSuccess() {

                                viewHolder.shimmer.setVisibility(View.GONE);
                                viewHolder.bvImage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                viewHolder.bvImage.setVisibility(View.VISIBLE);
                                Glide.with(context).load(R.drawable.icon_noimage).into(viewHolder.bvImage);
                            }
                        });*/
                    }

                }
            } else {
                Glide.with(context)
                        .load(R.drawable.ic_item)
                        .fitCenter()
                        .into(viewHolder.bvImage);
            }


        } else {
            ViewHolder skeletonViewHolder = (ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);
        }
    }

    private void showViewNotesDialog(String consumerNotes, String itemName) {

        viewNotesDialog = new ViewNotesDialog(context, consumerNotes, itemName);
        viewNotesDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        viewNotesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        viewNotesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        viewNotesDialog.show();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        viewNotesDialog.getWindow().setGravity(Gravity.BOTTOM);
        viewNotesDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public int getItemCount() {

        return isLoading ? 10 : itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private BorderImageView bvImage;
        private CustomTextViewBold tvItemName, tvItemQuantity;
        private CardView cvCard;
        private SkeletonLoadingView shimmer;
        private CustomTextViewBoldItalic tvNotes;
        private CustomTextViewSemiBold tvItemPrice;


        public ViewHolder(@NonNull View itemView, boolean isLoading) {
            super(itemView);

            if (!isLoading) {

                cvCard = itemView.findViewById(R.id.cv_card);
                tvItemName = itemView.findViewById(R.id.tv_itemName);
                bvImage = itemView.findViewById(R.id.iv_itemImage);
                shimmer = itemView.findViewById(R.id.shimmer);
                tvItemQuantity = itemView.findViewById(R.id.tv_quantity);
                tvItemPrice = itemView.findViewById(R.id.tv_itemPrice);
                tvNotes = itemView.findViewById(R.id.tv_notes);

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
}