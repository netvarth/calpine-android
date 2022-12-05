package com.jaldeeinc.jaldee.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.jaldeeinc.jaldee.widgets.TouchImageView;

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
        MediaTypeAndExtention type;
        type = Config.getFileType(url);
        if (type.getMediaType().equals(Constants.docType)) {
            if (type.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                Glide.with(mContext).load(R.drawable.pdf).fitCenter().into(imageView);
            } else {
                Glide.with(mContext).load(R.drawable.icon_document).fitCenter().into(imageView);
            }

        } else if (type.getMediaType().equals(Constants.audioType)) {

            Glide.with(mContext).load(R.drawable.audio_icon).fitCenter().into(imageView);

        } else if (type.getMediaType().equals(Constants.videoType)) {

            Glide.with(mContext).load(R.drawable.video_icon).fitCenter().into(imageView);

        } else if (type.getMediaType().equals(Constants.txtType)) {

            Glide.with(mContext).load(R.drawable.icon_text).fitCenter().into(imageView);

        } else {
            Glide.with(mContext)
                    .load(url)
                    .fitCenter()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //on load failed
                            Glide.with(mContext).load(finalUrl).placeholder(R.drawable.icon_noimage).into(imageView);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //on load success

                            return false;
                        }
                    })
                    .into(imageView);
            /*PicassoTrustAll.getInstance(mContext).load(url).fit().centerInside().into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                    PicassoTrustAll.getInstance(mContext).load(finalUrl).placeholder(R.drawable.icon_noimage).into(imageView);

                }

            });*/
        }

        cv_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    // this will request for permission when permission is not true
                } else {
                    // Download code here
                    String url = mGalleryImage.get(position);
                   /* URL url1 = null;
                    try {
                        url1 = new URL(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    String filname = FilenameUtils.getName(url1.getPath());

                    File file = new File(Uri.parse(url).toString());
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setDescription(file.getName());
                    request.setTitle(filname);
                    // request.setMimeType(".jpg");
// in order for this if to run, you must use the android 3.2 to compile your app
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url);

// get download service and enqueue file
                    DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    if (manager != null) {
                        manager.enqueue(request);
                    }*/

                    MediaTypeAndExtention type = Config.getFileType(url);

                    if (type.getMediaType().equals(Constants.docType) || type.getMediaType().equals(Constants.audioType)
                            || type.getMediaType().equals(Constants.videoType) || type.getMediaType().equals(Constants.txtType)
                            || type.getMediaType().equals(Constants.imgType)) {
                        if (type.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                            Config.openOnlinePdf(mContext, url);
                        } else {
                            Config.openOnlineDoc(mContext, url);
                        }
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
