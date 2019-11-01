package com.jaldeeinc.jaldee.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaldeeinc.jaldee.BuildConfig;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.DetailInboxAdapter;
import com.jaldeeinc.jaldee.callback.DetailInboxAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.response.InboxModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jaldeeinc.jaldee.adapter.DetailInboxAdapter.PICKFILE_RESULT_CODE;

/**
 * Created by sharmila on 27/8/18.
 */

public class DetailInboxList extends AppCompatActivity implements DetailInboxAdapterCallback {
    RecyclerView recylce_inbox_detail;
    Context mContext;
    DetailInboxAdapter mDetailAdapter;
    static ArrayList<InboxModel> mDetailInboxList = new ArrayList<>();
    TextView txtprovider, tv_attach, tv_fileAttach;
    String provider;
    DetailInboxAdapterCallback mInterface;
    String message;
    BottomSheetDialog dialog;
    private static final int CREATE_REQUEST_CODE = 40;
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;



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
    //   tv_attach = (TextView) findViewById(R.id.txt_attach);
    //   tv_fileAttach = (TextView) findViewById(R.id.txt_file);

        tv_title.setTypeface(tyface);
        txtprovider.setTypeface(tyface);
        txtprovider.setText(provider);
        mInterface = (DetailInboxAdapterCallback) this;
        Config.logV("mDetailInboxList SIZE #############" + mDetailInboxList.size());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recylce_inbox_detail.setLayoutManager(mLayoutManager);

        Collections.sort(mDetailInboxList, new Comparator<InboxModel>() {
            @Override
            public int compare(InboxModel r1, InboxModel r2) {
                return new Long(r2.getTimeStamp()).compareTo(new Long(r1.getTimeStamp()));
            }
        });
        mDetailAdapter = new DetailInboxAdapter(mDetailInboxList, mContext, mInterface);
        recylce_inbox_detail.setAdapter(mDetailAdapter);
        mDetailAdapter.notifyDataSetChanged();
    }

    public static boolean setInboxList(ArrayList<InboxModel> data) {
        mDetailInboxList = data;
        Config.logV("mDetailInboxList SIZE" + mDetailInboxList.size());
        return true;
    }

    @Override
    public void  onMethodCallback(final String waitListId, final int accountID, final long timestamp,final File attachment) {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.DialogStyle);
        dialog.setContentView(R.layout.reply);
        dialog.show();

        final Button btn_send = dialog.findViewById(R.id.btn_send);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final EditText edt_message = dialog.findViewById(R.id.edt_message);
        TextView txtsendmsg = dialog.findViewById(R.id.txtsendmsg);
     //   TextView tv_attach = dialog.findViewById(R.id.txt_attach);
        edt_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                if (edt_message.getText().toString().length() >= 1 && !edt_message.getText().toString().trim().isEmpty()) {
                    btn_send.setEnabled(true);
                    btn_send.setClickable(true);
                    btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.roundedrect_blue));
                } else {
                    btn_send.setEnabled(false);
                    btn_send.setClickable(false);
                    btn_send.setBackground(mContext.getResources().getDrawable(R.drawable.btn_checkin_grey));
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
                Config.logV("WAITLIST ID @@@@@@@@@@@" + waitListId);
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
                            Config.logV("WAITLIST Past Date --------------------" + new Date());
                            ApiCommunicate("h_" + waitListId, String.valueOf(accountID), edt_message.getText().toString(), dialog, attachment);
                        } else {
                            Config.logV("WAITLIST Today Date --------------------");
                            ApiCommunicate(waitListId, String.valueOf(accountID), edt_message.getText().toString(), dialog, attachment);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    ApiCommunicateWithoutWaitListID(String.valueOf(accountID), edt_message.getText().toString(), dialog);
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

//        tv_attach.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("file/*");
//                startActivityForResult(intent,PICKFILE_RESULT_CODE);
//             //  ApiCommunicate(waitListId,String.valueOf(accountID),message,dialog,attachment);
//            }
//        });

    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch(requestCode){
//            case PICKFILE_RESULT_CODE:
//                if(resultCode==RESULT_OK){
//                    String FilePath = data.getData().getPath();
//                    tv_fileAttach.setText(FilePath);
//                    tv_fileAttach.setVisibility(View.VISIBLE);
////
//                    String path="File Path";
//                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                    File file = new File(path);
//
//                    MimeTypeMap mime = MimeTypeMap.getSingleton();
//                    String ext=file.getName().substring(file.getName().indexOf(".")+1);
//                    String type = mime.getMimeTypeFromExtension(ext);
//
//                    intent.setDataAndType(Uri.fromFile(file),type);
//
//                    startActivity(intent);
//
//                }
//                break;
//
//        }
//    }






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

    private void ApiCommunicate(String waitListId, String accountID, String message, final BottomSheetDialog dialog, File attachment) {


        ApiInterface apiService =
                ApiClient.getClient(mContext).create(ApiInterface.class);


        final Dialog mDialog = Config.getProgressDialog(mContext, mContext.getResources().getString(R.string.dialog_log_in));
        mDialog.show();


        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("communicationMessage", message);
            jsonObj.put("attachments",attachment);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObj.toString());
        Call<ResponseBody> call = apiService.WaitListMessage(waitListId, String.valueOf(accountID), body);

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
}
