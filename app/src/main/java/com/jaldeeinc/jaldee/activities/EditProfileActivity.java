package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.jaldeeinc.jaldee.Fragment.EditProfileFragment;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    ImageView calenderclick;
    static TextInputEditText txtdob;
    TextInputEditText txtfirstname;
    TextInputEditText txtlastname;
    RadioGroup radio_gender;
    String radiogender = "";
    Button btn_edtSubmit;
    RadioButton rMale, rFemale, rOther;
    TextView tv_phone;
    TextInputEditText tv_email;
    DatabaseHandler db;
    Context mContext;
    static SimpleDateFormat simpleDateFormat;
    LinearLayout Llayout;
    String countryCode = "";
    static EditText edtTelegramNumber, edtWhtsAppNumber;
    CountryCodePicker WhtsappCCodePicker, TelegramCCodePicker;

    private static final String DATE_PATTERN =
            "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        TextView tv_title = (TextView) findViewById(R.id.toolbartitle);

        ImageView iBackPress = (ImageView) findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here

                finish();
            }
        });


        tv_title.setText("Update Profile");

        Llayout = (LinearLayout) findViewById(R.id.Llayout);
        calenderclick = (ImageView) findViewById(R.id.calenderclick);
        txtdob = (TextInputEditText) findViewById(R.id.edtdob);
        txtfirstname = (TextInputEditText) findViewById(R.id.edtFirstName);
        txtlastname = (TextInputEditText) findViewById(R.id.edtLastName);
        btn_edtSubmit = (Button) findViewById(R.id.btn_edtSubmit);
        radio_gender = (RadioGroup) findViewById(R.id.radiogender);
        rFemale = (RadioButton) findViewById(R.id.radioF);
        rMale = (RadioButton) findViewById(R.id.radioM);
        rOther = (RadioButton) findViewById(R.id.radioOthr);
        tv_phone = (TextView) findViewById(R.id.txtphone);
        tv_email = (TextInputEditText) findViewById(R.id.txtemail);
        WhtsappCCodePicker = (CountryCodePicker) findViewById(R.id.Wccp);
        TelegramCCodePicker = (CountryCodePicker) findViewById(R.id.Tccp);
        edtWhtsAppNumber = findViewById(R.id.edtWhtsAppNumber);
        edtTelegramNumber = findViewById(R.id.edtTelegram);

//        txtfirstname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
//        txtlastname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        mContext = EditProfileActivity.this;

        countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");


        btn_edtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!txtfirstname.getText().toString().equalsIgnoreCase("") && !txtlastname.getText().toString().equalsIgnoreCase("")) {
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
                } else {
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
                    case R.id.radioOthr:
                        // do operations specific to this selection
                        radiogender = "other";
                        break;

                }
            }
        });

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        calenderclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new EditProfileActivity.MyDatePickerDialog();
                newFragment.show(getSupportFragmentManager(), "date picker");

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
    }


    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(.\\d+)?");
    }

    private void ApiGetProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(EditProfileActivity.this).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerId);

        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(EditProfileActivity.this, mDialog);

                    if (response.code() == 200) {

                        if (response.body() != null) {

                            String db_user = SharedPreference.getInstance(mContext).getStringValue("userDb", "");
                            db.DeleteProfile();
                            db.insertUserInfo(response.body().getUserprofile());

                            SharedPreference.getInstance(mContext).setValue("mobile", response.body().getUserprofile().getPrimaryMobileNo());
                            ProfileModel getProfile = db.getProfileDetail(consumerId);
                            showProfileDetail(getProfile);
                            SharedPreference.getInstance(mContext).setValue("userDb", "success");
                            Config.logV("ProfileModel size-----------" + getProfile);
                        }

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
                    Config.closeDialog(EditProfileActivity.this, mDialog);

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
        tv_phone.setText(countryCode + " " + getProfile.getPrimaryMobileNo());
        tv_email.setText(getProfile.getEmail());

        if (getProfile.getWhtsAppCountryCode() != null && !getProfile.getWhtsAppCountryCode().equalsIgnoreCase("")) {
            WhtsappCCodePicker.setCountryForPhoneCode(Integer.parseInt(getProfile.getWhtsAppCountryCode().replace("+", "")));
            edtWhtsAppNumber.setText(getProfile.getWhtsAppNumber());
        }
        if (getProfile.getTelgrmCountryCode() != null && !getProfile.getTelgrmCountryCode().equalsIgnoreCase("")) {
            TelegramCCodePicker.setCountryForPhoneCode(Integer.parseInt(getProfile.getTelgrmCountryCode().replace("+", "")));
            edtTelegramNumber.setText(getProfile.getTelgrmNumber());
        }
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
                } else if (radiogender.equalsIgnoreCase("Female")) {
                    rFemale.setChecked(true);
                } else if (radiogender.equalsIgnoreCase("Other")) {
                    rOther.setChecked(true);
                }
            }
        } else {
            radiogender = "";
        }


    }

    private void ApiEditProfileDetail() {


        ApiInterface apiService =
                ApiClient.getClient(EditProfileActivity.this).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(EditProfileActivity.this).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonObj1 = new JSONObject();
        JSONObject jsonObj2 = new JSONObject();
        try {
            jsonObj.put("id", consumerId);
            jsonObj.put("firstName", txtfirstname.getText().toString());
            jsonObj.put("lastName", txtlastname.getText().toString());

            jsonObj.put("email", tv_email.getText().toString());
            if (edtWhtsAppNumber.getText() != null && !edtWhtsAppNumber.getText().toString().isEmpty()) {
                jsonObj1.put("countryCode", WhtsappCCodePicker.getSelectedCountryCodeWithPlus());
                jsonObj1.put("number", edtWhtsAppNumber.getText());
                jsonObj.putOpt("whatsAppNum", jsonObj1);
            }
            if (edtTelegramNumber.getText() != null && !edtTelegramNumber.getText().toString().isEmpty()) {
                jsonObj2.put("countryCode", TelegramCCodePicker.getSelectedCountryCodeWithPlus());
                jsonObj2.put("number", edtTelegramNumber.getText());
                jsonObj.putOpt("telegramNum", jsonObj2);
            }
           /* if(radiogender.equalsIgnoreCase("")){
                radiogender=getGender;
            }*/

            if (radio_gender.getCheckedRadioButtonId() != -1) {
                jsonObj.put("gender", radiogender);
            }
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

            Config.logV("FINAL DATE @@@@@@@@@@@@@@" + finalDate);
            jsonObj.put("dob", finalDate);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.getEditProfileDetail(body);
//        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(EditProfileActivity.this, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {

                            SharedPreference.getInstance(mContext).setValue("firstname", txtfirstname.getText().toString());
                            SharedPreference.getInstance(mContext).setValue("lastname", txtlastname.getText().toString());
                            SharedPreference.getInstance(mContext).setValue("email", tv_email.getText().toString());

                            Toast.makeText(mContext, "Profile has been updated successfully ", Toast.LENGTH_LONG).show();
                            finish();
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
                    Config.closeDialog(EditProfileActivity.this, mDialog);

            }
        });


    }


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