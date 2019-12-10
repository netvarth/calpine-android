package com.jaldeeinc.jaldee.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.model.FileAttachment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    List<FileAttachment> inboxList;
    Context mContext;
    ArrayList<String> mGalleryAttachments = new ArrayList<>();



    public ImageAdapter(List<FileAttachment> inboxList,Context mContext) {
        this.inboxList = inboxList;
        this.mContext = mContext;
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

        if(inboxList.get(position).getThumbPath()!= null && inboxList.get(position).getS3path().contains("pdf")){

            myViewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pdfs));



        }
         else {

            Picasso.Builder builder = new Picasso.Builder(myViewHolder.imageView.getContext());
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                }
            });

            if (inboxList.get(position).getS3path() != null) {
                builder.build().load(inboxList.get(position).getS3path()).fit().into(myViewHolder.imageView);


            }
        }
//            PdfRenderer renderer = new PdfRenderer(myViewHolder.imageView.getContext());
//            renderer.Page(inboxList.get(position).getS3path()).fit().into(myViewHolder.imageView);

            mGalleryAttachments.add(inboxList.get(position).getS3path());

            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryAttachments, mContext);
//                    if (mValue) {
//
//                        Intent intent = new Intent(myViewHolder.imageView.getContext(), SwipeGalleryImage.class);
//                        intent.putExtra("pos", 0);
//                        myViewHolder.imageView.getContext()
//                                .startActivity(intent);
//                    }
                    if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions((Activity) v.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        // this will request for permission when permission is not true
                    }else{
                        // Download code here
                        String url = inboxList.get(position).getS3path() ;
                        File file  = new File(Uri.parse(url).toString());
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                        request.setDescription(file.getName());
                        request.setTitle(file.getName());
                       // request.setMimeType(".jpg");
// in order for this if to run, you must use the android 3.2 to compile your app
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        }
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url);

// get download service and enqueue file
                        DownloadManager manager = (DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                    }
                    }




            });
//            myViewHolder.imageView.setImageURI(Uri.parse(inboxList.get(position).getS3path()));




//            myViewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(inboxList.get(position).getS3path()));

//        myViewHolder.imageView.setImageURI(Uri.parse(inboxList.get(position).getThumbPath()));

//            myViewHolder.imageView.getLayoutParams().height = 100;
//            myViewHolder.imageView.getLayoutParams().width = 100;
        }


    @Override
    public int getItemCount() {
        return inboxList.size();
    }

}
