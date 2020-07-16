package com.jaldeeinc.jaldee.Fragment;

import androidx.fragment.app.Fragment;

import com.jaldeeinc.jaldee.utils.BackPressImpl;

public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {

        return new BackPressImpl(this).onBackPressed();
    }
}