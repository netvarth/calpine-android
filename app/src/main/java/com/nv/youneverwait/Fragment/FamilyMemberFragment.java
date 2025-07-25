package com.nv.youneverwait.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.utils.SharedPreference;
import com.nv.youneverwait.utils.TypefaceFont;

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

/**
 * Created by sharmila on 11/7/18.
 */

public class FamilyMemberFragment extends RootFragment {
    Context mContext;
    ImageView calenderclick;
    static EditText dob;
    TextInputEditText edtfirstname, edtmobileno;
    TextInputEditText lastname;
    RadioGroup gender;
    String radiogender = "";
    Button btn_Save;
    RadioButton rFemale, rMale;
    String ValueCheck = "";
    String mPassfname = "", mPassLastname = "", mPassgender = "", mPassDob = "", mPassPh = "", mUser;
    TextInputLayout text_input_lastname, text_input_firstname;
    TextInputLayout txt_InputMob;

    private static final String DATE_PATTERN =
            "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View row = inflater.inflate(R.layout.fragment_member, container, false);

        mContext = getActivity();

        Config.logV("Add mEmber------------------------------");

        //  ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });
        txt_InputMob = (TextInputLayout) row.findViewById(R.id.text_input_mobno);

        tv_title.setText("Add Members");
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");


        tv_title.setTypeface(tyface);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ValueCheck = bundle.getString("familymember", "");

