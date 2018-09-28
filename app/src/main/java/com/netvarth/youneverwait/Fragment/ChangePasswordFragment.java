package com.netvarth.youneverwait.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
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
    TextInputEditText edtOldpwd,edtNewpwd,edtconfirmpwd;
   Button mDone;
  //  private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$" ;
  private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9]).{8,}$" ;
    private Pattern pattern;
    private Matcher matcher;

    TextInputLayout txt_InputPwd,txt_Confirm_InputPwd,txt_Old_InputPwd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View row = inflater.inflate(R.layout.changepwd, container, false);

        mContext = getActivity();

        toolbar = (Toolbar) row.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Change Password");

        TextView tv_title = (TextView) toolbar.findViewById(R.id.toolbar_title);

        tv_title.setText("Change Password");
     //   tv_title.setGravity(Gravity.CENTER);

        Typeface tyface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);



        edtOldpwd=(TextInputEditText) row.findViewById(R.id.edt_oldpwd) ;
        edtNewpwd=(TextInputEditText) row.findViewById(R.id.edt_newpwd) ;
        edtconfirmpwd=(TextInputEditText) row.findViewById(R.id.confirmpwd) ;
        edtOldpwd.setTransformationMethod(new PasswordTransformationMethod());
        edtNewpwd.setTransformationMethod(new PasswordTransformationMethod());
        edtconfirmpwd.setTransformationMethod(new PasswordTransformationMethod());


        txt_InputPwd=(TextInputLayout) row.findViewById(R.id.text_input_layout_pwd) ;
        txt_Confirm_InputPwd=(TextInputLayout) row.findViewById(R.id.text_input_layout_pwd_confirm) ;
        txt_Old_InputPwd=(TextInputLayout) row.findViewById(R.id.text_input_old_pwd) ;



        Typeface tyface_edittext = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Montserrat_Regular.otf");
        edtOldpwd.setTypeface(tyface_edittext);
        edtNewpwd.setTypeface(tyface_edittext);
        edtconfirmpwd.setTypeface(tyface_edittext);

        txt_InputPwd.setTypeface(tyface_edittext);
        txt_Confirm_InputPwd.setTypeface(tyface_edittext);
        txt_Old_InputPwd.setTypeface(tyface_edittext);





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

                getFragmentManager().popBackStack();
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
            txt_InputPwd.setError(getString(R.string.err_pwd_valid));
            requestFocus(edtNewpwd);
            return false;
        } else {
            txt_InputPwd.setError(null);
            txt_InputPwd. setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword() {
        if (edtconfirmpwd.getText().toString().trim().isEmpty()) {
            txt_Confirm_InputPwd.setError(getString(R.string.err_msg_password));
            requestFocus(edtconfirmpwd);
            return false;
        } else {
            txt_Confirm_InputPwd.setError(null);
            txt_Confirm_InputPwd. setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOldPassword() {
        if (!validatePwd(edtOldpwd.getText().toString())) {
            txt_Old_InputPwd.setError(getString(R.string.err_pwd_valid));
            requestFocus(edtOldpwd);
            return false;
        } else {
            txt_Old_InputPwd.setError(null);
            txt_Old_InputPwd.setErrorEnabled(false);
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
