package com.jaldeeinc.jaldee.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.jaldeeinc.jaldee.Fragment.MyJaldee;
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
            MyJaldee myJaldee = new MyJaldee();
            myJaldee.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
            transaction.addToBackStack(null);
            transaction.replace(R.id.notifycontainer, myJaldee).commit();
        }
        else if (message.contains("check-in")){

            Bundle bundle = new Bundle();
            bundle.putBoolean("toHome", true);
            bundle.putString("message",message);
            MyJaldee myJaldee = new MyJaldee();
            myJaldee.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
            transaction.addToBackStack(null);
            transaction.replace(R.id.notifycontainer, myJaldee).commit();
        }
        else if (message.contains("token")){

            Bundle bundle = new Bundle();
            bundle.putBoolean("toHome", true);
            bundle.putString("message",message);
            MyJaldee myJaldee = new MyJaldee();
            myJaldee.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
            transaction.addToBackStack(null);
            transaction.replace(R.id.notifycontainer, myJaldee).commit();
        }
        else {

            Bundle bundle = new Bundle();
            bundle.putBoolean("toHome", true);
            bundle.putString("message",message);
            MyJaldee myJaldee = new MyJaldee();
            myJaldee.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right);
            // Store the Fragment in stack
            transaction.addToBackStack(null);
            transaction.replace(R.id.notifycontainer, myJaldee).commit();
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(NotificationActivity.this,Home.class);
        intent.removeExtra("message");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
