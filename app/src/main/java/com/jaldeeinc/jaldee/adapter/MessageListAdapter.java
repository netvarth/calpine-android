package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewRegularItalic;
import com.jaldeeinc.jaldee.model.UserMessage;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private ArrayList<UserMessage> mMessageList;

    public MessageListAdapter(Context context, ArrayList<UserMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        UserMessage message = (UserMessage) mMessageList.get(position);
        int activeConsumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);

        if (message.getId() == activeConsumerId) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_messages, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_messages, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        UserMessage message = (UserMessage) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        CustomTextViewMedium mText;
        CustomTextViewRegularItalic timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            mText = itemView.findViewById(R.id.tv_sentMessage);
            timeText = itemView.findViewById(R.id.tv_sentTime);
        }

        void bind(UserMessage message) {
            if (message.getMessage() != null) {
                mText.setText(message.getMessage());
            }
            // Format the stored timestamp into a readable String using method.
            timeText.setText(formatTimeStamp(message.getTimeStamp()));

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        CustomTextViewMedium messageText;
        CustomTextViewRegularItalic timeText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.tv_receivedMessage);
            timeText = itemView.findViewById(R.id.tv_receivedTime);

        }

        void bind(UserMessage message) {
            if (message.getMessage() != null) {
                messageText.setText(message.getMessage());
            }
            // Format the stored timestamp into a readable String using method.
            timeText.setText(formatTimeStamp(message.getTimeStamp()));

        }
    }

    public static String formatTimeStamp(String tStamp){

        long time = Long.parseLong(tStamp);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a");
        String timeStamp = sdf.format(time);

        return timeStamp;
    }
}