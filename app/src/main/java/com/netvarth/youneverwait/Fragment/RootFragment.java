package com.netvarth.youneverwait.Fragment;

import android.support.v4.app.Fragment;

import com.netvarth.youneverwait.Fragment.OnBackPressListener;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }
}