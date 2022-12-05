package com.jaldeeinc.jaldee.Fragment;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaldeeinc.jaldee.Interface.IDeleteImagesInterface;
import com.jaldeeinc.jaldee.Interface.ISaveNotes;
import com.jaldeeinc.jaldee.Interface.ISelectedBooking;
import com.jaldeeinc.jaldee.Interface.ISendData;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BookingDetails;
import com.jaldeeinc.jaldee.activities.CheckInDetails;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.ReleasedQNRActivity;
import com.jaldeeinc.jaldee.activities.ViewAttachmentActivity;
import com.jaldeeinc.jaldee.adapter.ImagePreviewAdapter;
import com.jaldeeinc.jaldee.adapter.MyPaymentAdapter;
import com.jaldeeinc.jaldee.adapter.TodayBookingsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.ActionsDialog;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.jaldeeinc.jaldee.model.RlsdQnr;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.MyPayments;
import com.jaldeeinc.jaldee.response.ShoppingList;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyRequests extends RootFragment implements ISelectedBooking, ISendData, IDeleteImagesInterface, ISaveNotes {

    List<MyPayments> paymentsList;
    ListView payments_listview;
    String uniqueid;
    private MyPaymentAdapter mAdapter;
    LinearLayout ll_nopayments;
    Context mContext;
    Dialog mDialog;

    ArrayList<ActiveAppointment> appntRequest = new ArrayList<>();
    ArrayList<Bookings> bookings = new ArrayList<Bookings>();
    LinearLayout llNoBookingsForToday;
    private RecyclerView rvTodays;
    private LinearLayoutManager linearLayoutManager;
    private TodayBookingsAdapter todayBookingsAdapter;
    private ISelectedBooking iSelectedBooking;
    boolean hideMoreInfo = false;
    List<RlsdQnr> fReleasedQNR, fReleasedQNR1;
    private ActionsDialog actionsDialog;
    private ISendData iSendData;
    private int GALLERY = 1, CAMERA = 2;
    private BottomSheetDialog dialog;
    TextView tvErrorMessage, tv_attach, tv_camera;
    RecyclerView recycle_image_attachment;
    ArrayList<ShoppingListModel> imagePathList = new ArrayList<>();
    File f, file;
    private Uri mImageUri;
    ImagePreviewAdapter imagePreviewAdapter;
    private CustomNotes customNotes;
    private IDeleteImagesInterface iDeleteImagesInterface;
    private ISaveNotes iSaveNotes;

    public MyRequests() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);
        iSelectedBooking = (ISelectedBooking) this;
        iSendData = (ISendData) this;
        iDeleteImagesInterface = (IDeleteImagesInterface) this;
        iSaveNotes = (ISaveNotes) this;

        mContext = getActivity();
        initializations(view);


        Home.doubleBackToExitPressedOnce = false;
