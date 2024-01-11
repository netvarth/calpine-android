package com.jaldeeinc.customapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldeebusiness.BuildConfig;
import com.jaldeeinc.jaldeebusiness.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Context mContext;
    LinearLayout llSplash, llError;
    AlertDialog deleteDialog;
    Button btnRetry;
    WebView myWebView;
    Dialog dialog;
    DatabaseHandler db;
    Map<String, String> notificationData = new HashMap<>();
    ImageView iv_error;
    RelativeLayout loadingPanel;
    TextView tv_error, tv_error1;
    int SPLASH_SCREEN_TIME_OUT = 2700; // After completion of 3000 ms, the next activity will get started.

    int TIME_OUT_FOR_GET_FIREBASE_ID = 2000; // After completion of 3000 ms, the next activity will get started.
    protected ValueCallback<Uri> filePickerFileMessage;
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.129 Mobile Safari/537.36";
    private static String file_type = "*/*";
    private String cam_file_data = null;
    private ValueCallback<Uri> file_data;
    private ValueCallback<Uri[]> file_path;
    private final static int file_req_code = 1;
    boolean doubleBackToExitPressedOnce = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_NEEDED = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.INTERNET,
            //Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        db = new DatabaseHandler(mContext);
        dialog = new Dialog(MainActivity.this, WindowManager.LayoutParams.MATCH_PARENT);
        deleteDialog = new AlertDialog.Builder(this).create();

        initialize();

        // getting necessary details from intent
        Intent intent = getIntent();
        if (intent != null) {

            String jobject = intent.getStringExtra("notificationData");

            if (jobject != null && !jobject.trim().isEmpty()) {   // create notification Data in case of notification came if app forefround or back ground
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    try {
                        JSONObject jsonObject = new JSONObject(jobject);
                        notificationData = new Gson().fromJson(String.valueOf(jsonObject), new TypeToken<HashMap<String, String>>() {
                        }.getType());
                        SPLASH_SCREEN_TIME_OUT = 0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {    // create notification Data in case of notification came if app killed state
                Bundle extras = intent.getExtras();
                if (extras != null && !extras.isEmpty()) {

                    Set<String> ks = extras.keySet();
                    Iterator<String> iterator = ks.iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        notificationData.put(key, extras.getString(key));
                    }
                    SPLASH_SCREEN_TIME_OUT = 0;
                }
            }
        }

        chkInternetAndLoadCheckVersion();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chkInternetAndLoadCheckVersion();
                Handler handler = new Handler();
                // run a thread after 2 seconds to start the home screen
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingPanel.setVisibility(View.GONE);
                    }
                }, 2000);
                loadingPanel.setVisibility(View.VISIBLE);

            }
        });
    }

    private void loadHomePage(ConfigResponseDTO config, CustomAppConfigDTO customAppConfigDTO) {
        runOnUiThread(new Runnable() {
            public void run() {

                WebView.setWebContentsDebuggingEnabled(false);
                WebSettings settings = myWebView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setGeolocationEnabled(true);
                settings.setLoadWithOverviewMode(true);
                settings.setUseWideViewPort(true);
                settings.setAllowFileAccess(true);
                settings.setAllowContentAccess(true);
                settings.setAllowFileAccessFromFileURLs(true);
                settings.setAllowUniversalAccessFromFileURLs(true);
                settings.setSupportMultipleWindows(true);
                settings.setJavaScriptCanOpenWindowsAutomatically(true);
                settings.setDomStorageEnabled(true);
                settings.setLoadsImagesAutomatically(true);
                myWebView.setWebViewClient(new WebViewClient());
                myWebView.clearCache(true);
                settings.setUserAgentString(USER_AGENT);
                if (Build.VERSION.SDK_INT >= 19) {
                    myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                } else {
                    myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    settings.setMediaPlaybackRequiresUserGesture(false);
                }

                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_NEEDED, REQUEST_EXTERNAL_STORAGE);
                String uniqueID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) + "" + BuildConfig.PASSKEY;
                Handler handler0 = new Handler();
                // run a thread after 2 seconds to get firebase uid
                handler0.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
                        String regId = pref.getString("regId", null);
                        String customPath = "";
                        if (BuildConfig.PASSKEY.equalsIgnoreCase("JALDEE")) {
                            customPath = BuildConfig.UIROOT_URL + "business/login?at=" + uniqueID + "&muid=" + regId + "&device=" + ApiClient.getDeviceName();
                        } else {
                            String lang = BuildConfig.LAN;
                            customPath = BuildConfig.UIROOT_URL + config.getHomeUrl() + "?inst_id=" + uniqueID + "&muid=" + regId + "&app_id=" + getPackageName() + "&cl_dt=" + Constants.CLEARDATA + "&uid=" + BuildConfig.PASSKEY + "&device=" + ApiClient.getDeviceName();
                            if (lang != null && !lang.trim().isEmpty()) {
                                customPath = customPath + lang;
                            }
                        }
                        if (notificationData != null && !notificationData.isEmpty()) {
                            URL url = null;
                            try {
                                GsonBuilder builder = new GsonBuilder();
                                builder.setPrettyPrinting();
                                Gson gson = builder.create();
                                String jsonString = gson.toJson(notificationData);
                                url = new URL(customPath + "&notification=" + URLEncoder.encode(jsonString, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            if (url != null) {
                                customPath = url.toString();
                            }
                        }

                        System.out.println(customPath);
                        Log.d("custompath", customPath);
                        myWebView.loadUrl(customPath);
                        myWebView.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                                return overrideUrlLoading(wv, url, customAppConfigDTO);
                            }

//                            @Override
//                            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//
//                                /*boolean isUrlStarts = false;
//                                if(request.getUrl() != null && !request.getUrl().toString().trim().isEmpty() && request.getUrl().toString().trim().startsWith("https://developers-sounds")){
//                                    isUrlStarts = true;  // this condition for some mp3 files open page thrown error
//                                }*/
//                               /* if(!isUrlStarts) {
//                                        llError.setVisibility(View.VISIBLE);
//                                        llSplash.setVisibility(View.GONE);
//                                        tv_error.setText("Please check your Connection.\nYour Internet Connection May not be active");
//                                        tv_error1.setText("please try again.");
//                                        iv_error.setImageDrawable(getResources().getDrawable(R.drawable.somethingwentwrong));
//                                }*/
//                            }

                        });
                        myWebView.setWebChromeClient(new WebChromeClient() {
                            @Override
                            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                                enbleLocation();
                                callback.invoke(origin, true, false);
                            }

                            @Override
                            public void onPermissionRequest(final PermissionRequest request) {
                                request.grant(request.getResources());
                            }

                            @Override
                            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

                                WebView newWebView = new WebView(MainActivity.this);
                                newWebView.setLayoutParams(new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                ));
                                WebSettings webSettings = newWebView.getSettings();
                                webSettings.setJavaScriptEnabled(true);
                                webSettings.setGeolocationEnabled(true);
                                webSettings.setLoadWithOverviewMode(true);
                                webSettings.setUseWideViewPort(true);
                                webSettings.setAllowFileAccess(true);
                                webSettings.setAllowContentAccess(true);
                                webSettings.setAllowFileAccessFromFileURLs(true);
                                webSettings.setAllowUniversalAccessFromFileURLs(true);
                                webSettings.setSupportMultipleWindows(true);
                                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                                webSettings.setDomStorageEnabled(true);
                                webSettings.setLoadsImagesAutomatically(true);
                                webSettings.setUserAgentString(USER_AGENT);
                                //newWebView.clearCache(true);
                                newWebView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                                    /*    if (customAppConfigDTO != null
                                                && (customAppConfigDTO.getIntentUrls() != null)
                                                || customAppConfigDTO.getExceptionUrls() != null) {
                                            if (customAppConfigDTO.getExceptionUrls() != null && !customAppConfigDTO.getExceptionUrls().isEmpty()) {
                                                List<String> exceptionUrls = customAppConfigDTO.getExceptionUrls();
                                                for (String exceptionUrl : exceptionUrls) {
                                                    if (url.startsWith(exceptionUrl)) {
                                                        if (newWebView.getParent() != null) {
                                                            ((ViewGroup) newWebView.getParent()).removeView(newWebView);
                                                        }
                                                        dialog.setContentView(newWebView);
                                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                                        dialog.show();
                                                        return false;
                                                    }
                                                }
                                            }
                                            if (customAppConfigDTO.getIntentUrls() != null && !customAppConfigDTO.getIntentUrls().isEmpty()) {
                                                List<String> intentUrls = customAppConfigDTO.getIntentUrls();
                                                for (String intentUrl : intentUrls) {
                                                    if (url.startsWith(intentUrl)) {
                                                        try {
                                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                                            intent.setData(Uri.parse(url));
                                                            startActivity(intent);
                                                            return true;
                                                        } catch (Exception e) {

                                                        }
                                                    }
                                                }
                                            }
                                            if (url.startsWith("https:") || url.startsWith("http:")) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(url));
                                                startActivity(intent);
                                                return true;
                                            } else {

                                                return false;
                                            }
                                        }
                                        if (newWebView.getParent() != null) {
                                            ((ViewGroup) newWebView.getParent()).removeView(newWebView);
                                        }
                                        dialog.setContentView(newWebView);
                                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                        dialog.show();
                                        return false;*/
                                        return overrideUrlLoading(wv, url, customAppConfigDTO);
                                    }

                                    @Override
                                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                                        llError.setVisibility(View.VISIBLE);
                                        llSplash.setVisibility(View.GONE);
                                        tv_error.setText("Oops! something went wrong...");
                                        tv_error1.setText("please try again.");
                                        iv_error.setImageDrawable(getResources().getDrawable(R.drawable.somethingwentwrong));
                                    }

                                });
                                newWebView.setWebChromeClient(new WebChromeClient() {
                                    @Override
                                    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                                        callback.invoke(origin, true, false);
                                    }

                                    @Override
                                    public void onPermissionRequest(final PermissionRequest request) {
                                        request.grant(request.getResources());
                                    }

                                    @Override
                                    public void onCloseWindow(WebView window) {
                                        dialog.dismiss();
                                    }
                                });
                                ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
                                resultMsg.sendToTarget();

                                return true;
                            }

                            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                                if (requestFilePermissions() && Build.VERSION.SDK_INT >= 21) {
                                    file_path = filePathCallback;
                                    Intent takePictureIntent = null;
                                    Intent takeVideoIntent = null;

                                    takePictureIntent = createCameraCaptureIntent(false);
                                    //takeVideoIntent = createCameraCaptureIntent(true);

                                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                    contentSelectionIntent.setType(file_type);
                                    Intent[] intentArray;
                                    if (takePictureIntent != null && takeVideoIntent != null) {
                                        intentArray = new Intent[]{takePictureIntent, takeVideoIntent};
                                    } else if (takePictureIntent != null) {
                                        intentArray = new Intent[]{takePictureIntent};
                                    } else if (takeVideoIntent != null) {
                                        intentArray = new Intent[]{takeVideoIntent};
                                    } else {
                                        intentArray = new Intent[0];
                                    }
                                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser");
                                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                                    startActivityForResult(chooserIntent, file_req_code);
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        });
                        myWebView.setDownloadListener(new DownloadListener() {
                            public void onDownloadStart(String url, String userAgent,
                                                        String contentDisposition, String mimetype,
                                                        long contentLength) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });

                    }
                }, TIME_OUT_FOR_GET_FIREBASE_ID);

                Handler handler = new Handler();
                // run a thread after 2 seconds to start the home screen
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        llSplash.animate()
                                .translationZ(llSplash.getWidth())
                                .alpha(0.0f)
                                .setDuration(400)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        llSplash.setVisibility(View.GONE);
                                        myWebView.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                }, SPLASH_SCREEN_TIME_OUT);
            }
        });
    }

    private void checkVersion() {
        ApiInterface apiService = ApiClient.getClientS3Cloud().create(ApiInterface.class);
        Call<VersionResponseDTO> call = null;
        if (BuildConfig.PASSKEY.equalsIgnoreCase("JALDEE")) {
            call = apiService.getPVersionInfo(BuildConfig.PASSKEY, new Date().toString());
        } else {
            call = apiService.getVersionInfo(BuildConfig.PASSKEY, new Date().toString());
        }
        call.enqueue(new Callback<VersionResponseDTO>() {
            @Override
            public void onResponse(Call<VersionResponseDTO> call, Response<VersionResponseDTO> response) {
                VersionResponseDTO versionResponse = response.body();
                System.out.println(new Gson().toJson(versionResponse).toString());
                if (versionResponse != null && !BuildConfig.VERSION.equals(versionResponse.getPlaystore().getVersion()) && !versionResponse.getPlaystore().isInReview()) {

                    updateApp(versionResponse.getPlaystore());
                } else {

                    Integer cacheIndex = db.getCacheIndex();

                    if (versionResponse != null && versionResponse.getCacheIndex() != null && (cacheIndex == null || versionResponse.getCacheIndex() != cacheIndex)) {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.commit();
                        if (cacheIndex == null) {
                            db.insertCacheIndexValue(versionResponse.getCacheIndex());
                        } else {
                            db.updateCacheIndex(versionResponse.getCacheIndex());
                        }
                    }
                    getConfigurations();
                }
            }

            @Override
            public void onFailure(Call<VersionResponseDTO> call, Throwable t) {
                t.printStackTrace();
                call.cancel();
                getConfigurations();
            }
        });
    }

    private void updateApp(VersionDTO playstore) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(playstore.getTitle());
        alertDialog.setMessage(playstore.getMessage().toString());
        alertDialog.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = mContext.getPackageName();
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playstore.getLink())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playstore.getLink())));
                }
            }
        });
        alertDialog.show();
    }

    private void getConfigurations() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);

        String configurations = pref.getString("configuration", null);
        ConfigResponseDTO configDTO;

        String customAppConfigurations = pref.getString("customAppConfiguration", null);
        CustomAppConfigDTO customAppConfigDTO;

        ArrayList<Object> configObjects = new ArrayList<>();

        if ((BuildConfig.PASSKEY.equalsIgnoreCase("JALDEE") && (customAppConfigurations == null || customAppConfigurations.equals("null") || customAppConfigurations.isEmpty()))
                || (!BuildConfig.PASSKEY.equalsIgnoreCase("JALDEE") && (configurations == null || configurations.equals("null") || configurations.isEmpty() || customAppConfigurations == null || customAppConfigurations.equals("null") || customAppConfigurations.isEmpty()))) {
            try {
                ApiInterface apiService = ApiClient.getClientS3Cloud().create(ApiInterface.class);
                List<Observable<?>> requests = new ArrayList<>();
                // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.
                requests.add(apiService.getCustomAppConfiguration(new Date().toString()));
                if (!BuildConfig.PASSKEY.equalsIgnoreCase("JALDEE")) {
                    requests.add(apiService.getConfiguration(BuildConfig.PASSKEY, new Date().toString()));
                }

                // Zip all requests with the Function, which will receive the results.
                Observable.zip(requests, new Function<Object[], Object>() {

                            @Override
                            public Object apply(Object[] objects) throws Exception {
                                // Objects[] is an array of combined results of completed requests
                                CustomAppConfigDTO customAppConfigDTO = (CustomAppConfigDTO) objects[0];
                                customAppConfigDTO = customAppConfigDTO == null ? new CustomAppConfigDTO() : customAppConfigDTO;
                                configObjects.add(customAppConfigDTO);

                                if (!BuildConfig.PASSKEY.equalsIgnoreCase("JALDEE")) {
                                    ConfigResponseDTO configDTO = (ConfigResponseDTO) objects[1];
                                    configDTO = configDTO == null ? new ConfigResponseDTO() : configDTO;
                                    configObjects.add(configDTO);
                                }


                                return configObjects;
                            }
                        })
                        // After all requests had been performed the next observer will receive the Object, returned from Function

                        .subscribe(
                                // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                                new Consumer<Object>() {
                                    @Override
                                    public void accept(Object object) throws Exception {
                                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
                                        SharedPreferences.Editor editor = pref.edit();

                                        ArrayList<Object> configObjects = (ArrayList<Object>) object;
                                        CustomAppConfigDTO customAppConfigDTO;
                                        ConfigResponseDTO configDTO = new ConfigResponseDTO();

                                        customAppConfigDTO = (CustomAppConfigDTO) configObjects.get(0);
                                        editor.putString("customAppConfiguration", new Gson().toJson(customAppConfigDTO));
                                        if (!BuildConfig.PASSKEY.equalsIgnoreCase("JALDEE")) {
                                            configDTO = (ConfigResponseDTO) configObjects.get(1);
                                            editor.putString("configuration", new Gson().toJson(configDTO));
                                        }
                                        editor.commit();
                                        loadHomePage(configDTO, customAppConfigDTO);
                                    }
                                },

                                // Will be triggered if any error during requests will happen
                                new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable e) throws Exception {
                                        Log.e("ListOf Calls", "2");
                                        requests.clear();
                                    }
                                }
                        );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            configDTO = new Gson().fromJson(configurations, ConfigResponseDTO.class);
            customAppConfigDTO = new Gson().fromJson(customAppConfigurations, CustomAppConfigDTO.class);

            loadHomePage(configDTO, customAppConfigDTO);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View deleteDialogView = factory.inflate(R.layout.back_dialog, null);
            deleteDialog.setView(deleteDialogView);
            deleteDialogView.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                    MainActivity.super.onBackPressed();
                }
            });
            deleteDialogView.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });
            deleteDialog.show();
        }
        this.doubleBackToExitPressedOnce = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                if (myWebView != null && myWebView.canGoBack()) {
                    myWebView.goBack();
                } else {
                    MainActivity.super.onBackPressed();
                    finish();
                }
            }
        }, 400);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        deleteDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == file_req_code) {
                    if (null == file_path) {
                        return;
                    }
                    if (intent == null || intent.getDataString() == null) {
                        if (cam_file_data != null) {
                            results = new Uri[]{Uri.parse(cam_file_data)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }

                    file_path.onReceiveValue(results);
                    file_path = null;
                }

            } else {
                file_path.onReceiveValue(results);
                file_path = null;
            }
        } else {
            if (requestCode == file_req_code) {
                if (null == filePickerFileMessage) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                filePickerFileMessage.onReceiveValue(result);
                filePickerFileMessage = null;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private boolean requestFilePermissions() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();fc
                            //Toast.makeText(getApplicationContext(), "You Denied the Permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();

                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
        return true;
    }

    private Intent createCameraCaptureIntent(boolean isVideo) {
        //boolean isVideo = false;
        /*boolean isVideo = false;
        if (mimeTypes != null && mimeTypes.length == 1 && mimeTypes[0] != null && mimeTypes[0].startsWith("video")) {
            isVideo = true;
        }*/
        Intent takePictureIntent = new Intent(isVideo ? MediaStore.ACTION_VIDEO_CAPTURE : MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
            File imageVideoFile = null;

            try {
                imageVideoFile = createImageOrVideo(isVideo);
            } catch (IOException ex) {
                Log.e("", "Image file creation failed", ex);
                ex.printStackTrace();
            }
            if (imageVideoFile != null) {
                cam_file_data = "file:" + imageVideoFile.getAbsolutePath();

                Uri photoUri = FileProvider.getUriForFile(
                        this,
                        getPackageName(),
                        imageVideoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            } else {
                takePictureIntent = null;
            }
        }
        return takePictureIntent;
    }

    //Creating image or video file for upload
    protected File createImageOrVideo(boolean isVideo) throws IOException {
        @SuppressLint("SimpleDateFormat")
        String new_name = "IMG";
        File sd_directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(new_name, isVideo ? ".mp4" : ".jpg", sd_directory);
    }

    private void chkInternetAndLoadCheckVersion() {
        /*ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);*/
        boolean connected = isNetworkConnected(this);
        if (connected) {
            llError.setVisibility(View.GONE);
            llSplash.setVisibility(View.VISIBLE);
            checkVersion();

        } else {
            llError.setVisibility(View.VISIBLE);
            llSplash.setVisibility(View.GONE);
            tv_error.setText("No Internet connection..");
            tv_error1.setText("Please check your connection status and try again.");
            iv_error.setImageDrawable(getResources().getDrawable(R.drawable.somethingwentwrong));
        }
    }

    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void initialize() {

        llSplash = findViewById(R.id.ll_splash);
        llError = findViewById(R.id.ll_error);
        btnRetry = findViewById(R.id.btn_retry);
        iv_error = findViewById(R.id.iv_error);
        loadingPanel = findViewById(R.id.loadingPanel);
        tv_error = findViewById(R.id.tv_error);
        tv_error1 = findViewById(R.id.tv_error1);
        myWebView = (WebView) findViewById(R.id.webview);
        llError.setVisibility(View.GONE);
        llSplash.setVisibility(View.VISIBLE);

    }

    private void enbleLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(MainActivity.this)
                .checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The device location is enabled.
                } catch (ApiException exception) {
                    if (exception.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        // Location settings are not satisfied. Show the dialog to the user.
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(MainActivity.this, 101);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                    }
                }
            }
        });
    }

    public boolean overrideUrlLoading(WebView wv, String url, CustomAppConfigDTO customAppConfigDTO) {
        if (customAppConfigDTO != null
                && (customAppConfigDTO.getIntentUrls() != null)
                || customAppConfigDTO.getExceptionUrls() != null) {
            if (customAppConfigDTO.getExceptionUrls() != null && !customAppConfigDTO.getExceptionUrls().isEmpty()) {
                List<String> exceptionUrls = customAppConfigDTO.getExceptionUrls();
                for (String exceptionUrl : exceptionUrls) {
                    if (url.startsWith(exceptionUrl)) {
                        if (wv.getParent() != null) {
                            ((ViewGroup) wv.getParent()).removeView(wv);
                        }
                        dialog.setContentView(wv);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialog.show();
                        return false;
                    }
                }
            }
            if (customAppConfigDTO.getIntentUrls() != null && !customAppConfigDTO.getIntentUrls().isEmpty()) {
                List<String> intentUrls = customAppConfigDTO.getIntentUrls();
                for (String intentUrl : intentUrls) {
                    if (url.startsWith(intentUrl)) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                            return true;
                        } catch (Exception e) {

                        }
                    }
                }
            }
            if (url.startsWith("https:") || url.startsWith("http:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        }
        if (wv.getParent() != null) {
            ((ViewGroup) wv.getParent()).removeView(wv);
        }
        dialog.setContentView(wv);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        return false;
    }

}