package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.widgets.TouchImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CustomPageAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<String> mGalleryImage;

    public CustomPageAdapter(Context context, ArrayList<String> GalleryImage) {
        mContext = context;
        mGalleryImage = GalleryImage;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        String url = mGalleryImage.get(position);
        //String url=gallery.getUrl();


        if (url.contains(" ")) {
            url = url.replaceAll(" ", "%20");
        }

        final String finalUrl = url;

        Picasso.with(mContext).load(url).fit().centerInside().into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Picasso.with(mContext).load(finalUrl).placeholder(R.drawable.icon_noimage).into(imageView);
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
