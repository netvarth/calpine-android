package com.jaldeeinc.jaldee.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jaldeeinc.jaldee.Fragment.MyBookings;
import com.jaldeeinc.jaldee.Fragment.MyPaymentsFragment;
import com.jaldeeinc.jaldee.Fragment.Tab2Fragment;

public class JaldeeTabs extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public JaldeeTabs(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MyBookings bookings = new MyBookings();
                return bookings;
            case 1:
                Tab2Fragment tab2Fragment = new Tab2Fragment();
                return tab2Fragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}