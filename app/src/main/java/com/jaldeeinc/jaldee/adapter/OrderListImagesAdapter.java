package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.jaldeeinc.jaldee.response.ShoppingList;
import com.jaldeeinc.jaldee.widgets.TouchImageView;

import java.util.ArrayList;
import java.util.Arrays;

public class OrderListImagesAdapter extends RecyclerView.Adapter<OrderListImagesAdapter.MyViewHolder> {

    public ArrayList<ShoppingList> itemList;
    private CustomNotes customNotes;
    static Context mContext;
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_preview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        String imagePath = itemList.get(position).getS3path();
        MediaTypeAndExtention type;
        type = Config.getFileType(imagePath);
        if (!((Activity) mContext).isFinishing()) {

            myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);

            if (type.getMediaType().equals(Constants.docType)) {

                if (type.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                    Glide.with(mContext).load(itemList.get(position).getThumbPath()).placeholder(R.drawable.icon_noimage).into(myViewHolder.iv_file_attach);
                } else {
                    Glide.with(mContext).load(R.drawable.icon_document).placeholder(R.drawable.icon_noimage).into(myViewHolder.iv_file_attach);
                }

            } else if (type.getMediaType().equals(Constants.audioType)) {
                Glide.with(mContext).load(R.drawable.audio_icon).placeholder(R.drawable.icon_noimage).into(myViewHolder.iv_file_attach);

            } else if (type.getMediaType().equals(Constants.videoType)) {
                Glide.with(mContext).load(R.drawable.video_icon).placeholder(R.drawable.icon_noimage).into(myViewHolder.iv_file_attach);

            } else if (type.getMediaType().equals(Constants.txtType)) {
                Glide.with(mContext).load(R.drawable.icon_text).placeholder(R.drawable.icon_noimage).into(myViewHolder.iv_file_attach);

            } else if (Arrays.asList(Constants.imgExtFormats).contains(itemList.get(position).getType())) {
                Glide.with(mContext).load(itemList.get(position).getS3path()).placeholder(R.drawable.icon_noimage).into(myViewHolder.iv_file_attach);

            } else {
                Glide.with(mContext).load(itemList.get(position).getS3path()).placeholder(R.drawable.icon_noimage).into(myViewHolder.iv_file_attach);

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
                MediaTypeAndExtention type = Config.getFileType(itemList.get(position).getS3path());

                if (type.getMediaType().equals(Constants.docType) || type.getMediaType().equals(Constants.audioType)
                        || type.getMediaType().equals(Constants.videoType) || type.getMediaType().equals(Constants.txtType)) {
                    if (type.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                        Config.openOnlinePdf(mContext, itemList.get(position).getS3path());
                    } else {
                        Config.openOnlineDoc(mContext, itemList.get(position).getS3path());
                    }
                } else {  // else its a image
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
            TextView tvCaption = dialog.findViewById(R.id.tv_caption);
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
}

