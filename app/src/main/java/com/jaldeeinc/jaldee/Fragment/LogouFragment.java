package com.jaldeeinc.jaldee.Fragment;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Register;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.service.LocationUpdatesService;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 12/7/18.
 */

public class LogouFragment  extends RootFragment {

    Context mContext;

    Button mBtnyes,mBtnNo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View row = inflater.inflate(R.layout.logout, container, false);

        mContext = getActivity();


        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });

        tv_title.setText("Logout");


        mBtnNo=(Button)row.findViewById(R.id.btnno) ;
        mBtnyes=(Button)row.findViewById(R.id.btnyes) ;
        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/JosefinSans-Bold.ttf");
        mBtnNo.setTypeface(tyface);
        mBtnyes.setTypeface(tyface);
        mBtnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Config.isOnline(mContext)){
                    ApiLogout();
                }
            }
        });

        mBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    getFragmentManager().popBackStackImmediate();

            }
        });
        return row;
    }

    private void ApiLogout() {

        ApiInterface apiService =
                ApiClient.getClient(getActivity()).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ResponseBody> call = apiService.logOut();


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        Config.logV("Response----------------");

                        Toast.makeText(mContext,"Logged out successfully ",Toast.LENGTH_LONG).show();
                        SharedPreference.getInstance(mContext).clear();
                        DatabaseHandler db=new DatabaseHandler(mContext);
                        db.deleteDatabase();
                        // if(response.body().equals("true")) {

                      Intent iLogout=new Intent(mContext, Register.class);
                      startActivity(iLogout);
                       getActivity().finish();
                        //  }


                    }else{

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
