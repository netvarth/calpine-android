package com.jaldeeinc.jaldee.adapter;

/**
 * Created by sharmila on 27/8/18.
 */

import android.content.Context;
import android.graphics.Bitmap;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.callback.DetailInboxAdapterCallback;
import com.jaldeeinc.jaldee.common.Config;
import com.jaldeeinc.jaldee.response.InboxModel;
import com.jaldeeinc.jaldee.utils.SharedPreference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sharmila on 14/8/18.
 */
public class DetailInboxAdapter extends RecyclerView.Adapter<DetailInboxAdapter.MyViewHolder> {

    private List<InboxModel> mInboxList;
    Context mContext;
    //boolean is_seemore=false;\
    Bitmap bitmap;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_provider, tv_date, tv_message, tv_reply, tv_noAttachments;
        LinearLayout linear_inbox_layout;
        ImageView img_sent;
        TextView tv_seemore;
        RecyclerView recyclerImage;
        LinearLayout imageScroll;


        public MyViewHolder(View view) {
            super(view);
            tv_provider = view.findViewById(R.id.txt_provider);
            tv_date = view.findViewById(R.id.txt_date);
            tv_message = view.findViewById(R.id.txt_message);
            linear_inbox_layout = view.findViewById(R.id.inbox_layout);
            img_sent = view.findViewById(R.id.img_sent);
            tv_reply = view.findViewById(R.id.txtreply);
            tv_seemore = view.findViewById(R.id.tv_seemore);
            recyclerImage = view.findViewById(R.id.recyclerImage);
            imageScroll = view.findViewById(R.id.imageScroll);
            tv_noAttachments = view.findViewById(R.id.txt_attachments);

        }
    }

    DetailInboxAdapterCallback callback;

    public DetailInboxAdapter(List<InboxModel> mInboxList, Context mContext, DetailInboxAdapterCallback callback, Bitmap bitmap) {
        this.mContext = mContext;
        this.mInboxList = mInboxList;
        Config.logV("Detail Inbox----------***************----" + mInboxList.size());
        this.callback = callback;
        // this.activity = mActivity;
        this.bitmap = bitmap;

    }

    @Override
    public DetailInboxAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detaill_inboxlist_row, parent, false);


        return new DetailInboxAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final DetailInboxAdapter.MyViewHolder myViewHolder, final int position) {
        final InboxModel inboxList = mInboxList.get(position);

        myViewHolder.tv_message.setText(inboxList.getMsg());
        if (inboxList.getAttachments() != null) {
            if (inboxList.getAttachments().size() == 1) {
                myViewHolder.tv_noAttachments.setText(inboxList.getAttachments().size() + " " + "Attachment");
                myViewHolder.tv_noAttachments.setVisibility(View.VISIBLE);
            } else if(inboxList.getAttachments().size()>1){
                myViewHolder.tv_noAttachments.setText(inboxList.getAttachments().size() + " " + "Attachments");
                myViewHolder.tv_noAttachments.setVisibility(View.VISIBLE);
            }

        }
        myViewHolder.tv_noAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext,3);
                myViewHolder.recyclerImage.setLayoutManager(mLayoutManager);
                if (inboxList.getAttachments() != null) {
                    myViewHolder.imageScroll.setVisibility(View.VISIBLE);
                    ImageAdapter imageAdapter = new ImageAdapter(inboxList.getAttachments(),mContext);
                    myViewHolder.recyclerImage.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                }
            }
        });


        //Config.logV("Detail Inbox-------&&&&&&&&&&&&-------"+inboxList.getMsg());
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(inboxList.getTimeStamp());
        myViewHolder.tv_date.setText(formatter.format(calendar.getTime()));

        int consumerId = SharedPreference.getInstance(mContext).getIntValue("consumerId", 0);


        if (consumerId == inboxList.getId()) {
            myViewHolder.img_sent.setImageResource(R.drawable.icon_sent);
        } else {
            myViewHolder.img_sent.setImageResource(R.drawable.icon_received);
        }

        if (consumerId == inboxList.getId()) {
            myViewHolder.tv_reply.setVisibility(View.INVISIBLE);

        } else {
            myViewHolder.tv_reply.setVisibility(View.VISIBLE);

        }


        if (!inboxList.isIs_see()) {
            myViewHolder.tv_message.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = myViewHolder.tv_message.getLineCount();

                    if (inboxList.getLineCount() == 0) {
                        inboxList.setLineCount(lineCount);

                    }
                    //Config.logV("No of line---------------" + lineCount + "Name" + inboxList.getUserName());
                    Config.logV("SeeMore " + inboxList.isIs_see() + "message" + inboxList.getMsg());
                    Config.logV("No of line---------------" + lineCount);
                    if (inboxList.getLineCount() > 3) {
                        myViewHolder.tv_message.setMaxLines(3);
                        myViewHolder.tv_message.setEllipsize(TextUtils.TruncateAt.END);
                        myViewHolder.tv_seemore.setText("See More");
                        myViewHolder.tv_seemore.setVisibility(View.VISIBLE);
                    } else {
                        myViewHolder.tv_seemore.setVisibility(View.INVISIBLE);
                    }
                    // Use lineCount here


                }
            });
        } else {
            myViewHolder.tv_message.post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = myViewHolder.tv_message.getLineCount();

                    if (inboxList.getLineCount() == 0) {
                        inboxList.setLineCount(lineCount);

                    }

                    //Config.logV("No of line---------------" + lineCount + "Name" + inboxList.getUserName());
                    Config.logV("SeeMore @@@" + inboxList.isIs_see() + "message@@@" + inboxList.getMsg());
                    Config.logV("No of line@@@@---------------" + lineCount);
                    if (inboxList.getLineCount() > 3) {
                        myViewHolder.tv_message.setMaxLines(Integer.MAX_VALUE);
                        myViewHolder.tv_message.setEllipsize(null);
                        // is_seemore=true;
                        myViewHolder.tv_seemore.setVisibility(View.VISIBLE);
                        myViewHolder.tv_seemore.setText("See Less");
                    } else {
                        myViewHolder.tv_seemore.setVisibility(View.INVISIBLE);
                    }
                    // Use lineCount here


                }
            });

        }


        myViewHolder.tv_seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.logV("Is seemore------------------" + inboxList.isIs_see());
                if (!inboxList.isIs_see()) {

                    myViewHolder.tv_message.setMaxLines(Integer.MAX_VALUE);
                    myViewHolder.tv_message.setEllipsize(null);
                    // is_seemore=true;
                    myViewHolder.tv_seemore.setText("See Less");
                    inboxList.setIs_see(true);

                    notifyDataSetChanged();


                } else {

                    myViewHolder.tv_message.setMaxLines(3);
                    myViewHolder.tv_message.setEllipsize(TextUtils.TruncateAt.END);
                    myViewHolder.tv_seemore.setText("See More");
                    inboxList.setIs_see(false);
                    notifyDataSetChanged();

                    // is_seemore=false;

                }
            }
        });
        //   ResizableCustomView.doResizeTextView(myViewHolder.tv_message, 3, "See More", true);


        // myViewHolder.tv_provider.setText(inboxList.getUserName());


        myViewHolder.tv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onMethodCallback(inboxList.getWaitlistId(), String.valueOf(inboxList.getUniqueID()), inboxList.getTimeStamp());


            }
        });


    }


    @Override
    public int getItemCount() {
        return mInboxList.size();
    }
}




