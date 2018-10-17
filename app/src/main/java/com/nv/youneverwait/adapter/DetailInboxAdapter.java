package com.nv.youneverwait.adapter;

/**
 * Created by sharmila on 27/8/18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nv.youneverwait.R;
import com.nv.youneverwait.callback.DetailInboxAdapterCallback;
import com.nv.youneverwait.common.Config;
import com.nv.youneverwait.response.InboxModel;
import com.nv.youneverwait.utils.SharedPreference;

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
    //boolean is_seemore=false;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_provider, tv_date, tv_message, tv_reply;
        LinearLayout linear_inbox_layout;
        ImageView img_sent;
        TextView tv_seemore;

        public MyViewHolder(View view) {
            super(view);
            tv_provider = (TextView) view.findViewById(R.id.txt_provider);
            tv_date = (TextView) view.findViewById(R.id.txt_date);
            tv_message = (TextView) view.findViewById(R.id.txt_message);
            linear_inbox_layout = (LinearLayout) view.findViewById(R.id.inbox_layout);
            img_sent = (ImageView) view.findViewById(R.id.img_sent);
            tv_reply = (TextView) view.findViewById(R.id.txtreply);
            tv_seemore = (TextView) view.findViewById(R.id.tv_seemore);


        }
    }

    DetailInboxAdapterCallback callback;

    public DetailInboxAdapter(List<InboxModel> mInboxList, Context mContext, DetailInboxAdapterCallback callback) {
        this.mContext = mContext;
        this.mInboxList = mInboxList;
        Config.logV("Detail Inbox----------***************----" + mInboxList.size());
        this.callback = callback;
        // this.activity = mActivity;

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


        myViewHolder.tv_message.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = myViewHolder.tv_message.getLineCount();
                //Config.logV("No of line---------------" + lineCount + "Name" + inboxList.getUserName());

                if (lineCount > 3) {
                    myViewHolder.tv_message.setMaxLines(3);
                    myViewHolder.tv_message.setEllipsize(TextUtils.TruncateAt.END);
                    myViewHolder.tv_seemore.setVisibility(View.VISIBLE);
                } else {
                    myViewHolder.tv_seemore.setVisibility(View.INVISIBLE);
                }
                // Use lineCount here


            }
        });

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
                    // notifyDataSetChanged();
                } else {
                    myViewHolder.tv_message.setMaxLines(3);
                    myViewHolder.tv_message.setEllipsize(TextUtils.TruncateAt.END);
                    myViewHolder.tv_seemore.setText("See More");
                    inboxList.setIs_see(false);
                    // notifyDataSetChanged();
                    // is_seemore=false;

                }
            }
        });
        //   ResizableCustomView.doResizeTextView(myViewHolder.tv_message, 3, "See More", true);


        // myViewHolder.tv_provider.setText(inboxList.getUserName());


        myViewHolder.tv_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onMethodCallback(inboxList.getWaitlistId(),inboxList.getId());

            }
        });


    }


    @Override
    public int getItemCount() {
        return mInboxList.size();
    }
}