package com.jaldeeinc.jaldee.Fragment;


import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BillActivity;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.PaymentActivity;
import com.jaldeeinc.jaldee.adapter.DetailFileAdapter;
import com.jaldeeinc.jaldee.adapter.ExpandableListAdapter;
import com.jaldeeinc.jaldee.adapter.ExpandableListAdapterAppointment;
import com.jaldeeinc.jaldee.callback.ActiveAdapterOnCallback;
import com.jaldeeinc.jaldee.callback.HistoryAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.ActiveCheckIn;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.payumoney.sdkui.ui.utils.ToastUtils;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab1Fragment extends RootFragment implements HistoryAdapterCallback,ActiveAdapterOnCallback {

    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};

    public Tab1Fragment() {

    }

    boolean firstTimeRating = false;
    Context mContext;
    Activity mActivity;

    ArrayList<ActiveCheckIn> mCheckFutureList = new ArrayList<>();
    ArrayList<ActiveAppointment> mCheckFutureListAppointment = new ArrayList<>();
    ArrayList<ActiveCheckIn> mCheckTodayList = new ArrayList<>();
    ArrayList<ActiveAppointment> mAppointmentTodayList = new ArrayList<>();
    ArrayList<ActiveCheckIn> mCheckTodayFutureList = new ArrayList<>();
    ArrayList<ActiveAppointment> mAppointmentFutureList = new ArrayList<>();

    ArrayList<ActiveCheckIn> mCheckOldList = new ArrayList<>();
    ArrayList<ActiveAppointment> mAppointmentOldList = new ArrayList<>();


    HistoryAdapterCallback mInterface;
    ActiveAdapterOnCallback mCallback;
    ExpandableListView expandlist;
    ExpandableListView expandlistAppointment;

    TextView tv_attach, tv_camera;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int CAMERA = 2;
    private int GALLERY = 1;
    ArrayList<String> fileAttachment;
    String path;
    Bitmap bitmap;
    File f, file;
    RecyclerView recycle_image_attachment;
    RelativeLayout displayImages;
    ArrayList<String> imagePathList = new ArrayList<>();
    private Uri mImageUri;
    String filePath;
    TextView txtCheckins;
    public final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    static double latitude;
    static double longitude;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    public final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.fragment_tab1, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        mInterface = (HistoryAdapterCallback) this;
        mCallback = (ActiveAdapterOnCallback) this;

        Home.doubleBackToExitPressedOnce = false;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //expList = (ExpandableListView) row.findViewById(R.id.exp_list);
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        final TextView txtCheckins = (TextView) row.findViewById(R.id.checkins);
        final TextView txtAppointments = (TextView) row.findViewById(R.id.appointments);
//        final TextView txtDonations = (TextView) row.findViewById(R.id.donations);
//        TextView txtPaylog = (TextView) row.findViewById(R.id.paylog);
        final TextView txtnoappointments = (TextView) row.findViewById(R.id.txtnoappointments);
