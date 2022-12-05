package com.jaldeeinc.jaldee.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.SearchAWsResponse;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewSearchFragment extends RootFragment {

    static Context mContext;
    String query, url;
    static String latitude;
    static String longitude;
    String s_LocName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View row = inflater.inflate(R.layout.fragment_searchdetail, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
                query = bundle.getString("query", "");
                url = bundle.getString("url", "");
            latitude = bundle.getString("latitude", "");
            longitude = bundle.getString("longitude", "");
            s_LocName = bundle.getString("locName", "");
        }

        ApiSEARCHAWSLoadFirstDatas("(and location1:['11.06818254054054,75.67389445945945%27,'9.98710145945946,76.75497554054054%27] account_type:1 branch_id:4)", "haversin(10.527642,76.214435,location1.latitude,location1.longitude)");
        return row;
    }


    public void ApiSEARCHAWSLoadFirstDatas(String mQueryPass, String mPass) {


        final ApiInterface apiService =
                ApiClient.getClientAWS(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Map<String, String> query = new HashMap<>();

        query.put("start", "0");
            query.put("q", mQueryPass);

        String mobile = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        if (mobile.startsWith("55")) {
            query.put("fq", "(and  test_account:1 )");
        } else {
            query.put("fq", "(and  (not test_account:1) )");
        }


        Map<String, String> params = new HashMap<>();

        params.put("size", "10");
        params.put("q.parser", "structured");
        params.put("sort", "claimable asc, ynw_verified_level desc, distance asc");
        params.put("expr.distance", mPass);
        params.put("return","_all_fields,distance");

        Call<SearchAWsResponse> call = apiService.getSearchAWS(query, params);


        call.enqueue(new Callback<SearchAWsResponse>() {
            @Override
            public void onResponse(Call<SearchAWsResponse> call, Response<SearchAWsResponse> response) {

                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL--------First-------" + response.raw().request().url().toString().trim());

                    Config.logV("Response--codess-------------------------" + response.code());

                    if (response.code() == 200) {

                        Config.logV("Response--Body AWSssqwe-------------------------" + response);
//                        Config.logV("Response--Body AWSss-------------------------" + new Gson().toJson(response.body()));

                        Config.logV("Status" + response.body().getStatus().getRid());

                        Config.logV("Found @@@@@@@@@@@@@@@@@@" + response.body().getHits().getFound());

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchAWsResponse> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


}
