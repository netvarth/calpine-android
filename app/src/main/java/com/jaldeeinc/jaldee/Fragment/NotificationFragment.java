package com.jaldeeinc.jaldee.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.TelegramNotificationSettingsResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends RootFragment {
    Context mContext;
    private String phoneNumber, countryCode, botUrl = "";
    boolean telegramotificationStatus;
    Switch switch_paytm_notification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.notification_layout, container, false);
        mContext = getActivity();

        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        switch_paytm_notification = row.findViewById(R.id.switch_paytm_notification);
        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        tv_title.setText("Notifications");
        APIGetTelegramNotificationSettings();

        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });
        switch_paytm_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (switch_paytm_notification.isChecked()) {
                    updateTelegramNotification("ENABLED");
                } else {
                    updateTelegramNotification("DISABLED");
                }
            }
        });
        return row;
    }

    public void APIGetTelegramChatId(String status) {
        phoneNumber = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "").replace("+", "");
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.getTelegramChatId(countryCode, phoneNumber);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    if (response.code() == 200) {
                        if (response.body().contentLength() == 0) {
                            showtelegramLaunchAlertDialog();
                        } else {
                            APIUpdateTelegramNotification(status);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void updateTelegramNotification(String status) {
        if (status.equals("ENABLED")) {
            APIGetTelegramChatId(status);
        } else if (status.equals("DISABLED")) {
            APIUpdateTelegramNotification(status);
        }
    }

    private void APIUpdateTelegramNotification(String status) {
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.putTelegramNotificationsettings(status);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    long ijnkl = response.body().contentLength();
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    if (response.code() == 200) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void APIGetTelegramNotificationSettings() {
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<TelegramNotificationSettingsResponse> call = apiService.getTelegramSettings();

        call.enqueue(new Callback<TelegramNotificationSettingsResponse>() {
            @Override
            public void onResponse(Call<TelegramNotificationSettingsResponse> call, Response<TelegramNotificationSettingsResponse> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    if (response.code() == 200) {
                        TelegramNotificationSettingsResponse tegrmNotfcnSttngs = response.body();
                        telegramotificationStatus = tegrmNotfcnSttngs.isStatus();
                        botUrl = tegrmNotfcnSttngs.getBotUrl();
                        switch_paytm_notification.setChecked(telegramotificationStatus);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TelegramNotificationSettingsResponse> call, Throwable t) {

            }
        });
    }

    private void showtelegramLaunchAlertDialog() {

        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.show_telegram_launch_dialog);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView iv_close = dialog.findViewById(R.id.iv_close);
        CardView cv_telegram_launch = dialog.findViewById(R.id.cv_telegram_launch);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch_paytm_notification.setChecked(telegramotificationStatus);
            }
        });

        cv_telegram_launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(botUrl); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        dialog.show();

    }
}
