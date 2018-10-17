package com.nv.youneverwait.Fragment;

import android.support.v4.app.Fragment;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {

        return new BackPressImpl(this).onBackPressed();
    }
}