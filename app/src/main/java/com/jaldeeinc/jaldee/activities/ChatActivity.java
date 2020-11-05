package com.jaldeeinc.jaldee.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.MessageListAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.UserMessage;
import com.jaldeeinc.jaldee.response.InboxList;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
    private CustomTextViewSemiBold tvTitle;
    ArrayList<String> imagePathList = new ArrayList<>();
    Bitmap bitmap;
    File f, file;
    String path;
    private LinearLayout llNoHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mContext = ChatActivity.this;
        Intent i = getIntent();
        uuId = i.getStringExtra("uuid");
        accountID = i.getIntExtra("accountId", 0);
        providerName = i.getStringExtra("name");
        rvChatMessages = findViewById(R.id.rv_chatMessages);
        etMessage = findViewById(R.id.et_message);
        ivSend = findViewById(R.id.iv_submit_message);
        tvTitle = findViewById(R.id.tv_title);
        cvBack = findViewById(R.id.cv_back);
        llNoHistory = findViewById(R.id.ll_noHistory);

        if (providerName != null) {
            tvTitle.setText(providerName);
        }
        Typeface font_style = Typeface.createFromAsset(mContext.getAssets(), "fonts/JosefinSans-Regular.ttf");
        etMessage.setTypeface(font_style);
        rvChatMessages = (RecyclerView) findViewById(R.id.rv_chatMessages);
        messageListAdapter = new MessageListAdapter(mContext, userMessagesList);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(mContext));
        rvChatMessages.setAdapter(messageListAdapter);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etMessage.getText().toString().trim().equalsIgnoreCase("")) {

                    ApiCommunicateAppointment(uuId, String.valueOf(accountID), etMessage.getText().toString());

                }
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

    private void ApiCommunicateAppointment(String waitListId, String accId, String message) {
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
//                path = saveImage(bitmap);
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

                                if (mInboxList.get(i).getWaitlistId() != null) {
                                    if (mInboxList.get(i).getWaitlistId().equalsIgnoreCase(uuId)) {

                                        inboxModels.add(mInboxList.get(i));
                                    }
                                }
                            }

                            if (inboxModels.size() > 0) {

                                for (InboxList inbox : inboxModels) {

                                    UserMessage userMessage = new UserMessage();
                                    userMessage.setMessage(inbox.getMsg());
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
                                    userMessagesList.add(userMessage);
                                }
                                llNoHistory.setVisibility(View.GONE);
                                rvChatMessages.setVisibility(View.VISIBLE);
                                messageListAdapter = new MessageListAdapter(mContext, userMessagesList);
                                rvChatMessages.setLayoutManager(new LinearLayoutManager(mContext));
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

}