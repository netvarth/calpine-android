package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

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
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.SearchTerminology;
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
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    String value = null;
    int familyMEmID;
    private String prepayAmount = "";
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
    private static final String IMAGE_DIRECTORY = "/Jaldee" +
            "";
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

                Type labelPathType = new TypeToken<ArrayList<LabelPath>>(){}.getType();

                try {
                    ArrayList<LabelPath> pathList = new Gson().fromJson(imagesString, labelPathType);
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
            }

            if (bookingModel.getServiceInfo() != null) {

                if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {

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
                    ApiAppointment(jsonObject, bookingModel.getAccountId());
                }
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

                        getConfirmationId(bookingModel.getAccountId(), bookingModel.getMessage());

                    } else {
                        if (response.code() == 422) {

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
        Call<ResponseBody> call = apiService.submitAppointmentQuestionnaire(uid, requestBody,bookingModel.getAccountId());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {
                        imagePathList.clear();


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
                            String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                            if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                QuestionnaireInput input = new QuestionnaireInput();
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                input = gson.fromJson(inputString, QuestionnaireInput.class);
                                ApiSubmitQuestionnnaire(input,activeAppointment.getUid());
                            }
                            if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true") && (prepayAmount != null && Float.parseFloat(prepayAmount) > 0)) {

                                if (isPaytm) {

                                    PaytmPayment payment = new PaytmPayment(ReconfirmationActivity.this, iPaymentResponse);
                                    payment.ApiGenerateHashPaytm(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, ReconfirmationActivity.this, ReconfirmationActivity.this, "", familyMEmID, appEncId);

                                } else {

                                    new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this).ApiGenerateHash1(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                }
                            } else {
                                if (bookingImagesList.size() > 0) {
                                    ApiCommunicateAppointment(value, String.valueOf(bookingModel.getAccountId()), txt_addnote, dialog);
                                }
                                getConfirmationDetails(bookingModel.getAccountId());

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

    private void ApiCommunicateAppointment(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {

        ApiInterface apiService = ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
        MediaType type ;
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < bookingImagesList.size(); i++) {

            String extension = "";

            if (bookingImagesList.get(i).contains(".")) {
                extension = bookingImagesList.get(i).substring(bookingImagesList.get(i).lastIndexOf(".") + 1);
            }

            if (extension.equalsIgnoreCase("pdf")){
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
            }
        });

    }

    private void getConfirmationDetails(int userId) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(value, String.valueOf(userId));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();
                        SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE,"");
                        SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES,"");

                        if (activeAppointment != null) {
                            appEncId = activeAppointment.getAppointmentEncId();
                            Bundle b = new Bundle();
                            b.putSerializable("BookingDetails", activeAppointment);
                            b.putString("terminology", mSearchTerminology.getProvider());
                            b.putString("from", "");
                            b.putString("waitlistPhonenumber", bookingModel.getPhoneNumber());
                            b.putString("accountID", String.valueOf(bookingModel.getAccountId()));
                            b.putString("livetrack", bookingModel.getServiceInfo().getLivetrack());
                            b.putString("confId", value);
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
        getConfirmationDetails(bookingModel.getAccountId());
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
        getConfirmationDetails(bookingModel.getAccountId());
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
}