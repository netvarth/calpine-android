package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.QuestionnaireUrls;
import com.jaldeeinc.jaldee.response.SearchService;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SubmitQuestionnaire;
import com.jaldeeinc.jaldee.utils.SharedPreference;
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

public class CheckInReconfirmation extends AppCompatActivity implements PaymentResultWithDataListener, IPaymentResponse {

    public Context mContext;

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

    @BindView(R.id.tv_waitingInLine)
    CustomTextViewBold tvWaitingInLine;

    @BindView(R.id.tv_customerName)
    CustomTextViewBold tvCustomerName;

    @BindView(R.id.tv_email)
    CustomTextViewBold tvEmail;

    @BindView(R.id.tv_phoneNumber)
    CustomTextViewBold tvPhoneNumber;

    @BindView(R.id.tvBookingForHint)
    CustomTextViewSemiBold tvBookingForHint;

    @BindView(R.id.tv_timeHint)
    CustomTextViewSemiBold tvTimeHint;

    String value = null;
    int familyMEmID;
    String totalAmountPay;
    String totalServicePay;
    private String prepayAmount = "";
    BookingModel bookingModel = new BookingModel();
    SearchTerminology mSearchTerminology;
    SearchService checkInInfo = new SearchService();
    String checkEncId;
    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    BottomSheetDialog dialog;
    public IPaymentResponse iPaymentResponse;
    ArrayList<LabelPath> imagePathList = new ArrayList<>();
    ArrayList<String> bookingImagesList = new ArrayList<>();
    ActiveCheckIn activeAppointment;

