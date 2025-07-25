package com.nv.youneverwait.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.utils.SharedPreference;

import org.w3c.dom.Text;

/**
 * Created by sharmila on 3/7/18.
 */

public class Splash extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000;

    private final Handler mHandler   = new Handler();
    private final Launcher mLauncher = new Launcher();

    @Override
    protected void onStart() {
        super.onStart();

        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        TextView txtlogo=(TextView)findViewById(R.id.txtlogo) ;
        Typeface tyface = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        txtlogo.setTypeface(tyface);

        Config.logV("SPLASH @@@@@@@@@@@");


    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mLauncher);
        super.onStop();
    }



    private void launch() {
        if (!isFinishing()) {
            String check=SharedPreference.getInstance(this).getStringValue("register","");
            if(check.equalsIgnoreCase("success")) {
                Intent iLogin = new Intent(this, Home.class);
                iLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(iLogin);
                finish();
            }else{
                Intent iLogin = new Intent(this, Register.class);
                startActivity(iLogin);
                finish();
            }
        }
    }

    private class Launcher implements Runnable {
        @Override
        public void run() {
            launch();
        }
    }
}
