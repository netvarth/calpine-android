package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.model.FileAttachment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    List<FileAttachment> inboxList;
    Context mContext;
    ArrayList<String> mGalleryAttachments = new ArrayList<>();



    public ImageAdapter(List<FileAttachment> inboxList) {
        this.inboxList = inboxList;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.file_attach_detail);
        }
    }

    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imagelayout, parent, false);

        return new ImageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageAdapter.MyViewHolder myViewHolder, final int position) {

        if(inboxList.get(position).getThumbPath()!= null){


            Picasso.Builder builder = new Picasso.Builder(myViewHolder.imageView.getContext());
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                }
            });

            if(inboxList.get(position).getS3path()!=null) {
                builder.build().load(inboxList.get(position).getS3path()).fit().into(myViewHolder.imageView);
            }

            mGalleryAttachments.add(inboxList.get(position).getS3path());

            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryAttachments, mContext);
                    if (mValue) {

                        Intent intent = new Intent(myViewHolder.imageView.getContext(), SwipeGalleryImage.class);
                        intent.putExtra("pos", 0);
                        myViewHolder.imageView.getContext()
                                .startActivity(intent);
                    }
                }
            });
//            myViewHolder.imageView.setImageURI(Uri.parse(inboxList.get(position).getS3path()));




//            myViewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(inboxList.get(position).getS3path()));

//        myViewHolder.imageView.setImageURI(Uri.parse(inboxList.get(position).getThumbPath()));

//            myViewHolder.imageView.getLayoutParams().height = 100;
//            myViewHolder.imageView.getLayoutParams().width = 100;
        }

    }
    @Override
    public int getItemCount() {
        return inboxList.size();
    }


}
