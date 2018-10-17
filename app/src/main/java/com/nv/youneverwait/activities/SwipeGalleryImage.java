package com.nv.youneverwait.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.adapter.CustomPageAdapter;

import java.util.ArrayList;

/**
 * Created by sharmila on 31/7/18.
 */

public class SwipeGalleryImage extends AppCompatActivity {


    public static Activity mActivity;
    private Toolbar mToolbar;
    static ViewPager mViewPager;
    static ArrayList<String> mGalleryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_downloadswipe_image);
        mActivity = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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

        final TextView title = (TextView) findViewById(android.R.id.text1);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

         mViewPager = (ViewPager) findViewById(R.id.pager);


        Bundle extras = getIntent().getExtras();


            CustomPageAdapter mCustomPagerAdapter = new CustomPageAdapter(this, mGalleryList);
            mViewPager.setAdapter(mCustomPagerAdapter);
            mViewPager.setCurrentItem(0);


        //title.setText(myGalleryNameList.get(intValue));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                // title.setText(myDocNameList.get(position));
            }

            @Override
            public void onPageSelected(int position) {


                // title.setText(myGalleryNameList.get(position));

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
