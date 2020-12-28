package com.jaldeeinc.jaldee.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.AppointmentActivity;
import com.jaldeeinc.jaldee.activities.HistoryActivity;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.OrdersHistoryActivity;
import com.jaldeeinc.jaldee.adapter.JaldeeTabs;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.NotificationDialog;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.utils.SharedPreference;

public class MyJaldee extends RootFragment {


    TabLayout tabLayout;
    ViewPager viewPager;
    public boolean toHome = false;
    public String message = null;
    NotificationDialog notificationDialog;
    Context mContext;
    Activity mActivity;
    private CustomTextViewMedium tvConsumerName;
    String mFirstName,mLastName;
    private CustomTextViewSemiBold tvHistory, tvOrdersHistory;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Animation slideUp, slideRight;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyJaldee() {
        // Required empty public constructor
    }

    public static MyJaldee newInstance(String param1, String param2) {
        MyJaldee fragment = new MyJaldee();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.my_jaldee, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        Home.doubleBackToExitPressedOnce = false;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            toHome = bundle.getBoolean("toHome");
            message = bundle.getString("message");
        }

        tabLayout=(TabLayout)view.findViewById(R.id.tabLayout);
        viewPager=(ViewPager)view.findViewById(R.id.viewPager);
        tvConsumerName = view.findViewById(R.id.tv_consumerName);
        slideUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_in);
        slideRight = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_out);
        tvHistory = view.findViewById(R.id.tv_history);
        tvOrdersHistory = view.findViewById(R.id.tv_Orderhistory);

        if (message != null) {

            notificationDialog = new NotificationDialog(mContext, message);
            notificationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            notificationDialog.show();
            notificationDialog.setCancelable(false);
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 1);
            notificationDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (bundle != null) {
                bundle.remove("message");
            }
        }

        mFirstName = SharedPreference.getInstance(mContext).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(mContext).getStringValue("lastname", "");
        String name = mFirstName + " "+ mLastName+"!";

        if (name.trim().length() < 18){
            tvConsumerName.setTextSize(30);
        } else {
            tvConsumerName.setTextSize(25);
        }
        tvConsumerName.setText(name);

        tvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent historyIntent = new Intent(getContext(), HistoryActivity.class);
                startActivity(historyIntent);
            }
        });

        tvOrdersHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ordersHistoryIntent = new Intent(getContext(), OrdersHistoryActivity.class);
                startActivity(ordersHistoryIntent);
            }
        });

        tabLayout.addTab(tabLayout.newTab().setText("My Bookings"));
        tabLayout.addTab(tabLayout.newTab().setText("My Payments"));
        tabLayout.addTab(tabLayout.newTab().setText("My Orders"));
        tabLayout.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tabLayout.setTextDirection(View.TEXT_ALIGNMENT_TEXT_START);
        final JaldeeTabs adapter = new JaldeeTabs(getContext(),getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return view;
    }

}