package com.netvarth.youneverwait.connection;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.common.Config;

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
            // throw new NoConnectivityException();
            Config.showAlertBuilder(mContext, "No Network", "Please check your connection");
        } else {
            try {
                Request.Builder builder = chain.request().newBuilder();
                return chain.proceed(builder.build());
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                Config.showAlertBuilder(mContext, mContext.getResources().getString(R.string.alertdialog_title_error),
                        mContext.getResources().getString(R.string.dialog_time_out_text));
            } catch (SocketException e) {
                e.printStackTrace();
                Config.showAlertBuilder(mContext, mContext.getResources().getString(R.string.alertdialog_title_error),
                        mContext.getResources().getString(R.string.dialog_server_down_text));
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

}