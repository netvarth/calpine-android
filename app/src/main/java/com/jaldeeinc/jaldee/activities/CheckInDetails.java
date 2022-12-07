package com.jaldeeinc.jaldee.activities;

import static com.jaldeeinc.jaldee.connection.ApiClient.context;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.jaldeeinc.jaldee.custom.Contents;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomerNotes;
import com.jaldeeinc.jaldee.custom.InstructionsDialog;
import com.jaldeeinc.jaldee.custom.MeetingDetailsWindow;
import com.jaldeeinc.jaldee.custom.MeetingInfo;
import com.jaldeeinc.jaldee.custom.PrescriptionDialog;
import com.jaldeeinc.jaldee.custom.QRCodeEncoder;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.model.LabelPath;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.jaldeeinc.jaldee.model.QuestionnaireResponseInput;
import com.jaldeeinc.jaldee.model.RlsdQnr;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.AnswerLineResponse;
import com.jaldeeinc.jaldee.response.GetQuestion;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInDetails extends AppCompatActivity implements IDeleteImagesInterface, ISaveNotes {

    @BindView(R.id.tv_datehint)
    TextView tv_datehint;
    @BindView(R.id.ll_token)
    LinearLayout ll_token;
    @BindView(R.id.tv_token_number)
    TextView tv_token_number;
    @BindView(R.id.tv_tokenWaitTime)
    TextView tvTokenWaitTime;
    @BindView(R.id.ll_tokenWaitTime)
    LinearLayout ll_tokenWaitTime;
    @BindView(R.id.ll_batch)
    LinearLayout ll_batch;
    @BindView(R.id.tv_doctorName)
    TextView tvDoctorName;
    @BindView(R.id.tv_doctorName1)
    TextView tvDoctorName1;
    @BindView(R.id.tv_locationName)
    TextView tvLocationName;
    @BindView(R.id.iv_teleService)
    ImageView ivTeleService;
    @BindView(R.id.tv_serviceName)
    TextView tvServiceName;
    @BindView(R.id.tv_consumerName)
    TextView tvConsumerName;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_phoneNumber)
    TextView tvPhoneNumber;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_providerName)
    TextView tvProviderName;
    @BindView(R.id.tv_providerName1)
    TextView tvProviderName1;
    @BindView(R.id.tv_confirmationNumber)
    TextView tvConfirmationNumber;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_viewMore)
    TextView tvViewMore;
    @BindView(R.id.tv_billText)
    TextView tvBillText;
    @BindView(R.id.tv_bill_receiptText)
    TextView tvBillReceiptText;
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
    CardView llMoreDetails;
    @BindView(R.id.ll_message)
    LinearLayout llMessage;
    @BindView(R.id.ll_reschedule)
    LinearLayout llReschedule;
    @BindView(R.id.ll_location)
    LinearLayout llLocation;
    @BindView(R.id.iv_ltIcon)
    ImageView ivLtIcon;
    @BindView(R.id.tv_trackingText)
    TextView tvTrackingText;
    @BindView(R.id.ll_rating)
    LinearLayout llRating;
    @BindView(R.id.ll_customerNotes)
    LinearLayout llCustomerNotes;
    @BindView(R.id.tv_customerNotes)
    TextView tvCustomerNotes;
    @BindView(R.id.ll_instructions)
    LinearLayout llInstructions;
    @BindView(R.id.tv_amountToPay)
    TextView tvAmountToPay;
    @BindView(R.id.cv_meetingDetails)
    CardView cvMeetingDetails;
    @BindView(R.id.iv_meetingIcon)
    ImageView ivMeetingIcon;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_Qr)
    ImageView ivQR;
    @BindView(R.id.ll_prescription)
    LinearLayout llPrescription;
    @BindView(R.id.ll_sendAttachments)
    LinearLayout llSendAttachments;
    @BindView(R.id.ll_viewAttachments)
    LinearLayout llViewAttachments;
    @BindView(R.id.ll_questionnaire)
    LinearLayout llQuestionnaire;
    @BindView(R.id.ll_service_option_qnr)
    LinearLayout ll_service_option_qnr;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.tv_cnsmrDetails_Heading)
    TextView tv_cnsmrDetails_Heading;
    @BindView(R.id.icon_text)
    TextView icon_text;
    @BindView(R.id.iv_location_icon)
    ImageView iv_location_icon;
    @BindView(R.id.iv_prvdr_phone_icon)
    ImageView iv_prvdr_phone_icon;
    @BindView(R.id.iv_prvdr_email_icon)
    ImageView iv_prvdr_email_icon;


    boolean firstTimeRating = false;
    boolean isTvViewMore = false;

    private InstructionsDialog instructionsDialog;
    private CustomerNotes customerNotes;
    private Context mContext;
    private boolean isToken = true;
    private Bookings bookingInfo = new Bookings();
    private boolean isActive = true;
    private ActiveCheckIn activeCheckIn = new ActiveCheckIn();
    private TeleServiceCheckIn meetingDetails;
    private MeetingDetailsWindow meetingDetailsWindow;
    private MeetingInfo meetingInfo;
    private String uuid;
    private PrescriptionDialog prescriptionDialog;
    private Bookings bookings = new Bookings();
    String ynwUUid, accountId, countryCode, click_action;
    private boolean fromPushNotification = false;
    String uid;
    int id;

    // files related
    File f, file;
    String path, from, from1 = "";
    private LinearLayout llNoHistory;
    private ImageView iv_attach;
    TextView tv_attach, tv_camera;
    private BottomSheetDialog bDialog;
    TextView tvErrorMessage;
    RecyclerView recycle_image_attachment;
    private int GALLERY = 1, CAMERA = 2;
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
        //setContentView(R.layout.activity_check_in_details);
        setContentView(R.layout.activity_booking_details);


        ButterKnife.bind(CheckInDetails.this);
        mContext = CheckInDetails.this;
        iDeleteImagesInterface = (IDeleteImagesInterface) this;
        iSaveNotes = this;

        Intent i = getIntent();
        if (i != null) {
            //bookingInfo = (Bookings) i.getSerializableExtra("bookingInfo");
            uid = i.getStringExtra("uid");
            id = i.getIntExtra("accountId", 0);
            isActive = i.getBooleanExtra("isActive", true);
            ynwUUid = i.getStringExtra("uuid");
            accountId = i.getStringExtra("account");
            fromPushNotification = i.getBooleanExtra(Constants.PUSH_NOTIFICATION, false);
            click_action = i.getStringExtra("click_action");
        }


        tv_datehint.setText("Date & Time-window");
        ll_batch.setVisibility(View.GONE);
        tv_cnsmrDetails_Heading.setText("Booking For");
        Glide.with(mContext).load(R.drawable.location_icon_1).into(iv_location_icon);
        Glide.with(mContext).load(R.drawable.phone_icon_1).into(iv_prvdr_phone_icon);
        Glide.with(mContext).load(R.drawable.email_icon_1).into(iv_prvdr_email_icon);

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        llReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CheckInDetails.this, RescheduleCheckinActivity.class);
                intent.putExtra("uniqueId", activeCheckIn.getProviderAccount().getUniqueId());
                intent.putExtra("providerId", activeCheckIn.getProviderAccount().getId());
                intent.putExtra("ynwuuid", activeCheckIn.getYnwUuid());
                startActivity(intent);
            }
        });

        llInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (activeCheckIn != null && activeCheckIn.getService() != null) {
                        instructionsDialog = new InstructionsDialog(mContext, activeCheckIn.getService().getPostInfoText(), activeCheckIn.getService().getPostInfoTitle());
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
                    Intent intent = new Intent(CheckInDetails.this, ChatActivity.class);
                    if (activeCheckIn.getYnwUuid().contains("h_")) {
                        uuid = activeCheckIn.getYnwUuid().replace("h_", "");
                        intent.putExtra("uuid", uuid);
                    } else {
                        intent.putExtra("uuid", activeCheckIn.getYnwUuid());
                    }
                    intent.putExtra("accountId", activeCheckIn.getProviderAccount().getId());
                    intent.putExtra("name", activeCheckIn.getProviderAccount().getBusinessName());
                    intent.putExtra("from", Constants.CHECKIN);
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
                String mesg = "";
                if (isToken) {
                    mesg = "Do you want to cancel this Token ?";
                } else {
                    mesg = "Do you want to cancel this CheckIn ?";
                }
                txtsendmsg.setText(mesg);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApiDeleteCheckIn(activeCheckIn.getYnwUuid(), String.valueOf(activeCheckIn.getProviderAccount().getId()), dialog);
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
                    Intent intent = new Intent(mContext, CheckinShareLocation.class);
                    intent.putExtra("waitlistPhonenumber", activeCheckIn.getWaitlistingFor().get(0).getPhoneNo());
                    intent.putExtra("uuid", activeCheckIn.getYnwUuid());
                    intent.putExtra("accountID", String.valueOf(activeCheckIn.getProviderAccount().getId()));
                    intent.putExtra("title", activeCheckIn.getProviderAccount().getBusinessName());
                    intent.putExtra("terminology", "Check-in");
                    intent.putExtra("calcMode", "Check-in");
                    intent.putExtra("queueStartTime", activeCheckIn.getQueue().getQueueStartTime());
                    intent.putExtra("queueEndTime", activeCheckIn.getQueue().getQueueEndTime());
                    if (activeCheckIn.getJaldeeWaitlistDistanceTime() != null && activeCheckIn.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime() != null) {
                        intent.putExtra("jaldeeDistance", String.valueOf(activeCheckIn.getJaldeeWaitlistDistanceTime().getJaldeeDistanceTime().getJaldeeDistance().getDistance()));
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

                ApiRating(String.valueOf(activeCheckIn.getProviderAccount().getId()), activeCheckIn.getYnwUuid());

            }
        });

        llCustomerNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    if (activeCheckIn != null && activeCheckIn.getService() != null) {
                        customerNotes = new CustomerNotes(mContext, tvCustomerNotes.getText().toString()/*activeCheckIn.getService().getConsumerNoteTitle()*/, activeCheckIn.getConsumerNote());
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

                if (activeCheckIn != null && activeCheckIn.getCheckinEncId() != null) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/html");
                    String statusUrl = Constants.URL + "status/" + activeCheckIn.getCheckinEncId();
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Share your CheckIn/Token status link");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, statusUrl);
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

                            if (activeCheckIn != null) {
                                sendAttachments(activeCheckIn.getProviderAccount().getId(), activeCheckIn.getYnwUuid());
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

                if (activeCheckIn != null) {
                    getWaitlistImages(activeCheckIn.getYnwUuid(), activeCheckIn.getProviderAccount().getId());
                }
            }
        });
        ll_service_option_qnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activeCheckIn != null) {
                    if (activeCheckIn.getServiceOption() != null && activeCheckIn.getServiceOption().getQuestionAnswers() != null && activeCheckIn.getServiceOption().getQuestionAnswers().size() > 0) {
                        QuestionnaireResponseInput input = buildQuestionnaireInput(activeCheckIn.getServiceOption());
                        ArrayList<LabelPath> labelPaths = buildQuestionnaireLabelPaths(activeCheckIn.getServiceOption());

                        SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
                        SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

                        Intent intent = new Intent(mContext, UpdateQuestionnaire.class);
                        intent.putExtra("serviceId", activeCheckIn.getService().getId());
                        intent.putExtra("accountId", activeCheckIn.getProviderAccount().getId());
                        intent.putExtra("uid", activeCheckIn.getYnwUuid());
                        intent.putExtra("isEdit", false);
                        intent.putExtra("from", Constants.BOOKING_CHECKIN);
                        if (activeCheckIn != null && activeCheckIn.getWaitlistStatus() != null) {
                            intent.putExtra("status", activeCheckIn.getWaitlistStatus());
                        }
                        mContext.startActivity(intent);

                    }
                }
            }
        });
        llQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (activeCheckIn != null) {
                    if (activeCheckIn.getReleasedQnr() != null) {
                        if (activeCheckIn.getReleasedQnr().size() == 1 && activeCheckIn.getReleasedQnr().get(0).getStatus().equalsIgnoreCase("submitted")) {

                            QuestionnaireResponseInput input = buildQuestionnaireInput(activeCheckIn.getQuestionnaire());
                            ArrayList<LabelPath> labelPaths = buildQuestionnaireLabelPaths(activeCheckIn.getQuestionnaire());

                            SharedPreference.getInstance(mContext).setValue(Constants.QUESTIONNAIRE, new Gson().toJson(input));
                            SharedPreference.getInstance(mContext).setValue(Constants.QIMAGES, new Gson().toJson(labelPaths));

                            Intent intent = new Intent(mContext, UpdateQuestionnaire.class);
                            intent.putExtra("serviceId", activeCheckIn.getService().getId());
                            intent.putExtra("accountId", activeCheckIn.getProviderAccount().getId());
                            intent.putExtra("uid", activeCheckIn.getYnwUuid());
                            intent.putExtra("isEdit", true);
                            intent.putExtra("from", Constants.BOOKING_CHECKIN);
                            if (activeCheckIn != null && activeCheckIn.getWaitlistStatus() != null) {
                                intent.putExtra("status", activeCheckIn.getWaitlistStatus());
                            }
                            mContext.startActivity(intent);
                        } else {
                            Gson gson = new Gson();
                            String myJson = gson.toJson(activeCheckIn);

                            Intent intent = new Intent(mContext, ReleasedQNRActivity.class);
                            intent.putExtra("bookingInfo", myJson);
                            intent.putExtra("from", Constants.BOOKING_CHECKIN);
                            mContext.startActivity(intent);
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        try {
            // Api call
            if (uid != null) {
                getBookingDetails(uid, id);
            } else {

                // this gets called when activity is launched from push notification
                if (ynwUUid != null) {
                    if (click_action != null && click_action != "" && click_action.equalsIgnoreCase("CONSUMER_SHARE_PRESCRIPTION")) {
                        ViewMoreActions();
                        scrollView.scrollTo(0, scrollView.getBottom());
                    }
                    getBookingDetails(ynwUUid, Integer.parseInt(accountId));
                }
            }

            updateImages();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void getBookingDetails(String uid, int id) {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(CheckInDetails.this, CheckInDetails.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(uid, String.valueOf(id));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeCheckIn = response.body();

                        if (activeCheckIn != null) {
                            if (!activeCheckIn.getWaitlistStatus().equalsIgnoreCase("Cancelled") && !activeCheckIn.getWaitlistStatus().equalsIgnoreCase("done")) {

                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date strDate = sdf.parse(activeCheckIn.getDate());
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
                            if (activeCheckIn.getReleasedQnr() != null) {
                                List<RlsdQnr> fReleasedQNR = activeCheckIn.getReleasedQnr().stream()
                                        .filter(p -> !p.getStatus().equalsIgnoreCase("unReleased")).collect(Collectors.toList());
                                activeCheckIn.getReleasedQnr().clear();
                                activeCheckIn.setReleasedQnr((ArrayList<RlsdQnr>) fReleasedQNR); // remove releasedqnr response and add rlsdqnr with remove "unReleased" status
                            }
                            updateUI(activeCheckIn);
                        }
                    }
                } catch (Exception e) {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUI(ActiveCheckIn checkInInfo) {

        try {

            if (checkInInfo != null) {
                if (checkInInfo.getProvider() != null) {

                    if (checkInInfo.getProvider().getBusinessName() != null && !checkInInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                        tvDoctorName.setText(checkInInfo.getProvider().getBusinessName());
                        tvDoctorName.setVisibility(View.VISIBLE);
                        tvDoctorName1.setText(checkInInfo.getProvider().getBusinessName());
                        tvDoctorName1.setVisibility(View.VISIBLE);
                    } else {
                        String name = checkInInfo.getProvider().getFirstName() + " " + checkInInfo.getProvider().getLastName();
                        tvDoctorName.setText(name);
                        tvDoctorName.setVisibility(View.VISIBLE);
                        tvDoctorName1.setText(name);
                        tvDoctorName1.setVisibility(View.VISIBLE);
                    }
                    tvProviderName1.setText(checkInInfo.getProviderAccount().getBusinessName());
                    tvProviderName1.setVisibility(View.VISIBLE);
                    tvProviderName.setVisibility(View.VISIBLE);
                    tvProviderName.setText(checkInInfo.getProviderAccount().getBusinessName());
                    tvProviderName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Intent intent = new Intent(CheckInDetails.this, ProviderDetailActivity.class);
                                intent.putExtra("uniqueID", checkInInfo.getProviderAccount().getUniqueId());
                                intent.putExtra("locationId", checkInInfo.getQueue().getLocation().getId());
                                startActivity(intent);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    tvProviderName.setVisibility(View.GONE);
                    tvProviderName1.setVisibility(View.GONE);
                    tvDoctorName1.setVisibility(View.VISIBLE);
                    tvDoctorName1.setText(checkInInfo.getProviderAccount().getBusinessName());
                    tvDoctorName1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    tvDoctorName.setVisibility(View.VISIBLE);
                    tvDoctorName.setText(checkInInfo.getProviderAccount().getBusinessName());
                    tvDoctorName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                Intent intent = new Intent(CheckInDetails.this, ProviderDetailActivity.class);
                                intent.putExtra("uniqueID", checkInInfo.getProviderAccount().getUniqueId());
                                intent.putExtra("locationId", checkInInfo.getQueue().getLocation().getId());
                                startActivity(intent);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                if (checkInInfo.getCheckinEncId() != null) {
                    //Encode with a QR Code image
                    String statusUrl = Constants.URL + "status/" + checkInInfo.getCheckinEncId();
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int smallerDimension = width < height ? width : height;
                    smallerDimension = smallerDimension * 3 / 4;

                    QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(statusUrl,
                            null,
                            Contents.Type.TEXT,
                            BarcodeFormat.QR_CODE.toString(), smallerDimension);
                    try {
                        Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();

                        ivQR.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Dialog settingsDialog = new Dialog(CheckInDetails.this);
                                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_layout, null));
                                LinearLayout ll_download_qr = settingsDialog.findViewById(R.id.ll_download_qr);

                                ImageView imageView = settingsDialog.findViewById(R.id.iv_close);
                                ImageView ivQR = settingsDialog.findViewById(R.id.iv_Qr);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        settingsDialog.dismiss();
                                    }
                                });
                                ll_download_qr.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions((Activity) view.getContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                            // this will request for permission when permission is not true
                                        } else {
                                            storeImage(bitmap);
                                        }
                                    }
                                });
                                Glide.with(context).load(bitmap).into(ivQR);
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
                if (checkInInfo.getService() != null) {

                    if (checkInInfo.getService().getDeptName() != null) {

                        tvServiceName.setText(checkInInfo.getService().getName() + " (" + checkInInfo.getService().getDeptName() + ")");
                    } else {
                        tvServiceName.setText(checkInInfo.getService().getName());
                    }

                    if (checkInInfo.getService().getServiceType() != null && checkInInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {

                        if (isActive) {
                            if (checkInInfo.getWaitlistStatus() != null && checkInInfo.getWaitlistStatus().equalsIgnoreCase("done")) {
                                cvMeetingDetails.setVisibility(View.GONE);
                            } else {
                                cvMeetingDetails.setVisibility(View.VISIBLE);
                            }
                        } else {
                            cvMeetingDetails.setVisibility(View.GONE);
                        }
                        if (checkInInfo.getService().getVirtualCallingModes() != null) {
                            ivTeleService.setVisibility(View.VISIBLE);
                            if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivTeleService.setImageResource(R.drawable.zoom_meet);
                                ivMeetingIcon.setImageResource(R.drawable.zoom_meet);
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivTeleService.setImageResource(R.drawable.google_meet);
                                ivMeetingIcon.setImageResource(R.drawable.google_meet);
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                if (checkInInfo.getService().getVirtualServiceType() != null && checkInInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_videoicon);
                                    ivMeetingIcon.setImageResource(R.drawable.whatsapp_videoicon);
                                } else {
                                    ivTeleService.setImageResource(R.drawable.whatsapp_icon);
                                    ivMeetingIcon.setImageResource(R.drawable.whatsapp_icon);
                                }
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivTeleService.setImageResource(R.drawable.phoneicon_sized);
                                ivMeetingIcon.setImageResource(R.drawable.phoneicon_sized);
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
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
                if (checkInInfo.getCheckinEncId() != null) {
                    tvConfirmationNumber.setText(checkInInfo.getCheckinEncId());
                }
                // to set Phone number
                if (checkInInfo.getWaitlistPhoneNumber() != null && !checkInInfo.getWaitlistPhoneNumber().isEmpty()) {
                    tvPhoneNumber.setVisibility(View.VISIBLE);
                    countryCode = checkInInfo.getCountryCode();
                    tvPhoneNumber.setText(countryCode + "\u00a0" + checkInInfo.getWaitlistPhoneNumber());
                } else {
                    hideView(tvPhoneNumber);
                }
                // to set status
                if (checkInInfo.getWaitlistStatus() != null) {
                    if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("done")) {
                        llRating.setVisibility(View.VISIBLE);
                    } else {
                        hideView(llRating);
                    }
                    tvStatus.setVisibility(View.VISIBLE);
                    if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled")) {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.red));
                        tvStatus.setText(convertToTitleForm(checkInInfo.getWaitlistStatus()));

                    } else if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("done")) {
                        tvStatus.setText("Completed");
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                    } else {
                        tvStatus.setTextColor(mContext.getResources().getColor(R.color.location_theme));
                        tvStatus.setText(convertToTitleForm(checkInInfo.getWaitlistStatus()));
                    }
                } else {
                    tvStatus.setVisibility(View.GONE);
                }


                // to set paid info
                if (checkInInfo.getAmountPaid() != 0) {
                    llPayment.setVisibility(View.VISIBLE);
                    tvAmount.setText("₹" + Config.getAmountNoOrTwoDecimalPoints(checkInInfo.getAmountPaid()));
                } else {

                    llPayment.setVisibility(View.GONE);
                }

                // to set consumer name
                if (checkInInfo.getWaitlistingFor() != null) {
                    String fName = checkInInfo.getWaitlistingFor().get(0).getFirstName();
                    String lName = checkInInfo.getWaitlistingFor().get(0).getLastName();
                    if (fName != null && !fName.trim().isEmpty()) {
                        icon_text.setText(String.valueOf(fName.trim().charAt(0)));
                    } else if (lName != null && !lName.trim().isEmpty()) {
                        icon_text.setText(String.valueOf(lName.trim().charAt(0)));
                    }
                    tvConsumerName.setText(checkInInfo.getWaitlistingFor().get(0).getFirstName() + " " + checkInInfo.getWaitlistingFor().get(0).getLastName());

                }

                // to set appointment date
                if (checkInInfo.getDate() != null && checkInInfo.getQueue() != null) {
                    String date = getCustomDateString(checkInInfo.getDate());
                    String time = checkInInfo.getQueue().getQueueStartTime() + " - " + checkInInfo.getQueue().getQueueEndTime();
                    tvDate.setText(date);
                    tvTime.setText(time);

                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date checkinDate = sdf.parse(checkInInfo.getDate());
                Date today = sdf.parse(LocalDateTime.now().toString());
                // to set waitTime or token No with waitTime
                if (checkInInfo.getShowToken() != null && checkInInfo.getShowToken().equalsIgnoreCase("true")) {
                    tvTitle.setText("Token Details");
                    isToken = true;
                    if (checkInInfo.getCalculationMode() != null && !checkInInfo.getCalculationMode().equalsIgnoreCase("NoCalc")) {
                        ll_token.setVisibility(View.VISIBLE);
                        tv_token_number.setText(String.valueOf(checkInInfo.getToken()));
                        ll_tokenWaitTime.setVisibility(View.VISIBLE);
                        tvTokenWaitTime.setVisibility(View.VISIBLE);
                        if (!checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled") && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("done") && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("started")) {
                            tvTokenWaitTime.setVisibility(View.VISIBLE);
                            if (checkinDate.after(today)) {   //future upcomming checkin/token service time
                                tvTokenWaitTime.setText("Starts at : " + (checkInInfo.getServiceTime()));

                            } else {
                                tvTokenWaitTime.setText("Est wait time : " + Config.getTimeinHourMinutes(checkInInfo.getAppxWaitingTime()));
                            }
                        } else {
                            tvTokenWaitTime.setVisibility(View.GONE);
                        }

                    } else {
                        ll_token.setVisibility(View.VISIBLE);
                        tv_token_number.setText(String.valueOf(checkInInfo.getToken()));
                        tvTokenWaitTime.setVisibility(View.GONE);
                        ll_tokenWaitTime.setVisibility(View.GONE);
                    }
                } else {
                    tvTitle.setText("CheckIn Details");
                    isToken = false;
                    ll_tokenWaitTime.setVisibility(View.VISIBLE);
                    tvTokenWaitTime.setVisibility(View.VISIBLE);

                    if (checkinDate.after(today)) {    //future upcomming checkin/token service time
                        tvTokenWaitTime.setText("Starts at : " + (checkInInfo.getServiceTime()));
                    } else {
                        tvTokenWaitTime.setText("Estimated waiting time : " + Config.getTimeinHourMinutes(checkInInfo.getAppxWaitingTime()));
                    }
                    if (!checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled") && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("done") && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("started")) {
                        tvTime.setVisibility(View.VISIBLE);
                    } else {
                        tvTime.setVisibility(View.GONE);
                    }
                }


                // to set location
                if (checkInInfo.getQueue() != null && checkInInfo.getQueue().getLocation() != null) {

                    if (checkInInfo.getQueue().getLocation().getPlace() != null) {

                        tvLocationName.setText(checkInInfo.getQueue().getLocation().getPlace());

                        tvLocationName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                openMapView(checkInInfo.getQueue().getLocation().getLattitude(), checkInInfo.getQueue().getLocation().getLongitude(), checkInInfo.getQueue().getLocation().getPlace());
                            }
                        });
                    }
                }

                if (isActive) {

                    cvShare.setVisibility(View.VISIBLE);
                    if (checkInInfo.getWaitlistStatus() != null) {
                        if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Checkedin") || checkInInfo.getWaitlistStatus().equalsIgnoreCase("Arrived")) {
                            llReschedule.setVisibility(View.VISIBLE);
                        } else {
                            hideView(llReschedule);
                        }

                        if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Checkedin") || checkInInfo.getWaitlistStatus().equalsIgnoreCase("Arrived") || checkInInfo.getWaitlistStatus().equalsIgnoreCase("prepaymentPending")) {
                            llCancel.setVisibility(View.VISIBLE);
                        } else {

                            hideView(llCancel);
                        }
                    }

                    llSendAttachments.setVisibility(View.VISIBLE);
                    if (checkInInfo.isHasAttachment()) {

                        llViewAttachments.setVisibility(View.VISIBLE);
                    } else {

                        hideView(llViewAttachments);
                    }

                    // to show Questionnaire option
                    if (checkInInfo.getQuestionnaire() != null && checkInInfo.getQuestionnaire().getQuestionAnswers() != null && checkInInfo.getQuestionnaire().getQuestionAnswers().size() > 0) {
                        llQuestionnaire.setVisibility(View.VISIBLE);
//                  } else if (checkInInfo.getReleasedQnr() != null && checkInInfo.getReleasedQnr().size() > 0) {
                    } else if (checkInInfo.getReleasedQnr() != null && checkInInfo.getQuestionnaire() != null && checkInInfo.getQuestionnaire().getQuestionAnswers() != null && checkInInfo.getReleasedQnr().size() > 0) {
                        llQuestionnaire.setVisibility(View.VISIBLE);
                    } else {
                        hideView(llQuestionnaire);
                    }
                    if (checkInInfo.getServiceOption() != null && checkInInfo.getServiceOption().getQuestionAnswers() != null && checkInInfo.getServiceOption().getQuestionAnswers().size() > 0) {
                        ll_service_option_qnr.setVisibility(View.VISIBLE);
                    } else {
                        hideView(ll_service_option_qnr);
                    }
                    if (checkInInfo.getService() != null) {

                        if (checkInInfo.getService().getLivetrack().equalsIgnoreCase("true")) {
                            llLocation.setVisibility(View.VISIBLE);
                            if (checkInInfo.getJaldeeWaitlistDistanceTime() != null) {
                                tvTrackingText.setText("   Tracking On   ");
                                Glide.with(CheckInDetails.this).load(R.drawable.new_location).into(ivLtIcon);
                            } else {
                                tvTrackingText.setText("   Tracking Off   ");
                                ivLtIcon.setImageResource(R.drawable.location_off);

                            }
                        } else {
                            hideView(llLocation);
                        }
                    }


                } else {
                    cvShare.setVisibility(View.GONE);
                    hideView(llReschedule);
                    hideView(llCancel);
                    hideView(llLocation);
                    hideView(llViewAttachments);
                    hideView(llSendAttachments);
                    if (checkInInfo.isPrescShared()) {
                        llPrescription.setVisibility(View.VISIBLE);
                    } else {
                        hideView(llPrescription);
                    }
                }

                // hide instructions link when there are no post instructions
                if (checkInInfo.getService() != null && checkInInfo.getService().isPostInfoEnabled()
                        && ((checkInInfo.getService().getPostInfoText() != null && !checkInInfo.getService().getPostInfoText().trim().isEmpty())
                        || (checkInInfo.getService().getPostInfoTitle() != null && !checkInInfo.getService().getPostInfoTitle().trim().isEmpty()))) {
                    if (isActive) {
                        llInstructions.setVisibility(View.VISIBLE);
                    }
                } else {

                    hideView(llInstructions);
                }

                // hide customerNotes when there is no notes from consumer
                if (checkInInfo.getConsumerNote() != null && !checkInInfo.getConsumerNote().equalsIgnoreCase("")) {
                    if (isActive) {
                        llCustomerNotes.setVisibility(View.VISIBLE);
                        if (checkInInfo.getProviderAccount() != null) {
                            if (checkInInfo.getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("healthCare")) {
                                tvCustomerNotes.setText("Patient Note");
                            } else if (checkInInfo.getProviderAccount().getServiceSector().getDomain().equalsIgnoreCase("educationalInstitution")) {
                                tvCustomerNotes.setText("Student Note");
                            } else {
                                tvCustomerNotes.setText("Customer Notes");
                            }
                        }
                    }
                } else {
                    hideView(llCustomerNotes);
                }

                if (checkInInfo.getPaymentStatus().equalsIgnoreCase("FullyPaid") || checkInInfo.getPaymentStatus().equalsIgnoreCase("Refund")) {
                    String amount = "₹" + " " + Config.getAmountNoOrTwoDecimalPoints(checkInInfo.getAmountDue());
                    tvAmountToPay.setText(amount);
                    tvAmountToPay.setVisibility(View.GONE);
                    cvBill.setVisibility(View.VISIBLE);
                    tvBillText.setVisibility(View.GONE);
                    tvBillReceiptText.setVisibility(View.VISIBLE);
                    tvBillReceiptText.setText("Receipt");
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cvBill.getLayoutParams();
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    cvBill.setLayoutParams(lp);
                } else {
                    String amount = "₹" + " " + Config.getAmountNoOrTwoDecimalPoints(checkInInfo.getAmountDue());
                    if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled")) {
                        tvAmountToPay.setVisibility(View.GONE);
                    } else {
                        tvAmountToPay.setText(amount);
                        tvAmountToPay.setVisibility(View.VISIBLE);
                    }
                    if (checkInInfo.getBillStatus() != null && checkInInfo.getBillId() != 0) {
                        cvBill.setVisibility(View.VISIBLE);
                    } else {
                        cvBill.setVisibility(View.GONE);
                    }
                    tvBillText.setText("Bill");
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cvBill.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END);
                    cvBill.setLayoutParams(lp);
                }
                if (!activeCheckIn.getYnwUuid().contains("h_")) {   //below code only execute if it is not a history booking

                    if (checkInInfo.getBillViewStatus() != null && !checkInInfo.getWaitlistStatus().equalsIgnoreCase("cancelled")) {
                        if (checkInInfo.getBillViewStatus().equalsIgnoreCase("Show")) {
                            cvBill.setVisibility(View.VISIBLE);
                        } else {
                            cvBill.setVisibility(View.GONE);
                        }

                    } else {
                        /**26-3-21*/
                        /**/
                        if (!checkInInfo.getPaymentStatus().equalsIgnoreCase("NotPaid")) {
                            cvBill.setVisibility(View.VISIBLE);
                            if (checkInInfo.getPaymentStatus().equalsIgnoreCase("Refund")) {
                                cvBill.setVisibility(View.GONE);
                            }
                        } else {
                            cvBill.setVisibility(View.GONE);
                        }/**/
                        // cvBill.setVisibility(View.GONE);
                        /***/
                    }
                    /**26-3-21*/
                    if (checkInInfo.getBillViewStatus() == null || checkInInfo.getBillViewStatus().equalsIgnoreCase("NotShow") || checkInInfo.getWaitlistStatus().equals("Rejected")) {
                        cvBill.setVisibility(View.GONE);
                    }
                    if (checkInInfo.getWaitlistStatus().equalsIgnoreCase("Cancelled"))
                        cvBill.setVisibility(View.GONE);
                    /***/
                    if (checkInInfo.getParentUuid() != null) {
                        cvBill.setVisibility(View.GONE);
                    }
                }

                cvBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent iBill = new Intent(CheckInDetails.this, BillActivity.class);
                        iBill.putExtra("ynwUUID", checkInInfo.getYnwUuid());
                        iBill.putExtra("provider", checkInInfo.getProviderAccount().getBusinessName());
                        if (checkInInfo.getProvider() != null) {
                            if (checkInInfo.getProvider().getBusinessName() != null && !checkInInfo.getProvider().getBusinessName().equalsIgnoreCase("")) {
                                iBill.putExtra("providerName", checkInInfo.getProvider().getBusinessName());
                            } else {
                                String name = checkInInfo.getProvider().getFirstName() + " " + checkInInfo.getProvider().getLastName();
                                iBill.putExtra("providerName", name);
                            }
                        }
                        iBill.putExtra("accountID", String.valueOf(checkInInfo.getProviderAccount().getId()));
                        iBill.putExtra("payStatus", checkInInfo.getPaymentStatus());
                        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
                        iBill.putExtra("consumer", checkInInfo.getWaitlistingFor().get(0).getFirstName() + " " + checkInInfo.getWaitlistingFor().get(0).getLastName());
                        iBill.putExtra("uniqueId", checkInInfo.getProviderAccount().getUniqueId());
                        iBill.putExtra("encId", checkInInfo.getCheckinEncId());
                        iBill.putExtra("bookingStatus", checkInInfo.getWaitlistStatus());
                        iBill.putExtra("location", checkInInfo.getQueue().getLocation().getPlace());
                        if (checkInInfo.getProviderAccount() != null && checkInInfo.getProviderAccount().getServiceSector() != null && checkInInfo.getProviderAccount().getServiceSector().getDomain() != null) {
                            if (!checkInInfo.getProviderAccount().getServiceSector().getDomain().isEmpty()) {
                                iBill.putExtra("domain", checkInInfo.getProviderAccount().getServiceSector().getDomain());
                            }
                        }


                        startActivity(iBill);

                    }
                });

                cvMeetingDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        apiGetMeetingDetails(checkInInfo.getYnwUuid(), checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode(), checkInInfo.getProviderAccount().getId(), checkInInfo);

                    }
                });

                if (checkInInfo.isPrescShared()) {
                    llPrescription.setVisibility(View.VISIBLE);
                } else {
                    hideView(llPrescription);
                }

                llPrescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prescriptionDialog = new PrescriptionDialog(mContext, isActive, checkInInfo, "checkin");
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

    BottomSheetDialog dialog;
    float rate = 0;
    String comment = "";

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

    private void ApiRating(final String accountID, final String UUID) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Map<String, String> query = new HashMap<>();
        query.put("account", accountID);
        query.put("uId-eq", UUID);
        Call<ArrayList<RatingResponse>> call = apiService.getRating(query);
        Config.logV("Location-----###########@@@@@@" + query);
        call.enqueue(new Callback<ArrayList<RatingResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RatingResponse>> call, final Response<ArrayList<RatingResponse>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckInDetails.this, mDialog);
                    Config.logV("URL----------Location-----###########@@@@@@-----" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code--------Message-----------------" + response.code());
                    if (response.code() == 200) {
                        final ArrayList<RatingResponse> mRatingDATA = response.body();
                        Config.logV("Response--code--------BottomSheetDialog-----------------" + response.code());
                        dialog = new BottomSheetDialog(mContext);
                        dialog.setContentView(R.layout.rating);
                        dialog.setCancelable(true);
                        dialog.show();
                        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
                        final RatingBar rating = (RatingBar) dialog.findViewById(R.id.rRatingBar);
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
                    Config.closeDialog(CheckInDetails.this, mDialog);
            }
        });
    }

    private void apiGetMeetingDetails(String uuid, String mode, int accountID, ActiveCheckIn info) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<TeleServiceCheckIn> call = apiService.getMeetingDetails(uuid, mode, accountID);

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
    public void showMeetingDetailsWindow(ActiveCheckIn activeCheckIn, String mode, TeleServiceCheckIn meetingDetails) {

        meetingDetailsWindow = new MeetingDetailsWindow(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getService().getVirtualCallingModes().get(0).getVirtualServiceType());
        meetingDetailsWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingDetailsWindow.show();
        meetingDetailsWindow.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingDetailsWindow.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    // for Whatsspp and Phone
    public void showMeetingWindow(ActiveCheckIn activeCheckIn, String mode, TeleServiceCheckIn meetingDetails) {

        if (mode.equalsIgnoreCase("WhatsApp")) {
            meetingInfo = new MeetingInfo(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getVirtualService().getWhatsApp(), activeCheckIn.getService().getVirtualServiceType(), activeCheckIn.getCountryCode(), Constants.CHECKIN);
        } else {
            meetingInfo = new MeetingInfo(mContext, activeCheckIn.getCheckInTime(), activeCheckIn.getService().getName(), meetingDetails, activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode(), activeCheckIn.getVirtualService().getPhone(), "", activeCheckIn.getCountryCode(), Constants.CHECKIN);
        }
        meetingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        meetingInfo.show();
        meetingInfo.setCancelable(false);
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        meetingInfo.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
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
            call = apiService.PostRating(accountID, body);
        } else {
            call = apiService.PutRating(accountID, body);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Config.logV("URL-------Request---" + response.raw().request().url().toString().trim());
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckInDetails.this, mDialog);
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
                    Config.closeDialog(CheckInDetails.this, mDialog);
            }
        });
    }


    private void ApiDeleteCheckIn(String ynwuuid, String accountID, final BottomSheetDialog dialog) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ResponseBody> call = apiService.deleteActiveCheckIn(ynwuuid, String.valueOf(accountID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckInDetails.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        if (response.body().string().equalsIgnoreCase("true")) {

                            String mesg = "";
                            if (isToken) {
                                mesg = "Token cancelled successfully";
                            } else {
                                mesg = "CheckIn cancelled successfully";
                            }
                            DynamicToast.make(context, mesg, AppCompatResources.getDrawable(
                                            context, R.drawable.ic_info_black),
                                    ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.green), Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            isActive = false;
                            getBookingDetails(activeCheckIn.getYnwUuid(), activeCheckIn.getProviderAccount().getId());

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
                    Config.closeDialog(CheckInDetails.this, mDialog);
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


    private void sendAttachments(int accountId, String ynwUuid) {

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaTypeAndExtention type;
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < imagePathList.size(); i++) {

            type = Config.getFileType(imagePathList.get(i).getImagePath());

            file = new File(imagePathList.get(i).getImagePath());

            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type.getMediaTypeWithExtention(), file));
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
        Call<ResponseBody> call = apiService.waitlistSendAttachments(ynwUuid, accountId, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(CheckInDetails.this, mDialog);

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
                    Config.closeDialog(CheckInDetails.this, mDialog);
            }
        });


    }

    private void getWaitlistImages(String ynwUuid, int id) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<ShoppingList>> call = apiService.getWaitlistAttachments(ynwUuid, id);
        call.enqueue(new Callback<ArrayList<ShoppingList>>() {
            @Override
            public void onResponse(Call<ArrayList<ShoppingList>> call, Response<ArrayList<ShoppingList>> response) {

                if (mDialog.isShowing())
                    Config.closeDialog(CheckInDetails.this, mDialog);
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
                    Config.closeDialog(CheckInDetails.this, mDialog);
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
            Intent intent = new Intent(CheckInDetails.this, Home.class);
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

                        String mimeType = this.mContext.getContentResolver().getType(uri);
                        String uriString = uri.toString();
                        String extension = "";
                        if (uriString.contains(".")) {
                            extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                        }

                        if (mimeType != null) {
                            extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                        }
                        File photoFile = null;

                        try {
                            // Creating file
                            try {
                                photoFile = Config.createFile(mContext, extension, true);
                            } catch (IOException ex) {
                                Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                // Log.d(TAG, "Error occurred while creating the file");
                            }

                            InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                            FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                            // Copying
                            Config.copyStream(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();
                        } catch (Exception e) {
                            Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                            //Log.d(TAG, "onActivityResult: " + e.toString());
                        }
                        String orgFilePath = photoFile.getAbsolutePath();
                        if (Arrays.asList(Constants.fileExtFormats).contains(extension)) {
                            if (orgFilePath == null) {
                                orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
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
                            Uri uri = item.getUri();
                            String mimeType = this.mContext.getContentResolver().getType(uri);
                            String uriString = uri.toString();
                            String extension = "";
                            if (uriString.contains(".")) {
                                extension = uriString.substring(uriString.lastIndexOf(".") + 1);
                            }

                            if (mimeType != null) {
                                extension = mimeType.substring(mimeType.lastIndexOf("/") + 1);
                            }
                            File photoFile = null;

                            try {
                                // Creating file
                                try {
                                    photoFile = Config.createFile(mContext, extension, true);
                                } catch (IOException ex) {
                                    Toast.makeText(mContext, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();

                                    // Log.d(TAG, "Error occurred while creating the file");
                                }

                                InputStream inputStream = mContext.getContentResolver().openInputStream(uri);
                                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                                // Copying
                                Config.copyStream(inputStream, fileOutputStream);
                                fileOutputStream.close();
                                inputStream.close();
                            } catch (Exception e) {
                                Toast.makeText(mContext, "onActivityResult: " + e.toString(), Toast.LENGTH_SHORT).show();

                                //Log.d(TAG, "onActivityResult: " + e.toString());
                            }
                            String orgFilePath = photoFile.getAbsolutePath();
                            if (Arrays.asList(Constants.fileExtFormats).contains(extension)) {

                                if (orgFilePath == null) {
                                    orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
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
                File photoFile = null;/////////
                // ///////
                try {//////////
                    photoFile = Config.createFile(mContext, "png", true);//////////
                } catch (IOException e) {/////////////
                    e.printStackTrace();///////////
                }///////////
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");/////////
                try (FileOutputStream out = new FileOutputStream(photoFile)) {////////////
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance////////////
                    // PNG is a lossless format, the compression factor (100) is ignored/////////
                } catch (IOException e) {////////////
                    e.printStackTrace();///////////
                }////////
                String path = photoFile.getAbsolutePath();////////

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

                /*DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                recycle_image_attachment.setLayoutManager(mLayoutManager);
                recycle_image_attachment.setAdapter(mDetailFileAdapter);
                mDetailFileAdapter.notifyDataSetChanged();*/
            }
        }
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

    private void storeImage(Bitmap image) {

        File pictureFile = null;
        try {
            pictureFile = Config.createFile(context, "png", false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (pictureFile == null) {
            //"Error creating media file, check storage permissions: "
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();

           /* Uri uri = Uri.fromFile(pictureFile);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String path = uri.getPath();
            String extension = path.substring(path.lastIndexOf("."));;
            String type = mime.getMimeTypeFromExtension(extension);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);*/

        } catch (FileNotFoundException e) {
            //Log.d(TAG, "File not found: " + e.getMessage());
            e.printStackTrace();

        } catch (IOException e) {
            //Log.d(TAG, "Error accessing file: " + e.getMessage());
            e.printStackTrace();

        }
    }
}