//        mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
//        mDialog.show();

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTodays.setLayoutManager(linearLayoutManager);
        todayBookingsAdapter = new TodayBookingsAdapter(bookings, getContext(), true, iSelectedBooking, hideMoreInfo);
        rvTodays.setAdapter(todayBookingsAdapter);

        return view;
    }

    @Override
    public void onResume() {
        try {
            if (Config.isOnline(mContext)) {
                ApiRequests();
            } else {
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
            updateImages();

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }


    private void ApiRequests() {
        final ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        Call<ArrayList<ActiveAppointment>> call = apiService.getAppointmentRequest();
        call.enqueue(new Callback<ArrayList<ActiveAppointment>>() {

            @Override
            public void onResponse(Call<ArrayList<ActiveAppointment>> call, Response<ArrayList<ActiveAppointment>> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        appntRequest = response.body();
                        bookings.clear();
                        for (ActiveAppointment activeAppointment : appntRequest) {

                            Bookings bookingInfo = new Bookings();
                            bookingInfo.setBookingId(activeAppointment.getUid());
                            bookingInfo.setBookingType(Constants.APPOINTMENT);
                            bookingInfo.setAppointmentInfo(activeAppointment);
                            bookingInfo.setRescheduled(activeAppointment.isRescheduled());
                            if (activeAppointment.getReleasedQnr() != null) {
                                bookingInfo.setReleasedQnr(activeAppointment.getReleasedQnr());
                            }
                            if (activeAppointment.getProviderAccount() != null) {
                                bookingInfo.setSpName(activeAppointment.getProviderAccount().getBusinessName());
                            }
                            if (activeAppointment.getProvider() != null) {  // to get businessName of firstName & lastName
                                if (activeAppointment.getProvider().getBusinessName() != null) {
                                    bookingInfo.setProviderName(activeAppointment.getProvider().getBusinessName());
                                } else {
                                    bookingInfo.setProviderName(activeAppointment.getProvider().getFirstName() + " " + activeAppointment.getProvider().getLastName());
                                }
                            }

                            if (activeAppointment.getService() != null && activeAppointment.getApptStatus() != null) {
                                bookingInfo.setServiceName(activeAppointment.getService().getName());

                                if (activeAppointment.getService().getDeptName() != null) {
                                    bookingInfo.setDeptName(activeAppointment.getService().getDeptName());
                                } else {
                                    bookingInfo.setDeptName(null);
                                }

                                if (activeAppointment.getService().getServiceType().equalsIgnoreCase("virtualService")) { //  check if it is a virtual service
                                    bookingInfo.setVirtual(true);
                                }

                                if (activeAppointment.getService().getServiceType().equalsIgnoreCase("virtualService") && activeAppointment.getApptStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                                    bookingInfo.setBookingStatus(null);
                                } else {
                                    bookingInfo.setBookingStatus(activeAppointment.getApptStatus());
                                }
                            }

                            if (activeAppointment.getAppmtDate() != null) { //  to set time and date

                                String date = getCustomDateString(activeAppointment.getAppmtDate());
                                if (activeAppointment.getAppmtTime() != null) {
                                    String time = convertTime(activeAppointment.getAppmtTime().split("-")[0]);
                                    bookingInfo.setDate(date + " " + time);
                                } else {
                                    bookingInfo.setDate(date);
                                }
                                bookingInfo.setBookingOn(activeAppointment.getAppmtDate()); // to check if it is today's or future's
                            }

                            if (activeAppointment.getService() != null) {

                                if (activeAppointment.getService().getVirtualServiceType() != null) {

                                    if (activeAppointment.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                        bookingInfo.setVideoService(true);
                                    } else {
                                        bookingInfo.setVideoService(false);
                                    }
                                }

                                if (activeAppointment.getService().getVirtualCallingModes() != null) {

                                    bookingInfo.setCallingType(activeAppointment.getService().getVirtualCallingModes().get(0).getCallingMode());
                                }
                            }
                            if (activeAppointment.getVideoCallButton() != null) {
                                bookingInfo.setVideoCallButton(activeAppointment.getVideoCallButton());
                            }
                            if (activeAppointment.getVideoCallMessage() != null) {
                                bookingInfo.setVideoCallMessage(activeAppointment.getVideoCallMessage());
                            }
                            if (activeAppointment.isHasAttachment()) {
                                bookingInfo.setHasAttachment(true);
                            } else {
                                bookingInfo.setHasAttachment(false);
                            }
                            /** booking Type "booking"/"request" **/
                            //bookingInfo.setServiceBookingType(appointmentServiceInfo.getServiceBookingType());
                            bookingInfo.setIsDate(activeAppointment.getService().isDate());
                            bookingInfo.setIsDateTime(activeAppointment.getService().isDateTime());
                            bookingInfo.setIsNoDateTime(activeAppointment.getService().isNoDateTime());
                            /** booking Type "booking"/"request" **/
                            if (activeAppointment.getProviderAccount() != null) {
                                bookingInfo.setUniqueId(activeAppointment.getProviderAccount().getUniqueId());
                            }
                            bookings.add(bookingInfo);
                        }
                        if (bookings.size() > 0) {

                            llNoBookingsForToday.setVisibility(View.GONE);
                            rvTodays.setVisibility(View.VISIBLE);
                            rvTodays.setLayoutManager(linearLayoutManager);
                            todayBookingsAdapter = new TodayBookingsAdapter(bookings, getContext(), false, iSelectedBooking, hideMoreInfo);
                            rvTodays.setAdapter(todayBookingsAdapter);
                        } else {
                            rvTodays.setVisibility(View.GONE);
                            llNoBookingsForToday.setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ActiveAppointment>> call, Throwable t) {

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

    private void initializations(View view) {
        rvTodays = view.findViewById(R.id.rv_todays);
        llNoBookingsForToday = view.findViewById(R.id.ll_noTodayBookings);

    }

    @Override
    public void sendBookingInfo(Bookings bookings) {


        if (bookings != null) {

            if (bookings.getBookingType().equalsIgnoreCase(Constants.APPOINTMENT)) {
                if (bookings.getAppointmentInfo() != null) {
                    if (bookings.getAppointmentInfo().getReleasedQnr() != null) {
                        fReleasedQNR = bookings.getAppointmentInfo().getReleasedQnr().stream()
                                .filter(p -> p.getStatus().equalsIgnoreCase("released")).collect(Collectors.toList());

                        fReleasedQNR1 = bookings.getAppointmentInfo().getReleasedQnr().stream()
                                .filter(p -> !p.getStatus().equalsIgnoreCase("unReleased")).collect(Collectors.toList());
                        bookings.getAppointmentInfo().getReleasedQnr().clear();
                        bookings.getAppointmentInfo().setReleasedQnr((ArrayList<RlsdQnr>) fReleasedQNR); // remove releasedqnr response and add rlsdqnr without "unReleased" status

                    }
                    if (fReleasedQNR != null && !fReleasedQNR.isEmpty() && fReleasedQNR.size() > 0) {
                        Gson gson = new Gson();
                        String myJson = gson.toJson(bookings.getAppointmentInfo());

                        Intent intent = new Intent(mContext, ReleasedQNRActivity.class);
                        intent.putExtra("bookingInfo", myJson);
                        intent.putExtra("from", Constants.BOOKING_APPOINTMENT);
                        mContext.startActivity(intent);

                    } else {
                        Intent intent = new Intent(mContext, BookingDetails.class);
                        intent.putExtra("uid", bookings.getAppointmentInfo().getUid());
                        intent.putExtra("accountId", bookings.getAppointmentInfo().getProviderAccount().getId());
                        if (bookings.getBookingStatus() != null) {
                            if (!bookings.getBookingStatus().equalsIgnoreCase("Cancelled") && !bookings.getBookingStatus().equalsIgnoreCase("Completed")) {
                                intent.putExtra("isActive", true);
                            } else {
                                intent.putExtra("isActive", false);
                            }
                        }
                        startActivity(intent);
                    }
                }
            } else if (bookings.getBookingType().equalsIgnoreCase(Constants.CHECKIN) || bookings.getBookingType().equalsIgnoreCase(Constants.TOKEN)) {
                if (bookings.getCheckInInfo() != null) {

                    if (bookings.getCheckInInfo().getReleasedQnr() != null) {
                        fReleasedQNR = bookings.getCheckInInfo().getReleasedQnr().stream()
                                .filter(p -> p.getStatus().equalsIgnoreCase("released")).collect(Collectors.toList());

                        fReleasedQNR1 = bookings.getCheckInInfo().getReleasedQnr().stream()
                                .filter(p -> !p.getStatus().equalsIgnoreCase("unReleased")).collect(Collectors.toList());
                        bookings.getCheckInInfo().getReleasedQnr().clear();
                        bookings.getCheckInInfo().setReleasedQnr((ArrayList<RlsdQnr>) fReleasedQNR); // remove releasedqnr response and add rlsdqnr without "unReleased" status

                    }
                    if (fReleasedQNR != null && !fReleasedQNR.isEmpty() && fReleasedQNR.size() > 0) {
                        Gson gson = new Gson();
                        String myJson = gson.toJson(bookings.getCheckInInfo());

                        Intent intent = new Intent(mContext, ReleasedQNRActivity.class);
                        intent.putExtra("bookingInfo", myJson);
                        intent.putExtra("from", Constants.BOOKING_CHECKIN);
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, CheckInDetails.class);
                        intent.putExtra("uid", bookings.getCheckInInfo().getYnwUuid());
                        intent.putExtra("accountId", bookings.getCheckInInfo().getProviderAccount().getId());
                        if (bookings.getBookingStatus() != null) {
                            if (!bookings.getBookingStatus().equalsIgnoreCase("Cancelled")) {
                                intent.putExtra("isActive", true);
                            } else {
                                intent.putExtra("isActive", false);
                            }
                        }
                        startActivity(intent);
                    }
                }
            }
        }
    }

    @Override
    public void sendSelectedBookingActions(Bookings bookings) {


        boolean isActive = false;
        if (bookings != null && bookings.getBookingStatus() != null) {
            if (!bookings.getBookingStatus().equalsIgnoreCase("Cancelled") && !bookings.getBookingStatus().equalsIgnoreCase("done") && !bookings.getBookingStatus().equalsIgnoreCase("Completed")) {
                isActive = true;
            }
        } else {
            if (bookings != null && bookings.isVirtual()) {
                isActive = true;
            }
        }
        actionsDialog = new ActionsDialog(mContext, isActive, bookings, iSendData);
        actionsDialog.getWindow().getAttributes().windowAnimations = R.style.slidingUpAndDown;
        actionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionsDialog.show();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 1);
        actionsDialog.getWindow().setGravity(Gravity.BOTTOM);
        actionsDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    @Override
    public void sendAttachments(Bookings bookings) {


        dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        dialog.setContentView(R.layout.files_layout);
        dialog.show();

        final Button btn_send = dialog.findViewById(R.id.btn_send);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_send.setText("Send");
        Typeface font_style = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-SemiBold.ttf");
        btn_cancel.setTypeface(font_style);
        btn_send.setTypeface(font_style);
        tvErrorMessage = dialog.findViewById(R.id.tv_errorMessage);
        tv_attach = dialog.findViewById(R.id.btn);
        tv_camera = dialog.findViewById(R.id.camera);
        recycle_image_attachment = dialog.findViewById(R.id.recycler_view_image);

        imagePathList.clear();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imagePathList != null && imagePathList.size() > 0) {

                    /*if (bookings.getCheckInInfo() != null) {
                        sendAttachments(bookings.getCheckInInfo().getProviderAccount().getId(), bookings.getCheckInInfo().getYnwUuid());
                    } else */if (bookings.getAppointmentInfo() != null) {
                        sendAppointmentAttachments(bookings.getAppointmentInfo().getProviderAccount().getId(), bookings.getAppointmentInfo().getUid());
                    }
                    tvErrorMessage.setVisibility(View.GONE);
                    dialog.dismiss();
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
                dialog.dismiss();
            }
        });


        requestMultiplePermissions();
        tv_attach.setVisibility(View.VISIBLE);
        tv_camera.setVisibility(View.VISIBLE);


        tv_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (imagePathLists.size() > 0) {
                    DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathLists, mContext);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(v.getContext(), 3);
                    recycle_image_attachment.setLayoutManager(mLayoutManager);
                    recycle_image_attachment.setAdapter(mDetailFileAdapter);
                    mDetailFileAdapter.notifyDataSetChanged();
                }*/
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

    @Override
    public void viewAttachments(Bookings bookings) {
//        if (bookings.getCheckInInfo() != null) {
//
//            getWaitlistImages(bookings.getCheckInInfo().getYnwUuid(), bookings.getCheckInInfo().getProviderAccount().getId());
//        } else {
            getAppointmentImages(bookings.getAppointmentInfo().getUid(), bookings.getAppointmentInfo().getProviderAccount().getId());
        //}
    }

    @Override
    public void cancel() {
        ApiRequests();
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
                    Config.closeDialog((Activity) mContext, mDialog);
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
                    Config.closeDialog((Activity) mContext, mDialog);
            }
        });

    }
    private void sendAppointmentAttachments(int accountId, String ynwUuid) {

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
        Call<ResponseBody> call = apiService.appointmentSendAttachments(ynwUuid, accountId, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog((Activity) mContext, mDialog);

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
                    Config.closeDialog((Activity) mContext, mDialog);
            }
        });


    }
    private void requestMultiplePermissions() {
        Dexter.withActivity((Activity) mContext)
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
                            Uri imageUri = item.getUri();
                            String mimeType = mContext.getContentResolver().getType(imageUri);
                            String uriString = imageUri.toString();
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

                                InputStream inputStream = mContext.getContentResolver().openInputStream(imageUri);
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
                                    orgFilePath = Config.getFilePathFromURI(mContext, imageUri, extension);
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
    private void updateImages() {

        if (imagePathList != null && imagePathList.size() > 0) {

            imagePreviewAdapter = new ImagePreviewAdapter(imagePathList, mContext, true, iDeleteImagesInterface);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
            recycle_image_attachment.setLayoutManager(mLayoutManager);
            recycle_image_attachment.setAdapter(imagePreviewAdapter);
            imagePreviewAdapter.notifyDataSetChanged();
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
}