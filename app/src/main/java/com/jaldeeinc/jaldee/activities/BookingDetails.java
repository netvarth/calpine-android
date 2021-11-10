package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jaldeeinc.jaldee.Interface.IDeleteImagesInterface;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.ImagePreviewAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.ChatHistory;
import com.jaldeeinc.jaldee.custom.Contents;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewRegularItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.CustomerNotes;
import com.jaldeeinc.jaldee.custom.InstructionsDialog;
import com.jaldeeinc.jaldee.custom.MeetingDetailsWindow;
import com.jaldeeinc.jaldee.custom.MeetingInfo;
import com.jaldeeinc.jaldee.custom.PrescriptionDialog;
import com.jaldeeinc.jaldee.custom.QRCodeEncoder;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.QuestionnaireResponseInput;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.AnswerLineResponse;
import com.jaldeeinc.jaldee.response.GetQuestion;
import com.jaldeeinc.jaldee.response.InboxModel;
import com.jaldeeinc.jaldee.response.QuestionAnswers;
import com.jaldeeinc.jaldee.response.QuestionnaireResponse;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.jaldeeinc.jaldee.response.ShoppingList;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

public class BookingDetails extends AppCompatActivity implements IDeleteImagesInterface, ISaveNotes {

    @BindView(R.id.tv_providerName)
    CustomTextViewMedium tvProviderName;

    @BindView(R.id.tv_doctorName)
    CustomTextViewBold tvDoctorName;

    @BindView(R.id.tv_serviceName)
    CustomTextViewMedium tvServiceName;

