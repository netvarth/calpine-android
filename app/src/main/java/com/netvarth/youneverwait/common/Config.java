package com.netvarth.youneverwait.common;

/**
 * Created by sharmila on 2/7/18.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.netvarth.youneverwait.R;
import java.util.Vector;


public class Config {


    public static boolean is_log_enabled = true;
    public static String APP_TAG = "YNW"; //app name
    public static Vector<AlertDialog> dialogs = new Vector<AlertDialog>();
    public static int taskCount;

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
}
