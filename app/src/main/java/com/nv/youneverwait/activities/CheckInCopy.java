package com.nv.youneverwait.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nv.youneverwait.R;
import com.nv.youneverwait.adapter.PaymentAdapter;
import com.nv.youneverwait.adapter.QueueTimeSlotAdapter;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.connection.ApiClient;
import com.nv.youneverwait.connection.ApiInterface;
import com.nv.youneverwait.custom.CustomTypefaceSpan;
import com.nv.youneverwait.model.TestModel;
import com.nv.youneverwait.response.CheckSumModel;
import com.nv.youneverwait.response.PaymentModel;
import com.nv.youneverwait.response.QueueTimeSlotModel;
import com.nv.youneverwait.response.SearchService;
import com.nv.youneverwait.response.SearchSetting;
import com.nv.youneverwait.response.SearchTerminology;
import com.nv.youneverwait.utils.SharedPreference;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sharmila on 6/8/18.
 */

public class CheckInCopy extends AppCompatActivity {
    static Context mContext;
    static Activity mActivity;
    Spinner mSpinnerService;
    static int serviceId;
    ArrayList<SearchService> LServicesList = new ArrayList<>();

    String uniqueID;
    TextView tv_addmember;
    String accountID;
    static QueueTimeSlotAdapter mQueueAdapter;
    PaymentAdapter mPayAdpater;
    // static RecyclerView mRecycleQueueList;
    // RecyclerView mRecyclePayList;
    static int mSpinnertext;
    static ArrayList<QueueTimeSlotModel> mQueueTimeSlotList = new ArrayList<>();
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    static String modifyAccountID;
    boolean isPrepayment;
    TextView tv_amount;
    String sAmountPay;
    static TextView tv_name;
    String mFirstName, mLastName;
    int consumerID;
    static TextView tv_waittime;
    static TextView tv_queue;
    static TextView txt_date;
    ImageView img_calender_checkin;
    LinearLayout LcheckinDatepicker;
    String mFrom;
    String title, place;
    TextView tv_titlename, tv_place, tv_checkin_service;
    static ImageView ic_left, ic_right;
    static TextView tv_queuetime;
    static TextView tv_queuename;
    static LinearLayout queuelayout;
    TextView txt_chooseservice;
    static int i = 0;
    static ImageView ic_cal_minus;
    ImageView ic_cal_add;
    Button btn_checkin;
    static int queueId = 0;

    int selectedService;

    static String selectedDateFormat;
    String serviceSelected;

    TextView tv_addnote;

    String txt_message="";
    private void ApiGenerateHash(String ynwUUID, String amount,String accountID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        JSONObject jsonObj = new JSONObject();
        try {
            String androidId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            jsonObj.put("amount", amount);
            jsonObj.put("paymentMode", "DC");
            jsonObj.put("uuid", ynwUUID);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<CheckSumModel> call = apiService.generateHash(body,accountID);

        call.enqueue(new Callback<CheckSumModel>() {
            @Override
            public void onResponse(Call<CheckSumModel> call, Response<CheckSumModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {


                        CheckSumModel response_data = response.body();
                        Config.logV("Response--Sucess-------------------------" + new Gson().toJson(response.body()));
                        // Config.logV("Checksum id-----------" + response_data.getChecksum());
                        // Config.logV("Product key-----------" + response_data.getProductinfo());

                       // launchPaymentFlow1(sAmountPay, response_data, serviceSelected);


                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<CheckSumModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin);
        mActivity = this;

        btn_checkin = (Button) findViewById(R.id.btn_checkin);

        //  Lpayment = (LinearLayout) findViewById(R.id.Lpayment);
        queuelayout = (LinearLayout) findViewById(R.id.queuelayout);
        txt_chooseservice = (TextView) findViewById(R.id.txt_chooseservice);


        tv_amount = (TextView) findViewById(R.id.txtamount);
        tv_name = (TextView) findViewById(R.id.txtname);
        tv_waittime = (TextView) findViewById(R.id.txt_waittime);
        txt_date = (TextView) findViewById(R.id.txt_date);
        img_calender_checkin = (ImageView) findViewById(R.id.calender_checkin);
        LcheckinDatepicker = (LinearLayout) findViewById(R.id.checkinDatepicker);
        tv_queue = (TextView) findViewById(R.id.txt_queue);
        tv_place = (TextView) findViewById(R.id.txt_place);
        tv_titlename = (TextView) findViewById(R.id.txt_title);
        tv_checkin_service = (TextView) findViewById(R.id.txt_checkin_service);

        tv_queuetime = (TextView) findViewById(R.id.txt_queuetime);
        tv_queuename = (TextView) findViewById(R.id.txt_queuename);

        ImageView iBackPress=(ImageView)findViewById(R.id.backpress) ;
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });
        TextView tv_title = (TextView)findViewById(R.id.toolbartitle);
        tv_title.setText("Check-in");

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);

        tv_titlename.setTypeface(tyface);



        tv_addnote = (TextView) findViewById(R.id.txtaddnote);
        tv_addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                dialog.setContentView(R.layout.reply);
                dialog.show();

                Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
                txtsendmsg.setVisibility(View.GONE);
                btn_send.setText("ADD");


                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txt_message=edt_message.getText().toString();
                        dialog.dismiss();

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });


