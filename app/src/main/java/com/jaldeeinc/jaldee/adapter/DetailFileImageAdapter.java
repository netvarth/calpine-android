package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.net.Uri;

import androidx.core.util.AtomicFile;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.R;

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

            Uri imgUri = Uri.parse(this.imagePathList.get(position));
            myViewHolder.iv_file_attach.setImageURI(null);
            myViewHolder.iv_file_attach.setImageURI(imgUri);


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

