package com.jaldeeinc.jaldee.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.CustomPageAdapter;

import java.util.ArrayList;

/**
 * Created by sharmila on 31/7/18.
 */

public class
SwipeGalleryImage extends AppCompatActivity {


    public static Activity mActivity;
    private Toolbar mToolbar;
    static ViewPager mViewPager;
    static ArrayList<String> mGalleryList;

int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_downloadswipe_image);
        mActivity = this;
        mToolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_title);
        final TextView title =  findViewById(android.R.id.text1);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
         mViewPager =  findViewById(R.id.pager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("pos", 0);
        }
            CustomPageAdapter mCustomPagerAdapter = new CustomPageAdapter(this, mGalleryList);
            mViewPager.setAdapter(mCustomPagerAdapter);
            mViewPager.setCurrentItem(position);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public static boolean SetGalleryList(ArrayList<String> myGalleryList,Context mContext){
        mGalleryList=myGalleryList;
        return true;

    }
}
