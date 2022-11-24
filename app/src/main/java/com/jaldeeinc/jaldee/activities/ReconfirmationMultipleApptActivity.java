package com.jaldeeinc.jaldee.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CircleTransform;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.PicassoTrustAll;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.jaldeeinc.jaldee.model.ProviderConsumerFamilyMemberModel;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.QuestionnaireUrls;
import com.jaldeeinc.jaldee.response.SubmitQuestionnaire;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

public class ReconfirmationMultipleApptActivity extends AppCompatActivity {


    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.toolbartitle)
    TextView toolbartitle;
    @BindView(R.id.tv_providerPhoneNumber)
    TextView tv_providerPhoneNumber;
    @BindView(R.id.tv_providerEmail)
    TextView tv_providerEmail;
    @BindView(R.id.ll_providerPhoneNumber)
    LinearLayout ll_providerPhoneNumber;
    @BindView(R.id.tv_locationName)
    TextView tvLocationName;
    @BindView(R.id.ll_providerEmail)
    LinearLayout ll_providerEmail;
    @BindView(R.id.tv_providerName)
    TextView tvProviderName;
    @BindView(R.id.tv_serviceName)
    TextView tvServiceName;
    @BindView(R.id.iv_serviceIcon)
    ImageView ivServiceIcon;
    @BindView(R.id.iv_teleService)
    ImageView iv_teleService;
    @BindView(R.id.tv_description)
    TextView tvDescription;
    @BindView(R.id.tv_cnsmrDetails_Heading)
    TextView tv_cnsmrDetails_Heading;
    @BindView(R.id.tv_customerName)
    TextView tvCustomerName;
    @BindView(R.id.tv_phoneNumber)
    TextView tvPhoneNumber;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.ll_preinfo)
    LinearLayout ll_preinfo;
    @BindView(R.id.tv_preInfoTitle)
    TextView tv_preInfoTitle;
    @BindView(R.id.tv_preInfo)
    TextView tv_preInfo;
    @BindView(R.id.gridLayout_booking)
    GridLayout gridLayout_booking;
    @BindView(R.id.ll_donationAmount)
    LinearLayout ll_donationAmount;
    @BindView(R.id.tv_donationAmount)
    TextView tv_donationAmount;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_date)
    LinearLayout ll_date;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_time)
    LinearLayout ll_time;
    @BindView(R.id.txtserviceamount)
    TextView txtserviceamount;
    @BindView(R.id.txtprepayamount)
    TextView txtprepayamount;
    @BindView(R.id.ll_prepay)
    LinearLayout ll_prepay;
    @BindView(R.id.ll_serviceamount)
    LinearLayout ll_serviceamount;
    @BindView(R.id.ll_fullfees_payd_advance)
    LinearLayout ll_fullfees_payd_advance;
    @BindView(R.id.ll_servicePrepay)
    LinearLayout ll_servicePrepay;
    @BindView(R.id.ll_payment_mode)
    LinearLayout ll_payment_mode;
    @BindView(R.id.ll_jCash)
    LinearLayout llJCash;
    @BindView(R.id.cb_jCash)
    CheckBox cbJCash;
    @BindView(R.id.tv_jCashHint)
    TextView tvJCashHint;
    @BindView(R.id.gv_payment_modes)
    GridView gv_payment_modes;
    @BindView(R.id.tv_buttonName)
    TextView tvButtonName;
    @BindView(R.id.ll_cancellation_policy)
    LinearLayout ll_cancellation_policy;
    @BindView(R.id.cv_cancellation_policy)
    CardView cv_cancellation_policy;
    @BindView(R.id.tv_payment_link)
    TextView tv_payment_link;
    @BindView(R.id.cv_submit)
    CardView cvSubmit;
    @BindView(R.id.iv_location_icon)
    ImageView iv_location_icon;
    @BindView(R.id.iv_prvdr_phone_icon)
    ImageView iv_prvdr_phone_icon;
    @BindView(R.id.iv_prvdr_email_icon)
    ImageView iv_prvdr_email_icon;
    @BindView(R.id.icon_text)
    TextView icon_text;
    @BindView(R.id.ll_coupon)
    LinearLayout ll_coupon;
    @BindView(R.id.ll_coupons)
    LinearLayout llCoupons;
    @BindView(R.id.cb_coupon)
    CheckBox cb_coupon;
    @BindView(R.id.rl_coupon)
    RelativeLayout rlCoupon;
    @BindView(R.id.tv_apply)
    TextView tvApply;
    @BindView(R.id.et_code)
    EditText etCode;


    /*@BindView(R.id.tv_bookingAt)
    CustomTextViewMedium tv_bookingAt;*/
    @BindView(R.id.tv_vitual_service_number)
    CustomTextViewBold tv_vitual_service_number;
    @BindView(R.id.tv_waitingInLine)
    CustomTextViewLight tvWaitingInLine;

    public Context mContext;
    public int familyMEmID;
    ArrayList<BookingModel> bookingModels = new ArrayList<>();
    List<String> uids = new ArrayList<>();
    BookingModel bookingModel;
    public int serviceId = 0;
    public ArrayList<ShoppingListModel> attachedImagePathList = new ArrayList<>();
    public File file;
    public ArrayList<LabelPath> imagePathList = new ArrayList<>();
    public ActiveAppointment activeAppointment = new ActiveAppointment();
    public String value = null;
    public JSONObject jsonObject;

    JSONArray familyArrayModelProviderConsumer = new JSONArray();
    Integer providerConsumerId;
    int consumerId;
    String providerLogoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_reconfirmation_mutiple_appt);
        setContentView(R.layout.activity_reconfirmation);

        ButterKnife.bind(ReconfirmationMultipleApptActivity.this);
        mContext = ReconfirmationMultipleApptActivity.this;

        Intent intent = getIntent();
        bookingModels = (ArrayList<BookingModel>) intent.getSerializableExtra("datas");
        consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        toolbartitle.setText("Confirm & pay");
        getQuestionnaireImages();

        String time = bookingModels
                .stream()
                .map(a -> String.valueOf(a.getTime().replaceAll(" ", "\u00a0")))
                .collect(Collectors.joining(", "));
        String convertedTime = time.replaceAll("am", "AM").replaceAll("pm", "PM");

        if (convertedTime != null && !convertedTime.equalsIgnoreCase("")) {
            ll_time.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(convertedTime);
        } else {
            ll_time.setVisibility(View.GONE);
            tvTime.setVisibility(View.GONE);
        }

        bookingModel = bookingModels.get(bookingModels.size() - 1);

        if (bookingModel != null) {
            providerLogoUrl = bookingModel.getProviderLogo();
            if (providerLogoUrl != null && !providerLogoUrl.trim().isEmpty()) {
                PicassoTrustAll.getInstance(mContext).load(providerLogoUrl).placeholder(R.drawable.service_avatar).error(R.drawable.service_avatar).transform(new CircleTransform()).fit().into(ivServiceIcon);
            }
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
            if (bookingModel.getProviderPhoneNumber() != null && !bookingModel.getProviderPhoneNumber().isEmpty()) {
                ll_providerPhoneNumber.setVisibility(View.VISIBLE);
                tv_providerPhoneNumber.setText(bookingModel.getProviderPhoneNumber());
            } else {
                ll_providerPhoneNumber.setVisibility(View.GONE);
            }
            if (bookingModel.getProviderEmail() != null && !bookingModel.getProviderEmail().isEmpty()) {
                ll_providerEmail.setVisibility(View.VISIBLE);
                tv_providerEmail.setText(bookingModel.getProviderEmail());
            } else {
                ll_providerEmail.setVisibility(View.GONE);
            }


            if (bookingModel.getDate() != null) {
                ll_date.setVisibility(View.VISIBLE);
                tvDate.setVisibility(View.VISIBLE);
                tvDate.setText(Html.fromHtml(bookingModel.getDate()));
            } else {
                ll_date.setVisibility(View.GONE);
                tvDate.setVisibility(View.GONE);
            }

            if (bookingModel.getFrom() != null) {
                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    Appointment();
                }
            }
        }
        cv_cancellation_policy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Dialog cancellationPolicyDialog = new Dialog(mContext);
                cancellationPolicyDialog.setContentView(R.layout.cancellation_policy_dialog);
                cancellationPolicyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cancellationPolicyDialog.setCancelable(false);
                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                cancellationPolicyDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);

                String cancelation_policy_2 = getResources().getString(R.string.cancelation_policy_2);
                String cancelation_policy_3 = getResources().getString(R.string.cancelation_policy_3);
                if (bookingModel.getFrom() != null) {
                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                        cancelation_policy_2 = cancelation_policy_2.replace("VARIABLE", "appointment");
                        cancelation_policy_3 = cancelation_policy_3.replace("VARIABLE", "appointment");
                    } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                        if (bookingModel.isToken) {
                            cancelation_policy_2 = cancelation_policy_2.replace("VARIABLE", "token");
                            cancelation_policy_3 = cancelation_policy_3.replace("VARIABLE", "token");
                        } else {
                            cancelation_policy_2 = cancelation_policy_2.replace("VARIABLE", "checkin");
                            cancelation_policy_3 = cancelation_policy_3.replace("VARIABLE", "checkin");
                        }
                    }
                }
                CustomTextViewLight setup_intro_bullet_first_text = cancellationPolicyDialog.findViewById(R.id.setup_intro_bullet_first_text);
                CustomTextViewLight setup_intro_bullet_second_text = cancellationPolicyDialog.findViewById(R.id.setup_intro_bullet_second_text);
                setup_intro_bullet_first_text.setText(cancelation_policy_2);
                setup_intro_bullet_second_text.setText(cancelation_policy_3);

                cancellationPolicyDialog.show();

                LinearLayout close = cancellationPolicyDialog.findViewById(R.id.ll_close);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancellationPolicyDialog.cancel();
                    }
                });
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookingModel.getAmountRequiredNow() > 0) {
                    DynamicToast.make(ReconfirmationMultipleApptActivity.this, "Booking not allowed to this service.Please contact your provider", AppCompatResources.getDrawable(
                                    ReconfirmationMultipleApptActivity.this, R.drawable.ic_info_black),
                            ContextCompat.getColor(ReconfirmationMultipleApptActivity.this, R.color.white), ContextCompat.getColor(ReconfirmationMultipleApptActivity.this, R.color.red), Toast.LENGTH_SHORT).show();

                } else {
                    if (bookingModels != null && bookingModels.size() > 0) {
                        ApiBooking(bookingModels, bookingModel.getAccountId());
                    }
                }
            }
        });
    }

    private void Appointment() {
        familyMEmID = bookingModel.getFamilyEMIID();
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
                                if (bookingModel.getWhtsappCountryCode() != null && bookingModel.getWhtsappPhoneNumber() != null) {
                                    tv_vitual_service_number.setVisibility(View.VISIBLE);
                                    tv_vitual_service_number.setText("+" + bookingModel.getWhtsappCountryCode() + " " + bookingModel.getWhtsappPhoneNumber());
                                    tv_vitual_service_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsapp_videoicon_sized, 0, 0, 0);
                                }
                            } else {
                                ivServiceIcon.setImageResource(R.drawable.whatsapp_icon);
                                if (bookingModel.getWhtsappCountryCode() != null && bookingModel.getWhtsappPhoneNumber() != null) {
                                    tv_vitual_service_number.setVisibility(View.VISIBLE);
                                    tv_vitual_service_number.setText("+" + bookingModel.getWhtsappCountryCode() + " " + bookingModel.getWhtsappPhoneNumber());
                                    tv_vitual_service_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whatsappicon_sized, 0, 0, 0);
                                }
                            }
                        } else if (bookingModel.getServiceInfo().getCallingMode().equalsIgnoreCase("phone")) {
                            ivServiceIcon.setImageResource(R.drawable.phoneaudioicon);
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
        getAttachedImages();
        serviceId = bookingModel.getServiceInfo().getServiceId();
        tvWaitingInLine.setVisibility(View.GONE);
        gridLayout_booking.setVisibility(View.VISIBLE);
        ll_donationAmount.setVisibility(View.GONE);
        //tv_bookingAt.setText("APPOINTMENT AT : ");
        tv_cnsmrDetails_Heading.setText("Booking For");
        if (bookingModel.getCustomerName() != null && !bookingModel.getCustomerName().trim().equalsIgnoreCase("")) {
            String fName = bookingModel.getCustomerName();
            if (fName != null && !fName.trim().isEmpty()) {
                icon_text.setText(String.valueOf(fName.trim().charAt(0)));
            }
            tvCustomerName.setText(bookingModel.getCustomerName());
        }
        updateUI();

        if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
            txtprepayamount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getAmountRequiredNow())));
            txtserviceamount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getNetTotal())));
            ll_serviceamount.setVisibility(View.VISIBLE);
            if (bookingModel.getAmountRequiredNow() == bookingModel.getNetTotal()) {
                ll_fullfees_payd_advance.setVisibility(View.VISIBLE);
                ll_prepay.setVisibility(View.GONE);
            } else {
                ll_fullfees_payd_advance.setVisibility(View.GONE);
                ll_prepay.setVisibility(View.VISIBLE);
            }

        } else {
            ll_serviceamount.setVisibility(View.GONE);
            ll_prepay.setVisibility(View.GONE);
        }
    }

    public void getAttachedImages() {
        if (bookingModel.getImagePathList() != null && bookingModel.getImagePathList().size() > 0) {
            attachedImagePathList = bookingModel.getImagePathList();
        } else {
            attachedImagePathList = new ArrayList<>();
        }
    }

    public void updateUI() {
        Glide.with(mContext).load(R.drawable.location_icon_1).into(iv_location_icon);
        Glide.with(mContext).load(R.drawable.phone_icon_1).into(iv_prvdr_phone_icon);
        Glide.with(mContext).load(R.drawable.email_icon_1).into(iv_prvdr_email_icon);
        if (bookingModel.getEmailId() != null && !bookingModel.getEmailId().equalsIgnoreCase("")) {
            tvEmail.setText(bookingModel.getEmailId());
        }
        if (bookingModel.getPhoneNumber() != null && bookingModel.getCountryCode() != null) {
            tvPhoneNumber.setText(bookingModel.getCountryCode() + " " + bookingModel.getPhoneNumber());
        }
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {

            ll_cancellation_policy.setVisibility(View.GONE);
            llJCash.setVisibility(View.GONE);
            tv_payment_link.setVisibility(View.GONE);
            gv_payment_modes.setVisibility(View.GONE);
            ll_payment_mode.setVisibility(View.GONE);
            ll_servicePrepay.setVisibility(View.GONE);
            txtprepayamount.setText("0");
            tvButtonName.setText("Confirm");
            toolbartitle.setText("Confirm");

            cbJCash.setChecked(false);

        }
    }

    public static Double getFloatAsDouble(Float fValue) {
        return Double.valueOf(fValue.toString());
    }

    public void ApiBooking(ArrayList<BookingModel> bookingModels, int accountId) {
        final Dialog mDialog = Config.getProgressDialog(ReconfirmationMultipleApptActivity.this, ReconfirmationMultipleApptActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), bookingModels.get(0).getJsonObject().toString());
        ApiInterface apiService = ApiClient.getClient(ReconfirmationMultipleApptActivity.this).create(ApiInterface.class);
        Call<ResponseBody> call = null;
        if (bookingModel.getFrom() != null) {
            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                call = apiService.Appointment(String.valueOf(accountId), body);
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        if (response.code() == 200) {
                            JSONObject reader = new JSONObject(response.body().string());
                            if (reader.has("parent_uuid")) {
                                value = reader.getString("parent_uuid");
                            } else if (reader.has("uid")) {
                                value = reader.getString("uid");
                            }
                            uids.add(value);
                        }
                        bookingModels.remove(0);
                        if (bookingModels.size() > 0) {
                            ApiBooking(bookingModels, accountId);
                        } else {
                            if (uids.size() == 0) {
                                //if (response.code() == 422) {
                                Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                //  }
                            } else {
                                getApptConfirmationId(bookingModel.getAccountId(), bookingModel.getMessage());
                            }
                        }
                        if (mDialog.isShowing())
                            Config.closeDialog(getParent(), mDialog);


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

    private void ApiCommunicate(String id, String accountID, String message) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaTypeAndExtention type;
        JSONObject captions = new JSONObject();
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < attachedImagePathList.size(); i++) {

            type = Config.getFileType(attachedImagePathList.get(i).getImagePath());

            file = new File(attachedImagePathList.get(i).getImagePath());//////////

            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type.getMediaTypeWithExtention(), file));
            try {
                captions.put(String.valueOf(i), attachedImagePathList.get(i).getCaption());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestBody body1 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(captions));
        mBuilder.addFormDataPart("captions", "blob", body1);

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
        Call<ResponseBody> call = null;
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
            call = apiService.appointmentSendAttachments(id, Integer.parseInt(accountID.split("-")[0]), requestBody);
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            call = apiService.waitlistSendAttachments(id, Integer.parseInt(accountID.split("-")[0]), requestBody);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        //Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();///////
                        attachedImagePathList.clear();
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

    private void ApiSubmitQuestionnnaire(QuestionnaireInput input, String uid) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {
           /*
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
            }*/
            file = new File(imagePathList.get(i).getPath());///////

            mBuilder.addFormDataPart("files", file.getName(), RequestBody.create(type, file));
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(input);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("question", "blob", body);
        RequestBody requestBody = mBuilder.build();

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<SubmitQuestionnaire> call = null;
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
            call = apiService.submitAppointmentQuestionnaire(uid, requestBody, bookingModel.getAccountId());
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            call = apiService.submitWaitListQuestionnaire(uid, requestBody, bookingModel.getAccountId());
        }

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
                            getApptConfirmationDetails(bookingModel.getAccountId());
                        }

                    } else {
                        if (response.code() == 422) {
                            Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                        getApptConfirmationDetails(bookingModel.getAccountId());

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

    public void getQuestionnaireImages() {
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
                                                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                                                    ApiCheckStatus(activeAppointment.getUid(), bookingModel.getAccountId(), result);
                                                }
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

        for (int i = 0; i < result.getUrls().size(); i++) {

            JSONObject urlObj = new JSONObject();

            urlObj.put("uid", result.getUrls().get(i).getUid());
            urlObj.put("labelName", result.getUrls().get(i).getLabelName());
            urlObj.put("url", result.getUrls().get(i).getUrl());
            urlObj.put("document", result.getUrls().get(i).getDocument());
            if (result.getUrls().get(i).getColumnId() != null && !result.getUrls().get(i).getColumnId().trim().equalsIgnoreCase("")) {
                urlObj.put("columnId", result.getUrls().get(i).getColumnId());
                urlObj.put("gridOrder", 1);
            }

            uploadArray.put(urlObj);

        }


        uploadObj.putOpt("urls", uploadArray);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), uploadObj.toString());
        Call<ResponseBody> call = null;
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
            call = apiService.checkAppointmentUploadStatus(uid, accountId, body);
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            call = apiService.checkWaitlistUploadStatus(uid, accountId, body);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

                       /* Bundle b = new Bundle();
                        b.putStringArrayList("uids", (ArrayList<String>) uids);
                        b.putString("accountID", String.valueOf(bookingModel.getAccountId()));
                        Intent intent = new Intent(ReconfirmationMultipleApptActivity.this, MultipleAppointmentConfirmation.class);
                        intent.putExtras(b);
                        startActivity(intent);*/

                        String serviceDescription = "";
                        if (bookingModel.getServiceInfo().getDescription() != null && !bookingModel.getServiceInfo().getDescription().trim().isEmpty()) {
                            serviceDescription = bookingModel.getServiceInfo().getDescription();
                        }
                        //encId = activeAppointment.getAppointmentEncId();

                        Intent checkin = new Intent(ReconfirmationMultipleApptActivity.this, MultipleAppointmentConfirmation.class);
                        if (bookingModel.getEmailId() != null && !bookingModel.getEmailId().equalsIgnoreCase("")) {
                            checkin.putExtra("email", bookingModel.getEmailId());
                        }
                        checkin.putExtra("serviceDescription", serviceDescription);
                        //checkin.putExtra("terminology", mSearchTerminology.getProvider());
                        checkin.putExtra("from", "");
                        checkin.putExtra("typeOfService", Constants.APPOINTMENT);
                        checkin.putExtra("waitlistPhonenumberCountryCode", bookingModel.getCountryCode());
                        checkin.putExtra("waitlistPhonenumber", bookingModel.getPhoneNumber());
                        checkin.putExtra("accountID", String.valueOf(bookingModel.getAccountId()));
                        if (bookingModel.getServiceInfo().getLivetrack() != null && bookingModel.getServiceInfo().getLivetrack().equalsIgnoreCase("true")) {
                            checkin.putExtra("livetrack", true);
                        } else {
                            checkin.putExtra("livetrack", false);
                        }
                        checkin.putExtra("uids", (ArrayList<String>) uids);
                        checkin.putExtra("providerLogoUrl", providerLogoUrl);


                        startActivity(checkin);

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


    private void getApptConfirmationDetails(int userId) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(value, String.valueOf(userId));
                call.enqueue(new Callback<ActiveAppointment>() {
                    @Override
                    public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                        try {
                            if (mDialog.isShowing())
                                Config.closeDialog(ReconfirmationMultipleApptActivity.this, mDialog);

                            Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                            Config.logV("Response--code-------------------------" + response.code());
                            if (response.code() == 200) {
                                activeAppointment = response.body();
                                imagePathList.clear();
                                SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                                SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");

                                if (activeAppointment != null) {
                                    /*//encId = activeAppointment.getAppointmentEncId();
                                    Bundle b = new Bundle();
                                    b.putStringArrayList("uids", (ArrayList<String>) uids);
                                    b.putString("accountID", String.valueOf(bookingModel.getAccountId()));
                                    Intent intent = new Intent(ReconfirmationMultipleApptActivity.this, MultipleAppointmentConfirmation.class);
                                    intent.putExtras(b);
                                    startActivity(intent);*/

                                    String serviceDescription = "";
                                    if (bookingModel.getServiceInfo().getDescription() != null && !bookingModel.getServiceInfo().getDescription().trim().isEmpty()) {
                                        serviceDescription = bookingModel.getServiceInfo().getDescription();
                                    }
                                    //encId = activeAppointment.getAppointmentEncId();

                                    Intent checkin = new Intent(ReconfirmationMultipleApptActivity.this, MultipleAppointmentConfirmation.class);
                                    if (bookingModel.getEmailId() != null && !bookingModel.getEmailId().equalsIgnoreCase("")) {
                                        checkin.putExtra("email", bookingModel.getEmailId());
                                    }
                                    checkin.putExtra("serviceDescription", serviceDescription);
                                    //checkin.putExtra("terminology", mSearchTerminology.getProvider());
                                    checkin.putExtra("from", "");
                                    checkin.putExtra("typeOfService", Constants.APPOINTMENT);
                                    checkin.putExtra("waitlistPhonenumberCountryCode", bookingModel.getCountryCode());
                                    checkin.putExtra("waitlistPhonenumber", bookingModel.getPhoneNumber());
                                    checkin.putExtra("accountID", String.valueOf(bookingModel.getAccountId()));
                                    if (bookingModel.getServiceInfo().getLivetrack() != null && bookingModel.getServiceInfo().getLivetrack().equalsIgnoreCase("true")) {
                                        checkin.putExtra("livetrack", true);
                                    } else {
                                        checkin.putExtra("livetrack", false);
                                    }
                                    checkin.putExtra("uids", (ArrayList<String>) uids);
                                    checkin.putExtra("providerLogoUrl", providerLogoUrl);


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
                            Config.closeDialog(ReconfirmationMultipleApptActivity.this, mDialog);

                    }
                });
            }
        }, 1000);

    }

    private void getApptConfirmationId(int userId, String txt_addnote) {

        final ApiInterface apiService =
                ApiClient.getClient(ReconfirmationMultipleApptActivity.this).create(ApiInterface.class);
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
                            // encId = activeAppointment.getAppointmentEncId();

                            if (attachedImagePathList.size() > 0) {
                                ApiCommunicate(value, String.valueOf(bookingModel.getAccountId()), txt_addnote);
                            }
                            String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                            if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                QuestionnaireInput input = new QuestionnaireInput();
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                input = gson.fromJson(inputString, QuestionnaireInput.class);
                                ApiSubmitQuestionnnaire(input, activeAppointment.getUid());
                            } else {
                                getApptConfirmationDetails(bookingModel.getAccountId());

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
}
