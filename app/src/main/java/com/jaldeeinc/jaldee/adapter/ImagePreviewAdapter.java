package com.jaldeeinc.jaldee.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.Interface.IDeleteImagesInterface;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.widgets.TouchImageView;

import java.util.ArrayList;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.MyViewHolder> {

    ArrayList<ShoppingListModel> itemList;
    Context mContext;
    private boolean isEdit = false;
    private IDeleteImagesInterface iDeleteImagesInterface;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_file_attach;
        LinearLayout fileList;
        ImageView delete_file,ivAddNote;

        public MyViewHolder(View view) {
            super(view);
            iv_file_attach = view.findViewById(R.id.file);
            fileList = view.findViewById(R.id.fileList);
            delete_file = view.findViewById(R.id.deletefile);
            ivAddNote = view.findViewById(R.id.iv_addNote);

        }
    }

    public ImagePreviewAdapter(ArrayList<ShoppingListModel> itemList, Context mContext, boolean isEdit,IDeleteImagesInterface iDeleteImagesInterface) {
        this.itemList = itemList;
        this.mContext = mContext;
        this.isEdit = isEdit;
        this.iDeleteImagesInterface = iDeleteImagesInterface;
    }

    @Override
    public ImagePreviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_preview, parent, false);

        return new ImagePreviewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImagePreviewAdapter.MyViewHolder myViewHolder, final int position) {
        // final  fileList = mfileList.get(position);
        Log.i("path", itemList.get(position).getImagePath());
        String imagePath = itemList.get(position).getImagePath();
        if (imagePath.substring(imagePath.lastIndexOf(".") + 1).equals("pdf")) {
            myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);
        } else {

            Uri imgUri = Uri.parse(itemList.get(position).getImagePath());
            myViewHolder.iv_file_attach.setImageURI(imgUri);

        }

        if (!isEdit){
            myViewHolder.ivAddNote.setVisibility(View.GONE);
            myViewHolder.delete_file.setVisibility(View.GONE);
        } else {
            myViewHolder.ivAddNote.setVisibility(View.VISIBLE);
            myViewHolder.delete_file.setVisibility(View.VISIBLE);
        }

        myViewHolder.delete_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iDeleteImagesInterface.delete(position,itemList.get(position).getImagePath());
            }
        });

        myViewHolder.ivAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iDeleteImagesInterface.addedNotes(position);

            }
        });

        myViewHolder.iv_file_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFullImage(position);

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

            if (itemList.get(position).getCaption() != null && !itemList.get(position).getCaption().trim().equalsIgnoreCase("")){

                tvCaption.setVisibility(View.VISIBLE);
            } else {

                tvCaption.setVisibility(View.GONE);
            }

            tvCaption.setText("");
            tvCaption.setText(itemList.get(position).getCaption());

            Uri imgUri = Uri.parse(itemList.get(position).getImagePath());
            tImage.setImageURI(imgUri);


            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}

