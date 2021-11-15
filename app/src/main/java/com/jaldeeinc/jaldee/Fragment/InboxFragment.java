package com.jaldeeinc.jaldee.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.adapter.InboxAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.InboxModel;
import com.jaldeeinc.jaldee.response.NewInbox;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 6/7/18.
 */

public class InboxFragment extends RootFragment /*implements FragmentInterface*/ {


    public InboxFragment() {
        // Required empty public constructor
    }

    Context mContext;
    Activity mActivity;
    RecyclerView mrRecylce_inboxlist;
    InboxAdapter mInboxAdapter;
    ArrayList<NewInbox> mInboxList = new ArrayList<>();
    DatabaseHandler db;
    ArrayList<InboxModel> mDBInboxList = new ArrayList<>();
    ArrayList<InboxModel> mDBSORTInboxList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    TextView tv_noinbox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Config.logV("Inbox Fragment---------------------");
        View row = inflater.inflate(R.layout.fragment_inbox, container, false);
        mrRecylce_inboxlist = (RecyclerView) row.findViewById(R.id.recylce_inbox);
        tv_noinbox = (TextView) row.findViewById(R.id.txt_noinbox);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mContext = getActivity();
        mActivity = getActivity();
        Home.doubleBackToExitPressedOnce = false;


        linearLayoutManager = new LinearLayoutManager(mContext);
        mrRecylce_inboxlist.setLayoutManager(linearLayoutManager);
        mInboxAdapter = new InboxAdapter(mInboxList, mContext, mActivity, true);
        mrRecylce_inboxlist.setAdapter(mInboxAdapter);


        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);

        iBackPress.setVisibility(View.GONE);

        tv_title.setText("Inbox");

        Typeface tyface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/JosefinSans-Bold.ttf");
        tv_title.setTypeface(tyface);

        return row;

    }

    private void ApiInboxList() {

        final ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<NewInbox>> call = apiService.getChats();
        call.enqueue(new Callback<ArrayList<NewInbox>>() {
            @Override
            public void onResponse(Call<ArrayList<NewInbox>> call, Response<ArrayList<NewInbox>> response) {

                try {

                    if (response.code() == 200) {
                        mInboxList.clear();
                        mInboxList = response.body();

                        if (mInboxList == null) {
                            mInboxList = new ArrayList<>();
                        }

                        if (mInboxList.size() > 0) {

                            tv_noinbox.setVisibility(View.GONE);
                            mrRecylce_inboxlist.setVisibility(View.VISIBLE);
                            linearLayoutManager = new LinearLayoutManager(mContext);
                            mrRecylce_inboxlist.setLayoutManager(linearLayoutManager);
                            mInboxAdapter = new InboxAdapter(mInboxList, mContext, mActivity, false);
                            mrRecylce_inboxlist.setAdapter(mInboxAdapter);

                        } else {

                            tv_noinbox.setVisibility(View.VISIBLE);
                            mrRecylce_inboxlist.setVisibility(View.GONE);

                        }


                    } else {

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<NewInbox>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        ApiInboxList();

    }


    /*@Override
    public void fragmentBecameVisible() {
        Config.logV("OnResume------INBox Frgamrnt VIisble--------------------");


    }*/
}
