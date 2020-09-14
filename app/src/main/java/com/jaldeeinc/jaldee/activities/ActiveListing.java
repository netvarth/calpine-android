package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.jaldeeinc.jaldee.Fragment.AppointmentMyJaldee;
import com.jaldeeinc.jaldee.Fragment.CheckinsMyJaldee;
import com.jaldeeinc.jaldee.Fragment.TokensMyJaldee;
import com.jaldeeinc.jaldee.R;

public class ActiveListing extends AppCompatActivity {

    String takeTo = "";

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_listing);

        Intent i = getIntent();
        takeTo = i.getStringExtra("takeTo");

        Bundle bundle = new Bundle();
        bundle.putBoolean("toHome", true);

        if (takeTo.equalsIgnoreCase(Constants.TOKEN)) {
            TokensMyJaldee tokensMyJaldee = new TokensMyJaldee();
            tokensMyJaldee.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
//            transaction.addToBackStack(null);
            transaction.replace(R.id.mainlayout, tokensMyJaldee).commit();
        } else if (takeTo.equalsIgnoreCase(Constants.APPOINTMENT)) {

            AppointmentMyJaldee appointmentMyJaldee = new AppointmentMyJaldee();
            appointmentMyJaldee.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
            transaction.replace(R.id.mainlayout, appointmentMyJaldee).commit();

        } else if (takeTo.equalsIgnoreCase(Constants.CHECKIN)) {

            CheckinsMyJaldee checkinsMyJaldee = new CheckinsMyJaldee();
            checkinsMyJaldee.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
            transaction.replace(R.id.mainlayout, checkinsMyJaldee).commit();
        }
    }


}