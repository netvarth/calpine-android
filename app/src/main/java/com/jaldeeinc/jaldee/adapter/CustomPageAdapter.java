package com.jaldeeinc.jaldee.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.widgets.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


public class CustomPageAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<String> mGalleryImage;
    String from = "";


    public CustomPageAdapter(Context context, ArrayList<String> GalleryImage, String fromSection) {
        mContext = context;
        mGalleryImage = GalleryImage;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        from = fromSection;
    }

    @Override
    public int getCount() {
        return mGalleryImage.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        final TouchImageView imageView = (TouchImageView) itemView.findViewById(R.id.imageView);
        CardView cv_download = itemView.findViewById(R.id.download);

        if (from != null && from.equalsIgnoreCase(Constants.CHAT)) {
            cv_download.setVisibility(View.VISIBLE);
        } else {
            cv_download.setVisibility(View.GONE);
        }


        String url = mGalleryImage.get(position);
        //String url=gallery.getUrl();


        if (url.contains(" ")) {
            url = url.replaceAll(" ", "%20");
        }

        final String finalUrl = url;

        PicassoTrustAll.getInstance(mContext).load(url).fit().centerInside().into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                PicassoTrustAll.getInstance(mContext).load(finalUrl).placeholder(R.drawable.icon_noimage).into(imageView);

            }

        });

        cv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    // this will request for permission when permission is not true
                } else {
                    // Download code here
                    String url = mGalleryImage.get(position);
                    File file = new File(Uri.parse(url).toString());
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setDescription(file.getName());
                    request.setTitle(file.getName());
                    // request.setMimeType(".jpg");
// in order for this if to run, you must use the android 3.2 to compile your app
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url);

// get download service and enqueue file
                    DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    if (manager != null) {
                        manager.enqueue(request);
                    }

                }
            }
        });

        container.addView(itemView);

        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
