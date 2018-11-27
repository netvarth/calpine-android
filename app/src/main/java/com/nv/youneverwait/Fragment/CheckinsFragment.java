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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.activities.BillActivity;
import com.nv.youneverwait.activities.Home;
import com.nv.youneverwait.adapter.FavouriteAdapter;
import com.nv.youneverwait.adapter.HistoryCheckInAdapter;
import com.nv.youneverwait.callback.HistoryAdapterCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.response.ActiveCheckIn;
import com.nv.youneverwait.response.FavouriteModel;
import com.nv.youneverwait.response.RatingResponse;
import com.nv.youneverwait.response.SearchCheckInMessage;
import com.nv.youneverwait.utils.LogUtil;
import com.nv.youneverwait.utils.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckinsFragment extends RootFragment implements HistoryAdapterCallback {


    public CheckinsFragment() {
        // Required empty public constructor

    }


    boolean firstTimeRating=false;
    Context mContext;
    Activity mActivity;
    RecyclerView mrRecylce_checklistFuture, mrRecylce_checklistOLd, mrRecylce_checklistTOday;
    HistoryCheckInAdapter mCheckAdapter;
    //  ArrayList<ArrayList<ActiveCheckIn>> mCheckList = new ArrayList<>();
    ArrayList<ActiveCheckIn> mCheckFutureList = new ArrayList<>();
    ArrayList<ActiveCheckIn> mCheckTodayList = new ArrayList<>();

    ArrayList<ActiveCheckIn> mCheckTodayFutureList = new ArrayList<>();

    ArrayList<ActiveCheckIn> mCheckOldList = new ArrayList<>();

    TextView tv_future, tv_old, tv_today, tv_notodaychekcin, tv_nofuturecheckin, tv_nocheckold;
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
        tv_nocheckold = (TextView) row.findViewById(R.id.txtnocheckold);
        Home.doubleBackToExitPressedOnce = false;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                    tv_nocheckold.setVisibility(View.GONE);
                } else {
                    isExpandOld = true;
                    mrRecylce_checklistOLd.setVisibility(View.VISIBLE);
                    tv_old.setBackground(getActivity().getResources().getDrawable(R.drawable.input_border_top));
                    tv_old.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_up_light, 0);
                    if (mCheckOldList.size() == 0) {
                        tv_notodaychekcin.setVisibility(View.VISIBLE);
                        mrRecylce_checklistOLd.setVisibility(View.GONE);
                    }

                }

            }
        });


        ApiFavList();

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
                            tv_today.setText("TODAY "+"( "+mCheckTodayList.size()+" )");
                        }

                        ApiFutureChekInList();
                    } else {
                        if (response.code() != 419) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
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

                        if (mCheckOldList.size() == 0) {
                            tv_nocheckold.setVisibility(View.VISIBLE);
                            mrRecylce_checklistOLd.setVisibility(View.GONE);
                        } else {
                            tv_nocheckold.setVisibility(View.GONE);
                            //  mrRecylce_checklistOLd.setVisibility(View.VISIBLE);
                        }


                        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(mContext);
                        mrRecylce_checklistTOday.setLayoutManager(mLayoutManager1);
                        mCheckAdapter = new HistoryCheckInAdapter(mFavList, mCheckTodayList, mContext, mActivity, mInterface, "today");
                        mrRecylce_checklistTOday.setAdapter(mCheckAdapter);
                        mCheckAdapter.notifyDataSetChanged();

                        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(mContext);
                        mrRecylce_checklistFuture.setLayoutManager(mLayoutManager2);
                        mCheckAdapter = new HistoryCheckInAdapter(mFavList, mCheckFutureList, mContext, mActivity, mInterface, "future");
                        mrRecylce_checklistFuture.setAdapter(mCheckAdapter);
                        mCheckAdapter.notifyDataSetChanged();


                        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(mContext);
                        mrRecylce_checklistOLd.setLayoutManager(mLayoutManager3);
                        mCheckAdapter = new HistoryCheckInAdapter(mFavList, mCheckOldList, mContext, mActivity, mInterface, "old");
                        mrRecylce_checklistOLd.setAdapter(mCheckAdapter);
                        mCheckAdapter.notifyDataSetChanged();


                    } else {
                       // Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                       /* if(response.code()==419){
                            String cookie= SharedPreference.getInstance(mContext).getStringValue("PREF_COOKIES","");
                            LogUtil.writeLogTest(" Session Expired "+cookie);
                        }
