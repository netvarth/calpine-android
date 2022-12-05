package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;

import java.util.ArrayList;

public class CheckoutItemsAdapter extends RecyclerView.Adapter<CheckoutItemsAdapter.ViewHolder> {

    ArrayList<CartItemModel> cartItemsList = new ArrayList<>();
    public Context context;
    private boolean isLoading = true;
    private int lastPosition = -1;
    private int selectedPosition = 0;
    private DatabaseHandler db;

    public CheckoutItemsAdapter(ArrayList<CartItemModel> cartItemsList, Context context, boolean isLoading) {
        this.cartItemsList = cartItemsList;
        this.context = context;
        this.isLoading = isLoading;
        db = new DatabaseHandler(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_line_shimmer, parent, false);
            return new ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
            return new ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if (!isLoading) {
            final CartItemModel cartItem = cartItemsList.get(position);

            setAnimation(viewHolder.llLayout, position);

            viewHolder.tvItemName.setText(cartItem.getItemName());

            //double amount1 = cartItem.getDiscountedPrice() * cartItem.getQuantity();
            double amount = db.getItemDiscountedPrice(cartItem.getItemId());
            viewHolder.tvPrice.setText("₹ "+ Config.getAmountNoOrTwoDecimalPoints(amount));

            viewHolder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        } else {

            ViewHolder skeletonViewHolder = (ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : cartItemsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CustomTextViewSemiBold tvItemName;
        private CustomTextViewMedium tvPrice,tvQuantity;
        private LinearLayout llLayout;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {
                tvItemName = itemView.findViewById(R.id.tv_itemName);
                tvPrice = itemView.findViewById(R.id.tv_price);
                tvQuantity = itemView.findViewById(R.id.tv_qauntity);
                llLayout = itemView.findViewById(R.id.ll_layout);
            }
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}