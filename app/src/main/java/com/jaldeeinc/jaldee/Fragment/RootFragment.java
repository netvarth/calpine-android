package com.jaldeeinc.jaldee.Fragment;

import android.support.v4.app.Fragment;

import com.jaldeeinc.jaldee.utils.BackPressImpl;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {

        return new BackPressImpl(this).onBackPressed();
    }
}