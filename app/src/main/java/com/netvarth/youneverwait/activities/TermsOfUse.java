package com.netvarth.youneverwait.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.common.Config;


/**
 * Created by sharmila on 14/5/18.
 */

public class TermsOfUse extends AppCompatActivity {
    WebView webview;
    Toolbar mToolbar;
    private Dialog progressBar;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.termsofuse);
        mContext = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tv_title = (TextView) mToolbar.findViewById(R.id.toolbar_title);

        tv_title.setText("Terms & Conditions");
        Typeface tyface = Typeface.createFromAsset(this.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
               // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });


        webview = (WebView) findViewById(R.id.mWebview_about);


        //WebView loading start
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        progressBar = Config.getProgressDialog(this, "");
        progressBar.show();

        webview.loadUrl("https://www.youneverwait.com/#/terms");
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {

                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });
        //Webview loading end


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_title);


        TextView title = (TextView) findViewById(android.R.id.text1);
        title.setText("TermsOfUse");

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
                        webview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }




}
