package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.Item;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private ArrayList<CatalogItem> itemsList;
    public Context context;
    private boolean isLoading = true;
    private int lastPosition = -1;
    private IItemInterface iItemInterface;


    public ItemsAdapter(ArrayList<CatalogItem> itemsList, ItemsActivity context, boolean isLoading, IItemInterface iItemInterface) {
        this.itemsList = itemsList;
        this.context = context;
        this.isLoading = isLoading;
        this.iItemInterface = iItemInterface;
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

            // to set itemName
            if (catalogItem.getItems().getDisplayName() != null) {

                viewHolder.tvItemName.setText(catalogItem.getItems().getDisplayName());
            }


            // to set item Image
            if (catalogItem.getItems().getItemImagesList() != null && catalogItem.getItems().getItemImagesList().size() > 0) {

                for (int i = 0; i < catalogItem.getItems().getItemImagesList().size(); i++) {

                    if (catalogItem.getItems().getItemImagesList().get(i).isDisplayImage()) {

                        PicassoTrustAll.getInstance(context).load(catalogItem.getItems().getItemImagesList().get(i).getUrl()).into(viewHolder.bIvItemImage);

                    }
                }
            }

            // to set itemPrice
            if (catalogItem.getItems().getPrice() != null){

                viewHolder.tvPrice.setText("₹"+catalogItem.getItems().getPrice());
            }

            viewHolder.rlAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    viewHolder.rlAdd.setVisibility(View.GONE);
                    viewHolder.numberButton.setVisibility(View.VISIBLE);
                }
            });

            viewHolder.cvCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    iItemInterface.onItemClick(catalogItem);
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
        private CustomTextViewSemiBold tvPrice;
        private BorderImageView bIvItemImage;
        private ElegantNumberButton numberButton;
        private CardView cvCard;
        private RelativeLayout rlAdd;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {

                cvCard = itemView.findViewById(R.id.cv_card);
                bIvItemImage = itemView.findViewById(R.id.iv_itemImage);
                tvItemName = itemView.findViewById(R.id.tv_itemName);
                tvPrice = itemView.findViewById(R.id.tv_price);
                numberButton = itemView.findViewById(R.id.number_button);
                rlAdd = itemView.findViewById(R.id.rl_add);


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
