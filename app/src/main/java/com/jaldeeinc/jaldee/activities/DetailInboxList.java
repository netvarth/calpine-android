package com.jaldeeinc.jaldee.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailFileAdapter;
import com.jaldeeinc.jaldee.adapter.DetailInboxAdapter;
import com.jaldeeinc.jaldee.callback.DetailInboxAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.FileAttachment;
import com.jaldeeinc.jaldee.response.InboxModel;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by sharmila on 27/8/18.
 */

public class DetailInboxList extends AppCompatActivity implements DetailInboxAdapterCallback {
    RecyclerView recylce_inbox_detail;
    Context mContext;
    DetailInboxAdapter mDetailAdapter;
    static ArrayList<InboxModel> mDetailInboxList = new ArrayList<>();


    TextView txtprovider;
    String provider;
    DetailInboxAdapterCallback mInterface;
    BottomSheetDialog dialog;
    ImageView imageview;
    TextView tv_attach, tv_camera;
    private static final String IMAGE_DIRECTORY = "/Jaldee" +
            "";
    private int GALLERY = 1, CAMERA = 2;
    ArrayList<String> fileAttachment;
    String path;
    Bitmap bitmap;
    File f, file;
    RecyclerView recycle_image_attachment;
    RelativeLayout displayImages;
    ArrayList<String> imagePathList = new ArrayList<>();
    private Uri mImageUri;
    ArrayList<FileAttachment> attachments;

