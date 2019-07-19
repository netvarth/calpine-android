package com.jaldeeinc.jaldee.common;

/**
 * Created by sharmila on 2/7/18.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.LoginResponse;
import com.jaldeeinc.jaldee.utils.LogUtil;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public class Config {


    public static boolean is_log_enabled = true;
    public static String APP_TAG = "YNW"; //app name
    public static Vector<AlertDialog> dialogs = new Vector<AlertDialog>();
    public static int taskCount;
    public static final String SHARED_PREF = "jaldee_firebase";
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";


    public static final String NOTIFICATION_EVENT = "NOTIFICATION_EVENT";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static void logV(String message) {
        if (is_log_enabled) {
            Log.v(APP_TAG, message);
        }
    }
    public static void logE(String message) {
        if (is_log_enabled) {
            if(message.contains("start"))
                taskCount++;
            else if(message.contains("stop"))
                taskCount--;
            Log.e(APP_TAG, message+"   >>  "+taskCount);
        }
    }

    public static String getTodaysDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }
    public static String getYesterdayDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }
    public static void logV(String title, String message) {
        if (is_log_enabled) {
            Log.v(title, message);
        }
    }
    public static String getMacAddress(Activity activity) {
        WifiManager manager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        if(address==null)
            address="";

        return address;
    }

    public static Dialog getProgressDialog(Context context, String title) {

        Dialog dialog = new Dialog(context, R.style.ProgressBar);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mViewDialog = mInflater.inflate(R.layout.layout_circle_dialog_progress, null);
        ProgressBar spinner = (ProgressBar) mViewDialog.findViewById(R.id.progress);

        spinner.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(mViewDialog);
        ViewGroup.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = context.getResources().getDimensionPixelSize(R.dimen.layout_alert_dialog_width);
        lp.height = context.getResources().getDimensionPixelSize(R.dimen.layout_alert_dialog_height);

        return dialog;
    }


    public static void ApiSessionResetLogin(String loginId, String password, final Context context) {

        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);


        SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Config.logV("REGISTARION ID______RENEW________@@@@@@@___"+regId);
        LogUtil.writeLogTest("REG ID @@@@@@@@@@@"+regId);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("loginId", loginId);
            jsonObj.put("password", password);
            jsonObj.put("mUniqueId", regId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Config.logV("JSON--------------" + jsonObj);

        Call<LoginResponse> call = apiService.LoginResponse(body);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {

                try {



                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response-- LOGIN RESERT code-------------------------" + response.code());

                    LogUtil.writeLogTest("REG ID RESP CODE@@@@@@@@@@@"+response.code());
                    if (response.code() == 200) {
                        Config.logV("Response--code-------------------------" + response.body().getFirstName());


                        // get header value
                        String cookie = response.headers().get("Set-Cookie");

                        Config.logV("Response--Cookie-------------------------" + cookie);
                        if (!cookie.isEmpty()) {

                            SharedPreference.getInstance(context).getStringValue("PREF_COOKIES", "");
                            String header = response.headers().get("Set-Cookie");
                            String Cookie_header = header.substring(0, header.indexOf(";"));

                            SharedPreference.getInstance(context).setValue("PREF_COOKIES", Cookie_header);
                            Config.logV("Set Cookie sharedpref------------" + Cookie_header);

                            LogUtil.writeLogTest("****Login Cookie****"+Cookie_header);

                        }


                        Headers headerList = response.headers();
                        String version = headerList.get("Version");
                        Config.logV("Header----------" + version);

                        SharedPreference.getInstance(context).setValue("Version", version);

                        // Config.logV("Email------------------"+response.body().get);
                        SharedPreference.getInstance(context).setValue("consumerId", response.body().getId());
                        SharedPreference.getInstance(context).setValue("register", "success");
                        SharedPreference.getInstance(context).setValue("firstname", response.body().getFirstName());
                        SharedPreference.getInstance(context).setValue("lastname", response.body().getLastName());

                        SharedPreference.getInstance(context).setValue("s3Url", response.body().getS3Url());

                        SharedPreference.getInstance(context).setValue("mobile", response.body().getPrimaryPhoneNumber());
                        Intent iReg = new Intent(context, Home.class);
                        iReg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(iReg);
                       // ((Activity)context).finish();
                        Config.logV("App@@@ ForeBackGround Sucess");

                    }else{
                       // Toast.makeText(context,response.errorBody().string(),Toast.LENGTH_LONG).show();
                        Config.logV("App@@@ ForeBackGround fail");
                       /* Intent iReg = new Intent(context, Home.class);
                        iReg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(iReg);*/
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });

    }
    public static void ApiUpdateToken(final Context context) {

        ApiInterface apiService =
                ApiClient.getClient(context).create(ApiInterface.class);


        SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        LogUtil.writeLogTest("REG ID @@@@@@@@@@@"+regId);
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("token", regId);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Config.logV("JSON--------------" + jsonObj);

        Call<ResponseBody> call = apiService.updatePushToken(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                try {



                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());

                    if (response.code() == 200) {


                    }else{

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });

    }
    public static void closeDialog(Activity mActivity,Dialog mDialog) {

        try {
            try {
                if (mActivity!=null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (mActivity.isDestroyed()) { // or call isFinishing() if min sdk version < 17
                            return;
                        }
                    } else if (mActivity.isFinishing())
                        return;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Config.logV(e.getMessage());
        }
    }

    public static void showAlertBuilder(final Context mContext, final String title, final String message) {
        try {

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!((Activity) mContext).isFinishing()) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        if (title.equals("") || title == null) {
                            alertDialog.setTitle(null);
                        } else {
                            alertDialog.setTitle(title);
                        }
                        alertDialog.setMessage(message);

                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getResources().getString(R.string.layout_button_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              alertDialog.dismiss();
                            }
                        });
                        //Add dialog to vector collection of dialogs
                        dialogs.add(alertDialog);
                        // Showing Alert Message
                        alertDialog.show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Config.logV(e.toString());
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public static String ChangeDateFormat(String datepass) throws ParseException {
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-mm-dd");
        Date newDate=spf.parse(datepass);
        spf= new SimpleDateFormat("dd-mm-yyyy");
        String convertdate = spf.format(newDate);
        return  convertdate;

    }
    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }
}
