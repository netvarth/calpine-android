package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.ServicesAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.ProviderUserModel;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.Catalog;
import com.jaldeeinc.jaldee.response.DepServiceInfo;
import com.jaldeeinc.jaldee.response.DepartmentInfo;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.Questionnaire;
import com.jaldeeinc.jaldee.response.QuestionnaireUrls;
import com.jaldeeinc.jaldee.response.QueueList;
import com.jaldeeinc.jaldee.response.ScheduleList;
import com.jaldeeinc.jaldee.response.SearchAppoinment;
import com.jaldeeinc.jaldee.response.SearchDepartmentServices;
import com.jaldeeinc.jaldee.response.SearchDonation;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.ServiceInfo;
import com.jaldeeinc.jaldee.response.WalletCheckSumModel;
import com.jaldeeinc.jaldee.response.SubmitQuestionnaire;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReconfirmationActivity extends AppCompatActivity implements PaymentResultWithDataListener, IPaymentResponse {

    @BindView(R.id.LservicePrepay)
    LinearLayout LservicePrepay;

    @BindView(R.id.txtprepayamount)
    CustomTextViewSemiBold txtprepayamount;

    @BindView(R.id.txtserviceamount)
    CustomTextViewSemiBold txtserviceamount;

    @BindView(R.id.ll_prepay)
    LinearLayout LPrepay;

    @BindView(R.id.ll_serviceamount)
    LinearLayout LserviceAmount;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_paytm)
    CardView cvPaytm;

    @BindView(R.id.cv_razorpay)
    CardView cvRazorpay;

    @BindView(R.id.ll_paymentOptions)
    LinearLayout llPaymentOptions;

    @BindView(R.id.tv_buttonName)
    CustomTextViewBold tvButtonName;

    @BindView(R.id.tv_paytm)
    CustomTextViewMedium tvPaytm;

    @BindView(R.id.tv_razorpay)
    CustomTextViewMedium tvRazorpay;

    @BindView(R.id.tv_providerName)
    CustomTextViewBold tvProviderName;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.tv_serviceName)
    CustomTextViewSemiBold tvServiceName;

    @BindView(R.id.iv_serviceIcon)
    ImageView ivServiceIcon;

    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;

    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;

    @BindView(R.id.tv_customerName)
    CustomTextViewBold tvCustomerName;

    @BindView(R.id.tv_email)
    CustomTextViewBold tvEmail;

    @BindView(R.id.tv_phoneNumber)
    CustomTextViewBold tvPhoneNumber;

    @BindView(R.id.cb_jCash)
    CheckBox cbJCash;

    @BindView(R.id.ll_jCash)
    LinearLayout llJCash;

    @BindView(R.id.tv_jCashHint)
    CustomTextViewMedium tvJCashHint;

    String value = null;
    int familyMEmID;
    private String prepayAmount = "", prePayRemainingAmount = "";
    BookingModel bookingModel = new BookingModel();
    SearchTerminology mSearchTerminology;
    ActiveAppointment activeAppointment = new ActiveAppointment();
    String appEncId;
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    BottomSheetDialog dialog;
    public IPaymentResponse iPaymentResponse;
    ArrayList<LabelPath> imagePathList = new ArrayList<>();
    ArrayList<String> bookingImagesList = new ArrayList<>();
    public Context mContext;

    //files related
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    private Uri mImageUri;
    File f;
    String path;
    Bitmap bitmap;
    File file;
    public JSONObject jsonObject;
    public boolean isPaytm = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconfirmation);
        ButterKnife.bind(ReconfirmationActivity.this);
        iPaymentResponse = this;
        mContext = ReconfirmationActivity.this;
        cbJCash.setChecked(false);

        Intent intent = getIntent();
        bookingModel = (BookingModel) intent.getSerializableExtra("data");

        if (bookingModel != null) {

            try {
                jsonObject = new JSONObject(bookingModel.getJsonObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // to set providerName
            if (bookingModel.getProviderName() != null) {

                if (bookingModel.getAccountBusinessName() != null) {

                    tvProviderName.setText(bookingModel.getProviderName() + ", " + bookingModel.getAccountBusinessName());

                } else {

                    tvProviderName.setText(bookingModel.getProviderName());
                }
            }

            if (bookingModel.getLocationName() != null) {
                tvLocationName.setText(bookingModel.getLocationName());
            }

            if (bookingModel.getServiceInfo() != null) {

                if (bookingModel.getServiceInfo().getServiceName() != null) {

                    tvServiceName.setText(bookingModel.getServiceInfo().getServiceName());

                    if (bookingModel.getServiceInfo().getServiceType() != null && bookingModel.getServiceInfo().getServiceType().equalsIgnoreCase("virtualService")) {

                        if (bookingModel.getServiceInfo().getCallingMode() != null) {

                            ivServiceIcon.setVisibility(View.VISIBLE);
                            if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("Zoom")) {

                                ivServiceIcon.setImageResource(R.drawable.zoom);

                            } else if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("GoogleMeet")) {

                                ivServiceIcon.setImageResource(R.drawable.googlemeet);

                            } else if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (bookingModel.getServiceInfo().getVirtualServiceType() != null && bookingModel.getServiceInfo().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivServiceIcon.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivServiceIcon.setImageResource(R.drawable.whatsapp_icon);
                                }

                            } else if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("VideoCall")) {

                                ivServiceIcon.setImageResource(R.drawable.ic_jaldeevideo);

                            } else {
                                ivServiceIcon.setVisibility(View.GONE);
                            }
                        } else {

                            ivServiceIcon.setVisibility(View.GONE);

                        }
                    } else {

                        ivServiceIcon.setVisibility(View.GONE);
                    }

                }
            }

            if (bookingModel.getDate() != null) {

                tvDate.setText(bookingModel.getDate());
            }

            if (bookingModel.getTime() != null) {

                tvTime.setText(bookingModel.getTime());
            }

            if (bookingModel.getCustomerName() != null) {

                tvCustomerName.setText(bookingModel.getCustomerName());
            }

            if (bookingModel.getEmailId() != null) {

                tvEmail.setText(bookingModel.getEmailId());
            }

            if (bookingModel.getPhoneNumber() != null && bookingModel.getCountryCode() != null) {
                tvPhoneNumber.setText(bookingModel.getCountryCode() + " " + bookingModel.getPhoneNumber());
            }

            bookingImagesList = bookingModel.getImagesList();
            String imagesString = SharedPreference.getInstance(mContext).getStringValue(Constants.QIMAGES, "");

            if (imagesString != null && !imagesString.trim().equalsIgnoreCase("")) {

                Type labelPathListType = new TypeToken<ArrayList<LabelPath>>() {
                }.getType();

                try {
                    ArrayList<LabelPath> pathList = new Gson().fromJson(imagesString, labelPathListType);
                    imagePathList = pathList;
                } catch (JsonSyntaxException e) {
                    imagePathList = new ArrayList<>();
                    e.printStackTrace();
                }

            }
            mSearchTerminology = bookingModel.getmSearchTerminology();
            familyMEmID = bookingModel.getFamilyEMIID();
            if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
                APIPayment(String.valueOf(bookingModel.getAccountId()));
            }

            updateUI(bookingModel.getServiceInfo(), bookingModel.getEligibleJcashAmt());

            /*if (bookingModel.getServiceInfo() != null) {

                if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {

                    llPaymentOptions.setVisibility(View.VISIBLE);
                } else {

                    llPaymentOptions.setVisibility(View.GONE);
                }
            }*/
        }
        cbJCash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cbJCash.isChecked()) {
                    if (bookingModel.getEligibleJcashAmt() >= bookingModel.getAmountRequiredNow()) {
                        tvButtonName.setText("Confirm");
                        llPaymentOptions.setVisibility(View.GONE);
                    } else {
                        tvButtonName.setText("Proceed to Payment");
                        llPaymentOptions.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvButtonName.setText("Proceed to Payment");
                    if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
                        llPaymentOptions.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        cvPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isPaytm = true;
                cvRazorpay.setCardBackgroundColor(getResources().getColor(R.color.unselect));
                tvRazorpay.setTextColor(getResources().getColor(R.color.location_theme));
                cvPaytm.setCardBackgroundColor(getResources().getColor(R.color.location_theme));
                tvPaytm.setTextColor(getResources().getColor(R.color.white));


            }
        });

        cvRazorpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isPaytm = false;
                cvPaytm.setCardBackgroundColor(getResources().getColor(R.color.unselect));
                tvPaytm.setTextColor(getResources().getColor(R.color.location_theme));
                cvRazorpay.setCardBackgroundColor(getResources().getColor(R.color.location_theme));
                tvRazorpay.setTextColor(getResources().getColor(R.color.white));

            }
        });

        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bookingModel != null) {
                    ApiAppointment(jsonObject, bookingModel.getAccountId());
                }
            }
        });

    }

    private void uploadFilesToS3(ArrayList<LabelPath> filesList, SubmitQuestionnaire result) {

        try {

            ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

            final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
            mDialog.show();
            List<Observable<?>> requests = new ArrayList<>();

            for (LabelPath l : filesList) {

                if (l.getUrl() != null && !l.getUrl().trim().equalsIgnoreCase("")) {

                    RequestBody requestFile = RequestBody.create(MediaType.parse(l.getType()), new File(l.getPath()));

                    requests.add(apiService.uploadPreSignedS3File(l.getUrl(), requestFile));
                }
            }

            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests


                    // do something with those results and emit new event
                    return objects;
                }
            })
                    // After all requests had been performed the next observer will receive the Object, returned from Function

                    .subscribe(
                            // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                            new Consumer<Object>() {
                                @Override
                                public void accept(Object object) throws Exception {
                                    //Do something on successful completion of all requests
                                    Log.e("ListOf Calls", "0");

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Stuff that updates the UI

                                            try {

                                                if (mDialog.isShowing())
                                                    Config.closeDialog(getParent(), mDialog);

                                                ApiCheckStatus(activeAppointment.getUid(), bookingModel.getAccountId(), result);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                }
                            },

                            // Will be triggered if any error during requests will happen
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable e) throws Exception {
                                    Log.e("ListOf Calls", "1");

                                    //Do something on error completion of requests
                                }
                            }
                    );


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ApiCheckStatus(String uid, int accountId, SubmitQuestionnaire result) throws JSONException {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        JSONObject uploadObj = new JSONObject();
        JSONArray uploadArray = new JSONArray();

        for (int i = 0; i < result.getUrls().size(); i++) {

            JSONObject urlObj = new JSONObject();

            urlObj.put("uid", result.getUrls().get(i).getUid());
            urlObj.put("labelName", result.getUrls().get(i).getLabelName());

            uploadArray.put(urlObj);

        }

        uploadObj.putOpt("urls", uploadArray);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), uploadObj.toString());

        Call<ResponseBody> call = apiService.checkAppointmentUploadStatus(uid, accountId, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

                        getConfirmationDetails(accountId);


                    } else {
                        if (response.code() == 422) {
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
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });

    }


    private void ApiAppointment(JSONObject jsonObject, int accountId) {

        final Dialog mDialog = Config.getProgressDialog(ReconfirmationActivity.this, ReconfirmationActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        ApiInterface apiService = ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.Appointment(String.valueOf(accountId), body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {


                        SharedPreference.getInstance(ReconfirmationActivity.this).setValue("refreshcheckin", "true");

                        JSONObject reader = new JSONObject(response.body().string());
                        Iterator iteratorObj = reader.keys();

                        while (iteratorObj.hasNext()) {
                            String getJsonObj = (String) iteratorObj.next();
                            System.out.println("KEY: " + "------>" + getJsonObj);
                            value = reader.getString(getJsonObj);
                            if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
                                prepayAmount = reader.getString("_prepaymentAmount");
                            }

                        }

                        if (cbJCash.isChecked()) {
                            getPrePayRemainingAmntNeeded(prepayAmount, cbJCash.isChecked(), bookingModel.getAccountId());
                        } else {
                            getConfirmationId(bookingModel.getAccountId(), bookingModel.getMessage());
                        }

                    } else {
                        if (response.code() == 422) {

                            String errorString = response.errorBody().string();

                            Config.logV("Error String-----------" + errorString);
                            Map<String, String> tokens = new HashMap<String, String>();
                            tokens.put("Customer", Config.toTitleCase(mSearchTerminology.getCustomer()));
                            tokens.put("provider", mSearchTerminology.getProvider());
                            tokens.put("arrived", mSearchTerminology.getArrived());
                            tokens.put("waitlisted", mSearchTerminology.getWaitlisted());

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

                            System.out.println("SubString@@@@@@@@@@@@@" + sb.toString());


                            Toast.makeText(ReconfirmationActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            String responseerror = response.errorBody().string();
                            Config.logV("Response--error-------------------------" + responseerror);
                            if (response.code() != 419)
                                Toast.makeText(ReconfirmationActivity.this, responseerror, Toast.LENGTH_LONG).show();
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

    private void getPrePayRemainingAmntNeeded(String prepayAmount, boolean isJcashSelected, int id) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<String> call = apiService.getPrePayRemainingAmnt(isJcashSelected, false, prepayAmount);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Config.logV("URL------GET Prepay remaining amount---------" + response.raw().request().url().toString().trim());
                Config.logV("Response--code-------------------------" + response.code());
                if (response.code() == 200) {
                    prePayRemainingAmount = response.body();
                    getConfirmationId(bookingModel.getAccountId(), bookingModel.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("PrePayRemainingAmntNeed", t.toString());
                t.printStackTrace();
            }
        });
    }

    private void ApiSubmitQuestionnnaire(QuestionnaireInput input, String uid) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(imagePathList.get(i).getPath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            } else {
                file = new File(imagePathList.get(i).getPath());
            }
            mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(type, file));
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(input);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("question", "blob", body);
        RequestBody requestBody = mBuilder.build();

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<SubmitQuestionnaire> call = apiService.submitAppointmentQuestionnaire(uid, requestBody, bookingModel.getAccountId());

        call.enqueue(new Callback<SubmitQuestionnaire>() {
            @Override
            public void onResponse(Call<SubmitQuestionnaire> call, Response<SubmitQuestionnaire> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

                        SubmitQuestionnaire result = response.body();

                        if (result != null && result.getUrls().size() > 0) {

                            for (QuestionnaireUrls url : result.getUrls()) {

                                for (LabelPath p : imagePathList) {

                                    if (url.getUrl().contains(p.getFileName())) {

                                        p.setUrl(url.getUrl());
                                    }
                                }
                            }

                            uploadFilesToS3(imagePathList, result);
                        } else {

                            getConfirmationDetails(bookingModel.getAccountId());

                        }

                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SubmitQuestionnaire> call, Throwable t) {
                // Log error here since request failed
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
                Config.logV("Fail---------------" + t.toString());
            }
        });

    }


    boolean showPaytmWallet = false;
    boolean showPayU = false;

    private void APIPayment(String accountID) {


        ApiInterface apiService =
                ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(ReconfirmationActivity.this, ReconfirmationActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(accountID);

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
                            LservicePrepay.setVisibility(View.VISIBLE);
                            LPrepay.setVisibility(View.VISIBLE);

                            String firstWord = "";
                            String secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(bookingModel.getServiceInfo().getMinPrePaymentAmount()));
                            String thirdWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(bookingModel.getServiceInfo().getTotalAmount()));

                            Spannable spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new ForegroundColorSpan(ReconfirmationActivity.this.getResources().getColor(R.color.colorAccent)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            txtprepayamount.setText(spannable);

                            Spannable spannable1 = new SpannableString(firstWord + thirdWord);
                            spannable1.setSpan(new ForegroundColorSpan(ReconfirmationActivity.this.getResources().getColor(R.color.colorAccent)),
                                    firstWord.length(), firstWord.length() + thirdWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            txtserviceamount.setText(spannable1);
                        }

                        if (showPaytmWallet) {
                            cvPaytm.setVisibility(View.VISIBLE);
                        } else {
                            cvPaytm.setVisibility(View.GONE);
                        }
                        if (showPayU) {
                            cvRazorpay.setVisibility(View.VISIBLE);
                        } else {
                            cvRazorpay.setVisibility(View.GONE);
                        }

                    } else {
                        Toast.makeText(ReconfirmationActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
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


    private void getConfirmationId(int userId, String txt_addnote) {

        final ApiInterface apiService =
                ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(value, String.valueOf(userId));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {

                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();

                        if (activeAppointment != null) {
                            appEncId = activeAppointment.getAppointmentEncId();

                            if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
                                if (cbJCash.isChecked() && Double.parseDouble(prePayRemainingAmount) <= 0) {
                                    isGateWayPaymentNeeded(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, true, false, false, false, "JCASH", txt_addnote);
                                    //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                                } else if (prepayAmount != null && Float.parseFloat(prepayAmount) > 0) {
                                    if (isPaytm) {
                                        PaytmPayment payment = new PaytmPayment(ReconfirmationActivity.this, iPaymentResponse);
                                        if (cbJCash.isChecked()) {
                                            payment.ApiGenerateHashPaytm2(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", true, false, false, true, appEncId, mContext, ReconfirmationActivity.this);
                                        } else {
                                            payment.ApiGenerateHashPaytm(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, ReconfirmationActivity.this, ReconfirmationActivity.this, "", familyMEmID, appEncId);
                                        }
                                    } else {
                                        if (cbJCash.isChecked()) {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this).ApiGenerateHash2(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", true, false, true, false);
                                        } else {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this).ApiGenerateHash1(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                        }
                                    }
                                }
                            } else {
                                if (bookingImagesList.size() > 0) {
                                    ApiCommunicateAppointment(value, String.valueOf(bookingModel.getAccountId()), txt_addnote, dialog);
                                }
                                String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                                if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                    QuestionnaireInput input = new QuestionnaireInput();
                                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                    input = gson.fromJson(inputString, QuestionnaireInput.class);
                                    ApiSubmitQuestionnnaire(input, activeAppointment.getUid());
                                } else {

                                    getConfirmationDetails(bookingModel.getAccountId());
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
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
            }
        });

    }

    public void isGateWayPaymentNeeded(String ynwUUID, final String amount, String
            accountID, String purpose, boolean isJcashUsed, boolean isreditUsed,
                                       boolean isRazorPayPayment, boolean isPayTmPayment, String paymentMode, String txt_addnote) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        //  String uniqueID = UUID.randomUUID().toString();
        SharedPreference.getInstance(mContext).setValue("prePayment", false);
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("accountId", accountID);
            jsonObj.put("amountToPay", Float.valueOf(amount));
            jsonObj.put("isJcashUsed", isJcashUsed);
            jsonObj.put("isPayTmPayment", isPayTmPayment);
            jsonObj.put("isRazorPayPayment", isRazorPayPayment);
            jsonObj.put("isreditUsed", isreditUsed);
            jsonObj.put("paymentMode", paymentMode);
            jsonObj.put("paymentPurpose", purpose);
            jsonObj.put("uuid", ynwUUID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<WalletCheckSumModel> call = apiService.generateHash2(body);
        call.enqueue(new Callback<WalletCheckSumModel>() {

            @Override
            public void onResponse(Call<WalletCheckSumModel> call, Response<WalletCheckSumModel> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(ReconfirmationActivity.this, mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());


                    if (response.code() == 200) {

                        WalletCheckSumModel respnseWCSumModel = response.body();

                        if (respnseWCSumModel != null && !respnseWCSumModel.isGateWayPaymentNeeded()) {

                            if (bookingImagesList.size() > 0) {
                                ApiCommunicateAppointment(value, accountID, txt_addnote, dialog);
                            }
                            getConfirmationDetails(Integer.parseInt(accountID));
                        }
                    } else {
                        String responseerror = response.errorBody().string();
                        Config.logV("Response--error-------------------------" + responseerror);
                        Toast.makeText(mContext, responseerror, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<WalletCheckSumModel> call, Throwable t) {

            }
        });
    }

    private void ApiCommunicateAppointment(String waitListId, String accountID, String message,
                                           final BottomSheetDialog dialog) {

        ApiInterface apiService = ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
        MediaType type;
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < bookingImagesList.size(); i++) {

            String extension = "";

            if (bookingImagesList.get(i).contains(".")) {
                extension = bookingImagesList.get(i).substring(bookingImagesList.get(i).lastIndexOf(".") + 1);
            }

            if (extension.equalsIgnoreCase("pdf")) {
                type = MediaType.parse("application/pdf");

            } else {
                type = MediaType.parse("image/*");

            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(ReconfirmationActivity.this.getApplicationContext().getContentResolver(), Uri.fromFile(new File(bookingImagesList.get(i))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            } else {
                file = new File(bookingImagesList.get(i));
            }
            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
        }
        RequestBody requestBody = mBuilder.build();

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.appointmentSendAttachments(waitListId, Integer.parseInt(accountID.split("-")[0]), requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (mDialog.isShowing())
                    Config.closeDialog(ReconfirmationActivity.this, mDialog);

                try {

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        bookingImagesList.clear();
                        dialog.dismiss();


                    } else {
                        if (response.code() == 422) {
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
                    Config.closeDialog(ReconfirmationActivity.this, mDialog);

            }
        });

    }

    private void getConfirmationDetails(int userId) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(value, String.valueOf(userId));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ReconfirmationActivity.this, mDialog);

                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();
                        imagePathList.clear();
                        SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                        SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");

                        if (activeAppointment != null) {
                            appEncId = activeAppointment.getAppointmentEncId();
                            Bundle b = new Bundle();
                            b.putString("terminology", mSearchTerminology.getProvider());
                            b.putString("from", "");
                            b.putString("waitlistPhonenumber", bookingModel.getPhoneNumber());
                            b.putString("accountID", String.valueOf(bookingModel.getAccountId()));
                            b.putString("livetrack", bookingModel.getServiceInfo().getLivetrack());
                            b.putString("uid", activeAppointment.getUid());
                            Intent checkin = new Intent(ReconfirmationActivity.this, AppointmentConfirmation.class);
                            checkin.putExtras(b);
                            startActivity(checkin);
                        }

                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {

                if (mDialog.isShowing())
                    Config.closeDialog(ReconfirmationActivity.this, mDialog);

            }
        });

    }


    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (myBitmap != null) {
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        }
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(ReconfirmationActivity.this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    public void sendPaymentResponse() {

        if (bookingImagesList != null && bookingImagesList.size() > 0) {
            ApiCommunicateAppointment(value, String.valueOf(bookingModel.getAccountId()), bookingModel.getMessage(), dialog);
        }

        String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

        if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

            QuestionnaireInput input = new QuestionnaireInput();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            input = gson.fromJson(inputString, QuestionnaireInput.class);
            ApiSubmitQuestionnnaire(input, activeAppointment.getUid());
        } else {

            getConfirmationDetails(bookingModel.getAccountId());
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            new PaymentGateway(this.mContext, ReconfirmationActivity.this).sendPaymentStatus(razorpayModel, "SUCCESS");
            Toast.makeText(this.mContext, "Payment Successful", Toast.LENGTH_LONG).show();
            paymentFinished(razorpayModel);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    private void paymentFinished(RazorpayModel razorpayModel) {

        if (bookingImagesList != null && bookingImagesList.size() > 0) {
            ApiCommunicateAppointment(value, String.valueOf(bookingModel.getAccountId()), bookingModel.getMessage(), dialog);
        }

        String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

        if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

            QuestionnaireInput input = new QuestionnaireInput();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            input = gson.fromJson(inputString, QuestionnaireInput.class);
            ApiSubmitQuestionnnaire(input, activeAppointment.getUid());
        } else {

            getConfirmationDetails(bookingModel.getAccountId());
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        try {
            AlertDialog alertDialog = new AlertDialog.Builder(ReconfirmationActivity.this).create();
            alertDialog.setTitle("Payment Failed");
            alertDialog.setMessage("Unable to process your request.Please try again after some time");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent homeIntent = new Intent(ReconfirmationActivity.this, Home.class);
                            startActivity(homeIntent);
                            finish();

                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }

    public void updateUI(ServiceInfo serviceInfo, double eligibleJcashAmt) {
        if (bookingModel.getServiceInfo() != null && bookingModel.getServiceInfo().getTotalAmount() != null && !bookingModel.getServiceInfo().getTotalAmount().equalsIgnoreCase("0.0")) {
            LservicePrepay.setVisibility(View.VISIBLE);
            LserviceAmount.setVisibility(View.VISIBLE);

            String firstWord = "";
            String thirdWord;
            thirdWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(bookingModel.getServiceInfo().getTotalAmount()));

            Spannable spannable = new SpannableString(firstWord + thirdWord);
            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)),
                    firstWord.length(), firstWord.length() + thirdWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtserviceamount.setText(spannable);
            if (serviceInfo.getIsPrePayment().equalsIgnoreCase("true")) {
                if (eligibleJcashAmt > 0) {
                    cbJCash.setChecked(true);
                    llJCash.setVisibility(View.VISIBLE);
                    cbJCash.append(Config.getAmountNoOrTwoDecimalPoints(eligibleJcashAmt));
                    if (eligibleJcashAmt >= Double.parseDouble(serviceInfo.getMinPrePaymentAmount())) {
                        tvJCashHint.setVisibility(View.GONE);
                        llPaymentOptions.setVisibility(View.GONE);
                        tvButtonName.setText("Confirm");
                    } else {
                        tvJCashHint.setVisibility(View.VISIBLE);
                        llPaymentOptions.setVisibility(View.VISIBLE);
                        tvButtonName.setText("Proceed to Payment");
                    }
                } else if (eligibleJcashAmt == 0) {
                    cbJCash.setChecked(false);
                    llJCash.setVisibility(View.GONE);
                    llPaymentOptions.setVisibility(View.VISIBLE);
                    tvButtonName.setText("Proceed to Payment");
                } else {
                    cbJCash.setChecked(false);
                    llJCash.setVisibility(View.GONE);
                    llPaymentOptions.setVisibility(View.GONE);
                }
            } else {
                llPaymentOptions.setVisibility(View.GONE);
                llJCash.setVisibility(View.GONE);
            }
        } else {
            llPaymentOptions.setVisibility(View.GONE);
            llJCash.setVisibility(View.GONE);
        }
    }
}