*/
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
                            tv_future.setText("FUTURE"+" ( "+mCheckFutureList.size()+" )");
                        }
                       /* mCheckList.add(mCheckOldList);
                        mCheckList.add(mCheckFutureList);

                        Config.logV("mCheckList  mCheckFutureList size-------------------------" + mCheckFutureList.size());
                        Config.logV("mCheckList  Final size-------------------------" + mCheckList.size());
                        expandableAdapter = new ExpandableAdapter(mContext, mCheckList, parents);
                        expList.setAdapter(expandableAdapter);


*/


                        ApiOldChekInList();


                    } else {
                        if (response.code() != 419) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
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
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
        dialog.setContentView(R.layout.reply);
        dialog.show();

        final Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
        final Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
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

        edt_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
               if(edt_message.getText().toString().length()>1){
                   btn_send.setEnabled(true);
                   btn_send.setClickable(true);
                   btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
               }else{
                   btn_send.setEnabled(false);
                   btn_send.setClickable(false);
                   btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
               }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    @Override
    public void onMethodBillIconCallback(String value, String provider,String accountID ) {
        Intent iBill = new Intent(mContext, BillActivity.class);
        iBill.putExtra("ynwUUID", value);
        iBill.putExtra("provider", provider);
        iBill.putExtra("accountID", accountID);
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

    @Override
    public void onMethodAddFavourite(int value) {
        ApiAddFavo(value);
    }

    @Override
    public void onMethodDeleteFavourite(int value) {
        ApiRemoveFavo(value);

    }

    @Override
    public void onMethodRating(String accountID, String UUID) {


        ApiRating(accountID, UUID);
    }

    BottomSheetDialog dialog;

    float rate = 0;
    String comment = "";

    private void ApiRating(final String accountID, final String UUID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Map<String, String> query = new HashMap<>();

        query.put("account", accountID);
        query.put("uId-eq", UUID);


        Call<ArrayList<RatingResponse>> call = apiService.getRating(query);

        Config.logV("Location-----###########@@@@@@" + query);

        call.enqueue(new Callback<ArrayList<RatingResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RatingResponse>> call, final Response<ArrayList<RatingResponse>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL----------Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());

                    if (response.code() == 200) {



                            final ArrayList<RatingResponse> mRatingDATA = response.body();
                            Config.logV("Response--code--------BottomSheetDialog-----------------" + response.code());
                            dialog = new BottomSheetDialog(mContext);
                            dialog.setContentView(R.layout.rating);
                            dialog.setCancelable(true);
                            dialog.show();
                            TextView tv_title = (TextView) dialog.findViewById(R.id.txtratevisit);

                            final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                            final RatingBar rating = (RatingBar) dialog.findViewById(R.id.rRatingBar);

                            Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            tv_title.setTypeface(tyface);
                            final Button btn_close = (Button) dialog.findViewById(R.id.btn_cancel);

                            final Button btn_rate = (Button) dialog.findViewById(R.id.btn_send);
                            btn_rate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {



                                    rate=rating.getRating();
                                    comment = edt_message.getText().toString();

                                   if(response.body().size()==0) {
                                       firstTimeRating=true;
                                   }else{
                                       firstTimeRating=false;
                                   }
                                    ApiPUTRating(Math.round(rate), UUID, comment, accountID, dialog,firstTimeRating);

                                }
                            });

                        edt_message.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void afterTextChanged(Editable arg0) {
                                if(edt_message.getText().toString().length()>1){
                                    btn_rate.setEnabled(true);
                                    btn_rate.setClickable(true);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
                                }else{
                                    btn_rate.setEnabled(false);
                                    btn_rate.setClickable(false);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                }
                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });
                            btn_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();


                                }
                            });

                            if(response.body().size()>0) {
                                if (mRatingDATA.get(0).getStars() != 0) {
                                    rating.setRating(Float.valueOf(mRatingDATA.get(0).getStars()));
                                }


                                if (mRatingDATA.get(0).getFeedback() != null) {
                                    Config.logV("Comments---------" + mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                                    edt_message.setText(mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                                }
                            }



                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<RatingResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    private void ApiPUTRating(final int stars, final String UUID, String feedback, String accountID, final BottomSheetDialog dialog,boolean firstTimerate) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("uuid", UUID);
            jsonObj.put("stars", String.valueOf(stars));
            jsonObj.put("feedback", feedback);

            Config.logV("Feedback--------------" + feedback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());

        Call<ResponseBody> call;
        if(firstTimerate){
         call =    apiService.PostRating(accountID, body);
        }else{
           call =    apiService.PutRating(accountID, body);
        }

        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    Config.logV("URL-------Request---" + response.raw().request().url().toString().trim());
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    dialog.dismiss();
                    Config.logV("Put Rating#########################" + response.code());
                    if (response.code() == 200) {


                        if (response.body().string().equalsIgnoreCase("true")) {

                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


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


                    } else {
                        if (response.code() != 419) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
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
                            ApiFavList();

                        }


                    } else {
                        if (response.code() != 419) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();

    }

    private void ApiAddFavo(int providerID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.AddFavourite(providerID);

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
                            Toast.makeText(mContext,"Added to Favourites",Toast.LENGTH_LONG).show();
                            ApiFavList();
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

    ArrayList<FavouriteModel> mFavList = new ArrayList<>();

    private void ApiFavList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<FavouriteModel>> call = apiService.getFavourites();


        call.enqueue(new Callback<ArrayList<FavouriteModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FavouriteModel>> call, Response<ArrayList<FavouriteModel>> response) {

                try {


                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mFavList.clear();
                        mFavList = response.body();
                        ApiTodayChekInList();


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<FavouriteModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }

    private void ApiRemoveFavo(int providerID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.DeleteFavourite(providerID);

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
                            ApiFavList();
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


}
