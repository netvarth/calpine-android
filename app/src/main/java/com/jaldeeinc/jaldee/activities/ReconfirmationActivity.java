package com.jaldeeinc.jaldee.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jaldeeinc.jaldee.Interface.IPaymentGateway;
import com.jaldeeinc.jaldee.Interface.IPaymentResponse;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.PaymentModeAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewLight;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.BookingModel;
import com.jaldeeinc.jaldee.model.FamilyArrayModel;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnaireInput;
import com.jaldeeinc.jaldee.model.RazorpayModel;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.payment.PaymentGateway;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.ActiveDonation;
import com.jaldeeinc.jaldee.response.PayMode;
import com.jaldeeinc.jaldee.response.PaymentModel;
import com.jaldeeinc.jaldee.response.QuestionnaireUrls;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SubmitQuestionnaire;
import com.jaldeeinc.jaldee.response.WalletCheckSumModel;
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

public class ReconfirmationActivity extends AppCompatActivity implements PaymentResultWithDataListener, IPaymentResponse, IPaymentGateway {

    @BindView(R.id.ll_providerPhoneNumber)
    LinearLayout ll_providerPhoneNumber;
    @BindView(R.id.tv_providerPhoneNumber)
    CustomTextViewMedium tv_providerPhoneNumber;
    @BindView(R.id.ll_providerEmail)
    LinearLayout ll_providerEmail;
    @BindView(R.id.tv_providerEmail)
    CustomTextViewMedium tv_providerEmail;
    @BindView(R.id.tv_customerName)
    CustomTextViewBold tvCustomerName;
    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;
    @BindView(R.id.tv_providerName)
    CustomTextViewBold tvProviderName;
    @BindView(R.id.tv_phoneNumber)
    CustomTextViewMedium tvPhoneNumber;
    @BindView(R.id.tv_serviceName)
    CustomTextViewBold tvServiceName;
    @BindView(R.id.tv_vitual_service_number)
    CustomTextViewBold tv_vitual_service_number;
    @BindView(R.id.iv_serviceIcon)
    ImageView ivServiceIcon;
    @BindView(R.id.tv_waitingInLine)
    CustomTextViewLight tvWaitingInLine;
    @BindView(R.id.tv_bookingAt)
    CustomTextViewMedium tv_bookingAt;
    @BindView(R.id.tv_cnsmrDetails_Heading)
    CustomTextViewMedium tv_cnsmrDetails_Heading;
    @BindView(R.id.ll_donationAmount)
    LinearLayout ll_donationAmount;
    @BindView(R.id.tv_donationAmount)
    CustomTextViewBold tv_donationAmount;
    @BindView(R.id.ll_bookingAt)
    LinearLayout ll_bookingAt;
    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;
    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;
    @BindView(R.id.tv_email)
    CustomTextViewMedium tvEmail;
    @BindView(R.id.txtprepayamount)
    CustomTextViewBold txtprepayamount;
    @BindView(R.id.txtserviceamount)
    CustomTextViewBold txtserviceamount;
    @BindView(R.id.cv_servicePrepay)
    CardView cv_servicePrepay;
    @BindView(R.id.ll_cancellation_policy)
    LinearLayout ll_cancellation_policy;
    @BindView(R.id.cv_cancellation_policy)
    CardView cv_cancellation_policy;
    @BindView(R.id.tv_buttonName)
    CustomTextViewSemiBold tvButtonName;
    @BindView(R.id.tv_payment_link)
    CustomTextViewBold tv_payment_link;
    @BindView(R.id.cb_jCash)
    CheckBox cbJCash;
    @BindView(R.id.ll_jCash)
    LinearLayout llJCash;
    @BindView(R.id.tv_jCashHint)
    CustomTextViewMedium tvJCashHint;
    @BindView(R.id.cv_back)
    CardView cvBack;
    @BindView(R.id.cv_submit)
    CardView cvSubmit;
    @BindView(R.id.gv_payment_modes)
    GridView gv_payment_modes;
    @BindView(R.id.iv_image1)
    ImageView iv_image1;
    @BindView(R.id.iv_cnsmr_phone_icon)
    ImageView iv_cnsmr_phone_icon;
    @BindView(R.id.iv_cnsmr_email_icon)
    ImageView iv_cnsmr_email_icon;
    @BindView(R.id.iv_location_icon)
    ImageView iv_location_icon;
    @BindView(R.id.iv_prvdr_phone_icon)
    ImageView iv_prvdr_phone_icon;
    @BindView(R.id.iv_prvdr_email_icon)
    ImageView iv_prvdr_email_icon;
    @BindView(R.id.ll_fullfees_payd_advance)
    LinearLayout ll_fullfees_payd_advance;
    @BindView(R.id.ll_serviceamount)
    LinearLayout ll_serviceamount;
    @BindView(R.id.ll_prepay)
    LinearLayout ll_prepay;
    @BindView(R.id.ll_payment_mode)
    LinearLayout ll_payment_mode;


