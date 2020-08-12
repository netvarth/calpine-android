package com.jaldeeinc.jaldee.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.utils.SharedPreference;

/**
 * Created by sharmila on 3/7/18.
 */

public class Splash extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;

    private final Handler mHandler = new Handler();
    private final Launcher mLauncher = new Launcher();
    private ImageView ivLogo;
    Animation animation;

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ivLogo = findViewById(R.id.iv_logo);
        TextView txtlogo = findViewById(R.id.txtlogo);
        Typeface tyface = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        txtlogo.setTypeface(tyface);

        animation = AnimationUtils.loadAnimation(Splash.this, R.anim.sample);
        ivLogo.startAnimation(animation);


        Config.logV("SPLASH @@@@@@@@@@@");
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mLauncher);
        super.onStop();
    }

    private void launch() {
        if (!isFinishing()) {
            String check = SharedPreference.getInstance(this).getStringValue("register", "");
            if (check.equalsIgnoreCase("success")) {
                checkDeepLink();
//                Intent iLogin = new Intent(this, Home.class);
//                iLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(iLogin);
//                finish();
            } else {
                checkDeepLinkRegister();
            }
        }
    }

    private void checkDeepLink() {
        if (getIntent() != null && getIntent().getData() != null) {
            Uri data = getIntent().getData();
            String scheme = data.getScheme();
            String host = data.getHost();
            String path = data.getPath();
            String param = data.getLastPathSegment();

            Log.i("DeepLink", "data : " + data);
            Log.i("DeepLink", "Host : " + host);
            Log.i("DeepLink", "param : " + param);
            Log.i("DeepLink", "path : " + path);
            Log.i("DeepLink", "scheme : " + scheme);

            if (host.equals("scale.jaldee.com")) {
                Log.i("retert","ertert");
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("detail_id", (param));  // URL query values as string, you need to parse string to long.
                intent.putExtra("path", (path));  // URL query values as string, you need to parse string to long.
                startActivity(intent);
            }

        } else {
            Intent iLogin = new Intent(this, Home.class);
            iLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(iLogin);
            finish();
        }
    }


    private void checkDeepLinkRegister() {
        if (getIntent() != null && getIntent().getData() != null) {
            Uri data = getIntent().getData();
            String scheme = data.getScheme();
            String host = data.getHost();
            String path = data.getPath();
            String param = data.getEncodedFragment();
            Log.i("DeepLink", "Schema : " + data);
            Log.i("DeepLink", "Host : " + host);
            Log.i("DeepLink", "param : " + host);
            Log.i("DeepLink", "param : " + param);

            if (host.equals("scale.jaldee.com")) {

                Intent iLogin = new Intent(this, Register.class);
                iLogin.putExtra("detail_id", (param));
                startActivity(iLogin);
                finish();
            }

        } else {
            Intent iLogin = new Intent(this, Register.class);
            startActivity(iLogin);
            finish();
        }
    }

    private class Launcher implements Runnable {
        @Override
        public void run() {
            launch();
        }
    }
}
