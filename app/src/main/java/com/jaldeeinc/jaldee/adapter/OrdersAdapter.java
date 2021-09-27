package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.AddressDialog;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.custom.ViewNotesDialog;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.response.ActiveOrders;
import com.jaldeeinc.jaldee.response.Item;
import com.jaldeeinc.jaldee.response.ItemDetails;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
import com.squareup.picasso.Callback;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (isLoading) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shimmer, viewGroup, false);
            return new OrdersAdapter.ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item, viewGroup, false);
            return new OrdersAdapter.ViewHolder(v, false);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final OrdersAdapter.ViewHolder viewHolder, final int position) {

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
                        PicassoTrustAll.getInstance(context).load(orderItem.getItemImagesList().get(i).getUrl()).into(viewHolder.bvImage, new Callback() {
                            @Override
                            public void onSuccess() {

                                viewHolder.shimmer.setVisibility(View.GONE);
                                viewHolder.bvImage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {

                                viewHolder.bvImage.setVisibility(View.VISIBLE);
                                viewHolder.bvImage.setImageResource(R.drawable.icon_noimage);
                            }
                        });
                    }

                }
            } else {

                viewHolder.bvImage.setImageResource(R.drawable.icon_noimage);
            }


        } else {
            OrdersAdapter.ViewHolder skeletonViewHolder = (OrdersAdapter.ViewHolder) viewHolder;
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
        private CustomTextViewItalicSemiBold tvNotes;
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