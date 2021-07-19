package com.jaldeeinc.jaldee.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaldeeinc.jaldee.R;
import com.jaldeeinc.jaldee.activities.ChatActivity;
import com.jaldeeinc.jaldee.activities.Constants;
import com.jaldeeinc.jaldee.activities.DetailInboxList;
import com.jaldeeinc.jaldee.database.DatabaseHandler;
import com.jaldeeinc.jaldee.response.InboxModel;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sharmila on 14/8/18.
 */
public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {


    private List<InboxModel> mInboxList;
    Context mContext;
    Activity activity;
    private int lastPosition = -1;
    ArrayList<InboxModel> mDetailInboxList = new ArrayList<>();
    private boolean isLoading;

    public InboxAdapter(List<InboxModel> mInboxList, Context mContext, Activity mActivity, boolean isLoading) {
        this.mContext = mContext;
        this.mInboxList = mInboxList;
        this.activity = mActivity;
        this.isLoading = isLoading;
    }

    @Override
    public InboxAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {


        if (isLoading) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shimmer_inbox, viewGroup, false);
            return new InboxAdapter.MyViewHolder(v, true);

        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inboxlist_row, viewGroup, false);
            return new InboxAdapter.MyViewHolder(v, false);
        }
    }


    @Override
    public void onBindViewHolder(final InboxAdapter.MyViewHolder myViewHolder, final int position) {

        if (!isLoading) {
            final InboxModel inboxList = mInboxList.get(position);

            setAnimation(myViewHolder.cvCard, position);


            if (inboxList.getMsg() != null) {
                myViewHolder.tv_message.setText(Html.fromHtml(inboxList.getMsg()));
            }
//        Log.i("kingiii",new Gson().toJson(inboxList.getAttachments()));
            myViewHolder.tv_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    if (inboxList.getWaitlistId() != null) {
                        intent.putExtra("uuid", inboxList.getWaitlistId());
                    }
                    if (inboxList.getUniqueID() != null) {
                        intent.putExtra("accountId", Integer.parseInt(inboxList.getUniqueID()));
                    }
                    intent.putExtra("name", inboxList.getAccountName());
//                    if (inboxList.getWaitlistId() != null && inboxList.getWaitlistId().contains("_wl")) {
//                        intent.putExtra("from", Constants.CHECKIN);
//                    } else if (inboxList.getWaitlistId() != null && inboxList.getWaitlistId().contains("_appt")) {
//                        intent.putExtra("from", Constants.APPOINTMENT);
//                    } else if (inboxList.getWaitlistId() != null && inboxList.getWaitlistId().contains("_odr")) {
//                        intent.putExtra("from", Constants.ORDERS);
//                    } else if (inboxList.getWaitlistId() != null && inboxList.getWaitlistId().contains("_dtn")) {
//                        intent.putExtra("from", Constants.DONATION);
//                    } else {
//                        intent.putExtra("from", Constants.PROVIDER);
//                    }
                    intent.putExtra("from", Constants.INBOX);
                    view.getContext().startActivity(intent);

                }
            });


            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(inboxList.getTimeStamp());
            myViewHolder.tv_date.setText(formatter.format(calendar.getTime()));
            

            // String cap_Provider = inboxList.getUserName().substring(0, 1).toUpperCase() + inboxList.getUserName().substring(1);
            myViewHolder.tv_provider.setText(inboxList.getAccountName());
            myViewHolder.linear_inbox_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    DatabaseHandler db = new DatabaseHandler(mContext);
                    mDetailInboxList = db.getInboxDetail(inboxList.getUniqueID());
                    if (DetailInboxList.setInboxList(mDetailInboxList)) {
                        Intent intent = new Intent(v.getContext(), ChatActivity.class);
                        if (inboxList.getWaitlistId() != null) {
                            intent.putExtra("uuid", inboxList.getWaitlistId());
                        }
                        if (inboxList.getUniqueID() != null) {
                            intent.putExtra("accountId", Integer.parseInt(inboxList.getUniqueID()));
                        }
                        intent.putExtra("name", inboxList.getAccountName());
//                        if (inboxList.getWaitlistId() != null && inboxList.getWaitlistId().contains("_wl")) {
//                            intent.putExtra("from", Constants.CHECKIN);
//                        } else if (inboxList.getWaitlistId() != null && inboxList.getWaitlistId().contains("_appt")) {
//                            intent.putExtra("from", Constants.APPOINTMENT);
//                        } else if (inboxList.getWaitlistId() != null && inboxList.getWaitlistId().contains("_odr")) {
//                            intent.putExtra("from", Constants.ORDERS);
//                        } else if (inboxList.getWaitlistId() != null && inboxList.getWaitlistId().contains("_dtn")) {
//                            intent.putExtra("from", Constants.DONATION);
//                        } else {
//                            intent.putExtra("from", Constants.PROVIDER);
//                        }
                        intent.putExtra("from", Constants.INBOX);
                        mContext.startActivity(intent);
                    }
                }

            });

        } else {
            InboxAdapter.MyViewHolder skeletonViewHolder = (InboxAdapter.MyViewHolder) myViewHolder;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            skeletonViewHolder.itemView.setLayoutParams(params);
        }


    }


    @Override
    public int getItemCount() {
        return isLoading ? 15 : mInboxList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_provider, tv_date, tv_message;
        LinearLayout linear_inbox_layout;
        private CardView cvCard;

        public MyViewHolder(View view, boolean isLoading) {
            super(view);
            tv_provider = (TextView) view.findViewById(R.id.txt_provider);
            tv_date = (TextView) view.findViewById(R.id.txt_date);
            tv_message = (TextView) view.findViewById(R.id.txt_message);
            linear_inbox_layout = (LinearLayout) view.findViewById(R.id.inbox_layout);
            cvCard = view.findViewById(R.id.card);


        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }


}