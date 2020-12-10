package com.jaldeeinc.jaldee.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.SwipeGalleryImage;
import com.jaldeeinc.jaldee.custom.CustomTextViewMedium;
import com.jaldeeinc.jaldee.custom.CustomTextViewRegularItalic;
import com.jaldeeinc.jaldee.custom.CustomTextViewSemiBold;
import com.jaldeeinc.jaldee.model.UserMessage;
import com.jaldeeinc.jaldee.utils.SharedPreference;
import com.squareup.picasso.Picasso;

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
        CustomTextViewSemiBold attachText;
        LinearLayout ll_attachments;
        ImageView iv_attach1,iv_attach2;
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

        }

        void bind(UserMessage message) {
            if (message.getMessage() != null) {
                mText.setText(Html.fromHtml(message.getMessage()));
                mText.setMovementMethod(LinkMovementMethod.getInstance());
            }
            // Format the stored timestamp into a readable String using method.
            timeText.setText(formatTimeStamp(message.getTimeStamp()));

            if(message.getAttachments()!=null && message.getAttachments().size()>0){
                mGalleryAttachments.clear();
                for(int i=0;i<message.getAttachments().size();i++) {
                    mGalleryAttachments.add(message.getAttachments().get(i).getS3path());
                }
                ll_attachments.setVisibility(View.VISIBLE);
                if(message.getAttachments().size()==1) {
                    Picasso.Builder builder = new Picasso.Builder(iv_attach1.getContext());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    iv_attach1.setVisibility(View.VISIBLE);
                    attachText.setVisibility(View.GONE);
                    iv_attach2.setVisibility(View.GONE);
                    builder.build().load(message.getAttachments().get(0).getS3path()).fit().into(iv_attach1);
                }
                else if(message.getAttachments().size()==2){
                    Picasso.Builder builder = new Picasso.Builder(iv_attach1.getContext());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    builder.build().load(message.getAttachments().get(0).getS3path()).fit().into(iv_attach1);
                    Picasso.Builder builder1 = new Picasso.Builder(iv_attach2.getContext());
                    builder1.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    builder1.build().load(message.getAttachments().get(1).getS3path()).fit().into(iv_attach2);
                    iv_attach1.setVisibility(View.VISIBLE);
                    iv_attach2.setVisibility(View.VISIBLE);
                    attachText.setVisibility(View.GONE);
                }
                else if(message.getAttachments().size()>2){
                    Picasso.Builder builder = new Picasso.Builder(iv_attach1.getContext());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    builder.build().load(message.getAttachments().get(0).getS3path()).fit().into(iv_attach1);
                    Picasso.Builder builder1 = new Picasso.Builder(iv_attach2.getContext());
                    builder1.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    builder1.build().load(message.getAttachments().get(1).getS3path()).fit().into(iv_attach2);
                    int size = message.getAttachments().size() - 2;
                    attachText.setText( "+ " + size + " more");
                    attachText.setVisibility(View.VISIBLE);
                    iv_attach1.setVisibility(View.VISIBLE);
                    iv_attach2.setVisibility(View.VISIBLE);
                }
            }
            else{
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
//                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext,3);
//                    recyclerImageSent.setLayoutManager(mLayoutManager);
//                    if (message.getAttachments() != null) {
//                        ImageAdapter imageAdapter = new ImageAdapter(message.getAttachments(),mContext);
//                        recyclerImageSent.setAdapter(imageAdapter);
//                        imageAdapter.notifyDataSetChanged();
//                    }
                }
            });

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        CustomTextViewMedium messageText;
        CustomTextViewRegularItalic timeText;
        CustomTextViewSemiBold attachmentsText;
        LinearLayout ll_attachReceiv;
        ImageView iv_img1,iv_img2;
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

        }

        void bind(UserMessage message) {
            if (message.getMessage() != null) {
                messageText.setText(Html.fromHtml(message.getMessage()));
                messageText.setMovementMethod(LinkMovementMethod.getInstance());
            }
            // Format the stored timestamp into a readable String using method.
            timeText.setText(formatTimeStamp(message.getTimeStamp()));
            if(message.getAttachments()!=null && message.getAttachments().size()>0){
                mGalleryAttachments.clear();
                ll_attachReceiv.setVisibility(View.VISIBLE);
                for(int i=0;i<message.getAttachments().size();i++) {
                    mGalleryAttachments.add(message.getAttachments().get(i).getS3path());
                }
                if(message.getAttachments().size()==1) {
                    Picasso.Builder builder = new Picasso.Builder(iv_img1.getContext());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    iv_img1.setVisibility(View.VISIBLE);
                    attachmentsText.setVisibility(View.GONE);
                    iv_img2.setVisibility(View.GONE);
                    builder.build().load(message.getAttachments().get(0).getS3path()).fit().into(iv_img1);
                }
                else if(message.getAttachments().size()==2){
                    Picasso.Builder builder = new Picasso.Builder(iv_img1.getContext());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    builder.build().load(message.getAttachments().get(0).getS3path()).fit().into(iv_img1);
                    Picasso.Builder builder1 = new Picasso.Builder(iv_img2.getContext());
                    builder1.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    builder1.build().load(message.getAttachments().get(1).getS3path()).fit().into(iv_img2);
                    iv_img1.setVisibility(View.VISIBLE);
                    iv_img2.setVisibility(View.VISIBLE);
                    attachmentsText.setVisibility(View.GONE);
                }
                else if(message.getAttachments().size()>2){
                    Picasso.Builder builder = new Picasso.Builder(iv_img1.getContext());
                    builder.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    builder.build().load(message.getAttachments().get(0).getS3path()).fit().into(iv_img1);
                    Picasso.Builder builder1 = new Picasso.Builder(iv_img2.getContext());
                    builder1.listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                    builder1.build().load(message.getAttachments().get(1).getS3path()).fit().into(iv_img2);
                    int size = message.getAttachments().size() - 2;
                    attachmentsText.setText( "+ " + size + " more");
                    attachmentsText.setVisibility(View.VISIBLE);
                    iv_img1.setVisibility(View.VISIBLE);
                    iv_img2.setVisibility(View.VISIBLE);
                }
            }
            else{
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

//                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext,3);
//                    recyclerImage.setLayoutManager(mLayoutManager);
//                    if (message.getAttachments() != null) {
//                        ImageAdapter imageAdapter = new ImageAdapter(message.getAttachments(),mContext);
//                        recyclerImage.setAdapter(imageAdapter);
//                        imageAdapter.notifyDataSetChanged();
//                    }
                }
            });

        }
    }

    public static String formatTimeStamp(String tStamp){

        long time = Long.parseLong(tStamp);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' h:mm a");
        String timeStamp = sdf.format(time);

        return timeStamp;
    }
}