    @BindView(R.id.iv_teleService)
    ImageView ivTeleService;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.tv_confirmationNumber)
    CustomTextViewBold tvConfirmationNumber;

    @BindView(R.id.tv_status)
    CustomTextViewBold tvStatus;

    @BindView(R.id.tv_amount)
    CustomTextViewBold tvAmount;

    @BindView(R.id.tv_consumerName)
    CustomTextViewBold tvConsumerName;

    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;

    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;

    @BindView(R.id.tv_batchNo)
    CustomTextViewBold tvBatchNo;

    @BindView(R.id.tv_viewMore)
    CustomTextViewSemiBold tvViewMore;

    @BindView(R.id.tv_billText)
    CustomTextViewSemiBold tvBillText;

    @BindView(R.id.tv_bill_receiptText)
    CustomTextViewSemiBold tvBillReceiptText;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_share)
    CardView cvShare;

    @BindView(R.id.cv_bill)
    CardView cvBill;

    @BindView(R.id.cv_enquiry)
    CardView cvEnquiry;

    @BindView(R.id.ll_payment)
    LinearLayout llPayment;

    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;

    @BindView(R.id.ll_moreDetails)
    NeomorphFrameLayout llMoreDetails;

    @BindView(R.id.ll_message)
    LinearLayout llMessage;

    @BindView(R.id.ll_reschedule)
    LinearLayout llReschedule;

    @BindView(R.id.ll_batch)
    LinearLayout llBatch;

    @BindView(R.id.ll_location)
    LinearLayout llLocation;

    @BindView(R.id.ll_rating)
    LinearLayout llRating;

    @BindView(R.id.ll_customerNotes)
    LinearLayout llCustomerNotes;

    @BindView(R.id.tv_customerNotes)
    CustomTextViewMedium tvCustomerNotes;

    @BindView(R.id.ll_instructions)
    LinearLayout llInstructions;

    @BindView(R.id.iv_ltIcon)
    ImageView ivLtIcon;

    @BindView(R.id.tv_trackingText)
    CustomTextViewMedium tvTrackingText;

    @BindView(R.id.tv_amountToPay)
    CustomTextViewRegularItalic tvAmountToPay;

    @BindView(R.id.cv_meetingDetails)
    NeomorphFrameLayout cvMeetingDetails;

    @BindView(R.id.iv_meetingIcon)
    ImageView ivMeetingIcon;

    @BindView(R.id.iv_Qr)
    ImageView ivQR;

    @BindView(R.id.ll_prescription)
    LinearLayout llPrescription;

    @BindView(R.id.tv_phoneNumber)
    CustomTextViewBold tvPhoneNumber;

    @BindView(R.id.ll_phoneNumber)
    LinearLayout llPhoneNumber;

    @BindView(R.id.ll_sendAttachments)
    LinearLayout llSendAttachments;

    @BindView(R.id.ll_viewAttachments)
    LinearLayout llViewAttachments;

    @BindView(R.id.ll_questionnaire)
    LinearLayout llQuestionnaire;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    boolean firstTimeRating = false;
    boolean isTvViewMore = false;

    TabLayout tabLayout;
    ViewPager viewPager;
    private Context mContext;
    private Bookings bookingInfo = new Bookings();
    private boolean isActive = true;
    private ActiveAppointment apptInfo = new ActiveAppointment();
    private InstructionsDialog instructionsDialog;
    private CustomerNotes customerNotes;
    private ChatHistory chatHistory;
    ArrayList<InboxModel> mInboxList = new ArrayList<>();
    private TeleServiceCheckIn meetingDetails;
    private MeetingDetailsWindow meetingDetailsWindow;
    private MeetingInfo meetingInfo;
    private String uuid;
    private PrescriptionDialog prescriptionDialog;
    String ynwUUid, accountId, countryCode, click_action;
    private boolean fromPushNotification = false;
    String uid;
    int id;

    // files related
    Bitmap bitmap;
    File f, file;
    String path, from, from1 = "";
    private LinearLayout llNoHistory;
    private ImageView iv_attach;
    TextView tv_attach, tv_camera;
    private BottomSheetDialog bDialog;
    CustomTextViewSemiBold tvErrorMessage;
    RecyclerView recycle_image_attachment;
    private int GALLERY = 1, CAMERA = 2;
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    private Uri mImageUri;
    ImagePreviewAdapter imagePreviewAdapter;
    private IDeleteImagesInterface iDeleteImagesInterface;
    ArrayList<ShoppingListModel> imagePathList = new ArrayList<>();
    private CustomNotes customNotes;
    private ISaveNotes iSaveNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        ButterKnife.bind(BookingDetails.this);
        mContext = BookingDetails.this;
        iDeleteImagesInterface = (IDeleteImagesInterface) this;
        iSaveNotes = (ISaveNotes) this;

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        Intent i = getIntent();
        if (i != null) {
            bookingInfo = (Bookings) i.getSerializableExtra("bookingInfo");
            uid = i.getStringExtra("uid");
            id = i.getIntExtra("accountId", 0);
            isActive = i.getBooleanExtra("isActive", true);
            ynwUUid = i.getStringExtra("uuid");
            accountId = i.getStringExtra("account");
            fromPushNotification = i.getBooleanExtra(Constants.PUSH_NOTIFICATION, false);
            click_action = i.getStringExtra("click_action");
        }

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        llReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BookingDetails.this, RescheduleActivity.class);
                intent.putExtra("providerId", apptInfo.getProviderAccount().getId());
                intent.putExtra("ynwuuid", apptInfo.getUid());
                intent.putExtra("uniqueId", apptInfo.getProviderAccount().getUniqueId());
                startActivity(intent);
            }
        });

        llInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (apptInfo != null && apptInfo.getService() != null) {
                        instructionsDialog = new InstructionsDialog(mContext, apptInfo.getService().getPostInfoText(), apptInfo.getService().getPostInfoTitle());
                        instructionsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                        instructionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        instructionsDialog.show();
                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        instructionsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        llMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(BookingDetails.this, ChatActivity.class);
                    if (apptInfo.getUid().contains("h_")) {
                        uuid = apptInfo.getUid().replace("h_", "");
                        intent.putExtra("uuid", uuid);
                    } else {
                        intent.putExtra("uuid", apptInfo.getUid());
                    }
                    intent.putExtra("accountId", apptInfo.getProviderAccount().getId());
                    intent.putExtra("name", apptInfo.getProviderAccount().getBusinessName());
                    intent.putExtra("from", Constants.APPOINTMENT);
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                dialog.setContentView(R.layout.cancelcheckin);
                dialog.show();
                Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
                Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
                txtsendmsg.setText("Do you want to cancel this Appointment?");
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApiDeleteAppointment(apptInfo.getUid(), String.valueOf(apptInfo.getProviderAccount().getId()), dialog);
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

        tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewMoreActions();
            }
        });

        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(mContext, CheckinShareLocationAppointment.class);
                    intent.putExtra("waitlistPhonenumber", apptInfo.getConsumer().getUserProfile().getPrimaryMobileNo());
                    intent.putExtra("uuid", apptInfo.getUid());
                    intent.putExtra("accountID", String.valueOf(apptInfo.getProviderAccount().getId()));
                    intent.putExtra("title", apptInfo.getProviderAccount().getBusinessName());
                    intent.putExtra("terminology", "Check-in");
                    intent.putExtra("calcMode", "Check-in");
                    intent.putExtra("queueStartTime", apptInfo.getSchedule().getApptSchedule().getTimeSlots().get(0).getsTime());
                    intent.putExtra("queueEndTime", apptInfo.getSchedule().getApptSchedule().getTimeSlots().get(0).geteTime());
                    if (apptInfo.getJaldeeApptDistanceTime() != null && apptInfo.getJaldeeApptDistanceTime().getJaldeeDistanceTime() != null) {
                        intent.putExtra("jaldeeDistance", apptInfo.getJaldeeApptDistanceTime().getJaldeeDistanceTime().getJaldeeDistance().getDistance());
                    }
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        llRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApiRating(String.valueOf(apptInfo.getProviderAccount().getId()), apptInfo.getUid());

            }
        });

        llCustomerNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (apptInfo != null && apptInfo.getService() != null) {
                        customerNotes = new CustomerNotes(mContext, apptInfo.getService().getConsumerNoteTitle(), apptInfo.getConsumerNote());
                        customerNotes.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                        customerNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        customerNotes.show();
                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        customerNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (apptInfo != null && apptInfo.getAppointmentEncId() != null) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/html");
                    String statusUrl = Constants.URL + "status/" + apptInfo.getAppointmentEncId();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share your Appointment status link");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, statusUrl);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }

            }
        });


        llSendAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bDialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
                bDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                bDialog.setContentView(R.layout.files_layout);
                bDialog.show();

                final Button btn_send = bDialog.findViewById(R.id.btn_send);
                Button btn_cancel = bDialog.findViewById(R.id.btn_cancel);
                btn_send.setText("Send");
                Typeface font_style = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
                btn_cancel.setTypeface(font_style);
                btn_send.setTypeface(font_style);
                tvErrorMessage = bDialog.findViewById(R.id.tv_errorMessage);
                tv_attach = bDialog.findViewById(R.id.btn);
                tv_camera = bDialog.findViewById(R.id.camera);
                recycle_image_attachment = bDialog.findViewById(R.id.recycler_view_image);

                imagePathList.clear();

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (imagePathList != null && imagePathList.size() > 0) {

                            if (apptInfo != null) {
                                sendAppointmentAttachments(apptInfo.getProviderAccount().getId(), apptInfo.getUid());
                            }
                            tvErrorMessage.setVisibility(View.GONE);
                            bDialog.dismiss();
                        } else {
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imagePathList != null && imagePathList != null) {
                            imagePathList.clear();
                        }
                        bDialog.dismiss();
                    }
                });


                requestMultiplePermissions();
                tv_attach.setVisibility(View.VISIBLE);
                tv_camera.setVisibility(View.VISIBLE);


                tv_attach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{
                                            Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY);

                                    return;
                                } else {
                                    Intent intent = new Intent();
                                    intent.setType("*/*");
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                                }
                            } else {

                                Intent intent = new Intent();
                                intent.setType("*/*");
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                });


                tv_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                    requestPermissions(new String[]{
                                            Manifest.permission.CAMERA}, CAMERA);

                                    return;
                                } else {
                                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    Intent cameraIntent = new Intent();
                                    cameraIntent.setType("image/*");
                                    cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, CAMERA);
                                }
                            } else {

                                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                Intent cameraIntent = new Intent();
                                cameraIntent.setType("image/*");
                                cameraIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, CAMERA);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                });

            }
        });

        llViewAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (apptInfo != null) {

                    getAppointmentImages(apptInfo.getUid(), apptInfo.getProviderAccount().getId());

                }

            }
        });

        llQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (apptInfo != null) {

                    QuestionnaireResponseInput input = buildQuestionnaireInput(apptInfo.getQuestionnaire());
                    ArrayList<LabelPath> labelPaths = buildQuestionnaireLabelPaths(apptInfo.getQuestionnaire());

                    SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
                    SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

                    Intent intent = new Intent(mContext, UpdateQuestionnaire.class);
                    intent.putExtra("serviceId", apptInfo.getService().getId());
                    intent.putExtra("accountId", apptInfo.getProviderAccount().getId());
                    intent.putExtra("uid", apptInfo.getUid());
                    intent.putExtra("isEdit", true);
                    intent.putExtra("from", Constants.BOOKING_APPOINTMENT);
                    if (apptInfo != null && apptInfo.getApptStatus() != null) {
                        intent.putExtra("status", apptInfo.getApptStatus());
                    }
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {

        // Api call
        if (uid != null) {
            getAppointmentDetails(uid, id);
        } else {

            // this gets called when activity is launched from push notification
            if (ynwUUid != null) {
                if (click_action != null && click_action != "" && click_action.equalsIgnoreCase("CONSUMER_SHARE_PRESCRIPTION")) {
                    ViewMoreActions();
                    scrollView.scrollTo(0, scrollView.getBottom());
                }
                getAppointmentDetails(ynwUUid, Integer.parseInt(accountId));
            }
        }

        updateImages();

        super.onResume();
    }


    public void getAppointmentDetails(String uid, int id) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(BookingDetails.this, BookingDetails.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(uid, String.valueOf(id));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        apptInfo = response.body();

                        if (apptInfo != null) {

                            if (!apptInfo.getApptStatus().equalsIgnoreCase("Cancelled") && (!apptInfo.getApptStatus().equalsIgnoreCase("done") || !apptInfo.getApptStatus().equalsIgnoreCase("Completed"))) {

                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date strDate = sdf.parse(apptInfo.getAppmtDate());
                                    Date date = null;
                                    date = getCurrentDate();
                                    if (date.compareTo(strDate) == 0) {
                                        isActive = true;
                                    } else if (date.compareTo(strDate) == 1) {
                                        isActive = false;
                                    } else if (date.compareTo(strDate) == -1) {
                                        isActive = true;
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                isActive = false;
                            }
                            updateUI(apptInfo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ActiveAppointment> call, Throwable t) {
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);
            }
        });
    }


    private void updateUI(ActiveAppointment appointmentInfo) {

        try {

            if (appointmentInfo != null) {
                if (appointmentInfo.getProvider() != null) {

                    if (appointmentInfo.getProvider().getBusinessName() != null && !appointmentInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                        tvDoctorName.setText(appointmentInfo.getProvider().getBusinessName());
                    } else {
                        String name = appointmentInfo.getProvider().getFirstName() + " " + appointmentInfo.getProvider().getLastName();
                        tvDoctorName.setText(name);
                    }
                    tvProviderName.setVisibility(View.VISIBLE);
                    tvProviderName.setText(appointmentInfo.getProviderAccount().getBusinessName());
                    tvProviderName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Intent intent = new Intent(BookingDetails.this, ProviderDetailActivity.class);
                                intent.putExtra("uniqueID", appointmentInfo.getProviderAccount().getUniqueId());
                                intent.putExtra("locationId", appointmentInfo.getLocation().getId());
                                startActivity(intent);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    tvProviderName.setVisibility(View.INVISIBLE);
                    tvDoctorName.setText(appointmentInfo.getProviderAccount().getBusinessName());

                    tvDoctorName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Intent intent = new Intent(BookingDetails.this, ProviderDetailActivity.class);
                                intent.putExtra("uniqueID", appointmentInfo.getProviderAccount().getUniqueId());
                                intent.putExtra("locationId", appointmentInfo.getLocation().getId());
                                startActivity(intent);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

                if (appointmentInfo.getAppointmentEncId() != null) {
                    //Encode with a QR Code image
                    String statusUrl = Constants.URL + "status/" + appointmentInfo.getAppointmentEncId();
                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(statusUrl,
                            null,
                            Contents.Type.TEXT,
                            BarcodeFormat.QR_CODE.toString(), 175);
                    try {
                        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                        ivQR.setImageBitmap(bitmap);

                        ivQR.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Dialog settingsDialog = new Dialog(BookingDetails.this);
                                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                                settingsDialog.getWindow().getAttributes().windowAnimations = R.style.zoomInAndOut;
                                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout
                                        , null));
                                ImageView imageView = settingsDialog.findViewById(R.id.iv_close);
                                ImageView ivQR = settingsDialog.findViewById(R.id.iv_Qr);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        settingsDialog.dismiss();
                                    }
                                });

                                ivQR.setImageBitmap(bitmap);
                                settingsDialog.show();
                            }
                        });


                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }

                tvViewMore.setVisibility(View.VISIBLE);
                if (isTvViewMore) {
                    llMoreDetails.setVisibility(View.VISIBLE);
                } else {
                    llMoreDetails.setVisibility(View.GONE);
                }
                if (appointmentInfo.getService() != null) {

                    if (appointmentInfo.getService().getDeptName() != null) {

                        tvServiceName.setText(appointmentInfo.getService().getName() + " (" + appointmentInfo.getService().getDeptName() + ")");
                    } else {
                        tvServiceName.setText(appointmentInfo.getService().getName());
                    }

                    if (appointmentInfo.getService().getServiceType() != null && appointmentInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {

                        if (isActive) {
                            if (appointmentInfo.getApptStatus() != null && (appointmentInfo.getApptStatus().equalsIgnoreCase("done") || (appointmentInfo.getApptStatus().equalsIgnoreCase("Completed")))) {
                                cvMeetingDetails.setVisibility(View.GONE);
                            } else {
                                cvMeetingDetails.setVisibility(View.VISIBLE);
                            }
                        } else {
                            cvMeetingDetails.setVisibility(View.GONE);
                        }
                        if (appointmentInfo.getService().getVirtualCallingModes() != null) {
                            ivTeleService.setVisibility(View.VISIBLE);
                            if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivTeleService.setImageResource(R.drawable.zoom_meet);
                                ivMeetingIcon.setImageResource(R.drawable.zoom_meet);
                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivTeleService.setImageResource(R.drawable.google_meet);
                                ivMeetingIcon.setImageResource(R.drawable.google_meet);
                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (appointmentInfo.getService().getVirtualServiceType() != null && appointmentInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_videoicon);
                                    ivMeetingIcon.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_icon);
                                    ivMeetingIcon.setImageResource(R.drawable.whatsapp_icon);
                                }
                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivTeleService.setImageResource(R.drawable.phoneicon_sized);
                                ivMeetingIcon.setImageResource(R.drawable.phoneicon_sized);
                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                                ivTeleService.setImageResource(R.drawable.ic_jaldeevideo);
                                ivMeetingIcon.setImageResource(R.drawable.ic_jaldeevideo);
                            }
                        } else {
                            ivTeleService.setVisibility(View.GONE);
                        }
                    } else {

                        cvMeetingDetails.setVisibility(View.GONE);
                    }

                }

                // to set confirmation number
                if (appointmentInfo.getAppointmentEncId() != null) {
                    tvConfirmationNumber.setText(appointmentInfo.getAppointmentEncId());
                }
                // to set Phone number
                if (appointmentInfo.getPhoneNumber() != null && !appointmentInfo.getPhoneNumber().isEmpty()) {
                    llPhoneNumber.setVisibility(View.VISIBLE);
                    countryCode = appointmentInfo.getCountryCode();
                    tvPhoneNumber.setText(countryCode + "\u00a0" + appointmentInfo.getPhoneNumber());
                } else {
                    hideView(llPhoneNumber);
                }
                // to set status
                if (appointmentInfo.getApptStatus() != null) {
                    if (appointmentInfo.getApptStatus().equalsIgnoreCase("Completed")) {
                        llRating.setVisibility(View.VISIBLE);
                    } else {
                        hideView(llRating);
                    }
                    tvStatus.setVisibility(View.VISIBLE);
                    if (appointmentInfo.getApptStatus().equalsIgnoreCase("Cancelled")) {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                        tvStatus.setText(convertToTitleForm(appointmentInfo.getApptStatus()));

                    } else {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                        tvStatus.setText(convertToTitleForm(appointmentInfo.getApptStatus()));
                    }
                } else {
                    tvStatus.setVisibility(View.GONE);
                }


                // to set paid info
                if (appointmentInfo.getAmountPaid() != null && !appointmentInfo.getAmountPaid().equalsIgnoreCase("0.0")) {
                    llPayment.setVisibility(View.VISIBLE);
                    tvAmount.setText("₹" + " " + Config.getAmountNoOrTwoDecimalPoints(Double.parseDouble(appointmentInfo.getAmountPaid())) + " " + "PAID");
                } else {

                    llPayment.setVisibility(View.GONE);
                }

                // to set consumer name
                if (appointmentInfo.getAppmtFor() != null) {

                    if (appointmentInfo.getAppmtFor().get(0).getUserName() != null) {
                        tvConsumerName.setText(appointmentInfo.getAppmtFor().get(0).getUserName());
                    } else {
                        tvConsumerName.setText(appointmentInfo.getAppmtFor().get(0).getFirstName() + " " + appointmentInfo.getAppmtFor().get(0).getLastName());
                    }
                }

                // to set appointment date
                if (appointmentInfo.getAppmtDate() != null) {
                    tvDate.setText(getCustomDateString(appointmentInfo.getAppmtDate()));
                }

                // to set slot time
                if (appointmentInfo.getAppmtTime() != null) {

                    tvTime.setText(convertTime(appointmentInfo.getAppmtTime().split("-")[0]));
                }

                if (appointmentInfo.getBatchId() != null) {
                    llBatch.setVisibility(View.VISIBLE);
                    tvBatchNo.setText(appointmentInfo.getBatchId());
                } else {
                    llBatch.setVisibility(View.GONE);
                }

                // to set location
                if (appointmentInfo.getLocation() != null) {

                    if (appointmentInfo.getLocation().getPlace() != null) {

                        tvLocationName.setText(appointmentInfo.getLocation().getPlace());

                        tvLocationName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                openMapView(appointmentInfo.getLocation().getLattitude(), appointmentInfo.getLocation().getLongitude(), appointmentInfo.getLocation().getPlace());
                            }
                        });
                    }
                }

                if (isActive) {

                    cvShare.setVisibility(View.VISIBLE);
                    if (appointmentInfo.getApptStatus() != null) {
                        if (appointmentInfo.getApptStatus().equalsIgnoreCase("Confirmed") || appointmentInfo.getApptStatus().equalsIgnoreCase("Arrived")) {
                            llReschedule.setVisibility(View.VISIBLE);
                        } else {
                            hideView(llReschedule);
                        }

                        if (appointmentInfo.getApptStatus().equalsIgnoreCase("Confirmed") || appointmentInfo.getApptStatus().equalsIgnoreCase("Arrived") || appointmentInfo.getApptStatus().equalsIgnoreCase("prepaymentPending")) {
                            llCancel.setVisibility(View.VISIBLE);
                        } else {

                            hideView(llCancel);
                        }
                    }

                    if (appointmentInfo.getService() != null) {

                        if (appointmentInfo.getService().getLivetrack().equalsIgnoreCase("true")) {
                            llLocation.setVisibility(View.VISIBLE);
                            if (appointmentInfo.getJaldeeApptDistanceTime() != null) {
                                tvTrackingText.setText("   Tracking On   ");
                                Glide.with(BookingDetails.this).load(R.drawable.new_location).into(ivLtIcon);
                            } else {
                                tvTrackingText.setText("   Tracking Off   ");
                                ivLtIcon.setImageResource(R.drawable.location_off);

                            }
                        } else {
                            hideView(llLocation);
                        }
                    }

                    llSendAttachments.setVisibility(View.VISIBLE);
                    if (appointmentInfo.isHasAttachment()) {

                        llViewAttachments.setVisibility(View.VISIBLE);
                    } else {

                        hideView(llViewAttachments);
                    }

                    // to show Questionnaire option
                    if (appointmentInfo.getQuestionnaire() != null && appointmentInfo.getQuestionnaire().getQuestionAnswers() != null && appointmentInfo.getQuestionnaire().getQuestionAnswers().size() > 0) {

                        llQuestionnaire.setVisibility(View.VISIBLE);
                    } else {

                        hideView(llQuestionnaire);
                    }

                } else {
                    cvShare.setVisibility(View.GONE);
                    hideView(llReschedule);
                    hideView(llCancel);
                    hideView(llLocation);
                    if (appointmentInfo.isPrescShared()) {
                        llPrescription.setVisibility(View.VISIBLE);
                    } else {
                        hideView(llPrescription);
                    }
                }

                // hide instructions link when there are no post instructions
                if (appointmentInfo.getService() != null && appointmentInfo.getService().isPostInfoEnabled()) {
                    if (isActive) {
                        llInstructions.setVisibility(View.VISIBLE);
                    }
                } else {

                    hideView(llInstructions);
                }

                // hide customerNotes when there is no notes from consumer
                if (appointmentInfo.getConsumerNote() != null && !appointmentInfo.getConsumerNote().equalsIgnoreCase("")) {
                    if (isActive) {
                        llCustomerNotes.setVisibility(View.VISIBLE);
                        if (appointmentInfo.getProviderAccount() != null) {
                            if (appointmentInfo.getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("healthCare")) {
                                tvCustomerNotes.setText("Patient Note");
                            } else if (appointmentInfo.getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("educationalInstitution")) {
                                tvCustomerNotes.setText("Student Note");
                            } else {
                                tvCustomerNotes.setText("Customer Notes");
                            }
                        }
                    }
                } else {
                    hideView(llCustomerNotes);
                }

                if (appointmentInfo.getPaymentStatus().equalsIgnoreCase("FullyPaid") || appointmentInfo.getPaymentStatus().equalsIgnoreCase("Refund")) {
                    tvAmountToPay.setVisibility(View.GONE);
                    cvBill.setVisibility(View.VISIBLE);
                    tvBillText.setVisibility(View.GONE);
                    tvBillReceiptText.setVisibility(View.VISIBLE);
                    tvBillReceiptText.setText("Receipt");
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cvBill.getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    cvBill.setLayoutParams(lp);
                } else {
                    String amount = "₹" + " " + Config.getAmountNoOrTwoDecimalPoints(appointmentInfo.getAmountDue());
                    if (appointmentInfo.getApptStatus().equalsIgnoreCase("Cancelled")) {
                        tvAmountToPay.setVisibility(View.GONE);
                    } else {
                        tvAmountToPay.setText(amount);
                        tvAmountToPay.setVisibility(View.VISIBLE);
                    }
                    cvBill.setVisibility(View.VISIBLE);
                    tvBillText.setText("Bill");
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cvBill.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END);
                    cvBill.setLayoutParams(lp);
                }

                if (appointmentInfo.getBillViewStatus() != null && !appointmentInfo.getApptStatus().equalsIgnoreCase("cancelled")) {
                    if (appointmentInfo.getBillViewStatus().equalsIgnoreCase("Show")) {
                        cvBill.setVisibility(View.VISIBLE);
                    } else {
                        cvBill.setVisibility(View.GONE);
                    }

                } else {
                    /**26-3-21*/
                    /**/
                    if (!appointmentInfo.getPaymentStatus().equalsIgnoreCase("NotPaid")) {
                        cvBill.setVisibility(View.VISIBLE);
                    } else {
                        cvBill.setVisibility(View.GONE);
                    }/**/
                    //cvBill.setVisibility(View.GONE);
                    /***/
                }
                /**26-3-21*/
                if (appointmentInfo.getBillViewStatus() == null || appointmentInfo.getBillViewStatus().equalsIgnoreCase("NotShow") || appointmentInfo.getBillStatus().equals("Settled") || appointmentInfo.getApptStatus().equals("Rejected")) {
                    cvBill.setVisibility(View.GONE);
                }
                if (appointmentInfo.getApptStatus().equalsIgnoreCase("Cancelled"))
                    cvBill.setVisibility(View.GONE);

                /***/

                cvBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iBill = new Intent(BookingDetails.this, BillActivity.class);
                        iBill.putExtra("ynwUUID", appointmentInfo.getUid());
                        iBill.putExtra("provider", appointmentInfo.getProviderAccount().getBusinessName());
                        if (appointmentInfo.getProvider() != null) {
                            if (appointmentInfo.getProvider().getBusinessName() != null && !appointmentInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                                iBill.putExtra("providerName", appointmentInfo.getProvider().getBusinessName());
                            } else {
                                String name = appointmentInfo.getProvider().getFirstName() + " " + appointmentInfo.getProvider().getLastName();
                                iBill.putExtra("providerName", name);
                            }
                        }
                        iBill.putExtra("accountID", String.valueOf(appointmentInfo.getProviderAccount().getId()));
                        iBill.putExtra("payStatus", appointmentInfo.getPaymentStatus());
                        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                        iBill.putExtra("consumer", appointmentInfo.getAppmtFor().get(0).getFirstName() + " " + appointmentInfo.getAppmtFor().get(0).getLastName());
                        iBill.putExtra("uniqueId", appointmentInfo.getProviderAccount().getUniqueId());
                        iBill.putExtra("encId", appointmentInfo.getAppointmentEncId());
                        iBill.putExtra("bookingStatus", appointmentInfo.getApptStatus());
                        iBill.putExtra("location", appointmentInfo.getLocation().getPlace());
                        if (appointmentInfo.getProviderAccount() != null && appointmentInfo.getProviderAccount().getServiceSector() != null && appointmentInfo.getProviderAccount().getServiceSector().getDomain() != null) {
                            if (!appointmentInfo.getProviderAccount().getServiceSector().getDomain().isEmpty()) {
                                iBill.putExtra("domain", appointmentInfo.getProviderAccount().getServiceSector().getDomain());
                            }
                        }
                        startActivity(iBill);

                    }
                });

                cvMeetingDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        apiGetMeetingDetails(appointmentInfo.getUid(), appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode(), appointmentInfo.getProviderAccount().getId(), appointmentInfo);

                    }
                });

                if (appointmentInfo.isPrescShared()) {
                    llPrescription.setVisibility(View.VISIBLE);
                } else {
                    hideView(llPrescription);
                }

                llPrescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prescriptionDialog = new PrescriptionDialog(mContext, isActive, appointmentInfo, "checkin");
                        prescriptionDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
                        prescriptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        prescriptionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        prescriptionDialog.show();
                        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        prescriptionDialog.getWindow().setGravity(Gravity.BOTTOM);
                        prescriptionDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                });


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void apiGetMeetingDetails(String uuid, String mode, int accountID, ActiveAppointment info) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<TeleServiceCheckIn> call = apiService.getMeetingDetailsAppointment(uuid, mode, accountID);

        call.enqueue(new Callback<TeleServiceCheckIn>() {
            @Override
            public void onResponse(Call<TeleServiceCheckIn> call, Response<TeleServiceCheckIn> response) {

                try {
                    if (response.code() == 200) {

                        meetingDetails = response.body();
                        if (meetingDetails != null) {

                            if (mode.equalsIgnoreCase("GoogleMeet")) {

                                showMeetingDetailsWindow(info, mode, meetingDetails);
                            } else if (mode.equalsIgnoreCase("Zoom")) {

                                showMeetingDetailsWindow(info, mode, meetingDetails);

                            } else if (mode.equalsIgnoreCase("WhatsApp")) {

                                showMeetingWindow(info, mode, meetingDetails);

                            } else if (mode.equalsIgnoreCase("Phone")) {

                                showMeetingWindow(info, mode, meetingDetails);

                            } else if (mode.equalsIgnoreCase("VideoCall")) {

                                showMeetingDetailsWindow(info, mode, meetingDetails);

                            }
                        }
                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TeleServiceCheckIn> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

            }
        });
    }

    // for zoom and GMeet
    public void showMeetingDetailsWindow(ActiveAppointment activeAppointment, String mode, TeleServiceCheckIn meetingDetails) {

        meetingDetailsWindow = new MeetingDetailsWindow(mContext, activeAppointment.getApptTime(), activeAppointment.getService().getName(), meetingDetails, activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode(), activeAppointment.getService().getVirtualCallingModes().get(0).getVirtualServiceType());
        meetingDetailsWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingDetailsWindow.show();
        meetingDetailsWindow.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingDetailsWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // for Whatsspp and Phone
    public void showMeetingWindow(ActiveAppointment activeAppointment, String mode, TeleServiceCheckIn meetingDetails) {

        if (mode.equalsIgnoreCase("WhatsApp")) {
            meetingInfo = new MeetingInfo(mContext, activeAppointment.getApptTime(), activeAppointment.getService().getName(), meetingDetails, activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode(), activeAppointment.getVirtualService().getWhatsApp(), activeAppointment.getService().getVirtualServiceType(), activeAppointment.getCountryCode(), Constants.APPOINTMENT);
        } else {
            meetingInfo = new MeetingInfo(mContext, activeAppointment.getApptTime(), activeAppointment.getService().getName(), meetingDetails, activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode(), activeAppointment.getVirtualService().getPhone(), "", activeAppointment.getCountryCode(), Constants.APPOINTMENT);
        }
        meetingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingInfo.show();
        meetingInfo.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingInfo.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void ApiDeleteAppointment(String ynwuuid, String accountID, final BottomSheetDialog dialog) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.deleteAppointment(ynwuuid, String.valueOf(accountID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(BookingDetails.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            DynamicToast.make(context, "Appointment cancelled successfully", AppCompatResources.getDrawable(
                                    context, R.drawable.ic_info_black),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            isActive = false;
                            getAppointmentDetails(apptInfo.getUid(), apptInfo.getProviderAccount().getId());

                        }
                    } else {
                        if (response.code() != 419) {
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
                    Config.closeDialog(BookingDetails.this, mDialog);
            }
        });
    }

    private void ViewMoreActions() {

        if (llMoreDetails.getVisibility() != View.VISIBLE) {

            llMoreDetails.setVisibility(View.VISIBLE);
            tvViewMore.setText("View Less");
            isTvViewMore = true;

        } else {

            llMoreDetails.setVisibility(View.GONE);
            tvViewMore.setText("View More");
            isTvViewMore = false;
        }
    }

    BottomSheetDialog dialog;
    float rate = 0;
    String comment = "";

    private void ApiRating(final String accountID, final String UUID) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Map<String, String> query = new HashMap<>();
        query.put("account-eq", accountID);
        query.put("uId-eq", UUID);
        Call<ArrayList<RatingResponse>> call = apiService.getRatingApp(query);
        Config.logV("Location-----###########@@@@@@" + query);
        call.enqueue(new Callback<ArrayList<RatingResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RatingResponse>> call, final Response<ArrayList<RatingResponse>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(BookingDetails.this, mDialog);
                    Config.logV("URL----------Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());
                    if (response.code() == 200) {
                        final ArrayList<RatingResponse> mRatingDATA = response.body();
                        Config.logV("Response--code--------BottomSheetDialog-----------------" + response.code());
                        dialog = new BottomSheetDialog(mContext);
                        dialog.setContentView(R.layout.rating);
                        dialog.setCancelable(true);
                        dialog.show();
                        TextView tv_title = (TextView) dialog.findViewById(R.id.txtratevisit);
                        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                        final RatingBar rating = (RatingBar) dialog.findViewById(R.id.rRatingBar);
                        Typeface tyface = Typeface.createFromAsset(mContext.getAssets(),
                                "fonts/Montserrat_Bold.otf");
                        tv_title.setTypeface(tyface);
                        final Button btn_close = (Button) dialog.findViewById(R.id.btn_cancel);
                        final Button btn_rate = (Button) dialog.findViewById(R.id.btn_send);
                        btn_rate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rate = rating.getRating();
                                comment = edt_message.getText().toString();
                                if (response.body().size() == 0) {
                                    firstTimeRating = true;
                                } else {
                                    firstTimeRating = false;
                                }
                                ApiPUTRating(Math.round(rate), UUID, comment, accountID, dialog, firstTimeRating);

                            }
                        });
                        edt_message.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void afterTextChanged(Editable arg0) {
                                if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty() && rating != null && rating.getRating() != 0) {
                                    btn_rate.setEnabled(true);
                                    btn_rate.setClickable(true);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.curved_save));
                                } else {
                                    btn_rate.setEnabled(false);
                                    btn_rate.setClickable(false);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                }
                            }

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        });

                        if (rating != null) {
                            rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                    if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty() && rating.getRating() != 0) {
                                        btn_rate.setEnabled(true);
                                        btn_rate.setClickable(true);
                                        btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.curved_save));
                                    } else {
                                        btn_rate.setEnabled(false);
                                        btn_rate.setClickable(false);
                                        btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
                                    }
                                }
                            });
                        }

                        btn_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        if (response.body().size() > 0) {
                            if (mRatingDATA.get(0).getStars() != 0) {
                                rating.setRating(Float.valueOf(mRatingDATA.get(0).getStars()));
                            }
                            if (mRatingDATA.get(0).getFeedback() != null) {
                                Config.logV("Comments---------" + mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                                edt_message.setText(mRatingDATA.get(0).getFeedback().get(mRatingDATA.get(0).getFeedback().size() - 1).getComments());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<RatingResponse>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(BookingDetails.this, mDialog);
            }
        });
    }

    private void ApiPUTRating(final int stars, final String UUID, String feedback, String accountID, final BottomSheetDialog dialog, boolean firstTimerate) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("uuid", UUID);
            jsonObj.put("stars", String.valueOf(stars));
            jsonObj.put("feedback", feedback);
            Config.logV("Feedback--------------" + feedback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call;
        if (firstTimerate) {
            call = apiService.PostRatingApp(accountID, body);
        } else {
            call = apiService.PutRatingApp(accountID, body);
        }
//        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL-------Request---" + response.raw().request().url().toString().trim());
                    if (mDialog.isShowing())
                        Config.closeDialog(BookingDetails.this, mDialog);
                    dialog.dismiss();
                    Config.logV("Put Rating#########################" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {
                            DynamicToast.make(context, "Rated successfully", AppCompatResources.getDrawable(
                                    context, R.drawable.icon_tickmark),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Location-----###########@@@@@@-------Fail--------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(BookingDetails.this, mDialog);
            }
        });
    }

    private void updateImages() {

        if (imagePathList != null && imagePathList.size() > 0) {

            imagePreviewAdapter = new ImagePreviewAdapter(imagePathList, mContext, true, iDeleteImagesInterface);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
            recycle_image_attachment.setLayoutManager(mLayoutManager);
            recycle_image_attachment.setAdapter(imagePreviewAdapter);
            imagePreviewAdapter.notifyDataSetChanged();
        }
    }

    private void sendAppointmentAttachments(int accountId, String ynwUuid) {

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type;
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {

            String extension = "";

            if (imagePathList.get(i).getImagePath().contains(".")) {
                extension = imagePathList.get(i).getImagePath().substring(imagePathList.get(i).getImagePath().lastIndexOf(".") + 1);
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
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(imagePathList.get(i).getImagePath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            } else {
                file = new File(imagePathList.get(i).getImagePath());
            }
            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
        }

        Map<String, String> query = new HashMap<>();
        String json = "";

        for (int i = 0; i < imagePathList.size(); i++) {

            query.put(String.valueOf(i), imagePathList.get(i).getCaption());

        }
        Gson gson = new GsonBuilder().create();
        json = gson.toJson(query);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json);
        mBuilder.addFormDataPart("captions", "blob", body);
        RequestBody requestBody = mBuilder.build();
        Call<ResponseBody> call = apiService.appointmentSendAttachments(ynwUuid, accountId, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(BookingDetails.this, mDialog);

                    if (response.code() == 200) {

                        if (response.body() != null) {

                            DynamicToast.make(mContext, "Attachments sent successfully",
                                    ContextCompat.getColor(mContext, R.color.white), ContextCompat.getColor(mContext, R.color.green), Toast.LENGTH_SHORT).show();

                        }

                    } else {


                    }

                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(BookingDetails.this, mDialog);
            }
        });


    }

    private QuestionnaireResponseInput buildQuestionnaireInput(QuestionnaireResponse questionnaire) {

        QuestionnaireResponseInput responseInput = new QuestionnaireResponseInput();
        responseInput.setQuestionnaireId(questionnaire.getQuestionnaireId());
        ArrayList<AnswerLineResponse> answerLineResponse = new ArrayList<>();
        ArrayList<GetQuestion> questions = new ArrayList<>();

        for (QuestionAnswers qAnswers : questionnaire.getQuestionAnswers()) {

            answerLineResponse.add(qAnswers.getAnswerLine());
            questions.add(qAnswers.getGetQuestion());

        }

        responseInput.setAnswerLines(answerLineResponse);
        responseInput.setQuestions(questions);

        return responseInput;

    }

    private ArrayList<LabelPath> buildQuestionnaireLabelPaths(QuestionnaireResponse questionnaire) {

        ArrayList<LabelPath> labelPaths = new ArrayList<>();

        for (QuestionAnswers qAnswers : questionnaire.getQuestionAnswers()) {

            if (qAnswers.getGetQuestion().getFieldDataType().equalsIgnoreCase("fileUpload")) {

                JsonArray jsonArray = new JsonArray();
                jsonArray = qAnswers.getAnswerLine().getAnswer().get("fileUpload").getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {

                    LabelPath path = new LabelPath();
                    path.setId(labelPaths.size());
                    path.setFileName(jsonArray.get(i).getAsJsonObject().get("caption").getAsString());
                    path.setLabelName(qAnswers.getAnswerLine().getLabelName());
                    path.setPath(jsonArray.get(i).getAsJsonObject().get("s3path").getAsString());
                    path.setType(jsonArray.get(i).getAsJsonObject().get("type").getAsString());

                    labelPaths.add(path);
                }

            }

        }

        return labelPaths;

    }


    private void getAppointmentImages(String uid, int id) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<ShoppingList>> call = apiService.getAppointmentAttachments(uid, id);
        call.enqueue(new Callback<ArrayList<ShoppingList>>() {
            @Override
            public void onResponse(Call<ArrayList<ShoppingList>> call, Response<ArrayList<ShoppingList>> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(BookingDetails.this, mDialog);
                try {

                    if (response.code() == 200) {

                        ArrayList<ShoppingList> attachments = new ArrayList<>();
                        attachments = response.body();

                        if (attachments != null && attachments.size() > 0) {

                            Intent intent = new Intent(mContext, ViewAttachmentActivity.class);
                            intent.putExtra("imagesList", attachments);
                            startActivity(intent);

                        }

                    }
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ShoppingList>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(BookingDetails.this, mDialog);
            }
        });

    }


    public static String convertToTitleForm(String name) {
        String convertName = name;
        convertName = convertName.substring(0, 1).toUpperCase() + convertName.substring(1).toLowerCase();
        return convertName;
    }

    public static String getCustomDateString(String d) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(d);
        String date = format.format(date1);

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("d'st' MMM, yyyy");

        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("d'nd' MMM, yyyy");

        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("d'rd' MMM, yyyy");

        else
            format = new SimpleDateFormat("d'th' MMM, yyyy");

        String yourDate = format.format(date1);

        return yourDate;
    }

    public static String convertTime(String time) {

        String formattedTime = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date dateObj = sdf.parse(time);
            time = new SimpleDateFormat("hh:mm aa").format(dateObj);
            formattedTime = time.replace("am", "AM").replace("pm", "PM");

        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }

    private void openMapView(String latitude, String longitude, String locationName) {
        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + locationName);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
    }

    private void hideView(View view) {
        GridLayout gridLayout = (GridLayout) view.getParent();
        if (gridLayout != null) {
            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                if (view == gridLayout.getChildAt(i)) {
                    gridLayout.removeViewAt(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (fromPushNotification) {
            Intent intent = new Intent(BookingDetails.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            super.onBackPressed();
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public Date getCurrentDate() {

        Date date = new Date();
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            ;
            String d = formatter.format(date);
            date = (Date) formatter.parse(d);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
//                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();fc
                            Toast.makeText(mContext, "You Denied the Permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(mContext, "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   mTxvBuy.setEnabled(true);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        String orgFilePath = getRealPathFromURI(uri, this);
                        String filepath = "";//default fileName

                        String mimeType = this.mContext.getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        if (Arrays.asList(fileExtsSupported).contains(extension)) {
                            if (orgFilePath == null) {
                                orgFilePath = getFilePathFromURI(mContext, uri, extension);
                            }
                        } else {
                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ShoppingListModel model = new ShoppingListModel();
                        model.setImagePath(orgFilePath);
                        imagePathList.add(model);


                        if (imagePathList.size() > 0) {
                            tvErrorMessage.setVisibility(View.GONE);
                        } else {
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }

                        /*DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();*/

                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri imageUri = item.getUri();
                            String orgFilePath = getRealPathFromURI(imageUri, this);
                            String filepath = "";//default fileName

                            String mimeType = mContext.getContentResolver().getType(imageUri);
                            String uriString = imageUri.toString();
                            String extension = "";
                            if (uriString.contains(".")) {
                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                            }

                            if (mimeType != null) {
                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                            }
                            if (Arrays.asList(fileExtsSupported).contains(extension)) {
                                if (orgFilePath == null) {
                                    orgFilePath = getFilePathFromURI(mContext, imageUri, extension);
                                }
                            } else {
                                Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ShoppingListModel model = new ShoppingListModel();
                            model.setImagePath(orgFilePath);
                            imagePathList.add(model);

                            if (imagePathList.size() > 0) {
                                tvErrorMessage.setVisibility(View.GONE);
                            } else {
                                tvErrorMessage.setVisibility(View.VISIBLE);
                            }
                        }
                        /*DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {
            if (data != null && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //      imageview.setImageBitmap(bitmap);
                path = saveImage(bitmap);
                // imagePathList.add(bitmap.toString());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                }
//            String paths = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "Pic from camera", null);
                if (path != null) {
                    mImageUri = Uri.parse(path);
                    ShoppingListModel model = new ShoppingListModel();
                    model.setImagePath(mImageUri.toString());
                    imagePathList.add(model);
                    if (imagePathList.size() > 0) {
                        tvErrorMessage.setVisibility(View.GONE);
                    } else {
                        tvErrorMessage.setVisibility(View.VISIBLE);
                    }
                }
                try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                recycle_image_attachment.setLayoutManager(mLayoutManager);
                recycle_image_attachment.setAdapter(mDetailFileAdapter);
                mDetailFileAdapter.notifyDataSetChanged();*/
            }
        }
    }


    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }

    public static String getFilePathFromURI(Context context, Uri contentUri, String extension) {
        //copy file and send new file path
        String fileName = getFileNameInfo(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            String ext = "";
            if (fileName.contains(".")) {
            } else {
                ext = "." + extension;
            }
            File wallpaperDirectoryFile = new File(
                    Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY + File.separator + fileName + ext);
            copy(context, contentUri, wallpaperDirectoryFile);
            return wallpaperDirectoryFile.getAbsolutePath();
        }
        return null;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            FileOutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected static String getFileNameInfo(Uri uri) {
        if (uri == null) {
            return null;
        }
        String fileName = null;
        String path = uri.getPath();
        int cut = 0;
        if (path != null) {
            cut = path.lastIndexOf('/');
        }
        if (cut != -1) {
            if (path != null) {
                fileName = path.substring(cut + 1);
            }
        }
        return fileName;
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
            MediaScannerConnection.scanFile(mContext,
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
    public void delete(int position, String imagePath) {

        imagePathList.remove(position);
        imagePreviewAdapter.notifyDataSetChanged();

        if (imagePathList != null && imagePathList.size() > 0) {

            for (int i = 0; i < imagePathList.size(); i++) {

                if (imagePathList.get(i).getImagePath().equalsIgnoreCase(imagePath)) {

                    imagePathList.remove(i);
                }
            }
        }
    }

    @Override
    public void addedNotes(int position) {

        showNotesDialog(position);

    }

    private void showNotesDialog(int position) {

        customNotes = new CustomNotes(mContext, position, iSaveNotes, imagePathList.get(position).getCaption());
        customNotes.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        customNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customNotes.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customNotes.setCancelable(false);
        customNotes.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        customNotes.getWindow().setGravity(Gravity.BOTTOM);
        customNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void saveMessage(String caption, int position) {

        imagePathList.get(position).setCaption(caption);
        imagePreviewAdapter.notifyDataSetChanged();
    }
}