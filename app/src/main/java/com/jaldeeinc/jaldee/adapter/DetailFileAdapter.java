package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.R;

import java.net.URI;
import java.util.ArrayList;


public class DetailFileAdapter extends RecyclerView.Adapter<DetailFileAdapter.MyViewHolder> {
    private final ArrayList<String> imagePathList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_file_attach;
        LinearLayout fileList;
        public MyViewHolder(View view) {
            super(view);
            iv_file_attach = view.findViewById(R.id.file);
            fileList = view.findViewById(R.id.fileList);

        }
    }


    public DetailFileAdapter(ArrayList<String> mfileList, Context mContext) {
        this.imagePathList = mfileList;
        this.mContext = mContext;
    }

    @Override
    public DetailFileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_attach_list_row, parent, false);

        return new DetailFileAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DetailFileAdapter.MyViewHolder myViewHolder, final int position) {
       // final  fileList = mfileList.get(position);
        Log.i("path", this.imagePathList.get(position));
        myViewHolder.iv_file_attach.setImageURI(Uri.parse(this.imagePathList.get(position)));
    }

    @Override
    public int getItemCount() {
Log.i("count",String.valueOf(imagePathList.size()));
        return imagePathList.size();
    }
}
