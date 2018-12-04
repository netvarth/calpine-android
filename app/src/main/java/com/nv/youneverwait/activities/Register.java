package com.nv.youneverwait.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.utils.LogUtil;
import com.nv.youneverwait.utils.NotificationUtils;
import com.nv.youneverwait.utils.SharedPreference;
import com.nv.youneverwait.utils.TypefaceFont;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by sharmila on 2/7/18.
 */

public class Register extends AppCompatActivity {

    Context mContext;
    TextInputEditText mEdtMobno;
    TextInputLayout txt_InputMob;
    Button btn_reg_submit;
    TextView tv_terms, tv_provider, tv_download;
    /*public static final int RequestPermissionCode = 7;
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]
                {
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE

                }, RequestPermissionCode);

    }*/
    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean WritePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;


                    if (WritePermission && ReadPermission) {
                        Log.v("","Permission Granted-------------");
                    }
                    else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }
    // Checking permission is enabled or not using function starts from here.
    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);


        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED ;

    }*/
   /* private BroadcastReceiver mRegistrationBroadcastReceiver;
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e("", "Firebase reg id: " + regId);


    }*/
    String sforceupdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mEdtMobno = (TextInputEditText) findViewById(R.id.editmobno);
        txt_InputMob = (TextInputLayout) findViewById(R.id.text_input_layout);
        TextView tv_welcome = (TextView) findViewById(R.id.txtwelcome);
        tv_terms = (TextView) findViewById(R.id.txt_terms);

        tv_provider = (TextView) findViewById(R.id.txtProvider);
        tv_download = (TextView) findViewById(R.id.txt_download);

        btn_reg_submit = (Button) findViewById(R.id.reg_submit);
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        tv_welcome.setTypeface(tyface);

        TextView tv_ynw = (TextView) findViewById(R.id.txtynw);
        Typeface tyface_ynw = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_ynw.setTypeface(tyface_ynw);


        mContext = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sforceupdate = extras.getString("forceupdate", "");
        }

        if (sforceupdate != null) {
            if (sforceupdate.equalsIgnoreCase("true")) {

                showForceUpdateDialog();
            }
        }


        /*//GCM REGISTRATION

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();


                }
            }
        };

        displayFirebaseRegId();*/


       /* if(CheckingPermissionIsEnabledOrNot())
        {

        }

        // If, If permission is not enabled then else condition will execute.
        else {

            //Calling method to enable permission.
            RequestMultiplePermission();

        }*/
        LogUtil.writeLogTest("**********************Jaldee***************************");
        mEdtMobno.addTextChangedListener(new MyTextWatcher(mEdtMobno));

        Typeface tyface_edittext = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        mEdtMobno.setTypeface(tyface_edittext);

        Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Light.otf");
        txt_InputMob.setTypeface(tyface_edittext_hint);

        Typeface tyface_btn = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Medium.otf");
        btn_reg_submit.setTypeface(tyface_btn);


        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iterm = new Intent(v.getContext(), TermsOfUse.class);
                mContext.startActivity(iterm);
            }
        });

        String firstWord = "Jaldee ";
        String secondWord = "Terms and Conditions";
        //  <font color='#00AEF2'><b>Terms and Conditions

        Spannable spannable = new SpannableString(firstWord + secondWord);

        Typeface tyface_edittext1 = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Regular.otf");
        Typeface tyface_edittext2 = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");

        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.title_consu)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_terms.setText(spannable);


        String text1 = "Are you a ";
        String text2 = "Provider? ";
        //  <font color='#00AEF2'><b>Terms and Conditions

        Spannable spannable_txt = new SpannableString(text1 + text2);

        spannable_txt.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_txt.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), text1.length(), text1.length() + text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_provider.setText(spannable_txt);


        String text_1 = "Download ";
        String text_2 = "Jaldee Provider App ";
        //  <font color='#00AEF2'><b>Terms and Conditions

        Spannable spannable_txt1 = new SpannableString(text_1 + text_2);
        spannable_txt1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.title_consu)), text_1.length(), text_1.length() + text_2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_txt1.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext1), 0, text_1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable_txt1.setSpan(new CustomTypefaceSpan("sans-serif", tyface_edittext2), text_1.length(), text_1.length() + text_2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_download.setText(spannable_txt1);


    }

    private boolean validatePhone() {
        if (mEdtMobno.getText().toString().trim().isEmpty() || mEdtMobno.getText().toString().length() > 10 || mEdtMobno.getText().toString().length() < 10) {

            SpannableString s = new SpannableString(getString(R.string.err_msg_phone));
            Typeface tyface_edittext_hint = Typeface.createFromAsset(getAssets(),
                    "fonts/Montserrat_Light.otf");
            s.setSpan(new TypefaceFont(tyface_edittext_hint), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txt_InputMob.setErrorEnabled(true);
            txt_InputMob.setError(s);
            //txt_InputMob.setError(getString(R.string.err_msg_phone));
            requestFocus(mEdtMobno);
            return false;
        } else {
            txt_InputMob.setError(null);
            txt_InputMob.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editmobno:
                    validatePhone();
                    break;

            }
        }
    }

    public void ApiMobileExist(String mobileno) {

        ApiInterface apiService =
                ApiClient.getClient(this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(this, this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.chkNewUser(mobileno);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("Response---------------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response123---------------------------");

                        if (response.body().string().equalsIgnoreCase("false")) {
                            SharedPreference.getInstance(mContext).setValue("mobno", mEdtMobno.getText().toString());
                            Intent iReg = new Intent(mContext, Signup.class);
                            startActivity(iReg);
                            //  finish();

                        } else {
                            SharedPreference.getInstance(mContext).setValue("mobno", mEdtMobno.getText().toString());
                            Intent iReg = new Intent(mContext, Login.class);
                            startActivity(iReg);
                            // finish();

                        }


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });

    }

    public void BtnSubmit(View view) {
        String mobno = mEdtMobno.getText().toString();


        if (validatePhone()) {
            ApiMobileExist(mobno);
        }

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }*/

    public void showForceUpdateDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Please update your app");
        alertDialog.setMessage("This app version is not supported any longer. Please update your app from the Play Store.");
        alertDialog.setPositiveButton("UPDATE NOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = mContext.getPackageName();
                try {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        alertDialog.show();
    }


}
