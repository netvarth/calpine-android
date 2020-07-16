package com.jaldeeinc.jaldee.Fragment;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.gson.Gson;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.BillActivity;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.Home;
import com.jaldeeinc.jaldee.activities.PaymentActivity;
import com.jaldeeinc.jaldee.adapter.DetailFileAdapter;
import com.jaldeeinc.jaldee.adapter.ExpandableListAdapterAppointment;
import com.jaldeeinc.jaldee.callback.ActiveAdapterOnCallback;
import com.jaldeeinc.jaldee.callback.HistoryAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTypefaceSpan;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.ActiveAppointment;
import com.jaldeeinc.jaldee.response.FavouriteModel;
import com.jaldeeinc.jaldee.response.RatingResponse;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentMyJaldee extends RootFragment implements HistoryAdapterCallback,ActiveAdapterOnCallback {

    String[] imgExtsSupported = new String[]{"jpg", "jpeg", "png"};
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};

    public AppointmentMyJaldee() {
        // Required empty public constructor
    }
    String simpleFileName1 = "note1.txt";
    String simpleFileName2 = "note2.txt";
    String simpleFileName3 = "note3.txt";
    boolean firstTimeRating = false;
    Context mContext;
    Activity mActivity;
    private int PICK_IMAGE_REQUEST = 1;
    ArrayList<ActiveAppointment> mAppointmentTodayList = new ArrayList<>();
    ArrayList<ActiveAppointment> mAppointmentFutureList = new ArrayList<>();
    ArrayList<ActiveAppointment> mAppointmentOldList = new ArrayList<>();
    HistoryAdapterCallback mInterface;
    ActiveAdapterOnCallback mCallback;
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
    EditText edt_message;
    Dialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View row = inflater.inflate(R.layout.appointment_myjaldee, container, false);
        mContext = getActivity();
        mActivity = getActivity();
        mInterface = (HistoryAdapterCallback) this;
        mCallback = (ActiveAdapterOnCallback) this;
        Home.doubleBackToExitPressedOnce = false;
        TextView tv_title = (TextView) row.findViewById(R.id.toolbartitle);
        ImageView iBackPress = (ImageView) row.findViewById(R.id.backpress);
        Typeface tyface1 = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface1);
        iBackPress.setVisibility(View.VISIBLE);
        tv_title.setText("Appointments");
        Typeface tyface = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final TextView txtCheckins = (TextView) row.findViewById(R.id.checkins);
        expandlistAppointment = (ExpandableListView) row.findViewById(R.id.appointmentView);
        expandlistAppointment.setVisibility(View.VISIBLE);

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
        mDialog = Config.getProgressDialog(getActivity(), getActivity().getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        if (Config.isOnline(mContext)) {
            ApiFavList();
        } else {
            DatabaseHandler db = new DatabaseHandler(mContext);
            mFavList.clear();
            mFavList = db.getFavouriteID();
        }
        mFutureFlag = false;
        mTodayFlag = false;
        mOldFlag = false;
        if (Config.isOnline(mContext)) {
            //ApiTodayAppointmentList();
            getActiveAppointments();
        }
        return row;
    }

    /**
     * * Author Mani E V
     * To get Active Appointments [Both Today and Future]
     */
    private void getActiveAppointments() {
        Config.logV("API TODAY Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Map<String, String> filter = new HashMap<String, String>();
        filter.put("apptStatus-neq", "failed,prepaymentPending");
        Call<ArrayList<ActiveAppointment>> call = apiService.getActiveAppointment(filter);
        call.enqueue(new Callback<ArrayList<ActiveAppointment>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveAppointment>> call, Response<ArrayList<ActiveAppointment>> response) {
                try {
                    if (response.code() == 200) {
                        mAppointmentFutureList.clear();
                        mAppointmentTodayList.clear();
                        ArrayList<ActiveAppointment> mActiveAppointments = response.body();
                        Log.i("appointment123today",new Gson().toJson(mActiveAppointments));
                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        for (int i = 0; i < mActiveAppointments.size(); i++) {
                            if (date.equalsIgnoreCase(mActiveAppointments.get(i).getAppmtDate())) {
                                mAppointmentTodayList.add(mActiveAppointments.get(i));
                                Log.i("appointment123456",new Gson().toJson(mAppointmentTodayList));
                            } else {
                                mAppointmentFutureList.add(mActiveAppointments.get(i));
                            }
                        }
                        getHistoryAppointments();
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

    /**
     * * Author Mani E V
     * To get History Appointments
     */
    private void getHistoryAppointments() {
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        Map<String, String> filter = new HashMap<String, String>();
        filter.put("apptStatus-neq", "failed,prepaymentPending");
        Call<ArrayList<ActiveAppointment>> call = apiService.getAppointmentList(filter);
        call.enqueue(new Callback<ArrayList<ActiveAppointment>>() {
            @Override
            public void onResponse(Call<ArrayList<ActiveAppointment>> call, Response<ArrayList<ActiveAppointment>> response) {
                try {
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        mAppointmentOldList.clear();
                        mAppointmentOldList = response.body();
                        Log.i("appointment123Old", new Gson().toJson(mAppointmentOldList));
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
                    ApiCommunicateAppointment(ynwuuid, String.valueOf(accountID), edt_message.getText().toString(), dialog);
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
                        imagePathList.add(orgFilePath);
                        DetailFileAdapter mDetailFileAdapter = new DetailFileAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();
                        if(imagePathList.size()>0 &&  edt_message.getText().toString().equals("")){
                            Toast.makeText(mContext, "Please enter add note", Toast.LENGTH_SHORT).show();
                        }
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
            if(imagePathList.size()>0 &&  edt_message.getText().toString().equals("")){
                Toast.makeText(mContext, "Please enter add note", Toast.LENGTH_SHORT).show();
            }
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
    public void onMethodBillIconCallback(String payStatus, String value, String provider, String accountID, String CustomerName,int customerId) {
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
                    ApiDeleteAppointment(ynwuuid, String.valueOf(accountID), dialog);
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
    public void onMethodActiveBillIconCallback(String payStatus, String value, String provider, String accountID, String consumer,int customerId) {
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
    public void onMethodActivePayIconCallback(String payStatus, String value, String provider, String accountID, double amountDue,int customerId) {
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
            } else {
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
                        DatabaseHandler db = new DatabaseHandler(mContext);
                        db.DeleteFAVID();
                        db.insertFavIDInfo(response.body());
                        mFavList = db.getFavouriteID();
                        getActiveAppointments();
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
                if (mDialog.isShowing())
                    Config.closeDialog(getActivity(), mDialog);
            }
        });
    }
    boolean mTodayFlag = false, mOldFlag = false, mFutureFlag = false;
    ExpandableListAdapterAppointment adapterAppointment;
    boolean mTodayFlagAppointment = false, mOldFlagAppointment = false, mFutureFlagAppointment = false;
    void setItemsAppointment() {
        ArrayList<String> header = new ArrayList<String>();
        HashMap<String, ArrayList<ActiveAppointment>> hashMap = new HashMap<String, ArrayList<ActiveAppointment>>();
        header.add("Today");
        header.add("Future");
        header.add("Old");
        hashMap.put(header.get(0), mAppointmentTodayList);
        hashMap.put(header.get(1), mAppointmentFutureList);
        hashMap.put(header.get(2), mAppointmentOldList);
        if (mDialog.isShowing())
            Config.closeDialog(getActivity(), mDialog);
        LocationManager mgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        adapterAppointment = new ExpandableListAdapterAppointment(mFavList, mContext, mActivity, mInterface, header, hashMap, mTodayFlag, mFutureFlag, mOldFlag, mgr, mCallback);
        expandlistAppointment.setAdapter(adapterAppointment);
        expandlistAppointment.setVerticalScrollBarEnabled(false);
        adapterAppointment.notifyDataSetChanged();
        if (mAppointmentTodayList.size() > 0 || mTodayFlag)
            expandlistAppointment.expandGroup(0);
        if (mAppointmentFutureList.size() > 0 || mFutureFlag)
            expandlistAppointment.expandGroup(1);
        if ((mAppointmentTodayList.size() == 0 && mAppointmentFutureList.size() == 0 && mAppointmentOldList.size() > 0) || mOldFlag) {
            expandlistAppointment.expandGroup(2);
        }
    }
}