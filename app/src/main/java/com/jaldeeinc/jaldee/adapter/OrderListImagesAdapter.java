package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.ShoppingList;
import com.jaldeeinc.jaldee.widgets.TouchImageView;

import java.util.ArrayList;

public class OrderListImagesAdapter extends RecyclerView.Adapter<OrderListImagesAdapter.MyViewHolder> {

    public ArrayList<ShoppingList> itemList;
    private CustomNotes customNotes;
    Context mContext;
    private boolean isEdit = false;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_file_attach;
        LinearLayout fileList;
        ImageView delete_file, ivAddNote;

        public MyViewHolder(View view) {
            super(view);
            iv_file_attach = view.findViewById(R.id.file);
            fileList = view.findViewById(R.id.fileList);
            delete_file = view.findViewById(R.id.deletefile);
            ivAddNote = view.findViewById(R.id.iv_addNote);

        }
    }

    public OrderListImagesAdapter(ArrayList<ShoppingList> itemList, Context mContext, boolean isEdit) {
        this.itemList = itemList;
        this.mContext = mContext;
        this.isEdit = false;
    }

    @Override
    public OrderListImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_preview, parent, false);

        return new OrderListImagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrderListImagesAdapter.MyViewHolder myViewHolder, final int position) {
        String imagePath = itemList.get(position).getS3path();
        if (imagePath != null && imagePath.substring(imagePath.lastIndexOf(".") + 1).equals("pdf")) {
            myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);

            if (itemList.get(position).getType() != null && (itemList.get(position).getType().equalsIgnoreCase("application/pdf") || itemList.get(position).getType().equalsIgnoreCase("pdf"))) {

                if (!((Activity) mContext).isFinishing()) {

                    Glide.with(mContext)
                            .load(itemList.get(position).getThumbPath())
                            .apply(new RequestOptions().error(R.drawable.icon_noimage))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    //on load failed
                                    myViewHolder.iv_file_attach.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    //on load success
                                    return false;
                                }
                            })
                            .into(myViewHolder.iv_file_attach);

                }

            } else {

                if (!((Activity) mContext).isFinishing()) {

                    Glide.with(mContext)
                            .load(itemList.get(position).getS3path())
                            .apply(new RequestOptions().error(R.drawable.icon_noimage))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    //on load failed
                                    myViewHolder.iv_file_attach.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    //on load success
                                    return false;
                                }
                            })
                            .into(myViewHolder.iv_file_attach);

                }

            }

        }

        if (!isEdit) {
            myViewHolder.ivAddNote.setVisibility(View.GONE);
            myViewHolder.delete_file.setVisibility(View.GONE);
        } else {
            myViewHolder.ivAddNote.setVisibility(View.VISIBLE);
            myViewHolder.delete_file.setVisibility(View.VISIBLE);
        }


        myViewHolder.iv_file_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemList.get(position).getType().equalsIgnoreCase("application/pdf") || itemList.get(position).getType().equalsIgnoreCase("pdf")) {

                    openOnlinePdf(mContext, itemList.get(position).getS3path());

                } else {

                    showFullImage(position);
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        Log.i("count", String.valueOf(itemList.size()));
        return itemList.size();
    }

    private void showFullImage(int position) {

        try {

            Dialog dialog = new Dialog(mContext);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.shoppinglist_imagepreview);
            dialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            TouchImageView tImage = dialog.findViewById(R.id.iv_image);
            CustomTextViewMedium tvCaption = dialog.findViewById(R.id.tv_caption);
            ImageView ivClose = dialog.findViewById(R.id.iv_close);
            tvCaption.setMovementMethod(new ScrollingMovementMethod());


            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            if (itemList.get(position).getCaption() != null && !itemList.get(position).getCaption().trim().equalsIgnoreCase("")) {

                tvCaption.setVisibility(View.VISIBLE);
            } else {

                tvCaption.setVisibility(View.GONE);
            }

            tvCaption.setText("");
            tvCaption.setText(itemList.get(position).getCaption());

            if (!((Activity) mContext).isFinishing()) {

                Glide.with(mContext)
                        .load(itemList.get(position).getS3path())
                        .apply(new RequestOptions().error(R.drawable.icon_noimage))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //on load failed
                                tImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_noimage));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //on load success
                                return false;
                            }
                        })
                        .into(tImage);

            }

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void openOnlinePdf(Context mContext, String filePath) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(filePath));
        mContext.startActivity(browserIntent);
    }

}