            if (ValueCheck.equalsIgnoreCase("edit")) {


                mPassfname = bundle.getString("firstName", "");
                mPassLastname = bundle.getString("lastname", "");
                mPassDob = bundle.getString("dob", "");
                mPassgender = bundle.getString("gender", "");
                mPassPh = bundle.getString("mobile", "");
                mUser = bundle.getString("user", "");

                Config.logV("Dob--1111----------" + mPassDob);
            }
        }

        //setupUI(row.findViewById(R.id.LMlayout));

        calenderclick = (ImageView) row.findViewById(R.id.calenderclick);
        dob = (EditText) row.findViewById(R.id.edtdob);

        text_input_lastname = (TextInputLayout) row.findViewById(R.id.text_input_lastname);

        text_input_firstname = (TextInputLayout) row.findViewById(R.id.text_input_firstname);


        edtfirstname = (TextInputEditText) row.findViewById(R.id.edtFirstName1);
        lastname = (TextInputEditText) row.findViewById(R.id.edtLastName);
        btn_Save = (Button) row.findViewById(R.id.btn_save);
        gender = (RadioGroup) row.findViewById(R.id.radiogender);
        rFemale = (RadioButton) row.findViewById(R.id.radioF);
        rMale = (RadioButton) row.findViewById(R.id.radioM);
        edtmobileno = (TextInputEditText) row.findViewById(R.id.edtmobileno);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        calenderclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyDatePickerDialog();
                newFragment.show(getChildFragmentManager(), "date picker");

            }
        });

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Check-------------" + ValueCheck);
                if (ValueCheck.equalsIgnoreCase("profile")) {
                    Config.logV("SUcesss------------");
                    if (validateCHeck()) {
                        if(edtmobileno.length()>0){
                            if(validatePhone()){
                                ApiAddFamilyMember();
                            }
                        }else if(!dob.getText().toString().equalsIgnoreCase("")) {

                            String dateSelected = dob.getText().toString().replaceAll("-", "/");
                            Config.logV("date Selected @@@@@@@@@@@@@@"+dateSelected);

                            Matcher matcher = Pattern.compile(DATE_PATTERN).matcher(dateSelected);
                            if (matcher.matches()) {

                                ApiAddFamilyMember();
                            } else {
                                Toast.makeText(mContext, "Invalid Date!", Toast.LENGTH_LONG).show();

                            }
                        }
                        else
                        {
                            ApiAddFamilyMember();
                        }

                    }


                } else {


                    if (validateCHeck()) {
                        if(edtmobileno.length()>0){
                            if(validatePhone()){
                                ApiUpdateFamilyMember(mUser);
                            }
                        }else if(!dob.getText().toString().equalsIgnoreCase("")) {

                            String dateSelected = dob.getText().toString().replaceAll("-", "/");
                            Config.logV("date Selected @@@@@@@@@@@@@@"+dateSelected);
                            Matcher matcher = Pattern.compile(DATE_PATTERN).matcher(dateSelected);
                            if (matcher.matches()) {

                                ApiUpdateFamilyMember(mUser);
                            } else {
                                Toast.makeText(mContext, "Invalid Date!", Toast.LENGTH_LONG).show();

                            }
                        }else{
                            ApiUpdateFamilyMember(mUser);
                        }

                    }



                }
            }
        });
        if (ValueCheck.equalsIgnoreCase("edit")) {
            edtfirstname.setText(mPassfname);
            lastname.setText(mPassLastname);

            if (!mPassgender.equalsIgnoreCase("")) {
                if (mPassgender.equalsIgnoreCase("male")) {
                    rMale.setChecked(true);
                } else {
                    rFemale.setChecked(true);
                }
            }

            if (!mPassDob.equalsIgnoreCase("")) {
                Config.logV("Date GEt @@@@@@@@@@"+mPassDob);
                /*SimpleDateFormat timeFormat = new SimpleDateFormat(
                        "dd-MM-yyyy");
                String finalDate = timeFormat.format(mPassDob);
                dob.setText(finalDate);*/

                String selectedDate = mPassDob;
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


                    dob.setText(finalDate);
                }

            }
            if (!mPassPh.equalsIgnoreCase("")) {
                edtmobileno.setText(mPassPh);
            }
        }
        return row;
    }
    private boolean validatePhone() {
        if ( edtmobileno.getText().toString().length() > 10 || edtmobileno.getText().toString().length() < 10) {

            SpannableString s = new SpannableString(getString(R.string.err_msg_phone));
            txt_InputMob. setErrorEnabled(true);
            txt_InputMob.setError(s);
            //txt_InputMob.setError(getString(R.string.err_msg_phone));
            return false;
        } else {
            txt_InputMob.setError(null);
            txt_InputMob. setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateCHeck() {
        if (edtfirstname.getText().toString().equalsIgnoreCase("") && lastname.getText().toString().equalsIgnoreCase("")) {
            text_input_firstname.setError("Please enter valid first name");
            text_input_lastname.setError("Please enter valid last name");
            return false;
        } else if (lastname.getText().toString().equalsIgnoreCase("")) {
            text_input_lastname.setError("Please enter valid last name");
            text_input_firstname.setErrorEnabled(false);
            text_input_firstname.setError(null);
            return false;
        } else if (edtfirstname.getText().toString().equalsIgnoreCase("")) {
            text_input_firstname.setError("Please enter valid first name");
            text_input_lastname.setErrorEnabled(false);
            text_input_lastname.setError(null);
            return false;
        } else {
            text_input_firstname.setErrorEnabled(false);
            text_input_firstname.setError(null);
            text_input_lastname.setErrorEnabled(false);
            text_input_lastname.setError(null);
        }

        return true;
    }

    private void ApiUpdateFamilyMember(String mUser) {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        //  final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject userProfile = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {
            if (!edtmobileno.getText().toString().equalsIgnoreCase("") && edtmobileno.getText().toString() != null) {
                jsonObj.put("primaryMobileNo", edtmobileno.getText().toString());
            }
            jsonObj.put("firstName", edtfirstname.getText().toString());
            jsonObj.put("lastName", lastname.getText().toString());

            if (!radiogender.equalsIgnoreCase("") && radiogender != null) {
                jsonObj.put("gender", radiogender);
            }
            if (!mDate.equalsIgnoreCase("") && mDate != null&&!dob.getText().toString().equalsIgnoreCase("")) {
                jsonObj.put("dob", mDate);
            }

            if (!edtmobileno.getText().toString().equalsIgnoreCase("") && edtmobileno.getText().toString() != null) {
                jsonObj.put("countryCode", "+91");
            }

            userProfile.putOpt("userProfile", jsonObj);
            userProfile.put("user", mUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userProfile.toString());
        Call<ResponseBody> call = apiService.UpdateFamilyMEmber(body);
        Config.logV("Request--BODY-------------------------" + new Gson().toJson(userProfile.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Request--BODY-------------------------" + new Gson().toJson(response.body()));
                    if (response.code() == 200) {


                        Toast.makeText(mContext, "Member updated successfully ", Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStack();

                      /*  Bundle bundle = new Bundle();
                        bundle.putString("refersh", "update");

                        FamilyListFragment pfFragment = new FamilyListFragment();

                        pfFragment.setArguments(bundle);
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        // Store the Fragment in stack
                        // transaction.addToBackStack(null);
                        transaction.replace(R.id.mainlayout, pfFragment).commit();*/
                    }else{
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_LONG).show();
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

    private void ApiAddFamilyMember() {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(getActivity()).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject userProfile = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {
            if (!edtmobileno.getText().toString().equalsIgnoreCase("") && edtmobileno.getText().toString() != null) {
                jsonObj.put("primaryMobileNo", edtmobileno.getText().toString());
            }
            jsonObj.put("firstName", edtfirstname.getText().toString());
            jsonObj.put("lastName", lastname.getText().toString());

            if (!radiogender.equalsIgnoreCase("") && radiogender != null) {
                jsonObj.put("gender", radiogender);
            }
            if (!mDate.equalsIgnoreCase("") && mDate != null&&!dob.getText().toString().equalsIgnoreCase("")) {
                jsonObj.put("dob", mDate);
            }

            if (!edtmobileno.getText().toString().equalsIgnoreCase("") && edtmobileno.getText().toString() != null) {
                jsonObj.put("countryCode", "+91");
            }

            userProfile.putOpt("userProfile", jsonObj);
            //  userProfile.put("parent",consumerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userProfile.toString());
        Call<ResponseBody> call = apiService.AddFamilyMEmber(body);
        Config.logV("Request--BODY-------------------------" + new Gson().toJson(userProfile.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Request--BODY-------------------------" + new Gson().toJson(response.body()));
                    if (response.code() == 200) {

                        // if(response.body().string().equalsIgnoreCase("true")) {
                        Toast.makeText(mContext, "Member added successfully ", Toast.LENGTH_LONG).show();
                        getFragmentManager().popBackStackImmediate();
                        // }

                       /* Bundle bundle = new Bundle();
                        bundle.putString("refersh", "update");

                        FamilyListFragment pfFragment = new FamilyListFragment();

                        pfFragment.setArguments(bundle);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        // Store the Fragment in stack
                        //transaction.addToBackStack(null);
                        transaction.replace(R.id.mainlayout, pfFragment).commit();*/
                    }else{
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_LONG).show();
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

    static String mDate = "";

    public static class MyDatePickerDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog  da = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
            da.getDatePicker().setMaxDate(System.currentTimeMillis());
            return  da;
        }

        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                       /* Toast.makeText(getActivity(), "selected date is " + view.getYear() +
                                " / " + (view.getMonth() + 1) +
                                " / " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();
                       */
                        /*mDate = view.getYear() +
                                "-" + (view.getMonth() + 1) +
                                "-" + view.getDayOfMonth();*/

                        mDate = view.getYear() +
                                "-" + (view.getMonth() + 1) +
                                "-" + view.getDayOfMonth();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                        Calendar calendar = new GregorianCalendar(year, month, day);

                        dob.setText(simpleDateFormat.format(calendar.getTime()));

                    }
                };
    }

    /*public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof TextInputEditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }*/
}