//        final TextView txtnodonations = (TextView) row.findViewById(R.id.txtnodonations);
//        final TextView txtnopaylog = (TextView) row.findViewById(R.id.txtnopaylog);

        expandlist = (ExpandableListView) row.findViewById(R.id.simple_expandable_listview);
        expandlistAppointment = (ExpandableListView) row.findViewById(R.id.appointmentView);



        LocationManager service = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setMessage("To continue, turn on device location, which uses Google location service");

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alertDialog.setPositiveButton("Turn On GPS", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            alertDialog.show();
        }

        txtCheckins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckinsMyJaldee pfFragment = new CheckinsMyJaldee();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, pfFragment).commit();
            }
        });
        txtAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppointmentMyJaldee afFragment = new AppointmentMyJaldee();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.mainlayout, afFragment).commit();
            }
        });




        if (Config.isOnline(mContext)) {
            ApiFavList();
        } else {
            DatabaseHandler db = new DatabaseHandler(mContext);
            mFavList.clear();
            mFavList = db.getFavouriteID();
        }

        if (Config.isOnline(mContext)) {
            ApiTodayChekInList();

        } else {

            setItems();
        }
        mFutureFlag = false;
        mTodayFlag = false;
        mOldFlag = false;


        if (Config.isOnline(mContext)) {
            ApiTodayAppointmentList();
        }

        return row;
    }




    private void ApiTodayChekInList() {

        Config.logV("API TODAY Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

       /* Map<String, String> query = new HashMap<>();

        query.put("from", "0");
        query.put("count", "10");*/
        Call<ArrayList<ActiveCheckIn>> call = apiService.getActiveCheckIn();


        call.enqueue(new Callback<ArrayList<ActiveCheckIn>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveCheckIn>> call, Response<ArrayList<ActiveCheckIn>> response) {

                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mCheckTodayFutureList.clear();
                        mCheckTodayList.clear();


                        mCheckTodayFutureList = response.body();
                        Log.i("hgfhrty",new Gson().toJson(mCheckTodayFutureList));


                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        for (int i = 0; i < mCheckTodayFutureList.size(); i++) {
                            if (date.equalsIgnoreCase(mCheckTodayFutureList.get(i).getDate())) {
                                mCheckTodayList.add(response.body().get(i));
                                Log.i("hgfhrty22",new Gson().toJson(mCheckTodayList));
                            }

                        }

                        DatabaseHandler db = new DatabaseHandler(mContext);
                        db.DeleteMyCheckin("today");
                        db.insertMyCheckinInfo(mCheckTodayList);
                        ApiFutureChekInList();


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
            public void onFailure(Call<ArrayList<ActiveCheckIn>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }


    private void ApiTodayAppointmentList() {
        Config.logV("API TODAY Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<ActiveAppointment>> call = apiService.getActiveAppointment();
        call.enqueue(new Callback<ArrayList<ActiveAppointment>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveAppointment>> call, Response<ArrayList<ActiveAppointment>> response) {
                try {

                    if (response.code() == 200) {

                        mAppointmentFutureList.clear();
                        mAppointmentTodayList.clear();

                        mAppointmentFutureList = response.body();
                        Log.i("appointment123today",new Gson().toJson(mAppointmentFutureList));

                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        for (int i = 0; i < mAppointmentFutureList.size(); i++) {
                            if (date.equalsIgnoreCase(mAppointmentFutureList.get(i).getAppmtDate())) {
                                mAppointmentTodayList.add(response.body().get(i));
                                Log.i("appointment123456",new Gson().toJson(mAppointmentTodayList));
                            }
                        }
                        ApiFutureAppointmentList();
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
            public void onFailure(Call<ArrayList<ActiveAppointment>> call, Throwable t) {
                // Log error here since request failed

            }
        });


    }


    private void ApiOldChekInList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

       /* Map<String, String> query = new HashMap<>();

        query.put("from", "0");
        query.put("count", "10");*/
        Call<ArrayList<ActiveCheckIn>> call = apiService.getCheckInList();


        call.enqueue(new Callback<ArrayList<ActiveCheckIn>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveCheckIn>> call, Response<ArrayList<ActiveCheckIn>> response) {

                try {


                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mCheckOldList.clear();
                        mCheckOldList = response.body();

                        DatabaseHandler db = new DatabaseHandler(mContext);
                        db.DeleteMyCheckin("old");
                        db.insertMyCheckinInfo(mCheckOldList);

                        Config.logV("mCheckList mCheckOldList size-------------------------" + mCheckOldList.size());


                        setItems();

                    } else {
                        // Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ActiveCheckIn>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }
    private void ApiOldAppointmentList() {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<ActiveAppointment>> call = apiService.getAppointmentList();
        call.enqueue(new Callback<ArrayList<ActiveAppointment>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveAppointment>> call, Response<ArrayList<ActiveAppointment>> response) {

                try {


                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mAppointmentOldList.clear();
                        mAppointmentOldList = response.body();
                        Log.i("appointment123Old",new Gson().toJson(mAppointmentOldList));

                        setItemsAppointment();

                    } else {
                        // Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ActiveAppointment>> call, Throwable t) {
                // Log error here since request failed

            }
        });


    }

    private void ApiFutureChekInList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        Call<ArrayList<ActiveCheckIn>> call = apiService.getFutureCheckInList();


        call.enqueue(new Callback<ArrayList<ActiveCheckIn>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveCheckIn>> call, Response<ArrayList<ActiveCheckIn>> response) {

                try {



                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mCheckFutureList.clear();
                        mCheckFutureList = response.body();

                        DatabaseHandler db = new DatabaseHandler(mContext);
                        db.DeleteMyCheckin("future");
                        db.insertMyCheckinInfo(mCheckFutureList);

                        ApiOldChekInList();


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
            public void onFailure(Call<ArrayList<ActiveCheckIn>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }    private void ApiFutureAppointmentList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<ActiveAppointment>> call = apiService.getFutureAppointmentList();
        call.enqueue(new Callback<ArrayList<ActiveAppointment>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveAppointment>> call, Response<ArrayList<ActiveAppointment>> response) {
                try {


                    if (response.code() == 200) {
                        mCheckFutureListAppointment.clear();
                        mCheckFutureListAppointment = response.body();
                        Log.i("appointment123future",new Gson().toJson(mCheckFutureListAppointment));

                        ApiOldAppointmentList();

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
            public void onFailure(Call<ArrayList<ActiveAppointment>> call, Throwable t) {

            }
        });


    }

    @Override
    public void onMethodMessageCallback(final String ynwuuid, final String accountID, String providerNAme, final String from) {
        imagePathList.clear();
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
        dialog.setContentView(R.layout.reply);
        dialog.show();

        final Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
        final Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);
        String firstWord = "Message to ";
        String secondWord = providerNAme;
        Spannable spannable = new SpannableString(firstWord + secondWord);
        Typeface tyface2 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        spannable.setSpan(new CustomTypefaceSpan("sans-serif", tyface2), firstWord.length(), firstWord.length() + secondWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtsendmsg.setText(spannable);

        tv_attach = dialog.findViewById(R.id.btn);
        tv_camera = dialog.findViewById(R.id.camera);
        recycle_image_attachment = dialog.findViewById(R.id.recycler_view_image);
        //  imageview = dialog.findViewById(R.id.iv);
        // RelativeLayout displayImages = dialog.findViewById(R.id.display_images);


        if (ynwuuid != null) {
            requestMultiplePermissions();
            tv_attach.setVisibility(View.VISIBLE);
            tv_camera.setVisibility(View.VISIBLE);


            tv_attach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if ((ContextCompat.checkSelfPermission(mContext, permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                                requestPermissions(new String[]{
                                        permission.READ_EXTERNAL_STORAGE}, GALLERY);

                                return;
                            } else {
                                Intent intent = new Intent();
                                intent.setType("*/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
                            }
                        } else {

                            Intent intent = new Intent();
                            intent.setType("*/*");
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
                            if (ContextCompat.checkSelfPermission(mContext, permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                                requestPermissions(new String[]{
                                        permission.CAMERA}, CAMERA);

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


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from.equalsIgnoreCase("checkin")){
                    ApiCommunicate(ynwuuid, String.valueOf(accountID), edt_message.getText().toString(), dialog);
                }else{
                    ApiCommunicateAppointment(ynwuuid, String.valueOf(accountID), edt_message.getText().toString(), dialog);
                }



            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePathList.clear();
                dialog.dismiss();
            }
        });

        edt_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty()) {
                    btn_send.setEnabled(true);
                    btn_send.setClickable(true);
                    btn_send.setBackground(mContext.getResources().getDrawable(R.color.blue));
                } else {
                    btn_send.setEnabled(false);
                    btn_send.setClickable(false);
                    btn_send.setBackground(mContext.getResources().getDrawable(R.color.button_grey));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY) {
            if (data != null) {
                try {
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        String orgFilePath = getRealPathFromURI(uri, getActivity());
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

//
                        imagePathList.add(orgFilePath);
//

                        DetailFileAdapter mDetailFileAdapter = new DetailFileAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAMERA) {
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
            DetailFileAdapter mDetailFileAdapter = new DetailFileAdapter(imagePathList, mContext);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
            recycle_image_attachment.setLayoutManager(mLayoutManager);
            recycle_image_attachment.setAdapter(mDetailFileAdapter);
            mDetailFileAdapter.notifyDataSetChanged();

        }
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
        Dexter.withActivity((Activity) mContext)
                .withPermissions(
                        permission.CAMERA,
                        permission.WRITE_EXTERNAL_STORAGE,
                        permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(mContext, "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                            Toast.makeText(mContext, "You Denied the Permissions", Toast.LENGTH_SHORT).show();

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPDFPath(Uri uri) {

        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.getApplicationContext().getContentResolver().query(contentUri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getFilePathFromURI(Uri contentUri, Context context) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(context.getExternalCacheDir() + File.separator + fileName);
            //copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public String getRealFilePath(Uri uri) {
        String path = uri.getPath();
        String[] pathArray = path.split(":");
        String fileName = pathArray[pathArray.length - 1];
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName;
    }


    @Override
    public void onMethodBillIconCallback(String payStatus, String value, String provider, String accountID, String CustomerName) {
        Intent iBill = new Intent(mContext, BillActivity.class);
        iBill.putExtra("ynwUUID", value);
        iBill.putExtra("provider", provider);
        iBill.putExtra("accountID", accountID);
        iBill.putExtra("payStatus", payStatus);
        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
        iBill.putExtra("consumer", CustomerName);
        startActivity(iBill);
    }

    @Override
    public void onMethodDelecteCheckinCallback(final String ynwuuid, final int accountID, boolean todayFlag, boolean futFlag, boolean oldFlag, final String from) {

        mOldFlag = oldFlag;
        mFutureFlag = futFlag;
        mTodayFlag = todayFlag;

        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        dialog.setContentView(R.layout.cancelcheckin);
        dialog.show();

        Button btn_send = (Button) dialog.findViewById(R.id.btn_send);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg = (TextView) dialog.findViewById(R.id.txtsendmsg);



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(from.equalsIgnoreCase("checkin")){
                    ApiDeleteCheckin(ynwuuid, String.valueOf(accountID), dialog);
                }else{
                    ApiDeleteAppointment(ynwuuid, String.valueOf(accountID), dialog);
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
    public void onMethodActiveCallback(String value) {
        Bundle bundle = new Bundle();

        SearchDetailViewFragment pfFragment = new SearchDetailViewFragment();

        bundle.putString("uniqueID", value);
        pfFragment.setArguments(bundle);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Store the Fragment in stack
        transaction.addToBackStack(null);
        transaction.replace(R.id.mainlayout, pfFragment).commit();
    }

    @Override
    public void onMethodActiveBillIconCallback(String payStatus, String value, String provider, String accountID, String consumer) {
        Log.i("Purpose: ", "billPayment");
        Intent iBill = new Intent(mContext, BillActivity.class);
        iBill.putExtra("ynwUUID", value);
        iBill.putExtra("provider", provider);
        iBill.putExtra("accountID", accountID);
        iBill.putExtra("payStatus", payStatus);
        iBill.putExtra("consumer", consumer);
        iBill.putExtra("purpose", Constants.PURPOSE_BILLPAYMENT);
        startActivity(iBill);
    }

    @Override
    public void onMethodActivePayIconCallback(String payStatus, String value, String provider, String accountID, double amountDue) {
        Log.i("Purpose: ", "prePayment");
        // APIPayment(accountID, ynwUUID, amountDue);
        Intent i = new Intent(mContext, PaymentActivity.class);
        i.putExtra("ynwUUID", value);
        i.putExtra("accountID", accountID);
        i.putExtra("amountDue", amountDue);
        i.putExtra("purpose", Constants.PURPOSE_PREPAYMENT);
        startActivity(i);
    }


    @Override
    public void onMethodAddFavourite(int value, boolean todayFlag, boolean futFlag, boolean oldFlag) {
        mOldFlag = oldFlag;
        mFutureFlag = futFlag;
        mTodayFlag = todayFlag;
        ApiAddFavo(value);
    }

    @Override
    public void onMethodDeleteFavourite(int value, boolean todayFlag, boolean futFlag, boolean oldFlag) {
        mOldFlag = oldFlag;
        mFutureFlag = futFlag;
        mTodayFlag = todayFlag;
        ApiRemoveFavo(value);

    }

    @Override
    public void onMethodRating(String accountID, String UUID, boolean todayFlag, boolean futFlag, boolean oldFlag) {
        mOldFlag = oldFlag;
        mFutureFlag = futFlag;
        mTodayFlag = todayFlag;

        ApiRating(accountID, UUID);
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

        query.put("account", accountID);
        query.put("uId-eq", UUID);


        Call<ArrayList<RatingResponse>> call = apiService.getRating(query);

        Config.logV("Location-----###########@@@@@@" + query);

        call.enqueue(new Callback<ArrayList<RatingResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<RatingResponse>> call, final Response<ArrayList<RatingResponse>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

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
                                if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty()) {
                                    btn_rate.setEnabled(true);
                                    btn_rate.setClickable(true);
                                    btn_rate.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
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
                    Config.closeDialog(getActivity(), mDialog);

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
            call = apiService.PostRating(accountID, body);
        } else {
            call = apiService.PutRating(accountID, body);
        }

        Config.logV("Request--BODY-------------------------" + new Gson().toJson(jsonObj.toString()));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    Config.logV("URL-------Request---" + response.raw().request().url().toString().trim());
                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    dialog.dismiss();
                    Config.logV("Put Rating#########################" + response.code());
                    if (response.code() == 200) {


                        if (response.body().string().equalsIgnoreCase("true")) {
                            Toast.makeText(mContext, "Rated successfully", Toast.LENGTH_LONG).show();
                            ApiTodayChekInList();

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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    private String getFileNameByUri(Context context, Uri uri) {
        String filepath = "";//default fileName
        //Uri filePathUri = uri;
        File file;
        if (uri.getScheme().toString().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION}, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            String mImagePath = cursor.getString(column_index);
            cursor.close();
            filepath = mImagePath;

        } else if (uri.getScheme().compareTo("file") == 0) {
            try {
                file = new File(new URI(uri.toString()));
                if (file.exists())
                    filepath = file.getAbsolutePath();
                file = null;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            filepath = uri.getPath();
        }
        return filepath;
    }

    private void ApiCommunicate(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
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
//            else{
//                path = getRealFilePath(Uri.parse(imagePathList.get(0)));
//            }
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
            jsonObj.put("communicationMessage", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());

        Call<ResponseBody> call = apiService.WaitListMessage(waitListId, String.valueOf(accountID), requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();
                        imagePathList.clear();
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
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }



    private void ApiCommunicateAppointment(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {


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
//            else{
//                path = getRealFilePath(Uri.parse(imagePathList.get(0)));
//            }
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
            jsonObj.put("communicationMessage", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());

        Call<ResponseBody> call = apiService.AppointmentMessage(waitListId, String.valueOf(accountID), requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_LONG).show();
                        imagePathList.clear();
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
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }


    private void ApiDeleteCheckin(String ynwuuid, String accountID, final BottomSheetDialog dialog) {


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
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body().string().equalsIgnoreCase("true")) {

                            Toast.makeText(mContext, "Check-In cancelled successfully", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            ApiFavList();

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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


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
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body().string().equalsIgnoreCase("true")) {

                            Toast.makeText(mContext, "Appointment cancelled successfully", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            ApiFavList();

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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void ApiAddFavo(int providerID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.AddFavourite(providerID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body().string().equalsIgnoreCase("true")) {

                            ApiFavList();
                            Toast.makeText(mContext, "Added to Favourites", Toast.LENGTH_LONG).show();
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    ArrayList<FavouriteModel> mFavList = new ArrayList<>();

    private void ApiFavList() {

        Config.logV("API FAV Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<FavouriteModel>> call = apiService.getFavourites();


        call.enqueue(new Callback<ArrayList<FavouriteModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FavouriteModel>> call, Response<ArrayList<FavouriteModel>> response) {

                try {


                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mFavList.clear();
                        //  mFavList = response.body();
                        DatabaseHandler db = new DatabaseHandler(mContext);
                        db.DeleteFAVID();
                        db.insertFavIDInfo(response.body());
                        mFavList = db.getFavouriteID();
                        ApiTodayChekInList();
                        ApiTodayAppointmentList();


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<FavouriteModel>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());


            }
        });


    }

    private void ApiRemoveFavo(int providerID) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();

        Call<ResponseBody> call = apiService.DeleteFavourite(providerID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getActivity(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        if (response.body().string().equalsIgnoreCase("true")) {
                            Toast.makeText(mContext, "Removed from favourites", Toast.LENGTH_LONG).show();
                            ApiFavList();
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
                    Config.closeDialog(getActivity(), mDialog);

            }
        });


    }

    ExpandableListAdapter adapter;

    boolean mTodayFlag = false, mOldFlag = false, mFutureFlag = false;

    void setItems() {


        mCheckTodayList.clear();
        mCheckOldList.clear();
        mCheckFutureList.clear();

        // Array list for header
        ArrayList<String> header = new ArrayList<String>();


        // Hash map for both header and child
        HashMap<String, ArrayList<ActiveCheckIn>> hashMap = new HashMap<String, ArrayList<ActiveCheckIn>>();

        // Adding headers to list
        header.add("Today");
        header.add("Future");
        header.add("Old");
        // Adding child data


        DatabaseHandler db = new DatabaseHandler(mContext);

        mCheckTodayList = db.getMyCheckinList("today");
        mCheckOldList = db.getMyCheckinList("old");
        mCheckFutureList = db.getMyCheckinList("future");


        Config.logV("Today---#####----" + mCheckTodayList.size() + "" + mTodayFlag);
        Config.logV("Futrue-------" + mCheckFutureList.size() + "" + mFutureFlag);
        Config.logV("Old-------" + mCheckOldList.size() + "" + mOldFlag);

        // Adding header and childs to hash map
        hashMap.put(header.get(0), mCheckTodayList);
        hashMap.put(header.get(1), mCheckFutureList);
        hashMap.put(header.get(2), mCheckOldList);

        LocationManager mgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        adapter = new ExpandableListAdapter(mFavList, mContext, mActivity, mInterface, header, hashMap, mTodayFlag, mFutureFlag, mOldFlag, mgr, mCallback);
        // Setting adpater over expandablelistview
        expandlist.setAdapter(adapter);
        expandlist.setVerticalScrollBarEnabled(false);
        adapter.notifyDataSetChanged();


        if (mCheckTodayList.size() > 0 || mTodayFlag)
            expandlist.expandGroup(0);
        if (mCheckFutureList.size() > 0 || mFutureFlag)
            expandlist.expandGroup(1);
        if ((mCheckTodayList.size() == 0 && mCheckFutureList.size() == 0 && mCheckOldList.size() > 0) || mOldFlag) {
            expandlist.expandGroup(2);
        }


    }



    ExpandableListAdapterAppointment adapterAppointment;

    boolean mTodayFlagAppointment = false, mOldFlagAppointment = false, mFutureFlagAppointment = false;

    void setItemsAppointment() {


        ArrayList<String> header = new ArrayList<String>();
        HashMap<String, ArrayList<ActiveAppointment>> hashMap = new HashMap<String, ArrayList<ActiveAppointment>>();
        header.add("Today");
        header.add("Future");
        header.add("Old");


        hashMap.put(header.get(0), mAppointmentTodayList);
        hashMap.put(header.get(1), mCheckFutureListAppointment);
        hashMap.put(header.get(2), mAppointmentOldList);

        LocationManager mgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        adapterAppointment = new ExpandableListAdapterAppointment(mFavList, mContext, mActivity, mInterface, header, hashMap, mTodayFlag, mFutureFlag, mOldFlag, mgr, mCallback);
        expandlistAppointment.setAdapter(adapterAppointment);
        expandlistAppointment.setVerticalScrollBarEnabled(false);
        adapterAppointment.notifyDataSetChanged();


        if (mAppointmentTodayList.size() > 0 || mTodayFlag)
            expandlistAppointment.expandGroup(0);
        if (mCheckFutureListAppointment.size() > 0 || mFutureFlag)
            expandlistAppointment.expandGroup(1);
        if ((mAppointmentTodayList.size() == 0 && mCheckFutureListAppointment.size() == 0 && mAppointmentOldList.size() > 0) || mOldFlag) {
            expandlistAppointment.expandGroup(2);
        }


    }

    private void getMyLocation() {
        if (googleApiClient != null) {

            if (googleApiClient.isConnected()) {

                Config.logV("Google api connected granted");
                int permissionLocation = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

                    Config.logV("Google api connected granted@2@@@");
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(0);        // 10 seconds, in milliseconds
                    locationRequest.setFastestInterval(0); // 1 second, in milliseconds

                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);
                    //DefaultLocation();

                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(getActivity(),
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }

                                    Config.logV("Google apiClient LocationSettingsStatusCodes.SUCCESS");
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically

                                        Config.logV("Google Ask to turn on GPS automatically");
                                        /*status.startResolutionForResult(getActivity(),
                                                REQUEST_CHECK_SETTINGS_GPS);*/
                                        startIntentSenderForResult(status.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS_GPS, null, 0, 0, 0, null);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                        e.printStackTrace();
                                    }


                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    Config.logV("Google Location settings are not satisfied");
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        //   alertDialog.setTitle("GPSAlertDialogTitle");

        //Setting Dialog Message
        alertDialog.setMessage("To continue, turn on device location");

        //On Pressing Setting button
        alertDialog.setPositiveButton("action_settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}