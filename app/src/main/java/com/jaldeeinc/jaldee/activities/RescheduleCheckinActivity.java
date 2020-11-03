package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jaldeeinc.jaldee.Interface.ISelectQ;
import com.jaldeeinc.jaldee.Interface.ISelectedQueue;
import com.jaldeeinc.jaldee.R;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinodev.androidneomorphframelayout.NeomorphFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.Interface.OnBottomReachedListener;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailFileImageAdapter;
import com.jaldeeinc.jaldee.adapter.QueuesAdapter;
import com.jaldeeinc.jaldee.adapter.TimeSlotsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddNotes;
import com.jaldeeinc.jaldee.custom.CheckInSlotsDialog;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.custom.SlotsDialog;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.AvailableSlotsData;
import com.jaldeeinc.jaldee.response.ProfileModel;
import com.jaldeeinc.jaldee.response.QueueTimeSlotModel;
import com.jaldeeinc.jaldee.response.SearchTerminology;
import com.jaldeeinc.jaldee.response.SlotsData;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
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
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
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

public class RescheduleCheckinActivity extends AppCompatActivity implements ISelectQ,ISelectedQueue,ISendMessage {


    @BindView(R.id.tv_spName)
    CustomTextViewBold tvSpName;

    @BindView(R.id.tv_locationName)
    CustomTextViewMedium tvLocationName;

    @BindView(R.id.tv_serviceName)
    CustomTextViewBold tvServiceName;

    @BindView(R.id.iv_teleService)
    ImageView ivteleService;

    @BindView(R.id.tv_number)
    CustomTextViewMedium tvNumber;

    @BindView(R.id.tv_email)
    CustomTextViewMedium tvEmail;

    @BindView(R.id.tv_consumerName)
    CustomTextViewBold tvConsumerName;

    @BindView(R.id.tv_date)
    CustomTextViewBold tvDate;

    @BindView(R.id.tv_time)
    CustomTextViewBold tvTime;

    @BindView(R.id.tv_changeTime)
    CustomTextViewBold tvChangeTime;

    @BindView(R.id.tv_actualTime)
    CustomTextViewBold tvActualTime;

    @BindView(R.id.cv_back)
    CardView cvBack;

    @BindView(R.id.cv_submit)
    CardView cvSubmit;

    @BindView(R.id.cv_cancel)
    CardView cvCancel;

    @BindView(R.id.iv_minus)
    ImageView ivMinus;

    @BindView(R.id.iv_add)
    ImageView ivPlus;

    @BindView(R.id.tv_calenderDate)
    CustomTextViewSemiBold tvCalenderDate;

    @BindView(R.id.tv_userName)
    CustomTextViewBold tv_userName;

    @BindView(R.id.providerlabel)
    CustomTextViewMedium tv_labelprovider;

    @BindView(R.id.cv_addNote)
    CardView  cvAddNote;

    @BindView(R.id.cv_attachFile)
    CardView cvAttachFile;

    @BindView(R.id.rescheduleTitle)
    CustomTextViewMedium tv_title;

    @BindView(R.id.tv_term)
    CustomTextViewMedium tv_bookingFor;

    @BindView(R.id.tv_peopleAhead)
    CustomTextViewBold tv_peopleAhead;


