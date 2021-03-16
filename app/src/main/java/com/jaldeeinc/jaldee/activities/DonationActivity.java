package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.Interface.IConsumerNameSubmit;
import com.jaldeeinc.jaldee.Interface.IMailSubmit;
import com.jaldeeinc.jaldee.Interface.IMobileSubmit;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.ConsumerNameDialog;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.DecimalDigitsInputFilter;
import com.jaldeeinc.jaldee.custom.EmailEditWindow;
import com.jaldeeinc.jaldee.custom.MobileNumberDialog;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveDonation;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonationActivity extends AppCompatActivity implements IPaymentResponse, PaymentResultWithDataListener, IMailSubmit, IMobileSubmit, IConsumerNameSubmit {


  /*  @BindView(R.id.tv_providerName)
    CustomTextViewSemiBold tvProviderName;*/

    @BindView(R.id.tv_serviceName)
    CustomTextViewBold tvServiceName;

    @BindView(R.id.tv_description)
    CustomTextViewMedium tvDescription;

    @BindView(R.id.ll_amountHint)
    LinearLayout llAmountHint;

    @BindView(R.id.ll_consumerName)
    LinearLayout llConsumerName;

    @BindView(R.id.tv_amountHint)
    CustomTextViewMedium tvAmountHint;

    @BindView(R.id.et_amount)
    EditText etAmount;

    @BindView(R.id.tv_consumerName)
    CustomTextViewMedium tvConsumerName;

    @BindView(R.id.tv_error_donor)
    CustomTextViewMedium tvDonorError;

    @BindView(R.id.tv_errorAmount)
    CustomTextViewMedium tvErrorAmount;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.tv_mobile)
    CustomTextViewMedium tvNumber;

    @BindView(R.id.tv_email)
    CustomTextViewMedium tvEmail;

    @BindView(R.id.tv_note)
    EditText et_note;

    @BindView(R.id.ll_preinfo)
    LinearLayout llPreInfo;

    @BindView(R.id.tv_preInfoTitle)
    CustomTextViewSemiBold tvPreInfoTitle;

    @BindView(R.id.tv_preInfo)
    CustomTextViewMedium tvPreInfo;

    @BindView(R.id.tv_submit)
    CustomTextViewBold tvSubmit;

    @BindView(R.id.cv_payTm)
    CardView cvPayTm;

    @BindView(R.id.cv_razorpay)
    CardView cvRazorpay;

    @BindView(R.id.tv_errorPayment)
    CustomTextViewMedium tvErrorPayment;

    @BindView(R.id.tv_donationName)
    CustomTextViewMedium tvDonationName;
    @BindView(R.id.tv_moreInfo)
    CustomTextViewMedium tvMoreInfo;

    @BindView(R.id.ll_donation)
    LinearLayout llDonation;

    private IPaymentResponse paymentResponse;
    private int locationId;
    private int uniqueId;
    private String providerName;
    private int providerId;
    static Activity mActivity;
    static Context mContext;
    int familyMEmID;
    String value = null;
    private String phoneNumber;
    private SearchDonation serviceInfo = new SearchDonation();
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    private EmailEditWindow emailEditWindow;
    private MobileNumberDialog mobileNumberDialog;
    private ConsumerNameDialog consumerNameDialog;
    private IMailSubmit iMailSubmit;
    private IMobileSubmit iMobileSubmit;
    private IConsumerNameSubmit iConsumerNameSubmit;
    ProfileModel profileDetails;
    private ActiveDonation activeDonation;
    private String dntEncId;
    private String countryCode;
    private boolean payTm = false;
    private boolean razorPay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        ButterKnife.bind(DonationActivity.this);
        mActivity = this;
        mContext = this;
        paymentResponse = this;
        iMailSubmit = this;
        iMobileSubmit = this;
        iConsumerNameSubmit = this;
        Typeface tyface = Typeface.createFromAsset(DonationActivity.this.getAssets(),
                "fonts/JosefinSans-SemiBold.ttf");
        etAmount.setTypeface(tyface);
        tvConsumerName.setTypeface(tyface);
        tvAmountHint.setTypeface(tyface);
        //tvDescription.setTypeface(tyface);
        //tvPreInfoTitle.setTypeface(tyface);
        tvAmountHint.setTextColor(Color.parseColor("#484848"));
        Intent intent = getIntent();
        uniqueId = intent.getIntExtra("uniqueID", 0);
        providerName = intent.getStringExtra("providerName");
        providerId = intent.getIntExtra("providerId", 0);
        locationId = intent.getIntExtra("locationId", 0);
        serviceInfo = (SearchDonation) intent.getSerializableExtra("donationInfo");
        cvSubmit.setCardBackgroundColor(Color.parseColor("#7a7a7a"));
        cvPayTm.setCardBackgroundColor(Color.parseColor("#f1f0f0"));
        cvRazorpay.setCardBackgroundColor(Color.parseColor("#f1f0f0"));

       /* if (providerName != null) {
            tvProviderName.setText(providerName);
        }*/

        try {
            if (serviceInfo != null) {

                String name = serviceInfo.getName();
                tvServiceName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
                tvDonationName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
                tvDescription.setText(serviceInfo.getDescription());
                tvAmountHint.setText("Amount must be in range between" + " ₹\u00A0" + Config.getAmountinTwoDecimalPoints(Double.parseDouble(serviceInfo.getMinDonationAmount())) + " and ₹\u00A0" + Config.getAmountinTwoDecimalPoints(Double.parseDouble(serviceInfo.getMaxDonationAmount())) + " (multiples of ₹\u00A0" + Config.getAmountinTwoDecimalPoints(serviceInfo.getMultiples()) + ")");
                llAmountHint.setVisibility(View.VISIBLE);
                tvErrorAmount.setVisibility(View.GONE);
                et_note.setHint(serviceInfo.getConsumerNoteTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String mFirstName = SharedPreference.getInstance(mContext).getStringValue("firstname", "");
        String mLastName = SharedPreference.getInstance(mContext).getStringValue("lastname", "");
        int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
        familyMEmID = consumerId;
        tvConsumerName.setText(mFirstName + " " + mLastName);



        //Click-actions
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //tvSubmit.setText("Donate ₹ "+etAmount+" now");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // tvSubmit.setText("Donate ₹ "+etAmount+" now");
            }

            @Override
            public void afterTextChanged(Editable s) {
                double amount = 0;
                if (!etAmount.getText().toString().isEmpty()) {
                    amount = Double.parseDouble(etAmount.getText().toString());
                }
                if (amount > 0) {
                    if (payTm || razorPay) {
                        tvSubmit.setText("Donate ₹ " + etAmount.getText() + " now");
                        cvSubmit.setCardBackgroundColor(Color.parseColor("#F1B51C"));
                    }
                    // cvSubmit.setBackgroundColor(Color.parseColor("#F1B51C"));
                } else {
                    tvSubmit.setText("Donate");
                    cvSubmit.setCardBackgroundColor(Color.parseColor("#7a7a7a"));
                }
            }
        });
        cvPayTm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvPayTm.setCardBackgroundColor(Color.parseColor("#7a7a7a"));
                cvRazorpay.setCardBackgroundColor(Color.parseColor("#f1f0f0"));
                tvErrorPayment.setVisibility(View.GONE);
                payTm = true;
                razorPay = false;
                double amount = 0;
                if (!etAmount.getText().toString().isEmpty()) {
                    amount = Double.parseDouble(etAmount.getText().toString());
                }
                if (amount > 0) {
                    tvSubmit.setText("Donate ₹ " + etAmount.getText() + " now");
                    cvSubmit.setCardBackgroundColor(Color.parseColor("#F1B51C"));
                }

            }
        });
        cvRazorpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cvRazorpay.setCardBackgroundColor(Color.parseColor("#7a7a7a"));
                cvPayTm.setCardBackgroundColor(Color.parseColor("#f1f0f0"));
                tvErrorPayment.setVisibility(View.GONE);
                razorPay = true;
                payTm = false;
                double amount = 0;
                if (!etAmount.getText().toString().isEmpty()) {
                    amount = Double.parseDouble(etAmount.getText().toString());
                }
                if (amount > 0) {
                    tvSubmit.setText("Donate ₹ " + etAmount.getText() + " now");
                    cvSubmit.setCardBackgroundColor(Color.parseColor("#F1B51C"));
                }
            }
        });
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mailId = "";
                if (tvEmail.getText().toString() != null) {
                    mailId = tvEmail.getText().toString();
                }
                emailEditWindow = new EmailEditWindow(DonationActivity.this, profileDetails, iMailSubmit, mailId);
                emailEditWindow.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                emailEditWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                emailEditWindow.show();
                DisplayMetrics metrics = DonationActivity.this.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                emailEditWindow.getWindow().setGravity(Gravity.BOTTOM);
                emailEditWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        tvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvNumber.getText().toString() != null) {
                    mobileNumberDialog = new MobileNumberDialog(DonationActivity.this, profileDetails, iMobileSubmit, phoneNumber, countryCode);
                    mobileNumberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    mobileNumberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mobileNumberDialog.show();
                    DisplayMetrics metrics = DonationActivity.this.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    mobileNumberDialog.getWindow().setGravity(Gravity.BOTTOM);
                    mobileNumberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        });
        tvConsumerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvConsumerName.getText().toString() != null) {
                    consumerNameDialog = new ConsumerNameDialog(DonationActivity.this, profileDetails, iConsumerNameSubmit, tvConsumerName.getText().toString());
                    consumerNameDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                    consumerNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    consumerNameDialog.show();
                    DisplayMetrics metrics = DonationActivity.this.getResources().getDisplayMetrics();
                    int width = (int) (metrics.widthPixels * 1);
                    consumerNameDialog.getWindow().setGravity(Gravity.BOTTOM);
                    consumerNameDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        });


        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etAmount.getText().toString().trim().equalsIgnoreCase("")) {   //if amount is null or empty
                    tvErrorAmount.setVisibility(View.VISIBLE);
                    Toast.makeText(DonationActivity.this, "Please enter donation amount", Toast.LENGTH_SHORT).show();
                    llAmountHint.setVisibility(View.GONE);
                } else if(serviceInfo.isConsumerNoteMandatory() && (et_note.getText().toString().isEmpty() || et_note.getText().toString() == null)){
                    et_note.setBackground(getResources().getDrawable(R.drawable.donate_error_edittext));
                    Toast.makeText(DonationActivity.this, "Please provide add notes", Toast.LENGTH_SHORT).show();
                }else if (!payTm && !razorPay){   //if no select any payment method
                    tvErrorPayment.setVisibility(View.VISIBLE);
                    Toast.makeText(DonationActivity.this, "Please select payment method", Toast.LENGTH_SHORT).show();
                }else {

                    if (tvNumber.getText().length() > 9) {

                        ApiDonation("");
                    } else {
                        Toast.makeText(DonationActivity.this, "Mobile number should have 10 digits", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
        et_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                et_note.setBackground(getResources().getDrawable(R.drawable.donate_edittext));


            }
        });
        etAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvErrorAmount.setVisibility(View.GONE);
                llAmountHint.setVisibility(View.VISIBLE);
                tvAmountHint.setTextColor(Color.parseColor("#484848"));

            }
        });

        tvMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvMoreInfo.getText().toString().equalsIgnoreCase("Showmore..."))
                {
                    tvDescription.setMaxLines(Integer.MAX_VALUE);//your TextView
                    if (serviceInfo.isPreInfoEnabled()) {  //  check if pre-info is available for the service

                        llPreInfo.setVisibility(View.VISIBLE);
                        if (serviceInfo.getPreInfoTitle() != null) {
                            tvPreInfoTitle.setText(serviceInfo.getPreInfoTitle());
                        } else {
                            llPreInfo.setVisibility(View.GONE);
                        }
                        if (serviceInfo.getPreInfoText() != null) {
                            if(!serviceInfo.getPreInfoText().isEmpty()) {
                                tvPreInfo.setText((Html.fromHtml(serviceInfo.getPreInfoText())));
                            }else{
                                tvPreInfo.setVisibility(View.GONE);
                            }
                        } else   {
                            llPreInfo.setVisibility(View.GONE);
                        }
                    } else {
                        llPreInfo.setVisibility(View.GONE);
                    }
                    tvMoreInfo.setText("Showless");
                }
                else
                {
                    tvDescription.setMaxLines(3);//your TextView
                    llPreInfo.setVisibility(View.GONE);
                    tvMoreInfo.setText("Showmore...");
                }
            }
        });

        ApiGetProfileDetail();
        ApiSearchViewTerminology(uniqueId);
        APIPayment(providerId);
    }

    private void ApiGetProfileDetail() {

        ApiInterface apiService =
                ApiClient.getClient(DonationActivity.this).create(ApiInterface.class);

        final int consumerId = SharedPreference.getInstance(DonationActivity.this).getIntValue("consumerId", 0);

        final Dialog mDialog = Config.getProgressDialog(DonationActivity.this, DonationActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ProfileModel> call = apiService.getProfileDetail(consumerId);

        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        profileDetails = response.body();
                        if (profileDetails != null) {
                            tvConsumerName.setText(profileDetails.getUserprofile().getFirstName() + " " + profileDetails.getUserprofile().getLastName());
                            countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");
                            phoneNumber = profileDetails.getUserprofile().getPrimaryMobileNo();
                            tvNumber.setText(countryCode + " " + phoneNumber);

                            if (profileDetails.getUserprofile().getEmail() != null) {
                                tvEmail.setText(profileDetails.getUserprofile().getEmail());
                            } else {
                                tvEmail.setHint("Enter your Mail Id");
                            }
                        }
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });


    }


    boolean showPaytmWallet = false;
    boolean showPayU = false;

    private void APIPayment(int accountID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(String.valueOf(accountID));

        call.enqueue(new Callback<ArrayList<PaymentModel>>() {
            @Override
            public void onResponse(Call<ArrayList<PaymentModel>> call, Response<ArrayList<PaymentModel>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL----%%%%%-----------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        mPaymentData = response.body();

                        for (int i = 0; i < mPaymentData.size(); i++) {
                            if (mPaymentData.get(i).getDisplayname().equalsIgnoreCase("Wallet")) {
                                showPaytmWallet = true;
                            }

                            if (mPaymentData.get(i).getName().equalsIgnoreCase("CC") || mPaymentData.get(i).getName().equalsIgnoreCase("DC") || mPaymentData.get(i).getName().equalsIgnoreCase("NB")) {
                                showPayU = true;
                            }
                        }

                        if ((showPayU) || showPaytmWallet) {
                            Config.logV("URL----%%%%%---@@--");

                        }

                    } else {
                        Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_LONG).show();
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

    SearchTerminology mSearchTerminology;

    private void ApiSearchViewTerminology(int muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));


        Call<SearchTerminology> call = apiService.getSearchViewTerminology(muniqueID, sdf.format(currentTime));

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

    private void ApiDonation(String txt_addnote) {

        phoneNumber = tvNumber.getText().toString();
//        uuid = UUID.randomUUID().toString();
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        //  mDialog.show();

        if (tvConsumerName.getText().toString().trim().equalsIgnoreCase("")) {

            tvDonorError.setVisibility(View.VISIBLE);
            return;
        } else {
            tvDonorError.setVisibility(View.GONE);
        }

        JSONObject queueobj = new JSONObject();
        JSONObject service = new JSONObject();
        JSONObject donor = new JSONObject();
        JSONObject consumer = new JSONObject();
        JSONObject providerConsumer = new JSONObject();
        JSONObject location = new JSONObject();

        String uniqueID = UUID.randomUUID().toString();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        String formattedDate;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c);

        try {

            queueobj.put("date", formattedDate);
            queueobj.put("donationAmount", etAmount.getText().toString());
            service.put("id", serviceInfo.getId());
            location.put("id", locationId);

            consumer.put("id", familyMEmID);
            providerConsumer.put("id", familyMEmID);
            donor.put("firstName", tvConsumerName.getText().toString());

            queueobj.putOpt("service", service);
            queueobj.putOpt("consumer", consumer);
            queueobj.putOpt("donor", donor);
            queueobj.putOpt("providerConsumer", providerConsumer);
            queueobj.putOpt("location", location);
            queueobj.putOpt("donorPhoneNumber", SharedPreference.getInstance(mContext).getStringValue("mobile", ""));
            queueobj.putOpt("countryCode", countryCode);
            queueobj.putOpt("donorEmail", tvEmail.getText().toString());
            queueobj.putOpt("note", et_note.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("QueueObj Checkin", queueobj.toString());
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queueobj.toString());
        Call<ResponseBody> call = apiService.Donation(String.valueOf(providerId), body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("Response--code-------------------------" + response.body());
                    if (response.code() == 200) {
                        SharedPreference.getInstance(mContext).setValue("refreshcheckin", "true");
                        JSONObject reader = new JSONObject(response.body().string());
                        Iterator iteratorObj = reader.keys();

                        while (iteratorObj.hasNext()) {
                            String getJsonObj = (String) iteratorObj.next();
                            System.out.println("KEY: " + "------>" + getJsonObj);
                            value = reader.getString(getJsonObj);


                        }
                        getConfirmationId(providerId);

//                        if (!showPaytmWallet && !showPayU) {
//
//                            //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
//                        } else {
//                            try {
//                                final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
//                                dialog.setContentView(R.layout.prepayment);
//                                dialog.show();
//
//
//                                Button btn_paytm = (Button) dialog.findViewById(R.id.btn_paytm);
//                                Button btn_payu = (Button) dialog.findViewById(R.id.btn_payu);
//                                if (showPaytmWallet) {
//                                    btn_paytm.setVisibility(View.VISIBLE);
//                                } else {
//                                    btn_paytm.setVisibility(View.GONE);
//                                }
//                                if (showPayU) {
//                                    btn_payu.setVisibility(View.VISIBLE);
//                                } else {
//                                    btn_payu.setVisibility(View.GONE);
//                                }
//                                final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
//                                TextView txtamt = (TextView) dialog.findViewById(R.id.txtamount);
//
//                                TextView txtprepayment = (TextView) dialog.findViewById(R.id.txtprepayment);
//
//                                txtprepayment.setText("Donation Amount ");
//
//                                txtamt.setText("Rs." + getMoneyFormat(Config.getAmountinTwoDecimalPoints((Double.parseDouble(etAmount.getText().toString())))));
//                                Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
//                                        "fonts/Montserrat_Bold.otf");
//                                txtamt.setTypeface(tyface1);
//                                btn_payu.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                        new PaymentGateway(mContext, mActivity).ApiGenerateHash1(value, etAmount.getText().toString(), String.valueOf(providerId), Constants.PURPOSE_DONATIONPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
//                                        dialog.dismiss();
//                                    }
//                                });
//
//                                btn_paytm.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//
//                                        PaytmPayment payment = new PaytmPayment(mContext, paymentResponse);
//                                        payment.ApiGenerateHashPaytm(value, etAmount.getText().toString(), String.valueOf(providerId), Constants.PURPOSE_DONATIONPAYMENT, mContext, mActivity, "", familyMEmID,"");
//                                        dialog.dismiss();
//                                    }
//                                });
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }

                    } else {
                        if (response.code() == 422) {
                            float amount = Float.valueOf(etAmount.getText().toString());
                            float minAmount = Float.valueOf(serviceInfo.getMinDonationAmount());
                            float maxAmount = Float.valueOf(serviceInfo.getMaxDonationAmount());
                            float multipls = Float.valueOf(serviceInfo.getMultiples());
                            if(!(amount >= minAmount && amount <= maxAmount && amount%multipls == 0)){
                                tvAmountHint.setTextColor(Color.parseColor("#dc3545"));
                                llAmountHint.setVisibility(View.VISIBLE);
                                tvErrorAmount.setVisibility(View.GONE);
                            }
                            String errorString = response.errorBody().string();

                            Config.logV("Error String-----------" + errorString);
                            Map<String, String> tokens = new HashMap<String, String>();
                            tokens.put("Customer", Config.toTitleCase(mSearchTerminology.getCustomer()));
                            tokens.put("provider", mSearchTerminology.getProvider());
                            tokens.put("arrived", mSearchTerminology.getArrived());
                            tokens.put("waitlisted", mSearchTerminology.getWaitlist());

                            tokens.put("start", mSearchTerminology.getStart());
                            tokens.put("cancelled", mSearchTerminology.getCancelled());
                            tokens.put("done", mSearchTerminology.getDone());


                            StringBuffer sb = new StringBuffer();

                            Pattern p3 = Pattern.compile("\\[(.*?)\\]");

                            Matcher matcher = p3.matcher(errorString);
                            while (matcher.find()) {
                                System.out.println(matcher.group(1));
                                matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
                            }
                            matcher.appendTail(sb);

                            Toast.makeText(mContext, sb.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            String responseerror = response.errorBody().string();
                            Config.logV("Response--error-------------------------" + responseerror);
                            if (response.code() != 419)
                                Toast.makeText(mContext, responseerror, Toast.LENGTH_LONG).show();
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
                    Config.closeDialog(getParent(), mDialog);

            }
        });


    }

    private void getConfirmationId(int userId) {

        final ApiInterface apiService =
                ApiClient.getClient(DonationActivity.this).create(ApiInterface.class);
        Call<ActiveDonation> call = apiService.getActiveDonationUUID(value, String.valueOf(userId));
        call.enqueue(new Callback<ActiveDonation>() {
            @Override
            public void onResponse(Call<ActiveDonation> call, Response<ActiveDonation> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeDonation = response.body();
                        if (activeDonation != null) {
                            dntEncId = activeDonation.getDonationEncId();
                            if (!showPaytmWallet && !showPayU) {

                                //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    /*final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                                    dialog.setContentView(R.layout.prepayment);
                                    dialog.show();


                                    Button btn_paytm = (Button) dialog.findViewById(R.id.btn_paytm);
                                    Button btn_payu = (Button) dialog.findViewById(R.id.btn_payu);
                                    if (showPaytmWallet) {
                                        btn_paytm.setVisibility(View.VISIBLE);
                                    } else {
                                        btn_paytm.setVisibility(View.GONE);
                                    }
                                    if (showPayU) {
                                        btn_payu.setVisibility(View.VISIBLE);
                                    } else {
                                        btn_payu.setVisibility(View.GONE);
                                    }
                                    final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                                    TextView txtamt = (TextView) dialog.findViewById(R.id.txtamount);

                                    TextView txtprepayment = (TextView) dialog.findViewById(R.id.txtprepayment);

                                    txtprepayment.setText("Donation Amount ");

                                    txtamt.setText("Rs." + Config.getAmountinTwoDecimalPoints((Double.parseDouble(etAmount.getText().toString()))));
                                    Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                                            "fonts/JosefinSans-SemiBold.ttf");
                                    txtamt.setTypeface(tyface1);
                                    btn_payu.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            new PaymentGateway(mContext, mActivity).ApiGenerateHash1(value, etAmount.getText().toString(), String.valueOf(providerId), Constants.PURPOSE_DONATIONPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                            dialog.dismiss();
                                        }
                                    });

                                    btn_paytm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                            PaytmPayment payment = new PaytmPayment(mContext, paymentResponse);
                                            payment.ApiGenerateHashPaytm(value, etAmount.getText().toString(), String.valueOf(providerId), Constants.PURPOSE_DONATIONPAYMENT, mContext, mActivity, "", familyMEmID,dntEncId);
                                            dialog.dismiss();
                                        }
                                    });*/
                                    if (showPaytmWallet) {
                                        cvPayTm.setVisibility(View.VISIBLE);
                                    } else {
                                        cvPayTm.setVisibility(View.GONE);
                                    }
                                    if (showPayU) {
                                        cvRazorpay.setVisibility(View.VISIBLE);
                                    } else {
                                        cvRazorpay.setVisibility(View.GONE);
                                    }
                                    if (razorPay && !payTm) {
                                        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
                                        mDialog.show();
                                        new PaymentGateway(mContext, mActivity).ApiGenerateHash1(value, etAmount.getText().toString(), String.valueOf(providerId), Constants.PURPOSE_DONATIONPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                        mDialog.dismiss();
                                    }
                                    if (!razorPay && payTm) {
                                        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
                                        mDialog.show();
                                        PaytmPayment payment = new PaytmPayment(mContext, paymentResponse);
                                        payment.ApiGenerateHashPaytm(value, etAmount.getText().toString(), String.valueOf(providerId), Constants.PURPOSE_DONATIONPAYMENT, mContext, mActivity, "", familyMEmID, dntEncId);
                                        mDialog.dismiss();

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveDonation> call, Throwable t) {
            }
        });

    }


    @Override
    public void sendPaymentResponse() {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(R.layout.successful_donation)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        finish();
                    }
                })
                .show();
    }

    public void paymentFinished() {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(R.layout.successful_donation)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        finish();
                    }
                })
                .show();

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            new PaymentGateway(mContext, mActivity).sendPaymentStatus(razorpayModel, "SUCCESS");
            paymentFinished();

        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        try {
            Toast.makeText(mContext, "Payment failed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }

    @Override
    public void mailUpdated() {

        String mail = SharedPreference.getInstance(mContext).getStringValue("email", "");
        tvEmail.setText(mail);
    }

    @Override
    public void mobileUpdated() {

        String phone = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        countryCode = SharedPreference.getInstance(mContext).getStringValue("cCodeDonation", "");
        phoneNumber = phone;
        tvNumber.setText(countryCode + " " + phone);
    }

    public static String getMoneyFormat(String number) {

        if (!number.isEmpty()) {
            double val = Double.parseDouble(number);
            return NumberFormat.getNumberInstance(Locale.US).format(val);
        } else {
            return "0";
        }
    }

    @Override
    public void consumerNameUpdated() {
        String consumerName = SharedPreference.getInstance(mContext).getStringValue("consumerName", "");
        tvConsumerName.setText(consumerName);
    }
}