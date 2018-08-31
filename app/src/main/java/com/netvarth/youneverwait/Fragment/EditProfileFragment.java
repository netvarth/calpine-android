package com.netvarth.youneverwait.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
    static EditText dob;
    EditText firstname;
    EditText lastname;
    RadioGroup gender;
    String radiogender="";
    Button btn_edtSubmit;
    String getfirstname, getlastname, phone, email, getdob,getGender;
    RadioButton rMale,rFemale;
    TextView txtph,tv_email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View row = inflater.inflate(R.layout.editprofile, container, false);
        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        calenderclick = (ImageView) row.findViewById(R.id.calenderclick);
        dob = (EditText) row.findViewById(R.id.edtdob);
        firstname = (EditText) row.findViewById(R.id.edtFirstName);
        lastname = (EditText) row.findViewById(R.id.edtLastName);
        btn_edtSubmit = (Button) row.findViewById(R.id.btn_edtSubmit);
        gender=(RadioGroup)row.findViewById(R.id.radiogender) ;
        rFemale=(RadioButton)row.findViewById(R.id.radioF) ;
        rMale=(RadioButton)row.findViewById(R.id.radioM) ;
        txtph=(TextView)row.findViewById(R.id.txtphone) ;
        tv_email=(TextView)row.findViewById(R.id.txtemail) ;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            getfirstname = bundle.getString("firstname", "");
            getlastname = bundle.getString("lastname", "");
            phone = bundle.getString("phone", "");
            email = bundle.getString("email", "");
            getdob = bundle.getString("dob", "");
            getGender = bundle.getString("gender", "");


        }
        if(!getGender.equalsIgnoreCase("")){
           if( getGender.equalsIgnoreCase("male") ){
                rMale.setChecked(true);
            }else{
                rFemale.setChecked(true);
            }
        }

        firstname.setText(getfirstname);
        lastname.setText(getlastname);
        txtph.setText(phone);
        dob.setText(getdob);
        if(email!=null){
          tv_email.setText(email);
        }


        btn_edtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiEditProfileDetail();
            }
        });
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

        return row;
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
            jsonObj.put("firstName", firstname.getText().toString());
            jsonObj.put("lastName", lastname.getText().toString());
            if(radiogender.equalsIgnoreCase("")){
                radiogender=getGender;
            }
            jsonObj.put("gender", radiogender);
            jsonObj.put("dob", dob.getText().toString());

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
                        dob.setText(mDate);
                    }
                };
    }
}
