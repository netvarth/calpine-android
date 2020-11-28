package com.jaldeeinc.jaldee.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.adapter.MessageListAdapter;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.connection.ApiClient;
import com.jaldeeinc.jaldee.connection.ApiInterface;
import com.jaldeeinc.jaldee.model.UserMessage;
import com.jaldeeinc.jaldee.response.InboxList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatHistory extends Dialog {

    private Context mContext;
    RecyclerView rvChatMessages;
    MessageListAdapter messageListAdapter;
    ArrayList<InboxList> mInboxList = new ArrayList<>();
    ArrayList<UserMessage> userMessagesList = new ArrayList<>();
    String encId = "";


    public ChatHistory(@NonNull Context context, String appointmentEncId) {
        super(context);
        this.mContext = context;
        this.encId = appointmentEncId;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_history);

        rvChatMessages = findViewById(R.id.rv_chatMessages);

        rvChatMessages = (RecyclerView) findViewById(R.id.rv_chatMessages);
        messageListAdapter = new MessageListAdapter(mContext, userMessagesList);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(mContext));
        rvChatMessages.getLayoutManager().scrollToPosition(userMessagesList.size() - 1);
        rvChatMessages.setAdapter(messageListAdapter);

        ApiInboxList();

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
                        Config.closeDialog(getOwnerActivity(), mDialog);
                    Config.logV("URL---------------" + response.raw().request().url().toString().trim());
                    Config.logV("Response--code-------------------------" + response.code());

                    if (response.code() == 200) {
                        mInboxList.clear();
                        mInboxList = response.body();
                        ArrayList<InboxList> inboxModels = new ArrayList<>();
                        if (mInboxList != null && mInboxList.size() > 0) {

                            for (int i = 0; i < mInboxList.size(); i++) {

                                if (mInboxList.get(i).getWaitlistId() != null) {
                                    if (mInboxList.get(i).getWaitlistId().equalsIgnoreCase(encId)) {

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
                                messageListAdapter = new MessageListAdapter(mContext, userMessagesList);
                                rvChatMessages.setLayoutManager(new LinearLayoutManager(mContext));
                                rvChatMessages.getLayoutManager().scrollToPosition(userMessagesList.size() - 1);
                                rvChatMessages.setAdapter(messageListAdapter);

                            } else {

                                // hide
                            }
                        } else {

                            rvChatMessages.setVisibility(View.GONE);

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
                    Config.closeDialog(getOwnerActivity(), mDialog);
            }
        });


    }

}
