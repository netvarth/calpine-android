package com.netvarth.youneverwait.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netvarth.youneverwait.R;
import com.netvarth.youneverwait.adapter.PaymentAdapter;
import com.netvarth.youneverwait.adapter.QueueTimeSlotAdapter;
import com.netvarth.youneverwait.common.Config;
import com.netvarth.youneverwait.connection.ApiClient;
import com.netvarth.youneverwait.connection.ApiInterface;
import com.netvarth.youneverwait.custom.CustomTypefaceSpan;
import com.netvarth.youneverwait.response.PaymentModel;
import com.netvarth.youneverwait.response.QueueTimeSlotModel;
import com.netvarth.youneverwait.response.SearchService;
import com.netvarth.youneverwait.response.SearchSetting;
import com.netvarth.youneverwait.response.SearchTerminology;
import com.netvarth.youneverwait.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
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

public class CheckIn extends AppCompatActivity {
    static Context mContext;
    static Activity mActivity;
    Spinner mSpinnerService;
    static int serviceId;
    ArrayList<SearchService> LServicesList = new ArrayList<>();
    Toolbar toolbar;
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
    static int queueId=0;

    int selectedService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_copy);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_checkin = (Button) findViewById(R.id.btn_checkin);

        //  Lpayment = (LinearLayout) findViewById(R.id.Lpayment);
        queuelayout = (LinearLayout) findViewById(R.id.queuelayout);
        txt_chooseservice = (TextView) findViewById(R.id.txt_chooseservice);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tv_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
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




        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
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
                UpdateDAte();


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
                UpdateDAte();
            }
        });


        tv_title.setText("Check-in");

        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);

        tv_titlename.setTypeface(tyface);
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
            if(mFrom.equalsIgnoreCase("searchdetail_future")||mFrom.equalsIgnoreCase("searchdetail_checkin")){
                modifyAccountID=accountID;
            }else {
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

                ApiCheckin();
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

                selectedService=((SearchService) mSpinnerService.getSelectedItem()).getId();
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


        if (mFrom.equalsIgnoreCase("checkin")||mFrom.equalsIgnoreCase("searchdetail_checkin")) {
            LcheckinDatepicker.setVisibility(View.GONE);
        } else {
            LcheckinDatepicker.setVisibility(View.VISIBLE);

            Date currentTime = new Date();
            final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            System.out.println("UTC time: " + sdf.format(currentTime));

            txt_date.setText(sdf.format(currentTime));


            UpdateDAte();


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


                        UpdateDAte();

                        ApiQueueTimeSlot(String.valueOf(serviceId), String.valueOf(mSpinnertext), modifyAccountID, mDate);
                    }
                };
    }


    public static void UpdateDAte() {
        Date selecteddate = null;
        String dtStart = txt_date.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        try {
            selecteddate = format.parse(dtStart);
            //  System.out.println(selecteddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


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

   static  int familyMEmID;
    public static void refreshName(String name ,int memID) {
        Config.logV("NAme----------" + name);
        if (name != null && !name.equalsIgnoreCase("")) {
            tv_name.setText(name);
            familyMEmID=memID;
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


                        if(mQueueTimeSlotList.get(i).getId()!=0) {
                            queueId = mQueueTimeSlotList.get(i).getId();
                        }

                        Config.logV("mQueueTimeSlotList-------------------------" + mQueueTimeSlotList.size());
                        if (mQueueTimeSlotList.size() > 0) {
                            tv_queue.setVisibility(View.VISIBLE);
                            queuelayout.setVisibility(View.VISIBLE);
                        } else {
                            tv_queue.setVisibility(View.GONE);
                            queuelayout.setVisibility(View.GONE);

                        }


                        tv_queuename.setText(mQueueTimeSlotList.get(0).getName());
                        tv_queuetime.setText(mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + mQueueTimeSlotList.get(0).getQueueSchedule().getTimeSlots().get(0).geteTime());

                        String firstWord = "Est Wait Time ";
                        String secondWord = null;
                        try {
                            secondWord = String.valueOf(mQueueTimeSlotList.get(0).getQueueWaitingTime()) + " Minutes";
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


                        if (mQueueTimeSlotList.size() > 1) {

                            ic_right.setVisibility(View.VISIBLE);
                            ic_left.setVisibility(View.VISIBLE);


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
                                    ic_left.setEnabled(true);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_active);
                                    tv_queuename.setText(mQueueTimeSlotList.get(i).getName());
                                    tv_queuetime.setText(mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).geteTime());

                                    String firstWord = "Est Wait Time ";
                                    String secondWord=null;
                                    try {
                                        secondWord = String.valueOf(mQueueTimeSlotList.get(i).getQueueWaitingTime()) + " Minutes";
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }


                                    if(mQueueTimeSlotList.get(i).getId()!=0) {
                                        queueId = mQueueTimeSlotList.get(i).getId();
                                    }

                                    if(mQueueTimeSlotList.get(i).getServiceTime()!=null){
                                        secondWord = mQueueTimeSlotList.get(i).getServiceTime() ;
                                    }

                                    Spannable spannable = new SpannableString(firstWord + secondWord);
                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tv_waittime.setText(spannable);


                                } else {
                                    ic_left.setEnabled(false);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
                                }


                                if (i < mQueueTimeSlotList.size()) {
                                    ic_right.setEnabled(true);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_active);
                                } else {
                                    ic_right.setEnabled(false);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
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
                                Config.logV("Right Click------------------" + i);
                                if (i < mQueueTimeSlotList.size()) {
                                    ic_right.setEnabled(true);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_active);
                                    tv_queuename.setText(mQueueTimeSlotList.get(i).getName());
                                    tv_queuetime.setText(mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).getsTime() + "- " + mQueueTimeSlotList.get(i).getQueueSchedule().getTimeSlots().get(0).geteTime());


                                    if(mQueueTimeSlotList.get(i).getId()!=0) {
                                        queueId = mQueueTimeSlotList.get(i).getId();
                                    }

                                    String firstWord = "Est Wait Time ";
                                    String secondWord=null;
                                    try {
                                        secondWord = String.valueOf(mQueueTimeSlotList.get(i).getQueueWaitingTime()) + " Minutes";
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }


                                    if(mQueueTimeSlotList.get(i).getServiceTime()!=null){
                                        secondWord = mQueueTimeSlotList.get(i).getServiceTime() ;
                                    }

                                    Spannable spannable = new SpannableString(firstWord + secondWord);
                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/Montserrat_Bold.otf");
                                    spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface1), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.title_grey)), 0, firstWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.violet)), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tv_waittime.setText(spannable);


                                } else {
                                    Config.logV("Right Click------------------" + i);
                                    i--;
                                    ic_right.setEnabled(false);
                                    ic_right.setImageResource(R.drawable.icon_right_angle_disabled);
                                }

                                if (i >= 0) {
                                    ic_left.setEnabled(true);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_active);
                                } else {
                                    ic_left.setEnabled(false);
                                    ic_left.setImageResource(R.drawable.icon_left_angle_disabled);
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

    private void ApiGenerateHash(String ynwUUID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        String uniqueID = UUID.randomUUID().toString();
        JSONObject jsonObj = new JSONObject();
        try {
            String androidId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            jsonObj.put("amount", "1");
            jsonObj.put("paymentMode", "DC");
            jsonObj.put("uuid", ynwUUID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.generateHash(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {


                        Config.logV("Response--cbody---------------------" + response.body().string());
                    }else{
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
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

    private void ApiCheckin() {


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
        JSONObject service=new JSONObject();
       //
        // JSONObject serviceObj=new JSONObject();
        JSONArray waitlistArray=new JSONArray();
        try {

            qjsonObj.put("id", queueId);
            queueobj.put("date", formattedDate);
            queueobj.put("consumerNote","");
            service.put("id",serviceId);

            if(familyMEmID==0){
                familyMEmID=consumerID;
            }
            waitobj.put("id",familyMEmID);
            waitlistArray.put(waitobj);



            queueobj.putOpt("service",selectedService);
            queueobj.putOpt("queue",qjsonObj);
            queueobj.putOpt("waitlistingFor",waitlistArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queueobj.toString());
        Call<ResponseBody> call = apiService.Checkin(modifyAccountID,body);
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

                        Toast.makeText(mContext," Check-in saved successfully ",Toast.LENGTH_LONG).show();
                        JSONObject reader = new JSONObject(response.body().string());
                        Iterator iteratorObj = reader .keys();
                        String value = null;
                        while (iteratorObj.hasNext())
                        {
                            String getJsonObj = (String)iteratorObj.next();
                            System.out.println("KEY: " + "------>" + getJsonObj);
                             value = reader.getString(getJsonObj);


                        }

                        System.out.println("VALUE: " + "------>" + value);
                       // finish();
                     //   Config.logV("Response--cbody---------------------" + getJsonObj);
                        ApiGenerateHash(value);
                    }else{
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                        Toast.makeText(mContext,responseerror,Toast.LENGTH_LONG).show();
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
}
