package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jaldeeinc.jaldee.Fragment.AppointmentMyJaldee;
import com.jaldeeinc.jaldee.Fragment.CheckinsMyJaldee;
import com.jaldeeinc.jaldee.Fragment.TokensMyJaldee;
import com.jaldeeinc.jaldee.R;

public class NotificationActivity extends AppCompatActivity {

    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Intent i =getIntent();
        message = i.getStringExtra("message");

        if (message.contains("appointment")){

            Bundle bundle = new Bundle();
            bundle.putBoolean("toHome", true);
            bundle.putString("message",message);
            AppointmentMyJaldee afFragment = new AppointmentMyJaldee();
            afFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
            transaction.addToBackStack(null);
            transaction.replace(R.id.notifycontainer, afFragment).commit();
        }
        else if (message.contains("check-in")){

            Bundle bundle = new Bundle();
            bundle.putBoolean("toHome", true);
            bundle.putString("message",message);
            CheckinsMyJaldee pfFragment = new CheckinsMyJaldee();
            pfFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
            transaction.addToBackStack(null);
            transaction.replace(R.id.notifycontainer, pfFragment).commit();
        }
        else if (message.contains("token")){

            Bundle bundle = new Bundle();
            bundle.putBoolean("toHome", true);
            bundle.putString("message",message);
            TokensMyJaldee pfFfragment = new TokensMyJaldee();
            pfFfragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
            transaction.addToBackStack(null);
            transaction.replace(R.id.notifycontainer, pfFfragment).commit();
        }
        else {

            Intent intent = new Intent(NotificationActivity.this,Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NotificationActivity.this,Home.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
