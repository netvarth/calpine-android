package com.netvarth.youneverwait.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.ViewPagerAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.utils.BottomNavigationViewHelper;

/**
 * Created by sharmila on 9/7/18.
 */

public class HomeTabFragment extends Fragment {

    BottomNavigationView bottomNavigationView;

    //This is our viewPager
    private ViewPager viewPager;
    Toolbar toolbar;
    Context mContext;

    //Fragments

    FavouriteFragment favFragment;
    MyHomeFragment homeFragment;
    CheckinsFragment checkinFragment;
    InboxFragment inboxFragment;
    ProfileFragment profileFragment;

    MenuItem prevMenuItem;
    static Fragment hometabFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.bottomtab_fragment, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);


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
                    public boolean onNavigationItemSelected( MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                viewPager.setCurrentItem(0);
                                item.setIcon(getResources().getDrawable(R.drawable.selected_home));
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

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        homeFragment = new MyHomeFragment();
        checkinFragment = new CheckinsFragment();
        favFragment = new FavouriteFragment();
        inboxFragment = new InboxFragment();
        profileFragment = new ProfileFragment();

        adapter.addFragment(homeFragment);
        adapter.addFragment(checkinFragment);
        adapter.addFragment(favFragment);
        adapter.addFragment(inboxFragment);
        adapter.addFragment(profileFragment);

        viewPager.setAdapter(adapter);
        /*viewPager.setOffscreenPageLimit(5);*/
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
