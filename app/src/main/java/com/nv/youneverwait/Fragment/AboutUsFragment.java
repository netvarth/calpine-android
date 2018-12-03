package com.nv.youneverwait.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nv.youneverwait.Fragment.RootFragment;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;


public class AboutUsFragment extends RootFragment {

    ImageView collapseabt, collapseterm, collapseprivacy;
    LinearLayout mLaboutus, mLterms, mLprivacy;
    boolean expandabtFlag = false, expandtermFlag = false, expandpvyFlag = false;
    WebView aboutWeb, privacyWeb, termsWeb;
    Activity mActivity;
    String webabout, webterm, webprivacy;
    private Dialog progressBar;

    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.aboutus, container, false);

        mContext = getActivity();
        mLaboutus = (LinearLayout)row. findViewById(R.id.Laboutus);


        aboutWeb = (WebView) row.findViewById(R.id.aboutWeb);
        mLprivacy = (LinearLayout) row.findViewById(R.id.Lprivacy);
        privacyWeb = (WebView) row.findViewById(R.id.privacyWeb);
        mLterms = (LinearLayout)row. findViewById(R.id.Lterms);
        termsWeb = (WebView) row.findViewById(R.id.termsWeb);

        //expList = (ExpandableListView) row.findViewById(R.id.exp_list);
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress = (ImageView)row. findViewById(R.id.backpress);

        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setText("About Us");
        tv_title.setTypeface(tyface);

        collapseabt = (ImageView) row.findViewById(R.id.collapseabt);
        collapseterm = (ImageView) row.findViewById(R.id.collapseterm);
        collapseprivacy = (ImageView)row. findViewById(R.id.collapseprivacy);
        expandabtFlag = false;
        webabout = "<html><body>" +
                "<p>Jaldee is an all-in-one web portal integrated with mobile application. It manages service providers’ day to day operations like  waitlist, schedule, billing and payments. Customers can search service providers and book their time slots either from the portal or from the mobile app.</p>" +
                "<p>Jaldee's cloud-based platform is scalable, secure, and ready for any size deployment. Jaldee is owned and operated by Netvarth technologies Pvt Ltd.</p>" +
                "</body>" +
                "</html>";
        mLaboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Click-----------------");
                if (!expandabtFlag) {
                    expandabtFlag = true;
                    aboutWeb.setVisibility(View.VISIBLE);
                    collapseabt.setImageResource(R.drawable.icon_up_light);
                    Config.logV("WEbabout--5556-------------" + webabout);
                    aboutWeb.loadData(webabout, "text/html", "UTF-8");

                    expandtermFlag=false;
                    expandpvyFlag=false;
                    termsWeb.setVisibility(View.GONE);
                    privacyWeb.setVisibility(View.GONE);
                    collapseterm.setImageResource(R.drawable.icon_down_light);
                    collapseprivacy.setImageResource(R.drawable.icon_down_light);

                } else {
                    aboutWeb.setVisibility(View.GONE);
                    collapseabt.setImageResource(R.drawable.icon_down_light);
                    expandabtFlag = false;
                }

            }
        });

        expandpvyFlag = false;
        mLprivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Click-----------------");
                if (!expandpvyFlag) {
                    expandpvyFlag = true;
                    privacyWeb.setVisibility(View.VISIBLE);
                    collapseprivacy.setImageResource(R.drawable.icon_up_light);


                    expandtermFlag=false;
                    expandabtFlag=false;
                    termsWeb.setVisibility(View.GONE);
                    aboutWeb.setVisibility(View.GONE);

                    collapseterm.setImageResource(R.drawable.icon_down_light);
                    collapseabt.setImageResource(R.drawable.icon_down_light);


                    //privacyWeb.loadData(webprivacy, "text/html", "UTF-8");


                   // privacyWeb.loadUrl("https://www.youneverwait.com/#/privacy/mobile");

                    privacyWeb.setWebViewClient(new WebViewClient() {

                        // This method will be triggered when the Page Started Loading

                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            progressBar = Config.getProgressDialog(mContext, "");
                            progressBar.show();
                            progressBar.setCancelable(true);
                            super.onPageStarted(view, url, favicon);
                        }

                        // This method will be triggered when the Page loading is completed

                        public void onPageFinished(WebView view, String url) {
                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                            }
                            super.onPageFinished(view, url);
                        }

                        // This method will be triggered when error page appear

                        @Override
                        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError er) {
                            handler.proceed();
                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                            }
                            // Ignore SSL certificate errors
                        }
                    });
                    privacyWeb.loadUrl("http://35.154.241.175/jaldee/#/privacy/mobile");
                    /*privacyWeb.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {

                            view.loadUrl(url);
                            return true;
                        }


                        public void onPageFinished(WebView view, String url) {

                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                            }
                        }
                    });*/
                } else {
                    privacyWeb.setVisibility(View.GONE);
                    expandpvyFlag = false;
                    collapseprivacy.setImageResource(R.drawable.icon_down_light);
                }

            }
        });

        expandtermFlag = false;

        mLterms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Click-----------------");

                if (!expandtermFlag) {
                    expandtermFlag = true;
                    termsWeb.setVisibility(View.VISIBLE);
                    collapseterm.setImageResource(R.drawable.icon_up_light);

                    expandpvyFlag=false;
                    expandabtFlag=false;
                    privacyWeb.setVisibility(View.GONE);
                    aboutWeb.setVisibility(View.GONE);
                    collapseprivacy.setImageResource(R.drawable.icon_down_light);
                    collapseabt.setImageResource(R.drawable.icon_down_light);

                    progressBar = Config.getProgressDialog(mContext, "");
                    progressBar.show();

                    //termsWeb.loadUrl("https://www.youneverwait.com/#/terms/mobile");
                    termsWeb.loadUrl("http://35.154.241.175/jaldee/#/terms/mobile");
                    termsWeb.setWebViewClient(new WebViewClient() {
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
                } else {
                    termsWeb.setVisibility(View.GONE);
                    expandtermFlag = false;
                    collapseterm.setImageResource(R.drawable.icon_down_light);
                }

            }
        });
        WebSettings settings = aboutWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        aboutWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        aboutWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        WebSettings settingsterm = termsWeb.getSettings();
        settingsterm.setJavaScriptEnabled(true);
        settingsterm.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        termsWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        termsWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        WebSettings settingprivacy = privacyWeb.getSettings();
        settingprivacy.setJavaScriptEnabled(true);
        settingprivacy.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        privacyWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        privacyWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        return row;
    }


}
