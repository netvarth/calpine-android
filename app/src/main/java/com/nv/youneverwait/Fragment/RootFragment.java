package com.nv.youneverwait.Fragment;

import android.support.v4.app.Fragment;

import com.nv.youneverwait.utils.BackPressImpl;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {

        return new BackPressImpl(this).onBackPressed();
    }
}