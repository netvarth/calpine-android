package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailFileImageAdapter;
import com.jaldeeinc.jaldee.adapter.MessageListAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewBold;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.UserMessage;
import com.jaldeeinc.jaldee.response.InboxList;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private Context mContext;
    RecyclerView rvChatMessages;
    MessageListAdapter messageListAdapter;
    ArrayList<InboxList> mInboxList = new ArrayList<>();
    ArrayList<UserMessage> userMessagesList = new ArrayList<>();
    String uuId = "", providerName = "";
    int accountID;
    private EditText etMessage;
    private ImageView ivSend;
    private CardView cvBack;
    private CustomTextViewBold tvTitle;
    ArrayList<String> imagePathList = new ArrayList<>();
    ArrayList<String> imagePathLists = new ArrayList<>();
    Bitmap bitmap;
    File f, file;
    String path, from, from1 = "";
    private LinearLayout llNoHistory;
    private ImageView iv_attach;
    private GifImageView ivNoSlots;
    BottomSheetDialog dialog;
    CustomTextViewSemiBold tvErrorMessage;
    TextView tv_attach, tv_camera;
    RecyclerView recycle_image_attachment;
    private int GALLERY = 1, CAMERA = 2;
    String[] fileExtsSupported = new String[]{"jpg", "jpeg", "png", "pdf"};
    private static final String IMAGE_DIRECTORY = "/Jaldee" +
            "";
    private Uri mImageUri;
    private boolean fromPushNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mContext = ChatActivity.this;
        Intent i = getIntent();
        uuId = i.getStringExtra("uuid");
        accountID = i.getIntExtra("accountId", 0);
        providerName = i.getStringExtra("name");
        from = i.getStringExtra("from");
        from1 = i.getStringExtra("from1");
        fromPushNotification = i.getBooleanExtra(Constants.PUSH_NOTIFICATION, false);
        rvChatMessages = findViewById(R.id.rv_chatMessages);
        etMessage = findViewById(R.id.et_message);
        ivSend = findViewById(R.id.iv_submit_message);
        tvTitle = findViewById(R.id.tv_title);
        cvBack = findViewById(R.id.cv_back);
        llNoHistory = findViewById(R.id.ll_noHistory);
        iv_attach = findViewById(R.id.iv_attachFile);
        ivNoSlots = findViewById(R.id.no_slots);

        if (providerName != null) {
            tvTitle.setText(providerName);
        }
        Typeface font_style = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-Regular.ttf");
        etMessage.setTypeface(font_style);
        rvChatMessages = (RecyclerView) findViewById(R.id.rv_chatMessages);
        messageListAdapter = new MessageListAdapter(mContext, userMessagesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.scrollToPositionWithOffset(0, userMessagesList.size());
        rvChatMessages.setLayoutManager(linearLayoutManager);
        rvChatMessages.getLayoutManager().scrollToPosition(userMessagesList.size() - 1);
        rvChatMessages.setAdapter(messageListAdapter);

        iv_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            if (from != null && from.equalsIgnoreCase(Constants.APPOINTMENT)) {
                                ApiCommunicateAppointment(uuId, String.valueOf(accountID), etMessage.getText().toString());
                            } else if (from != null && from.equalsIgnoreCase(Constants.CHECKIN)) {
                                ApiCommunicateCheckin(uuId, String.valueOf(accountID), etMessage.getText().toString());
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

                            if (from != null && from.equalsIgnoreCase(Constants.APPOINTMENT)) {
                                ApiCommunicateAppointment(uuId, String.valueOf(accountID), etMessage.getText().toString());
                            } else if (from != null && from.equalsIgnoreCase(Constants.CHECKIN)) {
                                ApiCommunicateCheckin(uuId, String.valueOf(accountID), etMessage.getText().toString());
                            } else if (from != null && from.equalsIgnoreCase(Constants.ORDERS)) {
                                ApiCommunicateOrders(uuId, String.valueOf(accountID), etMessage.getText().toString());
                            } else if (from != null && from.equalsIgnoreCase(Constants.PROVIDER) || from != null && from.equalsIgnoreCase(Constants.INBOX)) {
                                ApiCommunicate(String.valueOf(accountID), etMessage.getText().toString());
                            } else if (from != null && from.equalsIgnoreCase(Constants.DONATION)) {
                                ApiCommunicateDonation(uuId, String.valueOf(accountID), etMessage.getText().toString());
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


        });

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etMessage.getText().toString().trim().equalsIgnoreCase("")) {

                    if (from != null && from.equalsIgnoreCase(Constants.APPOINTMENT)) {
                        ApiCommunicateAppointment(uuId, String.valueOf(accountID), etMessage.getText().toString());
                    } else if (from != null && from.equalsIgnoreCase(Constants.CHECKIN)) {
                        ApiCommunicateCheckin(uuId, String.valueOf(accountID), etMessage.getText().toString());
                    } else if (from != null && from.equalsIgnoreCase(Constants.ORDERS)) {
                        ApiCommunicateOrders(uuId, String.valueOf(accountID), etMessage.getText().toString());
                    } else if (from != null && from.equalsIgnoreCase(Constants.PROVIDER) || from != null && from.equalsIgnoreCase(Constants.INBOX)) {
                        ApiCommunicate(String.valueOf(accountID), etMessage.getText().toString());
                    } else if (from != null && from.equalsIgnoreCase(Constants.DONATION)) {
                        ApiCommunicateDonation(uuId, String.valueOf(accountID), etMessage.getText().toString());
                    }

                }
            }
        });

        ivNoSlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApiCommunicate(String.valueOf(accountID), "Hello");

            }
        });

        cvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        ApiInboxList();
    }

    public String getURLForResource(int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }

    @Override
    public void onBackPressed() {

        if (fromPushNotification) {
            Intent intent = new Intent(ChatActivity.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            super.onBackPressed();
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
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
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
                recycle_image_attachment.setLayoutManager(mLayoutManager);
                recycle_image_attachment.setAdapter(mDetailFileAdapter);
                mDetailFileAdapter.notifyDataSetChanged();
            }
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


    private void ApiCommunicateAppointment(String waitListId, String accId, String message) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        if (message.equalsIgnoreCase("")) {
            message = "";
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("msg", message);
        obj.addProperty("messageType", "BOOKINGS");

//        String object = obj.toString() +";type=application/json";

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj.toString());


        mBuilder.addFormDataPart("message", "blob", body);

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
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.AppointmentMessage(waitListId, String.valueOf(accId), requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ChatActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        etMessage.setText("");
                        ApiInboxList();
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
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(ChatActivity.this, mDialog);
            }
        });
    }

    private void ApiCommunicateCheckin(String waitListId, String accountID, String message) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        if (message.equalsIgnoreCase("")) {
            message = "";
        }
        JsonObject obj = new JsonObject();
        obj.addProperty("msg", message);
        obj.addProperty("messageType", "BOOKINGS");

