package com.nv.youneverwait.connection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.common.MyApplication;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by sharmila on 2/7/18.
 */

public class ConnectivityInterceptor implements Interceptor {

    private Context mContext;

    public ConnectivityInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {

        if (!isOnline()) {
             throw new NoConnectivityException();
           // Config.showAlertBuilder(mContext, "No Network", "Please check your connection");

        } else {
            try {
                Request.Builder builder = chain.request().newBuilder();
                return chain.proceed(builder.build());
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
              /*  Config.showAlertBuilder(mContext, mContext.getResources().getString(R.string.alertdialog_title_error),
                        mContext.getResources().getString(R.string.dialog_time_out_text));*/
                Toast.makeText(mContext,  mContext.getResources().getString(R.string.dialog_time_out_text), Toast.LENGTH_LONG).show();
            } catch (SocketException e) {
                e.printStackTrace();
                /*Config.showAlertBuilder(mContext, mContext.getResources().getString(R.string.alertdialog_title_error),
                        mContext.getResources().getString(R.string.dialog_server_down_text));*/

                Toast.makeText(mContext,  mContext.getResources().getString(R.string.dialog_server_down_text), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();


            }
        }

        return chain.proceed(chain.request());

    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
    public class NoConnectivityException extends IOException {

        @Override
        public String getMessage() {
            Toast.makeText(mContext, "No network available, please check your WiFi or Data connection", Toast.LENGTH_LONG).show();
            //Config.showAlertBuilder(mContext, "No Network", "Please check your connection");
            return "No network available, please check your WiFi or Data connection";
        }

    }
}