    int queueId,serviceId,locationId,accountId;
    String apiDate;
    private CheckInSlotsDialog slotsDialog;
    private RecyclerView rvSlots;
    private LinearLayout llNoSlots,llChangeTo;
    private NeomorphFrameLayout cvCalender;
    QueuesAdapter queuesAdapter;
    private LinearLayout llSeeMoreHint;
    ArrayList<QueueTimeSlotModel> queuesData = new ArrayList<>();
    private ISelectedQueue iSelectedQueue;
    private ISelectQ iSelectQ;
    private Context mContext;
    final Calendar myCalendar = Calendar.getInstance();
    ActiveCheckIn checkInInfo = new ActiveCheckIn();
    private AddNotes addNotes;
    private ISendMessage iSendMessage;
    private String userMessage = "";
    BottomSheetDialog dialog;
    CustomTextViewSemiBold tvErrorMessage;
    TextView tv_attach, tv_camera;
    RecyclerView recycle_image_attachment;
    ArrayList<String> imagePathList = new ArrayList<>();
    ArrayList<String> imagePathLists = new ArrayList<>();
    private int GALLERY = 1, CAMERA = 2;
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    private static final String IMAGE_DIRECTORY = "/Jaldee" +
            "";
    String path;
    File f;
    private Uri mImageUri;
    Bitmap bitmap;
    File file;
    String value,user;
    ActiveCheckIn activeCheckIn = new ActiveCheckIn();
    SearchTerminology mSearchTerminology;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_checkin);
        ButterKnife.bind(RescheduleCheckinActivity.this);
        mContext = this;
        iSelectQ = this;
        iSendMessage = this;
        iSelectQ=this;
        iSelectedQueue = this;

        initializations();

        Intent i = getIntent();
        checkInInfo = (ActiveCheckIn) i.getSerializableExtra("checkinInfo");

        ApiSearchViewTerminology(Integer.parseInt(checkInInfo.getUniqueId()));


        if (checkInInfo != null && checkInInfo.getDate() != null) {

            Date sDate = null;String dtStart =checkInInfo.getDate();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                sDate = format.parse(dtStart);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorow = cal.getTime();
            try {
                tvCalenderDate.setText(getCalenderDateFormat(checkInInfo.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (today.before(sDate)) {
                Config.logV("Date Enabled---------------");
                ivMinus.setEnabled(true);
                ivMinus.setColorFilter(ContextCompat.getColor(RescheduleCheckinActivity.this, R.color.location_theme), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {
                Config.logV("Date Disabled---------------");
                ivMinus.setEnabled(false);
                ivMinus.setColorFilter(ContextCompat.getColor(RescheduleCheckinActivity.this, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }

        if (checkInInfo != null) {

            if(checkInInfo.getShowToken()!=null && checkInInfo.getShowToken().equalsIgnoreCase("true")){
                tv_title.setText("Reschedule a token");
                tv_bookingFor.setText("Token for");
            }
            else{
                tv_title.setText("Reschedule a checkin");
                tv_bookingFor.setText("Checkin for");
            }

            String name = checkInInfo.getBusinessName();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

            tvSpName.setText(name);

            if (checkInInfo.getProvider() != null) {
                String username = "";
                if (checkInInfo.getProvider().getBusinessName() != null) {
                    username = checkInInfo.getProvider().getBusinessName();
                } else {
                    username = checkInInfo.getProvider().getFirstName() + " " + checkInInfo.getProvider().getLastName();
                }
                username = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
                tv_userName.setText(username);
                tv_userName.setVisibility(View.VISIBLE);
                tvSpName.setTextSize(16);
                userId = checkInInfo.getProvider().getId();
            }
            else {
                userId = checkInInfo.getId();
            }


            try {
                if (checkInInfo.getLocation().getGoogleMapUrl() != null) {
                    String geoUri = checkInInfo.getLocation().getGoogleMapUrl();
                    if (geoUri != null) {

                        tvLocationName.setVisibility(View.VISIBLE);
                        tvLocationName.setText(checkInInfo.getLocation().getPlace());
                    } else {
                        tvLocationName.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvLocationName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String geoUri = checkInInfo.getLocation().getGoogleMapUrl();
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // to set service name
            if (checkInInfo.getService() != null) {

                if (checkInInfo.getService().getName() != null) {
                    String sName = checkInInfo.getService().getName();
                    sName = sName.substring(0, 1).toUpperCase() + sName.substring(1).toLowerCase();
                    tvServiceName.setText(sName);
                }

                try {
                    if (checkInInfo.getService().getServiceType() != null) {
                        if (checkInInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {
                            ivteleService.setVisibility(View.VISIBLE);
                            if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                ivteleService.setImageResource(R.drawable.zoom);
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                ivteleService.setImageResource(R.drawable.googlemeet);
                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                ivteleService.setImageResource(R.drawable.whatsapp_icon);

                            } else if (checkInInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                ivteleService.setImageResource(R.drawable.phone_icon);
                            }
                        } else {
                            ivteleService.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (checkInInfo.getDate() != null && checkInInfo.getService() != null && checkInInfo.getLocation() != null) {

                apiDate = checkInInfo.getDate();
                serviceId = checkInInfo.getService().getId();
                locationId = checkInInfo.getLocation().getId();
                accountId = checkInInfo.getId();
                // api call to get slots on default date (checkin date)
                ApiQueueTimeSlot(String.valueOf(locationId),String.valueOf(serviceId),String.valueOf(accountId),apiDate);

            }

            if (checkInInfo.getFirstName() != null && checkInInfo.getLastName()!=null) {
                String cName = Config.toTitleCase(checkInInfo.getFirstName()) + " " + Config.toTitleCase(checkInInfo.getLastName());
                tvConsumerName.setText(cName);
                if (checkInInfo.getPhoneNo() != null) {
                    tvNumber.setVisibility(View.VISIBLE);
                    tvNumber.setText(checkInInfo.getPhoneNo());
                } else {
                    tvNumber.setVisibility(View.GONE);
                }
                if (checkInInfo.getEmail() != null) {
                    tvEmail.setVisibility(View.VISIBLE);
                    tvEmail.setText(checkInInfo.getEmail());
                } else {
                    tvEmail.setVisibility(View.GONE);
                }
            }

            if (checkInInfo.getDate() != null && checkInInfo.getCheckInTime() != null) {
                String oldDate = convertDate(checkInInfo.getDate());
                String time = checkInInfo.getCheckInTime().split("-")[0];
                String oldtime = convertTime(time);
                tvActualTime.setText(oldDate + ", " + oldtime);
            }

        }

        if(checkInInfo.getConsumerNote()!=null){
            userMessage = checkInInfo.getConsumerNote();
        }

        // click actions

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateSelectedDate(year, monthOfYear, dayOfMonth);
            }
        };

        cvCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog da = new DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                da.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                da.show();
            }
        });

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        tvChangeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (checkInInfo.getDate() != null && checkInInfo.getService() != null && checkInInfo.getLocation() != null ) {
                        slotsDialog = new CheckInSlotsDialog(RescheduleCheckinActivity.this, checkInInfo.getService().getId(), checkInInfo.getLocation().getId(), iSelectQ, checkInInfo.getId(),checkInInfo.getDate());
                        slotsDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                        slotsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        slotsDialog.show();
                        DisplayMetrics metrics = RescheduleCheckinActivity.this.getResources().getDisplayMetrics();
                        int width = (int) (metrics.widthPixels * 1);
                        slotsDialog.setCancelable(false);
                        slotsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        if(checkInInfo.getProvider()!=null){
            user = tv_userName.getText().toString();
        }
        else{
            user = tvSpName.getText().toString();
        }
        cvAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addNotes = new AddNotes(mContext, user, iSendMessage, userMessage);
                addNotes.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                addNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addNotes.show();
                addNotes.setCancelable(false);
                DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();
                int width = (int) (metrics.widthPixels * 1);
                addNotes.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

        cvAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
                dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                dialog.setContentView(R.layout.files_layout);
                dialog.show();

                final Button btn_send = dialog.findViewById(R.id.btn_send);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
                btn_send.setText("Save");
                Typeface font_style = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
                btn_cancel.setTypeface(font_style);
                btn_send.setTypeface(font_style);
                tvErrorMessage = dialog.findViewById(R.id.tv_errorMessage);
                tv_attach = dialog.findViewById(R.id.btn);
                tv_camera = dialog.findViewById(R.id.camera);
                recycle_image_attachment = dialog.findViewById(R.id.recycler_view_image);

                if (imagePathList != null && imagePathList.size() > 0) {
                    DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(view.getContext(), 3);
                    recycle_image_attachment.setLayoutManager(mLayoutManager);
                    recycle_image_attachment.setAdapter(mDetailFileAdapter);
                    mDetailFileAdapter.notifyDataSetChanged();
                }


                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (imagePathList != null && imagePathList.size() > 0) {

                            tvErrorMessage.setVisibility(View.GONE);
                            imagePathLists = imagePathList;
                            dialog.dismiss();
                        } else {

                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }

                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imagePathList != null && imagePathLists != null) {
                            imagePathLists.clear();
                            imagePathList.clear();
                        }
                        dialog.dismiss();
                    }
                });


                requestMultiplePermissions();
                tv_attach.setVisibility(View.VISIBLE);
                tv_camera.setVisibility(View.VISIBLE);


                tv_attach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imagePathLists.size() > 0) {
                            DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathLists, mContext);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(v.getContext(), 3);
                            recycle_image_attachment.setLayoutManager(mLayoutManager);
                            recycle_image_attachment.setAdapter(mDetailFileAdapter);
                            mDetailFileAdapter.notifyDataSetChanged();
                        }
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

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (imagePathList != null && imagePathList.size() > 0) {

                            tvErrorMessage.setVisibility(View.GONE);
                            imagePathLists = imagePathList;
                            dialog.dismiss();
                        } else {

                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }
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


        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dtStart = tvCalenderDate.getText().toString();
                Config.logV("Date----------------" + dtStart);
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
                try {
                    date = format.parse(dtStart);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date added_date = addDays(date, 1);
                DateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
                //to convert Date to String, use format method of SimpleDateFormat class.
                String strDate = dateFormat.format(added_date);
                tvCalenderDate.setText(strDate);
                DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
                apiDate = selecteddateParse.format(added_date);
                UpdateDAte(apiDate);
            }
        });

        ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dtStart = tvCalenderDate.getText().toString();
                Config.logV("Date----------------" + dtStart);
                Date date = null;
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
                try {
                    date = format.parse(dtStart);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date added_date = subtractDays(date, 1);
                DateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
                //to convert Date to String, use format method of SimpleDateFormat class.
                String strDate = dateFormat.format(added_date);
                tvCalenderDate.setText(strDate);
                DateFormat selecteddateParse = new SimpleDateFormat("yyyy-MM-dd");
                apiDate = selecteddateParse.format(added_date);
                UpdateDAte(apiDate);
            }
        });


        cvSubmit.setClickable(false);
        cvSubmit.setEnabled(false);
        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reScheduleCheckin(checkInInfo.getId());
            }
        });
    }

    private void initializations() {

        rvSlots = findViewById(R.id.rv_slots);
        llSeeMoreHint = findViewById(R.id.ll_seeMoreHint);
        llNoSlots = findViewById(R.id.ll_noSlots);
        cvCalender = findViewById(R.id.fl_calender);
        llChangeTo = findViewById(R.id.ll_changeTo);
    }

    private void updateSelectedDate(int year, int monthOfYear, int dayOfMonth) {

        try {

            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE");
            Date date = new Date(year, monthOfYear, dayOfMonth - 1);
            String dayOfWeek = simpledateformat.format(date);

            String sMonth = "";
            if (monthOfYear < 9) {
                sMonth = "0" + String.valueOf(monthOfYear + 1);
            } else {
                sMonth = String.valueOf(monthOfYear + 1);
            }

            String mDate = dayOfWeek + ", " + dayOfMonth +
                    "/" + (sMonth) +
                    "/" + year;
            tvCalenderDate.setText(mDate);

            String apiFormat = "yyyy-MM-dd"; // your format
            SimpleDateFormat apiSdf = new SimpleDateFormat(apiFormat);
            String pickedDate = apiSdf.format(myCalendar.getTime());
            tvDate.setText(getCustomDateString(pickedDate));
            UpdateDAte(mDate);
            apiDate = pickedDate;
            ApiQueueTimeSlot(String.valueOf(locationId),String.valueOf(serviceId),String.valueOf(accountId),pickedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String UpdateDAte(String sDate) {
        Date selecteddate = null;
        String dtStart = tvCalenderDate.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        try {
            selecteddate = format.parse(dtStart);
            //  System.out.println(selecteddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ApiQueueTimeSlot(String.valueOf(locationId),String.valueOf(serviceId),String.valueOf(accountId),sDate);

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorow = cal.getTime();
        if (today.before(selecteddate)) {
            Config.logV("Date Enabled---------------");
            ivMinus.setEnabled(true);
            ivMinus.setColorFilter(ContextCompat.getColor(mContext, R.color.location_theme), android.graphics.PorterDuff.Mode.SRC_IN);

        } else {
            Config.logV("Date Disabled---------------");
            ivMinus.setEnabled(false);
            ivMinus.setColorFilter(ContextCompat.getColor(mContext, R.color.light_gray), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        return "";
    }

    private void reScheduleCheckin(int id) {

        ApiInterface apiService =
                ApiClient.getClient(RescheduleCheckinActivity.this).create(ApiInterface.class);

        JSONObject body = new JSONObject();
        try {
            body.put("ynwUuid",checkInInfo.getYnwUuid());
            body.put("date",apiDate);
            body.put("queue",queueId);
            body.put("consumerNote",userMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog mDialog = Config.getProgressDialog(RescheduleCheckinActivity.this, RescheduleCheckinActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), body.toString());
        Call<ResponseBody> call = apiService.reScheduleCheckin(id,requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        if(checkInInfo.getShowToken()!=null && checkInInfo.getShowToken().equalsIgnoreCase("true")){
                            Toast.makeText(RescheduleCheckinActivity.this, "Token rescheduled successfully", Toast.LENGTH_SHORT).show();
                        }
                        else {
                             Toast.makeText(RescheduleCheckinActivity.this, "Checkin rescheduled successfully", Toast.LENGTH_SHORT).show();
                        }

                        if (imagePathList.size() > 0) {
                            if(checkInInfo.getYnwUuid()!=null) {
                                ApiCommunicateCheckin(checkInInfo.getYnwUuid(), String.valueOf(id), userMessage, dialog);
                            }
                        }
                        else{
                            getConfirmationDetails(id);
                        }


                    } else if (response.code() == 422){


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


                      //  Toast.makeText(mContext, sb.toString(), Toast.LENGTH_LONG).show();

                        DynamicToast.make(RescheduleCheckinActivity.this, sb.toString(), AppCompatResources.getDrawable(
                                RescheduleCheckinActivity.this, R.drawable.ic_info_black),
                                ContextCompat.getColor(RescheduleCheckinActivity.this, R.color.white), ContextCompat.getColor(RescheduleCheckinActivity.this, R.color.red), Toast.LENGTH_SHORT).show();

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

    public static String getCalenderDateFormat(String d) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = format.parse(d);
        format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        String yourDate = format.format(date1);

        return yourDate;

    }


    public static String convertDate(String date) {

        String finalDate = "";
        Date selectedDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            selectedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (DateUtils.isToday(selectedDate.getTime())) {
            finalDate = "Today";
        } else {
            Format f = new SimpleDateFormat("MMM dd");
            finalDate = f.format(selectedDate);
        }

        return finalDate;
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

    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    public Date subtractDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    @Override
    public void getMessage(String message) {
        if (message != null) {
            userMessage = message;
        }


    }
    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
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
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
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
                            Toast.makeText(getApplicationContext(), "You Denied the Permission", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //   mTxvBuy.setEnabled(true);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {


            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    showAlert("Payment Successful");
                    finish();
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    showAlert("Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    showAlert("Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
            } else {
                //  Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
            showAlert("Payment Cancelled");
        }
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

                        imagePathList.add(orgFilePath);

                        DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();

                    }
                    else if (data.getClipData() != null) {
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
                            imagePathList.add(orgFilePath);
                        }
                        DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //      imageview.setImageBitmap(bitmap);
                path = saveImage(bitmap);
                // imagePathList.add(bitmap.toString());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//            String paths = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "Pic from camera", null);
                if (path != null) {
                    mImageUri = Uri.parse(path);
                    imagePathList.add(mImageUri.toString());
                }
                try {
                    bytes.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                recycle_image_attachment.setLayoutManager(mLayoutManager);
                recycle_image_attachment.setAdapter(mDetailFileAdapter);
                mDetailFileAdapter.notifyDataSetChanged();
            }
        }
    }
    private void ApiCommunicateCheckin(String waitListId, String accountID, String message,
                                           final BottomSheetDialog dialog) {


        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < imagePathList.size(); i++) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(imagePathList.get(i))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            }
            else {
                file = new File(imagePathList.get(i));
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

        Call<ResponseBody> call = apiService.WaitListMessage(waitListId, String.valueOf(accountID.split("-")[0]), requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {


                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        imagePathList.clear();
                        //  dialog.dismiss();
                        getConfirmationDetails(Integer.parseInt(accountID));


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
                ApiClient.getClient(RescheduleCheckinActivity.this).create(ApiInterface.class);
        Call<ActiveCheckIn> call = apiService.getActiveCheckInUUID(checkInInfo.getYnwUuid(), String.valueOf(userId));
        call.enqueue(new Callback<ActiveCheckIn>() {
            @Override
            public void onResponse(Call<ActiveCheckIn> call, Response<ActiveCheckIn> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeCheckIn = response.body();
                        if (activeCheckIn != null) {
                            Bundle b = new Bundle();
                            b.putSerializable("BookingDetails", activeCheckIn);
                            b.putString("terminology", mSearchTerminology.getProvider());
                            b.putString("from","Reschedule");
                            Intent checkin = new Intent(RescheduleCheckinActivity.this, CheckInConfirmation.class);
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
            public void onFailure(Call<ActiveCheckIn> call, Throwable t) {
            }
        });

    }
    private void ApiSearchViewTerminology(int muniqueID) {


        ApiInterface apiService =
                ApiClient.getClientS3Cloud(RescheduleCheckinActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(RescheduleCheckinActivity.this, RescheduleCheckinActivity.this.getResources().getString(R.string.dialog_log_in));
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

    private void ApiQueueTimeSlot(String locationId, String subSeriveID, String accountID, String mDate) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<QueueTimeSlotModel>> call = apiService.getQueueTimeSlot(locationId, subSeriveID, mDate, accountID);
        call.enqueue(new Callback<ArrayList<QueueTimeSlotModel>>() {
            @Override
            public void onResponse(Call<ArrayList<QueueTimeSlotModel>> call, Response<ArrayList<QueueTimeSlotModel>> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog((Activity) mContext, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    Config.logV("mQueueTimeSlotList--------11111-----------------" + response.code());
                    if (response.code() == 200) {
                       queuesData = response.body();

                        if (queuesData != null) {
                            if (queuesData.size() > 0) {
                                rvSlots.setVisibility(View.VISIBLE);
                                llChangeTo.setVisibility(View.VISIBLE);
                                llNoSlots.setVisibility(View.GONE);
                                tvDate.setVisibility(View.VISIBLE);
                                tvTime.setVisibility(View.VISIBLE);
                                cvSubmit.setClickable(true);
                                cvSubmit.setEnabled(true);
                                cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.location_theme));
                                if (queuesData.size()>4){
                                    llSeeMoreHint.setVisibility(View.VISIBLE);
                                }
                                else {
                                    llSeeMoreHint.setVisibility(View.GONE);
                                }
                                queueId = queuesData.get(0).getId();
                                tv_peopleAhead.setText(String.valueOf(queuesData.get(0).getQueueSize()));
                                tvTime.setText(queuesData.get(0).getQueueSchedule().getTimeSlots().get(0).getsTime());
                                tvDate.setText(getCustomDateString(queuesData.get(0).getEffectiveSchedule().getStartDate()));
                                tvCalenderDate.setText(getCalenderDateFormat(queuesData.get(0).getEffectiveSchedule().getStartDate()));
                                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
                                rvSlots.setLayoutManager(mLayoutManager);
                                queuesAdapter = new QueuesAdapter(mContext, queuesData, iSelectedQueue);
                                rvSlots.setAdapter(queuesAdapter);
                            } else {

                                rvSlots.setVisibility(View.GONE);
                                llNoSlots.setVisibility(View.VISIBLE);
                                llSeeMoreHint.setVisibility(View.GONE);
                                tvDate.setVisibility(View.GONE);
                                tvTime.setVisibility(View.GONE);
                                llChangeTo.setVisibility(View.GONE);
                                cvSubmit.setClickable(false);
                                cvSubmit.setEnabled(false);
                                cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.inactive_text));

                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<QueueTimeSlotModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog((Activity) mContext, mDialog);
            }
        });
    }

    @Override
    public void sendSelectedQueueInfo(String displayTime, int queueId, QueueTimeSlotModel queueDetails, String selectedDate) {


    }

    @Override
    public void sendSelectedQueue(String displayQueueTime, QueueTimeSlotModel queue, int id) {
        tv_peopleAhead.setText(String.valueOf(queue.getQueueSize()));
        tvTime.setText(displayQueueTime.split("-")[0]);
        queueId = id;
        cvSubmit.setClickable(true);
        cvSubmit.setEnabled(true);
        cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.location_theme));

    }
}