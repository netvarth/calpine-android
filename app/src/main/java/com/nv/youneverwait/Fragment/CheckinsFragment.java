package com.nv.youneverwait.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.BillActivity;
import com.nv.youneverwait.activities.Home;
import com.nv.youneverwait.adapter.HistoryCheckInAdapter;
import com.nv.youneverwait.callback.HistoryAdapterCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.response.ActiveCheckIn;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckinsFragment extends RootFragment implements HistoryAdapterCallback /*,FragmentInterface*/{


    public CheckinsFragment() {
        // Required empty public constructor

    }


    Context mContext;
    Activity mActivity;
    RecyclerView mrRecylce_checklistFuture, mrRecylce_checklistOLd, mrRecylce_checklistTOday;
    HistoryCheckInAdapter mCheckAdapter;
    //  ArrayList<ArrayList<ActiveCheckIn>> mCheckList = new ArrayList<>();
    ArrayList<ActiveCheckIn> mCheckFutureList = new ArrayList<>();
    ArrayList<ActiveCheckIn> mCheckTodayList = new ArrayList<>();

    ArrayList<ActiveCheckIn> mCheckTodayFutureList = new ArrayList<>();

    ArrayList<ActiveCheckIn> mCheckOldList = new ArrayList<>();

    TextView tv_future, tv_old, tv_today, tv_notodaychekcin, tv_nofuturecheckin;
    boolean isExpandtoday = true, isExpandOld = false, isExpandFut = true;

    HistoryAdapterCallback mInterface;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_checkins, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        mInterface = (HistoryAdapterCallback) this;
        Config.logV("CheckIn--------------------------");
        mrRecylce_checklistFuture = (RecyclerView) row.findViewById(R.id.recylce_checkin);
        mrRecylce_checklistOLd = (RecyclerView) row.findViewById(R.id.recylce_oldcheckin);
        mrRecylce_checklistTOday = (RecyclerView) row.findViewById(R.id.recylce_checkintoday);
        tv_nofuturecheckin = (TextView) row.findViewById(R.id.txtnocheckfuture);
        tv_notodaychekcin = (TextView) row.findViewById(R.id.txtnocheckintoday);

        Home.doubleBackToExitPressedOnce=false;

        //expList = (ExpandableListView) row.findViewById(R.id.exp_list);
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);

        tv_future = (TextView) row.findViewById(R.id.txtfuture);
        tv_old = (TextView) row.findViewById(R.id.txtold);
        tv_today = (TextView) row.findViewById(R.id.txttoday);

        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        iBackPress.setVisibility(View.GONE);

        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setText("My Check-ins");
        tv_title.setTypeface(tyface);

        tv_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpandtoday) {
                    isExpandtoday = false;
                    mrRecylce_checklistTOday.setVisibility(View.GONE);
                    tv_today.setBackground(getActivity().getResources().getDrawable(R.drawable.input_background_opaque_round));
                    tv_today.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down_light, 0);
                    tv_notodaychekcin.setVisibility(View.GONE);

                } else {
                    isExpandtoday = true;
                    mrRecylce_checklistTOday.setVisibility(View.VISIBLE);
                    tv_today.setBackground(getActivity().getResources().getDrawable(R.drawable.input_border_top));
                    tv_today.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up_light, 0);
                    if (mCheckTodayList.size() == 0) {
                        tv_notodaychekcin.setVisibility(View.VISIBLE);
                        mrRecylce_checklistTOday.setVisibility(View.GONE);
                    }
                }

            }
        });

        tv_future.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpandFut) {
                    isExpandFut = false;
                    mrRecylce_checklistFuture.setVisibility(View.GONE);
                    tv_future.setBackground(getActivity().getResources().getDrawable(R.drawable.input_background_opaque_round));
                    tv_future.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down_light, 0);
                    tv_nofuturecheckin.setVisibility(View.GONE);
                } else {
                    isExpandFut = true;
                    mrRecylce_checklistFuture.setVisibility(View.VISIBLE);
                    tv_future.setBackground(getActivity().getResources().getDrawable(R.drawable.input_border_top));
                    tv_future.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up_light, 0);
                    if (mCheckFutureList.size() == 0) {
                        tv_nofuturecheckin.setVisibility(View.VISIBLE);
                        mrRecylce_checklistFuture.setVisibility(View.GONE);
                    }
                }

            }
        });


        tv_old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpandOld) {
                    isExpandOld = false;
                    mrRecylce_checklistOLd.setVisibility(View.GONE);
                    tv_old.setBackground(getActivity().getResources().getDrawable(R.drawable.input_background_opaque_round));
                    tv_old.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down_light, 0);
                } else {
                    isExpandOld = true;
                    mrRecylce_checklistOLd.setVisibility(View.VISIBLE);
                    tv_old.setBackground(getActivity().getResources().getDrawable(R.drawable.input_border_top));
                    tv_old.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up_light, 0);
                }

            }
        });


            ApiTodayChekInList();

        return row;
    }

    private void ApiTodayChekInList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
       /* Map<String, String> query = new HashMap<>();

        query.put("from", "0");
        query.put("count", "10");*/
        Call<ArrayList<ActiveCheckIn>> call = apiService.getActiveCheckIn();


        call.enqueue(new Callback<ArrayList<ActiveCheckIn>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveCheckIn>> call, Response<ArrayList<ActiveCheckIn>> response) {

                try {


                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mCheckTodayFutureList.clear();
                        mCheckTodayList.clear();
                        mCheckTodayFutureList = response.body();

                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        for (int i = 0; i < mCheckTodayFutureList.size(); i++) {
                            if (date.equalsIgnoreCase(mCheckTodayFutureList.get(i).getDate())) {
                                mCheckTodayList.add(response.body().get(i));
                            }

                        }

                        if (mCheckTodayList.size() == 0) {
                            tv_notodaychekcin.setVisibility(View.VISIBLE);
                            mrRecylce_checklistTOday.setVisibility(View.GONE);
                        } else {
                            tv_notodaychekcin.setVisibility(View.GONE);
                            mrRecylce_checklistTOday.setVisibility(View.VISIBLE);
                        }

                        ApiFutureChekInList();
                    }else{
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ActiveCheckIn>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        });


    }

    private void ApiOldChekInList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
       /* Map<String, String> query = new HashMap<>();

        query.put("from", "0");
        query.put("count", "10");*/
        Call<ArrayList<ActiveCheckIn>> call = apiService.getCheckInList();


        call.enqueue(new Callback<ArrayList<ActiveCheckIn>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveCheckIn>> call, Response<ArrayList<ActiveCheckIn>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mCheckOldList.clear();
                        mCheckOldList = response.body();

                        Config.logV("mCheckList mCheckOldList size-------------------------" + mCheckOldList.size());


                        if (mCheckFutureList.size() == 0 && mCheckTodayList.size() == 0) {
                            isExpandOld = true;
                            mrRecylce_checklistOLd.setVisibility(View.VISIBLE);
                            tv_old.setBackground(getActivity().getResources().getDrawable(R.drawable.input_border_top));
                            tv_old.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up_light, 0);
                        } else {
                            isExpandOld = false;
                            mrRecylce_checklistOLd.setVisibility(View.GONE);
                            tv_old.setBackground(getActivity().getResources().getDrawable(R.drawable.input_background_opaque_round));
                            tv_old.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_down_light, 0);
                        }
                        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mContext);
                        mrRecylce_checklistTOday.setLayoutManager(mLayoutManager1);
                        mCheckAdapter = new HistoryCheckInAdapter(mCheckTodayList, mContext, mActivity,mInterface ,"today");
                        mrRecylce_checklistTOday.setAdapter(mCheckAdapter);
                        mCheckAdapter.notifyDataSetChanged();

                        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(mContext);
                        mrRecylce_checklistFuture.setLayoutManager(mLayoutManager2);
                        mCheckAdapter = new HistoryCheckInAdapter(mCheckFutureList, mContext, mActivity,mInterface,"future" );
                        mrRecylce_checklistFuture.setAdapter(mCheckAdapter);
                        mCheckAdapter.notifyDataSetChanged();


                        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(mContext);
                        mrRecylce_checklistOLd.setLayoutManager(mLayoutManager3);
                        mCheckAdapter = new HistoryCheckInAdapter(mCheckOldList, mContext, mActivity,mInterface ,"old");
                        mrRecylce_checklistOLd.setAdapter(mCheckAdapter);
                        mCheckAdapter.notifyDataSetChanged();


                    }else{
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ActiveCheckIn>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private void ApiFutureChekInList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
       /* Map<String, String> query = new HashMap<>();

        query.put("from", "0");
        query.put("count", "10");*/
        Call<ArrayList<ActiveCheckIn>> call = apiService.getFutureCheckInList();


        call.enqueue(new Callback<ArrayList<ActiveCheckIn>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveCheckIn>> call, Response<ArrayList<ActiveCheckIn>> response) {

                try {


                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mCheckFutureList.clear();
                        mCheckFutureList = response.body();

                        if (mCheckFutureList.size() == 0) {
                            tv_nofuturecheckin.setVisibility(View.VISIBLE);
                            mrRecylce_checklistFuture.setVisibility(View.GONE);
                        } else {
                            tv_nofuturecheckin.setVisibility(View.GONE);
                            mrRecylce_checklistFuture.setVisibility(View.VISIBLE);
                        }
                       /* mCheckList.add(mCheckOldList);
                        mCheckList.add(mCheckFutureList);

                        Config.logV("mCheckList  mCheckFutureList size-------------------------" + mCheckFutureList.size());
                        Config.logV("mCheckList  Final size-------------------------" + mCheckList.size());
                        expandableAdapter = new ExpandableAdapter(mContext, mCheckList, parents);
                        expList.setAdapter(expandableAdapter);


*/


                        ApiOldChekInList();


                    }else{
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ActiveCheckIn>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        });


    }
    @Override
    public void onMethodMessageCallback(final String ynwuuid, final String accountID, String providerNAme) {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext,R.style.DialogStyle);
        dialog.setContentView(R.layout.reply);
        dialog.show();

        Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
        String firstWord = "Message to ";
        String secondWord = providerNAme;
        Spannable spannable = new SpannableString(firstWord + secondWord);
        Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtsendmsg.setText(spannable);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiCommunicate(ynwuuid, String.valueOf(accountID), edt_message.getText().toString(), dialog);

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onMethodBillIconCallback(String value, String provider) {
        Intent iBill = new Intent(mContext, BillActivity.class);
        iBill.putExtra("ynwUUID", value);
        iBill.putExtra("provider", provider);
        startActivity(iBill);
    }

    @Override
    public void onMethodDelecteCheckinCallback(final String ynwuuid, final int accountID) {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.cancelcheckin);
        dialog.show();

        Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiDeleteCheckin(ynwuuid, String.valueOf(accountID), dialog);

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onMethodActiveCallback(String value) {
        Bundle bundle = new Bundle();

        SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();

        bundle.putString("uniqueID", value);
        pfFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }

    private void ApiCommunicate(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("communicationMessage", message);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.WaitListMessage(waitListId, String.valueOf(accountID), body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        dialog.dismiss();


                    }else{
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_SHORT).show();
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


    private void ApiDeleteCheckin(String ynwuuid, String accountID, final BottomSheetDialog dialog) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ResponseBody> call = apiService.deleteActiveCheckIn(ynwuuid, String.valueOf(accountID));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body().string().equalsIgnoreCase("true")) {
                            dialog.dismiss();
                            ApiTodayChekInList();

                        }


                    }else{
                        Toast.makeText(mContext,response.errorBody().string(),Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();

    }

    /*@Override
    public void fragmentBecameVisible() {


    }*/
}