    BookingModel bookingModel = new BookingModel();
    public Context mContext;
    public JSONObject jsonObject;
    public String value = null;
    public String prepayAmount = "";
    public String prePayRemainingAmount = "";
    public String selectedpaymentMode;
    public SearchTerminology mSearchTerminology;
    public ActiveCheckIn activeCheckin = new ActiveCheckIn();
    public ActiveAppointment activeAppointment = new ActiveAppointment();
    public ArrayList<FamilyArrayModel> MultipleFamilyList = new ArrayList<>();
    public ArrayList<LabelPath> imagePathList = new ArrayList<>();
    public ArrayList<ShoppingListModel> attachedImagePathList = new ArrayList<>();
    public ArrayList<PaymentModel> mPaymentData = new ArrayList<>();
    private ArrayList<PayMode> payModes = new ArrayList<>();

    public int familyMEmID;
    public int serviceId = 0;
    public boolean indiaPay = false;
    public boolean internationalPay = false;
    public boolean isInternational = false;
    public ActiveDonation activeDonation;
    public String encId;
    public String PAYMENT_LINK_FLAG;
    public IPaymentResponse iPaymentResponse;
    public IPaymentGateway iPaymentGateway;
    public BottomSheetDialog dialog;
    public String path;
    public Bitmap bitmap;
    public File file;
    public File f;
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconfirmation);
        ButterKnife.bind(ReconfirmationActivity.this);
        iPaymentResponse = this;
        iPaymentGateway = this;
        mContext = ReconfirmationActivity.this;

        Intent intent = getIntent();
        bookingModel = (BookingModel) intent.getSerializableExtra("data");
        mSearchTerminology = bookingModel.getmSearchTerminology();
        getQuestionnaireImages();

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
                tvDate.setVisibility(View.VISIBLE);
                tvDate.setText(bookingModel.getDate());
            } else {
                tvDate.setVisibility(View.GONE);
            }
            if (bookingModel.getTime() != null) {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(bookingModel.getTime());
            } else {
                tvTime.setVisibility(View.GONE);
            }
            if (bookingModel.getFrom() != null) {
                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    Appointment();
                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                    Checkin();
                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
                    Donation();
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
        cbJCash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (cbJCash.isChecked()) {
                    if (bookingModel.getEligibleJcashAmt() >= bookingModel.getAmountRequiredNow()) {
                        tvButtonName.setText("Confirm ");
                        tv_payment_link.setVisibility(View.GONE);
                        gv_payment_modes.setVisibility(View.GONE);

                        txtprepayamount.setText("0");
                    } else {
                        tvButtonName.setText("Make Payment");
                        tvJCashHint.setVisibility(View.VISIBLE);
                        if (indiaPay || internationalPay) {
                            gv_payment_modes.setVisibility(View.VISIBLE);
                            if (internationalPay && indiaPay) {
                                if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                    tv_payment_link.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            gv_payment_modes.setVisibility(View.GONE);
                            tv_payment_link.setVisibility(View.GONE);

                        }
                        txtprepayamount.setText(Config.getAmountNoOrTwoDecimalPoints(bookingModel.getAmountRequiredNow() - bookingModel.getEligibleJcashAmt()));
                    }
                } else {
                    tvButtonName.setText("Make Payment");
                    if (indiaPay || internationalPay) {
                        gv_payment_modes.setVisibility(View.VISIBLE);
                        if (internationalPay && indiaPay) {
                            if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                tv_payment_link.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        gv_payment_modes.setVisibility(View.GONE);
                        tv_payment_link.setVisibility(View.GONE);

                    }
                    /*if (showForInternationalPayment) {
                        tv_international_payment_link.setVisibility(View.VISIBLE);
                    } else {
                        tv_international_payment_link.setVisibility(View.GONE);
                    }*/
                    tvJCashHint.setVisibility(View.GONE);
                    txtprepayamount.setText(Config.getAmountNoOrTwoDecimalPoints(bookingModel.getAmountRequiredNow()));
                }
            }
        });
        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        tv_payment_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PAYMENT_LINK_FLAG.isEmpty() && PAYMENT_LINK_FLAG != null) {
                    if (PAYMENT_LINK_FLAG.equalsIgnoreCase("INDIA")) {
                        tv_payment_link.setVisibility(View.VISIBLE);
                        tv_payment_link.setText("Indian Payment ? Click here");
                        payModes = mPaymentData.get(0).getInternationalBankInfo();
                        PAYMENT_LINK_FLAG = "INTERNATIONAL";
                        isInternational = true;
                    } else if (PAYMENT_LINK_FLAG.equalsIgnoreCase("INTERNATIONAL")) {
                        tv_payment_link.setVisibility(View.VISIBLE);
                        tv_payment_link.setText("Non Indian Payment ? Click here");
                        payModes = mPaymentData.get(0).getIndiaBankInfo();
                        PAYMENT_LINK_FLAG = "INDIA";
                        isInternational = false;
                    }
                    selectedpaymentMode = null;

                    gv_payment_modes.setVisibility(View.VISIBLE);
                    gv_payment_modes.setAdapter(new PaymentModeAdapter(mContext, payModes, iPaymentGateway));

                } else {
                    tv_payment_link.setVisibility(View.GONE);
                }
            }
        });
        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bookingModel != null) {
                    ApiSubmit(jsonObject, bookingModel.getAccountId());
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
        ll_bookingAt.setVisibility(View.VISIBLE);
        ll_donationAmount.setVisibility(View.GONE);
        tv_bookingAt.setText("APPOINTMENT AT : ");
        tv_cnsmrDetails_Heading.setText("CONSULTATION FOR :");
        if (bookingModel.getCustomerName() != null && !bookingModel.getCustomerName().trim().equalsIgnoreCase("")) {
            tvCustomerName.setText(bookingModel.getCustomerName());
        }
        if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
            APIPayment(bookingModel.getAccountId());
        } else {
            updateUI();
        }
        if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
            txtprepayamount.setText(Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getAmountRequiredNow())));
            txtserviceamount.setText(Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getNetTotal())));
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

    private void Checkin() {
        familyMEmID = bookingModel.getFamilyEMIID();
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

                        } else if (bookingModel.getCheckInInfo().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                            ivServiceIcon.setImageResource(R.drawable.phoneaudioicon);
                            tvPhoneNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phoneaudioicon_sized, 0, 0, 0);
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
        getAttachedImages();
        serviceId = bookingModel.getCheckInInfo().getId();

        ll_bookingAt.setVisibility(View.VISIBLE);
        ll_donationAmount.setVisibility(View.GONE);
        if (bookingModel.isToken()) {
            tv_bookingAt.setText("TOKEN AT : ");
        } else {
            tv_bookingAt.setText("CHECKIN AT : ");
        }
        tv_cnsmrDetails_Heading.setText("CONSULTATION FOR :");
        if (bookingModel.getPeopleWaiting() != null) {
            tvWaitingInLine.setVisibility(View.VISIBLE);
            tvWaitingInLine.setText(bookingModel.getPeopleWaiting());
        }
        if (bookingModel.getMultipleFamilyMembers() != null && bookingModel.getMultipleFamilyMembers().size() > 0) {

            MultipleFamilyList = bookingModel.getMultipleFamilyMembers();
        } else {
            MultipleFamilyList = new ArrayList<>();
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
        if (bookingModel.getCheckInInfo().isPrePayment()) {
            APIPayment(bookingModel.getAccountId());
        } else {
            updateUI();
        }
        if (bookingModel.getCheckInInfo().isPrePayment()) {
            txtprepayamount.setText(Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getAmountRequiredNow())));
            txtserviceamount.setText(Config.getAmountNoOrTwoDecimalPoints(getFloatAsDouble(bookingModel.getNetTotal())));
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

    private void Donation() {
        familyMEmID = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        if (bookingModel.getDonationServiceInfo() != null) {
            if (bookingModel.getDonationServiceInfo().getName() != null) {
                tvServiceName.setText(bookingModel.getDonationServiceInfo().getName());
            }
        }
        serviceId = bookingModel.getDonationServiceInfo().getId();

        tvWaitingInLine.setVisibility(View.GONE);
        ll_bookingAt.setVisibility(View.GONE);
        ll_donationAmount.setVisibility(View.VISIBLE);
        tv_cnsmrDetails_Heading.setText("PAYMENT BY :");
        if (bookingModel.getCustomerName() != null && !bookingModel.getCustomerName().trim().equalsIgnoreCase("")) {
            tvCustomerName.setText(bookingModel.getCustomerName());
        }
        if (bookingModel.getDonationAmount() != null && !bookingModel.getDonationAmount().trim().equalsIgnoreCase("")) {
            tv_donationAmount.setText("₹ " + Config.getMoneyFormat(Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(bookingModel.getDonationAmount()))));
        }
        APIPayment(bookingModel.getAccountId());
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

    public void getAttachedImages() {
        if (bookingModel.getImagePathList() != null && bookingModel.getImagePathList().size() > 0) {
            attachedImagePathList = bookingModel.getImagePathList();
        } else {
            attachedImagePathList = new ArrayList<>();
        }
    }

    private void APIPayment(int accountID) {
        ApiInterface apiService =
                ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(ReconfirmationActivity.this, ReconfirmationActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ArrayList<PaymentModel>> call = apiService.getPaymentModes(String.valueOf(accountID), serviceId, Constants.PURPOSE_PREPAYMENT);
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
                        indiaPay = mPaymentData.get(0).isIndiaPay();
                        internationalPay = mPaymentData.get(0).isInternationalPay();
                        if (indiaPay && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                            selectedpaymentMode = null;
                            PAYMENT_LINK_FLAG = "INDIA";            /******** for set PAYMENT_LINK_FLAG in all cases(appt,checkin,donation) **/
                            tv_payment_link.setVisibility(View.VISIBLE);
                            if (mPaymentData != null && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                payModes = mPaymentData.get(0).getIndiaBankInfo();

                                gv_payment_modes.setVisibility(View.VISIBLE);
                                gv_payment_modes.setAdapter(new PaymentModeAdapter(mContext, payModes, iPaymentGateway));

                                if (internationalPay && mPaymentData.get(0).getInternationalBankInfo().size() > 0) {
                                    tv_payment_link.setText("Non Indian Payment ? Click here");
                                } else {
                                    tv_payment_link.setVisibility(View.GONE);
                                }
                                isInternational = false;
                            }
                        } else if (internationalPay && mPaymentData.get(0).getInternationalBankInfo().size() > 0) {
                            selectedpaymentMode = null;
                            PAYMENT_LINK_FLAG = "INTERNATIONAL";     /******** for set PAYMENT_LINK_FLAG in all cases(appt,checkin,donation) **/
                            tv_payment_link.setVisibility(View.VISIBLE);
                            if (mPaymentData != null && mPaymentData.get(0).getInternationalBankInfo().size() > 0) {
                                payModes = mPaymentData.get(0).getInternationalBankInfo();

                                gv_payment_modes.setVisibility(View.VISIBLE);
                                gv_payment_modes.setAdapter(new PaymentModeAdapter(mContext, payModes, iPaymentGateway));

                                if (indiaPay && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                    tv_payment_link.setText("Indian Payment ? Click here");
                                } else {
                                    tv_payment_link.setVisibility(View.GONE);
                                }
                                isInternational = true;
                            }
                        } else {
                            tv_payment_link.setVisibility(View.GONE);
                        }
                        updateUI();

                    } else {
                        Toast.makeText(ReconfirmationActivity.this, response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PaymentModel>> call, Throwable t) {

            }
        });
    }

    public static Double getFloatAsDouble(Float fValue) {
        return Double.valueOf(fValue.toString());
    }

    public void updateUI() {
        boolean isPrePayment = false;
        Double eligibleJcashAmt = Double.valueOf(0);
        Glide.with(mContext).load(R.drawable.appoinment_icon_time).into(iv_image1);
        Glide.with(mContext).load(R.drawable.location_icon_1).into(iv_location_icon);
        Glide.with(mContext).load(R.drawable.phone_icon_1).into(iv_prvdr_phone_icon);
        Glide.with(mContext).load(R.drawable.email_icon_1).into(iv_prvdr_email_icon);
        if (bookingModel.getEmailId() != null && !bookingModel.getEmailId().equalsIgnoreCase("")) {
            Glide.with(mContext).load(R.drawable.email_icon_2).into(iv_cnsmr_email_icon);
            tvEmail.setText(bookingModel.getEmailId());
        }
        if (bookingModel.getPhoneNumber() != null && bookingModel.getCountryCode() != null) {
            Glide.with(mContext).load(R.drawable.phone_icon_2).into(iv_cnsmr_phone_icon);
            tvPhoneNumber.setText(bookingModel.getCountryCode() + " " + bookingModel.getPhoneNumber());
        }
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
                    isPrePayment = true;
                }
                eligibleJcashAmt = bookingModel.getEligibleJcashAmt();
            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                isPrePayment = bookingModel.getCheckInInfo().isPrePayment();
                eligibleJcashAmt = bookingModel.getEligibleJcashAmt();
            }
            //if (bookingModel.getNetTotal() != 0) {
            //cv_servicePrepay.setVisibility(View.VISIBLE);
            if (isPrePayment) {
                if (indiaPay || internationalPay) {
                    cv_servicePrepay.setVisibility(View.VISIBLE);
                    ll_cancellation_policy.setVisibility(View.VISIBLE);
                    tvButtonName.setText("Make Payment");
                    gv_payment_modes.setVisibility(View.VISIBLE);
                    if (internationalPay && indiaPay) {
                        if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                            tv_payment_link.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tv_payment_link.setVisibility(View.GONE);
                    }

                } else {
                    tv_payment_link.setVisibility(View.GONE);
                    //cv_servicePrepay.setVisibility(View.GONE);
                    ll_cancellation_policy.setVisibility(View.GONE);
                    tvButtonName.setText("Confirm");
                    gv_payment_modes.setVisibility(View.GONE);
                }
                if (eligibleJcashAmt > 0) {
                    cbJCash.setChecked(true);
                    llJCash.setVisibility(View.VISIBLE);
                    cbJCash.setText("Use Jaldee cash balance : Rs " + Config.getAmountNoOrTwoDecimalPoints(eligibleJcashAmt));
                    if (bookingModel.getEligibleJcashAmt() >= bookingModel.getAmountRequiredNow()) {
                        txtprepayamount.setText("0");
                    } else {
                        txtprepayamount.setText(Config.getAmountNoOrTwoDecimalPoints(bookingModel.getAmountRequiredNow() - bookingModel.getEligibleJcashAmt()));
                    }
                    if (eligibleJcashAmt >= bookingModel.getAmountRequiredNow()) {
                        tvJCashHint.setVisibility(View.GONE);
                        tvButtonName.setText("Confirm");
                        tv_payment_link.setVisibility(View.GONE);
                        gv_payment_modes.setVisibility(View.GONE);
                    } else {
                        tvJCashHint.setVisibility(View.VISIBLE);
                        tvButtonName.setText("Make Payment");
                        if (indiaPay || internationalPay) {
                            gv_payment_modes.setVisibility(View.VISIBLE);
                            if (internationalPay && indiaPay) {
                                if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                    tv_payment_link.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            gv_payment_modes.setVisibility(View.GONE);
                            tv_payment_link.setVisibility(View.GONE);

                        }
                    }
                } else if (eligibleJcashAmt == 0) {
                    cbJCash.setChecked(false);
                    llJCash.setVisibility(View.GONE);
                    tvButtonName.setText("Make Payment");
                    if (indiaPay || internationalPay) {
                        gv_payment_modes.setVisibility(View.VISIBLE);
                        if (internationalPay && indiaPay) {
                            if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                tv_payment_link.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        gv_payment_modes.setVisibility(View.GONE);
                        tv_payment_link.setVisibility(View.GONE);

                    }
                } else {
                    cbJCash.setChecked(false);
                    llJCash.setVisibility(View.GONE);
                    if (indiaPay || internationalPay) {
                        gv_payment_modes.setVisibility(View.VISIBLE);
                        if (internationalPay && indiaPay) {
                            if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                                tv_payment_link.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        gv_payment_modes.setVisibility(View.GONE);
                        tv_payment_link.setVisibility(View.GONE);
                        gv_payment_modes.setVisibility(View.GONE);
                    }
                }
            } else {
                ll_cancellation_policy.setVisibility(View.GONE);
                llJCash.setVisibility(View.GONE);
                tv_payment_link.setVisibility(View.GONE);
                gv_payment_modes.setVisibility(View.GONE);
                ll_payment_mode.setVisibility(View.GONE);
                cv_servicePrepay.setVisibility(View.GONE);
                txtprepayamount.setText("0");
                tvButtonName.setText("Confirm");

                cbJCash.setChecked(false);

            }
          /*  } else {
                llJCash.setVisibility(View.GONE);
                tv_payment_link.setVisibility(View.GONE);
                rv_payment_modes.setVisibility(View.GONE);
                ll_payment_mode.setVisibility(View.GONE);

                txtserviceamount.setText("0");
                txtprepayamount.setText("0");
                tvButtonName.setText("Confirm");

                cbJCash.setChecked(false);

            }*/
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {

            if (bookingModel.getDonationAmount() != null && !bookingModel.getDonationAmount().trim().equalsIgnoreCase("")) {
                ll_cancellation_policy.setVisibility(View.GONE);
                llJCash.setVisibility(View.GONE);
                ll_serviceamount.setVisibility(View.GONE);
                ll_prepay.setVisibility(View.GONE);
                tvButtonName.setText("Make Payment");
                cbJCash.setChecked(false);
                if (indiaPay || internationalPay) {
                    gv_payment_modes.setVisibility(View.VISIBLE);
                    if (internationalPay && indiaPay) {
                        if (mPaymentData.get(0).getInternationalBankInfo().size() > 0 && mPaymentData.get(0).getIndiaBankInfo().size() > 0) {
                            tv_payment_link.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    gv_payment_modes.setVisibility(View.GONE);
                    tv_payment_link.setVisibility(View.GONE);

                }
            }
        }
    }

    public void ApiSubmit(JSONObject jsonObject, int accountId) {
        final Dialog mDialog = Config.getProgressDialog(ReconfirmationActivity.this, ReconfirmationActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        ApiInterface apiService = ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
        Call<ResponseBody> call = null;
        if (bookingModel.getFrom() != null) {
            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                call = apiService.Appointment(String.valueOf(accountId), body);
            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                call = apiService.Checkin(String.valueOf(accountId), body);
            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
                call = apiService.Donation(String.valueOf(accountId), body);
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (mDialog.isShowing())
                            Config.closeDialog(getParent(), mDialog);

                        if (response.code() == 200) {
                            SharedPreference.getInstance(mContext).setValue("refreshcheckin", "true");
                            JSONObject reader = new JSONObject(response.body().string());
                            Iterator iteratorObj = reader.keys();
                            if (reader.has("parent_uuid")) {
                                value = reader.getString("parent_uuid");
                            }
                            if (reader.has("uid")) {
                                value = reader.getString("uid");
                            }
                            if (reader.has("_prepaymentAmount")) {
                                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                    if ((bookingModel.getServiceInfo() != null && bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) || bookingModel.getCheckInInfo().isPrePayment()) {
                                        prepayAmount = reader.getString("_prepaymentAmount");
                                    }
                                }
                            }
                            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                if (cbJCash.isChecked()) {
                                    getPrePayRemainingAmntNeeded(prepayAmount, cbJCash.isChecked(), bookingModel.getAccountId());
                                } else {
                                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                                        getApptConfirmationId(accountId, bookingModel.getMessage());
                                    } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                        getCheckinConfirmationId(accountId, bookingModel.getMessage());
                                    }
                                }
                            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
                                getDonationConfirmationId(accountId);
                            }
                        } else if (response.code() == 422) {

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

    private void getDonationConfirmationId(int userId) {

        final ApiInterface apiService =
                ApiClient.getClient(ReconfirmationActivity.this).create(ApiInterface.class);
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
                            encId = activeDonation.getDonationEncId();
                            String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                            if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                QuestionnaireInput input = new QuestionnaireInput();
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                input = gson.fromJson(inputString, QuestionnaireInput.class);
                                ApiDontionSubmitQuestionnnaire(input, activeDonation.getUid());
                            }
                            if (selectedpaymentMode != null) {
                                try {
                                    final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
                                    mDialog.show();
                                    new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHash(bookingModel.getDonationAmount(), selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_DONATIONPAYMENT, serviceId, isInternational, encId, familyMEmID);
                                    mDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(mContext, "Please select mode of payment", Toast.LENGTH_LONG).show();
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
                            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                                getApptConfirmationDetails(bookingModel.getAccountId());
                            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                getCheckinConfirmationDetails(bookingModel.getAccountId());
                            }
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

    private void ApiDontionSubmitQuestionnnaire(QuestionnaireInput input, String uid) {

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

    private void getApptConfirmationId(int userId, String txt_addnote) {

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
                            encId = activeAppointment.getAppointmentEncId();

                            if (bookingModel.getServiceInfo().getIsPrePayment().equalsIgnoreCase("true")) {
                                if (cbJCash.isChecked() && Double.parseDouble(prePayRemainingAmount) <= 0) {
                                    isGateWayPaymentNeeded(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, true, false, false, false, "JCASH", txt_addnote);
                                    //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                                } else if (prepayAmount != null && Float.parseFloat(prepayAmount) > 0) {
                                    if (selectedpaymentMode != null) {
                                        if (cbJCash.isChecked()) {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHashWallet(prepayAmount, selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, serviceId, isInternational, encId, true);
                                        } else {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHash(prepayAmount, selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, serviceId, isInternational, encId, familyMEmID);
                                        }
                                    } else {
                                        Toast.makeText(mContext, "Please select mode of payment", Toast.LENGTH_LONG).show();
                                    }

                                    /*if (true) {
                                        // if (isPaytm) {
                                        PaytmPayment payment = new PaytmPayment(ReconfirmationActivity.this, iPaymentResponse);
                                        if (cbJCash.isChecked()) {
                                            payment.ApiGenerateHashPaytm2(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", true, false, false, true, encId, mContext, ReconfirmationActivity.this);
                                        } else {
                                            payment.ApiGenerateHashPaytm(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, ReconfirmationActivity.this, ReconfirmationActivity.this, "", familyMEmID, encId);
                                        }
                                    } else {
                                        if (cbJCash.isChecked()) {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this).ApiGenerateHash2(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", true, false, true, false);
                                        } else {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this).ApiGenerateHash1(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, "checkin", familyMEmID, Constants.SOURCE_PAYMENT);
                                        }
                                    }*/
                                }
                            } else {
                                if (attachedImagePathList.size() > 0) {
                                    ApiCommunicate(value, String.valueOf(bookingModel.getAccountId()), txt_addnote, dialog);
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

    private void getCheckinConfirmationId(int id, String txt_addnote) {

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
                        activeCheckin = response.body();

                        if (activeCheckin != null) {
                            encId = activeCheckin.getCheckinEncId();

                            if (bookingModel.getCheckInInfo().isPrePayment()) {
                                if (cbJCash.isChecked() && Double.parseDouble(prePayRemainingAmount) <= 0) {
                                    isGateWayPaymentNeeded(value, prepayAmount, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, true, false, false, false, "JCASH", txt_addnote);
                                    //Toast.makeText(mContext,"Pay amount by Cash",Toast.LENGTH_LONG).show();
                                } else if (prepayAmount != null && Float.parseFloat(prepayAmount) > 0) {
                                    if (selectedpaymentMode != null) {
                                        if (cbJCash.isChecked()) {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHashWallet(prepayAmount, selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, serviceId, isInternational, encId, true);
                                        } else {
                                            new PaymentGateway(ReconfirmationActivity.this, ReconfirmationActivity.this, iPaymentResponse).ApiGenerateHash(prepayAmount, selectedpaymentMode, value, String.valueOf(bookingModel.getAccountId()), Constants.PURPOSE_PREPAYMENT, serviceId, isInternational, encId, familyMEmID);
                                        }
                                    } else {
                                        Toast.makeText(mContext, "Please select mode of payment", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {

                                if (attachedImagePathList.size() > 0) {
                                    ApiCommunicate(value, String.valueOf(bookingModel.getAccountId()), txt_addnote, dialog);
                                }

                                String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");

                                if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {

                                    QuestionnaireInput input = new QuestionnaireInput();
                                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                    input = gson.fromJson(inputString, QuestionnaireInput.class);
                                    ApiSubmitQuestionnnaire(input, activeCheckin.getYnwUuid());
                                } else {

                                    getCheckinConfirmationDetails(bookingModel.getAccountId());
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

    public void isGateWayPaymentNeeded(String ynwUUID, final String amount, String accountID, String purpose, boolean isJcashUsed, boolean isreditUsed, boolean isRazorPayPayment, boolean isPayTmPayment, String paymentMode, String txt_addnote) {

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

                            if (attachedImagePathList.size() > 0) {
                                ApiCommunicate(value, accountID, txt_addnote, dialog);
                            }
                            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                                getApptConfirmationDetails(Integer.parseInt(accountID));
                            } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                getCheckinConfirmationDetails(Integer.parseInt(accountID));
                            }
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
                    if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                        getApptConfirmationId(bookingModel.getAccountId(), bookingModel.getMessage());
                    } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                        getCheckinConfirmationId(bookingModel.getAccountId(), bookingModel.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i("PrePayRemainingAmntNeed", t.toString());
                t.printStackTrace();
            }
        });
    }


    private void paymentFinished() {
        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {

            if (attachedImagePathList != null && attachedImagePathList.size() > 0) {
                ApiCommunicate(value, String.valueOf(bookingModel.getAccountId()), bookingModel.getMessage(), dialog);
            }
            String inputString = SharedPreference.getInstance(mContext).getStringValue(Constants.QUESTIONNAIRE, "");
            if (inputString != null && !inputString.trim().equalsIgnoreCase("")) {
                QuestionnaireInput input = new QuestionnaireInput();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                input = gson.fromJson(inputString, QuestionnaireInput.class);
                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    ApiSubmitQuestionnnaire(input, activeAppointment.getUid());
                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                    ApiSubmitQuestionnnaire(input, activeCheckin.getYnwUuid());
                }
            } else {
                if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                    getApptConfirmationDetails(bookingModel.getAccountId());
                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                    getCheckinConfirmationDetails(bookingModel.getAccountId());
                }
            }
        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.DONATION)) {
            paymentFinishedDonationDialog();
        }
    }

    private void paymentError() {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(ReconfirmationActivity.this).create();
            alertDialog.setTitle("Payment Failed");
            alertDialog.setMessage("Unable to process your request.Please try again after some time");
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT) || bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN) ||  bookingModel.getFrom().equalsIgnoreCase(Constants.TOKEN)) {
                                Intent homeIntent = new Intent(ReconfirmationActivity.this, Home.class);
                                startActivity(homeIntent);
                            }
                            finish();

                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError..", e);
        }
    }

    private void paymentFinishedDonationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(R.layout.successful_donation)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        finish();
                        Intent homeIntent = new Intent(ReconfirmationActivity.this, Home.class);
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

    private void ApiCommunicate(String id, String accountID, String message, final BottomSheetDialog dialog) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type;
        JSONObject captions = new JSONObject();
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < attachedImagePathList.size(); i++) {

            String extension = "";

            if (attachedImagePathList.get(i).getImagePath().contains(".")) {
                extension = attachedImagePathList.get(i).getImagePath().substring(attachedImagePathList.get(i).getImagePath().lastIndexOf(".") + 1);
            }
            if (extension.equalsIgnoreCase("pdf")) {
                type = MediaType.parse("application/pdf");
            } else if (extension.equalsIgnoreCase("png")) {
                type = MediaType.parse("image/png");
            } else if (extension.equalsIgnoreCase("jpeg")) {
                type = MediaType.parse("image/jpeg");
            } else {
                type = MediaType.parse("image/*");
            }

            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(attachedImagePathList.get(i).getImagePath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            } else {
                file = new File(attachedImagePathList.get(i).getImagePath());
            }
            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
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
                                                } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                                                    ApiCheckStatus(activeCheckin.getYnwUuid(), bookingModel.getAccountId(), result);
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
                        if (bookingModel.getFrom().equalsIgnoreCase(Constants.APPOINTMENT)) {
                            getApptConfirmationDetails(accountId);
                        } else if (bookingModel.getFrom().equalsIgnoreCase(Constants.CHECKIN)) {
                            getCheckinConfirmationDetails(accountId);
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
                                Config.closeDialog(ReconfirmationActivity.this, mDialog);

                            Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                            Config.logV("Response--code-------------------------" + response.code());
                            if (response.code() == 200) {
                                activeAppointment = response.body();
                                imagePathList.clear();
                                SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                                SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");

                                if (activeAppointment != null) {
                                    encId = activeAppointment.getAppointmentEncId();
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
        }, 1000);


    }

    private void getCheckinConfirmationDetails(int id) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(value, String.valueOf(id));
                call.enqueue(new Callback<ActiveCheckIn>() {
                    @Override
                    public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {

                        if (mDialog.isShowing())
                            Config.closeDialog(getParent(), mDialog);
                        try {

                            if (response.code() == 200) {
                                ActiveCheckIn activeAppointment = response.body();
                                SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, "");
                                SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, "");
                                imagePathList.clear();
                                if (activeAppointment != null) {
                                    encId = activeAppointment.getCheckinEncId();
                                    Bundle b = new Bundle();

                                    Intent checkin = new Intent(ReconfirmationActivity.this, CheckInConfirmation.class);
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

                        if (mDialog.isShowing())
                            Config.closeDialog(getParent(), mDialog);
                    }
                });

            }
        }, 1000);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            RazorpayModel razorpayModel = new RazorpayModel(paymentData);
            // new PaymentGateway(mContext, ReconfirmationActivity.this).sendPaymentStatus(razorpayModel, "SUCCESS");
            ApiCheckRazorpayPaymentStatus(razorpayModel);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        paymentError();
    }

    private void ApiCheckRazorpayPaymentStatus(RazorpayModel razorpayModel) {

        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {

            // jsonObj.put("paymentId", razorpayModel.getRazorpay_payment_id());
            jsonObj.put("paymentId", razorpayModel.getRazorpay_payment_id());
            jsonObj.put("orderId", razorpayModel.getRazorpay_order_id());
            jsonObj.put("signature", razorpayModel.getRazorpay_signature());


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
                        Config.closeDialog(ReconfirmationActivity.this, mDialog);
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
                        Config.closeDialog(ReconfirmationActivity.this, mDialog);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String TAG = "PaytmPayment";
        Log.e(TAG, " result code " + resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        String bundle = data.getStringExtra("response");//.getString("ORDERID");
        String orderId = "";
        try {
            JSONObject jsonObject = new JSONObject(bundle);
            orderId = jsonObject.getString("ORDERID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (requestCode == 01 && data != null) {
            if (data.getStringExtra("response").contains("TXN_SUCCESS")) {
                sendPaymentResponse("TXN_SUCCESS", orderId);
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
            } else {
                sendPaymentResponse("TXN_FAILED", orderId);
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

    @Override
    public void sendPaymentResponse(String paymentStatus, String orderid) {
        if (paymentStatus.equalsIgnoreCase("TXN_SUCCESS")) {
            ApiCheckPaytmPaymentStatus(orderid);
        } else {
            paymentError();
        }
    }

    @Override
    public void selectedPaymentMode(String mode) {
        selectedpaymentMode = mode;
    }
}