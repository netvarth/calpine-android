package com.jaldeeinc.jaldee.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.jaldeeinc.jaldee.Fragment.CustomerNotesFragment;
import com.jaldeeinc.jaldee.Fragment.InstructionsFragment;
import com.jaldeeinc.jaldee.Fragment.MessagesFragment;



public class MoreInfoTabs extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public MoreInfoTabs(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                InstructionsFragment instructionsFragment = new InstructionsFragment();
                return instructionsFragment;
            case 1:
                MessagesFragment messagesFragment = new MessagesFragment();
                return messagesFragment;
            case 2:
                CustomerNotesFragment customerNotesFragment = new CustomerNotesFragment();
                return customerNotesFragment;
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

