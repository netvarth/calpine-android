package com.jaldeeinc.jaldee.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.JdnResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class JdnFragment extends RootFragment {

    JdnResponse jdnList;
    String jdnDiscount, jdnMaxvalue,jdnNote;
    String uniqueid;

    TextView discount, maxvalue, note;

    public JdnFragment() {

    }

    Context mContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.jdnlist, container, false);
        discount = row.findViewById(R.id.jdn_value);
        maxvalue = row.findViewById(R.id.jdn_maximum_value);
            note = row.findViewById(R.id.jdn_note);

        mContext = getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            uniqueid = bundle.getString("uniqueID", "");
            ApiJDN(uniqueid);

        }

        ImageView iBackPress = row.findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                getFragmentManager().popBackStack();
            }
        });


        TextView tv_title = row.findViewById(R.id.toolbartitle);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        tv_title.setText(R.string.jdnDetails);

        return row;
    }


    private void ApiJDN(String uniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));

        Call<JdnResponse> call = apiService.getJdnList(Integer.parseInt(uniqueID), sdf.format(currentTime));
        call.enqueue(new Callback<JdnResponse>() {
            @Override
            public void onResponse(Call<JdnResponse> call, Response<JdnResponse> response) {
                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL-----1111----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-----detail--------------------" + response.code());

                    if (response.code() == 200) {
                          jdnList = response.body();
//                        jdnDiscount = jdnList.getDiscPercentage();
//                        jdnMaxvalue = jdnList.getDiscMax();
//                        jdnNote = jdnList.getDisplayNote();

                        if (jdnList.getDiscMax() != null && jdnList.getDiscPercentage() != null) {
//                            discount.setText(jdnList.getDiscPercentage() + "%");
//                            maxvalue.setText("₹" + jdnList.getDiscMax());
                            discount.setText("You will get a discount of "+ Config.getAmountNoDecimalPoints(Double.parseDouble(jdnList.getDiscPercentage())) + "%" + " " + "(" + "upto" + " " + "₹" + " "+ Config.getAmountinTwoDecimalPoints(Double.parseDouble(jdnList.getDiscMax())) + ")" + " " + " for every visit.");

                        }
                        if(jdnList.getDisplayNote()!= null){
                            note.setText(jdnList.getDisplayNote());
                        }
                        if(jdnList.getDisplayNote() == null){
                            note.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JdnResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

}
