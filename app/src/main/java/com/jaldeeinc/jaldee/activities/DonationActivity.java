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
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.EmailEditWindow;
import com.jaldeeinc.jaldee.custom.MobileNumberDialog;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.response.ActiveDonation;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonationActivity extends AppCompatActivity implements IPaymentResponse, PaymentResultWithDataListener, IMailSubmit, IMobileSubmit, IConsumerNameSubmit {


    @BindView(R.id.ll_main)
    LinearLayout llMain;

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
    EditText tvConsumerName;

    @BindView(R.id.tv_error_donor)
    CustomTextViewMedium tvDonorError;

    @BindView(R.id.tv_errorAmount)
    CustomTextViewMedium tvErrorAmount;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.tv_mobile)
    EditText etNumber;

    @BindView(R.id.tv_email)
    EditText tvEmail;

    @BindView(R.id.tv_note)
    EditText et_note;

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
    private String phoneNumber;
    private SearchDonation serviceInfo = new SearchDonation();
    private EmailEditWindow emailEditWindow;
    private MobileNumberDialog mobileNumberDialog;
    private ConsumerNameDialog consumerNameDialog;
    private IMailSubmit iMailSubmit;
    private IMobileSubmit iMobileSubmit;
    private IConsumerNameSubmit iConsumerNameSubmit;
    ProfileModel profileDetails;
    private ActiveDonation activeDonation;
    private String countryCode;
    String mFirstName;
    String mLastName;
    private String locationName;

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
        tvEmail.setTypeface(tyface);
        etNumber.setTypeface(tyface);
        tvAmountHint.setTextColor(Color.parseColor("#484848"));

        SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
        SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");
        Intent intent = getIntent();
        uniqueId = intent.getIntExtra("uniqueID", 0);
        providerName = intent.getStringExtra("providerName");
        providerId = intent.getIntExtra("providerId", 0);
        locationId = intent.getIntExtra("locationId", 0);
        locationName = intent.getStringExtra("locationName");
        serviceInfo = (SearchDonation) intent.getSerializableExtra("donationInfo");
        cvSubmit.setCardBackgroundColor(Color.parseColor("#fae8ba"));
        Spanned s1 = null;
        Spanned s2 = null;
        Spanned s3 = null;
        GradientDrawable payMthdBtnEnabled = new GradientDrawable();
        payMthdBtnEnabled.setColor(Color.parseColor("#F1B51C"));
        payMthdBtnEnabled.setCornerRadius(15);
        GradientDrawable payMthdBtnDisabled = new GradientDrawable();
        payMthdBtnDisabled.setColor(Color.parseColor("#FFFFFF"));
        payMthdBtnDisabled.setStroke(2, Color.parseColor("#d5d5d5"));
        payMthdBtnDisabled.setCornerRadius(15);
        cvRazorpay.setBackground(payMthdBtnDisabled);

        try {
            if (serviceInfo != null) {

                String name = serviceInfo.getName();
                tvServiceName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
                tvDonationName.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
                tvAmountHint.setText("Minimum" + " ₹" + Config.getMoneyFormat(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(serviceInfo.getMinDonationAmount()))) + " Maximum ₹" + Config.getMoneyFormat(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(serviceInfo.getMaxDonationAmount()))));
                llAmountHint.setVisibility(View.VISIBLE);
                tvErrorAmount.setVisibility(View.GONE);
                et_note.setHint(serviceInfo.getConsumerNoteTitle());
                if(Double.parseDouble(serviceInfo.getMinDonationAmount()) == Double.parseDouble(serviceInfo.getMaxDonationAmount())) {
                    etAmount.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(serviceInfo.getMinDonationAmount())));
                    tvSubmit.setText("Donate now");
                    cvSubmit.setCardBackgroundColor(Color.parseColor("#F1B51C"));
                }
                SpannableStringBuilder builder = new SpannableStringBuilder();
                if (serviceInfo.getDescription() != null && !serviceInfo.getDescription().equals("")) {
                    s1 = Html.fromHtml(serviceInfo.getDescription() + "<br><br>");
                    builder.append(s1);
                }
                if (serviceInfo.getPreInfoTitle() != null && !serviceInfo.getPreInfoTitle().equals("")) {
                    s2 = Html.fromHtml("<b><font color='#484848'>" + serviceInfo.getPreInfoTitle() + "</font></b>");
                    builder.append(s2);
                }
                if (serviceInfo.getPreInfoText() != null && !serviceInfo.getPreInfoText().equals("")) {
                    s3 = Html.fromHtml("<br><font color='#484848'>" + serviceInfo.getPreInfoText() + "</font>");
                    builder.append(s3);
                }

                tvDescription.setText(builder);
                addReadMore(builder.toString(), tvDescription);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mFirstName = SharedPreference.getInstance(mContext).getStringValue("firstname", "");
        mLastName = SharedPreference.getInstance(mContext).getStringValue("lastname", "");
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
        llMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (etAmount.isFocused()) {
                        etAmount.clearFocus();
                    }
                }
                return false;
            }
        });
        etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (etAmount.getText().toString().endsWith(".")) {
                        etAmount.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(etAmount.getText().toString().replace(".", ""))));
                    } else if (!etAmount.getText().toString().isEmpty()) {
                        etAmount.setText(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(etAmount.getText().toString())));
                    }
                }
            }
        });
        etAmount.addTextChangedListener(new TextWatcher() {
            String bforeTextCh;
            int cursorPosition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                bforeTextCh = s.toString();
                cursorPosition = etAmount.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etAmount.removeTextChangedListener(this);
                double amount = 0;
                String sAmount;
                String regex = "^0+(?!$)";
                Pattern mPattern = Pattern.compile("[0-9]{0,6}(\\.[0-9]{0,2})?");
                Matcher matcher = mPattern.matcher(etAmount.getText());
                if (!matcher.matches()) {
                    etAmount.setText(bforeTextCh);
                    etAmount.setSelection(cursorPosition);
                } else {
                    if (!etAmount.getText().toString().isEmpty()) {
                        if (etAmount.getText().toString().equals(".")) {
                            etAmount.setText("0.");
                            etAmount.setSelection(etAmount.getText().length());
                        } else if (etAmount.getText().toString().startsWith(".")) {
                            etAmount.setText("0".concat(etAmount.getText().toString()));
                            etAmount.setSelection(1);
                            amount = Double.parseDouble(etAmount.getText().toString());
                        } else {
                            amount = Double.parseDouble(etAmount.getText().toString());
                            sAmount = etAmount.getText().toString();
                            if (!sAmount.startsWith("0.") && sAmount.startsWith("0")) {
                                sAmount = sAmount.replaceAll(regex, "");
                                etAmount.setText(sAmount);
                                etAmount.setSelection(etAmount.getText().length());
                            }
                        }
                    }
                    if (amount > 0) {
                        tvSubmit.setText("Donate now");
                        cvSubmit.setCardBackgroundColor(Color.parseColor("#F1B51C"));
                    } else {
                        tvSubmit.setText("Donate");
                        cvSubmit.setCardBackgroundColor(Color.parseColor("#fae8ba"));
                    }
                }
                tvAmountHint.setTextColor(Color.parseColor("#484848"));
                etAmount.addTextChangedListener(this);
            }
        });

        tvEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (etAmount.isFocused()) {
                        etAmount.clearFocus();
                    }
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
                    emailEditWindow.getWindow().setGravity(Gravity.CENTER);
                    emailEditWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
                return false;
            }
        });

        etNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (etAmount.isFocused()) {
                        etAmount.clearFocus();
                    }
                    if (etNumber.getText().toString() != null) {
                        mobileNumberDialog = new MobileNumberDialog(DonationActivity.this, profileDetails, iMobileSubmit, phoneNumber, countryCode);
                        mobileNumberDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                        mobileNumberDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mobileNumberDialog.show();
                        DisplayMetrics metrics = DonationActivity.this.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        mobileNumberDialog.getWindow().setGravity(Gravity.CENTER);
                        mobileNumberDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                }
                return false;
            }
        });
        tvConsumerName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (etAmount.isFocused()) {
                        etAmount.clearFocus();
                    }
                    if (tvConsumerName.getText().toString() != null) {
                        consumerNameDialog = new ConsumerNameDialog(DonationActivity.this, profileDetails, iConsumerNameSubmit, mFirstName, mLastName);
                        consumerNameDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                        consumerNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        consumerNameDialog.show();
                        DisplayMetrics metrics = DonationActivity.this.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        consumerNameDialog.getWindow().setGravity(Gravity.CENTER);
                        consumerNameDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                }
                return false;
            }
        });


        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float amount = 0;
                float minAmount = Float.valueOf(serviceInfo.getMinDonationAmount());
                float maxAmount = Float.valueOf(serviceInfo.getMaxDonationAmount());
                int multipls = Integer.valueOf(serviceInfo.getMultiples());
                if (!etAmount.getText().toString().trim().equalsIgnoreCase("")) {
                    amount = Float.valueOf(etAmount.getText().toString());
                }
                if (etAmount.isFocused()) {
                    etAmount.clearFocus();
                }
                if (etAmount.getText().toString().trim().equalsIgnoreCase("")) {   //if amount is null or empty
                    tvErrorAmount.setVisibility(View.VISIBLE);
                    Toast.makeText(DonationActivity.this, "Please enter donation amount", Toast.LENGTH_SHORT).show();
                    llAmountHint.setVisibility(View.GONE);
                } else if (!(amount >= minAmount && amount <= maxAmount)) {
                    tvAmountHint.setTextColor(Color.parseColor("#dc3545"));
                    llAmountHint.setVisibility(View.VISIBLE);
                    tvErrorAmount.setVisibility(View.GONE);
                } else if ((multipls != 1) && (amount % multipls != 0)) {
                    Toast.makeText(DonationActivity.this, "Donation amount must be multiples of " + multipls, Toast.LENGTH_SHORT).show();
                    tvAmountHint.setTextColor(Color.parseColor("#484848"));
                    llAmountHint.setVisibility(View.VISIBLE);
                    tvErrorAmount.setVisibility(View.GONE);
                } else if (serviceInfo.isConsumerNoteMandatory() && (et_note.getText().toString().isEmpty() || et_note.getText().toString() == null)) {
                    et_note.setBackground(getResources().getDrawable(R.drawable.donate_error_edittext));
                    Toast.makeText(DonationActivity.this, "Please provide " + serviceInfo.getConsumerNoteTitle(), Toast.LENGTH_SHORT).show();
                } else {
                    if (etNumber.getText().length() > 9) {
                        ApiDonation("");
                    } else {
                        Toast.makeText(DonationActivity.this, "Mobile number should have 10 digits", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        et_note.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (true) {
                    et_note.setBackground(getResources().getDrawable(R.drawable.donate_edittext));
                }
                return false;
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
        ApiGetProfileDetail();
        ApiSearchViewTerminology(uniqueId);
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
                            mFirstName = profileDetails.getUserprofile().getFirstName();
                            mLastName = profileDetails.getUserprofile().getLastName();
                            tvConsumerName.setText(profileDetails.getUserprofile().getFirstName() + " " + profileDetails.getUserprofile().getLastName());
                            countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "");
                            phoneNumber = profileDetails.getUserprofile().getPrimaryMobileNo();
                            etNumber.setText(countryCode + " " + phoneNumber);
                            if (profileDetails.getUserprofile().getEmail() != null) {
                                tvEmail.setText(profileDetails.getUserprofile().getEmail());
                            }
                        }
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
            service.put("id", serviceInfo.getId());
            location.put("id", locationId);
            consumer.put("id", familyMEmID);
            providerConsumer.put("id", familyMEmID);
            queueobj.put("date", formattedDate);
            queueobj.put("donationAmount", etAmount.getText().toString());
            donor.put("firstName", mFirstName);
            donor.put("lastName", mLastName);
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
        getQuestionnaire(serviceInfo.getId(), providerId, queueobj);
    }

    private void getQuestionnaire(int serviceId, int accountId, JSONObject queueobj) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<Questionnaire> call = apiService.getDonationQuestions(serviceId, accountId);
        call.enqueue(new Callback<Questionnaire>() {
            @Override
            public void onResponse(Call<Questionnaire> call, Response<Questionnaire> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        Questionnaire questionnaire = response.body();

                        BookingModel model = new BookingModel();
                        model.setJsonObject(queueobj.toString());
                        model.setAccountId(accountId);
                        model.setDonationServiceInfo(serviceInfo);
                        model.setmSearchTerminology(mSearchTerminology);
                        model.setFamilyEMIID(familyMEmID);
                        model.setPhoneNumber(phoneNumber);
                        model.setQuestionnaire(questionnaire);
                        model.setFrom(Constants.DONATION);
                        model.setProviderName(providerName);
                        model.setLocationName(locationName);
                        model.setCustomerName(tvConsumerName.getText().toString());
                        model.setEmailId(tvEmail.getText().toString());
                        model.setCountryCode(countryCode);
                        model.setDonationAmount(etAmount.getText().toString());
                        if (questionnaire != null) {
                            if (questionnaire.getQuestionsList() != null) {
                                Intent intent = new Intent(DonationActivity.this, CustomQuestionnaire.class);
                                intent.putExtra("data", model);
                                intent.putExtra("from", Constants.DONATION);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(DonationActivity.this, DonationReconfirmation.class);
                                intent.putExtra("data", model);
                                startActivity(intent);
                            }
                        } else {
                            Intent intent = new Intent(DonationActivity.this, DonationReconfirmation.class);
                            intent.putExtra("data", model);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Questionnaire> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }

    @Override
    public void sendPaymentResponse(String paymentStatus, String orderid) {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(R.layout.successful_donation)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked

                        finish();

                        Intent homeIntent = new Intent(DonationActivity.this, Home.class);
                        homeIntent.putExtra("isDonation", "DONATION");
                        startActivity(homeIntent);

                    }
                })
                .show();
        CustomTextViewMedium tvProviderName = alertDialog.findViewById(R.id.tv_providerName);
        CustomTextViewMedium tvLocationName = alertDialog.findViewById(R.id.tv_locationName);
        CustomTextViewMedium tvDonorName = alertDialog.findViewById(R.id.tv_donorName);
        CustomTextViewMedium tvCause = alertDialog.findViewById(R.id.tv_cause);
        CustomTextViewMedium tvAmountPaid = alertDialog.findViewById(R.id.tv_amountPaid);
        CustomTextViewMedium tvPostInfoTitle = alertDialog.findViewById(R.id.tv_postInfoTitle);
        CustomTextViewLight tvPostInfoText = alertDialog.findViewById(R.id.tv_postInfoText);
        LinearLayout llPostInfo = alertDialog.findViewById(R.id.ll_postInfo);
        if (activeDonation != null) {
            Locale indian = new Locale("en", "IN");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(indian);
            String currency = formatter.format(Double.parseDouble(activeDonation.getDonationAmount()));
            if (activeDonation.getProviderAccount() != null) {
                tvProviderName.setText(activeDonation.getProviderAccount().getAsJsonObject().get("businessName").getAsString());
            }
            if (activeDonation.getLocation() != null) {
                tvLocationName.setText(activeDonation.getLocation().getAsJsonObject().get("place").getAsString());
            }
            if (activeDonation.getDonor() != null) {
                String firstName = "", lastName = "";
                if (activeDonation.getDonor().getAsJsonObject().get("firstName") != null) {
                    firstName = activeDonation.getDonor().getAsJsonObject().get("firstName").getAsString();
                }
                if (activeDonation.getDonor().getAsJsonObject().get("lastName") != null) {
                    lastName = activeDonation.getDonor().getAsJsonObject().get("lastName").getAsString();
                }
                tvDonorName.setText(firstName + " " + lastName);
            }
            if (activeDonation.getService() != null) {
                tvCause.setText(activeDonation.getService().getAsJsonObject().get("name").getAsString());
            }
            tvAmountPaid.setText(currency);
            if (activeDonation.getService().getAsJsonObject().get("postInfoEnabled") != null && activeDonation.getService().getAsJsonObject().get("postInfoEnabled").getAsBoolean()) {
                if (activeDonation.getService().getAsJsonObject().get("postInfoTitle") != null && !activeDonation.getService().getAsJsonObject().get("postInfoTitle").getAsString().isEmpty()) {
                    tvPostInfoTitle.setText(activeDonation.getService().getAsJsonObject().get("postInfoTitle").getAsString());
                    tvPostInfoTitle.setVisibility(View.VISIBLE);
                    llPostInfo.setVisibility(View.VISIBLE);
                }
                if (activeDonation.getService().getAsJsonObject().get("postInfoText") != null && !activeDonation.getService().getAsJsonObject().get("postInfoText").getAsString().isEmpty()) {
                    tvPostInfoText.setText(activeDonation.getService().getAsJsonObject().get("postInfoText").getAsString());
                    tvPostInfoText.setVisibility(View.VISIBLE);
                    llPostInfo.setVisibility(View.VISIBLE);
                }
            }
        }
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

                        Intent homeIntent = new Intent(DonationActivity.this, Home.class);
                        homeIntent.putExtra("isDonation", "DONATION");
                        startActivity(homeIntent);
                    }
                })
                .show();
        CustomTextViewMedium tvProviderName = alertDialog.findViewById(R.id.tv_providerName);
        CustomTextViewMedium tvLocationName = alertDialog.findViewById(R.id.tv_locationName);
        CustomTextViewMedium tvDonorName = alertDialog.findViewById(R.id.tv_donorName);
        CustomTextViewMedium tvCause = alertDialog.findViewById(R.id.tv_cause);
        CustomTextViewMedium tvAmountPaid = alertDialog.findViewById(R.id.tv_amountPaid);
        CustomTextViewMedium tvPostInfoTitle = alertDialog.findViewById(R.id.tv_postInfoTitle);
        CustomTextViewLight tvPostInfoText = alertDialog.findViewById(R.id.tv_postInfoText);
        LinearLayout llPostInfo = alertDialog.findViewById(R.id.ll_postInfo);
        if (activeDonation != null) {
            Locale indian = new Locale("en", "IN");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(indian);
            String currency = formatter.format(Double.parseDouble(activeDonation.getDonationAmount()));
            if (activeDonation.getProviderAccount() != null) {
                tvProviderName.setText(activeDonation.getProviderAccount().getAsJsonObject().get("businessName").getAsString());
            }
            if (activeDonation.getLocation() != null) {
                tvLocationName.setText(activeDonation.getLocation().getAsJsonObject().get("place").getAsString());
            }
            if (activeDonation.getDonor() != null) {
                String firstName = "", lastName = "";
                if (activeDonation.getDonor().getAsJsonObject().get("firstName") != null) {
                    firstName = activeDonation.getDonor().getAsJsonObject().get("firstName").getAsString();
                }
                if (activeDonation.getDonor().getAsJsonObject().get("lastName") != null) {
                    lastName = activeDonation.getDonor().getAsJsonObject().get("lastName").getAsString();
                }
                tvDonorName.setText(firstName + " " + lastName);
            }
            if (activeDonation.getService() != null) {
                tvCause.setText(activeDonation.getService().getAsJsonObject().get("name").getAsString());
            }
            tvAmountPaid.setText(currency);
            if (activeDonation.getService().getAsJsonObject().get("postInfoEnabled") != null && activeDonation.getService().getAsJsonObject().get("postInfoEnabled").getAsBoolean()) {
                if (activeDonation.getService().getAsJsonObject().get("postInfoTitle") != null && !activeDonation.getService().getAsJsonObject().get("postInfoTitle").getAsString().isEmpty()) {
                    tvPostInfoTitle.setText(activeDonation.getService().getAsJsonObject().get("postInfoTitle").getAsString());
                    tvPostInfoTitle.setVisibility(View.VISIBLE);
                    llPostInfo.setVisibility(View.VISIBLE);
                }
                if (activeDonation.getService().getAsJsonObject().get("postInfoText") != null && !activeDonation.getService().getAsJsonObject().get("postInfoText").getAsString().isEmpty()) {
                    tvPostInfoText.setText(activeDonation.getService().getAsJsonObject().get("postInfoText").getAsString());
                    tvPostInfoText.setVisibility(View.VISIBLE);
                    llPostInfo.setVisibility(View.VISIBLE);
                }
            }
        }
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

    }

    @Override
    public void mailUpdated(String emailId) {
        tvEmail.setText(emailId);
    }

    @Override
    public void mobileUpdated() {

        phoneNumber = SharedPreference.getInstance(mContext).getStringValue("mobile", "");
        countryCode = SharedPreference.getInstance(mContext).getStringValue("cCodeDonation", "");
        etNumber.setText(countryCode + " " + phoneNumber);
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
    public void consumerNameUpdated(String firstName, String lastName) {
        this.mFirstName = firstName;
        this.mLastName = lastName;
        tvConsumerName.setText(mFirstName + " " + mLastName);
    }
    private void addReadMore(final String text, final TextView textView) {
        SpannableString ss = new SpannableString(text.substring(0, 260) + "... read more");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                addReadLess(text, textView);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ds.setColor(getResources().getColor(R.color.design_default_color_primary_dark, getTheme()));
                } else {
                    ds.setColor(getResources().getColor(R.color.design_default_color_primary_dark));
                }
            }
        };
        ss.setSpan(clickableSpan, ss.length() - 10, ss.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void addReadLess(final String text, final TextView textView) {
        SpannableString ss = new SpannableString(text + " read less");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                addReadMore(text, textView);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ds.setColor(getResources().getColor(R.color.design_default_color_primary_dark, getTheme()));
                } else {
                    ds.setColor(getResources().getColor(R.color.design_default_color_primary_dark));
                }
            }
        };
        ss.setSpan(clickableSpan, ss.length() - 10, ss.length() , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}