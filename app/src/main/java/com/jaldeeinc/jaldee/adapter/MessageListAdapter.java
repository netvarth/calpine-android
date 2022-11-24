package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewRegularItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.MediaTypeAndExtention;
import com.jaldeeinc.jaldee.model.UserMessage;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
        CustomTextViewMedium tvMessageType;
        CustomTextViewSemiBold attachText;
        LinearLayout ll_attachments;
        ImageView iv_attach1, iv_attach2;
        RecyclerView recyclerImageSent;
        ArrayList<String> mGalleryAttachments = new ArrayList<>();

        SentMessageHolder(View itemView) {
            super(itemView);

            mText = itemView.findViewById(R.id.tv_sentMessage);
            timeText = itemView.findViewById(R.id.tv_sentTime);
            attachText = itemView.findViewById(R.id.tv_attach_sent);
            ll_attachments = itemView.findViewById(R.id.attach_sent);
            iv_attach1 = itemView.findViewById(R.id.img_sent1);
            iv_attach2 = itemView.findViewById(R.id.img_sent2);
            tvMessageType = itemView.findViewById(R.id.tv_messageType);

        }

        void bind(UserMessage message) {
            if (message.getMessage() != null && !message.getMessage().trim().equalsIgnoreCase("")) {

                mText.setVisibility(View.VISIBLE);
                mText.setText(message.getMessage());
                Linkify.addLinks(mText, Linkify.WEB_URLS);
            } else {
                mText.setVisibility(View.GONE);
            }
            // Format the stored timestamp into a readable String using method.
            timeText.setText(formatTimeStamp(message.getTimeStamp()));

            if (message.getMessageType() != null) {
                tvMessageType.setVisibility(View.VISIBLE);
                String upperString = message.getMessageType().substring(0, 1).toUpperCase() + message.getMessageType().substring(1).toLowerCase();
                tvMessageType.setText(upperString);
            } else {
                tvMessageType.setVisibility(View.GONE);
            }

            if (message.getAttachments() != null && message.getAttachments().size() > 0) {
                mGalleryAttachments.clear();
                for (int i = 0; i < message.getAttachments().size(); i++) {
                    mGalleryAttachments.add(message.getAttachments().get(i).getS3path());
                }
                ll_attachments.setVisibility(View.VISIBLE);

                if (message.getAttachments().size() == 1) {
                    MediaTypeAndExtention type = Config.getFileType(message.getAttachments().get(0).getS3path());

                    iv_attach1.setVisibility(View.VISIBLE);
                    attachText.setVisibility(View.GONE);
                    iv_attach2.setVisibility(View.GONE);
                    if (type.getMediaType().equals(Constants.docType)) {
                        if (type.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                            Glide.with(mContext).load(R.drawable.pdf).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                        } else {
                            Glide.with(mContext).load(R.drawable.icon_document).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                        }
                    } else if (type.getMediaType().equals(Constants.audioType)) {
                        Glide.with(mContext).load(R.drawable.audio_icon).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                    } else if (type.getMediaType().equals(Constants.videoType)) {
                        Glide.with(mContext).load(R.drawable.video_icon).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                    } else if (type.getMediaType().equals(Constants.txtType)) {
                        Glide.with(mContext).load(R.drawable.icon_text).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                    } else {
                        Glide.with(mContext).load(message.getAttachments().get(0).getS3path()).placeholder(R.drawable.icon_noimage).override(100, 200).into(iv_attach1);
                    }
                } else if (message.getAttachments().size() >= 2) {
                    MediaTypeAndExtention type1 = Config.getFileType(message.getAttachments().get(0).getS3path());
                    MediaTypeAndExtention type2 = Config.getFileType(message.getAttachments().get(1).getS3path());

                    if (type1.getMediaType().equals(Constants.docType)) {
                        if (type1.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                            Glide.with(mContext).load(R.drawable.pdf).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                        } else {
                            Glide.with(mContext).load(R.drawable.icon_document).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                        }
                    } else if (type1.getMediaType().equals(Constants.audioType)) {
                        Glide.with(mContext).load(R.drawable.audio_icon).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                    } else if (type1.getMediaType().equals(Constants.videoType)) {
                        Glide.with(mContext).load(R.drawable.video_icon).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                    } else if (type1.getMediaType().equals(Constants.txtType)) {
                        Glide.with(mContext).load(R.drawable.icon_text).placeholder(R.drawable.icon_noimage).into(iv_attach1);
                    } else {
                        Glide.with(mContext).load(message.getAttachments().get(0).getS3path()).placeholder(R.drawable.icon_noimage).override(100, 200).into(iv_attach1);
                    }

                    if (type2.getMediaType().equals(Constants.docType)) {
                        if (type2.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                            Glide.with(mContext).load(R.drawable.pdf).placeholder(R.drawable.icon_noimage).into(iv_attach2);
                        } else {
                            Glide.with(mContext).load(R.drawable.icon_document).placeholder(R.drawable.icon_noimage).into(iv_attach2);
                        }
                    } else if (type2.getMediaType().equals(Constants.audioType)) {
                        Glide.with(mContext).load(R.drawable.audio_icon).placeholder(R.drawable.icon_noimage).into(iv_attach2);
                    } else if (type2.getMediaType().equals(Constants.videoType)) {
                        Glide.with(mContext).load(R.drawable.video_icon).placeholder(R.drawable.icon_noimage).into(iv_attach2);
                    } else if (type2.getMediaType().equals(Constants.txtType)) {
                        Glide.with(mContext).load(R.drawable.icon_text).placeholder(R.drawable.icon_noimage).into(iv_attach2);
                    } else {
                        Glide.with(mContext).load(message.getAttachments().get(1).getS3path()).placeholder(R.drawable.icon_noimage).override(100, 200).into(iv_attach2);
                    }
                    if (message.getAttachments().size() == 2) {
                        attachText.setVisibility(View.GONE);
                    } else if (message.getAttachments().size() > 2) {
                        int size = message.getAttachments().size() - 2;
                        attachText.setText("+ " + size + " more");
                        attachText.setVisibility(View.VISIBLE);
                    }
                    iv_attach1.setVisibility(View.VISIBLE);
                    iv_attach2.setVisibility(View.VISIBLE);
                }
            } else {
                ll_attachments.setVisibility(View.GONE);
            }

            iv_attach1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryAttachments, mContext);
                    if (mValue) {

                        Intent intent = new Intent(iv_attach1.getContext(), SwipeGalleryImage.class);
                        intent.putExtra("pos", 0);
                        intent.putExtra("from", Constants.CHAT);
                        iv_attach1.getContext()
                                .startActivity(intent);
                    }
                }
            });

            iv_attach2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryAttachments, mContext);
                    if (mValue) {
                        Intent intent = new Intent(iv_attach1.getContext(), SwipeGalleryImage.class);
                        intent.putExtra("pos", 0);
                        intent.putExtra("from", Constants.CHAT);
                        iv_attach1.getContext()
                                .startActivity(intent);
                    }
                }
            });

            attachText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryAttachments, mContext);
                    if (mValue) {

                        Intent intent = new Intent(iv_attach1.getContext(), SwipeGalleryImage.class);
                        intent.putExtra("pos", 0);
                        intent.putExtra("from", Constants.CHAT);
                        iv_attach1.getContext()
                                .startActivity(intent);
                    }

                }
            });

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        CustomTextViewMedium messageText;
        CustomTextViewRegularItalic timeText;
        CustomTextViewMedium tvMessageType;
        CustomTextViewSemiBold attachmentsText;
        LinearLayout ll_attachReceiv;
        ImageView iv_img1, iv_img2;
        RecyclerView recyclerImage;
        ArrayList<String> mGalleryAttachments = new ArrayList<>();

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.tv_receivedMessage);
            timeText = itemView.findViewById(R.id.tv_receivedTime);
            attachmentsText = itemView.findViewById(R.id.tv_attach_receiv);
            ll_attachReceiv = itemView.findViewById(R.id.attach_receiv);
            iv_img1 = itemView.findViewById(R.id.img_receiv1);
            iv_img2 = itemView.findViewById(R.id.img_receiv2);
            tvMessageType = itemView.findViewById(R.id.tv_messageType);

        }

        void bind(UserMessage message) {
            if (message.getMessage() != null && !message.getMessage().trim().equalsIgnoreCase("")) {

                messageText.setVisibility(View.VISIBLE);
                messageText.setText(message.getMessage());
                Linkify.addLinks(messageText, Linkify.WEB_URLS);

            } else {
                messageText.setVisibility(View.GONE);

            }
            // Format the stored timestamp into a readable String using method.
            timeText.setText(formatTimeStamp(message.getTimeStamp()));

            if (message.getMessageType() != null) {
                tvMessageType.setVisibility(View.VISIBLE);
                String upperString = message.getMessageType().substring(0, 1).toUpperCase() + message.getMessageType().substring(1).toLowerCase();
                tvMessageType.setText(upperString);
            } else {
                tvMessageType.setVisibility(View.GONE);
            }
            if (message.getAttachments() != null && message.getAttachments().size() > 0) {
                mGalleryAttachments.clear();
                ll_attachReceiv.setVisibility(View.VISIBLE);
                for (int i = 0; i < message.getAttachments().size(); i++) {
                    mGalleryAttachments.add(message.getAttachments().get(i).getS3path());
                }
                if (message.getAttachments().size() == 1) {

                    iv_img1.setVisibility(View.VISIBLE);
                    attachmentsText.setVisibility(View.GONE);
                    iv_img2.setVisibility(View.GONE);

                    MediaTypeAndExtention type = Config.getFileType(message.getAttachments().get(0).getS3path());

                    if (type.getMediaType().equals(Constants.docType)) {
                        if (type.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                            Glide.with(mContext).load(R.drawable.pdf).placeholder(R.drawable.icon_noimage).into(iv_img1);
                        } else {
                            Glide.with(mContext).load(R.drawable.icon_document).placeholder(R.drawable.icon_noimage).into(iv_img1);
                        }
                    } else if (type.getMediaType().equals(Constants.audioType)) {
                        Glide.with(mContext).load(R.drawable.audio_icon).placeholder(R.drawable.icon_noimage).into(iv_img1);
                    } else if (type.getMediaType().equals(Constants.videoType)) {
                        Glide.with(mContext).load(R.drawable.video_icon).placeholder(R.drawable.icon_noimage).into(iv_img1);
                    } else if (type.getMediaType().equals(Constants.txtType)) {
                        Glide.with(mContext).load(R.drawable.icon_text).placeholder(R.drawable.icon_noimage).into(iv_img1);
                    } else {
                        Glide.with(mContext).load(message.getAttachments().get(0).getS3path()).placeholder(R.drawable.icon_noimage).override(100, 200).into(iv_img1);
                    }

                } else if (message.getAttachments().size() >= 2) {
                    MediaTypeAndExtention type1 = Config.getFileType(message.getAttachments().get(0).getS3path());
                    MediaTypeAndExtention type2 = Config.getFileType(message.getAttachments().get(1).getS3path());

                    if (type1.getMediaType().equals(Constants.docType)) {
                        if (type1.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                            Glide.with(mContext).load(R.drawable.pdf).placeholder(R.drawable.icon_noimage).into(iv_img1);
                        } else {
                            Glide.with(mContext).load(R.drawable.icon_document).placeholder(R.drawable.icon_noimage).into(iv_img1);
                        }
                    } else if (type1.getMediaType().equals(Constants.audioType)) {
                        Glide.with(mContext).load(R.drawable.audio_icon).placeholder(R.drawable.icon_noimage).into(iv_img1);
                    } else if (type1.getMediaType().equals(Constants.videoType)) {
                        Glide.with(mContext).load(R.drawable.video_icon).placeholder(R.drawable.icon_noimage).into(iv_img1);
                    } else if (type1.getMediaType().equals(Constants.txtType)) {
                        Glide.with(mContext).load(R.drawable.icon_text).placeholder(R.drawable.icon_noimage).into(iv_img1);
                    } else {
                        Glide.with(mContext).load(message.getAttachments().get(0).getS3path()).placeholder(R.drawable.icon_noimage).override(100, 200).into(iv_img1);
                    }
                    if (type2.getMediaType().equals(Constants.docType)) {
                        if (type2.getMediaTypeWithExtention().equals(Constants.pdfType)) {
                            Glide.with(mContext).load(R.drawable.pdf).placeholder(R.drawable.icon_noimage).into(iv_img2);
                        } else {
                            Glide.with(mContext).load(R.drawable.icon_document).placeholder(R.drawable.icon_noimage).into(iv_img2);
                        }
                    } else if (type2.getMediaType().equals(Constants.audioType)) {
                        Glide.with(mContext).load(R.drawable.audio_icon).placeholder(R.drawable.icon_noimage).into(iv_img2);
                    } else if (type2.getMediaType().equals(Constants.videoType)) {
                        Glide.with(mContext).load(R.drawable.video_icon).placeholder(R.drawable.icon_noimage).into(iv_img2);
                    } else if (type2.getMediaType().equals(Constants.txtType)) {
                        Glide.with(mContext).load(R.drawable.icon_text).placeholder(R.drawable.icon_noimage).into(iv_img2);
                    } else {
                        Glide.with(mContext).load(message.getAttachments().get(1).getS3path()).placeholder(R.drawable.icon_noimage).override(100, 200).into(iv_img2);
                    }
                    if (message.getAttachments().size() == 2) {
                        attachmentsText.setVisibility(View.GONE);
                    } else if (message.getAttachments().size() > 2) {
                        int size = message.getAttachments().size() - 2;
                        attachmentsText.setText("+ " + size + " more");
                        attachmentsText.setVisibility(View.VISIBLE);
                    }
                    iv_img1.setVisibility(View.VISIBLE);
                    iv_img2.setVisibility(View.VISIBLE);
                }
            } else {
                ll_attachReceiv.setVisibility(View.GONE);
            }

            iv_img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryAttachments, mContext);
                    if (mValue) {

                        Intent intent = new Intent(iv_img1.getContext(), SwipeGalleryImage.class);
                        intent.putExtra("pos", 0);
                        intent.putExtra("from", Constants.CHAT);
                        iv_img1.getContext()
                                .startActivity(intent);
                    }
                }
            });

            iv_img2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryAttachments, mContext);
                    if (mValue) {

                        Intent intent = new Intent(iv_img1.getContext(), SwipeGalleryImage.class);
                        intent.putExtra("pos", 0);
                        intent.putExtra("from", Constants.CHAT);
                        iv_img1.getContext()
                                .startActivity(intent);
                    }
                }
            });

            attachmentsText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean mValue = SwipeGalleryImage.SetGalleryList(mGalleryAttachments, mContext);
                    if (mValue) {

                        Intent intent = new Intent(iv_img1.getContext(), SwipeGalleryImage.class);
                        intent.putExtra("pos", 0);
                        intent.putExtra("from", Constants.CHAT);
                        iv_img1.getContext()
                                .startActivity(intent);
                    }

                }
            });

        }
    }

    public static String formatTimeStamp(String tStamp) {

        long time = Long.parseLong(tStamp);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a");
        String timeStamp = sdf.format(time);

        return timeStamp;
    }
}