    //files related
    private static final String IMAGE_DIRECTORY = "/Jaldee" +
            "";
    private Uri mImageUri;
    File f;
    String path;
    Bitmap bitmap;
    File file;
    public JSONObject jsonObject;
    public boolean isPaytm = false;
    public ArrayList<FamilyArrayModel> MultipleFamilyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_reconfirmation);
        ButterKnife.bind(CheckInReconfirmation.this);
        iPaymentResponse = this;
        mContext = CheckInReconfirmation.this;

        Intent intent = getIntent();
        bookingModel = (BookingModel) intent.getSerializableExtra("data");

        if (bookingModel != null) {

            try {
                jsonObject = new JSONObject(bookingModel.getJsonObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (bookingModel.getMultipleFamilyMembers() != null && bookingModel.getMultipleFamilyMembers().size() > 0) {

                MultipleFamilyList = bookingModel.getMultipleFamilyMembers();
            } else {
                MultipleFamilyList = new ArrayList<>();
            }

            if (bookingModel.getTotalAmount() != null) {
                totalAmountPay = bookingModel.getTotalAmount();
            }

            if (bookingModel.getTotalServicePay() != null) {
                totalServicePay = bookingModel.getTotalServicePay();
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

            if (bookingModel.getCheckInInfo() != null) {

                if (bookingModel.getCheckInInfo().getName() != null) {

                    tvServiceName.setText(bookingModel.getCheckInInfo().getName());

                    if (bookingModel.getCheckInInfo().getServiceType() != null && bookingModel.getCheckInInfo().getServiceType().equalsIgnoreCase("virtualService")) {

                        if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode() != null) {

                            ivServiceIcon.setVisibility(View.VISIBLE);
                            if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {

                                ivServiceIcon.setImageResource(R.drawable.zoom);

                            } else if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {

                                ivServiceIcon.setImageResource(R.drawable.googlemeet);

                            } else if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (bookingModel.getCheckInInfo().getVirtualServiceType() != null && bookingModel.getCheckInInfo().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivServiceIcon.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivServiceIcon.setImageResource(R.drawable.whatsapp_icon);
                                }

                            } else if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {

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

            if (bookingModel.getCheckInOrToken() != null) {

                tvBookingForHint.setText(bookingModel.getCheckInOrToken());
            }

            if (bookingModel.getHint() != null) {

                tvTimeHint.setText(bookingModel.getHint());
            }

            if (bookingModel.getDate() != null) {

                tvDate.setText(bookingModel.getDate());
            }

            if (bookingModel.getPeopleWaiting() != null) {

                tvWaitingInLine.setText(bookingModel.getPeopleWaiting());
            }

            if (bookingModel.getCustomerName() != null && !bookingModel.getCustomerName().trim().equalsIgnoreCase("")) {

                tvCustomerName.setText(bookingModel.getCustomerName());

            } else if (MultipleFamilyList != null && MultipleFamilyList.size() > 0) {

                ArrayList<String> names = new ArrayList<>();

                for (FamilyArrayModel model : MultipleFamilyList) {

                    String name = model.getFirstName() + " " + model.getLastName();

                    names.add(name);
                }

                tvCustomerName.setText(TextUtils.join(", ", names));

            }

            if (bookingModel.getEmailId() != null) {

                tvEmail.setText(bookingModel.getEmailId());
            }

            if (bookingModel.getPhoneNumber() != null && bookingModel.getCountryCode() != null) {
                tvPhoneNumber.setText(bookingModel.getCountryCode() + " " + bookingModel.getPhoneNumber());
            }

            if (bookingModel.getImagesList() != null && bookingModel.getImagesList().size() > 0) {

                bookingImagesList = bookingModel.getImagesList();
            } else {
                bookingImagesList = new ArrayList<>();
            }

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
            if (bookingModel.getCheckInInfo().isPrePayment()) {
                APIPayment(String.valueOf(bookingModel.getAccountId()));
            }

            if (bookingModel.getCheckInInfo() != null && bookingModel.getCheckInInfo().getTotalAmount() != null && !bookingModel.getCheckInInfo().getTotalAmount().equalsIgnoreCase("0.0")) {
                LservicePrepay.setVisibility(View.VISIBLE);
                LserviceAmount.setVisibility(View.VISIBLE);

                String firstWord = "";
                String thirdWord;
                thirdWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(bookingModel.getCheckInInfo().getTotalAmount()));


                Spannable spannable = new SpannableString(firstWord + thirdWord);
                spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)),
                        firstWord.length(), firstWord.length() + thirdWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txtserviceamount.setText(spannable);
            }

            if (bookingModel.getCheckInInfo() != null) {

                if (bookingModel.getCheckInInfo().isPrePayment()) {

                    llPaymentOptions.setVisibility(View.VISIBLE);
                } else {

                    llPaymentOptions.setVisibility(View.GONE);
                }
            }
        }

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
                    ApiCheckIn(jsonObject, bookingModel.getAccountId());
                }
            }
        });

    }


    private void ApiCheckIn(JSONObject jsonObject, int accountId) {

        final Dialog mDialog = Config.getProgressDialog(CheckInReconfirmation.this, CheckInReconfirmation.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        ApiInterface apiService = ApiClient.getClient(CheckInReconfirmation.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.Checkin(String.valueOf(accountId), body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {


                        SharedPreference.getInstance(CheckInReconfirmation.this).setValue("refreshcheckin", "true");

                        JSONObject reader = new JSONObject(response.body().string());
                        Iterator iteratorObj = reader.keys();

                        while (iteratorObj.hasNext()) {
                            String getJsonObj = (String) iteratorObj.next();
                            System.out.println("KEY: " + "------>" + getJsonObj);
                            value = reader.getString(getJsonObj);
                            if (bookingModel.getCheckInInfo().isPrePayment()) {
                                prepayAmount = reader.getString("_prepaymentAmount");
                            }
                            break;
                        }

                        getConfirmationId(bookingModel.getAccountId(), bookingModel.getMessage());

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


                            Toast.makeText(CheckInReconfirmation.this, sb.toString(), Toast.LENGTH_LONG).show();
                        } else {
                            String responseerror = response.errorBody().string();
                            Config.logV("Response--error-------------------------" + responseerror);
                            if (response.code() != 419)
                                Toast.makeText(CheckInReconfirmation.this, responseerror, Toast.LENGTH_LONG).show();
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

    private void getConfirmationId(int id, String txt_addnote) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(value, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();

                        if (activeAppointment != null) {
                            checkEncId = activeAppointment.getCheckinEncId();

                            if (bookingModel.getCheckInInfo().isPrePayment() && ((totalAmountPay != null && !totalAmountPay.equalsIgnoreCase("0.0")) || (prepayAmount != null && Float.parseFloat(prepayAmount) > 0))) {

                                if (isPaytm) {

                                    PaytmPayment payment = new PaytmPayment(CheckInReconfirmation.this, iPaymentResponse);
                                    if (MultipleFamilyList.size() > 0) {
                                        payment.ApiGenerateHashPaytm(value, totalAmountPay, String.valueOf(id), Constants.PURPOSE_PREPAYMENT, mContext, CheckInReconfirmation.this, "", familyMEmID, checkEncId);
                                    } else {
                                        payment.ApiGenerateHashPaytm(value, prepayAmount, String.valueOf(id), Constants.PURPOSE_PREPAYMENT, mContext, CheckInReconfirmation.this, "", familyMEmID, checkEncId);
                                    }
                                } else {

                                    if (MultipleFamilyList.size() > 1) {
                                        new PaymentGateway(mContext, CheckInReconfirmation.this).ApiGenerateHash1(value, totalAmountPay, String.valueOf(id), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                    } else {
                                        new PaymentGateway(mContext, CheckInReconfirmation.this).ApiGenerateHash1(value, prepayAmount, String.valueOf(id), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                    }
                                }


                            } else {

                                if (bookingImagesList.size() > 0) {
                                    ApiCommunicateCheckin(value, String.valueOf(id), txt_addnote, dialog);
                                }

                                String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                                if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                    QuestionnaireInput input = new QuestionnaireInput();
                                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                    input = gson.fromJson(inputString, QuestionnaireInput.class);
                                    ApiSubmitQuestionnnaire(input, activeAppointment.getYnwUuid());
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
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
                Log.i("mnbbnmmnbbnm", t.toString());
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
        Call<SubmitQuestionnaire> call = apiService.submitWaitListQuestionnaire(uid, requestBody, bookingModel.getAccountId());

        call.enqueue(new Callback<SubmitQuestionnaire>() {
            @Override
            public void onResponse(Call<SubmitQuestionnaire> call, Response<SubmitQuestionnaire> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

                        SubmitQuestionnaire result = response.body();

                        if (result !=  null && result.getUrls().size() > 0){

                            for (QuestionnaireUrls url : result.getUrls()){

                                for (LabelPath p : imagePathList){

                                    if (url.getUrl().contains(p.getFileName())){

                                        p.setUrl(url.getUrl());
                                    }
                                }
                            }

                            uploadFilesToS3(imagePathList,result);
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

    private void uploadFilesToS3(ArrayList<LabelPath> filesList, SubmitQuestionnaire result) {

        try {

            ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

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
                                                ApiCheckStatus(activeAppointment.getYnwUuid(),bookingModel.getAccountId(),result);
                                            } catch (JSONException e) {
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

        for (int i = 0; i<result.getUrls().size(); i++){

            JSONObject urlObj = new JSONObject();

            urlObj.put("uid",result.getUrls().get(i).getUid());
            urlObj.put("labelName",result.getUrls().get(i).getLabelName());

            uploadArray.put(urlObj);

        }


        uploadObj.putOpt("urls",uploadArray);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), uploadObj.toString());

        Call<ResponseBody> call = apiService.checkWaitlistUploadStatus(uid,accountId,body);

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


    private void ApiCommunicateCheckin(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
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
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(bookingImagesList.get(i))));
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
        Call<ResponseBody> call = apiService.waitlistSendAttachments(waitListId, Integer.parseInt(accountID.split("-")[0]), requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        //Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();///////
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
            }
        });
    }


    private void getConfirmationDetails(int id) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(value, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {

                    if (response.code() == 200) {
                        ActiveCheckIn activeAppointment = response.body();
                        SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                        SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");
                        imagePathList.clear();
                        if (activeAppointment != null) {
                            checkEncId = activeAppointment.getCheckinEncId();

                            Intent checkin = new Intent(CheckInReconfirmation.this, CheckInConfirmation.class);
                            checkin.putExtra("terminology", mSearchTerminology.getProvider());
                            checkin.putExtra("waitlistPhonenumber", bookingModel.getPhoneNumber());
                            checkin.putExtra("livetrack", bookingModel.getCheckInInfo().isLivetrack());
                            checkin.putExtra("accountID", String.valueOf(id));
                            checkin.putExtra("uid", activeAppointment.getYnwUuid());
                            startActivity(checkin);
                        }

                    }
                } catch (Exception e) {
                    Log.i("mnbbnmmnbbnm", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
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
            MediaScannerConnection.scanFile(CheckInReconfirmation.this,
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


    boolean showPaytmWallet = false;
    boolean showPayU = false;

    private void APIPayment(String accountID) {


        ApiInterface apiService =
                ApiClient.getClient(CheckInReconfirmation.this).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(CheckInReconfirmation.this, CheckInReconfirmation.this.getResources().getString(R.string.dialog_log_in));
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
                            Typeface tyface = Typeface.createFromAsset(getAssets(),
                                    "fonts/Montserrat_Bold.otf");
                            String firstWord = "";
                            String secondWord;
                            String thirdWord;
                            if (MultipleFamilyList.size() > 1) {
                                secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(totalAmountPay));
                                thirdWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(totalServicePay));
                            } else {
                                secondWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(bookingModel.getCheckInInfo().getMinPrePaymentAmount()));
                                thirdWord = "₹ " + Config.getAmountinTwoDecimalPoints(Double.parseDouble(bookingModel.getCheckInInfo().getTotalAmount()));
                            }
                            Spannable spannable = new SpannableString(firstWord + secondWord);
                            spannable.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)),
                                    firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            txtprepayamount.setText(spannable);

                            Spannable spannable1 = new SpannableString(firstWord + thirdWord);
                            spannable1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorAccent)),
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
                        Toast.makeText(CheckInReconfirmation.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
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


    @Override
    public void sendPaymentResponse() {

        if (bookingImagesList != null && bookingImagesList.size() > 0) {
            ApiCommunicateCheckin(value, String.valueOf(bookingModel.getAccountId()), bookingModel.getMessage(), dialog);
        }

        String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

        if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

            QuestionnaireInput input = new QuestionnaireInput();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            input = gson.fromJson(inputString, QuestionnaireInput.class);
            ApiSubmitQuestionnnaire(input, activeAppointment.getYnwUuid());
        } else {

            getConfirmationDetails(bookingModel.getAccountId());
        }

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            new PaymentGateway(this.mContext, CheckInReconfirmation.this).sendPaymentStatus(razorpayModel, "SUCCESS");
            Toast.makeText(this.mContext, "Payment Successful", Toast.LENGTH_LONG).show();
            paymentFinished(razorpayModel);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    private void paymentFinished(RazorpayModel razorpayModel) {

        if (bookingImagesList != null && bookingImagesList.size() > 0) {
            ApiCommunicateCheckin(value, String.valueOf(bookingModel.getAccountId()), bookingModel.getMessage(), dialog);
        }

        String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

        if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

            QuestionnaireInput input = new QuestionnaireInput();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            input = gson.fromJson(inputString, QuestionnaireInput.class);
            ApiSubmitQuestionnnaire(input, activeAppointment.getYnwUuid());
        } else {

            getConfirmationDetails(bookingModel.getAccountId());
        }

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        try {
            AlertDialog alertDialog = new AlertDialog.Builder(CheckInReconfirmation.this).create();
            alertDialog.setTitle("Payment Failed");
            alertDialog.setMessage("Unable to process your request.Please try again after some time");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent homeIntent = new Intent(CheckInReconfirmation.this, Home.class);
                            startActivity(homeIntent);
                            finish();

                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }
}