        ic_left = (ImageView) findViewById(R.id.ic_left);
        ic_right = (ImageView) findViewById(R.id.ic_right);


        ic_cal_minus = (ImageView) findViewById(R.id.ic_cal_minus);
        ic_cal_add = (ImageView) findViewById(R.id.ic_cal_add);

        ic_cal_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dtStart = txt_date.getText().toString();
                Config.logV("Date----------------" + dtStart);
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
                try {
                    date = format.parse(dtStart);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date added_date = addDays(date, 1);
                DateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");

                //to convert Date to String, use format method of SimpleDateFormat class.
                String strDate = dateFormat.format(added_date);
                txt_date.setText(strDate);


                DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
                selectedDateFormat = selecteddateParse.format(added_date);
                UpdateDAte(selectedDateFormat);


            }
        });


        ic_cal_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dtStart = txt_date.getText().toString();
                Config.logV("Date----------------" + dtStart);
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
                try {
                    date = format.parse(dtStart);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date added_date = subtractDays(date, 1);
                DateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");

                //to convert Date to String, use format method of SimpleDateFormat class.
                String strDate = dateFormat.format(added_date);
                txt_date.setText(strDate);


                DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
                selectedDateFormat = selecteddateParse.format(added_date);
                UpdateDAte(selectedDateFormat);


                //  UpdateDAte(strDate);
            }
        });



        btn_checkin.setTypeface(tyface);
        tv_queuename.setTypeface(tyface);
        txt_date.setTypeface(tyface);


        int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
        Config.logV("Consumer ID------------" + consumerId);
        mContext = this;
        mActivity = this;
        //  mRecycleQueueList = (RecyclerView) findViewById(R.id.recycleQueueList);
        // mRecyclePayList = (RecyclerView) findViewById(R.id.recyclepaymentList);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            serviceId = extras.getInt("serviceId");
            uniqueID = extras.getString("uniqueID");
            accountID = extras.getString("accountID");
            mFrom = extras.getString("from", "");
            if (mFrom.equalsIgnoreCase("searchdetail_future") || mFrom.equalsIgnoreCase("searchdetail_checkin")) {
                modifyAccountID = accountID;
            } else {
                modifyAccountID = accountID.substring(0, accountID.indexOf("-"));
            }


            title = extras.getString("title", "");
            place = extras.getString("place", "");

        }
        tv_place.setText(place);

        tv_titlename.setText(title);


        btn_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ApiGenerateHash();

                ApiCheckin(txt_message);
            }
        });

        mSpinnerService = (Spinner) findViewById(R.id.spinnerservice);
        ApiSearchViewServiceID(serviceId);
        ApiSearchViewSetting(uniqueID);
        ApiSearchViewTerminology(uniqueID);
        mFirstName = SharedPreference.getInstance(mContext).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(mContext).getStringValue("lastname", "");
        consumerID = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
        tv_name.setText(mFirstName + " " + mLastName);
        tv_addmember = (TextView) findViewById(R.id.txtaddmember);


        tv_addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iFamily = new Intent(v.getContext(), CheckinFamilyMember.class);
                iFamily.putExtra("firstname", mFirstName);
                iFamily.putExtra("lastname", mLastName);
                iFamily.putExtra("consumerID", consumerID);
                startActivity(iFamily);
            }
        });


        mSpinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mSpinnertext = ((SearchService) mSpinnerService.getSelectedItem()).getId();

                serviceSelected = ((SearchService) mSpinnerService.getSelectedItem()).getName();
                selectedService = ((SearchService) mSpinnerService.getSelectedItem()).getId();
                String firstWord = "Check-in for ";
                String secondWord = ((SearchService) mSpinnerService.getSelectedItem()).getName();

                Spannable spannable = new SpannableString(firstWord + secondWord);
                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Montserrat_Bold.otf");
                spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_consu)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv_checkin_service.setText(spannable);


                Date currentTime = new Date();
                final SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println("UTC time: " + sdf.format(currentTime));
                ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, sdf.format(currentTime));
                isPrepayment = ((SearchService) mSpinnerService.getSelectedItem()).isPrePayment();
                Config.logV("Payment------------" + isPrepayment);
                if (isPrepayment) {
                    APIPayment(modifyAccountID);
                    sAmountPay = ((SearchService) mSpinnerService.getSelectedItem()).getTotalAmount();

                } else {
                    // Lpayment.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });


        if (mFrom.equalsIgnoreCase("checkin") || mFrom.equalsIgnoreCase("searchdetail_checkin")) {
            LcheckinDatepicker.setVisibility(View.GONE);
        } else {
            LcheckinDatepicker.setVisibility(View.VISIBLE);

            Date currentTime = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.println("UTC time: " + sdf.format(currentTime));

            txt_date.setText(sdf.format(currentTime));


            DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
            selectedDateFormat = selecteddateParse.format(currentTime);


            UpdateDAte(selectedDateFormat);


        }

        img_calender_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyDatePickerDialog();
                newFragment.show(getSupportFragmentManager(), "date picker");


            }
        });

    }


    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * subtract days to date in java
     *
     * @param date
     * @param days
     * @return
     */
    public Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    static String mDate;

    public static class MyDatePickerDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog da = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
            da.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return da;
        }

        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day) {


                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                        Date date = new Date(year, month, day - 1);
                        String dayOfWeek = simpledateformat.format(date);
                        Config.logV("Day-------------" + dayOfWeek);

                        mDate = dayOfWeek + ", " + view.getDayOfMonth() +
                                "/" + (view.getMonth() + 1) +
                                "/" + view.getYear();
                        txt_date.setText(mDate);


                        selectedDateFormat = view.getYear() + "-" + (view.getMonth() + 1) + "-" + view.getDayOfMonth();
                        UpdateDAte(selectedDateFormat);


                    }
                };
    }


    public static void UpdateDAte(String selectedDate) {
        Date selecteddate = null;
        String dtStart = txt_date.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        try {
            selecteddate = format.parse(dtStart);
            //  System.out.println(selecteddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Config.logV("Selected Date----------------" + selectedDate);
        ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, selectedDate);
        if (new Date().before(selecteddate)) {
            Config.logV("Date Enabled---------------");
            ic_cal_minus.setEnabled(true);
            ic_cal_minus.setImageResource(R.drawable.icon_minus_active);

        } else {
            Config.logV("Date Disabled---------------");
            ic_cal_minus.setEnabled(false);
            ic_cal_minus.setImageResource(R.drawable.icon_minus_disabled);
        }
    }

    static int familyMEmID;

    public static void refreshName(String name, int memID) {
        Config.logV("NAme----------" + name);
        if (name != null && !name.equalsIgnoreCase("")) {
            tv_name.setText(name);
            familyMEmID = memID;
        }
    }

    private void ApiSearchViewSetting(String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchSetting> call = apiService.getSearchViewSetting(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchSetting>() {
            @Override
            public void onResponse(Call<SearchSetting> call, Response<SearchSetting> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchSetting> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }


    private void APIPayment(String accountID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<PaymentModel>> call = apiService.getPayment(accountID);

        call.enqueue(new Callback<ArrayList<PaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PaymentModel>> call, Response<ArrayList<PaymentModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mPaymentData = response.body();
                        /*if (mPaymentData.size() > 0) {
                            Lpayment.setVisibility(View.VISIBLE);
                            mPayAdpater = new PaymentAdapter(mPaymentData, mActivity);
                            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
                            mRecyclePayList.setLayoutManager(horizontalLayoutManager);
                            mRecyclePayList.setAdapter(mPayAdpater);
                            tv_amount.setText("Amount to Pay ₹" + sAmountPay);
                        }*/

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<PaymentModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }


    private static void ApiQueueTimeSlot(String serviceId, String subSeriveID, String accountID, String mDate) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<QueueTimeSlotModel>> call = apiService.getQueueTimeSlot(serviceId, subSeriveID, mDate, accountID);

        call.enqueue(new Callback<ArrayList<QueueTimeSlotModel>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueTimeSlotModel>> call, Response<ArrayList<QueueTimeSlotModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(mActivity, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("mQueueTimeSlotList--------11111-----------------" + response.code());
                    if (response.code() == 200) {
                        mQueueTimeSlotList = response.body();
                        i = 0;
                        if (mQueueTimeSlotList.size() > 0) {

                            tv_queuename.setVisibility(View.VISIBLE);
                            tv_queuetime.setVisibility(View.VISIBLE);
                            tv_waittime.setVisibility(View.VISIBLE);
                            if (mQueueTimeSlotList.get(i).getId() != 0) {
                                queueId = mQueueTimeSlotList.get(i).getId();
                            }

                            Config.logV("mQueueTimeSlotList-------------------------" + mQueueTimeSlotList.size());
                            tv_queue.setVisibility(View.VISIBLE);
                            queuelayout.setVisibility(View.VISIBLE);


                            tv_queuename.setText(mQueueTimeSlotList.get(0).getName());
                            tv_queuetime.setText(mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).geteTime());

                            String firstWord = "Est Wait Time ";
                            String secondWord = null;
                            try {
                                String startTime = "00:00";
                                String newtime = null;
                                int minutes = mQueueTimeSlotList.get(0).getQueueWaitingTime();
                                int h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
                                int m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));
                                if (m > 0 && h > 0) {
                                    newtime = h + "Hour :" + m + "Minutes";
                                } else if (h > 0 && m == 0) {
                                    newtime = h + "Hour";
                                } else {
                                    newtime = m + "Minutes";
                                }
                                secondWord = newtime;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            if (mQueueTimeSlotList.get(0).getServiceTime() != null) {
                                secondWord = mQueueTimeSlotList.get(0).getServiceTime();
                            }

                            Spannable spannable = new SpannableString(firstWord + secondWord);
                            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tv_waittime.setText(spannable);

                            if (mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("NoCalc")) {
                                tv_waittime.setVisibility(View.INVISIBLE);
                            } else {
                                tv_waittime.setVisibility(View.VISIBLE);
                            }


                        } else {


                            tv_queue.setVisibility(View.GONE);
                            queuelayout.setVisibility(View.GONE);
                            tv_queuename.setVisibility(View.GONE);
                            tv_queuetime.setVisibility(View.GONE);
                            tv_waittime.setVisibility(View.GONE);
                        }
                        if (mQueueTimeSlotList.size() > 1) {

                            ic_right.setVisibility(View.VISIBLE);
                            ic_left.setVisibility(View.VISIBLE);
                            ic_right.setImageResource(R.drawable.icon_right_angle_active);
                            ic_right.setEnabled(true);


                        } else {
                            ic_right.setVisibility(View.INVISIBLE);
                            ic_left.setVisibility(View.INVISIBLE);
                        }

                        if (i > 0) {
                            ic_left.setEnabled(true);
                            ic_left.setImageResource(R.drawable.icon_left_angle_active);
                        } else {
                            ic_left.setEnabled(false);
                            ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                        }


                        ic_left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                i--;
                                Config.logV("Left Click------------------**" + i);
                                if (i >= 0) {

                                    tv_queuename.setText(mQueueTimeSlotList.get(i).getName());
                                    tv_queuetime.setText(mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).geteTime());

                                    String firstWord = "Est Wait Time ";
                                    String secondWord = null;
                                    try {
                                        String startTime = "00:00";
                                        String newtime;
                                        int minutes = mQueueTimeSlotList.get(i).getQueueWaitingTime();
                                        int h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
                                        int m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));
                                        if (m > 0 && h > 0) {
                                            newtime = h + "Hour :" + m + "Minutes";
                                        } else if (h > 0 && m == 0) {
                                            newtime = h + "Hour";
                                        } else {
                                            newtime = m + "Minutes";
                                        }
                                        secondWord = newtime;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    if (mQueueTimeSlotList.get(i).getId() != 0) {
                                        queueId = mQueueTimeSlotList.get(i).getId();
                                    }

                                    if (mQueueTimeSlotList.get(i).getServiceTime() != null) {
                                        secondWord = mQueueTimeSlotList.get(i).getServiceTime();
                                    }

                                    Spannable spannable = new SpannableString(firstWord + secondWord);
                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tv_waittime.setText(spannable);

                                    if (mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("NoCalc")) {
                                        tv_waittime.setVisibility(View.INVISIBLE);
                                    } else {
                                        tv_waittime.setVisibility(View.VISIBLE);
                                    }
                                }


                                if (i < mQueueTimeSlotList.size()) {
                                    ic_right.setEnabled(true);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_active);
                                } else {
                                    ic_right.setEnabled(false);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                                }

                                if (i <= 0) {
                                    ic_left.setEnabled(false);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                                } else {

                                    ic_left.setEnabled(true);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_active);
                                }

                            }
                        });

                        ic_right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (i < 0) {
                                    i = 0;
                                }
                                i++;
                                Config.logV("Right Click----1111--------------" + i);
                                if (i < mQueueTimeSlotList.size()) {

                                    tv_queuename.setText(mQueueTimeSlotList.get(i).getName());
                                    tv_queuetime.setText(mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).geteTime());


                                    if (mQueueTimeSlotList.get(i).getId() != 0) {
                                        queueId = mQueueTimeSlotList.get(i).getId();
                                    }

                                    String firstWord = "Est Wait Time ";
                                    String secondWord = null;
                                    try {
                                        String startTime = "00:00";
                                        String newtime;
                                        int minutes = mQueueTimeSlotList.get(i).getQueueWaitingTime();
                                        int h = minutes / 60 + Integer.parseInt(startTime.substring(0, 1));
                                        int m = minutes % 60 + Integer.parseInt(startTime.substring(3, 4));
                                        if (m > 0 && h > 0) {
                                            newtime = h + "Hour :" + m + "Minutes";
                                        } else if (h > 0 && m == 0) {
                                            newtime = h + "Hour";
                                        } else {
                                            newtime = m + "Minutes";
                                        }
                                        secondWord = newtime;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    if (mQueueTimeSlotList.get(i).getServiceTime() != null) {
                                        secondWord = mQueueTimeSlotList.get(i).getServiceTime();
                                    }

                                    Spannable spannable = new SpannableString(firstWord + secondWord);
                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tv_waittime.setText(spannable);
                                    if (mQueueTimeSlotList.get(0).getCalculationMode().equalsIgnoreCase("NoCalc")) {
                                        tv_waittime.setVisibility(View.INVISIBLE);
                                    } else {
                                        tv_waittime.setVisibility(View.VISIBLE);
                                    }

                                }

                                if (i >= 0) {
                                    ic_left.setEnabled(true);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_active);
                                } else {
                                    ic_left.setEnabled(false);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                                }

                                Config.logV("Queuesize---------------" + mQueueTimeSlotList.size() + "position" + i);
                                if (i == mQueueTimeSlotList.size() - 1) {

                                    ic_right.setEnabled(false);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                                } else {
                                    ic_right.setEnabled(true);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_active);
                                }
                            }
                        });
                            /*mQueueAdapter = new QueueTimeSlotAdapter(mQueueTimeSlotList, mActivity);
                            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
                            mRecycleQueueList.setLayoutManager(horizontalLayoutManager);
                            mRecycleQueueList.setAdapter(mQueueAdapter);*/


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<QueueTimeSlotModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);

            }
        });


    }


    private void ApiSearchViewServiceID(final int id) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<SearchService>> call = apiService.getSearchService(id);

        call.enqueue(new Callback<ArrayList<SearchService>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchService>> call, Response<ArrayList<SearchService>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {


                        for (int i = 0; i < response.body().size(); i++) {
                            SearchService mService = new SearchService();
                            mService.setName(response.body().get(i).getName());
                            mService.setLocid(id);
                            mService.setId(response.body().get(i).getId());
                            mService.setPrePayment(response.body().get(i).isPrePayment());
                            mService.setTotalAmount(response.body().get(i).getTotalAmount());
                            LServicesList.add(mService);
                        }


                        if (LServicesList.size() > 0) {
                            mSpinnerService.setVisibility(View.VISIBLE);
                            txt_chooseservice.setVisibility(View.VISIBLE);
                            Config.logV("mServicesList" + LServicesList.size());
                            ArrayAdapter<SearchService> adapter = new ArrayAdapter<SearchService>(mActivity, android.R.layout.simple_spinner_dropdown_item, LServicesList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            mSpinnerService.setAdapter(adapter);
                        } else {

                            mSpinnerService.setVisibility(View.GONE);
                            txt_chooseservice.setVisibility(View.GONE);
                        }


                        mSpinnertext = ((SearchService) mSpinnerService.getSelectedItem()).getId();
                        Config.logV("mSpinnertext----" + mSpinnertext);
                        Config.logV("mSpinnertext-11---" + serviceId);
                        Config.logV("mSpinnertext-11333---" + accountID);

                        Date currentTime = new Date();
                        final SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy-MM-dd", Locale.US);
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        System.out.println("UTC time: " + sdf.format(currentTime));
                        ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, sdf.format(currentTime));

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchService>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }

    SearchTerminology mSearchTerminology;

    private void ApiSearchViewTerminology(String muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchTerminology> call = apiService.getSearchViewTerminology(Integer.parseInt(muniqueID), sdf.format(currentTime));

        call.enqueue(new Callback<SearchTerminology>() {
            @Override
            public void onResponse(Call<SearchTerminology> call, Response<SearchTerminology> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mSearchTerminology = response.body();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SearchTerminology> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }
    private void calculateHashInServer(final PayUmoneySdkInitializer.PaymentParam mPaymentParams) {

        String url = Constants.MONEY_HASH;
        StringRequest request = new StringRequest(Request.Method.POST, url,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // mTxvBuy.setEnabled(true);
                        String merchantHash = "";

                        System.out.println("Result----------"+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            merchantHash = jsonObject.getString("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //ProgressUtils.cancelLoading();

                        System.out.println("merchantHash"+merchantHash);
                        // merchantHash="2cb657bc86811c455a5a83b4620f153c9f44259c0825444c2d2bc96c7cc765defb2706c2b6de9d0fd6106bccf0d5a4ec423f22c40bceca23c90d0fdb6d7e89bb";
                        if (merchantHash.isEmpty() || merchantHash.equals("")) {

                        } else {
                            mPaymentParams.setMerchantHash(merchantHash);
                            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, CheckInCopy.this, R.style.PayUMoney, true);
                        }
                    }
                },

                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                      //  mTxvBuy.setEnabled(true);
                        if (error instanceof NoConnectionError) {
                         //   Toast.makeText(MainActivity.this, "Connect to internet Volley", Toast.LENGTH_SHORT).show();
                        } else {
                           // Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                      //  ProgressUtils.cancelLoading();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return mPaymentParams.getParams();
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
    private void ApiGenerateHash1() {


        ApiInterface apiService =
                ApiClient.getTestClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();

       // RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), params.toString());
        Call<TestModel> call = apiService.generateHashTest();

        call.enqueue(new Callback<TestModel>() {
            @Override
            public void onResponse(Call<TestModel> call, Response<TestModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL-------------RRRRRRRRRR--" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    String merchantHash = "";
                    if (response.code() == 200) {

                       // String  response_data = response.body();
                        TestModel responsedata=response.body();
                       // Config.logV("Checksum id-----------" + response_data.getChecksum());
                       // Config.logV("Product key-----------" + response_data.getProductinfo());
                      // merchantHash=response.body().getResult();


                        //System.out.println("merchantHash"+response.body().getKey());
                       launchPaymentFlow1("1",responsedata,"");



                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<TestModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }
    String value = null;
    private void ApiCheckin( String txt_addnote) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        String uniqueID = UUID.randomUUID().toString();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);


        JSONObject qjsonObj = new JSONObject();
        JSONObject queueobj = new JSONObject();
        JSONObject waitobj = new JSONObject();
        JSONObject service = new JSONObject();
        //
        // JSONObject serviceObj=new JSONObject();
        JSONArray waitlistArray = new JSONArray();
        try {

            qjsonObj.put("id", queueId);
            queueobj.put("date", formattedDate);
            queueobj.put("consumerNote", txt_addnote);
            service.put("id", serviceId);

            if (familyMEmID == 0) {
                familyMEmID = consumerID;
            }
            waitobj.put("id", familyMEmID);
            waitlistArray.put(waitobj);


            queueobj.putOpt("service", selectedService);
            queueobj.putOpt("queue", qjsonObj);
            queueobj.putOpt("waitlistingFor", waitlistArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queueobj.toString());
        Call<ResponseBody> call = apiService.Checkin(modifyAccountID, body);
        Config.logV("JSON--------------" + new Gson().toJson(queueobj.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {

                        txt_message="";
                        Toast.makeText(mContext, " Check-in saved successfully ", Toast.LENGTH_LONG).show();
                        JSONObject reader = new JSONObject(response.body().string());
                        Iterator iteratorObj = reader.keys();

                        while (iteratorObj.hasNext()) {
                            String getJsonObj = (String) iteratorObj.next();
                            System.out.println("KEY: " + "------>" + getJsonObj);
                            value = reader.getString(getJsonObj);


                        }

                        System.out.println("VALUE: " + "------>" + value);
                        // finish();
                        Config.logV("Response--isPrepayment------------------" + isPrepayment);
                        if (isPrepayment) {
                            final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                            dialog.setContentView(R.layout.prepayment);
                            dialog.show();

                            Button btn_paytm=(Button)dialog.findViewById(R.id.btn_paytm);
                            Button btn_payu=(Button)dialog.findViewById(R.id.btn_payu);
                            final EditText edt_message=(EditText) dialog.findViewById(R.id.edt_message);
                            TextView txtamt=(TextView) dialog.findViewById(R.id.txtamount);
                            txtamt.setText("Rs."+sAmountPay);
                            Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            txtamt.setTypeface(tyface1);
                            btn_payu.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //launchPaymentFlow();
                                    ApiGenerateHash1();
                                    dialog.dismiss();

                                }
                            });

                            btn_paytm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });



                        }else{
                            finish();
                        }
                    } else {
                        txt_message="";
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                        Toast.makeText(mContext, responseerror, Toast.LENGTH_LONG).show();
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
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }

    Dialog mDialog1 = null;




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   mTxvBuy.setEnabled(true);
      /*  if (mDialog1.isShowing())
            Config.closeDialog(getParent(), mDialog1);*/
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {


            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    showAlert("Payment Successful");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    showAlert("Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    showAlert("Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
            showAlert("Payment Cancelled111"+data);
        }
    }

    private Double convertStringToDouble(String str) {
        return Double.parseDouble(str);
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
    private void launchPaymentFlow() {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
        payUmoneyConfig.setPayUmoneyActivityTitle("Buy Shoe");
        payUmoneyConfig.setDoneButtonText("Pay Rs.10");


         /* String kEY="BC50nb";
          String MerchantId= "4825051";*/
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(convertStringToDouble("10"))
                .setTxnId(System.currentTimeMillis() + "")
                .setPhone(Constants.MOBILE)
                .setProductName("Shoe")
                .setFirstName(Constants.FIRST_NAME)
                .setEmail(Constants.EMAIL)
                .setsUrl(Constants.SURL)
                .setfUrl(Constants.FURL)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(Constants.DEBUG)
                .setKey(Constants.MERCHANT_KEY)
                .setMerchantId(Constants.MERCHANT_ID);

        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();
           // ApiGenerateHash1(mPaymentParams);
            calculateHashInServer(mPaymentParams);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
           // mTxvBuy.setEnabled(true);
        }
    }

    private void launchPaymentFlow1(String amount, TestModel checksumModel, String service) {
        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        // payUmoneyConfig.setPayUmoneyActivityTitle("Buy" + getResources().getString(R.string.nike_power_run));
        payUmoneyConfig.setDoneButtonText("Pay Rs." + amount);

        //  Config.logV("Response--PayU-------------------------" + checksumModel.getProductinfo().get(0).toString());



        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(convertStringToDouble("1"))
                .setTxnId(checksumModel.getTxnid())
                .setPhone("9895386009")
                .setProductName("jaldee")
                .setFirstName("lekha")
                .setEmail("lekha.george@calpinetech.com")
                .setsUrl("http://stage.bookmyconsult.com/test/payment-success")
                .setfUrl("http://stage.bookmyconsult.com/test/payment-failure")
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(true)
                .setKey("rjQUPktU")

                .setMerchantId("4934580");

        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();
            if (checksumModel.getResult().isEmpty() || checksumModel.getResult().equals("")) {
                Toast.makeText(mContext, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mDialog1 = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
                mDialog1.show();
                Config.logV("Checksum id---22222222222222--------" + checksumModel.getResult());
                mPaymentParams.setMerchantHash(checksumModel.getResult());
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, mActivity, R.style.PayUMoney, false);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage()+"test", Toast.LENGTH_LONG).show();
           /* if (mDialog1.isShowing())
                Config.closeDialog(getParent(), mDialog1);*/
            // mTxvBuy.setEnabled(true);
        }
    }

}
