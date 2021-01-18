package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jaldeeinc.jaldee.Interface.IImageInterface;
import com.jaldeeinc.jaldee.Interface.IItemInterface;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.ItemDetailAcitvity;
import com.jaldeeinc.jaldee.activities.ItemsActivity;
import com.jaldeeinc.jaldee.custom.BorderImageView;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.ElegantNumberButton;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.CartItemModel;
import com.jaldeeinc.jaldee.response.CatalogItem;
import com.jaldeeinc.jaldee.response.ItemImages;
import com.omjoonkim.skeletonloadingview.SkeletonLoadingView;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

public class ItemImagesAdapter extends RecyclerView.Adapter<ItemImagesAdapter.ViewHolder> {

    private ArrayList<ItemImages> imagesList;
    public Context context;
    private boolean isLoading = true;
    private int lastPosition = -1;
    private int selectedPosition = 0;
    private IImageInterface iImageInterface;

    public ItemImagesAdapter(ArrayList<ItemImages> imagesList, Context context, boolean isLoading, IImageInterface iImageInterface) {
        this.imagesList = imagesList;
        this.context = context;
        this.isLoading = isLoading;
        this.iImageInterface = iImageInterface;
    }

    @NonNull
    @Override
    public ItemImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isLoading) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_shimmer, parent, false);
            return new ItemImagesAdapter.ViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_thumbnail, parent, false);
            return new ItemImagesAdapter.ViewHolder(v, false);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ItemImagesAdapter.ViewHolder viewHolder, int position) {

        if (!isLoading) {
            final ItemImages itemImage = imagesList.get(position);

            if (position == selectedPosition) {
                viewHolder.llLoader.setVisibility(View.GONE);
                viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
            } else {
                viewHolder.llLoader.setVisibility(View.VISIBLE);
                viewHolder.bIvItemImage.setVisibility(View.VISIBLE);
            }
            setAnimation(viewHolder.cvCard, position);

            if (itemImage.getThumbUrl() != null) {
                if (!((Activity) context).isFinishing()) {

                    viewHolder.shimmer.setVisibility(View.VISIBLE);
                    viewHolder.bIvItemImage.setVisibility(View.VISIBLE);

                    Glide.with(context)
                            .load(itemImage.getThumbUrl())
                            .apply(new RequestOptions().error(R.drawable.icon_noimage).centerCrop())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    //on load failed
                                    viewHolder.shimmer.setVisibility(View.GONE);
                                    viewHolder.bIvItemImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_noimage));
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    //on load success
                                    viewHolder.shimmer.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(viewHolder.bIvItemImage);
                }

//                PicassoTrustAll.getInstance(context).load(itemImage.getThumbUrl()).into(viewHolder.bIvItemImage, new Callback() {
//                    @Override
//                    public void onSuccess() {
//
//                        viewHolder.shimmer.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                        viewHolder.shimmer.setVisibility(View.GONE);
//                        viewHolder.bIvItemImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_noimage));
//                    }
//                });
            } else {
                viewHolder.shimmer.setVisibility(View.GONE);
                viewHolder.bIvItemImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_noimage));
            }

            viewHolder.cvCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentPosition = viewHolder.getLayoutPosition();
                    if (selectedPosition != currentPosition) {
                        // Temporarily save the last selected position
                        int lastSelectedPosition = selectedPosition;
                        // Save the new selected position
                        selectedPosition = currentPosition;
                        // update the previous selected row
                        notifyItemChanged(currentPosition);
                        notifyItemChanged(lastSelectedPosition);
                        // select the clicked row
                        if (itemImage.getUrl() != null) {
                            viewHolder.llLoader.setVisibility(View.GONE);
                            iImageInterface.onImageClick(itemImage.getUrl());
                        }
                    }


                }
            });


        } else {

            ItemImagesAdapter.ViewHolder skeletonViewHolder = (ItemImagesAdapter.ViewHolder) viewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);

        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 10 : imagesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llLoader;
        private BorderImageView bIvItemImage;
        private CardView cvCard;
        private SkeletonLoadingView shimmer;

        public ViewHolder(@NonNull View itemView, boolean isLoading) {

            super(itemView);

            if (!isLoading) {
                cvCard = itemView.findViewById(R.id.cv_card);
                bIvItemImage = itemView.findViewById(R.id.iv_image);
                llLoader = itemView.findViewById(R.id.ll_loader);
                shimmer = itemView.findViewById(R.id.shimmer);
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

    @Override
    public void onViewRecycled(@NonNull ItemImagesAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.bIvItemImage);
    }
}
