package com.jaldeeinc.jaldee.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jaldeeinc.jaldee.activities.CheckoutListActivity;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.ItemsActivity;
import com.jaldeeinc.jaldee.activities.ViewAttachmentActivity;
import com.jaldeeinc.jaldee.adapter.DetailFileImageAdapter;
import com.jaldeeinc.jaldee.adapter.ImagePreviewAdapter;
import com.jaldeeinc.jaldee.adapter.TodayBookingsAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.ActionsDialog;
import com.jaldeeinc.jaldee.custom.CustomNotes;
import com.jaldeeinc.jaldee.custom.CustomTextViewItalicSemiBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.model.Bookings;
import com.jaldeeinc.jaldee.model.ShoppingListModel;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.ShoppingList;
import com.jaldeeinc.jaldee.response.TeleServiceCheckIn;
import com.jaldeeinc.jaldee.response.ViewAttachments;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class MyBookings extends RootFragment implements ISelectedBooking, ISendData, IDeleteImagesInterface, ISaveNotes {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Context mContext;
    private Activity mActivity;
    private CustomTextViewItalicSemiBold tvToday, tvUpcoming;
    private LinearLayout llNoBookingsForToday, llNoBookingsForFuture, llNoBookings, llBookings;
    private RecyclerView rvTodays, rvUpcomings;
    private TodayBookingsAdapter todayBookingsAdapter;
    private LinearLayoutManager linearLayoutManager, futureLayoutManager;
    private ISelectedBooking iSelectedBooking;
    ArrayList<ActiveAppointment> mAppointmentTodayList = new ArrayList<>();
    ArrayList<ActiveAppointment> mAppointmentFutureList = new ArrayList<>();
    ArrayList<Bookings> bookingsList = new ArrayList<>();
    List<ActiveCheckIn> allCheckInsOffline = new ArrayList<>();
    Animation slideUp, slideRight;
    private ActionsDialog actionsDialog;
    boolean hideMoreInfo = false;
    private ISendData iSendData;

    // files related
    ArrayList<String> imagePathList = new ArrayList<>();
    ArrayList<String> imagePathLists = new ArrayList<>();
    Bitmap bitmap;
    File f, file;
    String path, from, from1 = "";
    private LinearLayout llNoHistory;
    private ImageView iv_attach;
    TextView tv_attach, tv_camera;
    private BottomSheetDialog dialog;
    CustomTextViewSemiBold tvErrorMessage;
    RecyclerView recycle_image_attachment;
    private int GALLERY = 1, CAMERA = 2;
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    private static final String IMAGE_DIRECTORY = "/Jaldee" + "";
    private Uri mImageUri;
    ImagePreviewAdapter imagePreviewAdapter;
    private IDeleteImagesInterface iDeleteImagesInterface;
    ArrayList<ShoppingListModel> itemList = new ArrayList<>();
    private CustomNotes customNotes;
    private ISaveNotes iSaveNotes;

    public MyBookings() {
        // Required empty public constructor
    }


    public static MyBookings newInstance(String param1, String param2) {
        MyBookings fragment = new MyBookings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {

        try {
            if (Config.isOnline(mContext)) {
                apiGetAllBookings();
            } else {
                setOfflineBookings();
            }

            updateImages();
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_bookings, container, false);
        mContext = getActivity();
        iSelectedBooking = (ISelectedBooking) this;
        iSendData = (ISendData) this;
        iDeleteImagesInterface = (IDeleteImagesInterface) this;
        iSaveNotes = this;

        Home.doubleBackToExitPressedOnce = false;
        initializations(view);

        linearLayoutManager = new LinearLayoutManager(getContext());
        futureLayoutManager = new LinearLayoutManager(getContext());
        rvTodays.setLayoutManager(linearLayoutManager);
        todayBookingsAdapter = new TodayBookingsAdapter(bookingsList, getContext(), true, iSelectedBooking, hideMoreInfo);
        rvTodays.setAdapter(todayBookingsAdapter);


        return view;
    }


    private void initializations(View view) {

        rvTodays = view.findViewById(R.id.rv_todays);
        rvUpcomings = view.findViewById(R.id.rv_upcoming);
        llBookings = view.findViewById(R.id.ll_bookings);
        llNoBookings = view.findViewById(R.id.ll_noBookings);
        llNoBookingsForFuture = view.findViewById(R.id.ll_noFutureBookings);
        llNoBookingsForToday = view.findViewById(R.id.ll_noTodayBookings);
        tvToday = view.findViewById(R.id.tv_today);
        tvUpcoming = view.findViewById(R.id.tv_upcoming);
        slideUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_in);
        slideRight = AnimationUtils.loadAnimation(mContext, R.anim.slide_up_out);

    }


    private void apiGetAllBookings() {

        try {

            ApiInterface apiService =
                    ApiClient.getClient(getContext()).create(ApiInterface.class);

            List<Observable<?>> requests = new ArrayList<>();
            Map<String, String> appointFilter = new HashMap<String, String>();
            Map<String, String> checkInFilter = new HashMap<String, String>();
            appointFilter.put("apptStatus-neq", "failed,prepaymentPending");
            checkInFilter.put("waitlistStatus-neq", "failed,prepaymentPending");

            // Make a collection of all requests you need to call at once, there can be any number of requests, not only 3. You can have 2 or 5, or 100.
            requests.add(apiService.getAppointments(appointFilter));
            requests.add(apiService.getCheckIns(checkInFilter));
            // Zip all requests with the Function, which will receive the results.
            Observable.zip(requests, new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    // Objects[] is an array of combined results of completed requests
                    ArrayList<ActiveAppointment> appntList = (ArrayList<ActiveAppointment>) objects[0];
                    ArrayList<ActiveCheckIn> checkInList = (ArrayList<ActiveCheckIn>) objects[1];

                    appntList = appntList == null ? new ArrayList<ActiveAppointment>() : appntList;

                    checkInList = checkInList == null ? new ArrayList<ActiveCheckIn>() : checkInList;

                    ArrayList<Bookings> bookings = new ArrayList<Bookings>();

                    DatabaseHandler db = new DatabaseHandler(mContext);
                    db.DeleteMyCheckin("today");
                    db.DeleteMyCheckin("future");
                    db.insertMyCheckinInfo(checkInList);

                    bookingsList.clear();

                    for (ActiveAppointment activeAppointment : appntList) {

                        Bookings bookingInfo = new Bookings();
                        bookingInfo.setBookingId(activeAppointment.getUid());
                        bookingInfo.setBookingType(Constants.APPOINTMENT);
                        bookingInfo.setAppointmentInfo(activeAppointment);
                        bookingInfo.setRescheduled(activeAppointment.isRescheduled());
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
                            if (activeAppointment.getService().getServiceType().equalsIgnoreCase("virtualService")) { //  check if it is a virtual service
                                bookingInfo.setVirtual(true);
                            }

                            if (activeAppointment.getService().getServiceType().equalsIgnoreCase("virtualService") && activeAppointment.getApptStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                                bookingInfo.setBookingStatus(null);
                            } else {
                                bookingInfo.setBookingStatus(activeAppointment.getApptStatus());
                            }
                        }

                        if (activeAppointment.getAppmtDate() != null && activeAppointment.getAppmtTime() != null) { //  to set time and date

                            String date = getCustomDateString(activeAppointment.getAppmtDate());
                            String time = convertTime(activeAppointment.getAppmtTime().split("-")[0]);
                            bookingInfo.setDate(date + " " + time);
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
                        if(activeAppointment.getVideoCallButton() != null){
                            bookingInfo.setVideoCallButton(activeAppointment.getVideoCallButton());
                        }
                        if(activeAppointment.getVideoCallMessage() != null){
                            bookingInfo.setVideoCallMessage(activeAppointment.getVideoCallMessage());
                        }
                        if (activeAppointment.isHasAttachment()){
                            bookingInfo.setHasAttachment(true);
                        }else {
                            bookingInfo.setHasAttachment(false);
                        }

                        bookings.add(bookingInfo);
                    }


                    for (ActiveCheckIn activeCheckIn : checkInList) {

                        Bookings bookingInfo = new Bookings();
                        bookingInfo.setBookingId(activeCheckIn.getYnwUuid());
                        bookingInfo.setRescheduled(activeCheckIn.isRescheduled());

                        if (activeCheckIn.getShowToken().equalsIgnoreCase("true")) {
                            bookingInfo.setBookingType(Constants.TOKEN);
                        } else {
                            bookingInfo.setBookingType(Constants.CHECKIN);
                        }
                        bookingInfo.setCheckInInfo(activeCheckIn);
                        bookingInfo.setWaitingTime(activeCheckIn.getAppxWaitingTime());
                        bookingInfo.setTokenNo(activeCheckIn.getToken());
                        if (activeCheckIn.getCalculationMode() != null) {
                            bookingInfo.setCalculationMode(activeCheckIn.getCalculationMode());
                        }
                        if (activeCheckIn.getServiceTime() != null) {
                            bookingInfo.setServiceTime(activeCheckIn.getServiceTime());
                        }
                        if (activeCheckIn.getProviderAccount() != null) {
                            bookingInfo.setSpName(activeCheckIn.getProviderAccount().getBusinessName());
                        }

                        if (activeCheckIn.getProvider() != null) {  // to get businessName of firstName & lastName
                            if (activeCheckIn.getProvider().getBusinessName() != null) {
                                bookingInfo.setProviderName(activeCheckIn.getProvider().getBusinessName());
                            } else {
                                bookingInfo.setProviderName(activeCheckIn.getProvider().getFirstName() + " " + activeCheckIn.getProvider().getLastName());
                            }
                        }

                        if (activeCheckIn.getService() != null && activeCheckIn.getWaitlistStatus() != null) {
                            bookingInfo.setServiceName(activeCheckIn.getService().getName());
                            if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService")) { //  check if it is a virtual service
                                bookingInfo.setVirtual(true);
                            }

                            if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService") && activeCheckIn.getWaitlistStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                                bookingInfo.setBookingStatus(null);
                            } else {
                                bookingInfo.setBookingStatus(activeCheckIn.getWaitlistStatus());
                            }
                        }

                        if (activeCheckIn.getDate() != null && activeCheckIn.getQueue() != null) {

                            String date = getCustomDateString(activeCheckIn.getDate());
                            String time = activeCheckIn.getQueue().getQueueStartTime() + " - " + activeCheckIn.getQueue().getQueueEndTime();
                            bookingInfo.setDate(date + " " + time);
                            bookingInfo.setBookingOn(activeCheckIn.getDate());
                        }

                        if (activeCheckIn.getService() != null) {

                            if (activeCheckIn.getService().getVirtualServiceType() != null) {

                                if (activeCheckIn.getService().getVirtualServiceType().equalsIgnoreCase("videoService")) {
                                    bookingInfo.setVideoService(true);
                                } else {
                                    bookingInfo.setVideoService(false);
                                }
                            }

                            if (activeCheckIn.getService().getVirtualCallingModes() != null) {

                                bookingInfo.setCallingType(activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode());
                            }
                        }
                        if(activeCheckIn.getVideoCallButton() != null){
                            bookingInfo.setVideoCallButton(activeCheckIn.getVideoCallButton());
                        }
                        if(activeCheckIn.getVideoCallMessage() != null){
                            bookingInfo.setVideoCallMessage(activeCheckIn.getVideoCallMessage());
                        }
                        if (activeCheckIn.isHasAttachment()){
                            bookingInfo.setHasAttachment(true);
                        }else {
                            bookingInfo.setHasAttachment(false);
                        }

                        bookings.add(bookingInfo);
                    }

                    bookingsList.addAll(bookings);
                    //do something with those results and emit new event
                    return bookingsList;
                }
            })
                    // After all requests had been performed the next observer will receive the Object, returned from Function

                    .subscribe(
                            // Will be triggered if all requests will end successfully (4xx and 5xx also are successful requests too)
                            new Consumer<Object>() {
                                @Override
                                public void accept(Object object) throws Exception {
                                    //Do something on successful completion of all requests
                                    ArrayList<Bookings> allBookings = (ArrayList<Bookings>) object;

                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                ArrayList<Bookings> todayBookings = new ArrayList<>();
                                                ArrayList<Bookings> futureBookings = new ArrayList<>();

                                                if (allBookings != null && allBookings.size() > 0) {
                                                    llBookings.setVisibility(View.VISIBLE);
                                                    llNoBookings.setVisibility(View.GONE);
                                                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                                    for (int i = 0; i < allBookings.size(); i++) {

                                                        if (date.equalsIgnoreCase(allBookings.get(i).getBookingOn())) {

                                                            todayBookings.add(allBookings.get(i));
                                                        } else {
                                                            futureBookings.add(allBookings.get(i));
                                                        }
                                                    }

                                                    if (todayBookings.size() > 0) {

                                                        tvToday.setVisibility(View.VISIBLE);
                                                        llNoBookingsForToday.setVisibility(View.GONE);
                                                        rvTodays.setVisibility(View.VISIBLE);
                                                        rvTodays.setLayoutManager(linearLayoutManager);
                                                        todayBookingsAdapter = new TodayBookingsAdapter(todayBookings, getContext(), false, iSelectedBooking, hideMoreInfo);
                                                        rvTodays.setAdapter(todayBookingsAdapter);
                                                    } else {
                                                        tvToday.setVisibility(View.GONE);
                                                        rvTodays.setVisibility(View.GONE);
                                                        llNoBookingsForToday.setVisibility(View.GONE);
                                                    }

                                                    if (futureBookings.size() > 0) {

                                                        tvUpcoming.setVisibility(View.VISIBLE);
                                                        llNoBookingsForFuture.setVisibility(View.GONE);
                                                        rvUpcomings.setVisibility(View.VISIBLE);
                                                        rvUpcomings.setLayoutManager(futureLayoutManager);
                                                        todayBookingsAdapter = new TodayBookingsAdapter(futureBookings, getContext(), false, iSelectedBooking, hideMoreInfo);
                                                        rvUpcomings.setAdapter(todayBookingsAdapter);
                                                    } else {

                                                        tvUpcoming.setVisibility(View.GONE);
                                                        rvUpcomings.setVisibility(View.GONE);
                                                        llNoBookingsForFuture.setVisibility(View.GONE);
                                                    }

                                                } else {

                                                    // hide all
                                                    llNoBookings.setVisibility(View.VISIBLE);
                                                    llBookings.setVisibility(View.GONE);
                                                }

                                            }
                                        });
                                    }
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


    public void setOfflineBookings() {

        try {

            allCheckInsOffline.clear();
            bookingsList.clear();
            DatabaseHandler db = new DatabaseHandler(mContext);
            List<ActiveCheckIn> todayCheckInsOffline = new ArrayList<>();
            List<ActiveCheckIn> futureCheckInsOffline = new ArrayList<>();
            todayCheckInsOffline = db.getMyCheckinList("today");
            futureCheckInsOffline = db.getMyCheckinList("future");
            allCheckInsOffline.addAll(todayCheckInsOffline);
            allCheckInsOffline.addAll(futureCheckInsOffline);
            ArrayList<Bookings> bookings = new ArrayList<Bookings>();
            for (ActiveCheckIn activeCheckIn : allCheckInsOffline) {

                Bookings bookingInfo = new Bookings();
                bookingInfo.setBookingId(activeCheckIn.getYnwUuid());
                if (activeCheckIn.getShowToken() != null && activeCheckIn.getShowToken().equalsIgnoreCase("true")) {
                    bookingInfo.setBookingType(Constants.TOKEN);
                } else {
                    bookingInfo.setBookingType(Constants.CHECKIN);
                }
                bookingInfo.setCheckInInfo(activeCheckIn);
                bookingInfo.setWaitingTime(activeCheckIn.getAppxWaitingTime());
                bookingInfo.setTokenNo(activeCheckIn.getToken());
                if (activeCheckIn.getCalculationMode() != null) {
                    bookingInfo.setCalculationMode(activeCheckIn.getCalculationMode());
                }
                if (activeCheckIn.getServiceTime() != null) {
                    bookingInfo.setServiceTime(activeCheckIn.getServiceTime());
                }
                if (activeCheckIn.getBusinessName() != null) {
                    bookingInfo.setSpName(activeCheckIn.getBusinessName());
                }

                if (activeCheckIn.getProvider() != null) {  // to get businessName of firstName & lastName
                    bookingInfo.setProviderName(activeCheckIn.getProvider().getFirstName() + " " + activeCheckIn.getProvider().getLastName());
                }

                if (activeCheckIn.getName() != null) {
                    bookingInfo.setServiceName(activeCheckIn.getName());
                }

                if (activeCheckIn.getService() != null && activeCheckIn.getWaitlistStatus() != null) {
                    if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService")) { //  check if it is a virtual service
                        bookingInfo.setVirtual(true);
                    }

                    if (activeCheckIn.getService().getServiceType().equalsIgnoreCase("virtualService") && activeCheckIn.getWaitlistStatus().equalsIgnoreCase(Constants.ARRIVED)) {
                        bookingInfo.setBookingStatus(null);
                    } else {
                        bookingInfo.setBookingStatus(activeCheckIn.getWaitlistStatus());
                    }
                }

                if (activeCheckIn.getDate() != null && activeCheckIn.getQueueStartTime() != null && activeCheckIn.getQueueEndTime() != null) {

                    String date = getCustomDateString(activeCheckIn.getDate());
                    String time = activeCheckIn.getQueueStartTime() + " - " + activeCheckIn.getQueueEndTime();
                    bookingInfo.setDate(date + " " + time);
                    bookingInfo.setBookingOn(activeCheckIn.getDate());
                }

                if (activeCheckIn.getService() != null) {

                    if (activeCheckIn.getService().getVirtualCallingModes() != null) {

                        bookingInfo.setCallingType(activeCheckIn.getService().getVirtualCallingModes().get(0).getCallingMode());
                    }
                }
                if (activeCheckIn.getVideoCallMessage() != null) {
                    bookingInfo.setVideoCallMessage(activeCheckIn.getVideoCallMessage());
                }
                if (activeCheckIn.getVideoCallButton() != null) {
                    bookingInfo.setVideoCallButton(activeCheckIn.getVideoCallButton());
                }

                bookingInfo.setHasAttachment(false); // for temporary
                bookings.add(bookingInfo);
            }

            bookingsList.addAll(bookings);

            setOfflineBookingsToAdapter(bookingsList);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void setOfflineBookingsToAdapter(ArrayList<Bookings> offlineBookingsList) {

        ArrayList<Bookings> todayBookings = new ArrayList<>();
        ArrayList<Bookings> futureBookings = new ArrayList<>();
        if (offlineBookingsList != null && offlineBookingsList.size() > 0) {
            llBookings.setVisibility(View.VISIBLE);
            llNoBookings.setVisibility(View.GONE);
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            for (int i = 0; i < offlineBookingsList.size(); i++) {

                if (date.equalsIgnoreCase(offlineBookingsList.get(i).getBookingOn())) {

                    todayBookings.add(offlineBookingsList.get(i));
                } else {
                    futureBookings.add(offlineBookingsList.get(i));
                }
            }

            if (todayBookings.size() > 0) {

                llNoBookingsForToday.setVisibility(View.GONE);
                rvTodays.setVisibility(View.VISIBLE);
                rvTodays.setLayoutManager(linearLayoutManager);
                todayBookingsAdapter = new TodayBookingsAdapter(todayBookings, getContext(), false, iSelectedBooking, hideMoreInfo);
                rvTodays.setAdapter(todayBookingsAdapter);
            } else {
                rvTodays.setVisibility(View.GONE);
                llNoBookingsForToday.setVisibility(View.GONE);
            }

            if (futureBookings.size() > 0) {

                llNoBookingsForFuture.setVisibility(View.GONE);
                rvUpcomings.setVisibility(View.VISIBLE);
                rvUpcomings.setLayoutManager(futureLayoutManager);
                todayBookingsAdapter = new TodayBookingsAdapter(futureBookings, getContext(), false, iSelectedBooking, hideMoreInfo);
                rvUpcomings.setAdapter(todayBookingsAdapter);
            } else {

                rvUpcomings.setVisibility(View.GONE);
                llNoBookingsForFuture.setVisibility(View.GONE);
            }

        } else {

            // hide all
            llNoBookings.setVisibility(View.VISIBLE);
            llBookings.setVisibility(View.GONE);

        }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void sendBookingInfo(Bookings bookings) {

        if (bookings != null) {

            if (bookings.getBookingType().equalsIgnoreCase(Constants.APPOINTMENT)) {

                Intent intent = new Intent(mContext, BookingDetails.class);
                intent.putExtra("bookingInfo", bookings);
                if (bookings.getBookingStatus() != null) {
                    if (!bookings.getBookingStatus().equalsIgnoreCase("Cancelled") && !bookings.getBookingStatus().equalsIgnoreCase("Completed")) {
                        intent.putExtra("isActive", true);
                    } else {
                        intent.putExtra("isActive", false);
                    }
                }
                startActivity(intent);
            } else if (bookings.getBookingType().equalsIgnoreCase(Constants.CHECKIN) || bookings.getBookingType().equalsIgnoreCase(Constants.TOKEN)) {

                Intent intent = new Intent(mContext, CheckInDetails.class);
                intent.putExtra("bookingInfo", bookings);
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

    @Override
    public void sendSelectedBookingActions(Bookings bookings) {

        boolean isActive = false;
        if (bookings != null && bookings.getBookingStatus() != null) {
            if (!bookings.getBookingStatus().equalsIgnoreCase("Cancelled") && !bookings.getBookingStatus().equalsIgnoreCase("done") && !bookings.getBookingStatus().equalsIgnoreCase("Completed")) {
                isActive = true;
            }
        } else {
            if (bookings != null && bookings.isVirtual()){
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
        imagePathLists.clear();
        itemList.clear();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imagePathList != null && imagePathList.size() > 0) {

                    if (bookings.getCheckInInfo() != null) {
                        sendAttachments(bookings.getCheckInInfo().getProviderAccount().getId(), bookings.getCheckInInfo().getYnwUuid());
                    } else if (bookings.getAppointmentInfo() != null) {
                        sendAppointmentAttachments(bookings.getAppointmentInfo().getProviderAccount().getId(), bookings.getAppointmentInfo().getUid());
                    }
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

                    if (bookings.getCheckInInfo() != null) {

                        sendAttachments(bookings.getCheckInInfo().getProviderAccount().getId(), bookings.getCheckInInfo().getYnwUuid());
                    } else if (bookings.getAppointmentInfo() != null) {
                        sendAppointmentAttachments(bookings.getAppointmentInfo().getProviderAccount().getId(), bookings.getAppointmentInfo().getUid());
                    }
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



    @Override
    public void viewAttachments(Bookings bookings) {


        if (bookings.getCheckInInfo() != null){

            getWaitlistImages(bookings.getCheckInInfo().getYnwUuid(),bookings.getCheckInInfo().getProviderAccount().getId());
        } else {
            getAppointmentImages(bookings.getAppointmentInfo().getUid(),bookings.getAppointmentInfo().getProviderAccount().getId());
        }

    }

    @Override
    public void cancel() {

        apiGetAllBookings();
    }

    private void getAppointmentImages(String uid, int id) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<ShoppingList>> call = apiService.getAppointmentAttachments(uid,id);
        call.enqueue(new Callback<ArrayList<ShoppingList>>() {
            @Override
            public void onResponse(Call<ArrayList<ShoppingList>> call, Response<ArrayList<ShoppingList>> response) {
                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);
                try {

                    if (response.code() == 200) {

                        ArrayList<ShoppingList>  attachments = new ArrayList<>();
                        attachments = response.body();

                        if (attachments != null && attachments.size() > 0){

                            Intent intent = new Intent(mContext, ViewAttachmentActivity.class);
                            intent.putExtra("imagesList",attachments);
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
                    Config.closeDialog(mActivity, mDialog);
            }
        });

    }

    private void getWaitlistImages(String ynwUuid, int id) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        Call<ArrayList<ShoppingList>> call = apiService.getWaitlistAttachments(ynwUuid,id);
        call.enqueue(new Callback<ArrayList<ShoppingList>>() {
            @Override
            public void onResponse(Call<ArrayList<ShoppingList>> call, Response<ArrayList<ShoppingList>> response) {

                if (mDialog.isShowing())
                    Config.closeDialog(mActivity, mDialog);
                try {

                    if (response.code() == 200) {

                        ArrayList<ShoppingList>  attachments = new ArrayList<>();
                        attachments = response.body();

                        if (attachments != null && attachments.size() > 0){

                            Intent intent = new Intent(mContext, ViewAttachmentActivity.class);
                            intent.putExtra("imagesList",attachments);
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
                    Config.closeDialog(mActivity, mDialog);
            }
        });

    }

    private void updateImages() {

        if (itemList != null && itemList.size() > 0) {

            imagePreviewAdapter = new ImagePreviewAdapter(itemList, mContext, true, iDeleteImagesInterface);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
            recycle_image_attachment.setLayoutManager(mLayoutManager);
            recycle_image_attachment.setAdapter(imagePreviewAdapter);
            imagePreviewAdapter.notifyDataSetChanged();
        }
    }

    private void sendAttachments(int accountId, String ynwUuid) {

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < itemList.size(); i++) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(itemList.get(i).getImagePath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            } else {
                file = new File(itemList.get(i).getImagePath());
            }
            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
        }

        Map<String, String> query = new HashMap<>();
        String json = "";

        for (int i = 0; i < itemList.size(); i++) {

            query.put(String.valueOf(i), itemList.get(i).getCaption());

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
                        Config.closeDialog(mActivity, mDialog);

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
                    Config.closeDialog(mActivity, mDialog);
            }
        });


    }

    private void sendAppointmentAttachments(int accountId, String ynwUuid) {

        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < itemList.size(); i++) {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getApplicationContext().getContentResolver(), Uri.fromFile(new File(itemList.get(i).getImagePath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                path = saveImage(bitmap);
                file = new File(path);
            } else {
                file = new File(itemList.get(i).getImagePath());
            }
            mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
        }

        Map<String, String> query = new HashMap<>();
        String json = "";

        for (int i = 0; i < itemList.size(); i++) {

            query.put(String.valueOf(i), itemList.get(i).getCaption());

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
                        Config.closeDialog(mActivity, mDialog);

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
                    Config.closeDialog(mActivity, mDialog);
            }
        });


    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(mActivity)
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
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {


            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    showAlert("Payment Successful");
                    mActivity.finish();
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                    showAlert("Payment Cancelled");
                } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                    showAlert("Payment Failed");
                }

            } else if (resultModel != null && resultModel.getError() != null) {
                Toast.makeText(mContext, "Error check log", Toast.LENGTH_SHORT).show();
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
                        String orgFilePath = getRealPathFromURI(uri, mActivity);
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
                        itemList.add(model);

                        imagePathList.add(orgFilePath);

                        if (imagePathList.size() > 0) {
                            tvErrorMessage.setVisibility(View.GONE);
                        } else {
                            tvErrorMessage.setVisibility(View.VISIBLE);
                        }

                        DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();

                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri imageUri = item.getUri();
                            String orgFilePath = getRealPathFromURI(imageUri, mActivity);
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
                            itemList.add(model);

                            imagePathList.add(orgFilePath);

                            if (imagePathList.size() > 0) {
                                tvErrorMessage.setVisibility(View.GONE);
                            } else {
                                tvErrorMessage.setVisibility(View.VISIBLE);
                            }
                        }
                        DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
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
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                }
//            String paths = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), bitmap, "Pic from camera", null);
                if (path != null) {
                    mImageUri = Uri.parse(path);
                    ShoppingListModel model = new ShoppingListModel();
                    model.setImagePath(mImageUri.toString());
                    itemList.add(model);
                    imagePathList.add(mImageUri.toString());
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
                DetailFileImageAdapter mDetailFileAdapter = new DetailFileImageAdapter(imagePathList, mContext);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
                recycle_image_attachment.setLayoutManager(mLayoutManager);
                recycle_image_attachment.setAdapter(mDetailFileAdapter);
                mDetailFileAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
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

        itemList.remove(position);
        imagePreviewAdapter.notifyDataSetChanged();

        if (itemList != null && itemList.size() > 0) {

            for (int i = 0; i < itemList.size(); i++) {

                if (itemList.get(i).getImagePath().equalsIgnoreCase(imagePath)) {

                    itemList.remove(i);
                }
            }
        }
    }

    @Override
    public void addedNotes(int position) {

        showNotesDialog(position);
    }

    private void showNotesDialog(int position) {

        customNotes = new CustomNotes(mContext, position, iSaveNotes, itemList.get(position).getCaption());
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

        itemList.get(position).setCaption(caption);
        imagePreviewAdapter.notifyDataSetChanged();
    }
}