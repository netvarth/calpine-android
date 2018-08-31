package com.netvarth.youneverwait.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
 * Created by sharmila on 11/7/18.
 */

public class FamilyMemberFragment extends RootFragment {
    Context mContext;
    Toolbar toolbar;
    ImageView calenderclick;
    static EditText dob;
    EditText edtfirstname,edtmobileno;
    EditText lastname;
    RadioGroup gender;
    String radiogender="";
    Button btn_Save;
    RadioButton rFemale,rMale;
    String ValueCheck="";
    String mPassfname="",mPassLastname="",mPassgender="",mPassDob="",mPassPh="",mUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View row = inflater.inflate(R.layout.fragment_member, container, false);

        mContext = getActivity();

        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(" Members ");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ValueCheck = bundle.getString("familymember", "");

            if(ValueCheck.equalsIgnoreCase("edit")) {


                mPassfname = bundle.getString("firstName", "");

                mPassLastname = bundle.getString("lastname", "");
                mPassDob = bundle.getString("dob", "");
                mPassgender = bundle.getString("gender", "");
                mPassPh= bundle.getString("mobile", "");
                mUser=bundle.getString("user", "");

                Config.logV("Dob--1111----------"+mPassDob);
            }
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here

            }
        });
        calenderclick = (ImageView) row.findViewById(R.id.calenderclick);
        dob = (EditText) row.findViewById(R.id.edtdob);
        edtfirstname = (EditText) row.findViewById(R.id.edtFirstName1);
        lastname = (EditText) row.findViewById(R.id.edtLastName);
        btn_Save = (Button) row.findViewById(R.id.btn_save);
        gender=(RadioGroup)row.findViewById(R.id.radiogender) ;
        rFemale=(RadioButton)row.findViewById(R.id.radioF) ;
        rMale=(RadioButton)row.findViewById(R.id.radioM) ;
        edtmobileno=(EditText) row.findViewById(R.id.edtmobileno);
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
                Config.logV("Check-------------"+ValueCheck);
                if(ValueCheck.equalsIgnoreCase("profile")){
                    Config.logV("SUcesss------------");
                    ApiAddFamilyMember();


                }else {
                    ApiUpdateFamilyMember(mUser);
                }
            }
        });
        if(ValueCheck.equalsIgnoreCase("edit")) {
            edtfirstname.setText(mPassfname);
            lastname.setText(mPassLastname);

            if(!mPassgender.equalsIgnoreCase("")) {
               if(mPassgender.equalsIgnoreCase("male")) {
                    rMale.setChecked(true);
                }else{
                    rFemale.setChecked(true);
                }
            }

            if(!mPassDob.equalsIgnoreCase("")) {
                dob.setText(mPassDob);
            }
            if(!mPassPh.equalsIgnoreCase("")) {
                edtmobileno.setText(mPassPh);
            }
        }
        return row;
    }
    private void ApiUpdateFamilyMember(String mUser) {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

      //  final int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject userProfile=new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {
            if(!edtmobileno.getText().toString().equalsIgnoreCase("")&&edtmobileno.getText().toString()!=null) {
                jsonObj.put("primaryMobileNo", edtmobileno.getText().toString());
            }
            jsonObj.put("firstName", edtfirstname.getText().toString());
            jsonObj.put("lastName", lastname.getText().toString());

            if(!radiogender.equalsIgnoreCase("")&&radiogender!=null) {
                jsonObj.put("gender", radiogender);
            }
            if(!mDate.equalsIgnoreCase("")&&mDate!=null) {
                jsonObj.put("dob", mDate);
            }

            if(!edtmobileno.getText().toString().equalsIgnoreCase("")&&edtmobileno.getText().toString()!=null) {
                jsonObj.put("countryCode", "+91");
            }

            userProfile.putOpt("userProfile",jsonObj);
            userProfile.put("user",mUser);
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
                        getFragmentManager().popBackStackImmediate();


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
        JSONObject userProfile=new JSONObject();
        JSONObject jsonObj = new JSONObject();
        try {
            if(!edtmobileno.getText().toString().equalsIgnoreCase("")&&edtmobileno.getText().toString()!=null) {
                jsonObj.put("primaryMobileNo", edtmobileno.getText().toString());
            }
            jsonObj.put("firstName", edtfirstname.getText().toString());
            jsonObj.put("lastName", lastname.getText().toString());

            if(!radiogender.equalsIgnoreCase("")&&radiogender!=null) {
                jsonObj.put("gender", radiogender);
            }
            if(!mDate.equalsIgnoreCase("")&&mDate!=null) {
                jsonObj.put("dob", mDate);
            }

            if(!edtmobileno.getText().toString().equalsIgnoreCase("")&&edtmobileno.getText().toString()!=null) {
                jsonObj.put("countryCode", "+91");
            }

            userProfile.putOpt("userProfile",jsonObj);
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

                        getFragmentManager().popBackStackImmediate();
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
    static String mDate="";
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
                       /* Toast.makeText(getActivity(), "selected date is " + view.getYear() +
                                " / " + (view.getMonth() + 1) +
                                " / " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();
                       */
                        mDate = view.getYear() +
                                "-" + (view.getMonth() + 1) +
                                "-" + view.getDayOfMonth();

                        dob.setText(mDate);
                    }
                };
    }
}