    String[] imgExtsSupported = new String[]{"jpg", "jpeg", "png"};
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    EditText edt_message;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailinbox);
        recylce_inbox_detail = findViewById(R.id.recylce_inbox_detail);
        mContext = this;


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            provider = extras.getString("provider");

        }
        ImageView iBackPress = findViewById(R.id.backpress);
        iBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // what do you want here
                finish();
            }
        });


        TextView tv_title = findViewById(R.id.toolbartitle);
        tv_title.setText(Config.toTitleCase(provider));
        Typeface tyface = Typeface.createFromAsset(getAssets(),
                "fonts/Montserrat_Bold.otf");
        tv_title.setTypeface(tyface);
        txtprovider = (TextView) findViewById(R.id.txtprovider);
        tv_title.setTypeface(tyface);
        txtprovider.setTypeface(tyface);
        txtprovider.setText(provider);
        mInterface = (DetailInboxAdapterCallback) this;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recylce_inbox_detail.setLayoutManager(mLayoutManager);
        Collections.sort(mDetailInboxList, new Comparator<InboxModel>() {
            @Override
            public int compare(InboxModel r1, InboxModel r2) {
                return new Long(r2.getTimeStamp()).compareTo(new Long(r1.getTimeStamp()));
            }
        });

        mDetailAdapter = new DetailInboxAdapter(mDetailInboxList, mContext, mInterface, bitmap);
        recylce_inbox_detail.setAdapter(mDetailAdapter);
        mDetailAdapter.notifyDataSetChanged();

    }


    public static boolean setInboxList(ArrayList<InboxModel> data) {
        mDetailInboxList = data;
        return true;
    }

    @Override
    public void onMethodCallback(final String waitListId, final String accountID, final long timestamp) {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
        dialog.setContentView(R.layout.reply);
        dialog.show();
        final Button btn_send = dialog.findViewById(R.id.btn_send);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        edt_message = dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg = dialog.findViewById(R.id.txtsendmsg);
        tv_attach = dialog.findViewById(R.id.btn);
        tv_camera = dialog.findViewById(R.id.camera);
        recycle_image_attachment = dialog.findViewById(R.id.recycler_view_image);
        //  imageview = dialog.findViewById(R.id.iv);
        // RelativeLayout displayImages = dialog.findViewById(R.id.display_images);


        if (waitListId != null) {
            requestMultiplePermissions();
            tv_attach.setVisibility(View.VISIBLE);
            tv_camera.setVisibility(View.VISIBLE);

            tv_attach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                //requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

                                requestPermissions(new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY);

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
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                //requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);

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

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (waitListId.contains("_appt")) {


                    if (waitListId != null) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String dateString = formatter.format(new Date(timestamp));
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        String currentformattedDate = df.format(new Date());
                        Date currentdate = null;
                        try {
                            currentdate = df.parse(currentformattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (new SimpleDateFormat("dd/MM/yyyy").parse(dateString).before(currentdate)) {
                                ApiCommunicateAppointment("h_" + waitListId, String.valueOf(accountID), edt_message.getText().toString(), dialog);
                            } else {
                                ApiCommunicateAppointment(waitListId, String.valueOf(accountID), edt_message.getText().toString(), dialog);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else {
                        ApiCommunicateWithoutAppointmentID(String.valueOf(accountID), edt_message.getText().toString(), dialog);
                    }

                } else {
                    if (waitListId != null) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String dateString = formatter.format(new Date(timestamp));


                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        String currentformattedDate = df.format(new Date());
                        Date currentdate = null;
                        try {
                            currentdate = df.parse(currentformattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        try {
                            if (new SimpleDateFormat("dd/MM/yyyy").parse(dateString).before(currentdate)) {
                                ApiCommunicate("h_" + waitListId, String.valueOf(accountID), edt_message.getText().toString(), dialog);
                            } else {
                                ApiCommunicate(waitListId, String.valueOf(accountID), edt_message.getText().toString(), dialog);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else {
                        ApiCommunicateWithoutWaitListID(String.valueOf(accountID), edt_message.getText().toString(), dialog);
                    }
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) {
            return;
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
                        if (Arrays.asList(fileExtsSupported).contains(extension)) {
                            if (orgFilePath == null) {
                                orgFilePath = Config.getFilePathFromURI(mContext, uri, extension);
                            }
                        } else {
                            Toast.makeText(mContext, "File type not supported", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        imagePathList.add(orgFilePath);

                        DetailFileAdapter mDetailFileAdapter = new DetailFileAdapter(imagePathList, mContext);
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DetailInboxList.this, 3);
                        recycle_image_attachment.setLayoutManager(mLayoutManager);
                        recycle_image_attachment.setAdapter(mDetailFileAdapter);
                        mDetailFileAdapter.notifyDataSetChanged();
                        if (imagePathList.size() > 0 && edt_message.getText().toString().equals("")) {
                            Toast.makeText(mContext, "Please enter message", Toast.LENGTH_SHORT).show();
                        }
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
                }
                DetailFileAdapter mDetailFileAdapter = new DetailFileAdapter(imagePathList, mContext);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(DetailInboxList.this, 3);
                recycle_image_attachment.setLayoutManager(mLayoutManager);
                recycle_image_attachment.setAdapter(mDetailFileAdapter);
                mDetailFileAdapter.notifyDataSetChanged();
                if (imagePathList.size() > 0 && edt_message.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Please enter message", Toast.LENGTH_SHORT).show();
                }
            }
        }
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

    private void ApiCommunicateWithoutWaitListID(String accountID, String message, final BottomSheetDialog dialog) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("communicationMessage", message);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.PostMessage(accountID, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();

                    } else {
                        Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
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

    private void ApiCommunicateWithoutAppointmentID(String accountID, String message, final BottomSheetDialog dialog) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("communicationMessage", message);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.PostMessage(accountID, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(getParent(), mDialog);

                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {

                        Toast.makeText(mContext, "Message sent successfully", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();

                    } else {
                        Toast.makeText(mContext, response.errorBody().string(), Toast.LENGTH_SHORT).show();
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


    private void ApiCommunicate(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {

        Log.i("poiioppo", accountID);

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < imagePathList.size(); i++) {
//            if(imagePathList.contains("content://")) {
//                Uri imageUri = Uri.parse(imagePathList.get(i));
//                File file = new File(String.valueOf(imageUri));
//                mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
//            }
            file = new File(imagePathList.get(i));

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
                        Config.closeDialog(DetailInboxList.this, mDialog);

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
                    Config.closeDialog(DetailInboxList.this, mDialog);

            }
        });
    }


    private void ApiCommunicateAppointment(String waitListId, String accountID, String message, final BottomSheetDialog dialog) {

        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        mBuilder.addFormDataPart("message", message);
        for (int i = 0; i < imagePathList.size(); i++) {
//            if(imagePathList.contains("content://")) {
//                Uri imageUri = Uri.parse(imagePathList.get(i));
//                File file = new File(String.valueOf(imageUri));
//                mBuilder.addFormDataPart("attachments", file.getName(), RequestBody.create(type, file));
//            }
            file = new File(imagePathList.get(i));

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
                        Config.closeDialog(DetailInboxList.this, mDialog);

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
                    Config.closeDialog(DetailInboxList.this, mDialog);

            }
        });
    }

}