//        String object = obj.toString() +";type=application/json";

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj.toString());


        mBuilder.addFormDataPart("message", "blob", body);
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
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.WaitListMessage(waitListId, String.valueOf(accountID), requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog((Activity) mContext, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        etMessage.setText("");
                        ApiInboxList();
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
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog((Activity) mContext, mDialog);
            }
        });
    }

    private void ApiCommunicateOrders(String waitListId, String accId, String message) {
        ApiInterface apiService = ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        if (message.equalsIgnoreCase("")) {
            message = "";
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("msg", message);
        obj.addProperty("messageType", "BOOKINGS");

//        String object = obj.toString() +";type=application/json";

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj.toString());


        mBuilder.addFormDataPart("message", "blob", body);
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
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.orderMessage(waitListId, String.valueOf(accId), requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ChatActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {

                        etMessage.setText("");
                        ApiInboxList();
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
                Config.logV("Fail---------------" + t.toString());
                if (mDialog.isShowing())
                    Config.closeDialog(ChatActivity.this, mDialog);
            }
        });
    }

    private void ApiCommunicate(String accountID, String message) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        if (message.equalsIgnoreCase("")) {
            message = "";
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("msg", message);
        if (from.equalsIgnoreCase(Constants.PROVIDER)) {
            obj.addProperty("messageType", "ENQUIRY");
        } else {
            obj.addProperty("messageType", "CHAT");
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj.toString());


        mBuilder.addFormDataPart("message", "blob", body);
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
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.PostMessage(accountID, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ChatActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        etMessage.setText("");
                        ApiInboxList();
                        imagePathList.clear();
                    } else if (response.code() == 403) {
                        Toast.makeText(mContext, "Please complete the details of profile name,location and working hours to continue", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(mContext, "The account doesn't exist", Toast.LENGTH_SHORT).show();
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
                    Config.closeDialog(ChatActivity.this, mDialog);
            }
        });
    }

    private void ApiCommunicateDonation(String uuId, String accountID, String message) {
        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);
        MediaType type = MediaType.parse("*/*");
        MultipartBody.Builder mBuilder = new MultipartBody.Builder();
        mBuilder.setType(MultipartBody.FORM);
        if (message.equalsIgnoreCase("")) {
            message = "";
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("msg", message);
        obj.addProperty("messageType", "BOOKINGS");

//        String object = obj.toString() +";type=application/json";

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), obj.toString());


        mBuilder.addFormDataPart("message", "blob", body);
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
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.donationMessage(uuId, accountID, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (mDialog.isShowing())
                        Config.closeDialog(ChatActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());
                    if (response.code() == 200) {
                        etMessage.setText("");
                        ApiInboxList();
                        imagePathList.clear();
                    } else if (response.code() == 403) {
                        Toast.makeText(mContext, "Please complete the details of profile name,location and working hours to continue", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 404) {
                        Toast.makeText(mContext, "The account doesn't exist", Toast.LENGTH_SHORT).show();
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
                    Config.closeDialog(ChatActivity.this, mDialog);
            }
        });
    }


    private void ApiInboxList() {

        Config.logV("API Call");
        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ArrayList<InboxList>> call = apiService.getCommunications();
        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();
        final HashMap<String, List<InboxList>> messagesMap = new HashMap<String, List<InboxList>>();

        call.enqueue(new Callback<ArrayList<InboxList>>() {
            @Override
            public void onResponse(Call<ArrayList<InboxList>> call, Response<ArrayList<InboxList>> response) {

                try {

                    if (mDialog.isShowing())
                        Config.closeDialog(ChatActivity.this, mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mInboxList.clear();
                        userMessagesList.clear();
                        mInboxList = response.body();
                        ArrayList<InboxList> inboxModels = new ArrayList<>();
                        if (mInboxList != null && mInboxList.size() > 0) {

                            for (int i = 0; i < mInboxList.size(); i++) {

                                if (accountID == Integer.parseInt(mInboxList.get(i).getAccountId())) {

                                    inboxModels.add(mInboxList.get(i));

                                }
                            }

                            if (inboxModels.size() > 0) {

                                ArrayList<String> messageIds = new ArrayList<>();

                                for (InboxList inbox : inboxModels) {

                                    UserMessage userMessage = new UserMessage();
                                    userMessage.setMessage(inbox.getMsg());

                                    if (!inbox.isRead()) {

                                        messageIds.add(inbox.getMessageId());
                                    }

                                    if (inbox.getOwner() != null) {
                                        userMessage.setId(inbox.getOwner().getId());
                                    }
                                    if (inbox.getReceiver() != null && inbox.getReceiver().getName() != null) {
                                        userMessage.setUserName(inbox.getReceiver().getName());
                                    } else {
                                        userMessage.setUserName("");
                                    }
                                    if (inbox.getAccountName() != null) {
                                        userMessage.setSenderName(inbox.getAccountName());
                                    } else {
                                        userMessage.setSenderName("");
                                    }
                                    userMessage.setTimeStamp(inbox.getTimeStamp());
                                    if (inbox.getMessageType() != null) {
                                        userMessage.setMessageType(inbox.getMessageType());
                                    }
                                    userMessagesList.add(userMessage);

                                    if (inbox.getAttachments() != null && inbox.getAttachments().size() > 0) {
                                        userMessage.setAttachments(inbox.getAttachments());
                                    }

                                }

                                String ids = TextUtils.join("-", messageIds);
                                ApiReadMessages(ids);
                                llNoHistory.setVisibility(View.GONE);
                                rvChatMessages.setVisibility(View.VISIBLE);
                                messageListAdapter = new MessageListAdapter(mContext, userMessagesList);
                                rvChatMessages.setLayoutManager(new LinearLayoutManager(mContext));
                                rvChatMessages.getLayoutManager().scrollToPosition(userMessagesList.size() - 1);
                                rvChatMessages.setAdapter(messageListAdapter);

                            } else {
                                // hide
                                llNoHistory.setVisibility(View.VISIBLE);
                                rvChatMessages.setVisibility(View.GONE);
                            }
                        } else {

                            rvChatMessages.setVisibility(View.GONE);
                            llNoHistory.setVisibility(View.VISIBLE);
                        }


                    } else {

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<InboxList>> call, Throwable t) {
                // Log error here since request failed
                Config.logV("Fail---------------" + t.toString());

                if (mDialog.isShowing())
                    Config.closeDialog(ChatActivity.this, mDialog);
            }
        });


    }

    private void ApiReadMessages(String ids) {

        final ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);

        Call<ResponseBody> call = apiService.readMessages("0",ids,String.valueOf(accountID));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    if (response.code() == 200) {

                    } else {

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

}