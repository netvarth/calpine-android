package com.nv.youneverwait.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.Home;
import com.nv.youneverwait.adapter.InboxAdapter;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.database.DatabaseHandler;
import com.nv.youneverwait.response.InboxModel;
import com.nv.youneverwait.utils.SharedPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 6/7/18.
 */

public class InboxFragment extends RootFragment /*implements FragmentInterface*/{



    public InboxFragment() {
        // Required empty public constructor
    }

    Context mContext;
    Activity mActivity;
    RecyclerView mrRecylce_inboxlist;
    InboxAdapter mInboxAdapter;
    ArrayList<InboxModel> mInboxList = new ArrayList<>();
    DatabaseHandler db;
    ArrayList<InboxModel> mDBInboxList = new ArrayList<>();


    ArrayList<InboxModel> mDBSORTInboxList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Config.logV("Inbox Fragment---------------------");
        View row = inflater.inflate(R.layout.fragment_inbox, container, false);
        mrRecylce_inboxlist = (RecyclerView) row.findViewById(R.id.recylce_inbox);
        mContext = getActivity();
        Home.doubleBackToExitPressedOnce=false;
        if (Config.isOnline(getActivity())) {
            ApiInboxList();
        }else{
            mDBSORTInboxList.clear();
            db=new DatabaseHandler(mContext);
            mDBSORTInboxList = db.getAllInboxDetail();
            if(mDBSORTInboxList.size()>0) {

                Collections.sort(mDBSORTInboxList, new Comparator<InboxModel>() {
                    @Override
                    public int compare(InboxModel r1, InboxModel r2) {
                        return new Long(r2.getTimeStamp()).compareTo(new Long(r1.getTimeStamp()));
                    }
                });

                //  Config.logV("INBOX LIST----------------" + mDBInboxList.size());

                Config.logV("INBOX LIST------NEW----------" + mDBSORTInboxList.size());

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                mrRecylce_inboxlist.setLayoutManager(mLayoutManager);
                mInboxAdapter = new InboxAdapter(mDBSORTInboxList, mContext, mActivity, mDBInboxList);
                mrRecylce_inboxlist.setAdapter(mInboxAdapter);
                mInboxAdapter.notifyDataSetChanged();
            }
        }



        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        ImageView iBackPress=(ImageView)row.findViewById(R.id.backpress) ;

        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);


        iBackPress.setVisibility(View.GONE);


        tv_title.setText("Inbox");

        Typeface tyface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);

        return row;

    }

    private void ApiInboxList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<InboxModel>> call = apiService.getMessage();
        final Dialog mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        final HashMap<String, List<InboxModel>> messagesMap = new HashMap<String, List<InboxModel>>();


        call.enqueue(new Callback<ArrayList<InboxModel>>() {
            @Override
            public void onResponse(Call<ArrayList<InboxModel>> call, Response<ArrayList<InboxModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mInboxList.clear();
                        mInboxList = response.body();


                        /*Collections.sort(mInboxList, new Comparator<InboxModel>() {
                            @Override
                            public int compare(InboxModel r1, InboxModel r2) {
                                return new Long(r2.getTimeStamp()).compareTo(new Long(r1.getTimeStamp()));
                            }
                        });*/

                        mDBInboxList.clear();
                        mDBSORTInboxList.clear();
                        db = new DatabaseHandler(mContext);
                        Config.logV("INBOX SIZE_------------------"+mInboxList.size());
                       /* if (Config.isOnline(mContext)) {*/
                        if (mInboxList.size() > 0) {
                            db.DeleteInbox();

                            for (int i = 0; i < mInboxList.size(); i++) {

                                int activeConsumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);


                                String senderName = String.valueOf(mInboxList.get(i).getOwner().getUserName()).toLowerCase().trim();

                                int senderId = mInboxList.get(i).getOwner().getId();
                                String messageStatus = "in";

                                if (senderId == activeConsumerId) {

                                    senderId = mInboxList.get(i).getReceiver().getReceiverId();
                                    senderName = String.valueOf(mInboxList.get(i).getReceiver().getReceiverName()).toLowerCase().trim();
                                  //  Config.logV("SenderID--1111----------" + senderId);
                                    messageStatus = "out";
                                }

                                String senderKey = senderId + "_" + senderName;
                                InboxModel inbox = new InboxModel();
                                inbox.setTimeStamp(mInboxList.get(i).getTimeStamp());
                                inbox.setUserName(senderName);
                                inbox.setService(mInboxList.get(i).getService());
                                inbox.setMsg(mInboxList.get(i).getMsg());
                                inbox.setId(mInboxList.get(i).getOwner().getId());
                                inbox.setWaitlistId(mInboxList.get(i).getWaitlistId());
                                inbox.setMessageStatus(messageStatus);
                                inbox.setUniqueID(senderKey);
                               // mDBInboxList.add(inbox);

                                db.insertInboxModel(inbox);
                            }

                        }
                        /*if(db.CheckIsDataAlreadyInDBorNot()){

                            Config.logV("UPDATE INBOX LIST----------------"+mInboxList.size());
                            db.updateInboxInfo(mInboxList);

                        }else {

                            Config.logV("INSERT INBOX LIST----------------"+mInboxList.size());
                            db.insertInbox(mInboxList);
                        }*/

                        mDBSORTInboxList = db.getAllInboxDetail();


                        /*for (int i = 0; i < mDBInboxList.size(); i++) {

                            int activeConsumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

                            String senderName = String.valueOf(mDBInboxList.get(i).getUserName()).toLowerCase();

                            int senderId = mDBInboxList.get(i).getId();
                            String messageStatus = "in";

                            if (senderId == activeConsumerId) {

                                senderId = mDBInboxList.get(i).getReceiverId();
                                senderName = String.valueOf(mDBInboxList.get(i).getReceiverName()).toLowerCase();
                              *//*  Config.logV("Receiver ID-----------"+senderId);
                                Config.logV("Receiver NAme-----------"+senderName);*//*
                                Config.logV("SenderID--1111----------" + senderId);
                                messageStatus = "out";
                            }

                            Config.logV("SenderID------------" + senderId);
                            String senderKey = senderId + "_" + senderName;

                            if (messagesMap.containsKey(senderKey)) {

                                InboxModel inbox = new InboxModel();
                                inbox.setTimeStamp(mDBInboxList.get(i).getTimeStamp());
                                inbox.setId(mDBInboxList.get(i).getId());
                                inbox.setUserName(mDBInboxList.get(i).getUserName());
                                inbox.setMsg(mDBInboxList.get(i).getMsg());
                                inbox.setWaitlistId(mDBInboxList.get(i).getWaitlistId());
                                inbox.setMessageStatus(messageStatus);
                                messagesMap.get(senderKey).add(inbox);
                            } else {
                                ArrayList<InboxModel> emptyList = new ArrayList<InboxModel>();
                                InboxModel inbox = new InboxModel();
                                inbox.setTimeStamp(mDBInboxList.get(i).getTimeStamp());
                                inbox.setId(mDBInboxList.get(i).getId());
                                inbox.setUserName(mDBInboxList.get(i).getUserName());
                                inbox.setMsg(mDBInboxList.get(i).getMsg());
                                inbox.setWaitlistId(mDBInboxList.get(i).getWaitlistId());
                                inbox.setMessageStatus(messageStatus);
                                emptyList.add(inbox);
                                messagesMap.put(senderKey, emptyList);
                            }

                        }*/

                       /* mDBSORTInboxList.clear();


                        Config.logV("messagesMap LIST----------------" + messagesMap.size());
                        for (Map.Entry<String, List<InboxModel>> entry : messagesMap.entrySet()) {
                            String key = entry.getKey();
                            List<InboxModel> tab = entry.getValue();
                            Config.logV("Key----------" + key);
                            Config.logV("Value----------" + tab.size());

                            InboxModel inbox = new InboxModel();
                            inbox.setTimeStamp(tab.get(0).getTimeStamp());
                            inbox.setUserName(tab.get(0).getUserName());
                            inbox.setMsg(tab.get(0).getMsg());
                            inbox.setId(tab.get(0).getId());
                            inbox.setKey(key);
                            inbox.setWaitlistId(tab.get(0).getWaitlistId());
                            inbox.setMessageStatus(tab.get(0).getMessageStatus());
                            mDBSORTInboxList.add(inbox);

                        }*/


                        Collections.sort(mDBSORTInboxList, new Comparator<InboxModel>() {
                            @Override
                            public int compare(InboxModel r1, InboxModel r2) {
                                return new Long(r2.getTimeStamp()).compareTo(new Long(r1.getTimeStamp()));
                            }
                        });

                      //  Config.logV("INBOX LIST----------------" + mDBInboxList.size());

                        Config.logV("INBOX LIST------NEW----------" + mDBSORTInboxList.size());

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                        mrRecylce_inboxlist.setLayoutManager(mLayoutManager);
                        mInboxAdapter = new InboxAdapter(mDBSORTInboxList, mContext, mActivity, mDBInboxList);
                        mrRecylce_inboxlist.setAdapter(mInboxAdapter);
                        mInboxAdapter.notifyDataSetChanged();


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<InboxModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        Config.logV("OnResume------Inbox--------------------");
        if (Config.isOnline(getActivity())) {
            ApiInboxList();
        }else{
            mDBSORTInboxList.clear();
            db=new DatabaseHandler(mContext);
            mDBSORTInboxList = db.getAllInboxDetail();
            if(mDBSORTInboxList.size()>0) {

                Collections.sort(mDBSORTInboxList, new Comparator<InboxModel>() {
                    @Override
                    public int compare(InboxModel r1, InboxModel r2) {
                        return new Long(r2.getTimeStamp()).compareTo(new Long(r1.getTimeStamp()));
                    }
                });

                //  Config.logV("INBOX LIST----------------" + mDBInboxList.size());

                Config.logV("INBOX LIST------NEW----------" + mDBSORTInboxList.size());

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                mrRecylce_inboxlist.setLayoutManager(mLayoutManager);
                mInboxAdapter = new InboxAdapter(mDBSORTInboxList, mContext, mActivity, mDBInboxList);
                mrRecylce_inboxlist.setAdapter(mInboxAdapter);
                mInboxAdapter.notifyDataSetChanged();
            }
        }
    }


    /*@Override
    public void fragmentBecameVisible() {
        Config.logV("OnResume------INBox Frgamrnt VIisble--------------------");


    }*/
}
