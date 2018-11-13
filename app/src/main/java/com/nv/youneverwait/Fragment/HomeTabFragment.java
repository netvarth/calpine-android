package com.nv.youneverwait.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nv.youneverwait.R;

import com.nv.youneverwait.activities.Home;
import com.nv.youneverwait.adapter.ViewPagerAdapter;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.utils.BottomNavigationViewHelper;
import com.nv.youneverwait.utils.CustomViewPager;

/**
 * Created by sharmila on 9/7/18.
 */

public class HomeTabFragment extends Fragment {

    BottomNavigationView bottomNavigationView;

    //This is our viewPager
    private CustomViewPager viewPager;
    Toolbar toolbar;
    Context mContext;

    //Fragments

    FavouriteFragment favFragment;
    DashboardFragment homeFragment;
    CheckinsFragment checkinFragment;
    InboxFragment inboxFragment;
    ProfileFragment profileFragment;

    MenuItem prevMenuItem;
    static Fragment hometabFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.bottomtab_fragment, container, false);
        viewPager = (CustomViewPager) rootView.findViewById(R.id.viewpager);


        //Initializing the bottomNavigationView
        bottomNavigationView = (BottomNavigationView) rootView.findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Initializing viewPager


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                viewPager.setCurrentItem(0);
                                item.setIcon(getResources().getDrawable(R.drawable.selected_home));
                                Config.logV("Page SLECTED&&&&&&&&&&&&&&&&&&&&&&");


                                break;
                            case R.id.action_checkin:
                                viewPager.setCurrentItem(1);
                                item.setIcon(getResources().getDrawable(R.drawable.selected_checkin));
                                break;
                            case R.id.action_fav:
                                viewPager.setCurrentItem(2);
                                item.setIcon(getResources().getDrawable(R.drawable.selected_fav));
                                break;
                            case R.id.action_inbox:
                                viewPager.setCurrentItem(3);
                                item.setIcon(getResources().getDrawable(R.drawable.select_inbox));
                                break;
                            case R.id.action_profile:
                                viewPager.setCurrentItem(4);
                                item.setIcon(getResources().getDrawable(R.drawable.select_profile));
                                break;
                        }
                        return false;
                    }
                });

        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
               *//* FragmentInterface fragment = (FragmentInterface) adapter.instantiateItem(viewPager, position);
                if (fragment != null) {
                    fragment.fragmentBecameVisible();
                }*//*

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/

        viewPager.setOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);


                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



       /*  //Disable ViewPager Swipe

       viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        */

        setupViewPager(viewPager);
    }



    ViewPagerAdapter adapter;

    private void setupViewPager(CustomViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        homeFragment = new DashboardFragment();
        checkinFragment = new CheckinsFragment();
        favFragment = new FavouriteFragment();
        inboxFragment = new InboxFragment();
        profileFragment = new ProfileFragment();

        adapter.addFragment(homeFragment);
        adapter.addFragment(checkinFragment);
        adapter.addFragment(favFragment);
        adapter.addFragment(inboxFragment);
        adapter.addFragment(profileFragment);

        // int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        // Config.logV("Limit------------@@@@@@@@@@@@@@@@@@@@---------"+limit);

        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);

    }

    public boolean onBackPressed() {
        // currently visible tab Fragment

        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(viewPager.getCurrentItem());


        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed

            return currentFragment.onBackPressed();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }

}
