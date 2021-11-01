package com.jaldeeinc.jaldee.Fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.jaldeeinc.jaldee.R;

import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.adapter.ViewPagerAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.response.JCashInfo;
import com.jaldeeinc.jaldee.utils.AppPreferences;
import com.jaldeeinc.jaldee.utils.CustomViewPager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 9/7/18.
 */

public class HomeTabFragment extends Fragment {

    ChipNavigationBar bottomNavigationView;

    //This is our viewPager
    private CustomViewPager viewPager;
    Toolbar toolbar;
    Context mContext;
    String sforceupdate = "";

    //Fragments

    FavouriteFragment favFragment;
    DashboardFragment homeFragment;
    HomeSearchFragment homeSearchFragment;
    // CheckinsFragmentCopy checkinFragment;


    Tab1Fragment tab1Fragment;
    MyJaldee myJaldeeFragment;
    InboxFragment inboxFragment;
    ProfileFragment profileFragment;

    MenuItem prevMenuItem;
    static Fragment hometabFragment;
    String message;
    String tab;
    int myJaldeeTab = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.bottomtab_fragment, container, false);
        viewPager = (CustomViewPager) rootView.findViewById(R.id.viewpager);
        mContext = getActivity();

        Config.logV("Current @@@@@@@@@@@@@@@@11111");

        //Initializing the bottomNavigationView
        bottomNavigationView = (ChipNavigationBar) rootView.findViewById(R.id.bottom_navigation);
        //  BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

//  //      bottomNavigationView.setItemIconTintList(null);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tab = bundle.getString("tab");
            myJaldeeTab = bundle.getInt("myJaldeeTab");

        }
        if (bundle != null) {
            sforceupdate = bundle.getString("forceupdate", "");
        }

        if (bundle != null) {

            String content = bundle.getString("message");
            if (content != null) {
                if (content.length() > 25) {

                    message = content;
                }
            }

        }

        if (sforceupdate != null) {
            if (sforceupdate.equalsIgnoreCase("true")) {

                showForceUpdateDialog();
            }
        }

        bottomNavigationView.setItemSelected(R.id.action_home, true);

        ApiGetUnreadMessagesCount();


        return rootView;
    }

    private void ApiGetUnreadMessagesCount() {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ResponseBody> call = apiService.getUnreadMessagesCount();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.code() == 200) {

                        if (response.body() != null) {
                            String count = response.body().string();

                            bottomNavigationView.showBadge(R.id.action_inbox,Integer.parseInt(count));
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initializing viewPager


        bottomNavigationView.setOnItemSelectedListener(
                new ChipNavigationBar.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int i) {
                        ApiGetUnreadMessagesCount();
                        switch (i) {
                            case R.id.action_home:
                                viewPager.setCurrentItem(0);
//                                item.setIcon(getResources().getDrawable(R.drawable.selected_home));
                                Config.logV("Page SLECTED&&&&&&&&&&&&&&&&&&&&&&");
                                break;
                            case R.id.action_checkin:
                                viewPager.setCurrentItem(1);
//                                item.setIcon(getResources().getDrawable(R.drawable.selected_checkin));
                                break;
                            case R.id.action_fav:
                                viewPager.setCurrentItem(2);
//                                item.setIcon(getResources().getDrawable(R.drawable.selected_fav));
                                break;
                            case R.id.action_inbox:
                                viewPager.setCurrentItem(3);
//                                item.setIcon(getResources().getDrawable(R.drawable.select_inbox));
                                break;
                            case R.id.action_profile:
                                viewPager.setCurrentItem(4);
//                                item.setIcon(getResources().getDrawable(R.drawable.select_profile));
                                break;
                        }
                    }

                });


//        viewPager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (prevMenuItem != null) {
//                    prevMenuItem.setChecked(false);
//                } else {
//                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
//                }
//                Log.d("page", "onPageSelected: " + position);
//
//
//                bottomNavigationView.getMenu().getItem(position).setChecked(true);
//                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
//
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });


        //Disable ViewPager Swipe

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        setupViewPager(viewPager);
    }


    ViewPagerAdapter adapter;

    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        homeFragment = new DashboardFragment();
        homeSearchFragment = new HomeSearchFragment();


        tab1Fragment = new Tab1Fragment();
        myJaldeeFragment = new MyJaldee();
        Bundle bundle = new Bundle();
        if (myJaldeeTab == 2 || myJaldeeTab == 1) {
            bundle.putInt("myJaldeeTab", myJaldeeTab);
        }
        if (message != null && !message.equalsIgnoreCase("")) {
            bundle.putString("message", message);
        }
        tab1Fragment.setArguments(bundle);
        myJaldeeFragment.setArguments(bundle);
        // checkinFragment = new CheckinsFragmentCopy();
        favFragment = new FavouriteFragment();
        inboxFragment = new InboxFragment();
        profileFragment = new ProfileFragment();

        adapter.addFragment(homeSearchFragment);
        adapter.addFragment(myJaldeeFragment);
        adapter.addFragment(favFragment);
        adapter.addFragment(inboxFragment);
        adapter.addFragment(profileFragment);

        // int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        // Config.logV("Limit------------@@@@@@@@@@@@@@@@@@@@---------"+limit);
        Config.logV("Current @@@@@@@@@@@@@@@@");
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);

        if (tab != null) {
            Config.logV("Tab@@@@@@@@@@@@@@@@@@@@@@@@@@@" + tab);
            if (tab.equalsIgnoreCase("1")) {
                viewPager.setCurrentItem(1);
                bottomNavigationView.setItemSelected(R.id.action_checkin, true);
            }
        }


    }

    public boolean onBackPressed() {
        // currently visible tab Fragment

        try {
            OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(viewPager.getCurrentItem());

            if (currentFragment != null) {
                // lets see if the currentFragment or any of its childFragment can handle onBackPressed

                return currentFragment.onBackPressed();
            } else {
                Intent intent = new Intent(mContext, Home.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }

    public void showForceUpdateDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Jaldee update required ");
        alertDialog.setMessage(" This version of Jaldee is no longer supported. Please update to the latest version.");
        alertDialog.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = mContext.getPackageName();
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        alertDialog.show();
    }

}
