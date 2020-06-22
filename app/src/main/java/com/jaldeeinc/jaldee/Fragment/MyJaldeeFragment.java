package com.jaldeeinc.jaldee.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.adapter.InboxAdapter;
import com.jaldeeinc.jaldee.adapter.TabViewPagerAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.database.DatabaseHandler;


/**
 * Created by sharmila on 6/7/18.
 */

public class MyJaldeeFragment extends RootFragment /*implements FragmentInterface*/ {

    Context mContext;
    Activity mActivity;

    private TabLayout tabLayout;
    private ViewPager firstViewPager;


    public MyJaldeeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_myjaldee, container, false);

        firstViewPager = (ViewPager) rootView.findViewById(R.id.viewpager_content);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(firstViewPager);

        setupViewPager(firstViewPager);


        mContext = getActivity();
        TextView tv_title = (TextView) rootView.findViewById(R.id.toolbartitle);

        ImageView iBackPress = (ImageView) rootView.findViewById(R.id.backpress);

        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);


        iBackPress.setVisibility(View.GONE);


        tv_title.setText("My Jaldee");

        Typeface tyface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);

        return rootView;

    }


    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "My Bookings");
        adapter.addFragment(new Tab2Fragment(), "My Payments");
        viewPager.setAdapter(adapter);
    }


}
