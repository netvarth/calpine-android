package com.jaldeeinc.jaldee.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DatePickerDialog;

import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 10/7/18.
 */

public class EditProfileFragment extends RootFragment  /*implements DatePickerDialog.OnDateSetListener*/ {


    ImageView calenderclick;
    static TextInputEditText txtdob;
    TextInputEditText txtfirstname;
    TextInputEditText txtlastname;
    RadioGroup radio_gender;
    String radiogender = "";
    Button btn_edtSubmit;
    RadioButton rMale, rFemale;
    TextView tv_phone;
    TextInputEditText tv_email;
    DatabaseHandler db;
    Context mContext;
    static SimpleDateFormat simpleDateFormat;
    LinearLayout Llayout;

    private static final String DATE_PATTERN =
            "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View row = inflater.inflate(R.layout.editprofile, container, false);

        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });


        tv_title.setText("Update Profile");

        Llayout = (LinearLayout) row.findViewById(R.id.Llayout);
        calenderclick = (ImageView) row.findViewById(R.id.calenderclick);
        txtdob = (TextInputEditText) row.findViewById(R.id.edtdob);
        txtfirstname = (TextInputEditText) row.findViewById(R.id.edtFirstName);
        txtlastname = (TextInputEditText) row.findViewById(R.id.edtLastName);
        btn_edtSubmit = (Button) row.findViewById(R.id.btn_edtSubmit);
        radio_gender = (RadioGroup) row.findViewById(R.id.radiogender);
        rFemale = (RadioButton) row.findViewById(R.id.radioF);
        rMale = (RadioButton) row.findViewById(R.id.radioM);
        tv_phone = (TextView) row.findViewById(R.id.txtphone);
        tv_email = (TextInputEditText) row.findViewById(R.id.txtemail);

