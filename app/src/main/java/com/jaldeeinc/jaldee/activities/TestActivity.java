package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jaldeeinc.jaldee.Fragment.HomeSearchFragment;
import com.jaldeeinc.jaldee.Fragment.MyJaldee;
import com.jaldeeinc.jaldee.R;

public class TestActivity extends AppCompatActivity {

    HomeSearchFragment myJaldee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        myJaldee = new HomeSearchFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.fl_holder, myJaldee).commit();

    }
}