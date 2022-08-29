package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.payment.PaytmPayment;
import com.jaldeeinc.jaldee.response.ActiveDonation;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

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

public class DonationReconfirmation extends AppCompatActivity implements PaymentResultWithDataListener, IPaymentResponse {
    public Context mContext;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.tv_providerName)
    CustomTextViewBold tvProviderName;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.tv_serviceName)
    CustomTextViewSemiBold tvServiceName;

    @BindView(R.id.tvPaymentByHint)
    CustomTextViewSemiBold tvPaymentByHint;

    @BindView(R.id.tv_customerName)
    CustomTextViewBold tvCustomerName;

    @BindView(R.id.tv_email)
    CustomTextViewBold tvEmail;

    @BindView(R.id.tv_phoneNumber)
    CustomTextViewBold tvPhoneNumber;

    @BindView(R.id.tv_amounttext)
    CustomTextViewSemiBold tvAmounttext;

    @BindView(R.id.tv_amount)
    CustomTextViewBold tvAmount;

    @BindView(R.id.ll_paymentOptions)
    LinearLayout llPaymentOptions;

    @BindView(R.id.tv_buttonName)
    CustomTextViewBold tvButtonName;

    @BindView(R.id.cv_paytm)
    CardView cvPaytm;

    @BindView(R.id.tv_paytm)
    CustomTextViewMedium tvPaytm;

    @BindView(R.id.cv_razorpay)
    CardView cvRazorpay;

    @BindView(R.id.tv_razorpay)
    CustomTextViewMedium tvRazorpay;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    @BindView(R.id.tv_international_payment_link)
    CustomTextViewBold tv_international_payment_link;

    private String dntEncId;
    private ActiveDonation activeDonation;
    SearchTerminology mSearchTerminology;
    public boolean isPaytm = false;
    BookingModel bookingModel = new BookingModel();
    String value = null;
    boolean showPaytmWallet = false;
    boolean showPayU = false;
    boolean showForInternationalPayment = false;

    ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    public JSONObject jsonObject;
    public IPaymentResponse iPaymentResponse;
    int familyMEmID;
    ArrayList<LabelPath> imagePathList = new ArrayList<>();
    File f;
    String path;
    Bitmap bitmap;
    File file;
    //files related
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_reconfirmation);
        ButterKnife.bind(DonationReconfirmation.this);
        iPaymentResponse = this;
        mContext = DonationReconfirmation.this;

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
            if (bookingModel.getDonationServiceInfo() != null) {
                if (bookingModel.getDonationServiceInfo().getName() != null) {
                    tvServiceName.setText(bookingModel.getDonationServiceInfo().getName());
                }
            }
            if (bookingModel.getCustomerName() != null && !bookingModel.getCustomerName().trim().equalsIgnoreCase("")) {
                tvCustomerName.setText(bookingModel.getCustomerName());
            }

            if (bookingModel.getEmailId() != null) {

                tvEmail.setText(bookingModel.getEmailId());
            }

            if (bookingModel.getPhoneNumber() != null && bookingModel.getCountryCode() != null) {
                tvPhoneNumber.setText(bookingModel.getCountryCode() + " " + bookingModel.getPhoneNumber());
            }
            if (bookingModel.getDonationAmount() != null && !bookingModel.getDonationAmount().trim().equalsIgnoreCase("")) {
                tvAmount.setText("₹ " + Config.getMoneyFormat(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(bookingModel.getDonationAmount()))));
            }
            int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);
            familyMEmID = consumerId;
            String imagesString = SharedPreference.getInstance(mContext).getStringValue(Constants.QIMAGES, "");
            if (imagesString != null && !imagesString.trim().equalsIgnoreCase("")) {
                Type labelPathType = new TypeToken<ArrayList<LabelPath>>() {
                }.getType();
                try {
                    ArrayList<LabelPath> pathList = new Gson().fromJson(imagesString, labelPathType);
                    imagePathList = pathList;
                } catch (JsonSyntaxException e) {
                    imagePathList = new ArrayList<>();
                    e.printStackTrace();
                }
            }
            mSearchTerminology = bookingModel.getmSearchTerminology();


            APIPayment(bookingModel.getAccountId());


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
                    ApiDonation(jsonObject, bookingModel.getAccountId());
                }
            }
        });

        tv_international_payment_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bookingModel != null) {
                    isPaytm = false;
                    ApiDonation(jsonObject, bookingModel.getAccountId());
                }
            }
        });
    }

    private void ApiDonation(JSONObject queueobj, int accountId) {
        final Dialog mDialog = Config.getProgressDialog(DonationReconfirmation.this, DonationReconfirmation.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        ApiInterface apiService = ApiClient.getClient(DonationReconfirmation.this).create(ApiInterface.class);
        Log.i("QueueObj Checkin", queueobj.toString());
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queueobj.toString());
        Call<ResponseBody> call = apiService.Donation(String.valueOf(accountId), body);
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
                        getConfirmationId(accountId);
                    } else {
                        if (response.code() == 422) {
                            /*float amount = Float.valueOf(etAmount.getText().toString());
                            float minAmount = Float.valueOf(serviceInfo.getMinDonationAmount());
                            float maxAmount = Float.valueOf(serviceInfo.getMaxDonationAmount());
                            float multipls = Float.valueOf(serviceInfo.getMultiples());
                            if (!(amount >= minAmount && amount <= maxAmount && amount % multipls == 0)) {
                                tvAmountHint.setTextColor(Color.parseColor("#dc3545"));
                                llAmountHint.setVisibility(View.VISIBLE);
                                tvErrorAmount.setVisibility(View.GONE);
                            }*/
                            String errorString = response.errorBody().string();

                            Config.logV("Error String-----------" + errorString);
                            Map<String, String> tokens = new HashMap<>();
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
                ApiClient.getClient(DonationReconfirmation.this).create(ApiInterface.class);
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
                            String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                            if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                QuestionnaireInput input = new QuestionnaireInput();
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                input = gson.fromJson(inputString, QuestionnaireInput.class);
                                ApiSubmitQuestionnnaire(input, activeDonation.getUid());
                            }
                            if (!showPaytmWallet && !showPayU) {

                                //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    /*if (showPaytmWallet) {
                                        cvPaytm.setVisibility(View.VISIBLE);
                                    } else {
                                        cvPaytm.setVisibility(View.GONE);
                                    }
                                    if (showPayU) {
                                        cvRazorpay.setVisibility(View.VISIBLE);
                                    } else {
                                        cvRazorpay.setVisibility(View.GONE);
                                    }*/
                                    if (!isPaytm) {
                                        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
                                        mDialog.show();
                                        new PaymentGateway(mContext, DonationReconfirmation.this).ApiGenerateHash1(value, bookingModel.getDonationAmount(), String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_DONATIONPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                        mDialog.dismiss();
                                    } else {
                                        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
                                        mDialog.show();
                                        PaytmPayment payment = new PaytmPayment(mContext, iPaymentResponse);
                                        payment.ApiGenerateHashPaytm(value, bookingModel.getDonationAmount(), String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_DONATIONPAYMENT, mContext, DonationReconfirmation.this, "", familyMEmID, dntEncId);
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

    private void APIPayment(int accountID) {

        GradientDrawable payMthdBtnEnabled = new GradientDrawable();
        payMthdBtnEnabled.setColor(Color.parseColor("#F1B51C"));
        //payMthdBtnEnabled.setStroke(2, Color.parseColor("#F1B51C"));
        payMthdBtnEnabled.setCornerRadius(15);
        GradientDrawable payMthdBtnDisabled = new GradientDrawable();
        // payMthdBtnDisabled.setColor(Color.parseColor("#f1f0f0"));
        payMthdBtnDisabled.setColor(Color.parseColor("#FFFFFF"));
        payMthdBtnDisabled.setStroke(2, Color.parseColor("#d5d5d5"));
        payMthdBtnDisabled.setCornerRadius(15);
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<PaymentModel>> call = apiService.getPaymentMod(String.valueOf(accountID), Constants.PURPOSE_DONATIONPAYMENT);
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

                        /*for (int i = 0; i < mPaymentData.size(); i++) {
                            if (mPaymentData.get(i).getName().equalsIgnoreCase("PPI")) {
                                showPaytmWallet = true;
                            }

                            if (mPaymentData.get(i).getName().equalsIgnoreCase("CC") || mPaymentData.get(i).getName().equalsIgnoreCase("DC") || mPaymentData.get(i).getName().equalsIgnoreCase("NB") || mPaymentData.get(i).getName().equalsIgnoreCase("UPI")) {
                                showPayU = true;
                            }
                        }*/
                        String countryCode = SharedPreference.getInstance(mContext).getStringValue("countryCode", "").replace("+", "");
                        if (mPaymentData.get(0).isJaldeeBank()) {
                            if (!countryCode.equalsIgnoreCase("91"))  // for international number it shows Razorpay gateway only
                            {
                                showPaytmWallet = false;
                                showPayU = true;
                                showForInternationalPayment = false;
                            } else {
                                showPaytmWallet = true;
                                showPayU = false;
                                showForInternationalPayment = true;
                            }
                        } else {
                            if (mPaymentData.get(0).getPayGateways().contains("PAYTM")) {
                                showPaytmWallet = true;
                            }
                            if (mPaymentData.get(0).getPayGateways().contains("RAZORPAY")) {
                                showPayU = true;
                            }
                        }

                        if ((showPayU) || showPaytmWallet) {
                            Config.logV("URL----%%%%%---@@--");

                        }
                        /*if (showPayU)
                            cvRazorpay.setVisibility(View.VISIBLE);
                        else
                            cvRazorpay.setVisibility(View.GONE);

                        if (showPaytmWallet)
                            cvPaytm.setVisibility(View.VISIBLE);
                        else
                            cvPaytm.setVisibility(View.GONE);
                        */
                        if (showPaytmWallet && showPayU) {
                            llPaymentOptions.setVisibility(View.VISIBLE);
                            tvButtonName.setText("Proceed to Payment");
                        } else if (showPayU && !showPaytmWallet) {
                            isPaytm = false;
                            llPaymentOptions.setVisibility(View.GONE);
                            tvButtonName.setText("CC/DC/UPI");
                        } else if (!showPayU && showPaytmWallet) {
                            isPaytm = true;
                            llPaymentOptions.setVisibility(View.GONE);
                            tvButtonName.setText("CC/DC/UPI");
                        }
                        if (showForInternationalPayment) {
                            tv_international_payment_link.setVisibility(View.VISIBLE);
                        } else {
                            tv_international_payment_link.setVisibility(View.GONE);
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
        Call<ResponseBody> call = apiService.submitDonationQuestionnaire(uid, requestBody, bookingModel.getAccountId());

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
            MediaScannerConnection.scanFile(DonationReconfirmation.this,
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
    public void sendPaymentResponse(String paymentStatus, String orderid) {
        if (paymentStatus.equalsIgnoreCase("TXN_SUCCESS")) {
            ApiCheckPaytmPaymentStatus(orderid);
        } else {
            paymentError();
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void setPaymentRequestId(String paymentRequestId) {

    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            //new PaymentGateway(mContext, DonationReconfirmation.this).sendPaymentStatus(razorpayModel, "SUCCESS");
            ApiCheckRazorpayPaymentStatus(razorpayModel);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    private void ApiCheckRazorpayPaymentStatus(RazorpayModel razorpayModel) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("paymentId", razorpayModel.getRazorpay_payment_id());
            jsonObj.put("orderId", razorpayModel.getRazorpay_order_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.checkRazorpayPaymentStatus(body, bookingModel.getAccountId());
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(DonationReconfirmation.this, mDialog);

                    if (response.code() == 200) {

                        paymentFinished();

                    } else {

                        paymentError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void ApiCheckPaytmPaymentStatus(String orderId) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("paymentId", orderId);
            jsonObj.put("orderId", orderId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.checkPaytmPaymentStatus(body, bookingModel.getAccountId());
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(DonationReconfirmation.this, mDialog);

                    if (response.code() == 200) {

                        paymentFinished();

                    } else {

                        paymentError();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        paymentError();
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
                        Intent homeIntent = new Intent(DonationReconfirmation.this, Home.class);
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
            //Locale indian = new Locale("en", "IN");
            //NumberFormat formatter = NumberFormat.getCurrencyInstance(indian);
            //String currency = formatter.format(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(activeDonation.getDonationAmount())));
            String currency = Config.getMoneyFormat(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(activeDonation.getDonationAmount())));
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
            tvAmountPaid.setText("₹ " + currency);
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

    private void paymentError() {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(DonationReconfirmation.this).create();
            alertDialog.setTitle("Payment Failed");
            alertDialog.setMessage("Unable to process your request.Please try again after some time");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            //Intent homeIntent = new Intent(ReconfirmationActivity.this, Home.class);
                            //startActivity(homeIntent);
                            finish();
                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String TAG = "PaytmPayment";
        Log.e(TAG, " result code " + resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 01 && data != null) {
            if (data.getStringExtra("response").contains("TXN_SUCCESS")) {
                sendPaymentResponse("TXN_SUCCESS", "");
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
            } else {
                sendPaymentResponse("TXN_FAILED", "");
            }
            Log.e(TAG, " data " + data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - " + data.getStringExtra("response"));
            /*
            data response - {"BANKNAME":"WALLET","BANKTXNID":"1394221115",
            "CHECKSUMHASH":"7jRCFIk6eRmrep+IhnmQrlrL43KSCSXrmM+VHP5pH0ekXaaxjt3MEgd1N9mLtWyu4VwpWexHOILCTAhybOo5EVDmAEV33rg2VAS/p0PXdk\u003d",
            "CURRENCY":"INR","GATEWAYNAME":"WALLET","MID":"EAcP3138556","ORDERID":"100620202152",
            "PAYMENTMODE":"PPI","RESPCODE":"01","RESPMSG":"Txn Success","STATUS":"TXN_SUCCESS/TXN_FAILURE",
            "TXNAMOUNT":"2.00","TXNDATE":"2020-06-10 16:57:45.0","TXNID":"2020061011121280011018328631290118"}
             */
        } else {
            Log.e(TAG, " payment failed");
            Toast.makeText(this, "Payment Failed ", Toast.LENGTH_LONG).show();
        }
    }
}