//        txtfirstname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
//        txtlastname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        mContext = getActivity();

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);


        btn_edtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(!txtfirstname.getText().toString().equalsIgnoreCase("")&&!txtlastname.getText().toString().equalsIgnoreCase("")) {
                        if (!txtdob.getText().toString().equalsIgnoreCase("")) {
                            String dateSelected = txtdob.getText().toString().replaceAll("-", "/");
                            Matcher matcher = Pattern.compile(DATE_PATTERN).matcher(dateSelected);
                            if (matcher.matches()) {

                                ApiEditProfileDetail();
                            } else {
                                Toast.makeText(mContext, "Invalid Date!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            ApiEditProfileDetail();
                        }
                    }else{
                        Toast.makeText(mContext, "Please enter your name", Toast.LENGTH_LONG).show();
                    }

            }
        });
        //setupUI(row.findViewById(R.id.Llayout));


        radio_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioM:
                        // do operations specific to this selection
                        radiogender = "male";

                        break;
                    case R.id.radioF:
                        // do operations specific to this selection
                        radiogender = "female";
                        break;

                }
            }
        });

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        calenderclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyDatePickerDialog();
                newFragment.show(getChildFragmentManager(), "date picker");


              /*  if(!txtdob.getText().toString().equalsIgnoreCase("")){

                    Date date = null;
                    try {
                        date = simpleDateFormat.parse(txtdob.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    System.out.println(calendar.get(Calendar.YEAR));
                    System.out.println(calendar.get(Calendar.DAY_OF_MONTH));

                    showDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), R.style.DatePickerSpinner);
                }else{
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    showDate(year, month, day, R.style.DatePickerSpinner);
                }*/


            }
        });

        db = new DatabaseHandler(mContext);
        if (Config.isOnline(mContext)) {

            ApiGetProfileDetail();
        } else {

            int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
            db = new DatabaseHandler(mContext);
            if (db.checkForTables()) {
                ProfileModel getProfile = db.getProfileDetail(consumerId);
                showProfileDetail(getProfile);
            }
        }

        return row;
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(.\\d+)?");
    }

    private void ApiGetProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerId);

        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        Config.logV("Response--BODY-------------------------" + new Gson().toJson(response));
                        Config.logV("Response--first-------------------------" + response.body().getUserprofile().getFirstName());
                        Config.logV("Response--name-------------------------" + response.body().getUserprofile().getLastName());
                        Config.logV("Response--dob-------------------------" + response.body().getUserprofile().getDob());
                        Config.logV("Response--mob-------------------------" + response.body().getUserprofile().getPrimaryMobileNo());
                        String db_user = SharedPreference.getInstance(mContext).getStringValue("userDb", "");
                        //   if (!db_user.equalsIgnoreCase("success")) {

                        db.DeleteProfile();
                        db.insertUserInfo(response.body().getUserprofile());
                        // } /*else {
                        //  db.updateUserInfo(response.body().getUserprofile());

                        SharedPreference.getInstance(mContext).setValue("mobile", response.body().getUserprofile().getPrimaryMobileNo());
                        ProfileModel getProfile = db.getProfileDetail(consumerId);
                        showProfileDetail(getProfile);
                        SharedPreference.getInstance(mContext).setValue("userDb", "success");
                        Config.logV("ProfileModel size-----------" + getProfile);


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private void showProfileDetail(ProfileModel getProfile) {
        if (getProfile.getEmail() != null) {
            if (!getProfile.getEmail().equalsIgnoreCase("")) {
                tv_email.setText(getProfile.getEmail());
            }
        }
        txtfirstname.setText(getProfile.getFirstName());
        txtlastname.setText(getProfile.getLastName());
        tv_phone.setText(getProfile.getPrimaryMobileNo());
        tv_email.setText(getProfile.getEmail());

        String selectedDate = getProfile.getDob();
        if (selectedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date myDate = null;
            try {
                myDate = dateFormat.parse(selectedDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat(
                    "dd-MM-yyyy");
            String finalDate = timeFormat.format(myDate);


            txtdob.setText(finalDate);
        }

        if (getProfile.getGender() != null) {
            if (!getProfile.getGender().equalsIgnoreCase("")) {
                radiogender = getProfile.getGender();
                if (radiogender.equalsIgnoreCase("Male")) {
                    rMale.setChecked(true);
                } else {
                    rFemale.setChecked(true);
                }
            }
        } else {
            radiogender = "";
        }


    }

    private void ApiEditProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(getActivity()).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", consumerId);
            jsonObj.put("firstName", txtfirstname.getText().toString());
            jsonObj.put("lastName", txtlastname.getText().toString());

            jsonObj.put("email", tv_email.getText().toString());
           /* if(radiogender.equalsIgnoreCase("")){
                radiogender=getGender;
            }*/
            jsonObj.put("gender", radiogender);

            String selectedDate = txtdob.getText().toString();

            String finalDate = "";
            if (selectedDate != null && !selectedDate.equalsIgnoreCase("")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd-MM-yyyy");

                Date myDate = null;
                try {
                    myDate = dateFormat.parse(selectedDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                finalDate = timeFormat.format(myDate);
            }

Config.logV("FINAL DATE @@@@@@@@@@@@@@"+finalDate);
            jsonObj.put("dob", finalDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.getEditProfileDetail(body);
        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            //   Config.logV("PopBack---------------"+getFragmentManager().getBackStackEntryCount());

                            SharedPreference.getInstance(mContext).setValue("firstname", txtfirstname.getText().toString());
                            SharedPreference.getInstance(mContext).setValue("lastname", txtlastname.getText().toString());
                            SharedPreference.getInstance(mContext).setValue("email",tv_email.getText().toString());

                            Toast.makeText(mContext, "Profile has been updated successfully ", Toast.LENGTH_LONG).show();
                            getFragmentManager().popBackStackImmediate();
                           /* ProfileFragment pfFragment = new ProfileFragment();
                            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.mainlayout, pfFragment).commit();*/
                        }

                    } else {

                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_LONG).show();

                        Config.logV("Error" + response.errorBody().string());

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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }



/*

    @VisibleForTesting
    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(getActivity())
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .showDaySpinner(true)
                .build()
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        txtdob.setText(simpleDateFormat.format(calendar.getTime()));
    }
*/


    static String mDate;

    public static class MyDatePickerDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {


            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog da = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
            da.getDatePicker().setMaxDate(System.currentTimeMillis());
            return da;
        }

        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                      /*  mDate = view.getYear() +
                                "-" + (view.getMonth() + 1) +
                                "-" + view.getDayOfMonth();
*/
                        Calendar calendar = new GregorianCalendar(year, month, dayOfMonth);
                        txtdob.setText(simpleDateFormat.format(calendar.getTime()));
                        // txtdob.setText(mDate);
                    }


                };
    }
}
