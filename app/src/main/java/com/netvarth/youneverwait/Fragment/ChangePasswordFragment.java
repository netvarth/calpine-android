package com.netvarth.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.activities.Password;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.database.DatabaseHandler;
import com.netvarth.youneverwait.response.ProfileModel;
import com.netvarth.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

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

public class ChangePasswordFragment extends RootFragment {

    Context mContext;
    Toolbar toolbar;
   EditText edtOldpwd,edtNewpwd,edtconfirmpwd;
   Button mDone;
  //  private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$" ;
  private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9]).{8,}$" ;
    private Pattern pattern;
    private Matcher matcher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View row = inflater.inflate(R.layout.changepwd, container, false);

        mContext = getActivity();

        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Change Password");

        edtOldpwd=(EditText) row.findViewById(R.id.edt_oldpwd) ;
        edtNewpwd=(EditText) row.findViewById(R.id.edt_newpwd) ;
        edtconfirmpwd=(EditText) row.findViewById(R.id.confirmpwd) ;
        mDone=(Button)row.findViewById(R.id.btndone) ;
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Config.isOnline(mContext)){
                    if(validatePassword()&&validateConfirmPassword()&&validateOldPassword()) {
                        if (edtNewpwd.getText().toString().equalsIgnoreCase(edtconfirmpwd.getText().toString())) {
                            ApiChangePwd(edtOldpwd.getText().toString(), edtNewpwd.getText().toString());
                        }
                    }else{
                        Toast.makeText(getActivity(),"Password and Confirm Password do not match",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here

            }
        });

        edtOldpwd.addTextChangedListener(new MyTextWatcher(edtOldpwd));
        edtNewpwd.addTextChangedListener(new MyTextWatcher(edtNewpwd));
        edtconfirmpwd.addTextChangedListener(new MyTextWatcher(edtconfirmpwd));
        pattern = Pattern.compile(PASSWORD_PATTERN);

        return row;
    }
    public boolean validatePwd(final String password){

        matcher = pattern.matcher(password);
        return matcher.matches();

    }
    private boolean validatePassword() {
        if (!validatePwd(edtNewpwd.getText().toString())) {
            edtNewpwd.setError(getString(R.string.err_pwd_valid));
            requestFocus(edtNewpwd);
            return false;
        } else {
            edtNewpwd.setError(null);
        }

        return true;
    }

    private boolean validateConfirmPassword() {
        if (edtconfirmpwd.getText().toString().trim().isEmpty()) {
            edtconfirmpwd.setError(getString(R.string.err_msg_password));
            requestFocus(edtconfirmpwd);
            return false;
        } else {
            edtconfirmpwd.setError(null);
        }

        return true;
    }

    private boolean validateOldPassword() {
        if (edtOldpwd.getText().toString().trim().isEmpty()) {
            edtOldpwd.setError(getString(R.string.err_msg_password));
            requestFocus(edtOldpwd);
            return false;
        } else {
            edtOldpwd.setError(null);
        }

        return true;
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
                case R.id.edt_oldpwd:
                    validateOldPassword();
                    break;
                case R.id.edt_newpwd:
                    validatePassword();
                    break;
                case R.id.confirmpwd:
                    validateConfirmPassword();
                    break;

            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private void ApiChangePwd(String mOldPwd, String mNewpwd) {


        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("oldpassword", mOldPwd);
            jsonObj.put("password",mNewpwd);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.ChangePwd(body);
        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    //Config.logV("Request--BODY-------------------------" + new Gson().toJson(response.body().string()));
                    if (response.code() == 200) {
                        Config.logV("Response----------------");

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
}
