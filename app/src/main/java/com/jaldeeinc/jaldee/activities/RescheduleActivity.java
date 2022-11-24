package com.jaldeeinc.jaldee.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.jaldeeinc.jaldee.Interface.ISelectSlotInterface;
import com.jaldeeinc.jaldee.Interface.ISendMessage;
import com.jaldeeinc.jaldee.Interface.ISlotInfo;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailFileImageAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.AddNotes;
import com.jaldeeinc.jaldee.custom.SlotsDialog;
import com.jaldeeinc.jaldee.model.FileAttachment;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.jaldeeinc.jaldee.model.PriceList;
import com.jaldeeinc.jaldee.model.SelectedSlotDetail;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.Provider;
import com.jaldeeinc.jaldee.response.SearchTerminology;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RescheduleActivity extends AppCompatActivity implements ISlotInfo, ISelectSlotInterface, ISendMessage {

    @BindView(R.id.toolbartitle)
    TextView toolbartitle;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.ll_slots)
    LinearLayout ll_slots;
    @BindView(R.id.tv_spName)
    TextView tvSpName;
    @BindView(R.id.tv_locationName)
    TextView tvLocationName;
    @BindView(R.id.tv_serviceName)
    TextView tvServiceName;
    @BindView(R.id.iv_teleService)
    ImageView ivteleService;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_consumerName)
    TextView tvConsumerName;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_time1)
    TextView tvTime1;
    @BindView(R.id.tv_actualTime)
    TextView tvActualTime;
    @BindView(R.id.cv_submit)
    CardView cvSubmit;
    @BindView(R.id.cv_cancel)
    CardView cvCancel;
    @BindView(R.id.tv_userName)
    TextView tv_userName;
    @BindView(R.id.cv_addNote)
    LinearLayout cvAddNote;
    @BindView(R.id.cv_attachFile)
    LinearLayout cvAttachFile;
    @BindView(R.id.attach_file_size)
    TextView tvAttachFileSize;
    @BindView(R.id.tv_addNote)
    TextView tvAddNotes;
    @BindView(R.id.tv_balance_info)
    TextView tv_balance_info;
    @BindView(R.id.iv_location_icon)
    ImageView iv_location_icon;
    @BindView(R.id.iv_prvdr_phone_icon)
    ImageView iv_prvdr_phone_icon;
    @BindView(R.id.iv_prvdr_email_icon)
    ImageView iv_prvdr_email_icon;
    @BindView(R.id.icon_text)
    TextView icon_text;
    @BindView(R.id.ll_booking1)
    LinearLayout ll_booking1;
    @BindView(R.id.ll_booking2)
    LinearLayout ll_booking2;

    int currentScheduleId, scheduleId, serviceId, locationId, accountId;
    String slotTime, apiDate;
    private ISlotInfo iSlotInfo;
    private Context mContext;
    ActiveAppointment appointmentInfo = new ActiveAppointment();
    private AddNotes addNotes;
    private ISendMessage iSendMessage;
    private String userMessage = "";
    BottomSheetDialog dialog;
    TextView tvErrorMessage;
    TextView tv_attach, tv_camera;
    RecyclerView recycle_image_attachment;
    ArrayList<String> imagePathList = new ArrayList<>();
    ArrayList<String> s3ImgPathList = new ArrayList<>();
    ArrayList<String> imagePathLists = new ArrayList<>();
    private int GALLERY = 1, CAMERA = 2;
    private Uri mImageUri;
    private ArrayList<PriceList> priceLists = new ArrayList<PriceList>();

    File file;
    String user;
    ActiveAppointment activeAppointment = new ActiveAppointment();
    SearchTerminology mSearchTerminology;
    int userId;
    String uniqueId;
    String ynwuuid;
    boolean isPriceDynamic;
    boolean showOnlyAvailableSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule);
        ButterKnife.bind(RescheduleActivity.this);
        mContext = this;
        iSendMessage = this;

        iSlotInfo = this;

        Intent i = getIntent();
        uniqueId = i.getStringExtra("uniqueId");
        ynwuuid = i.getStringExtra("ynwuuid");
        userId = i.getIntExtra("providerId", 0);

        if (ynwuuid != null) {
            getApptInfo(ynwuuid, userId);
        }

        if (uniqueId != null) {
            ApiSearchViewTerminology(Integer.parseInt(uniqueId));
        }
        Glide.with(mContext).load(R.drawable.location_icon_1).into(iv_location_icon);
        Glide.with(mContext).load(R.drawable.phone_icon_1).into(iv_prvdr_phone_icon);
        Glide.with(mContext).load(R.drawable.email_icon_1).into(iv_prvdr_email_icon);
        toolbartitle.setText("Reschedule Appointment");
        // click actions

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


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
                            tvAttachFileSize.setText("Attach File" + "(" + imagePathList.size() + ")");
                            dialog.dismiss();
                        } else {
                            tvAttachFileSize.setText("Attach File");
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imagePathList.size() > 0) {
                            tvAttachFileSize.setText("Attach File" + "(" + imagePathList.size() + ")");
                        } else {
                            tvAttachFileSize.setText("Attach File");
                        }
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


        cvSubmit.setClickable(false);
        cvSubmit.setEnabled(false);
        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reScheduleAppointment(appointmentInfo.getProviderAccount().getId());
            }
        });
    }

    private void getApptInfo(String ynwuuid, int id) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(ynwuuid, String.valueOf(id));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        appointmentInfo = response.body();

                        if (appointmentInfo != null) {
                            showOnlyAvailableSlots = appointmentInfo.getService().isShowOnlyAvailableSlots();
                            currentScheduleId = appointmentInfo.getSchedule().getId();
                            isPriceDynamic = appointmentInfo.getService().isPriceDynamic();
                            if (appointmentInfo.getService() != null && appointmentInfo.getService().getConsumerNoteTitle() != null && !appointmentInfo.getService().getConsumerNoteTitle().equalsIgnoreCase("")) {
                                tvAddNotes.setText(appointmentInfo.getService().getConsumerNoteTitle());
                            } else {
                                tvAddNotes.setText("Add Note");
                            }
                            if (appointmentInfo.getAttchment() != null && appointmentInfo.getAttchment().size() > 0) {
                                for (FileAttachment attachment : appointmentInfo.getAttchment()) {
                                    if (attachment != null) {
                                        if (attachment.getThumbPath() != null && !attachment.getThumbPath().isEmpty()) {
                                            imagePathList.add(attachment.getThumbPath());
                                            s3ImgPathList.add(attachment.getThumbPath());
                                        }
                                    }
                                }
                            }
                            if (imagePathList != null && !imagePathList.isEmpty()) {
                                tvAttachFileSize.setText("Attach File" + "(" + imagePathList.size() + ")");
                            } else {
                                tvAttachFileSize.setText("Attach File");
                            }
                        }

                        if (appointmentInfo != null) {

                            String name = appointmentInfo.getProviderAccount().getBusinessName();
                            //name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

                            tvSpName.setText(name);

                            if (appointmentInfo.getProvider() != null) {
                                String username = "";
                                if (appointmentInfo.getProvider().getBusinessName() != null) {
                                    username = appointmentInfo.getProvider().getBusinessName();
                                } else {
                                    username = appointmentInfo.getProvider().getFirstName() + " " + appointmentInfo.getProvider().getLastName();
                                }
                                //username = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
                                tv_userName.setText(username);
                                tv_userName.setVisibility(View.VISIBLE);
                                tvSpName.setTextSize(16);
                                userId = appointmentInfo.getProvider().getId();
                            } else {
                                userId = appointmentInfo.getProviderAccount().getId();
                            }


                            try {
                                if (appointmentInfo.getLocation().getGoogleMapUrl() != null) {
                                    String geoUri = appointmentInfo.getLocation().getGoogleMapUrl();
                                    if (geoUri != null) {

                                        tvLocationName.setVisibility(View.VISIBLE);
                                        tvLocationName.setText(appointmentInfo.getLocation().getPlace());
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

                                    String geoUri = appointmentInfo.getLocation().getGoogleMapUrl();
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            // to set service name
                            if (appointmentInfo.getService() != null) {

                                if (appointmentInfo.getService().getName() != null) {
                                    String sName = appointmentInfo.getService().getName();
                                    sName = sName.substring(0, 1).toUpperCase() + sName.substring(1).toLowerCase();
                                    tvServiceName.setText(sName);
                                }

                                try {
                                    if (appointmentInfo.getService().getServiceType() != null) {
                                        if (appointmentInfo.getService().getServiceType().equalsIgnoreCase("virtualService")) {
                                            ivteleService.setVisibility(View.VISIBLE);
                                            if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("Zoom")) {
                                                ivteleService.setImageResource(R.drawable.zoom);
                                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("GoogleMeet")) {
                                                ivteleService.setImageResource(R.drawable.googlemeet);
                                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("WhatsApp")) {
                                                if (appointmentInfo.getService().getVirtualServiceType() != null && appointmentInfo.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                                    ivteleService.setImageResource(R.drawable.whatsapp_videoicon);
                                                } else {
                                                    ivteleService.setImageResource(R.drawable.whatsapp_icon);
                                                }

                                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("phone")) {
                                                ivteleService.setImageResource(R.drawable.phoneaudioicon);
                                            } else if (appointmentInfo.getService().getVirtualCallingModes().get(0).getCallingMode().equalsIgnoreCase("VideoCall")) {
                                                ivteleService.setImageResource(R.drawable.ic_jaldeevideo);
                                            }
                                        } else {
                                            ivteleService.setVisibility(View.GONE);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            if (appointmentInfo.getAppmtDate() != null && appointmentInfo.getService() != null && appointmentInfo.getLocation() != null && appointmentInfo.getProviderAccount() != null) {

                                apiDate = appointmentInfo.getAppmtDate();
                                serviceId = appointmentInfo.getService().getId();
                                locationId = appointmentInfo.getLocation().getId();
                                accountId = appointmentInfo.getProviderAccount().getId();
                                // api call to get slots on default date (appointment date)
                                SlotsDialog sl;
                                sl = new SlotsDialog(RescheduleActivity.this, serviceId, locationId, iSlotInfo, accountId, apiDate, 1, showOnlyAvailableSlots);
                                ll_slots.addView(sl);
                            }

                            if (appointmentInfo.getAppmtFor() != null) {
                                String cName = Config.toTitleCase(appointmentInfo.getAppmtFor().get(0).getFirstName()) + " " + Config.toTitleCase(appointmentInfo.getAppmtFor().get(0).getLastName());
                                if (cName != null && !cName.trim().isEmpty()) {
                                    icon_text.setText(String.valueOf(cName.trim().charAt(0)));
                                }
                                tvConsumerName.setText(cName);
                                if (appointmentInfo.getPhoneNumber() != null) {
                                    tvNumber.setVisibility(View.VISIBLE);
                                    if (appointmentInfo.getCountryCode() != null) {
                                        tvNumber.setText(appointmentInfo.getCountryCode() + " " + appointmentInfo.getPhoneNumber());
                                    } else {
                                        tvNumber.setText(appointmentInfo.getPhoneNumber());
                                    }
                                } else {
                                    tvNumber.setVisibility(View.GONE);
                                }

                                if (appointmentInfo.getAppmtFor().get(0).getEmail() != null) {
                                    tvEmail.setVisibility(View.VISIBLE);
                                    tvEmail.setText(appointmentInfo.getAppmtFor().get(0).getEmail());
                                } else {
                                    tvEmail.setVisibility(View.GONE);
                                }
                            }

                            if (appointmentInfo.getAppmtDate() != null && appointmentInfo.getAppmtTime() != null) {
                                String oldDate = Config.getCustomDateString(appointmentInfo.getAppmtDate());//convertDate(appointmentInfo.getAppmtDate());
                                String time = appointmentInfo.getAppmtTime().split("-")[0];
                                String oldtime = convertTime(time);
                                tvActualTime.setText(oldDate);
                                tvTime.setText(oldtime);
                            }
                            if (appointmentInfo.getConsumerNote() != null) {
                                userMessage = appointmentInfo.getConsumerNote();
                            }

                            if (appointmentInfo.getProvider() != null) {
                                user = tv_userName.getText().toString();
                            } else {
                                user = tvSpName.getText().toString();
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

    private void reScheduleAppointment(int id) {

        ApiInterface apiService =
                ApiClient.getClient(RescheduleActivity.this).create(ApiInterface.class);

        JSONObject body = new JSONObject();
        try {
            body.put("uid", appointmentInfo.getUid());
            body.put("time", slotTime);
            body.put("date", apiDate);
            body.put("schedule", scheduleId);
            body.put("consumerNote", userMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog mDialog = Config.getProgressDialog(RescheduleActivity.this, RescheduleActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), body.toString());
        Call<ResponseBody> call = apiService.reScheduleAppointment(id, requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        Toast.makeText(RescheduleActivity.this, "Appointment rescheduled successfully", Toast.LENGTH_SHORT).show();
                        imagePathList.removeAll(s3ImgPathList);

                        if (imagePathList.size() > 0) {
                            if (appointmentInfo.getUid() != null) {
                                ApiCommunicateAppointment(appointmentInfo.getUid(), String.valueOf(id), userMessage, dialog);
                            }
                        } else {
                            getConfirmationDetails(id);
                        }


                    } else if (response.code() == 422) {

                        if (response.errorBody() != null) {
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

                            Pattern p3 = Pattern.compile("\\[(.*?)]");

                            Matcher matcher = p3.matcher(errorString);

                            while (matcher.find()) {
                                System.out.println(matcher.group(1));
                                matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
                            }
                            matcher.appendTail(sb);

                            System.out.println("SubString@@@@@@@@@@@@@" + sb.toString());


                            //  Toast.makeText(mContext, sb.toString(), Toast.LENGTH_LONG).show();

                            DynamicToast.make(RescheduleActivity.this, sb.toString(), AppCompatResources.getDrawable(
                                            RescheduleActivity.this, R.drawable.ic_info_black),
                                    ContextCompat.getColor(RescheduleActivity.this, R.color.white), ContextCompat.getColor(RescheduleActivity.this, R.color.red), Toast.LENGTH_SHORT).show();
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

    public static String getApiDateFormat(String d) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        Date date1 = format.parse(d);
        format = new SimpleDateFormat("yyyy-MM-dd");
        String yourDate = format.format(date1);

        return yourDate;

    }

    @Override
    public void sendSelectedTime(String dspTime, String sTime, int schdId) {

        // assigning
        tvTime1.setText(dspTime);
        slotTime = sTime;
        scheduleId = schdId;
        cvSubmit.setClickable(true);
        cvSubmit.setEnabled(true);
        cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.blue5));
        if (isPriceDynamic) {
            getPriceListOfService(serviceId, currentScheduleId, scheduleId);
        }
    }

    @Override
    public void sendSelectedTime(List<SelectedSlotDetail> selectedSlotDetails) {
        if (selectedSlotDetails.size() == 1) {
            // assigning
            tvTime1.setText(selectedSlotDetails.get(0).getDisplayTime());
            slotTime = selectedSlotDetails.get(0).getSlotTime();
            scheduleId = selectedSlotDetails.get(0).getScheduleId();
            cvSubmit.setClickable(true);
            cvSubmit.setEnabled(true);
            cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.blue5));
            if (isPriceDynamic) {
                getPriceListOfService(serviceId, currentScheduleId, scheduleId);
            }
        }
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

                        imagePathList.add(orgFilePath);

                        if (imagePathList.size() > 0) {
                            tvErrorMessage.setVisibility(View.GONE);
                        } else {
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }

                        DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();

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

                            imagePathList.add(orgFilePath);

                            if (imagePathList.size() > 0) {
                                tvErrorMessage.setVisibility(View.GONE);
                            } else {
                                tvErrorMessage.setVisibility(View.VISIBLE);
                            }
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
                    imagePathList.add(mImageUri.toString());

                    if (imagePathList.size() > 0) {
                        tvErrorMessage.setVisibility(View.GONE);
                    } else {
                        tvErrorMessage.setVisibility(View.VISIBLE);
                    }
                }

                DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                recycle_image_attachment.setLayoutManager(mLayoutManager);
                recycle_image_attachment.setAdapter(mDetailFileAdapter);
                mDetailFileAdapter.notifyDataSetChanged();
            }
        }
    }

    private void ApiCommunicateAppointment(String waitListId, String accountID, String message,
                                           final BottomSheetDialog dialog) {


        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaTypeAndExtention type;
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < imagePathList.size(); i++) {

            type = Config.getFileType(imagePathList.get(i));

            file = new File(imagePathList.get(i));

            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type.getMediaTypeWithExtention(), file));
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
                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);
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
                ApiClient.getClient(RescheduleActivity.this).create(ApiInterface.class);
        Call<ActiveAppointment> call = apiService.getActiveAppointmentUUID(appointmentInfo.getUid(), String.valueOf(userId));
        call.enqueue(new Callback<ActiveAppointment>() {
            @Override
            public void onResponse(Call<ActiveAppointment> call, Response<ActiveAppointment> response) {
                try {
                    Config.logV("URL------ACTIVE CHECKIN---------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        activeAppointment = response.body();
                        if (activeAppointment != null) {
                            Intent checkin = new Intent(RescheduleActivity.this, BookingConfirmation.class);
                            if (appointmentInfo.getAppmtFor().get(0).getEmail() != null) {
                                checkin.putExtra("email", appointmentInfo.getAppmtFor().get(0).getEmail());
                            }
                            if (appointmentInfo.getPhoneNumber() != null) {
                                checkin.putExtra("waitlistPhonenumber", appointmentInfo.getPhoneNumber());
                                if (appointmentInfo.getCountryCode() != null) {
                                    checkin.putExtra("waitlistPhonenumberCountryCode", appointmentInfo.getCountryCode());
                                }
                            }
                            checkin.putExtra("terminology", mSearchTerminology.getProvider());
                            checkin.putExtra("from", Constants.RESCHEDULE);
                            checkin.putExtra("typeOfService", Constants.APPOINTMENT);
                            checkin.putExtra("livetrack", activeAppointment.getLivetrack());
                            checkin.putExtra("accountID", String.valueOf(activeAppointment.getProviderAccount().getId()));
                            if (activeAppointment.getUid() != null) {
                                checkin.putExtra("uid", String.valueOf(activeAppointment.getUid()));
                            }
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

    private void ApiSearchViewTerminology(int muniqueID) {


        ApiInterface apiService =
                ApiClient.getClient(RescheduleActivity.this).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(RescheduleActivity.this, RescheduleActivity.this.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println("UTC time: " + sdf.format(currentTime));
        Call<Provider> call = apiService.getTerminologies(muniqueID);

        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider> call, Response<Provider> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    if (response.code() == 200) {

                        Provider providerResponse = new Provider();

                        providerResponse = response.body();

                        if (providerResponse != null) {

                            if (providerResponse.getTerminologies() != null) {
                                mSearchTerminology = new Gson().fromJson(providerResponse.getTerminologies(), SearchTerminology.class);
                            }
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Provider> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(getParent(), mDialog);

            }
        });
    }


    private void getPriceListOfService(int serviceId, int currentScheduleId, int selectedScheduleId) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ArrayList<PriceList>> call = apiService.getPriceListOfService(serviceId);
        call.enqueue(new Callback<ArrayList<PriceList>>() {
            @Override
            public void onResponse(Call<ArrayList<PriceList>> call, Response<ArrayList<PriceList>> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        priceLists = response.body();
                        double oldPrice = 0;
                        double newPrice = 0;
                        double changePrice = 0;
                        double amountDifference = 0;
                        for (PriceList pl : priceLists) {
                            if (pl.getSchedule().getId() == currentScheduleId) {
                                oldPrice = pl.getPrice();
                            }
                            if (pl.getSchedule().getId() == selectedScheduleId) {
                                newPrice = pl.getPrice();
                            }
                        }
                        changePrice = newPrice - oldPrice;
                        amountDifference = appointmentInfo.getAmountDue() + changePrice;
                        if (amountDifference > 0) {
                            tv_balance_info.setVisibility(View.VISIBLE);
                            tv_balance_info.setText("The balance amount for the rescheduled booking will be ₹" + Config.getAmountNoOrTwoDecimalPoints(amountDifference) + ". Contact your provider directly for payment adjustments.");
                        } else if (amountDifference < 0) {
                            tv_balance_info.setVisibility(View.VISIBLE);
                            tv_balance_info.setText("Contact your provider directly for payment adjustments.");
                        } else {
                            tv_balance_info.setVisibility(View.GONE);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PriceList>> call, Throwable t) {
                Config.logV("Fail---------------" + t.toString());
            }
        });
    }

    @Override
    public void sendSlotInfo(List<SelectedSlotDetail> selectedSlotDetails) {
        if (selectedSlotDetails != null) {
            if (selectedSlotDetails.size() == 1) {
                try {
// getting data from dialog
                    String convertedTime = selectedSlotDetails.get(0).getDisplayTime().replace("am", "AM").replace("pm", "PM");
                    tvTime1.setText(convertedTime);
                    tvDate.setText(selectedSlotDetails.get(0).getDate());
                    scheduleId = selectedSlotDetails.get(0).getScheduleId();
                    slotTime = selectedSlotDetails.get(0).getSlotTime();

                    cvSubmit.setClickable(true);
                    cvSubmit.setEnabled(true);
                    cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.blue5));

                    try {
                        apiDate = getApiDateFormat(selectedSlotDetails.get(0).getCalendarDate());  // to convert selected date to api date format
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    ll_booking1.setVisibility(View.VISIBLE);
                    ll_booking2.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {

            ll_booking1.setVisibility(View.GONE);
            ll_booking2.setVisibility(View.VISIBLE);

            cvSubmit.setClickable(false);
            cvSubmit.setEnabled(false);
            cvSubmit.setCardBackgroundColor(getResources().getColor(R.color.inactive_text));
        }
    }
}