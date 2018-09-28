package com.netvarth.youneverwait.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.database.DatabaseHandler;
import com.netvarth.youneverwait.response.ProfileModel;
import com.netvarth.youneverwait.utils.MyDatePickerDialog;
import com.netvarth.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 10/7/18.
 */

public class EditProfileFragment extends RootFragment {

    Toolbar toolbar;
    ImageView calenderclick;
    static TextInputEditText txtdob;
    TextInputEditText txtfirstname;
    TextInputEditText txtlastname;
    RadioGroup radio_gender;
    String radiogender="";
    Button btn_edtSubmit;
    RadioButton rMale,rFemale;
    TextView tv_phone,tv_email;
    DatabaseHandler db;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View row = inflater.inflate(R.layout.editprofile, container, false);
        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        TextView tv_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv_title.setText("Update Profile");



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        calenderclick = (ImageView) row.findViewById(R.id.calenderclick);
        txtdob = (TextInputEditText) row.findViewById(R.id.edtdob);
        txtfirstname = (TextInputEditText) row.findViewById(R.id.edtFirstName);
        txtlastname = (TextInputEditText) row.findViewById(R.id.edtLastName);
        btn_edtSubmit = (Button) row.findViewById(R.id.btn_edtSubmit);
        radio_gender=(RadioGroup)row.findViewById(R.id.radiogender) ;
        rFemale=(RadioButton)row.findViewById(R.id.radioF) ;
        rMale=(RadioButton)row.findViewById(R.id.radioM) ;
        tv_phone=(TextView)row.findViewById(R.id.txtphone) ;
        tv_email=(TextView)row.findViewById(R.id.txtemail) ;

       mContext=getActivity();

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);



        btn_edtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiEditProfileDetail();
            }
        });
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
        calenderclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyDatePickerDialog();
                newFragment.show(getChildFragmentManager(), "date picker");

            }
        });

        db = new DatabaseHandler(mContext);
        if (Config.isOnline(mContext)) {

            ApiGetProfileDetail();
        } else {

            int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
            ProfileModel getProfile = db.getProfileDetail(consumerId);
            showProfileDetail(getProfile);
        }

        return row;
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
                        if (!db_user.equalsIgnoreCase("success")) {
                            db.insertUserInfo(response.body().getUserprofile());
                        } else {
                            db.updateUserInfo(response.body().getUserprofile());
                        }


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
        txtdob.setText(getProfile.getDob());
        if(!getProfile.getGender().equalsIgnoreCase("")){
            radiogender=getProfile.getGender();
            if(radiogender.equalsIgnoreCase("Male")){
                rMale.setChecked(true);
            }else{
                rFemale.setChecked(true);
            }
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
           /* if(radiogender.equalsIgnoreCase("")){
                radiogender=getGender;
            }*/
            jsonObj.put("gender", radiogender);
            jsonObj.put("dob", txtdob.getText().toString());

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
                        if(response.body().string().equalsIgnoreCase("true")){
                         //   Config.logV("PopBack---------------"+getFragmentManager().getBackStackEntryCount());

                           getFragmentManager().popBackStackImmediate();
                           /* ProfileFragment pfFragment = new ProfileFragment();
                            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.mainlayout, pfFragment).commit();*/
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
                    Config.closeDialog(getActivity(), mDialog);

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

            return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        }

        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        /*Toast.makeText(getActivity(), "selected date is " + view.getYear() +
                                " - " + (view.getMonth() + 1) +
                                " - " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();*/
                         mDate = view.getYear() +
                                "-" + (view.getMonth() + 1) +
                                "-" + view.getDayOfMonth();
                        txtdob.setText(mDate);
                    }
                };
    }
}
