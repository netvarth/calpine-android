package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.core.util.AtomicFile;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class DetailFileImageAdapter extends RecyclerView.Adapter<DetailFileImageAdapter.MyViewHolder> {
    private final ArrayList<String> imagePathList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_file_attach;
        LinearLayout fileList;
        ImageView delete_file;

        public MyViewHolder(View view) {
            super(view);
            iv_file_attach = view.findViewById(R.id.file);
            fileList = view.findViewById(R.id.fileList);
            delete_file = view.findViewById(R.id.deletefile);

        }
    }


    public DetailFileImageAdapter(ArrayList<String> mfileList, Context mContext) {
        this.imagePathList = mfileList;
        this.mContext = mContext;
    }

    @Override
    public DetailFileImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_attach_list_row, parent, false);

        return new DetailFileImageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DetailFileImageAdapter.MyViewHolder myViewHolder, final int position) {
        // final  fileList = mfileList.get(position);
        Log.i("path", this.imagePathList.get(position));
        String imagePath = this.imagePathList.get(position);
        if (imagePath.substring(imagePath.lastIndexOf(".") + 1).equals("pdf")) {
            myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);
        } else {

            Uri imgUri = Uri.parse(imagePathList.get(position));
            myViewHolder.iv_file_attach.setImageURI(imgUri);

//            Glide.with(mContext)
//                    .load(imgUri) // Uri of the picture
//                    .into(myViewHolder.iv_file_attach);

//            String path =  imagePathList.get(position);
//
//            File imgFile = new File(path);
//            if(imgFile.exists())
//            {
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                myViewHolder.iv_file_attach.setImageBitmap(myBitmap);
//            }
//            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(imgUri));
//                myViewHolder.iv_file_attach.setImageBitmap(bitmap);
//                myViewHolder.iv_file_attach.invalidate();
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }


//            myViewHolder.iv_file_attach.setImageURI(imgUri);

//            myViewHolder.iv_file_attach.setImageURI(null);
//            File imgFile = new  File(imagePathList.get(position));

//            if(imgFile.exists()){
//
//                Bitmap myBitmap = BitmapFactory.decodeFile("file:"+imgFile.getAbsolutePath());
//
//                myViewHolder.iv_file_attach.setImageBitmap(myBitmap);
//
//            }
//            myViewHolder.iv_file_attach.setImageURI(Uri.fromFile(imgFile));

//            File imgFile = new  File(imagePathList.get(position));
//
//            if(imgFile.exists()) {
//
////                Bitmap myBitmap = BitmapFactory.decodeFile("file:"+imgFile.getAbsolutePath());
//                String mImagePath = "file:" + imgFile.getAbsolutePath();
//                PicassoTrustAll.getInstance(mContext)
//                        .load(mImagePath)
//                        .centerCrop()
//                        .resize(200, 200)
//                        .into(myViewHolder.iv_file_attach);
//            }

        }

        myViewHolder.delete_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePathList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("count", String.valueOf(imagePathList.size()));
        return imagePathList.size();
    }
}

