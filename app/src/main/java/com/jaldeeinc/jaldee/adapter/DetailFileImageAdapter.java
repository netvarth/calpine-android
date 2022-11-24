package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.squareup.picasso.Picasso;

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
            delete_file.setVisibility(View.GONE);

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
        //Log.i("path", this.imagePathList.get(position));
        String imagePath = this.imagePathList.get(position);
        //String ext = imagePath.substring(imagePath.lastIndexOf(".") + 1);
        MediaTypeAndExtention type = Config.getFileType(imagePath);

        if (imagePath != null && !imagePath.isEmpty()) {
            if (type.getMediaType().equals(Constants.docType)) {
                if (type.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                    myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);
                    myViewHolder.delete_file.setVisibility(View.VISIBLE);
                    myViewHolder.iv_file_attach.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pdf));
                } else {
                    myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);
                    myViewHolder.delete_file.setVisibility(View.VISIBLE);
                    myViewHolder.iv_file_attach.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_document));
                }
            } else if (type.getMediaType().equals(Constants.audioType)) {
                myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);
                myViewHolder.delete_file.setVisibility(View.VISIBLE);
                myViewHolder.iv_file_attach.setImageDrawable(mContext.getResources().getDrawable(R.drawable.audio_icon));
            } else if (type.getMediaType().equals(Constants.videoType)) {
                myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);
                myViewHolder.delete_file.setVisibility(View.VISIBLE);
                myViewHolder.iv_file_attach.setImageDrawable(mContext.getResources().getDrawable(R.drawable.video_icon));
            } else if (type.getMediaType().equals(Constants.txtType)) {
                myViewHolder.iv_file_attach.setVisibility(View.VISIBLE);
                myViewHolder.delete_file.setVisibility(View.VISIBLE);
                myViewHolder.iv_file_attach.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_text));
            } else {

            /*Uri imgUri = Uri.parse(imagePathList.get(position));
            myViewHolder.iv_file_attach.setImageURI(imgUri);*/

                Picasso.Builder builder = new Picasso.Builder(myViewHolder.iv_file_attach.getContext());
                builder.listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        //exception.printStackTrace();
                        Uri imgUri = Uri.parse(imagePathList.get(position));
                        myViewHolder.iv_file_attach.setImageURI(imgUri);
                        myViewHolder.delete_file.setVisibility(View.VISIBLE);
                    }
                });
                builder.build().load(imagePathList.get(position)).fit().into(myViewHolder.iv_file_attach);
            }
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

